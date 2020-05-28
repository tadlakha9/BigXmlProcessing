package com.soprasteria.springboot.controller;

import java.io.File;
import javax.servlet.ServletContext;
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

import com.soprasteria.springboot.config.BigXmlProcessingConfig;
import com.soprasteria.springboot.constants.Messages;
import com.soprasteria.springboot.constants.MultiProcessorConstants;
import com.soprasteria.springboot.constants.ScriptConstants;
import com.soprasteria.springboot.model.Converter;
import com.soprasteria.springboot.model.PrettyPrint;
import com.soprasteria.springboot.model.Split;
import com.soprasteria.springboot.utils.ExecProcess;
import com.soprasteria.springboot.utils.MultiprocessorUtil;
import com.soprasteria.springboot.utils.SendEmailSSL;


/**
 * @author tushar
 * Controller for all functionalities
 */
@RestController
@RequestMapping("/home")
public class HomeController {

	/**
	 * SttdCode is return value after execution of command
	 */
	int SttdCode = 0;
	
	/**
	 *StdOut is standard output after execution of command 
	 */
	String StdOut = null;
	
	/**
	 * StdErr is error returned after execution of command
	 */
	String StdErr = null;
	
	/**
	 * message for return response
	 */
	String message = null;
	
	/**
	 * constructor
	 */
	@Autowired
	ServletContext context;
	
	/**
	 * constructor
	 */
	@Autowired
	BigXmlProcessingConfig bigXmlConfig;

	/**
     * Logger
     */
	private final Logger log = LoggerFactory.getLogger(HomeController.class);

	/**
	 * @return redirects to index page
	 */
	@GetMapping
	public String home() {
		return bigXmlConfig.getIndexPage();
	}

	/**
	 * Method for execution of script
	 * @param cmd is command given to execute via formatter script
	 * @throws Exception
	 */
	@GetMapping("/script")
	public void executeScript(String command) throws Exception{
		log.info("inside executeScript method");
		ExecProcess exec = null;
		String cmd = MultiProcessorConstants.EMPTY_STRING;
		File localScript = new File(bigXmlConfig.getScriptFilePath());
		
		//getting file path for FileFormatter script
		String localScriptPath = MultiProcessorConstants.INVERTED_COMMA
				+ MultiprocessorUtil.convertToScriptPath(localScript.getAbsolutePath()).toString()
				+ MultiProcessorConstants.INVERTED_COMMA;

		try {
			//command execution
			cmd = ScriptConstants.BASH + MultiProcessorConstants.SPACE + localScriptPath + MultiProcessorConstants.SPACE
					+ command;
			exec = new ExecProcess(cmd);
			exec.run();
			this.StdOut = exec.getStdout();
			this.SttdCode = exec.getReturnValue();
			this.StdErr = exec.getStderr();

		} catch (Exception e) {
			this.StdErr = exec.getStderr();

			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}
	}

	/**
	 * Method for Split functionality
	 * 
	 * @param file
	 * @param typeOfSplit(Level,Element,Flat,Size)
	 * @param level(split:Level)
	 * @param size(split:Size)
	 * @param splitByElement(split:Element)
	 * @param splitType(split:Flat(By line/Size))
	 * @param splitByLine(split:Flat)
	 * @param splitBySize(split:Flat)
	 * @param fileType(SGML/XML)
	 * @param catFile catalog file for given SGML file
	 * @throws Exception
	 * @return statusInfo
	 */
	@PostMapping("/splitXml")
	public ResponseEntity<String> splitXml(@RequestParam("file") MultipartFile file,
			@RequestParam("typeOfSplit") String typeOfSplit, @RequestParam("level") String level,
			@RequestParam("size") String size, @RequestParam("splitByElement") String splitByElement,
			@RequestParam("splitType") String splitType, @RequestParam("splitByLine") String splitByLine,
			@RequestParam("splitBySize") String splitBySize, @RequestParam("fileType") String fileType,
			@RequestParam("filecat") MultipartFile catFile) throws Exception {
		
		ResponseEntity<String> statusInfo = null;

		try {
			long startTime = System.currentTimeMillis();

			log.info("File name." + file.getOriginalFilename());
			Split split = new Split(typeOfSplit, level, size, splitByElement, splitBySize, splitBySize, splitBySize,
					fileType);
			log.info("splitObject:" + split);
			log.info("catFile name." + catFile.getOriginalFilename());

			// getting the file path and catalogue file path
			String filepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(file)).toString();
			String catfilepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(catFile)).toString();

