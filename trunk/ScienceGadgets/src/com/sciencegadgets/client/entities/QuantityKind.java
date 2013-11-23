package com.sciencegadgets.client.entities;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class QuantityKind implements Serializable{
	
	@Id
	String id;
	String dimension;
	
	public QuantityKind() {
	}
	public QuantityKind(String id, String dimension) {
		this.id=id;
		this.dimension = dimension;
	}
	
	public String getId(){
		return id;
	}
	public String getDimension(){
		return dimension;
	}
}
