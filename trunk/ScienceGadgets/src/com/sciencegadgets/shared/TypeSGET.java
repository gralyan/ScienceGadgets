package com.sciencegadgets.shared;

import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.ui.CSS;

/**
 * Types of equation xml nodes. See {@link MathAttribute} for attributes
 */

public enum TypeSGET {
	Equation("sget:equation",CSS.EQUATION, ChildRequirement.EQUATION), //
	Number("sget:num",CSS.NUMBER, ChildRequirement.TERMINAL), //
	Variable("sget:var",CSS.VARIABLE, ChildRequirement.TERMINAL), //
	Operation("sget:op",CSS.OPERATION, ChildRequirement.TERMINAL), //
	Term("sget:term",CSS.TERM, ChildRequirement.SEQUENCE), //
	Sum("sget:sum",CSS.SUM, ChildRequirement.SEQUENCE), //
	Fraction("sget:frac",CSS.FRACTION, ChildRequirement.BINARY), //
	Exponential("sget:exp",CSS.EXPONENTIAL, ChildRequirement.BINARY), //
	Log("sget:log",CSS.LOG, ChildRequirement.UNARY), //
	Trig("sget:trig",CSS.TRIG, ChildRequirement.UNARY);

	private String tag;
	private String cssClassName;
	private ChildRequirement childRequirement;
	public static final String IN_PREFIX = "in-";

	TypeSGET(String tag,String cssClassName, ChildRequirement childRequirement) {
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

	public static TypeSGET getType(String tag) {
		tag = tag.toLowerCase();
		for (TypeSGET type : TypeSGET.values()) {
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

	public enum Operator {
		EQUALS("\u003D", "&#x3d;"), ARROW_RIGHT("\u2192", "&#8594;"), DOT("\u00B7", "&middot;"), SPACE("\u00A0",
				"&nbsp;"), CROSS("\u00D7", "&times;"), PLUS("\u002B", "&#43;"), MINUS(
				"\u002D", "&#45;"), DIVIDE("\u00F7", "&#247;"), POW("\u0302","&#770;");

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