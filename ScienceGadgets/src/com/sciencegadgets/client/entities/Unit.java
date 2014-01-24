package com.sciencegadgets.client.entities;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.sciencegadgets.shared.UnitMap;
import com.sciencegadgets.shared.UnitUtil;

@Entity
public class Unit implements Serializable{

	private static final long serialVersionUID = -6353142243407326936L;

	@Id
	String name;
	
	@Parent
	Key<QuantityKind> quantityKind;
	
	String label;
	String description;
	String conversionMultiplier;

	public Unit() {
	}
	public Unit(String name, Key<QuantityKind> quantityKind,String label,String description,String conversionMultiplier) {
		this.name=name;
		this.quantityKind=quantityKind;
		this.label=label;
		this.description=description;
		this.conversionMultiplier=conversionMultiplier;
	}

	public String getLabel() {
		return label;
	}

	public String getName() {
		return name;
	}

	public Key<QuantityKind> getQuantityKindKey() {
		return quantityKind;
	}
	public String getQuantityKindName() {
		return quantityKind.getName();
	}

	public String getDescription() {
		return description;
	}

	public String getConversionMultiplier() {
		return conversionMultiplier;
	}

	public String getSymbol() {
		return UnitUtil.getSymbol(name);
	}

}
