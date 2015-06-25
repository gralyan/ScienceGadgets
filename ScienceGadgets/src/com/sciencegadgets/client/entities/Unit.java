/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
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
	public Unit(String name, String quantityKindName,String label,String description,String conversionMultiplier) {
		this.name=name;
		this.quantityKindName=quantityKindName;
		this.label=label;
		this.description=description;
		this.conversionMultiplier=conversionMultiplier;
	}
//	public Unit(String name, Key<QuantityKind> quantityKind,String label,String description,String conversionMultiplier) {
//		this.name=name;
////		this.quantityKind=quantityKind;
//		this.label=label;
//		this.description=description;
//		this.conversionMultiplier=conversionMultiplier;
//	}

	public String getLabel() {
		return label;
	}

	public UnitName getName() {
		return new UnitName(name);
	}

//	public Key<QuantityKind> getQuantityKindKey() {
//		return quantityKind;
//	}
	public String getQuantityKindName() {
//		if(quantityKind != null) {
//			return quantityKind.getName();
//		}else {
			return quantityKindName;
//		}
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
//		quantityKindName = quantityKind.getName();
//		quantityKind = null;
	}

}
