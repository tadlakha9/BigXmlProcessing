/**
 * 
 */
package com.soprasteria.springboot.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
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

import com.soprasteria.springboot.model.Converter;
import com.soprasteria.springboot.model.PrettyPrint;
import com.soprasteria.springboot.model.Split;
import com.soprasteria.springboot.utils.ExecProcess;

/**
 * @author tushar
 *
 */
@RestController
@RequestMapping("/home")
public class HomeController {

	int SttdCode = 0;
	String StdOut = null;
	String StdErr = null;
	
	@Autowired
	ServletContext context;

	private final Logger log = LoggerFactory.getLogger(HomeController.class);

	/**
	 * @return
	 */
	@GetMapping
	public String home() {
		return "forward:/index.html";
	}

	/**
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@PostMapping("/parseXml")
	public Response transformXml(@RequestParam("file") MultipartFile file) throws IOException {
		createLocalFolder();		

		createLocalFile(file);
		
		return Response.ok().build();
	}

	/**
	 * 
	 */
	@GetMapping("/script")
	public void executeScript(String command) {
		log.info("inside executeScript method");
		ExecProcess exec = null;
    	String cmd = "bash /$HOME/FileFormatter.ksh ";

        try {
        	cmd = cmd + command;
        	//cmd = "bash /home/user/test.sh -help"; 
        	//cmd = "bash /home/mjindal/FileFormatter.ksh -splits /mnt/c/Users/mjindal.EMEAAD/Documents/G101_MNT_L_0001_0001_AMM_AIRCRAFT.XML 100Kb";
        	exec = new ExecProcess(cmd);
        	exec.run();
            this.StdOut =exec.getStdout();
            this.SttdCode = exec.getReturnValue();
                      
        } catch (Exception e) {
        	this.StdErr = exec.getStderr();
            
            e.printStackTrace();
            
        } 
	}

	/**
	 * @param file
	 * @param typeOfSplit(Level, Element,Flat,Size)
	 * @param level(split:Level)
	 * @param size(split:Size)
	 * @param splitByElement(split:Element)
	 * @param splitType(split:Flat(By line/Size))
	 * @param splitByLine(split:Flat)
	 * @param splitBySize(split:Flat)
	 * @return
	 */
	@PostMapping("/splitXml")
	public ResponseEntity<String> splitXml(@RequestParam("file") MultipartFile file,
			@RequestParam("typeOfSplit") String typeOfSplit, @RequestParam("level") String level,
			@RequestParam("size") String size, @RequestParam("splitByElement") String splitByElement,
			@RequestParam("splitType") String splitType, @RequestParam("splitByLine") String splitByLine, 
			@RequestParam("splitBySize") String splitBySize) {
		
		log.info("File name." + file.getOriginalFilename());
		Split split = new Split(typeOfSplit, level, size, splitByElement, splitBySize, splitBySize, splitBySize);
		log.info("splitObject:" + split);
		
		
		String filepath = createLocalFile(file).replace('\\', '/').replaceFirst("C:", "c");
		
		String cmd = "-splits /mnt/" + filepath + " "+ size+"Kb";
		log.info("command   "+cmd);
		executeScript(cmd );
		
		return new ResponseEntity<String>("  Everything is working fine "+  this.StdOut, HttpStatus.OK);
	}

	/**
	 * @param file
	 * @param typeOfSort
	 * @param attribute
	 * @param keyattribute
	 * @param idattribute
	 * @return
	 */
	@PostMapping("/sortXml")
	public ResponseEntity<String> sortXml(@RequestParam("file") MultipartFile file,
			@RequestParam("sortType") String typeOfSort, @RequestParam("attribute") String attribute,
			@RequestParam("keyattribute") String keyattribute, @RequestParam("idattribute") String idattribute) {
		log.info("File name." + file.getOriginalFilename());
		return new ResponseEntity<String>("Everything is working fine for sort", HttpStatus.OK);
	}


	/**
	 * @param file
	 * @param fileType
	 * @return
	 */
	@PostMapping("/prettyPrintXml")
	public ResponseEntity<String> prettyPrintXml(@RequestParam("file") MultipartFile file){
				//to be included SGM file option as well
		log.info("File name." + file.getOriginalFilename());
		
		PrettyPrint print = new PrettyPrint(file.getOriginalFilename());
		log.info("Pretty Print:" + print);
		
		return new ResponseEntity<String>("Everything is working fine", HttpStatus.OK);
	}
	
	/**
	 * @param file
	 * @param file
	 * @param file
	 * @return
	 */
	@PostMapping("/convert")
	public ResponseEntity<String> convert(@RequestParam("file0")MultipartFile sgmlfile,
			@RequestParam("file1")MultipartFile catalogfile){
		
		log.info("Sgmlfile name." + sgmlfile.getOriginalFilename());
		log.info("catalogfile name." + catalogfile.getOriginalFilename());
		//to be used in future along with error file
		//log.info("Errorfile name." + errorfile.getOriginalFilename());
		
		Converter converter = new Converter(sgmlfile.getOriginalFilename(), catalogfile.getOriginalFilename());
		log.info("Converter : " +converter);
		
		return  new ResponseEntity<String>("Conversion is working fine", HttpStatus.OK);
	}
	
	
	/**
	 * @param files
	 * @return
	 */
	@PostMapping("/searching")
	public ResponseEntity<String> searching(@RequestParam("file") MultipartFile[] files,
			@RequestParam("searchId") String searchId, @RequestParam("extension") String extension,
			@RequestParam("text") String text){
		String dirPath = null;
		createLocalFolder();
		for(MultipartFile file:files) {
			dirPath = createLocalFile(file);
		}
		log.info("Dir name." + dirPath);
		log.info("searchId." + searchId);
		log.info("extension." + extension);
		log.info("text." + text);
		
		
		
		return new ResponseEntity<String>("Everything is working fine", HttpStatus.OK);
	}
	
	/**
	 * @param file
	 * @param typeOfSplit
	 * @param level
	 * @param size
	 * @param splitByElement
	 * @return
	 */
	@PostMapping("/feedback")
	public ResponseEntity<String> feedback(
			@RequestParam("feedbacktype") String feedbacktype, @RequestParam("desfeedback") String desfeedback,
			@RequestParam("name") String name, @RequestParam("email") String email, @RequestParam("projectname") String projectname) {
		log.info("feedbacktype " + feedbacktype);
		log.info("desfeedback " + desfeedback);
		log.info("name " + name);
		log.info("email " + email);
		return new ResponseEntity<String>("Feedback service is working fine", HttpStatus.OK);
	}
	
	/**
	 * 
	 */
	private void createLocalFolder() {
		boolean isExist = new File(context.getRealPath("/")).exists();
		if (!isExist) {
			new File(context.getRealPath("/webapp")).mkdir();
		}
	}
	
	/**
	 * @param file
	 * @return
	 */
	private String createLocalFile(MultipartFile file) {
		
		createLocalFolder();		
		
		String fileName = file.getOriginalFilename();
		String modifiedFileName = FilenameUtils.getBaseName(fileName) + "_" + System.currentTimeMillis() + "."
				+ FilenameUtils.getExtension(fileName);
		File serverFile = new File(context.getRealPath("/") + File.separator + modifiedFileName);

		try {
			FileUtils.writeByteArrayToFile(serverFile, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		//return serverFile.getParent();
		return serverFile.getAbsolutePath();
	}
	


}
