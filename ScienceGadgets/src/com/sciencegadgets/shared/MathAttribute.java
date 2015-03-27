package com.sciencegadgets.shared;

public enum MathAttribute {

	ID("id", "i"), Randomness("randomness", "r"), Unit("unit","u"), Function("function","f"), LogBase(
			"base","b"), Value("value","v");

	String name;
	String compressedName;

	MathAttribute(String name, String compressedName) {
		this.name = name;
		this.compressedName = compressedName;
	}

	public String getAttributeName() {
		return name;
	}
	
	public String getCompressedName() {
		return compressedName;
	}
}
