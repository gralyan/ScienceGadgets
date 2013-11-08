package com.sciencegadgets.client.entities;

import java.io.Serializable;

import com.googlecode.objectify.annotation.Entity;

@Entity
public class Unit implements Serializable{
/**
	 * 
	 */
	private static final long serialVersionUID = -6353142243407326936L;


	String code;
	String label;
	String symbol;
	String quantityKind;
	String description;
	double conversionMultiplier;
	double conversionOffset;

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

	public void setQuantityKind(String quantityKind) {
		this.quantityKind = quantityKind;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setConversionMultiplier(double conversionMultiplier) {
		this.conversionMultiplier = conversionMultiplier;
	}

	public void setConversionOffset(double conversionOffset) {
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

	public String getQuantityKind() {
		return quantityKind;
	}

	public String getDescription() {
		return description;
	}

	public double getConversionMultiplier() {
		return conversionMultiplier;
	}

	public double getConversionOffset() {
		return conversionOffset;
	}

}
