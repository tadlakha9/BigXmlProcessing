package com.soprasteria.springboot.model;

public class PrettyPrint {
	public String fileName;
	//to be included fileType(SGM file) option as well in future 

	public PrettyPrint(String fileName) {
		super();
		this.fileName = fileName;
	}

	@Override
	public String toString() {
		return "PrettyPrint [fileName=" + fileName + "]";
		
	}
	
	
}
