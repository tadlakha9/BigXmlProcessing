/**
 * 
 */
package com.soprasteria.springboot.controller;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
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
	public void transformXml(@RequestParam("file") MultipartFile file,@RequestParam("xsdFile") MultipartFile xsdFile) {	

		createLocalFolder();
		String filepath = createLocalFile(file);
		String extfilepath = createLocalFile(xsdFile);
		validate(filepath,extfilepath);
	}
	
	
	//validate XML with external xsd
	private ResponseEntity<String> validate(String xmlFile, String schemaFile) {
		try {
            SchemaFactory factory = 
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(schemaFile));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlFile)));
            System.out.println("File parsed successfully");
            return new ResponseEntity<String>("File parsed successfully "+  this.StdOut, HttpStatus.OK);
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            System.out.println("Error in file parsing");
            return new ResponseEntity<String>("  Error "+  this.StdErr, HttpStatus.NOT_FOUND);   
        }
      
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
		String localScriptPath = localScript.getAbsolutePath().replace("\\", "/").replaceFirst("C:", "/mnt/c");

		try {

			cmd = "bash " + localScriptPath + " " + command;
			// cmd = "bash /home/user/test.sh -help";
			// cmd = "bash /home/mjindal/FileFormatter.ksh -splits
			// /mnt/c/Users/mjindal.EMEAAD/Documents/G101_MNT_L_0001_0001_AMM_AIRCRAFT.XML
			// 100Kb";
			exec = new ExecProcess(cmd);
			exec.run();
			this.StdOut = exec.getStdout();
			this.SttdCode = exec.getReturnValue();

		} catch (Exception e) {
			this.StdErr = exec.getStderr();

			e.printStackTrace();

		}
	}

	/**
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
		String filepath = createLocalFile(file).replace('\\', '/').replaceFirst("C:", "c");
		String catfilepath = createLocalFile(catFile).replace('\\', '/').replaceFirst("C:", "c");

		// commands for different operations
		String cmd = "";
		switch (typeOfSplit) {
		case "Level":
			if (fileType.equalsIgnoreCase("XML")) {
				cmd = "-splitl /mnt/" + filepath + " " + level;
			} else {
				cmd = "-splitl /mnt/" + filepath + " " + level + " " + catfilepath;
			}

			break;
		case "Size":
			if (fileType.equalsIgnoreCase("XML")) {
				cmd = "-splits /mnt/" + filepath + " " + size + "Kb";

			} else {
				cmd = "-splits /mnt/" + filepath + " " + size + "Kb" + " " + catfilepath;

			}
			break;
		case "Element":
			if (fileType.equalsIgnoreCase("XML")) {
				cmd = "-splite /mnt/" + filepath + " " + splitByElement;

			} else {
				cmd = "-splite /mnt/" + filepath + " " + splitByElement + " " + catfilepath;

			}
			break;

		case "Flat":
			switch (splitType) {

			case "line":
				cmd = "-fsplitl /mnt/" + filepath + " " + splitByLine;
				break;

			case "size":
				cmd = "-fsplits /mnt/" + filepath + " " + splitBySize + "k";
				break;

			}

			break;

		}

		// executing the script
		log.info("command   " + cmd);
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
		String filepath = createLocalFile(file).replace('\\', '/').replaceFirst("C:", "c");

		// executing the script
		String cmd = "-sort /mnt/" + filepath;
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
		log.info("Pretty Print:" + print);
		String filepath = createLocalFile(file).replace('\\', '/').replaceFirst("C:", "c");

		// execute the script
		String cmd = "-format /mnt/" + filepath;
		log.info("command   " + cmd);
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
		String filepath = createLocalFile(sgmlfile).replace('\\', '/').replaceFirst("C:", "c");
		String catfilepath = createLocalFile(catalogfile).replace('\\', '/').replaceFirst("C:", "c");

		// executing the script
		String cmd = "-sgx /mnt/" + filepath + " " + catfilepath;
		log.info("command   " + cmd);
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
			dirPath = createLocalFile(file).replace('\\', '/').replaceFirst("C:", "c");
		}

		File filenew = new File(dirPath);
		dirPath = filenew.getParent();
		dirPath = dirPath.replace("\\", "/");
		System.out.println("searchID	:" + searchId);

		// executing the script
		if (searchId.equalsIgnoreCase("Text")) {
			if (text != null) {
				String cmd = "-searchp /mnt/" + dirPath + " " + text + " " + output;
				log.info("command   " + cmd);
				executeScript(cmd);
			}
		} else {
			if (extension != null) {
				String cmd = "-searcht /mnt/" + dirPath + " " + extension + " " + output;

				log.info("command   " + cmd);
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
	 * 
	 */
	private void createLocalFolder() {
		boolean isExist = new File("C:\\Temp\\MultiProcessor\\Target").exists();
		if (!isExist) {
			new File(context.getRealPath("/webapp")).mkdir();
		}
	}
	
	/**
	 * @param file
	 * @return
	 */
	private String createLocalFile(MultipartFile file) {

		// if (!searchFlag)
		createLocalFolder();

		String fileName = file.getOriginalFilename();
		String modifiedFileName = FilenameUtils.getBaseName(fileName) + "."
				+ FilenameUtils.getExtension(fileName).toUpperCase();
		File serverFile = new File("C:\\Temp\\MultiProcessor\\Target" + File.separator + modifiedFileName);

		try {
			FileUtils.writeByteArrayToFile(serverFile, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return serverFile.getParent();
		return serverFile.getAbsolutePath();
	}
	
}