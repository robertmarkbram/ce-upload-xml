package com.rob.ceuploadxml.service;

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
	private XmlDocMetadataRepository xmlDocRepo;

	@Autowired
	public FileSystemXmlStorageService(final StorageProperties properties,
			final XmlDocMetadataRepository xmlDocRepository) {
		this.rootLocation = Paths.get(properties.getLocation());
		this.xmlDocRepo = xmlDocRepository;
	}

	@Override
	public void store(final MultipartFile file, final String note) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		log.debug("Trying to save file {}.", filename);
		try {
			if (file.isEmpty()) {
				throw new StorageException("Cannot save empty file: " + filename);
			}
			// Prevent directory traversal attacks.
			if (filename.contains("..") || filename.contains("/")) {
				throw new StorageException("Potential directory traversal attach with file name: " + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, this.rootLocation.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file: " + filename, e);
		}
	}

	@Override
	public Path load(final String filename) {
		log.debug("Load file {}.", filename);
		return rootLocation.resolve(filename);
	}

	@Override
	public List<XmlDocMetadata> listFiles() {
		// TODO Auto-generated method stub
		return null;
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
