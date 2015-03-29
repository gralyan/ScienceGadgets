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
package com.sciencegadgets.shared;

public enum MathAttribute {

	ID("id", "i"), Randomness("randomness", "r"), Unit("unit","u"), Function("function","f"), LogBase(
			"base","b"), Value("value","v");

	String name;
	String compressedName;

	MathAttribute(String name, String compressedName) {
		this.name = name;
		this.compressedName = compressedName;
	}

	public String getAttributeName() {
		return name;
	}
	
	public String getCompressedName() {
		return compressedName;
	}
}
