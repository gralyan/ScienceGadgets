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
package com.sciencegadgets.shared.dimensions;

public class UnitAttribute {

	public static final String BASE_DELIMITER = "*";
	public static final String BASE_DELIMITER_REGEX = "\\*";
	public static final String EXP_DELIMITER = "^";
	public static final String EXP_DELIMITER_REGEX = "\\^";
	public static final String NAME_DELIMITER = "_";

	public static final String ANGLE = "Angle";
	public static final String ANGLE_ATTRIBUTE = "Angle^1";

	private String unitAttribute;
	
	/**
	 * {@link UnitAttribute} <b>attribute = </b>UnitMultiple*UnitMultiple... <br/>
	 * <br/>
	 * {@link UnitMultiple} <b>Unit =</b> quantityKind_symbol^exp <br/>
	 * {@link UnitMultiple} <b>Quantity =</b> quantityKind^exp <br/>
	 * <br/>
	 * examples: <br/>
	 * <b>liters:</b> Volume_L^1 <br/>
	 * <b>meters per seconds squared:</b> Length_m^1*Time_s^-2
	 */
	public UnitAttribute(String unitAttribute) {
		this.unitAttribute = unitAttribute;
	}

	@Override
	public String toString() {
		return unitAttribute + "";
	}

	public void setString(String unitAttribute) {
		this.unitAttribute = unitAttribute;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof UnitAttribute) {
			UnitAttribute other = (UnitAttribute) obj;
			if(toString().equals(other.toString())) {
				return true;
			}
			return new UnitMap(this).equals(new UnitMap(other));
		}
		return false;
	}

	public UnitMultiple[] getUnitMultiples() {
		if("".equals(toString())) {
			return new UnitMultiple[0];
		}
		String[] multiples = unitAttribute
				.split(UnitAttribute.BASE_DELIMITER_REGEX);
		int multipleCount = multiples.length;

		UnitMultiple[] unitMultiples = new UnitMultiple[multipleCount];
		for (int i = 0; i < multipleCount; i++) {
			unitMultiples[i] = new UnitMultiple(multiples[i]);
		}
		return unitMultiples;
	}
}
