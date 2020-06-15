package com.soprasteria.springboot.model;

public class Split {
	
	private String typeOfSplit;
	private String level;
	private String size;
	private String splitByElement;
	private String splitType;
	private String splitByLine;
	private String splitBySize;
	private String fileType;
	
	
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