			// commands for different operations
			String cmd = MultiProcessorConstants.EMPTY_STRING;

			//switch case for different types of split
			switch (typeOfSplit) {
			case MultiProcessorConstants.OPTION_SPLIT_BY_LEVEL:
				if (fileType.equalsIgnoreCase(MultiProcessorConstants.XML)) {
					cmd = ScriptConstants.SPLIT_BY_LEVEL + MultiProcessorConstants.SPACE + filepath
							+ MultiProcessorConstants.SPACE + level;
				} else {
					cmd = ScriptConstants.SPLIT_BY_LEVEL + MultiProcessorConstants.SPACE + filepath
							+ MultiProcessorConstants.SPACE + level + MultiProcessorConstants.SPACE + catfilepath;
				}
				break;
			case MultiProcessorConstants.OPTION_SPLIT_BY_SIZE:
				if (fileType.equalsIgnoreCase(MultiProcessorConstants.XML)) {
					cmd = ScriptConstants.SPLIT_BY_SIZE + MultiProcessorConstants.SPACE + filepath
							+ MultiProcessorConstants.SPACE + size + ScriptConstants.SIZE_IN_KB;
				} else {
					cmd = ScriptConstants.SPLIT_BY_SIZE + MultiProcessorConstants.SPACE + filepath
							+ MultiProcessorConstants.SPACE + size + ScriptConstants.SIZE_IN_KB
							+ MultiProcessorConstants.SPACE + catfilepath;
				}
				break;
			case MultiProcessorConstants.OPTION_SPLIT_BY_ELEMENT:
				if (fileType.equalsIgnoreCase(MultiProcessorConstants.XML)) {
					cmd = ScriptConstants.SPLIT_BY_ELEMENT + MultiProcessorConstants.SPACE + filepath
							+ MultiProcessorConstants.SPACE + splitByElement;
				} else {
					cmd = ScriptConstants.SPLIT_BY_ELEMENT + MultiProcessorConstants.SPACE + filepath
							+ MultiProcessorConstants.SPACE + splitByElement + MultiProcessorConstants.SPACE + catfilepath;
				}
				break;

			case MultiProcessorConstants.OPTION_FLAT_SPLIT:
				switch (splitType) {
				case MultiProcessorConstants.OPTION_FLAT_SPLIT_BY_LINE:
					cmd = ScriptConstants.FLAT_SPLIT_BY_LINE + MultiProcessorConstants.SPACE + filepath
							+ MultiProcessorConstants.SPACE + splitByLine;
					break;

				case MultiProcessorConstants.OPTION_FLAT_SPLIT_BY_SIZE:
					cmd = ScriptConstants.FLAT_SPLIT_BY_SIZE + MultiProcessorConstants.SPACE + filepath
							+ MultiProcessorConstants.SPACE + splitBySize + ScriptConstants.SIZE_KB;
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
			if (this.SttdCode != 0) {
				String message = Messages.ERROR_IN_SPLIT + this.StdErr;
				statusInfo = alert(message, true);
			} else {
				String message = Messages.SPLIT_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.StdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);
			}

		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
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
	 * @throws Exception
	 * @return statusInfo
	 */
	@PostMapping("/sortXml")
	public ResponseEntity<String> sortXml(@RequestParam("file") MultipartFile file,
			@RequestParam("sortType") String typeOfSort, @RequestParam("attribute") String attribute,
			@RequestParam("keyattribute") String keyattribute, @RequestParam("idattribute") String idattribute) throws Exception {

		ResponseEntity<String> statusInfo = null;
		
		try {
			long startTime = System.currentTimeMillis();

			// calculating the file path
			log.info("File name." + file.getOriginalFilename());
			String filepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(file)).toString();

			// executing the script
			String cmd = ScriptConstants.SORT + MultiProcessorConstants.SPACE + filepath;
			
			log.info("command   " + cmd);
			executeScript(cmd);

			// calculate execution time
			String executionTime = calculateTime(startTime);

			//sending the response
			if (this.SttdCode != 0) {
				String message = Messages.ERROR_IN_SORT + this.StdErr;
				statusInfo = alert(message, true);
			} else {
				String message = Messages.SORT_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.StdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);

			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}

		return statusInfo;

	}

