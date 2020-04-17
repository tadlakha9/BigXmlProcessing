package com.soprasteria.springboot.model;

public class PrettyPrint {
	public String fileType;

	public PrettyPrint(String fileType) {
		super();
		this.fileType = fileType;
	}

	@Override
	public String toString() {
		return "PrettyPrint [fileType=" + fileType + "]";
		
	}
	
	
	
	
}
