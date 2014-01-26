package com.sciencegadgets.shared;

import java.util.NoSuchElementException;

import com.sciencegadgets.client.JSNICalls;

/**
 * <em><b>TypeML - tag - attributes</b></em></br> Number - mn -
 * data-randomness</br> Number - mn - data-unit</br> Trig- trig -
 * data-function</br> Log- log -
 * data-base</br>
 * 
 */

public enum TypeML {
	Equation("sgmt:equation", ChildRequirement.EQUATION), //
	Number("sgmt:num", ChildRequirement.TERMINAL), //
	Variable("sgmt:var", ChildRequirement.TERMINAL), //
	Operation("sgmt:op", ChildRequirement.TERMINAL), //
	Term("sgmt:term", ChildRequirement.SEQUENCE), //
	Sum("sgmt:sum", ChildRequirement.SEQUENCE), //
	Fraction("sgmt:frac", ChildRequirement.BINARY), //
	Exponential("sgmt:exp", ChildRequirement.BINARY), //
	Log("sgmt:log", ChildRequirement.UNARY), //
	Trig("sgmt:trig", ChildRequirement.UNARY);

//	Equation("math", ChildRequirement.EQUATION), //
//	Number("mn", ChildRequirement.TERMINAL), //
//	Variable("mi", ChildRequirement.TERMINAL), //
//	Operation("mo", ChildRequirement.TERMINAL), //
//	Term("mrow", ChildRequirement.SEQUENCE), //
//	Sum("mfenced", ChildRequirement.SEQUENCE), //
//	Fraction("mfrac", ChildRequirement.BINARY), //
//	Exponential("msup", ChildRequirement.BINARY), //
//	Log("log", ChildRequirement.UNARY), //
//	Trig("trig", ChildRequirement.UNARY);

	private String tag;
	private ChildRequirement childRequirement;
	public static final String IN_PREFIX = "in-";

	TypeML(String tag, ChildRequirement childRequirement) {
		this.tag = tag;
		this.childRequirement = childRequirement;
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
		if(isFirstChild) {
			qualifier = firstChild;
		}
		return (IN_PREFIX + toString().toLowerCase() + qualifier);
	}
	public String asLogBase() {
		if(this.equals(Log)) {
			return (IN_PREFIX + toString().toLowerCase()+"-base");
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
	
	/**
	 * <em><b>childCountRequirement</b></em></br> 0 - no element children,
	 * <b>text only</b></br> 1 - one element child, no text</br> 2 - two element
	 * children, no text</br> 3 - three element children, no text, <b>equation only, middle
	 * must be equals</b></br> 4 - At least 3 element children, no text,
	 * <b>every other must my operation</b></br>
	 */
	public static enum ChildRequirement {
		EQUATION,TERMINAL, UNARY, BINARY,  SEQUENCE;

	}


	public static enum TrigFunctions {
		sin, cos, tan, sec, csc, cot, sinh, cosh, tanh, sech, csch, coth, arcsin, arccos, arctan, arccot, arccsc, arcsec, arccosh, arccoth, arccsch, arcsech, arcsinh, arctanh;
		
		public static String getInverse(String function){
			if(function.startsWith("arc")) {
				return function.replace("arc", "");
			}else {
				return "arc"+function;
			}
		}
	}

	public static enum Operator {
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