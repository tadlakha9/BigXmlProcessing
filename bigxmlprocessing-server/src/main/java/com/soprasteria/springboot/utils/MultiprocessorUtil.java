package com.soprasteria.springboot.utils;

import com.soprasteria.springboot.constants.Messages;
import com.soprasteria.springboot.constants.MultiProcessorConstants;
import com.soprasteria.springboot.constants.ScriptConstants;

public class MultiprocessorUtil {
	
	/**
     * Method to convert window path to Linux Path
     * @param path original Path
     * @return Processed Path
     * @throws IllegalArgumentException if an invalid path is provided
     */
	public static StringBuilder convertToScriptPath(String path) {
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
		return processedPath;
	}
}
