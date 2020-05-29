package com.soprasteria.springboot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.soprasteria.springboot.constants.Messages;
import com.soprasteria.springboot.constants.MultiProcessorConstants;
import com.soprasteria.springboot.constants.ScriptConstants;

public class MultiprocessorUtil {

	/**
	 * Properties object application parameters
	 */
	private static Properties applicationProperties;
	/** * Application.properties path */
	private static final String applicationPropertiesFilePath = "src//main//resources//application.properties";

	/**
	 * Method to convert window path to Linux Path
	 * 
	 * @param path original Path
	 * @return Processed Path
	 * @throws IllegalArgumentException if an invalid path is provided
	 */
	public static String convertToScriptPath(String path) {
		StringBuilder processedPath = new StringBuilder();

		// Return Empty Path if there is nothing to process in original Path
		if ((path == null) || (path.isEmpty()))
			throw new IllegalArgumentException(Messages.INVALID_PATH);

		String[] driveAndFolder = path.split(MultiProcessorConstants.COLON);

		// Handle invalid Paths
		if (driveAndFolder.length == 0 || driveAndFolder.length == 1)
			throw new IllegalArgumentException(Messages.INVALID_PATH);
		else {
			processedPath.append(ScriptConstants.ROOTPATH);
			processedPath.append(driveAndFolder[0].toLowerCase());
			processedPath.append(
					driveAndFolder[1].replace(MultiProcessorConstants.BACKSLASH, MultiProcessorConstants.SLASH));
		}
		return processedPath.toString();
	}

	/**
	 * Method to Load the specified parameter file into Properties object.
	 * 
	 * @param filePath   : propertyFile
	 * @param obligatory : if mandatory throw an exception if file not exist
	 * @return the Properties
	 * @throws IOException : If file not found or any other issue in loading the
	 *                     property file
	 */
	private static Properties loadParamFile(boolean obligatory) throws IOException {
		Properties properties = null;
		File applicationPropertyFile = new File(applicationPropertiesFilePath);
		try (FileInputStream paramStream = new FileInputStream(applicationPropertyFile)) {
			properties = new Properties();
			properties.load(paramStream);
		} catch (IOException ixe) {
			if (obligatory)
				throw new IOException(ixe.getLocalizedMessage());
		}
		return properties;
	}

	/**
	 * Search property in application.properties file
	 * 
	 * @param property property to be searched
	 * @return value corresponding to property if found
	 * @throws IOException if error in loading property File
	 */

	public static String getApplicationProperty(String property) throws IOException {
		String value = null;
		if (applicationProperties == null) {
			applicationProperties = loadParamFile(true);
		}
		if (applicationProperties != null)
			value = applicationProperties.getProperty(property);
		return value;
	}
	
	/**
	 * Method for command creation
	 * 
	 * @param option   For e.g. -splits, -sort, -format
	 * @param filePath path of the file
	 * @param args     all the other parameters
	 * @return cmd command to be executed
	 * 
	 */

	public static String getProcessorCommand(String option, String filePath, String... args) {

		StringBuilder cmd = new StringBuilder(option);
		cmd.append(MultiProcessorConstants.SPACE);
		cmd.append(filePath);

		for (String argument : args) {
			cmd.append(MultiProcessorConstants.SPACE);
			cmd.append(argument);
			cmd.append(MultiProcessorConstants.SPACE);
		}
		return cmd.toString();

	}
}

