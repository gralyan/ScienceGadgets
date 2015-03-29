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

/**
 * [quantityKind] + UnitAttribute.NAME_DELIMITER + [symbol]
 * +UnitAttribute.EXP_DELIMITER_REGEX+ [exp]
 * 
 * <br/>
 * examples: <br/>
 * <b>liter:</b> Volume_L^1 <br/>
 * <b>meter squared:</b> Length_m^2
 */
public class UnitMultiple {

	String unitMultiple;

	UnitMultiple(String unitMultiple) {
		this.unitMultiple = unitMultiple;
	}

	@Override
	public String toString() {
		return unitMultiple;
	}

	public UnitName getUnitName() {
		return new UnitName(
				unitMultiple.split(UnitAttribute.EXP_DELIMITER_REGEX)[0]);
	}

	public String getUnitExponent() {
		try {
			return unitMultiple.split(UnitAttribute.EXP_DELIMITER_REGEX)[1];
		} catch (IndexOutOfBoundsException e) {
			return "1";
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnitMultiple) {
			if (toString().equals(((UnitMultiple) obj).toString())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}