	/**
	 * Method for Pretty Print functionality
	 * 
	 * @param file XMLFile to be formatted
	 * @return statusInfo
	 * @throws Exception
	 */
	@PostMapping("/prettyPrintXml")
	public ResponseEntity<String> prettyPrintXml(@RequestParam("file") MultipartFile file) throws Exception {

		ResponseEntity<String> statusInfo = null;
		try {
			long startTime = System.currentTimeMillis();
			// to be included SGM file option as well
			log.info("File name." + file.getOriginalFilename());

			// create a local file
			PrettyPrint print = new PrettyPrint(file.getOriginalFilename());
			log.info("Pretty Print fields:" + print);
			String filepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(file)).toString();

			// execute the script
			String cmd = ScriptConstants.FORMAT + MultiProcessorConstants.SPACE + filepath;
			log.info("command executing  :  " + cmd);
			executeScript(cmd);

			// calculate execution time
			String executionTime = calculateTime(startTime);

			// sending the response
			if (this.SttdCode != 0) {
				String message = Messages.ERROR_IN_FORMATTING + this.StdErr;
				statusInfo = alert(message, true);
			} else {
				String message = Messages.FORMAT_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.StdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}

		return statusInfo;

	}
	

	/**
	 * Method for Convert functionality
	 * 
	 * @param sgmlfile
	 * @param catalogfile
	 * @return statusInfo
	 * @throws Exception
	 */
	@PostMapping("/convert")
	public ResponseEntity<String> convert(@RequestParam("file0") MultipartFile sgmlfile,
			@RequestParam("file1") MultipartFile catalogfile) throws Exception {

		ResponseEntity<String> statusInfo = null;
		try {
			long startTime = System.currentTimeMillis();

			log.info("Sgmlfile name." + sgmlfile.getOriginalFilename());
			log.info("catalogfile name." + catalogfile.getOriginalFilename());

			Converter converter = new Converter(sgmlfile.getOriginalFilename(), catalogfile.getOriginalFilename());
			log.info("Converter : " + converter);

			// calculating the path of file and catalog file
			String filepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(sgmlfile)).toString();
			String catfilepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(catalogfile)).toString();

			// executing the script
			String cmd = ScriptConstants.SGML_TO_XML + MultiProcessorConstants.SPACE + filepath
					+ MultiProcessorConstants.SPACE + catfilepath;
			log.info("command  : " + cmd);
			executeScript(cmd);

			// calculate execution time
			String executionTime = calculateTime(startTime);

