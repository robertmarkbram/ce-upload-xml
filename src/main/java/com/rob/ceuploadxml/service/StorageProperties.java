package com.rob.ceuploadxml.service;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@ConfigurationProperties("storage")
@Data
@Builder(toBuilder = true)
@EqualsAndHashCode
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class StorageProperties {

	/**
	 * Folder for storing files.
	 */
	@NonNull
	private String location;

}
