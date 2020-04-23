package com.soprasteria.springboot.model;

public class Converter {
	
	public String sgmlfile;
	public String catalogfile;
	public String errorfile;
	
	
	public Converter(String sgmlfile, String catalogfile, String errorfile) {
		super();
		this.sgmlfile = sgmlfile;
		this.catalogfile = catalogfile;
		this.errorfile = errorfile;
	}


	@Override
	public String toString() {
		return "Converter [sgmlfile=" + sgmlfile + ", catalogfile=" + catalogfile + ", errorfile=" + errorfile + "]";
	}
	
	

}
