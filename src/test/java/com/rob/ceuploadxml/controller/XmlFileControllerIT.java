package com.rob.ceuploadxml.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.IOException;
import java.nio.file.Files;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rob.ceuploadxml.model.XmlDocMetadata;
import com.rob.ceuploadxml.service.StorageException;
import com.rob.ceuploadxml.service.StorageFileNotFoundException;
import com.rob.ceuploadxml.service.XmlStorageService;

/**
 * Integration test of {@link XmlFileController} using a mock of
 * {@link XmlStorageService}, so it just tests controller logic.
 */
@ExtendWith(SpringExtension.class)
@WebMvcTest(XmlFileController.class)
@ComponentScan("com.rob.ceuploadxml")
public final class XmlFileControllerIT {

	/**
	 * HTTP status meaning resource not found.
	 */
	private static final int STATUS_404 = 404;

	/**
	 * HTTP status meaning resource bad request or bad data sent within request.
	 */
	private static final int STATUS_400 = 400;

	/**
	 * HTTP status meaning request was good.
	 */
	private static final ResultMatcher STATUS_OK = status().isOk();

	/**
	 * Where test files should be under project target/test-classes.
	 */
	private static final String TEST_FILES_LOCATION = "xml-files-to-upload/";

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
	 * Object to convert JSON return from RESTfull calls into the object they should
	 * map to.
	 */
	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Service underlying the controller we are testing.
	 */
	@MockBean
	private XmlStorageService service;

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
	 * @throws Exception if test code throws an unexpected exception
	 */
	@ParameterizedTest(name = "#{index} - [{0}]")
	@MethodSource("dataForTestDownloadFile")
	public void testDownloadFile(final String label, final String fileToRequest, final String fileToLoad,
			final ResultMatcher status) throws Exception {

		Resource xmlDoc = new ClassPathResource(TEST_FILES_LOCATION + fileToLoad);

		if (status.equals(STATUS_OK)) {
			Mockito.when(service.loadAsResource(Mockito.anyString())).thenReturn(xmlDoc);
		} else {
			Mockito.when(service.loadAsResource(Mockito.anyString())).thenThrow(StorageFileNotFoundException.class);
		}

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(PATH_GET + fileToRequest)//
				.contentType(MediaType.APPLICATION_JSON)//
				.accept(MediaType.APPLICATION_JSON))//
				.andExpect(status).andReturn();

		if (status.equals(STATUS_OK)) {
			String resultXml = result.getResponse().getContentAsString();
			assertNotNull(resultXml);
			assertEquals(Files.readString(xmlDoc.getFile().toPath()), resultXml, label);
		}
	}

	/**
	 * @return data for
	 *         {@link #testUploadFile(String, String, XmlDocMetadata, ResultMatcher)}
	 * @throws IOException if we have a problem accessing the test file.
	 */
	private static Stream<Arguments> dataForTestUploadFile() throws IOException {
		String filename1 = "test01.xml";
		String note1 = "Note about XML file.";
		Resource xmlDoc = new ClassPathResource(TEST_FILES_LOCATION + filename1);

		XmlDocMetadata metadata = XmlDocMetadata.builder()//
				.size(xmlDoc.getFile().length())//
				.note(note1)//
				.filename(filename1)//
				.build();

		return Stream.of(//
				Arguments.of("File is valid.", metadata, STATUS_OK), //
				Arguments.of("File should fail.", metadata, status().is(STATUS_400))//
		);
	}

	/**
	 * Test downloading of a file. Only testing logic in the controller, not the
	 * storage service. Since the controller really only calls the storage service,
	 * this is a bit of a pointless test right now, but included in case logic gets
	 * added.
	 * 
	 * @param label    for test
	 * @param expected data we expect to get back about the file we saved; can be
	 *                 null if <code>status</code> is not OK.
	 * @param status   HTTP response we should get back
	 * @throws Exception if test code throws an unexpected exception
	 */
	@ParameterizedTest(name = "#{index} - [{0}]")
	@MethodSource("dataForTestUploadFile")
	public void testUploadFile(final String label, final XmlDocMetadata expected, final ResultMatcher status)
			throws Exception {

		Resource xmlDoc = new ClassPathResource(TEST_FILES_LOCATION + expected.getFilename());

		MockMultipartFile fileToUpload = new MockMultipartFile("file", expected.getFilename(), "text/plain",
				Files.readString(xmlDoc.getFile().toPath()).getBytes());

		if (status.equals(STATUS_OK)) {
			Mockito.when(service.store(Mockito.any(), Mockito.anyString())).thenReturn(expected);
		} else {
			Mockito.when(service.store(Mockito.any(), Mockito.anyString())).thenThrow(StorageException.class);
		}

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
		}
	}
}
