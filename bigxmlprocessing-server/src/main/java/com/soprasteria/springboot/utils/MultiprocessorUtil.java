package com.soprasteria.springboot.utils;

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
        if ((path == null)  || (path.isEmpty()))
            throw new IllegalArgumentException("Path is invalid");
       
        String[] driveAndFolder = path.split(":");
       
        // Handle invalid Paths
        if (driveAndFolder.length == 0 || driveAndFolder.length == 1)
            throw new IllegalArgumentException("Path is invalid");
        else {
            processedPath.append("/mnt/");
            processedPath.append(driveAndFolder[0].toLowerCase());
            processedPath.append(driveAndFolder[1].replace("\\", "/"));
        }
        return processedPath;
    }
}
