package com.rob.ceuploadxml.service;

import java.nio.file.Path;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.rob.ceuploadxml.model.XmlDocMetadata;

/**
 * Service for dealing with XML files from some file store.
 */
public interface XmlStorageService {

	void init();

	/**
	 * Save a file to storage.
	 * 
	 * @param file file to store
	 * @param note or description to save against the file
	 * @return data about file stored
	 */
	XmlDocMetadata store(MultipartFile file, String note);

	/**
	 * Load a file from storage.
	 * 
	 * @param filename name of file to load
	 * @return path object for file
	 */
	Path load(String filename);

	/**
	 * @param filename name of file to load
	 * @return resource object for file
	 */
	Resource loadAsResource(String filename);

	/**
	 * @return list of all XML files.
	 */
	List<XmlDocMetadata> listFiles();

}
