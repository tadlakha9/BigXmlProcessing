#!/bin/ksh

#===================================================================================================================
usage ()
{
  echo "usage :"
  echo "-----"
  echo "    FileFormatter.ksh [ -d ] [-format] [-splitl] [-splits] [-splite] [-sort] [-sgx]"
  echo "          [-d : Directory mode] (ROOT_DIR) (CATALOGUE_FILE)"
  echo "				ROOT_DIR: Absolute/Relative Path of root directory. It can have both XML/xml or SGML files"
  echo "				CATALOGUE_FILE: Current CATALOGUE_FILE. Used to treat SGML files in case of ATA CATALOGUE_FILEs. In case of no CATALOGUE_FILE, 
								name of the CATALOGUE File can be given without extension."
  echo "             ex : FileFormatter.ksh -d /home/td_b260_delivery/DATA_PA TSM"
  echo "			 ex : FileFormatter.ksh -d /home/td_b260_delivery/DATA_PA SAMPLE
						  Where SAMPLE is corresponding to SAMPLE.CAT"
  echo ""
  echo "          [-format : Foramt XML File] (INPUT_FILE) (XML_OUTPUT)"
  echo "					 INPUT_FILE: Absolute/Relative Path of BIG XML File"
  echo "					 XML_OUTPUT: Preferred Output XML File Name. This is optional."
  echo "             ex : FileFormatter.ksh -format /home/td_b260_delivery/DATA_PA/Sample.XML"
  echo "             ex : FileFormatter.ksh -format /home/td_b260_delivery/DATA_PA/Sample.XML Sample_Out.xml"
  echo ""
  echo "          [-splitl : Split BIG XML File by given LEVEL.] (INPUT_FILE) (LEVEL)"
  echo "					 INPUT_FILE: Absolute/Relative Path of BIG XML File"
  echo "					 LEVEL: Various LEVELS of XML Tree. Root element is at LEVEL0. This is optional. 
									Default LEVEL is 1"
  echo "							In case of SGML please provide all the three parameters.(SGML)"
  echo "								ex : FileFormatter.ksh -splitl /home/td_b260_delivery/DATA_PA/AMM.SGML 2 AMM"
  echo "             ex : FileFormatter.ksh -splitl /home/td_b260_delivery/DATA_PA/Sample.XML"
  echo "             ex : FileFormatter.ksh -splitl /home/td_b260_delivery/DATA_PA/Sample.XML 2"
  echo ""
  echo "          [-splits : Split BIG XML File by given Size] (INPUT_FILE) (CHUNK_SIZE)"
  echo "					 INPUT_FILE: Absolute/Relative Path of BIG XML File"
  echo "					 CHUNK_SIZE: Size of each Splitted XML File. This is Optional. Default CHUNK_SIZE is 1Mb"
  echo "             ex : FileFormatter.ksh -splits /home/td_b260_delivery/DATA_PA/Sample.XML"
  echo "             ex : FileFormatter.ksh -splits /home/td_b260_delivery/DATA_PA/Sample.XML 10Mb"
  echo ""
  echo "          [-fsplits : Flat Split BIG XML or BIG SGML File by given Size] (XMLOrSGML_FILE) (CHUNK_SIZE)"
  echo "					 XMLOrSGML_FILE: Absolute/Relative Path of BIG XML File or BIG SGML File"
  echo "					 CHUNK_SIZE: Size of each Splitted XML or SGML File. This is Optional. Default CHUNK_SIZE is 1Mb"
  echo "             ex : FileFormatter.ksh -fsplits /home/td_b260_delivery/DATA_PA/Sample.XML "
  echo "             ex : FileFormatter.ksh -fsplits /home/td_b260_delivery/DATA_PA/Sample.XML 10m"
  echo "             ex : FileFormatter.ksh -fsplits /home/td_b260_delivery/DATA_PA/Sample.SGML "
  echo "             ex : FileFormatter.ksh -fsplits /home/td_b260_delivery/DATA_PA/Sample.SGML 10m"
  echo ""
  echo "          [-fsplitl : Split File by given line of codes. Splitted files are present in same folder"
  echo "					 INPUT_FILE: Absolute/Relative Path of BIG XML File"
  echo "					 LINE_SIZE: Lines of each Splitted XML File. This is Optional. Default LINE_SIZE is 100"
  echo "             ex : FileFormatter.ksh -fsplitl /home/td_b260_delivery/DATA_PA/Sample.XML 1000"
  echo ""
  echo "          [-splite : Split BIG XML File by given ELEMENT (INPUT_FILE) (ELEMENT)"
  echo "					 INPUT_FILE: Absolute/Relative Path of BIG XML File"
  echo "					 ELEMENT: Name of the Element. XML File will be splitted into files with root element as provided 
									  ELEMENT"
  echo "             ex : FileFormatter.ksh -splite /home/td_b260_delivery/DATA_PA/Sample.XML CHAPTER"
  echo ""
  echo "          [-sort : SORT the XML File (INPUT_FILE) (XML_OUTPUT)" 
  echo "					 INPUT_FILE: Absolute/Relative Path of BIG XML File"
  echo "					 XML_OUTPUT: Preferred Output XML File Name. This is optional."
  echo "             ex : FileFormatter.ksh -sort /home/td_b260_delivery/DATA_PA/Sample.XML"
  echo "             ex : FileFormatter.ksh -sort /home/td_b260_delivery/DATA_PA/Sample.XML Sample_Out.xml"
  echo ""
  echo "          [-validatexml : validate the XML File (XML_FILE) (EXT_FILE) " 
  echo "					 XML_FILE: Absolute/Relative Path of XML File"
  echo "					 EXT_FILE: External dtd or schema. This is only required for external validation."
  echo "             ex : FileFormatter.ksh -validatexml /home/td_b260_delivery/DATA_PA/Sample.XML /home/td_b260_delivery/DATA_PA/Sample.dtd "
  echo "             ex : FileFormatter.ksh -validatexml /home/td_b260_delivery/DATA_PA/Sample.XML /home/td_b260_delivery/DATA_PA/Sample.xsd "
  echo ""
  echo "          [-validatesgml : Validate SGML based on catalogue file (SGML_FILE) CATALOGUE_FILE"
  echo "				SGML_FILE: Absolute/Relative Path of SGML File."
  echo "				CATALOGUE_FILE: Absolute/Relative Path of CATALOGUE File."
  echo "             ex : FileFormatter.ksh -validatesgml /home/td_b260_delivery/DATA_PA/TSM.SGM CATALOGUE_FILE"
  echo "			 ex : FileFormatter.ksh -validatesgml /home/td_b260_delivery/DATA_PA/SAMPLE.SGM CATALOG_FILE"
  echo ""
  echo "          [-sgx : Covert SGML into Well-Formatted XML file. (SGML_FILE) (CATALOGUE_FILE)"
  echo "				SGML_FILE: Absolute/Relative Path of SGML File."
  echo "				CATALOGUE_FILE: Current CATALOGUE_FILE. Used to treat SGML files in case of ATA CATALOGUE_FILEs. 
								In case of no CATALOGUE_FILE, name of the CATALOGUE File can be given without extension."
  echo "             ex : FileFormatter.ksh -sgx /mnt/c/FORMATTER/SGML/AMM.SGM /mnt/c/FORMATTER/sgmlentities/AMM.CAT "
  echo "			 ex : FileFormatter.ksh -sgx /home/td_b260_delivery/DATA_PA/SAMPLE.SGM /home/td_b260_delivery/sgmlentities/SAMPLE.CAT
						  Where SAMPLE is corresponding to SAMPLE.CAT"
  echo ""
  echo "          [-searcht : Find type of file in folder. (ROOT_DIR) (TYPE) (OUTPUTFILE)"
  echo "				ROOT_DIR: Absolute/Relative Path of the root directory"
  echo "				TYPE: Extension of the files to be searched."
  echo "             ex : FileFormatter.ksh -searcht /home/td_b260_delivery/DATA_PA/ XML OUTPUTFILE"
  echo ""
  echo "          [-searchp : Find content in   file. (ROOT_DIR) (CONTENT) (OUTPUTFILE)"
  echo "				ROOT_DIR: Absolute/Relative Path of the root directory"
  echo "				CONTENT: content of the files to be searched."
  echo "             ex : FileFormatter.ksh -searchp /home/td_b260_delivery/DATA_PA/ XML OUTPUTFILE "
  echo ""
  echo "		  [-opendir : To open a Directory in a FileBrowser on WindowSystems. (ROOT_DIR)"
  echo "				ROOT_DIR: Absolute/Relative Path of the root directory"
  echo "             ex : FileFormatter.ksh -opendir /home/td_b260_delivery/DATA_PA/ "
  echo ""
  echo "		  [-open : To open a URL in a WebBrowser on WindowSystems. (URL)"
  echo "				URL: A Valid URL of a website"
  echo "             ex : FileFormatter.ksh -open http://localhost:8090/ "
  echo ""
  echo "    Directory Mode : "
  echo " 	   1 : List Preparation (List of XML and SGML Files in provided Source Directory)"
  echo " 	   2 : Formatting (XML Files are formatted. And a TEMP file is created)"
  echo " 	   3 : Sorting (By default only TDVV XML File is sorted. Other XML Files are Skipped)"
  echo " 	   4 : Conversion (SGML Files are converted into XML Files and formatted.)"
}

