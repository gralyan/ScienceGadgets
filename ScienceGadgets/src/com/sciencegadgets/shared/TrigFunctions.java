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

public enum TrigFunctions {
	sin, cos, tan, //
	sec, csc, cot, //

	sinh, cosh, tanh, //
	sech, csch, coth, //

	arcsin, arccos, arctan, //
	arccot, arccsc, arcsec, //

	arccosh, arccoth, arccsch, //
	arcsech, arcsinh, arctanh;
	
	public static final String ARC = "arc";

	public static String getInverseName(String function) {
		if (function.startsWith("arc")) {
			return function.replace("arc", "");
		} else {
			return "arc" + function;
		}
	}

	public boolean isArc() {
		return toString().contains(ARC);
	}

	public TrigFunctions getReciprocal() {
		switch (this) {
		case sin:
			return TrigFunctions.csc;
		case cos:
			return TrigFunctions.sec;
		case tan:
			return TrigFunctions.cot;
		case csc:
			return TrigFunctions.sin;
		case sec:
			return TrigFunctions.cos;
		case cot:
			return TrigFunctions.tan;
		case sinh:
			return TrigFunctions.csch;
		case cosh:
			return TrigFunctions.sech;
		case tanh:
			return TrigFunctions.coth;
		case csch:
			return TrigFunctions.sinh;
		case sech:
			return TrigFunctions.cosh;
		case coth:
			return TrigFunctions.tanh;
		}
		return null;
	}

	/**
	 * @return the tangent definition or derivatives in an array with 3
	 * objects. The first and second are related directly and the third is
	 * related inversely. <b>Either the second or third entry will always be null</b><br/><br/>
	 * <b>ex:</b> tan = sin/cos<br/>
	 * for tan, this will return {sin, null, cos}
	 * 
	 *
	 */
	public TrigFunctions[] getDefinition() {
		switch (this) {
		case sin:
			return new TrigFunctions[] {tan, cos ,null};
		case cos:
			return new TrigFunctions[] { sin,null, tan};
		case tan:
			return new TrigFunctions[] { sin,null, cos};
			
		case csc:
			return new TrigFunctions[] {cot, sec ,null};
		case sec:
			return new TrigFunctions[] { csc,null, cot};
		case cot:
			return new TrigFunctions[] { csc,null, sec};

		case sinh:
			return new TrigFunctions[] {tanh, cosh ,null};
		case cosh:
			return new TrigFunctions[] { sinh,null, tanh};
		case tanh:
			return new TrigFunctions[] { sinh,null, cosh};
			
		case csch:
			return new TrigFunctions[] {coth, sech ,null};
		case sech:
			return new TrigFunctions[] { csch,null, coth};
		case coth:
			return new TrigFunctions[] { csch,null, sech};

		}
		return null;
	}
}