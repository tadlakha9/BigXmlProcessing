package com.soprasteria.springboot.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.soprasteria.springboot.constants.Messages;
import com.soprasteria.springboot.constants.MultiProcessorConstants;
import com.soprasteria.springboot.constants.PropertyConstants;
import com.soprasteria.springboot.constants.ScriptConstants;

public class MultiprocessorUtil {

	/**
	 * Properties object application parameters
	 */
	private static Properties applicationProperties;
	
	/** Application.properties file path */
	private static final String APPLICATION_PROPERTY_FILE_PATH = "src//main//resources//application.properties";
	
	/** Operating System on which application is running */
	private static final String OS = System.getProperty("os.name");
	
	/**
	 * Method to convert window path to Linux Path
	 * 
	 * @param path original Path
	 * @return Processed Path
	 * @throws IllegalArgumentException if an invalid path is provided
	 */
	public static String convertToScriptPath(String path, String rootPath) {
		StringBuilder processedPath = new StringBuilder();

		// Return Empty Path if there is nothing to process in original Path
		if ((path == null) || (path.isEmpty()))
			throw new IllegalArgumentException(Messages.INVALID_PATH);

		String[] driveAndFolder = path.split(MultiProcessorConstants.COLON);

		// Check if already in script mode
		if (driveAndFolder.length == 0 || driveAndFolder.length == 1)
			return path;
		else {
			// Append '/mnt/' only if it is 'Window 10' otherwise only '/'
			if (rootPath != null && !rootPath.isEmpty()) 
				processedPath.append(rootPath);
			
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
		File applicationPropertyFile = new File(APPLICATION_PROPERTY_FILE_PATH);
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
	
	
	/**
	 * Method to clean given directory
	 * 
	 * @param dirPath Directory given to delete its content
	 * @throws Exception
	 */
	public static void deleteDirectory(String dirPath) throws Exception {
		ExecProcess exec = null;
		String cmd;

		if (new File(dirPath).exists()) {
			// getting script path
			String scriptpath = getApplicationProperty(PropertyConstants.SCRIPT_PATH);
			
			// getting root path
			String rootPath = getApplicationProperty(PropertyConstants.ROOT_PATH);

			File localScript = new File(scriptpath);
			String localScriptPath = MultiProcessorConstants.INVERTED_COMMA
					+ convertToScriptPath(localScript.getAbsolutePath(), rootPath) + MultiProcessorConstants.INVERTED_COMMA;
			dirPath = convertToScriptPath(dirPath, rootPath);

			// deleting contents in folder before start of application
			cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.BASH, localScriptPath,
					ScriptConstants.DELETE_CONTENT, dirPath);

			exec = new ExecProcess(cmd);
			try {
				exec.run();
			} catch (Exception e) {
				throw new Exception(e.getLocalizedMessage());
			}
		}
	}
	
	/**
	 * Method to check if OS: Windows
	 * @return boolean: true if Windows, false otherwise
	 */
	public static boolean isWindows() {
		return (OS.indexOf(MultiProcessorConstants.WIN) >= 0);
	}
	
	/**
	 * Method to check if OS: Unix or Linux
	 * 
	 * @return boolean: true if Unix or Linux, false otherwise
	 */
	public static boolean isUnixOrLinux() {
		return (OS.indexOf(MultiProcessorConstants.NIX) >= 0 || OS.indexOf(MultiProcessorConstants.NUX) >= 0
				|| OS.indexOf(MultiProcessorConstants.AIX) >= 0);
	}
}

