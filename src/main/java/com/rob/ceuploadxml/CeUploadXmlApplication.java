package com.rob.ceuploadxml;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.rob.ceuploadxml.service.StorageProperties;
import com.rob.ceuploadxml.service.XmlStorageService;

@SpringBootApplication(scanBasePackages = {"com.rob.ceuploadxml"})
@EnableConfigurationProperties(StorageProperties.class)
public class CeUploadXmlApplication {

	/**
	 * Launch the app.
	 * 
	 * @param args not used
	 */
	public static void main(final String[] args) {
		SpringApplication.run(CeUploadXmlApplication.class, args);
	}

	/**
	 * Ensure storage is initialised.
	 * 
	 * @param storageService service that stores/retrieves files
	 * @return runner
	 */
	@Bean
	CommandLineRunner init(final XmlStorageService storageService) {
		return args -> storageService.init();
	}
}