			// sending the response
			if (this.SttdCode != 0) {
				String message = Messages.ERROR_IN_CONVERSION + this.StdErr;
				statusInfo = alert(message, true);
			} else {
				String message = Messages.CONVERSION_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.StdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}
		return statusInfo;
	}
	
	/**
	 * Method for Searching functionality
	 * 
	 * @param files
	 * @param searchId
	 * @param extension
	 * @param text
	 * @return statusInfo
	 * @throws Exception
	 */
	@PostMapping("/searching")
	public ResponseEntity<String> searching(@RequestParam("file") MultipartFile[] files,
			@RequestParam("searchId") String searchId, @RequestParam("extension") String extension,
			@RequestParam("text") String text) throws Exception {

		ResponseEntity<String> statusInfo = null;
		try {
			long startTime = System.currentTimeMillis();

			String dirPath = null;
			String output = MultiProcessorConstants.OUTPUT_FILE_NAME;
			createLocalFolder();
			
			//conversion to linux path
			for (MultipartFile file : files) {
				dirPath = MultiprocessorUtil.convertToScriptPath(createLocalFile(file)).toString();
			}

			// getting directory path
			File filenew = new File(dirPath);
			dirPath = filenew.getParent();
			dirPath = dirPath.replace("\\", "/");

			// executing the script
			if (searchId.equalsIgnoreCase("Text")) {
				if (text != null) {
					String cmd = ScriptConstants.SEARCH_BY_PATTERN + MultiProcessorConstants.SPACE + dirPath
							+ MultiProcessorConstants.SPACE + text + MultiProcessorConstants.SPACE + output;
					log.info("command :  " + cmd);
					executeScript(cmd);
				}
			} else {
				if (extension != null) {
					String cmd = ScriptConstants.SEARCH_BY_TEXT + MultiProcessorConstants.SPACE + dirPath
							+ MultiProcessorConstants.SPACE + extension + MultiProcessorConstants.SPACE + output;
					log.info("command  : " + cmd);
					executeScript(cmd);
				}
			}

			log.info("Dir name." + dirPath);
			log.info("searchId." + searchId);
			log.info("extension." + extension);
			log.info("text." + text);

			// calculate execution time
			String executionTime = calculateTime(startTime);

			// sending the response
			if (this.SttdCode != 0) {

				String message = Messages.ERROR_IN_SEARCH + this.StdErr;
				statusInfo = alert(message, true);
			} else {
				String message = Messages.SEARCH_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.StdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(e.getLocalizedMessage());
		}

		return statusInfo;

	}
	
	/**
	 * Method for Feedback form
	 * 
	 * @param feedbacktype
	 * @param desfeedback
	 * @param name
	 * @param email
	 * @param projectname
	 * @return ResponseEntity
	 */
	@PostMapping("/feedback")
	public ResponseEntity<String> feedback(@RequestParam("feedbacktype") String feedbacktype,
			@RequestParam("desfeedback") String desfeedback, @RequestParam("name") String name,
			@RequestParam("email") String email, @RequestParam("projectname") String projectname) {
		SendEmailSSL sslemail = new SendEmailSSL();
		// getting subject for mail
		String subject = feedbacktype + Messages.FEEDBACK_FROM + name + Messages.FEEDBACK_FROM_EMAIL_ID + email
				+ Messages.FEEDBACK_FROM_PROJECT + projectname;
		
		// sending email
		sslemail.sendemail(subject, desfeedback);

		log.info("feedbacktype " + feedbacktype);
		log.info("desfeedback " + desfeedback);
		log.info("name " + name);
		log.info("email " + email);
		return new ResponseEntity<String>(Messages.FEEDBACK_SUBMITTED_SUCCESSFULLY, HttpStatus.OK);
	}

	
	/**
	 * Method for alert
	 * @param statusInfo
	 * @param isError   
	 * @return ResponseEntity
	 */
	public ResponseEntity<String> alert(String statusInfo, boolean isError) {
		
		//sending response
		if (isError) {
			return new ResponseEntity<String>(statusInfo, HttpStatus.EXPECTATION_FAILED);
		} else {
			return new ResponseEntity<String>(statusInfo, HttpStatus.OK);
		}
	}
	
	/**
	 * Method for calculating the execution time of method
	 * 
	 * @param startTime
	 * @return time
	 * 
	 */
	private String calculateTime(long startTime) {
		long stopTime = System.currentTimeMillis();
		long elapsedTime = stopTime - startTime;
		
		//calculation of execution time
		String time = Messages.EXECUTION_TIME + elapsedTime + MultiProcessorConstants.MILLISECONDS;
		if (elapsedTime >= 1000) {
			elapsedTime = elapsedTime / 1000;
			time = Messages.EXECUTION_TIME + elapsedTime + MultiProcessorConstants.SECONDS;
		}

		return time;
	}
	
	/**
	 * Method for creating a local folder
	 */
	private void createLocalFolder() {
		
		//boolean to check if folder exists
		boolean isExist = new File(bigXmlConfig.getUsersFolderPath()).exists();
		if (!isExist) {
			new File(context.getRealPath("/webapp")).mkdir();
		}
	}
	
	/**
	 * Method for local file creation
	 * @param file
	 * @return absolute path of server file
	 */
	private String createLocalFile(MultipartFile file) {
		
		//create local folder
		createLocalFolder();

		String fileName = file.getOriginalFilename();
		String modifiedFileName = FilenameUtils.getBaseName(fileName) + MultiProcessorConstants.DOT_CONST
				+ FilenameUtils.getExtension(fileName).toUpperCase();
		File serverFile = new File(bigXmlConfig.getUsersFolderPath() + File.separator + modifiedFileName);
		
		//copy files and folder to Target folder
		try {
			FileUtils.writeByteArrayToFile(serverFile, file.getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return serverFile.getAbsolutePath();
	}
	
		
}