package com.sciencegadgets.shared;

public enum MathAttribute {

	Randomness("data-randomness"), Unit("data-unit");
	
	String name;
	
	MathAttribute(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
