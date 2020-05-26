package com.soprasteria.springboot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Skill Matrix configuration implementation.
 *
 */
@Configuration

public class BigXmlProcessingConfig  {


    /** The Users folder path. */
    @Value("${users.folder.path}")
    private String usersFolderPath;

   
	 
    /**
     * {@inheritDoc}
     */
	
	public String getUsersFolderPath() {
		 return usersFolderPath;
	}
	
}
