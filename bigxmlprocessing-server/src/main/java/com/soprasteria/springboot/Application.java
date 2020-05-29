/**
 * 
 */
package com.soprasteria.springboot;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.soprasteria.springboot.constants.MultiProcessorConstants;
import com.soprasteria.springboot.constants.PropertyConstants;
import com.soprasteria.springboot.constants.ScriptConstants;
import com.soprasteria.springboot.utils.ExecProcess;
import com.soprasteria.springboot.utils.MultiprocessorUtil;

/**
 * @author tushar
 *
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		
		//calling method to clean folder
		cleanTargetFolder();
		
		SpringApplication.run(Application.class, args);

	}

	/**
	 * Method to clean Target folder
	 * @throws Exception
	 */
	private static void cleanTargetFolder() throws Exception {
		try {

			ExecProcess exec = null;
			String cmd;

			// getting directory path
			String dirPath = MultiprocessorUtil.getApplicationProperty(PropertyConstants.FOLDER_PATH); 
			
			String localScript = MultiprocessorUtil.getApplicationProperty(PropertyConstants.SCRIPT_PATH);
			String localScriptPath = MultiProcessorConstants.INVERTED_COMMA
					+ MultiprocessorUtil.convertToScriptPath(localScript)
					+ MultiProcessorConstants.INVERTED_COMMA;
			dirPath = MultiprocessorUtil.convertToScriptPath(dirPath);

			// deleting contents in folder before start of application
			cmd = ScriptConstants.BASH + MultiProcessorConstants.SPACE + localScriptPath + MultiProcessorConstants.SPACE
					+ ScriptConstants.DELETE_CONTENT + MultiProcessorConstants.SPACE + dirPath;
			exec = new ExecProcess(cmd);
			exec.run();

		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
		}
	}

}
