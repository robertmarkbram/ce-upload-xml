package com.rob.ceuploadxml.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

/**
 * XML document.
 */
@Entity
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
public final class XmlDoc {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private final Integer id;

	/**
	 * XML payload.
	 */
	@NonNull
	private final String xml;

	/**
	 * File name.
	 */
	@NonNull
	private final String filename;

	/**
	 * Note or description.
	 */
	@NonNull
	private final String note;

}
