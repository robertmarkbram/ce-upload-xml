package com.rob.ceuploadxml.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.rob.ceuploadxml.model.XmlDocMetadata;
import com.rob.ceuploadxml.model.repo.XmlDocMetadataRepository;
import com.rob.ceuploadxml.validator.XmlValidator;

import lombok.extern.log4j.Log4j2;

/**
 * Store and retrieve XML files.
 */
@Service
@Log4j2
public final class FileSystemXmlStorageService implements XmlStorageService {

	/**
	 * Where to store XML files.
	 */
	private final Path rootLocation;

	/**
	 * XML Doc repository.
	 */
	private final XmlDocMetadataRepository xmlDocRepo;

	/**
	 * XML Validator.
	 */
	private final XmlValidator xmlValidator = new XmlValidator();

	@Autowired
	public FileSystemXmlStorageService(final StorageProperties properties,
			final XmlDocMetadataRepository xmlDocRepository) {
		this.rootLocation = Paths.get(properties.getLocation());
		this.xmlDocRepo = xmlDocRepository;
	}

	@Override
	public XmlDocMetadata store(final MultipartFile file, final String note) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		log.debug("Trying to save file {}.", filename);
		
		Path xmlPath = null;
		checkForPathErrors(file, filename);
		failIfFileExists(filename);
		xmlPath = saveFile(file, filename);
		validateXml(xmlPath, filename);
		
		// Save metadata and return it.
		return xmlDocRepo.save(//
				XmlDocMetadata.builder()//
						.filename(file.getOriginalFilename())//
						.size(file.getSize())//
						.note(note)//
						.build());

	}
	
	/**
	 * Check if file itself looks legit.
	 * 
	 * @param file     multipart file from request
	 * @param filename file name
	 */
	private void checkForPathErrors(final MultipartFile file, final String filename) {
		if (!filename.endsWith(".xml")) {
			throw new StorageException("Can only accept files with .xml extension: " + filename);
		}
		if (file.isEmpty()) {
			throw new StorageException("Cannot save empty file: " + filename);
		}
		// Prevent directory traversal attacks.
		if (filename.contains("..") || filename.contains("/")) {
			throw new StorageException("Potential directory traversal attack with file name: " + filename);
		}

	}

	/**
	 * If file exists, fail. In this version, we do not update files - need to provide separate action for that.
	 * 
	 * @param filename file name
	 */
	private void failIfFileExists(final String filename) {
		List<XmlDocMetadata> found = xmlDocRepo.findByFilename(filename);
		if (!found.isEmpty()) {
			throw new StorageException("File already exists: " + filename);
		}
	}

	/**
	 * Save file to directory for new files.
	 * 
	 * @param file     multipart file from request
	 * @param filename file name
	 * @return xml path
	 */
	private Path saveFile(final MultipartFile file, final String filename) {
		Path xmlPath = null;
		try (InputStream inputStream = file.getInputStream()) {
			xmlPath = this.rootLocation.resolve(filename);
			Files.copy(inputStream, xmlPath, StandardCopyOption.REPLACE_EXISTING);
			File file2 = xmlPath.toFile();
			log.info("Saved file to [{}] which exists: {}", file2.getAbsolutePath(), file2.exists());
		} catch (IOException e) {
			log.error("Failed to store file [{}]", filename, e);
			throw new StorageException("Failed to store file: " + filename, e);
		}
		return xmlPath;
	}

	/**
	 * Validate XML file.
	 * 
	 * @param xmlPath  path to file
	 * @param filename file name
	 */
	private void validateXml(final Path xmlPath, final String filename) {
		log.debug("Validating file {}.", xmlPath);
		if (!xmlValidator.xmlIsWellFormed(xmlPath)) {
			try {
				Files.delete(xmlPath);
			} catch (IOException ioe) {
				log.error("WARNING: unable to delete file {}", xmlPath.toAbsolutePath(), ioe);
			}
			throw new StorageException("File contains invalid XML: " + filename);
		}

	}

	@Override
	public Path load(final String filename) {
		log.debug("Load file {}.", filename);
		return rootLocation.resolve(filename);
	}

	@Override
	public List<XmlDocMetadata> listFiles() {
		return xmlDocRepo.findAll();
	}

	@Override
	public Resource loadAsResource(final String filename) {
		log.debug("Load file as resource {}.", filename);
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);

			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void init() {
		log.debug("Initialise storage service at rootLocation {}.", rootLocation);
		try {
			Path directory = Files.createDirectories(rootLocation);
			log.info("Location for XML files: " + directory.toAbsolutePath());
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage at: " + rootLocation, e);
		}
	}
}
