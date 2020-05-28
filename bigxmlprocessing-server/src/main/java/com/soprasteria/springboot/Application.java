/**
 * 
 */
package com.soprasteria.springboot;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.soprasteria.springboot.constants.MultiProcessorConstants;
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
		try {

			ExecProcess exec = null;
			String cmd;
			String dirPath = "C:\\Temp\\MultiProcessor\\Target";
			File localScript = new File("src//main//resources//FileFormatter.ksh");
			String localScriptPath = "'"
					+ MultiprocessorUtil.convertToScriptPath(localScript.getAbsolutePath()).toString()
					+ MultiProcessorConstants.INVERTED_COMMA;
			dirPath = MultiprocessorUtil.convertToScriptPath(dirPath).toString();

			cmd = ScriptConstants.BASH + MultiProcessorConstants.SPACE + localScriptPath + MultiProcessorConstants.SPACE
					+ ScriptConstants.DELETE_CONTENT + MultiProcessorConstants.SPACE + dirPath;
			exec = new ExecProcess(cmd);
			exec.run();

		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
		}
		SpringApplication.run(Application.class, args);

	}

}
