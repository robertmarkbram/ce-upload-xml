package com.rob.ceuploadxml.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.rob.ceuploadxml.model.XmlDocMetadata.XmlDocMetadataBuilder;

/**
 * Test equality and validation (if any is built in to the Builder).
 */
public final class XmlDocMetadataTest {

	/**
	 * @return data for
	 *         {@link #testEquality(String, XmlDocMetadata, XmlDocMetadata, boolean)}.
	 */
	private static Stream<Arguments> dataForTestEquality() {
		final XmlDocMetadata xmlBase = XmlDocMetadata.builder()//
				.filename("file.xml")//
				.note("An XML file.")//
				.size(2)
				.build();

		final XmlDocMetadataBuilder builder = xmlBase.toBuilder();

		return Stream.of(//
				Arguments.of("Object equals itself.", xmlBase, xmlBase, true), //
				Arguments.of("State is same.", xmlBase, builder.build(), true), //
				Arguments.of("Note is different.", xmlBase, builder.note("The XML file.").build(), false), //
				Arguments.of("ID is different.", xmlBase, builder.id(2).build(), false), //
				Arguments.of("Size is different.", xmlBase, builder.size(1).build(), false), //
				Arguments.of("File name is different.", xmlBase, builder.filename("file2.xml").build(), false));
	}

	/**
	 * Test that all fields are used in equality.
	 * 
	 * @param label         for test
	 * @param xml1          first XML doc to compare for equality
	 * @param xml2          second XML doc to compare for equality
	 * @param shouldBeEqual should the two XML docs be equal?
	 */
	@ParameterizedTest(name = "#{index} - [{0}]")
	@MethodSource("dataForTestEquality")
	public void testEquality(final String label, final XmlDocMetadata xml1, final XmlDocMetadata xml2,
			final boolean shouldBeEqual) {
		assertEquals(shouldBeEqual, xml1.equals(xml2), label);
	}

}
