package com.soprasteria.springboot.model;

public class Converter {
	
	public String sgmlfile;
	public String errorfile;
	
	public Converter(String sgmlfile, String errorfile) {
		super();
		this.sgmlfile = sgmlfile;
		this.errorfile = errorfile;
	}

	@Override
	public String toString() {
		return "Converter [sgmlfile=" + sgmlfile + ", errorfile=" + errorfile + "]";
	}
	
}