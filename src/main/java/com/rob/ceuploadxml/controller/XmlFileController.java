package com.rob.ceuploadxml.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.rob.ceuploadxml.model.XmlDocMetadata;
import com.rob.ceuploadxml.service.XmlStorageService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(path = "/xmldoc")
@Log4j2
public class XmlFileController {

	/**
	 * XML file service.
	 */
	private XmlStorageService xmlStorageService;

	/**
	 * @param service XML file service
	 */
	@Autowired
	public XmlFileController(final XmlStorageService service) {
		this.xmlStorageService = service;
	}

	@PostMapping(path = "/add")
	@ResponseBody
	public final XmlDocMetadata saveXmlDoc(//
			@RequestParam("file") final MultipartFile file, //
			@RequestParam("note") final String note) {

		log.debug(() -> String.format("Attempting to add XML doc [%s] with note [%s].", file.getOriginalFilename(),
				note));

		if (StringUtils.isEmpty(note)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Note cannot be empty.");
		}
		if (note.length() > 200) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Note cannot be greater than 200 characters.");
		}
		if (file == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "File must be supplied.");
		}

		return xmlStorageService.store(file, note);
	}

	@GetMapping("/get/{filename:.+}")
	@ResponseBody
	public final ResponseEntity<Resource> getXmlDoc(@PathVariable final String filename) {

		log.info(() -> String.format("Looking for XML doc [%s].", filename));
		Resource file = xmlStorageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}


	@GetMapping("/list")
	@ResponseBody
	public final List<XmlDocMetadata> listFiles() {

		log.info("Listing files.");
		return xmlStorageService.listFiles();
	}

}
