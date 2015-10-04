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

import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.ui.CSS;

/**
 * Types of equation xml nodes. See {@link MathAttribute} for attributes
 */

public enum TypeSGET {
	Equation("sget:equation","e",CSS.EQUATION, ChildRequirement.EQUATION, TypeSGET.NOT_SET + "+" + TypeSGET.NOT_SET), //
	Number("sget:num","n",CSS.NUMBER, ChildRequirement.TERMINAL,"#"), //
	Variable("sget:var","v",CSS.VARIABLE, ChildRequirement.TERMINAL,"<span class=\""+CSS.MATH_FONT+"\">a</span>"), //
	Operation("sget:op","o",CSS.OPERATION, ChildRequirement.TERMINAL,"+-\u00B7\u00F7"), //
	Sum("sget:sum","s",CSS.SUM, ChildRequirement.SEQUENCE, TypeSGET.NOT_SET + "+" + TypeSGET.NOT_SET), //
	Term("sget:term","t",CSS.TERM, ChildRequirement.SEQUENCE, TypeSGET.NOT_SET + Operator.DOT.getSign() + TypeSGET.NOT_SET), //
	Fraction("sget:frac","f",CSS.FRACTION, ChildRequirement.BINARY,
			"<div style='border-bottom: thin solid; text-align: center;'>"
			+ TypeSGET.NOT_SET + "</div><div style='text-align: center;'>" + TypeSGET.NOT_SET + "</div>"), //
	Exponential("sget:exp","x",CSS.EXPONENTIAL, ChildRequirement.BINARY,
			TypeSGET.NOT_SET + "<sup>" + TypeSGET.NOT_SET + "</sup>"), //
	Log("sget:log", "l",CSS.LOG, ChildRequirement.UNARY,
			"log<sub>" + TypeSGET.NOT_SET + "</sub>(" + TypeSGET.NOT_SET + ")" ), //
	Trig("sget:trig","r",CSS.TRIG, ChildRequirement.UNARY, "sin(" + TypeSGET.NOT_SET + ")" );

	private String tag;
	private String compressedTag;
	private String cssClassName;
	private ChildRequirement childRequirement;
	private String icon;
	public static final String NOT_SET = "\u25A1";
	public static final String NAMESPACE_PREFIX = "sg-";
	public static final String CHILD_PREFIX = "in-";

	TypeSGET(String tag,String compressedTag,String cssClassName, ChildRequirement childRequirement, String icon) {
		this.tag = tag;
		this.compressedTag = compressedTag;
		this.childRequirement = childRequirement;
		this.cssClassName = cssClassName;
		this.icon = icon;
	}
	/**
	 * Children of the type are given an altered version of the style name. The pre
	 */
	public String asChild() {
		return (NAMESPACE_PREFIX+CHILD_PREFIX + toString().toLowerCase().replace(NAMESPACE_PREFIX, ""));
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
		String qualifier = isFirstChild ? firstChild : seconChild;
		return (asChild() + qualifier);
	}

	public String asLogBase() {
		if (this.equals(Log)) {
			return (asChild() + "-base");
		}
		return "";
	}
	
	public String getTag() {
		return tag;
	}
	
	public String getCompressedTag() {
		return compressedTag;
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
	
	public String getIcon() {
		return icon;
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