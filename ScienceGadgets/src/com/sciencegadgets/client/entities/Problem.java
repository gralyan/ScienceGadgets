package com.sciencegadgets.client.entities;

import java.io.Serializable;
import java.util.HashSet;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.sciencegadgets.client.entities.users.Badge;

@Entity
public class Problem  implements Serializable{

	private static final long serialVersionUID = 3194351441392500048L;

	@Id
	Long id;

	@Index
	Badge requiredBadge;
	
	String title;
	String description;
	HashSet<Equation> equations;
	
	public Problem(){
	}
	public Problem(String title, String description, Badge requiredBadge){
		this.title=title;
		this.description=description;
		this.requiredBadge=requiredBadge;
	}
	public Problem(String title, String description, Badge requiredBadge, Equation equation){
		this(title, description, requiredBadge);
		addEquation(equation);
	}
	
	public void addEquation(Equation eq) {
		if(equations == null) {
			equations = new HashSet<Equation>();
		}
		
		equations.add(eq);
	}
	
	public Badge getRequiredBadge() {
		return requiredBadge;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public HashSet<Equation> getEquations() throws NullPointerException {
		return equations;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
