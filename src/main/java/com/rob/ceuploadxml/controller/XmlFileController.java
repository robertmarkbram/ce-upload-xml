package com.rob.ceuploadxml.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rob.ceuploadxml.model.XmlDocMetadata;
import com.rob.ceuploadxml.service.XmlStorageService;

import lombok.extern.log4j.Log4j2;

@RestController
@RequestMapping(path = "/xmldoc")
@Log4j2
public final class XmlFileController {

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
	public XmlDocMetadata saveXmlDoc(@RequestParam final MultipartFile file, @RequestParam final String note) {

		log.debug(() -> String.format("Attempting to add XML doc [%s] with note [%s].", file, note));

		// @ResponseBody means the returned String is the response, not a view name
		// @RequestParam means it is a parameter from the GET or POST request

		return XmlDocMetadata.builder().filename(file.getOriginalFilename())
				.size(file.getSize()).note(note).build();
	}

	@GetMapping("/get/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> getXmlDoc(@PathVariable final String filename) {

		log.info(() -> String.format("Looking for XML doc [%s].", filename));
		Resource file = xmlStorageService.loadAsResource(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}
}
