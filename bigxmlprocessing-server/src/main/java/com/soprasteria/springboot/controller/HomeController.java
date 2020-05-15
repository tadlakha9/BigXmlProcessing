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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;
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
	String message=null;
	Boolean searchFlag=false;
	
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

		log.info("File name." + file.getOriginalFilename());
		Split split = new Split(typeOfSplit, level, size, splitByElement, splitBySize, splitBySize, splitBySize,
				fileType);
		log.info("splitObject:" + split);
		log.info("catFile name." + catFile.getOriginalFilename());

		String filepath = createLocalFile(file).replace('\\', '/').replaceFirst("C:", "c");
		String catfilepath = createLocalFile(catFile).replace('\\', '/').replaceFirst("C:", "c");
		switch (typeOfSplit) {
		case "Level":
			if (fileType == "XML") {
				String cmd1 = "-splitl /mnt/" + filepath + " " + level;
				log.info("command   " + cmd1);
				executeScript(cmd1);
			} else {
				String cmd1 = "-splitl /mnt/" + filepath + " " + level + " " + catfilepath;
				log.info("command   " + cmd1);
				executeScript(cmd1);
			}

			/*
			 * log.info("command   " + cmd1); executeScript(cmd1);
			 */
			break;
		case "Size":
			if (fileType == "XML") {
				String cmd2 = "-splits /mnt/" + filepath + " " + size + "Kb";
				log.info("command   " + cmd2);
				executeScript(cmd2);
			} else {
				String cmd2 = "-splits /mnt/" + filepath + " " + size + "Kb" + " " + catfilepath;
				log.info("command   " + cmd2);
				executeScript(cmd2);
			}
			break;
		case "Element":
			if (fileType == "XML") {
				String cmd3 = "-splite /mnt/" + filepath + " " + splitByElement;
				log.info("command   " + cmd3);
				executeScript(cmd3);
			} else {
				String cmd3 = "-splite /mnt/" + filepath + " " + splitByElement + " " + catfilepath;
				log.info("command   " + cmd3);
				executeScript(cmd3);
			}
			break;
		case "Flat":
			switch (splitType) {

			case "line":
				String cmd4 = "-fsplitl /mnt/" + filepath + " " + splitByLine;
				log.info("command   " + cmd4);
				executeScript(cmd4);
				break;

			case "size":
				String cmd5 = "-fsplits /mnt/" + filepath + " " + splitBySize + "k";
				log.info("command   " + cmd5);
				executeScript(cmd5);
				break;

			}

			break;

		}

		return new ResponseEntity<String>("Split is working fine here" + this.StdOut, HttpStatus.OK);
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
		String filepath = createLocalFile(file).replace('\\', '/').replaceFirst("C:", "c");
		String cmd = "-sort /mnt/" + filepath;;
		log.info("command   "+cmd);
		executeScript(cmd);
		return new ResponseEntity<String>(file.getOriginalFilename()+" is sorted succesfully", HttpStatus.OK);
	}


	/**
	 * @param file
	 * @param fileType
	 * @return
	 */
	@PostMapping("/prettyPrintXml")
	public ResponseEntity<String> prettyPrintXml(@RequestParam("file") MultipartFile file) {
		// to be included SGM file option as well
		log.info("File name." + file.getOriginalFilename());

		PrettyPrint print = new PrettyPrint(file.getOriginalFilename());
		log.info("Pretty Print:" + print);
		String filepath = createLocalFile(file).replace('\\', '/').replaceFirst("C:", "c");

		String cmd = "-format /mnt/" + filepath;
		log.info("command   " + cmd);
		executeScript(cmd);
		return new ResponseEntity<String>("Format is working fine" + this.StdOut, HttpStatus.OK);

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

		log.info("Sgmlfile name." + sgmlfile.getOriginalFilename());
		log.info("catalogfile name." + catalogfile.getOriginalFilename());
		// to be used in future along with error file
		// log.info("Errorfile name." + errorfile.getOriginalFilename());

		Converter converter = new Converter(sgmlfile.getOriginalFilename(), catalogfile.getOriginalFilename());
		log.info("Converter : " + converter);

		String filepath = createLocalFile(sgmlfile).replace('\\', '/').replaceFirst("C:", "c");
		String catfilepath = createLocalFile(catalogfile).replace('\\', '/').replaceFirst("C:", "c");
		String cmd = "-sgx /mnt/" + filepath + " " + catfilepath;
		log.info("command   " + cmd);
		executeScript(cmd);
		return new ResponseEntity<String>("Conversion of sgml to xml is working fine" + this.StdOut, HttpStatus.OK);

	}
	
	/**
	 * @param files
	 * @return
	 */
	@PostMapping("/searching")
	public ResponseEntity<String> searching(@RequestParam("file") MultipartFile[] files,
			@RequestParam("searchId") String searchId, @RequestParam("extension") String extension,
			@RequestParam("text") String text) {
		String dirPath = null;
		searchFlag=true;
		String output = "Result.txt";
		createLocalFolder();
		for (MultipartFile file : files) {
			dirPath = createLocalFile(file).replace('\\', '/').replaceFirst("C:", "c");
		}

		File filenew = new File(dirPath);
		dirPath = filenew.getParent();
		dirPath = dirPath.replace("\\", "/");
		System.out.println("searchID	:" + searchId);
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
		//clear searchFlag
		searchFlag=true;

		return new ResponseEntity<String>("Search is successful", HttpStatus.OK);
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

		if (!searchFlag)
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