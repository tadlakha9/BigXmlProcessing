/**
 * 
 */
package com.soprasteria.springboot.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author tushar
 *
 */
@Controller
@RequestMapping("/home")
public class HomeController {
	
	private final Logger log = LoggerFactory.getLogger(HomeController.class);
	
	
	@GetMapping
	public String home() {
		return "forward:/index.html";
	}
	
	@PostMapping("/transformXml")
	public Response transformXml(@RequestParam("file") MultipartFile file) {
		log.info("Saving user."+file.getOriginalFilename());

		return Response.ok().build();
	}
	
	@GetMapping("/script")
	public void executeScript() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("C:\\Users\\tadlakha\\Desktop\\GitTesting\\practise\\test.sh");
		try {

			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			
			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			System.out.println("output::"+output.toString());
		}catch(Exception e) {
			e.printStackTrace();
			
		}
	}
	
	

}
