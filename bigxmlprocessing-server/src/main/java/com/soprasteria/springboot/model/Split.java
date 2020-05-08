package com.soprasteria.springboot.model;

import org.springframework.web.multipart.MultipartFile;

public class Split {
	
	public String typeOfSplit;
	public String level;
	public String size;
	public String splitByElement;
	public String splitType;
	public String splitByLine;
	public String splitBySize;
	public String fileType;
	
	
	public Split(String typeOfSplit, String level, String size, String splitByElement, String splitType,
			String splitByLine, String splitBySize, String fileType) {
		super();
		this.typeOfSplit = typeOfSplit;
		this.level = level;
		this.size = size;
		this.splitByElement = splitByElement;
		this.splitType = splitType;
		this.splitByLine = splitByLine;
		this.splitBySize = splitBySize;
		this.fileType = fileType;
	}


	@Override
	public String toString() {
		return "Split [typeOfSplit=" + typeOfSplit + ", level=" + level + ", size=" + size + ", splitByElement="
				+ splitByElement + ", splitType=" + splitType + ", splitByLine=" + splitByLine + ", splitBySize="
				+ splitBySize + ", fileType=" + fileType + "]";
	}
	

	


	

}
