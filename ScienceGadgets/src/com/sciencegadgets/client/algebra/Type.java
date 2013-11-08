package com.sciencegadgets.client.algebra;

import java.util.NoSuchElementException;

/**
 * <b>tag - attributes</b></br>
 * mn - data-randomness</br>
 * 
 * 
 *
 */

public enum Type {
	Term("mrow", true), Sum("mfenced", true), Exponential("msup", true), Fraction(
			"mfrac", true), Variable("mi", false), Number("mn", false), Operation(
			"mo", false), Equation("math", true);
	
//, Aesthetic("mglyph", false)
	private String tag;
	private boolean hasChildren;
	public final String IN_PREFIX = "in-";

	Type(String tag, boolean hasChildren) {
		this.tag = tag;
		this.hasChildren = hasChildren;
	}

	public String asChild() {
		return (IN_PREFIX + toString().toLowerCase());
	}

	public String getTag() {
		return tag;
	}

	public boolean hasChildren() {
		return hasChildren;
	}

	static Type getType(String tag) throws NoSuchElementException {
		tag = tag.toLowerCase();
		Type type = null;
		if ("mfenced".equals(tag)) {
			type = Type.Sum;
		} else if ("mrow".equals(tag)) {
			type = Type.Term;
		} else if ("mi".equals(tag)) {
			type = Type.Variable;
		} else if ("mn".equals(tag)) {
			type = Type.Number;
		} else if ("msup".equals(tag)) {
			type = Type.Exponential;
		} else if ("mfrac".equals(tag)) {
			type = Type.Fraction;
		} else if ("mo".equals(tag)) {
			type = Type.Operation;
		} else if ("math".equals(tag)) {
			type = Type.Equation;
		}
//		else if ("mglyph".equals(tag)) {
//			type = Type.Aesthetic;
//		}
		if (type == null) {
			throw new NoSuchElementException("There is no type for the tag: "
					+ tag);
		}
		return type;
	}

	public static enum Operator {
		DOT("\u00B7", "&middot;"), SPACE("\u00A0", "&nbsp;"), CROSS("\u00D7",
//				"&times;"), PLUS("+", "&#43;"), MINUS("\u002D", "&#45;");
	"&times;"), PLUS("\u002B", "&#43;"), MINUS("\u002D", "&#45;");

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