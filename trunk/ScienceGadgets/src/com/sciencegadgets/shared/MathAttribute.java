package com.sciencegadgets.shared;

public enum MathAttribute {

	Randomness("data-randomness"), Unit("data-unit"), Function("data-function"), LogBase("data-base");
	
	String name;
	
	MathAttribute(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
	}
}
