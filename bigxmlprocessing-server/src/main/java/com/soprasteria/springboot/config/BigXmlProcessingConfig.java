package com.soprasteria.springboot.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Multi Processor configuration implementation.
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
	
	/** The search folder path. */
    @Value("${search.folder.path}")
    private String searchFolderPath;
   
	public String getSearchFolderPath() {
		 return searchFolderPath;
	}
	
	/**
	 * Index file name
	 */
	 @Value("${index.page.path}")
	private String indexpage;
	
	
	public String getIndexPage() {
		return indexpage;
	}
	
	
	
	/**
	 * Script File path 	
	 */
	 @Value("${script.folder.path}")
	private String scriptFilePath;
	
	
	public String getScriptFilePath() {
		return scriptFilePath;
	}
	
	/**
	 * Result file name
	 */
	 @Value("${result.filename}")
	private String resultFile;
	
	
	public String getResultFile() {
		return resultFile;
	}
	
	
}
