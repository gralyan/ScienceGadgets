package com.sciencegadgets.client.entities;

import java.io.Serializable;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

@Entity
public class Unit implements Serializable{

	private static final long serialVersionUID = -6353142243407326936L;

	@Id
	String code;
	
	@Parent
	Key<QuantityKind> quantityKind;
	
	String label;
	String symbol;
	String description;
	String conversionMultiplier;
	String conversionOffset;

	public Unit() {
	}
	
	public void setCode(String code) {
		this.code = code;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public void setQuantityKind(Key<QuantityKind> quantityKind) {
		this.quantityKind = quantityKind;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setConversionMultiplier(String conversionMultiplier) {
		this.conversionMultiplier = conversionMultiplier;
	}

	public void setConversionOffset(String conversionOffset) {
		this.conversionOffset = conversionOffset;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getLabel() {
		return label;
	}

	public String getSymbol() {
		return symbol;
	}

	public Key<QuantityKind> getQuantityKind() {
		return quantityKind;
	}

	public String getDescription() {
		return description;
	}

	public String getConversionMultiplier() {
		return conversionMultiplier;
	}

	public String getConversionOffset() {
		return conversionOffset;
	}

}
