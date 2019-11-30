package com.rob.ceuploadxml.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.rob.ceuploadxml.model.XmlDoc.XmlDocBuilder;

/**
 * Test equality and validation (if any is built in to the Builder).
 */
public final class XmlDocTest {

	/**
	 * @return data for {@link #testEquality(String, XmlDoc, XmlDoc, boolean)}.
	 */
	private static Stream<Arguments> dataForTestEquality() {
		final XmlDoc xmlBase = XmlDoc.builder()//
				.filename("file.xml")//
				.note("An XML file.")//
				.xml("<data name=\"test\"></data>")//
				.build();

		final XmlDocBuilder builder = xmlBase.toBuilder();

		return Stream.of(//
				Arguments.of("Object equals itself.", xmlBase, xmlBase, true), //
				Arguments.of("State is same.", xmlBase, builder.build(), true), //
				Arguments.of("Note is different.", xmlBase, builder.note("The XML file.").build(), false), //
				Arguments.of("ID is different.", xmlBase, builder.id(2).build(), false), //
				Arguments.of("File name is different.", xmlBase, builder.filename("file2.xml").build(), false), //
				Arguments.of("XML is different.", xmlBase, builder.xml("<data></data>").build(), false));
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
	public void testEquality(final String label, final XmlDoc xml1, final XmlDoc xml2, final boolean shouldBeEqual) {
		assertEquals(shouldBeEqual, xml1.equals(xml2), label);
	}

}