# Method to initialize all the variables
Initialize() 
{
	echo "Initializing Parameters.................."
	
	# Various Modes available
	DIR_MODE=""
	XML_FORMAT_MODE=""
	SGML_CONV_XML_MODE=""
	SPLIT_MODE=""
	SORT_MODE=""
	SEARCH_MODE=""
	XML_VALIDATE_MODE=""
	DELETE_MODE=""
	OPEN_MODE=""
	
	
	# Other Provided Arguments
	ROOT_DIR=""
	INPUT_FILE=""
	SGML_FILE=""	
	XML_OUTPUT=""
	OUTPUT_FILE=""
	CHUNK_SIZE=""
	LEVEL=""
	ELEMENT=""
	FLAG_SGML=""
	FLAG_SORT=""
	TYPE=""
	EXT_FILE=""
	URL=""
	
	# Pattern of the files to be searched
	PATTERN_XML="*.xml"
	PATTERN_SGML="*.SGM"
	
	# Default Size of the Splitted files 
	DEFAULT_CHUNCK_SIZE="1Mb"
	DEFAULT_CHUNCK_SIZE_FLAT_SPLIT="1m"
	
	# Default line Size of the Splitted files flatsplit
	DEFAULT_LINE_SIZE="100"
	
	#Start Time of execution of the script
	start_time=`date +%s%N`
	
	echo "All Parameters have been initialized to their default values.........."
}

