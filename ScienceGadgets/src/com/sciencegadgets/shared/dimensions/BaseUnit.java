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


public enum BaseUnit {
	m("Length", "m"), //
	kg("Mass", "kg"), //
	s("Time", "s"), //
	A("ElectricCurrent", "A"), //
	K("Temperature", "K"), //
	mol("AmountOfSubstance", "mol"), //
	cd("LuminousIntensity", "cd");

	private final String name;
	private final String symbol;
	private final String quantityKind;

	private BaseUnit(String quantityKind, String symbol) {
		this.name = quantityKind + UnitAttribute.NAME_DELIMITER + symbol;
		this.quantityKind = quantityKind;
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getQuantityKind() {
		return quantityKind;
	}
}