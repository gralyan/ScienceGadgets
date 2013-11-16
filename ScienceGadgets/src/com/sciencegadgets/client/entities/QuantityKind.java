package com.sciencegadgets.client.entities;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Entity
public class QuantityKind {
	
	@Id
	String id;
	
	public QuantityKind() {
	}
	public QuantityKind(String id) {
		this.id=id;
	}
	
	public String getId(){
		return id;
	}
}
