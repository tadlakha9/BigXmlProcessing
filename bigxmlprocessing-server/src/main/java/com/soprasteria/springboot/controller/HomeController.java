/**
 * 
 */
package com.soprasteria.springboot.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Response;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.glassfish.jersey.message.internal.MsgTraceEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.xml.sax.SAXException;
import com.soprasteria.springboot.model.Converter;
import com.soprasteria.springboot.model.PrettyPrint;
import com.soprasteria.springboot.model.Split;
import com.soprasteria.springboot.utils.ExecProcess;
import com.soprasteria.springboot.utils.SendEmailSSL;


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
	String message=null;
	boolean searchFlag=false;
	String initialFilePath="C:\\Temp\\MultiProcessor\\Target";
	
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
	 * @param file XML File
	 * @param xsdFile Schema for the validation
	 * @return response 
	 * @throws Exception if any error in parsing or file not found
	 */
	@PostMapping("/parseXml")
	public Response transformXml(@RequestParam("file") MultipartFile file, @RequestParam("fileType") String fileType,
			@RequestParam("fileXsd") MultipartFile xsdFile, @RequestParam("fileerror") MultipartFile fileError,
			@RequestParam("filecatalog") MultipartFile filecatalog) throws IOException {
		
		log.info("File name." + file.getOriginalFilename());
		log.info(" fileType.  " + fileType);
		log.info("xsdFile name. " + xsdFile.getOriginalFilename());
		log.info("fileError name. " + fileError.getOriginalFilename());
		log.info("filecatalog  ." + filecatalog.getOriginalFilename());
		
		createLocalFile(file);
	
		/*
		 * AJH code 
		 * String filepath = createLocalFile(file); 
		 * String schemaFilePath = createLocalFile(xsdFile);
		 * ResponseEntity<String> response =validate(filepath, schemaFilePath);
		 * return response;
		 */
		return Response.ok().build();
	}
	
		
	/**
	 * Method to validate XML file with external XSD
	 * @param xmlFile
	 * @param schemaFile
	 * @return response
	 * @throws Exception if any error in parsing
	 */
	private ResponseEntity<String> validate(String xmlFile, String schemaFile) throws Exception {
		ResponseEntity<String> response = null;
		try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(schemaFile));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlFile)));
            System.out.println("File parsed successfully");
            response = new ResponseEntity<String>("File parsed successfully", HttpStatus.OK);
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getLocalizedMessage());
            System.out.println("Error in file parsing");
            response = new ResponseEntity<String>("Error" + e.getLocalizedMessage(), HttpStatus.NOT_FOUND);
            throw new Exception(e); 
        }
		return response;
    }

	/**
	 * 
	 */
	@GetMapping("/script")
	public void executeScript(String command) {
		log.info("inside executeScript method");
		ExecProcess exec = null;
		String cmd = "";
		File localScript = new File("src//main//resources//FileFormatter.ksh");
		String localScriptPath = "'"+convertToScriptPath(localScript.getAbsolutePath()).toString()+ "'";

		try {

			cmd = "bash " + localScriptPath + " " + command;
			exec = new ExecProcess(cmd);
			exec.run();
			this.StdOut = exec.getStdout();
			this.SttdCode = exec.getReturnValue();
			this.StdErr = exec.getStderr();
			
		} catch (Exception e) {
			this.StdErr = exec.getStderr();

			e.printStackTrace();
			//to add throw and locla msg
		}
	}

	/**
	 * Method for Split functionality
	 * @param file
	 * @param      typeOfSplit(Level, Element,Flat,Size)
	 * @param      level(split:Level)
	 * @param      size(split:Size)
	 * @param      splitByElement(split:Element)
	 * @param      splitType(split:Flat(By line/Size))
	 * @param      splitByLine(split:Flat)
	 * @param      splitBySize(split:Flat)
	 * @param      fileType(SGML/XML)
	 * @param      catFile
	 * @return
	 */
	@PostMapping("/splitXml")
	public ResponseEntity<String> splitXml(@RequestParam("file") MultipartFile file,
			@RequestParam("typeOfSplit") String typeOfSplit, @RequestParam("level") String level,
			@RequestParam("size") String size, @RequestParam("splitByElement") String splitByElement,
			@RequestParam("splitType") String splitType, @RequestParam("splitByLine") String splitByLine,
			@RequestParam("splitBySize") String splitBySize, @RequestParam("fileType") String fileType,
			@RequestParam("filecat") MultipartFile catFile) {

		long startTime = System.currentTimeMillis();

		log.info("File name." + file.getOriginalFilename());
		Split split = new Split(typeOfSplit, level, size, splitByElement, splitBySize, splitBySize, splitBySize,
				fileType);
		log.info("splitObject:" + split);
		log.info("catFile name." + catFile.getOriginalFilename());

		// getting the file path and catalogue file path
		String filepath = convertToScriptPath(createLocalFile(file)).toString();
		String catfilepath = convertToScriptPath(createLocalFile(catFile)).toString();

		// commands for different operations
		String cmd = "";
		switch (typeOfSplit) {
		case "Level":
			if (fileType.equalsIgnoreCase("XML")) {
				cmd = "-splitl " + filepath + " " + level;
			} else {
				cmd = "-splitl " + filepath + " " + level + " " + catfilepath;
			}
			break;
		case "Size":
			if (fileType.equalsIgnoreCase("XML")) {
				cmd = "-splits " + filepath + " " + size + "Kb";
			} else {
				cmd = "-splits " + filepath + " " + size + "Kb" + " " + catfilepath;
			}
			break;
		case "Element":
			if (fileType.equalsIgnoreCase("XML")) {
				cmd = "-splite " + filepath + " " + splitByElement;
			} else {
				cmd = "-splite " + filepath + " " + splitByElement + " " + catfilepath;
			}
			break;

		case "Flat":
			switch (splitType) {
			case "line":
				cmd = "-fsplitl " + filepath + " " + splitByLine;
				break;

			case "size":
				cmd = "-fsplits " + filepath + " " + splitBySize + "k";
				break;
			}
			break;
		}

		// executing the script
		log.info("command :  " + cmd);
		executeScript(cmd);

		// calculate execution time
		String executionTime = calculateTime(startTime);

		// sending the response
		ResponseEntity<String> statusInfo = null;
		if (this.SttdCode != 0) {
			String message = "There is some error in splitting:" + this.StdErr;
			statusInfo = alert(message, true);
		} else {
			String message = "File splitted Succesfully!!!" + "\n" + this.StdOut + "\n" + executionTime;
			statusInfo = alert(message, false);
		}
		return statusInfo;
	}
	

	/**
	 * Method for Sorting functionality
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
		
		long startTime = System.currentTimeMillis();

		// calculating the file path
		log.info("File name." + file.getOriginalFilename());
		String filepath = convertToScriptPath(createLocalFile(file)).toString();

		// executing the script
		String cmd = "-sort " + filepath;
		;
		log.info("command   " + cmd);
		executeScript(cmd);

		// calculate execution time
		String executionTime = calculateTime(startTime);

		// sending the response
		ResponseEntity<String> statusInfo = null;
		if (this.SttdCode != 0) {

			String message = "There is some error in the sorting:" + this.StdErr;
			statusInfo = alert(message, true);
		} else {
			String message = "File sorted Succesfully!!!" + "\n" + this.StdOut + "\n" + executionTime;
			statusInfo = alert(message, false);

		}

		return statusInfo;

	}


	/**
	 * Method for Pretty Print functionality
	 * @param file
	 * @param fileType
	 * @return
	 */
	@PostMapping("/prettyPrintXml")
	public ResponseEntity<String> prettyPrintXml(@RequestParam("file") MultipartFile file) {
		
		long startTime = System.currentTimeMillis();
		// to be included SGM file option as well
		log.info("File name." + file.getOriginalFilename());

		// create a local file
		PrettyPrint print = new PrettyPrint(file.getOriginalFilename());
		log.info("Pretty Print fields:" + print);
		String filepath = convertToScriptPath(createLocalFile(file)).toString();

		// execute the script
		String cmd = "-format " + filepath;
		log.info("command executing  :  " + cmd);
		executeScript(cmd);

		// calculate execution time
		String executionTime = calculateTime(startTime);

		
		  // sending the response
		ResponseEntity<String> statusInfo = null; 
		if (this.SttdCode != 0) {
			String message = "There is some error in the formatting:" + this.StdErr;
			statusInfo = alert(message, true);
		} else {
			String message = "File formatted Succesfully!!!" + "\n" + this.StdOut + "\n" + executionTime;
			statusInfo = alert(message, false);
		}

		return statusInfo;
		
	}
	

	/**
	 * Method for Convert functionality
	 * @param file
	 * @param file
	 * @param file
	 * @return
	 */
	@PostMapping("/convert")
	public ResponseEntity<String> convert(@RequestParam("file0") MultipartFile sgmlfile,
			@RequestParam("file1") MultipartFile catalogfile) {
		
		long startTime = System.currentTimeMillis();

		log.info("Sgmlfile name." + sgmlfile.getOriginalFilename());
		log.info("catalogfile name." + catalogfile.getOriginalFilename());
		// to be used in future along with error file
		// log.info("Errorfile name." + errorfile.getOriginalFilename());

		Converter converter = new Converter(sgmlfile.getOriginalFilename(), catalogfile.getOriginalFilename());
		log.info("Converter : " + converter);

		// calculating the path of file and catalogue file
		String filepath = convertToScriptPath(createLocalFile(sgmlfile)).toString();
		String catfilepath = convertToScriptPath(createLocalFile(catalogfile)).toString();

		// executing the script
		String cmd = "-sgx " + filepath + " " + catfilepath;
		log.info("command  : " + cmd);
		executeScript(cmd);

		// calculate execution time
		String executionTime = calculateTime(startTime);

		// sending the response
		ResponseEntity<String> statusInfo = null;
		if (this.SttdCode != 0) {
			String message = "There is some error in the conversion:" + this.StdErr;
			statusInfo = alert(message, true);
		} else {
			String message = "File converted Succesfully!!!" + "\n" + this.StdOut + "\n" + executionTime;
			statusInfo = alert(message, false);
		}
		return statusInfo;
	}
	
	/**
	 * Method for Searching functionality
	 * @param files
	 * @return
	 */
	@PostMapping("/searching")
	public ResponseEntity<String> searching(@RequestParam("file") MultipartFile[] files,
			@RequestParam("searchId") String searchId, @RequestParam("extension") String extension,
			@RequestParam("text") String text) {
		long startTime = System.currentTimeMillis();

		String dirPath = null;
		searchFlag = true;
		String output = "Result.txt";
		createLocalFolder();
		for (MultipartFile file : files) {
			dirPath = convertToScriptPath(createLocalFile(file)).toString();
		}

		File filenew = new File(dirPath);
		dirPath = filenew.getParent();
		dirPath = dirPath.replace("\\", "/");
		System.out.println("searchID : " + searchId);

		// executing the script
		if (searchId.equalsIgnoreCase("Text")) {
			if (text != null) {
				String cmd = "-searchp " + dirPath + " " + text + " " + output;
				log.info("command :  " + cmd);
				executeScript(cmd);
			}
		} else {
			if (extension != null) {
				String cmd = "-searcht " + dirPath + " " + extension + " " + output;
				log.info("command  : " + cmd);
				executeScript(cmd);
			}
		}

		log.info("Dir name." + dirPath);
		log.info("searchId." + searchId);
		log.info("extension." + extension);
		log.info("text." + text);

		// clear searchFlag
		searchFlag = false;

		// calculate execution time
		String executionTime = calculateTime(startTime);

		// sending the response
		ResponseEntity<String> statusInfo = null;
		if (this.SttdCode != 0) {

			String message = "There is some error searching:" + this.StdErr;
			statusInfo = alert(message, true);
		} else {
			String message = "Searched Succesfully!!!" + "\n" + this.StdOut + "\n" + executionTime;
			statusInfo = alert(message, false);
		}

		return statusInfo;

	}
	
	/**
	 * Method for Feedback form
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
          SendEmailSSL sslemail=new SendEmailSSL();
          String subject = feedbacktype+" from "+name+" with email "+email+" from project "+projectname;
          sslemail.sendemail(subject, desfeedback);
          
          log.info("feedbacktype " + feedbacktype);
          log.info("desfeedback " + desfeedback);
          log.info("name " + name);
          log.info("email " + email);
          return new ResponseEntity<String>("Feedback service is working fine", HttpStatus.OK);
    }

	
	/**
	 * Method for error handling
	 * 
	 * @param message
	 * @param flag   
	 * @return
	 */
	public ResponseEntity<String> alert(String statusInfo, boolean isError) {

		if (isError) {
			return new ResponseEntity<String>(statusInfo, HttpStatus.PRECONDITION_FAILED);
		} else {
			return new ResponseEntity<String>(statusInfo, HttpStatus.OK);
		}
	}
	
	/**
	 * Method for calculating the execution time of method
	 * 
	 * @param startTime
	 * @return
	 * 
	 */
	private String calculateTime(long startTime) {
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		String time = "Total Execution Time: " + elapsedTime + "ms";
		if (elapsedTime >= 1000) {
			elapsedTime = elapsedTime / 1000;
			time = "Total Execution Time: " + elapsedTime + "seconds";
		}

		return time;
	}
	
	/**
	 * local folder creation
	 */
	private void createLocalFolder() {
		boolean isExist = new File(initialFilePath).exists();
		if (!isExist) {
			new File(context.getRealPath("/webapp")).mkdir();
		}
	}
	
	/**
	 * local file creation
	 * @param file
	 * @return
	 */
	private String createLocalFile(MultipartFile file) {

		// if (!searchFlag)
		createLocalFolder();

		String fileName = file.getOriginalFilename();
		String modifiedFileName = FilenameUtils.getBaseName(fileName) + "."
				+ FilenameUtils.getExtension(fileName).toUpperCase();
		File serverFile = new File(initialFilePath + File.separator + modifiedFileName);

		try {
			FileUtils.writeByteArrayToFile(serverFile, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return serverFile.getParent();
		return serverFile.getAbsolutePath();
	}
	
	
	/**
     * Method to convert window path to Linux Path
     * @param path original Path
     * @return Processed Path
     * @throws IllegalArgumentException if an invalid path is provided
     */
    private static StringBuilder convertToScriptPath(String path) {
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