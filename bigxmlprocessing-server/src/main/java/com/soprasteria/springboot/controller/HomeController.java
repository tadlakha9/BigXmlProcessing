/**
 * 
 */
package com.soprasteria.springboot.controller;

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
	public String addNewUser(@RequestParam("file") MultipartFile file) {
		log.info("Saving user."+file.getOriginalFilename());

		return "Everything is fine";
	}

}
