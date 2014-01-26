package com.sciencegadgets.shared;

public enum MathAttribute {

	Randomness("randomness"), Unit("unit"), Function("function"), LogBase(
			"base"), Value("value");

	String name;

	MathAttribute(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
