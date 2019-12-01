package com.rob.ceuploadxml.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * Test {@link XmlValidator}.
 */
public final class XmlValidatorTest {

	/**
	 * Where test files should be under project target/test-classes.
	 */
	private static final String TEST_FILES_LOCATION = "xml-files-to-upload/";

	/**
	 * @return data for {@link #testValidatingXml(String, String, boolean)}
	 */
	private static Stream<Arguments> dataForTestValidatingXml() {
		return Stream.of(//
				Arguments.of("File is valid.", "test01.xml", true), //
				Arguments.of("File is not well formed XML.", "invalid01.xml", false)//
		);
	}

	/**
	 * Test downloading of a file.
	 * 
	 * @param label    for test
	 * @param xmlPath  path to file we want to validate
	 * @param expected true if the file should be valid; false otherwise
	 * @throws IOException if test code throws an unexpected exception
	 */
	@ParameterizedTest(name = "#{index} - [{0}]")
	@MethodSource("dataForTestValidatingXml")
	public void testValidatingXml(final String label, final String xmlPath, final boolean expected) throws IOException {

		XmlValidator validator = new XmlValidator();

		Resource xmlDoc = new ClassPathResource(TEST_FILES_LOCATION + xmlPath);

		assertEquals(expected, validator.xmlIsWellFormed(xmlDoc.getFile().toPath()), label);
	}
}
