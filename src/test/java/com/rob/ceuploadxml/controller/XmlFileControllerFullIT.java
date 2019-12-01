package com.rob.ceuploadxml.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.FileCopyUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rob.ceuploadxml.model.XmlDocMetadata;
import com.rob.ceuploadxml.service.XmlStorageService;

import lombok.extern.log4j.Log4j2;

/**
 * Full integration test of {@link XmlFileController} and
 * {@link XmlStorageService}.
 */
@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@ComponentScan("com.rob.ceuploadxml")
@Log4j2
public final class XmlFileControllerFullIT {

	/**
	 * HTTP status meaning resource not found.
	 */
	private static final int STATUS_404 = 404;

	/**
	 * HTTP status meaning request was good.
	 */
	private static final ResultMatcher STATUS_OK = status().isOk();

	/**
	 * Where test files should be written to under project root.
	 */
	private static final String TEST_FILES_OUTPUT_LOCATION = "target/test-classes/xml-files/";

	/**
	 * Where test files should be loaded from under src/test/resources.
	 */
	private static final String TEST_FILES_FOR_UPLOAD_LOCATION = "/xml-files-to-upload/";

	/**
	 * REST path to get a doc.
	 */
	private static final String PATH_GET = "/xmldoc/get/";

	/**
	 * REST path to save a doc.
	 */
	private static final String PATH_ADD = "/xmldoc/add/";

	/**
	 * Allows mocking of Rest calls via MVC interface.
	 */
	@Autowired
	private MockMvc mockMvc;

	/**
	 * Object to convert JSON return from RESTfull calls into the object they should map to. 
	 */
	@Autowired
	private ObjectMapper objectMapper;
	
	/**
	 * @return data for
	 *         {@link #testDownloadFile(String, String, String, ResultMatcher)}
	 */
	private static Stream<Arguments> dataForTestDownloadFile() {
		return Stream.of(//
				Arguments.of("File exists OK.", "test01.xml", "test01.xml", STATUS_OK), //
				Arguments.of("File does not exist.", "badFile.xml", "test01.xml", status().is(STATUS_404))//
		);
	}

	/**
	 * Test downloading of a file.
	 * 
	 * @param label         for test
	 * @param fileToRequest name of file we will request from the REST end point
	 * @param fileToLoad    name of file we will load from file system to compare
	 *                      contents with; can be null if <code>status</code> is not
	 *                      OK.
	 * @param status        HTTP response we should get back
	 */
	@ParameterizedTest(name = "#{index} - [{0}]")
	@MethodSource("dataForTestDownloadFile")
	public void testDownloadFile(final String label, final String fileToRequest, final String fileToLoad,
			final ResultMatcher status) {

		Resource xmlDoc = new ClassPathResource(TEST_FILES_FOR_UPLOAD_LOCATION + fileToLoad);

		
		assertDoesNotThrow(() -> {
			// Ensure file we want to read exists in the stored files location.
			File src = xmlDoc.getFile();
			File destination = new File(TEST_FILES_OUTPUT_LOCATION + fileToLoad);
			log.info("Copy file from {} to {}.", src.getAbsolutePath(), destination.getAbsolutePath());
			FileCopyUtils.copy(src, destination);
			
			MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(PATH_GET + fileToRequest)//
					.contentType(MediaType.APPLICATION_JSON)//
					.accept(MediaType.APPLICATION_JSON))//
					.andExpect(status).andReturn();

			if (status.equals(STATUS_OK)) {
				String resultXml = result.getResponse().getContentAsString();
				assertNotNull(resultXml);
				assertEquals(Files.readString(src.toPath()), resultXml, label);
			}
		});
	}


	/**
	 * @return data for
	 *         {@link #testUploadFile(String, String, XmlDocMetadata, ResultMatcher)}
	 * @throws IOException if we have a problem accessing the test file.
	 */
	private static Stream<Arguments> dataForTestUploadFile() throws IOException {
		String filename1 = "test01.xml";
		String note1 = "Note about XML file.";
		Resource xmlDoc = new ClassPathResource(TEST_FILES_FOR_UPLOAD_LOCATION + filename1);
		
		XmlDocMetadata metadata = XmlDocMetadata.builder()//
				.size(xmlDoc.getFile().length())//
				.note(note1)//
				.filename(filename1)//
				.build();

		
		return Stream.of(//
				Arguments.of("File is valid.", metadata, STATUS_OK), //
				Arguments.of("File does not exist.", metadata, status().is(STATUS_404))//
		);
	}

	/**
	 * Test downloading of a file.
	 * 
	 * @param label            for test
	 * @param expected         data we expect to get back about the file we saved;
	 *                         can be null if <code>status</code> is not OK.
	 * @param status           HTTP response we should get back
	 */
	@ParameterizedTest(name = "#{index} - [{0}]")
	@MethodSource("dataForTestUploadFile")
	public void testUploadFile(final String label, final XmlDocMetadata expected,
			final ResultMatcher status) {

		Resource xmlDoc = new ClassPathResource(TEST_FILES_FOR_UPLOAD_LOCATION + expected.getFilename());

		assertDoesNotThrow(() -> {

			MockMultipartFile fileToUpload = new MockMultipartFile("file", expected.getFilename(), "text/plain",
				Files.readString(xmlDoc.getFile().toPath()).getBytes());

			MvcResult result = mockMvc.perform(MockMvcRequestBuilders//
					.multipart(PATH_ADD)//
					.file(fileToUpload)//
					.param("note", expected.getNote())//
					.accept(MediaType.APPLICATION_JSON))//
					.andExpect(status).andReturn();

			if (status.equals(STATUS_OK)) {
				String contentAsString = result.getResponse().getContentAsString();
				assertNotNull(contentAsString);
				XmlDocMetadata actual = objectMapper.readValue(contentAsString, XmlDocMetadata.class);
				assertEquals(expected, actual, label);
				// Uploaded file must exist.
				Resource xmlDocUploaded = new ClassPathResource(TEST_FILES_OUTPUT_LOCATION + expected.getFilename());
				assertTrue(xmlDocUploaded.exists());

			}
		});
	}
}
