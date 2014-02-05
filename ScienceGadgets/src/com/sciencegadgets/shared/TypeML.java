package com.sciencegadgets.shared;

import java.util.NoSuchElementException;

import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.JSNICalls;

/**
 * <em><b>TypeML - tag - attributes</b></em></br> Number - mn -
 * data-randomness</br> Number - mn - data-unit</br> Trig- trig -
 * data-function</br> Log- log - data-base</br>
 * 
 */

public enum TypeML {
	Equation("sgmt:equation",CSS.EQUATION, ChildRequirement.EQUATION), //
	Number("sgmt:num",CSS.NUMBER, ChildRequirement.TERMINAL), //
	Variable("sgmt:var",CSS.VARIABLE, ChildRequirement.TERMINAL), //
	Operation("sgmt:op",CSS.OPERATION, ChildRequirement.TERMINAL), //
	Term("sgmt:term",CSS.TERM, ChildRequirement.SEQUENCE), //
	Sum("sgmt:sum",CSS.SUM, ChildRequirement.SEQUENCE), //
	Fraction("sgmt:frac",CSS.FRACTION, ChildRequirement.BINARY), //
	Exponential("sgmt:exp",CSS.EXPONENTIAL, ChildRequirement.BINARY), //
	Log("sgmt:log",CSS.LOG, ChildRequirement.UNARY), //
	Trig("sgmt:trig",CSS.TRIG, ChildRequirement.UNARY);

	// Equation("math", ChildRequirement.EQUATION), //
	// Number("mn", ChildRequirement.TERMINAL), //
	// Variable("mi", ChildRequirement.TERMINAL), //
	// Operation("mo", ChildRequirement.TERMINAL), //
	// Term("mrow", ChildRequirement.SEQUENCE), //
	// Sum("mfenced", ChildRequirement.SEQUENCE), //
	// Fraction("mfrac", ChildRequirement.BINARY), //
	// Exponential("msup", ChildRequirement.BINARY), //
	// Log("log", ChildRequirement.UNARY), //
	// Trig("trig", ChildRequirement.UNARY);

	private String tag;
	private String cssClassName;
	private ChildRequirement childRequirement;
	public static final String IN_PREFIX = "in-";

	TypeML(String tag,String cssClassName, ChildRequirement childRequirement) {
		this.tag = tag;
		this.childRequirement = childRequirement;
		this.cssClassName = cssClassName;
	}

	public String asChild() {
		return (IN_PREFIX + toString().toLowerCase());
	}

	public String asChild(boolean isFirstChild) {
		String firstChild = "";
		String seconChild = "";
		switch (this) {
		case Fraction:
			firstChild = "-numerator";
			seconChild = "-denominator";
			break;
		case Exponential:
			firstChild = "-base";
			seconChild = "-exponent";
			break;
		}
		String qualifier = seconChild;
		if (isFirstChild) {
			qualifier = firstChild;
		}
		return (IN_PREFIX + toString().toLowerCase() + qualifier);
	}

	public String asLogBase() {
		if (this.equals(Log)) {
			return (IN_PREFIX + toString().toLowerCase() + "-base");
		}
		return "";
	}

	public String getTag() {
		return tag;
	}

	public ChildRequirement childRequirement() {
		return childRequirement;

	}

	public static TypeML getType(String tag) throws NoSuchElementException {
		tag = tag.toLowerCase();
		for (TypeML type : TypeML.values()) {
			if (type.getTag().equals(tag)) {
				return type;
			}
		}
		JSNICalls.error("There is no type for the tag: " + tag);
		return null;
	}
	
	@Override
	public String toString() {
		return cssClassName;
	}

	/**
	 * <em><b>childCountRequirement</b></em></br> 0 - no element children,
	 * <b>text only</b></br> 1 - one element child, no text</br> 2 - two element
	 * children, no text</br> 3 - three element children, no text, <b>equation
	 * only, middle must be equals</b></br> 4 - At least 3 element children, no
	 * text, <b>every other must my operation</b></br>
	 */
	public enum ChildRequirement {
		EQUATION, TERMINAL, UNARY, BINARY, SEQUENCE;

	}

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

	public enum Operator {
		Equals("\u003D", "&#x3d;"), DOT("\u00B7", "&middot;"), SPACE("\u00A0",
				"&nbsp;"), CROSS("\u00D7", "&times;"), PLUS("\u002B", "&#43;"), MINUS(
				"\u002D", "&#45;"), DIVIDE("\u00F7", "&#247;");

		private String sign;
		private String html;

		Operator(String sign, String html) {
			this.sign = sign;
			this.html = html;
		}

		public String getSign() {
			return sign;
		}

		public String getHTML() {
			return html;
		}

		public static Operator getMultiply() {
			return DOT;
		}
	}

}