package com.sciencegadgets.client.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;



@Entity
public class Equation {
	
	@Id
	Long id;
	
	String xml;
	
	public Equation(){ } 
	
	public void setXML(String xml){
		this.xml = xml;
	}

	public String getXML(){
		return xml;
	}
}


    
