/**
 * 
 */
package com.soprasteria.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import com.soprasteria.springboot.constants.PropertyConstants;
import com.soprasteria.springboot.utils.MultiprocessorUtil;

/**
 * @author tushar
 *
 */
@SpringBootApplication
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) throws Exception {
		
		//calling method to clean multiprocessor folder
		cleanMultiFolder();
		
		SpringApplication.run(Application.class, args);

	}

	/**
	 * Method to clean Multiprocessor folder
	 * 
	 * @throws Exception
	 */
	private static void cleanMultiFolder() throws Exception {
		try {
			// getting path of multiprocessor folder
			String dirPath = MultiprocessorUtil.getApplicationProperty(PropertyConstants.MULTIPROCESSOR_PATH);
			MultiprocessorUtil.deleteDirectory(dirPath);
		} catch (Exception e) {
			throw new Exception(e.getLocalizedMessage());
		}
	}

}
	
