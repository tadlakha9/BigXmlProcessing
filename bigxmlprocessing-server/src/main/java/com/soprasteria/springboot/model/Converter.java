package com.soprasteria.springboot.model;

public class Converter {
	
	public String sgmlfile;
	public String catalogfile;
	//public String errorfile;
	
	public Converter(String sgmlfile, String catalogfile) {
		super();
		this.sgmlfile = sgmlfile;
		this.catalogfile = catalogfile;
		//error file to be used in future 
		//this.errorfile = errorfile;
	}

	@Override
	public String toString() {
		return "Converter [sgmlfile=" + sgmlfile + ", catalogfile=" + catalogfile + "]";
	}
	
}
