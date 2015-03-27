package com.sciencegadgets.client.entities;

import java.io.Serializable;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;
import com.sciencegadgets.shared.dimensions.UnitName;

@Entity
public class Unit implements Serializable, IsSerializable{
	private static final long serialVersionUID = 1L;

	@Id
	String name;
	
	@Parent
	Key<QuantityKind> quantityKind;
	
	String quantityKindName;
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

	public UnitName getName() {
		return new UnitName(name);
	}

	public Key<QuantityKind> getQuantityKindKey() {
		return quantityKind;
	}
	public String getQuantityKindName() {
		if(quantityKind != null) {
			return quantityKind.getName();
		}else {
			return quantityKindName;
		}
	}

	public String getDescription() {
		return description;
	}

	public String getConversionMultiplier() {
		return conversionMultiplier;
	}

	public String getSymbol() {
		return getName().getSymbol();
	}
	
	public void removeQuantityKindKey() {
		quantityKindName = quantityKind.getName();
		quantityKind = null;
	}

}
