package com.rob.ceuploadxml.validator;

import java.io.IOException;
import java.nio.file.Path;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import lombok.extern.log4j.Log4j2;

/**
 * Validate XML.
 */
@Log4j2
public final class XmlValidator {

	/**
	 * Is the XML well formed? <b>Does not check</b> if it is valid against an XSD.
	 * 
	 * @param xmlPath path to XML file.
	 * @return true if the XML in <code>xmlPath</code> is well formed.
	 */
	public boolean xmlIsWellFormed(final Path xmlPath) {
		try {
			parseXml(xmlPath);
			return true;
		} catch (IOException | ParserConfigurationException | SAXException e) {
			log.error("Invalid XML within file: {}", xmlPath.toAbsolutePath(), e);
			return false;
		}
	}

	/**
	 * Parse the XML, throwing an exception on failure.
	 * 
	 * <b>Implementation note</b>: creating a new <code>DocumentBuilder</code> each
	 * time to prevent possible caching of previous XML documents. This may need to
	 * be revised.
	 * 
	 * @param xmlPath path to saved XML file
	 * @return parsed XML
	 * @throws IOException                  if we have problem reading the file
	 * @throws ParserConfigurationException if we cannot add XXE security feature
	 * @throws SAXException                 if we cannot parse the XML
	 */
	private Document parseXml(final Path xmlPath) throws IOException, ParserConfigurationException, SAXException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		// Prevent XXE attacks.
		factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		DocumentBuilder db = factory.newDocumentBuilder();
		return db.parse(xmlPath.toFile());
	}

}
