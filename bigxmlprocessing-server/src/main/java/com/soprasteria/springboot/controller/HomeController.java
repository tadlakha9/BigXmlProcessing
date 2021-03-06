package com.soprasteria.springboot.controller;

import java.io.File;

import java.io.IOException;

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
import com.soprasteria.springboot.constants.PropertyConstants;
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
	int sttdCode = 0;
	
	/**
	 *StdOut is standard output after execution of command 
	 */
	String stdOut = null;
	
	/**
	 * StdErr is error returned after execution of command
	 */
	String stdErr = null;
	
	/**
	 * message for return response
	 */
	String message = null;
	
	/**
	 * Flag for search method
	 */
	Boolean searchFlag = false;
	
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
				+ MultiprocessorUtil.convertToScriptPath(localScript.getAbsolutePath(), bigXmlConfig.getApplicationRootPath())
				+ MultiProcessorConstants.INVERTED_COMMA;

		try {
			//command execution
			cmd = ScriptConstants.BASH + MultiProcessorConstants.SPACE + localScriptPath + MultiProcessorConstants.SPACE
					+ command;
			exec = new ExecProcess(cmd);
			exec.run();
			this.stdOut = exec.getStdout();
			this.sttdCode = exec.getReturnValue();
			this.stdErr = exec.getStderr();

		} catch (Exception e) {
			if (exec != null) {
				this.stdErr = exec.getStderr();
				this.stdOut = exec.getStdout();
			}
			
			throw new Exception(e.getMessage());
		}
	}

	/**
	 * Method for Split functionality
	 * 
	 * @param file XMLFile/SGML file for split
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
			@RequestParam("filecat") MultipartFile[]  catalogfolder) throws Exception {

		ResponseEntity<String> statusInfo = null;
		String errorDir = null;
		String rootPAth = bigXmlConfig.getApplicationRootPath();

		try {
			long startTime = System.currentTimeMillis();
			String catalogdir= null;
			log.info(Messages.FILE_NAME, file.getOriginalFilename());
			Split split = new Split(typeOfSplit, level, size, splitByElement, splitBySize, splitBySize, splitBySize,
					fileType);
			log.info("splitObject: {0}", split);
			

			// getting the file path and catalog file path
			String filepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(file), rootPAth);
			
			// commands for different operations
			String cmd = MultiProcessorConstants.EMPTY_STRING;

			if (fileType.equalsIgnoreCase(MultiProcessorConstants.SGML)) {
				errorDir = createLogDir();
				// conversion to Linux path
				for (MultipartFile catfile : catalogfolder) {
					catalogdir = MultiprocessorUtil.convertToScriptPath(createLocalFile(catfile), rootPAth);
					log.info("catalogdir: {0}", catalogdir);
				}

				// after uploading, now getting directory path
				File filenew = new File(catalogdir);
				catalogdir = filenew.getParent();
				catalogdir = catalogdir.replace(MultiProcessorConstants.BACKSLASH, MultiProcessorConstants.SLASH);
			}
			
			// switch case for different types of split
			switch (typeOfSplit) {
			case MultiProcessorConstants.OPTION_SPLIT_BY_LEVEL:
				if (fileType.equalsIgnoreCase(MultiProcessorConstants.XML)) {
					cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SPLIT_BY_LEVEL, filepath, level);
				} else {
					cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SPLIT_BY_LEVEL, filepath, level,
							catalogdir,errorDir);
				}
				break;
			case MultiProcessorConstants.OPTION_SPLIT_BY_SIZE:
				if (fileType.equalsIgnoreCase(MultiProcessorConstants.XML)) {
					cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SPLIT_BY_SIZE, filepath,
							size + ScriptConstants.SIZE_IN_KB);
				} else {
					cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SPLIT_BY_SIZE, filepath,
							size + ScriptConstants.SIZE_IN_KB, catalogdir, errorDir);
				}
				break;
			case MultiProcessorConstants.OPTION_SPLIT_BY_ELEMENT:
				if (fileType.equalsIgnoreCase(MultiProcessorConstants.XML)) {
					cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SPLIT_BY_ELEMENT, filepath,
							splitByElement);

				} else {
					cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SPLIT_BY_ELEMENT, filepath,
							splitByElement, catalogdir, errorDir);
				}
				break;

			case MultiProcessorConstants.OPTION_FLAT_SPLIT:
				switch (splitType) {
				case MultiProcessorConstants.OPTION_FLAT_SPLIT_BY_LINE:
					cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.FLAT_SPLIT_BY_LINE, filepath,
							splitByLine);
					break;

				case MultiProcessorConstants.OPTION_FLAT_SPLIT_BY_SIZE:
					cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.FLAT_SPLIT_BY_SIZE, filepath,
							splitBySize + ScriptConstants.SIZE_KB);
					break;
				}
				break;
			}

			// executing the script
			log.info(Messages.COMMAND, cmd);
			executeScript(cmd);

			// calculate execution time
			String executionTime = calculateTime(startTime);

			// sending the response
			if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_SPLIT + this.stdErr;
				statusInfo = alert(message, true);
			} else {
				message = Messages.SPLIT_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.stdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);
			}

		} catch (Exception e) {
			if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_SPLIT + this.stdErr;
				throw new Exception(message);
			} else
				throw new Exception(e.getLocalizedMessage());
		}
		return statusInfo;
	}
	

	/**
	 * Method for Sorting functionality
	 * @param file XMLFile to be formatted
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
			@RequestParam("keyattribute") String keyattribute, @RequestParam("idattribute") String idattribute)
			throws Exception {

		ResponseEntity<String> statusInfo = null;

		try {
			long startTime = System.currentTimeMillis();

			// calculating the file path
			log.info(Messages.FILE_NAME, file.getOriginalFilename());
			String filepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(file), bigXmlConfig.getApplicationRootPath());
			
			//create output directory
			String outputDir = createOutputDir();

			// executing the script
			String cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SORT, filepath,outputDir);

			log.info(Messages.COMMAND, cmd);
			executeScript(cmd);

			// calculate execution time
			String executionTime = calculateTime(startTime);

			// sending the response
			if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_SORT + this.stdErr;
				statusInfo = alert(message, true);
			} else {
				message = Messages.SORT_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.stdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);

			}
		} catch (Exception e) {
			
			if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_SORT + this.stdErr;
				throw new Exception(message);
			}else 
				throw new Exception(e.getLocalizedMessage());	
		}

		return statusInfo;

	}

	/**
	 * Method for Pretty Print functionality
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
			log.info(Messages.FILE_NAME, file.getOriginalFilename());

			// create a local file
			PrettyPrint print = new PrettyPrint(file.getOriginalFilename());
			log.info("Pretty Print fields: {0}", print);
			String filepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(file), bigXmlConfig.getApplicationRootPath());
			
			//create output directory
			String outputDir = createOutputDir();
			
			// execute the script
			String cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.FORMAT, filepath,outputDir);
			log.info("command executing: {0}", cmd);
			executeScript(cmd);

			// calculate execution time
			String executionTime = calculateTime(startTime);

			// sending the response
				if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_FORMATTING + this.stdErr;
				statusInfo = alert(message, true);
			} else {
				message = Messages.FORMAT_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.stdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);
			}
		} catch (Exception e) {
			
			if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_FORMATTING + this.stdErr;
				throw new Exception(message);
			}else 
				throw new Exception(e.getLocalizedMessage());	
		}

		return statusInfo;

	}

	/**
	 * To create an Output directory for generated files
	 * 
	 * @return outputDir 
	 * @throws IOException
	 */
	private String createOutputDir() throws IOException {
		String outputDir = bigXmlConfig.getOutputFolderPath();
		File dir = new File(outputDir);
		if (!dir.exists()) {
			dir.mkdir();
		}
		outputDir = MultiprocessorUtil.convertToScriptPath(outputDir, bigXmlConfig.getApplicationRootPath());

		return outputDir;
	}

	/**
	 * Method for log directory
	 * @return
	 * @throws IOException
	 */
	private String createLogDir() throws IOException {
		String errorDir = MultiprocessorUtil.getApplicationProperty(PropertyConstants.ERROR_PATH);
		File edir = new File(errorDir);
		if (!edir.exists()) {
			edir.mkdir();
		}
		errorDir = MultiprocessorUtil.convertToScriptPath(errorDir, bigXmlConfig.getApplicationRootPath());

		return errorDir;
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
			@RequestParam("file1") MultipartFile[]  catalogfolder) throws Exception {	
		
		ResponseEntity<String> statusInfo = null;
		try {
			long startTime = System.currentTimeMillis();
			String catalogdir= null;
			// calculating the path of file 
			String filepath = MultiprocessorUtil.convertToScriptPath(createLocalFile(sgmlfile), bigXmlConfig.getApplicationRootPath());
			
			//create output and log directory
			String outputDir = createOutputDir();
			String errorDir = createLogDir();
			
			Converter converter = new Converter(sgmlfile.getOriginalFilename(), errorDir);
			log.info("Converter: {0} ", converter);
			
			// conversion to Linux path
			for (MultipartFile file : catalogfolder) {
				catalogdir = MultiprocessorUtil.convertToScriptPath(createLocalFile(file), bigXmlConfig.getApplicationRootPath());
			}
			// after uploading, now getting directory path
			File filenew = new File(catalogdir);
			catalogdir = filenew.getParent();
			catalogdir = catalogdir.replace(MultiProcessorConstants.BACKSLASH, MultiProcessorConstants.SLASH);	
					
			// executing the script
			String cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SGML_TO_XML, filepath, catalogdir, outputDir, errorDir);
			log.info(Messages.COMMAND, cmd);
			executeScript(cmd);
			
			// calculate execution time
			String executionTime = calculateTime(startTime);

			// sending the response
			if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_CONVERSION + this.stdErr;
				statusInfo = alert(message, true);
			} else {
				message = Messages.CONVERSION_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.stdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);
			}
		} catch (Exception e) {
			
			if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_CONVERSION + this.stdErr;
				throw new Exception(message);
			}else 
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
			
			searchFlag=true;

			String dirPath = null;
			String output = MultiProcessorConstants.OUTPUT_FILE_NAME;
			
			//creating folder for search
			createLocalFolderForSearch();
			
			//deleting contents of search folder
			dirPath = bigXmlConfig.getSearchFolderPath();
			MultiprocessorUtil.deleteDirectory(dirPath);

			// conversion to Linux path
			for (MultipartFile file : files) {
				dirPath = MultiprocessorUtil.convertToScriptPath(createLocalFile(file), bigXmlConfig.getApplicationRootPath());
			}

			// getting directory path
			File filenew = new File(dirPath);
			dirPath = filenew.getParent();
			dirPath = dirPath.replace(MultiProcessorConstants.BACKSLASH, MultiProcessorConstants.SLASH);
			
			//create log directory for search result
			String logDir = createLogDir();

			// executing the script
			if (searchId.equalsIgnoreCase("Text")) {
				if (text != null) {
					String cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SEARCH_BY_PATTERN, dirPath,
							text, logDir,output);
					log.info(Messages.COMMAND, cmd);
					executeScript(cmd);
				}
			} else {
				if (extension != null) {
					String cmd = MultiprocessorUtil.getProcessorCommand(ScriptConstants.SEARCH_BY_EXTENSION, dirPath,
							extension, logDir,output);
					log.info(Messages.COMMAND, cmd);
					executeScript(cmd);
				}
			}

			log.info("Dir name: {0}", dirPath);
			log.info("searchId: {0]", searchId);
			log.info("extension: {0}", extension);
			log.info("text: {0}", text);

			// calculate execution time
			String executionTime = calculateTime(startTime);
			
			//clearing searchFlag
			searchFlag=false;

			// sending the response
			if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_SEARCH + this.stdErr;
				statusInfo = alert(message, true);
			} else {
				message = Messages.SEARCH_SUCCESSFUL + MultiProcessorConstants.NEWLINE + this.stdOut
						+ MultiProcessorConstants.NEWLINE + executionTime;
				statusInfo = alert(message, false);
			}
		} catch (Exception e) {
			
			if (this.sttdCode != 0) {
				message = Messages.ERROR_IN_SEARCH + this.stdErr;
				throw new Exception(message);
			}else 
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

		log.info("feedbacktype: {0}", feedbacktype);
		log.info("desfeedback: {0}", desfeedback);
		log.info("name: {0}", name);
		log.info("email: {0}", email);
		return new ResponseEntity<>(Messages.FEEDBACK_SUBMITTED_SUCCESSFULLY, HttpStatus.OK);
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
			return new ResponseEntity<>(statusInfo, HttpStatus.EXPECTATION_FAILED);
		} else {
			return new ResponseEntity<>(statusInfo, HttpStatus.OK);
		}
	}
	
	/**
	 * Method for calculating the execution time of method
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
	 * Method for creating a local folder for search
	 */
	private void createLocalFolderForSearch() {

		// boolean to check if folder exists
		boolean isExist = new File(bigXmlConfig.getSearchFolderPath()).exists();
		if (!isExist) {
			new File(context.getRealPath("/webapp")).mkdir();
		}
	}
	
	/**
	 * Method for local file creation
	 * @param file
	 * @return absolute path of server file
	 */
	private String createLocalFile(MultipartFile file) throws Exception {
		
		File serverFile=null;
		//create local folder
		if(!searchFlag) {
			createLocalFolder();
		}

		String fileName = file.getOriginalFilename();
		String modifiedFileName = FilenameUtils.getBaseName(fileName) + MultiProcessorConstants.DOT_CONST
				+ FilenameUtils.getExtension(fileName).toUpperCase();
		if (searchFlag) {
			serverFile = new File(bigXmlConfig.getSearchFolderPath() + File.separator + modifiedFileName);
		} else {
			serverFile = new File(bigXmlConfig.getUsersFolderPath() + File.separator + modifiedFileName);
		}

		//copy files and folder to Target folder
		try {
			FileUtils.writeByteArrayToFile(serverFile, file.getBytes());
		} catch (Exception e) {
			
			throw new Exception(e.getLocalizedMessage());
		}
		
		return serverFile.getAbsolutePath();
	}
	
		
}