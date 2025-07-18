package com.example.demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
*
* @author Amar Pattanshetti
* 
*/

@Configuration
public class JackSonConfig {

	@Bean
	public ObjectMapper objectMapper() {
	    ObjectMapper mapper = new ObjectMapper();
	    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 👈 ignore unknown fields
	    return mapper;
	}

}
