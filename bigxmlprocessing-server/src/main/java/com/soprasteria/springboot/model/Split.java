package com.soprasteria.springboot.model;

public class Split {
	
	public String typeOfSplit;
	public String level;
	public String size;
	public String splitByElement;
	
	
	
	
	
	
	public Split(String typeOfSplit, String level, String size, String splitByElement) {
		super();
		this.typeOfSplit = typeOfSplit;
		this.level = level;
		this.size = size;
		this.splitByElement = splitByElement;
	}






	@Override
	public String toString() {
		return "Split [typeOfSplit=" + typeOfSplit + ", level=" + level + ", size=" + size + ", splitByElement="
				+ splitByElement + "]";
	}
	
	

}
