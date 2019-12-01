package com.rob.ceuploadxml.model.repo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;

import com.rob.ceuploadxml.model.XmlDocMetadata;

/**
 * Test {@link XmlDocMetadataRepository}.
 */
@DataJpaTest
@ComponentScan("com.rob.ceuploadxml")
public final class XmlDocMetadataRepositoryIT {

	/**
	 * Repo to test.
	 */
	@Autowired
	private XmlDocMetadataRepository repo;

	/**
	 * Most basic test for a repo - can it save and load back the same object?
	 */
	@Test
	public void testSaveAndLoad() {
		XmlDocMetadata metadata = XmlDocMetadata.builder().filename("one.xml").note("An xml file.").build();
		XmlDocMetadata expected = repo.save(metadata);
		XmlDocMetadata actual = repo.findById(expected.getId()).get();
		assertEquals(expected, actual);
	}

}
