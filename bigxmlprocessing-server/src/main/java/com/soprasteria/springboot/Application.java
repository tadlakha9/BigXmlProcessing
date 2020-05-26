/**
 * 
 */
package com.soprasteria.springboot;

import java.io.File;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

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
			String dirPath = "C:\\Skillmatrix\\Users";
			File localScript = new File("src//main//resources//FileFormatter.ksh");
			String localScriptPath = "'"
					+ MultiprocessorUtil.convertToScriptPath(localScript.getAbsolutePath()).toString() + "'";
			dirPath = MultiprocessorUtil.convertToScriptPath(dirPath).toString();

			cmd = "bash " + localScriptPath + " " + "-deletedir" + " " + dirPath;
			exec = new ExecProcess(cmd);
			exec.run();

		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
		}
		SpringApplication.run(Application.class, args);

	}

}
