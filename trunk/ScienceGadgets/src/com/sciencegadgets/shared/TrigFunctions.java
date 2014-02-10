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