# Method to get Parameters: To make arguments dynamic
GetParameters()
{
	while [ "$1" != "" ] ; do
		case $1 in
			-d) 
				DIR_MODE="$1"
				ROOT_DIR="$2"
				CATALOGUE_FILE="$3"
				#initialize special parameter
				#initializeForSGML
				if [ $# != 3 ]; then
					usage
					exit 1
				fi
				shift 3
			;;
			-format)
				XML_FORMAT_MODE="$1"
				INPUT_FILE="$2"
				OUTPUT_FILE="${2##*/}"
				ROOT_DIR=$(dirname $2)
				OUTPUT_DIR="$3"
				if [ ! -z $3 ];then
					XML_OUTPUT=$OUTPUT_DIR/${OUTPUT_FILE}".formatted"
				fi
				
				if [ $# = 3 ]; then
					shift 3
				elif [ $# = 2 ]; then
					shift 2
				else 
					usage
					exit 1
				fi
			;;
			-splitl)
				SPLIT_MODE="$1"
				INPUT_FILE="$2"
				LEVEL="$3"
				CATALOGUE_FILE="$4"
				ERROR_DIR="$5"
				OUTPUT_FILE=$(echo "$INPUT_FILE" | cut -f 1 -d '.')				
				ROOT_DIR=$(dirname $2)
				
				XML_OUTPUT=$OUTPUT_FILE.XML
				
				if [ ! -z $4 ];then
					#initialize special parameter
					initializeForSGML $ERROR_DIR				
				fi
				
				if [ $# = 5 ]; then
				    echo "INFO: You have provided ${CATALOGUE_FILE} for SGML splitting"
					echo "INFO: You are using Splitting for SGML file"
					shift 5
				elif [ $# = 3 ]; then
					shift 3
				elif [ $# = 2 ]; then
					#If Level not provide then default value taken
					echo "INFO: You have not provide required Level. Default Level 1 will be used"
					echo "INFO: Level 0 is the root Level"
					LEVEL=1
					shift 2
				else 
					usage
					exit 1
				fi
			;;
			-splits)
				SPLIT_MODE="$1"
				INPUT_FILE="$2"
				CHUNK_SIZE="$3"
				CATALOGUE_FILE="$4"
				ERROR_DIR="$5"
				OUTPUT_FILE=$(echo "$INPUT_FILE" | cut -f 1 -d '.')				
				ROOT_DIR=$(dirname $2)
				
				XML_OUTPUT=$OUTPUT_FILE.XML
				
				if [ ! -z $4 ];then
					#initialize special parameter
					initializeForSGML $ERROR_DIR				
				fi
				
				if [ $# = 5 ]; then
				    echo "INFO: You have provided ${CATALOGUE_FILE} for SGML splitting"
					echo "INFO: You are using Splitting for SGML file"					
					shift 5
				elif [ $# = 3 ]; then
					shift 3
				elif [ $# = 2 ]; then
					echo "INFO: You have not provide required chunk size. Default chunk size of 1Mb will be used"
					CHUNK_SIZE=$DEFAULT_CHUNCK_SIZE
					shift 2
				else 
					usage
					exit 1
				fi
			;;
			-fsplitl)
				SPLIT_MODE="$1"
				INPUT_FILE="$2"
				LINE_SIZE="$3"
				ROOT_DIR=$(dirname $2)
				
				if [ $# = 3 ]; then
					shift 3
				elif [ $# = 2 ]; then
					echo "INFO: You have not provided required line size. Default line size of 100 will be used"
					LINE_SIZE=$DEFAULT_LINE_SIZE
					shift 2
				else 
					usage
					exit 1
				fi
			;;
			-fsplits)
				SPLIT_MODE="$1"
				INPUT_FILE="$2"
				CHUNK_SIZE="$3"
				ROOT_DIR=$(dirname $2)
				
				if [ $# = 3 ]; then
					shift 3
				elif [ $# = 2 ]; then
					echo "INFO: You have not provide required chunk size. Default chunk size of 1Mb will be used"
					CHUNK_SIZE=$DEFAULT_CHUNCK_SIZE_FLAT_SPLIT
					shift 2
				else 
					usage
					exit 1
				fi
			;;
			-splite)
				SPLIT_MODE="$1"
				INPUT_FILE="$2"
				ELEMENT="$3"
				CATALOGUE_FILE="$4"
				ERROR_DIR="$5"
				OUTPUT_FILE=$(echo "$INPUT_FILE" | cut -f 1 -d '.')				
				ROOT_DIR=$(dirname $2)
				
				XML_OUTPUT=$OUTPUT_FILE.XML
				
				if [ ! -z $4 ];then
					#initialize special parameter
					initializeForSGML $ERROR_DIR				
				fi
				
				if [ $# = 5 ]; then
				    echo "INFO: You have provided ${CATALOGUE_FILE} for SGML splitting"
					echo "INFO: You are using Splitting for SGML file"
					shift 5
				elif [ $# = 3 ]; then
					shift 3
				else 
					usage
					exit 1
				fi
			;;
			-sort)
				SORT_MODE="$1"
			    INPUT_FILE="$2"
				CATALOGUE_FILE="$4"
				XML_ROOT_DIR=$(dirname $2)				
				OUTPUT_FILE="${2##*/}"
				OUTPUT_DIR="$3"
				
				if [ ! -z $3 ];then
					XML_OUTPUT=$OUTPUT_DIR/${OUTPUT_FILE}".sorted"
				fi
				
				if [ ! -z $4 ];then
					echo "INFO: We are in initilaize SGML"
					#initialize special parameter
					initializeForSGML		
				fi
				
				if [ $# = 4 ]; then
				    echo "INFO: You have provided ${CATALOGUE_FILE} for SGML splitting"
					echo "INFO: You are using Splitting for SGML file"
					shift 4
			    elif [ $# = 3 ]; then
					shift 3
				elif [ $# = 2 ]; then
					shift 2
				else 
					usage
					exit 1
				fi
			;;
			-sgx)
				SGML_CONV_XML_MODE="$1"
				SGML_FILE="$2"
				OUTPUT_DIR="$4"
				ERROR_DIR="$5"
				FILE_NAME="${2##*/}"
				MANUAL="${FILE_NAME%.*}"
				CATALOGUE_FILE=$3/$MANUAL".CAT"
				TMP_FILE=$3/$MANUAL".TMP"
				#copy of catalog file as tmp in same folder
				cp $CATALOGUE_FILE $TMP_FILE
				ROOT_DIR=$(dirname $SGML_FILE)
				XML_OUTPUT=$OUTPUT_DIR/$MANUAL".XML"
				
				#initialize special parameter
				initializeForSGML $ERROR_DIR
				
				if [ $# = 5 ]; then
					shift 5
				else 
					usage
					exit 1
				fi
			;;
			-searcht)
				SEARCH_MODE="$1"
				ROOT_DIR="$2"
				TYPE="*.$3"
				OUTPUT_DIR="$4"
				if [ ! -z $4 ];then
					OUTPUT_FILE=$OUTPUT_DIR/Result.txt
				fi
				
				if [ $# = 3 ]; then
					shift 3
				elif [ $# = 4 ]; then
					shift 4
				else 
					usage
					exit 1
				fi
			;;
			-searchp)
				SEARCH_MODE="$1"
				ROOT_DIR="$2"
				PATTERN="$3"
				OUTPUT_DIR="$4"
				if [ ! -z $4 ];then
					OUTPUT_FILE=$OUTPUT_DIR/Result.txt
				fi
				
				if [ $# = 3 ]; then
					shift 3
				elif [ $# = 4 ]; then
					shift 4
				else 
					usage
					exit 1
				fi
			;;
			-deletedir)
				DELETE_MODE="$1"
				ROOT_DIR="$2"
				
			    if [ $# = 2 ]; then
					shift 2
				else 
					usage
					exit 1
				fi
			;;
			-validatexml)
				XML_VALIDATE_MODE="$1"
				XML_FILE="$2"
				echo "INFO: You have provided ${XML_FILE} for XML validation"
				XML_ROOT_DIR=$(dirname $2)
				if [ ! -z $3 ];then
					EXT_FILE="$3"
				echo "INFO : You have provided ${EXT_FILE} for external dtd/schema"
				fi
				if [ $# = 3 ]; then
					shift 3
				elif [ $# = 2  ]; then
					shift 2
				else 
					usage
					exit 1
				fi
			;;
			-validatesgml)
				SGML_CONV_XML_MODE="$1"
				SGML_FILE="$2"
				CATALOGUE_FILE=$3
				OUTPUT_FILE=$(echo "$SGML_FILE" | cut -f 1 -d '.')
				ROOT_DIR=$(dirname $SGML_FILE)
				XML_OUTPUT=$OUTPUT_FILE.XML
				
				#initialize special parameter
				initializeForSGML
				
				if [ $# = 3 ]; then
					shift 3
				else 
					usage
					exit 1
				fi
			;;
			-opendir)
				OPEN_MODE="$1"
				ROOT_DIR="$2"
				
				if [ $# = 2 ]; then
					shift 2
				else 
					usage
					exit 1
				fi
			;;
			-open)
				OPEN_MODE="$1"
				URL="$2"
				
				if [ $# = 2 ]; then
					shift 2
				else 
					usage
					exit 1
				fi
			;;
		esac
	done
}

#Method to initialize some basic parameters for SGML files only
initializeForSGML() {	
	# Create an Empty Error file. Used to hold erros raised during SGML formatting
	ERROR_FILE=$ERROR_DIR/PARSING.ERROR
	touch $ERROR_FILE
}

#Method to format all XML|SGML files in the given Directory
#$1 ==> Absolute Path of the Root Directory
formatAllFILES() {
echo "In Method: formatAllFILES()------------------------>"
	if [ -d $1 ];
	then
		echo "Directory Mode: ${ROOT_DIR}"
		cd $1
		XML_FILES_LIST=$(find $1 -type f -iname "$PATTERN_XML")
		
		SGML_FILES_LIST=$(find $1 -type f -iname "$PATTERN_SGML")
		
		#XML File Formatting
		if [ ! -z $XML_FILES_LIST ]; then
			echo "INFO: Processing Started for the follwing XML Files :"
			echo ${XML_FILES_LIST}
			for file in $XML_FILES_LIST
			do
				TDVV_FILE_PATTERN="1K11_.*_VVmessages\.XML"
				if [[ $file =~ $TDVV_FILE_PATTERN ]]; then
					sortFile $file
				else
					formatXML $file
				fi
			done
		else
			echo "WARNING: There is no XML file for Processing"
			exit 1
		fi
		
		#SGML File Formatting
		if [ ! -z $SGML_FILES_LIST ]; then
			echo "INFO: Processing Started for the follwing SGML Files :"
			echo ${SGML_FILES_LIST}
			for file in $SGML_FILES_LIST
			do
				convertSGMLToXML $file $CATALOGUE_FILE
			done
		else
			echo "WARNING: There is no SGML File For Processing"
			exit 1
		fi
	fi
echo "Out Method: formatAllFILES()---------------------->"
calculateTime $start_time	
}

#Method to Format XML file: 
#$1 ==> Path to XML File 
#$2 ==> Path to Output File
formatXML() {
echo "In Method: formatXML()---------------------------->"	
	if [ -f $1 ]; 
	then
		if [ -z $2 ]; then
			echo "INFO: You have not provided any Output File. Default temp file will be created"
			XML_FILE_TEMP=${1}".formatted"
			
		else
			XML_FILE_TEMP=$2
		fi
		xmllint --format --recover ${1} --output ${XML_FILE_TEMP}
		if [ $? -eq 0 ];
		then
			if [ -z $FLAG_SGML ]; 
			then
				echo "INFO: XML File ${1} formatted to ${XML_FILE_TEMP} Successfully."
				echo "WARNING: Some Special Characters may have been dropped as they are not supported by XML Files!!!"
			fi
		else
			echo "ERROR: There is some error in File Formatting Process."
			echo "ERROR: XML File can't be parsed properly"
			exit 2
		fi
	else 
	  	echo "ERROR: ${1} not found"
	  	exit 1
	fi
	
	# Redirect to Output Directory
	openDirectory $(dirname $XML_FILE_TEMP)
echo "Out Method: formatXML()---------------------------->"
calculateTime $start_time
}

#Method to Sort XML file: 
#$1 ==> Path to XML File 
#$2 ==> Path to Output File
sortFile() {
echo "In Method: sortFile()-------------------------------->"	
if [ -f $1 ]; 
 then
	
	echo "INFO: File to be sorted: ${1}"
	echo "INFO: Path to Output File: ${2}"
	echo "INFO: CATALOGUE_FILE of the SGML file: ${3}"
	
	#To get extension of file
	FLAG_SORT="sort"
	TYPE="${1##*.}"	
	echo "INFO: Extension of the file to be sorted: ${TYPE} "
    if [[ $TYPE = XML ]];
		then
		echo "INFO: We are in XML Sort"	
        if [ -z $2 ]; then
			echo "INFO: You have not provided any Output File." 
			echo "INFO: Default .sorted file will be created"
			XML_FILE_TEMP=${1}".sorted"
		else
			XML_FILE_TEMP=$2
		fi    
		xmllint --c14n ${1} > ${XML_FILE_TEMP}
		xmllint --format --recover ${XML_FILE_TEMP} --output ${XML_FILE_TEMP}		
		if [ $? -eq 0 ];
		then
			echo "INFO: XML File Sorted and formatted to ${XML_FILE_TEMP} Successfully."
		else
			echo "ERROR: There is some error in File Sorting Process." 
			echo "ERROR: XML File can't be parsed properly"
			exit 3
		fi
		
	elif [[ $TYPE = SGM ]];
	then
		echo "INFO: We are in SGML Sort"							
		convertSGMLToXML ${1} $CATALOGUE_FILE $FLAG_SORT
		echo "SGML converted to XML"	        		
		sortFile $XML_OUTPUT ${2}			
    fi	
	
else 
	 echo "ERROR: ${1} not found"
	exit 1
fi
# Redirect to Output Directory
openDirectory $(dirname $XML_FILE_TEMP)
echo "Out Method: sortFile()--------------------------------->"
calculateTime $start_time
}

#Method to Convert SGML into XML: 
#$1 ==> SGML File Absolute Path. 
#$2 ==> Catalogue file name
#$3 ==> Flag to check if it is case of SORTING
convertSGMLToXML() {
echo "In Method: convertSGMLToXML()"	
	if [ -f $1 ]; 
	then
		
		sed -i 's:./DTD/::g' $TMP_FILE
		# Converting SGML into XML		
		osx -e -g -wall -E0 -c $TMP_FILE -x no-nl-in-tag -x pi-escape -x empty -f $ERROR_FILE $1 > $XML_OUTPUT
		echo "INFO: ERROR_FILE: ${ERROR_FILE}"
		if [ $? -eq 0 ];
		then
			if [ ! -z $XML_OUTPUT ]; then		
				# To set flag if SGML formatting option has been taken
				FLAG_SGML=1
				# FORMAT Converted XML File
				formatXML $XML_OUTPUT $XML_OUTPUT
				echo "INFO: SGML File Converted to XML: ${XML_OUTPUT} Successfully."
				echo "INFO: By default, coverted XML File is well formatted."
				echo "WARNING: Some DOCTYPE and Entity declarations have been removed from the SGML file as not supported by XML Files!!!"
			fi			
		else
			echo "ERROR: There is some error in Coversion of SGML into XML." 
			echo "ERROR: Check ${ERROR_FILE} file for more details"
			exit 4
		fi
	else 
	  	echo "ERROR: ${1} not found"
	  	exit 1
	fi
	# Redirect to Output Directory
	openDirectory $(dirname $XML_OUTPUT)
echo "Out Method: convertSGMLToXML()"
calculateTime $start_time
}

#Method to Spilt Big XML or SGML file into smaller chunks of given size: 
#$1 ==> Absolute Path of File 
#$2 ==> Size required of each chunk
#$3 ==> CATALOGUE_FILE of the SGM file
splitBySize() {
echo "In Method: splitBySize()------------------------>" 
if [ -f $1 ]; 
then
    echo "INFO: File to be splitted: ${1}"
	echo "INFO: Size to be splitted: ${2}"
	echo "INFO: CATALOGUE_FILE of the SGML file: ${3}"
	
	#To get extension of file
	TYPE="${1##*.}"	
	echo "INFO: Extension of the file to be splitted: ${TYPE}"
	
    if [[ $TYPE = XML ]];
    then
		echo "INFO: We are in XML Split"
        xml_split -s ${2} ${1}
        if [ $? -eq 0 ];
        then
            echo "INFO: XML File Splitted Successfully." 
            echo "INFO: Splitted files are copied at ${ROOT_DIR}"
        else
            echo "ERROR: There is some error in File Splitting Process."
			exit 5
        fi
    elif [[ $TYPE = SGM ]];
	then
		echo "INFO: We are in SGML Split"							
		convertSGMLToXML ${1} $CATALOGUE_FILE
		echo "INFO: SGML converted to XML"
		splitBySize $XML_OUTPUT ${2} $CATALOGUE_FILE
    fi									
else 
    echo "ERROR: ${1} not found"
    exit 1
fi
# Redirect to Output Directory
openDirectory $(dirname ${1})
echo "Out Method: splitBySize()------------------------>"
calculateTime $start_time
}


#Method to Spilt Big XML or SGML file into smaller chunks of given size: 
#$1 ==> Absolute Path of Big XML or SGML File 
#$2 ==> Size required of each chunk
#$3 ==> CATALOGUE_FILE of the SGML file (Or Name of the Catalogue File)
flatSplitBySize() {
echo "In Method: flatSplitBySize()------------------------>"	
echo "WARNING: You have chosen Flat split by Size option." 
echo "WARNING: It doesn't guarantee that every splitted file will be of the given size"
	if [ -f $1 ]; 
	then
		split -b ${2} -d ${1} ${1}
		if [ $? -eq 0 ];
		then
			echo "INFO: File Splitted Successfully." 
			echo "INFO: Splitted files are copied at ${ROOT_DIR}"
		else
			echo "ERROR: There is some error in File Splitting Process."
			exit 6
		fi
	else 
	  	echo "ERROR: ${1} not found"
	  	exit 1
	fi
	# Redirect to Output Directory
    openDirectory $(dirname ${1})
echo "Out Method: flatSplitBySize()------------------------>"
calculateTime $start_time
}

#Method to Flat Spilt Big XML file into smaller chunks of given size: 
#$1 ==> Absolute Path of Big XML File 
#$2 ==> line of code for each file
flatsplitByLine() {
echo "In Method: flatsplitByLine()------------------------>"	
echo "WARNING: You have chosen Split by line option." 
	if [ -f $1 ]; 
	then
		split -l ${2} -d ${1} ${1}
		if [ $? -eq 0 ];
		then
			echo "INFO: XML File Splitted Successfully as per line of code." 
			echo "INFO: Splitted files are copied at ${XML_ROOT_DIR}"
		else
			echo "ERROR: There is some error in File Splitting Process."
			exit 7
		fi
	else 
	  	echo "ERROR: ${1} not found"
	  	exit 1
	fi
	# Redirect to Output Directory
    openDirectory $(dirname ${1})
echo "Out Method: flatsplitByLine()------------------------>"
calculateTime $start_time
}


#Method to Spilt Big XML file into smaller chunks for given level: 
#$1 ==> Absolute Path of Big XML File 
#$2 ==> level of the element. Root element is counted as level 0
#$3 ==> CATALOGUE_FILE of the SGML file (Or Name of the Catalogue File)
splitByLevel() {
echo "In Method: splitByLevel()------------------------>" 
if [ -f $1 ]; 
    then
    echo "INFO: File to be splitted: ${1}"
	echo "INFO: Level to be splitted: ${2}"
	echo "INFO: CATALOGUE_FILE of the SGML file: ${3}"
	
	#To get extension of file
	TYPE="${1##*.}"	
	echo "INFO: Extension of the file to be splitted: ${TYPE}"
	
    if [[ $TYPE = XML ]];
    then
		echo "INFO: We are in XML Split"
        xml_split -l ${2} ${1}
        if [ $? -eq 0 ];
        then
            echo "INFO: XML File Splitted Successfully." 
            echo "INFO: Splitted files are copied at ${ROOT_DIR}"
        else
            echo "ERROR: There is some error in File Splitting Process."
			exit 8
        fi
    elif [[ $TYPE = SGM ]];
	then
		echo "INFO: We are in SGML Split"							
		convertSGMLToXML ${1} $CATALOGUE_FILE
		echo "INFO: SGML converted to XML"		
		splitByLevel $XML_OUTPUT ${2}		
    fi									
else 
    echo "ERROR: ${1} not found"
    exit 1
fi
# Redirect to Output Directory
openDirectory $(dirname ${1})
echo "Out Method: splitByLevel()------------------------>"
calculateTime $start_time
}

#Method to Spilt Big XML or BIG SGM file into smaller chunks for given level: 
#$1 ==> Absolute Path of Big XML or BIG SGM File 
#$2 ==> Name of the element such as "CHAPTER".
#$3 ==> CATALOGUE_FILE of the SGML file (Or Name of the Catalogue File)
splitByElement() {
echo "In Method: splitByElement()------------------------>"
echo "WARNING: You have chosen option of splitting by element."
echo "WARNING: This Option is a lot slower than split by element (-splite)"	
if [ -f $1 ]; 
then
	echo "INFO: File to be splitted: ${1}"
	echo "INFO: Element to be splitted: ${2}"
	echo "INFO: CATALOGUE_FILE of the SGML file: ${3}"
	
	#To get extension of file
	TYPE="${1##*.}"	
	echo " Extension of the file to be splited: ${TYPE} "
	
	if [[ $TYPE = XML ]];
    then
		echo "INFO: We are in XML Split"
		xml_split -c ${2} ${1}
		if [ $? -eq 0 ];
		then
			echo "INFO: XML File Splitted Successfully." 
			echo "INFO: Splitted files are copied at ${ROOT_DIR}"
		else
			echo "ERROR: There is some error in File Splitting Process."
			exit 9
		fi
	elif [[ $TYPE = SGM ]];
	then
		echo "INFO: We are in SGML Split"							
		convertSGMLToXML ${1} $CATALOGUE_FILE 
		echo "INFO: SGML converted to XML"		
		splitByElement $XML_OUTPUT ${2}
    fi									
else 
	echo "ERROR: ${1} not found"
	exit 1
fi
# Redirect to Output Directory
openDirectory $(dirname ${1})
echo "Out Method: splitByElement()------------------------>"
calculateTime $start_time
}

#Method to Search all files of the given in the provided directory: 
#$1 ==> Absolute Path of the directory 
#$2 ==> Type of the file (Extension of the file).
searchFilesByType() {
echo "In Method: searchFilesByType()------------------------>"
echo "INFO: You have chosen option of searching by type of the file"
	if [ -d $ROOT_DIR ]; 
	then
		FILES_LIST=$(find $ROOT_DIR -type f -iname "$TYPE")
		if [[ ! -z $FILES_LIST ]];
		then
			echo ${FILES_LIST} | sed -e 's|/mnt/|\n\n|g' > $OUTPUT_FILE
			
			echo "INFO: File of ${TYPE} Searched Successfully in the directory ${ROOT_DIR}" 
			echo "INFO: Result has been saved in file ${OUTPUT_FILE}."
		else
			echo "WARNING: There is no such file of ${TYPE} exists in directory ${ROOT_DIR}."
			exit 10
		fi
	else 
	  	echo "ERROR: Directory ${ROOT_DIR} not found"
	  	exit 1
	fi
# Redirect to Output Directory
openDirectory $(dirname $OUTPUT_FILE)
echo "Out Method: searchFilesByType()"
calculateTime $start_time
}

#Method to Search all files of the given in the provided directory with the given content: 
#$1 ==> Absolute Path of the directory 
#$2 ==> content of the file.
searchFilesByPattern() {
echo "In Method: searchFilesByPattern()------------------------>"
echo "INFO: You have chosen option of searching by pattern of the content in file"
	if [ -d $ROOT_DIR ]; 
	then
		FILES_LIST=$(find ${ROOT_DIR} -type f | grep -H -r ${PATTERN} ${ROOT_DIR})
		
	    if [[ ! -z $FILES_LIST ]];
		then
			echo ${FILES_LIST} | sed -e 's|/mnt/|\n\n|g' > $OUTPUT_FILE
			
			echo "INFO: Search Successful for Content ${PATTERN} in the directory ${ROOT_DIR}." 
			echo "INFO: Result has been saved in file ${OUTPUT_FILE}."
		else
			echo "WARNING: There is no such file with content ${PATTERN} exists in directory ${ROOT_DIR}."
			exit 11
		fi
	else 
	  	echo "ERROR: Directory ${ROOT_DIR} not found"
	  	exit 1
	fi
#Redirect to Output Directory
openDirectory $(dirname $OUTPUT_FILE)
echo "Out Method: searchFilesByPattern()"
calculateTime $start_time
}

#Method to delete all files/folders of the given in the provided directory : 
deleteContentsInFolder() {
echo "In Method: deleteContentsInFolder()	:"
echo "INFO: You have chosen option of deleting all contents in folder"
	if [ -d $ROOT_DIR ]; 
	then
		rm -r $ROOT_DIR/*
		echo "Deleted all contents in the directory $ROOT_DIR successfully"
	else 
	  	echo "ERROR: Directory ${ROOT_DIR} not found"
	  	exit 1
	fi
echo "Out Method: deleteContentsInFolder()"
calculateTime $start_time
}

#Method to validate XML file: 
#$1 ==> Path to XML File 
#$2 ==> Path to external DTD/XSD
validateXML() {
echo "In Method: validateXML()"	
	if [ -f $1 ]; 
	then
		if [ ! -z $2 ]; 
		then
			echo "INFO: Validation with external file ${EXT_FILE}" 
			#To get extension of file
			TYPE="${2##*.}"	
			echo "INFO: Extension of the external file: ${TYPE} "
	
			if [[ $TYPE = dtd ]];
			then
				echo " validation with external DTD"
				xmllint --dtdvalid ${2} ${1}
			elif [[ $TYPE = xsd ]];
			then
				echo " Validation with external xsd"
				xmllint --schema ${2} ${1}
			fi								
		else	
			echo " Validate with internal dtd "
			#a DTD stored in the same file:
			xmllint --valid ${1}
		fi
		
		if [ $? -eq 0 ];
		then
			echo "INFO: XML File parsed successfully."
		else
			echo "ERROR: There is some Parsing Error in File." 
			exit 12
		fi
	else 
	  	echo "ERROR: ${1} not found"
	  	exit 1
	fi
echo "Out Method: validateXML()"
calculateTime $start_time
}

#Method to validate SGML file: 
#$1 ==> Path to SGML File 
#$2 ==> Path to external DTD
validateSGML(){
echo "In Method: validateSGML()"	
	if [ -f $1 ]; 
	then
		onsgmls -s -e -g -wall -E0 -c ${2} -f $ERROR_FILE $1
		echo "ERROR_FILE: ${ERROR_FILE}"
		if [ $? -eq 0 ];
		then
			if [ -z $ERROR_FILE ]; then
				echo "INFO: SGML file is validated successfully"
			fi			
		else
			echo "ERROR: There is some error in Parsing of SGML." 
			echo "ERROR: Check ${ERROR_FILE} file for more details"
			exit 4
		fi
	else 
	  	echo "ERROR: ${1} not found"
	  	exit 1
	fi
	
echo "Out Method: validateSGML()"
calculateTime $start_time
}

#Method to Open given directory in File Browser
#$1 ==> Absolute Path of the directory
openDirectory() {
echo "In Method: openDirectory()"	
	
	echo "Directory : ${WIN_PATH}"
	
	if [ -d $1 ]; 
	then
		# Convert Linux Path to Window Path
		WIN_PATH=$(echo ${1} | sed -e 's|/mnt/|''|' -e 's|[a-z]|\U&:|' -e 's|/|\\|g') 
		
		# Redirect to Directory in File Browser
		xdg-open $WIN_PATH
		if [ $? -eq 0 ]; 
		then	
			echo "INFO: Directory: ${WIN_PATH} opened successfully!!!"
		else
			echo "INFO: There is some error in opening the Directory: ${WIN_PATH}"
			exit $?
		fi
	else
		echo "Directory: ${1} doesn't exist"
		exit 1
	fi

echo "Out Method: openDirectory()"
calculateTime $start_time
}

#Method to Open a URL in Default Browser
#$1 ==> Web URL
openUrlInWebBrowser() {	
echo "In Method: openUrlInWebBrowser()"

	# Check if URL is valid and exists
	curl -ivs  $1
	
	if [ $? -eq 0 ]; 
	then
		xdg-open $1
		if [ $? -eq 0 ]; 
		then	
			echo "INFO: URL: ${1} opened successfully!!!"
		else
			echo "INFO: There is some error in opening the URL: ${1}"
			exit $?
		fi
	else
		echo "URL: ${1} doesn't exist"
		exit 1
	fi
	
echo "Out Method: openUrlInWebBrowser()"
calculateTime $start_time
}

#Method to calculate Script Execution Time
#$1 ==> Start Time
calculateTime() {	
	if [ ! -z $1 ]; 
	then
		end_time=`date +%s%N`
		Execution_time=$(($((end_time-$1))/1000000))
		if [ $Execution_time -ge 1000 ] 
		then	
			Execution_time=$(($((end_time-$1))/1000000000))
			echo "INFO: Execution Time from script: ${Execution_time} seconds"
		else
			echo "INFO: Execution Time from script: ${Execution_time} ms"
		fi
	fi
}

#To do all required treatments
# Initialize all the Variables
Initialize

# Get and Check parameters
GetParameters $*

# Processing Of the Files
if [ "${DIR_MODE}" = "-d" ];then
	formatAllFILES  $ROOT_DIR
elif [ "${XML_FORMAT_MODE}" = "-format" ];then
	formatXML $INPUT_FILE $XML_OUTPUT
elif [ "${SPLIT_MODE}" = "-splits" ];then
	splitBySize $INPUT_FILE $CHUNK_SIZE $CATALOGUE_FILE 
elif [ "${SPLIT_MODE}" = "-fsplits" ];then
	flatSplitBySize $INPUT_FILE $CHUNK_SIZE 
elif [ "${SPLIT_MODE}" = "-fsplitl" ];then
	flatsplitByLine $INPUT_FILE $LINE_SIZE
elif [ "${SPLIT_MODE}" = "-splitl" ];then
	splitByLevel $INPUT_FILE $LEVEL $CATALOGUE_FILE
elif [ "${SPLIT_MODE}" = "-splite" ];then
	splitByElement $INPUT_FILE $ELEMENT $CATALOGUE_FILE
elif [ "${SORT_MODE}" = "-sort" ];then
	sortFile $INPUT_FILE $XML_OUTPUT $CATALOGUE_FILE
elif [ "${SGML_CONV_XML_MODE}" = "-validatesgml" ];then
	validateSGML $SGML_FILE $CATALOGUE_FILE
elif [ "${XML_VALIDATE_MODE}" = "-validatexml" ];then
	validateXML $XML_FILE $EXT_FILE	
elif [ "${SEARCH_MODE}" = "-searcht" ];then
	searchFilesByType
elif [ "${SEARCH_MODE}" = "-searchp" ];then
	searchFilesByPattern
elif [ "${SGML_CONV_XML_MODE}" = "-sgx" ];then
	convertSGMLToXML $SGML_FILE $TMP_FILE
elif [ "${DELETE_MODE}" = "-deletedir" ];then
	deleteContentsInFolder
elif [ "${OPEN_MODE}" = "-opendir" ];then
	openDirectory $ROOT_DIR
elif [ "${OPEN_MODE}" = "-open" ];then
	openUrlInWebBrowser $URL
fi