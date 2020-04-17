/**
 * 
 */
package com.soprasteria.springboot.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.ws.rs.core.Response;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.soprasteria.springboot.model.PrettyPrint;
import com.soprasteria.springboot.model.Split;

/**
 * @author tushar
 *
 */
@RestController
@RequestMapping("/home")
public class HomeController {

	@Autowired
	ServletContext context;

	private final Logger log = LoggerFactory.getLogger(HomeController.class);

	@GetMapping
	public String home() {
		return "forward:/index.html";
	}

	@PostMapping("/transformXml")
	public Response transformXml(@RequestParam("file") MultipartFile file, HttpSession session) throws IOException {
//		 Path rootLocation = Paths.get(session.getServletContext().getRealPath("/resources/images"));  
//         System.out.println("rootLocation  ==  " + rootLocation);
//         
//		log.info("Saving user."+file.getOriginalFilename());

		boolean isExist = new File(context.getRealPath("/")).exists();
		if (!isExist) {
			new File(context.getRealPath("/webapp")).mkdir();
		}

		String fileName = file.getOriginalFilename();
		String modifiedFileName = FilenameUtils.getBaseName(fileName) + "_" + System.currentTimeMillis() + "."
				+ FilenameUtils.getExtension(fileName);
		File serverFile = new File(context.getRealPath("/") + File.separator + modifiedFileName);

		try {
			FileUtils.writeByteArrayToFile(serverFile, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return Response.ok().build();
	}

	@GetMapping("/script")
	public void executeScript() {
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command("C:\\Users\\mjindal.EMEAAD\\Desktop\\FileFormatter.ksh");
		try {

			Process process = processBuilder.start();

			StringBuilder output = new StringBuilder();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;
			while ((line = reader.readLine()) != null) {
				output.append(line + "\n");
			}
			System.out.println("output::" + output.toString());
		} catch (Exception e) {
			e.printStackTrace();

		}
	}

	@PostMapping("/splitXml")
	public ResponseEntity<String> splitXml(@RequestParam("file") MultipartFile file,
			@RequestParam("typeOfSplit") String typeOfSplit, @RequestParam("level") String level,
			@RequestParam("size") String size, @RequestParam("splitByElement") String splitByElement) {
		log.info("File name." + file.getOriginalFilename());
		Split split = new Split(typeOfSplit, level, size, splitByElement);
		log.info("splitObject:" + split);
		return new ResponseEntity<String>("Everything is working fine", HttpStatus.OK);
	}

	@PostMapping("/sortXml")
	public ResponseEntity<String> sortXml(@RequestParam("file") MultipartFile file,
			@RequestParam("sortType") String typeOfSort, @RequestParam("attribute") String attribute,
			@RequestParam("keyattribute") String keyattribute, @RequestParam("idattribute") String idattribute) {
		log.info("File name." + file.getOriginalFilename());
		return new ResponseEntity<String>("Everything is working fine for sort", HttpStatus.OK);
	}

	
	
	@PostMapping("/prettyPrintXml")
	public ResponseEntity<String> prettyPrintXml(@RequestParam("file") MultipartFile file,
			@RequestParam("fileType") String fileType){
		log.info("File name." + file.getOriginalFilename());
		
		PrettyPrint print = new PrettyPrint(fileType);
		log.info("Pretty Print:" + print);
		
		return new ResponseEntity<String>("Everything is working fine", HttpStatus.OK);
	}
	


}
