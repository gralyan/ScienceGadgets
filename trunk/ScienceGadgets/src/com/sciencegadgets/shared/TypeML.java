package com.sciencegadgets.shared;

import java.util.NoSuchElementException;

/**
 * <b>tag - attributes</b></br>
 * mn - data-randomness</br>
 * 
 * 
 *
 */

public enum TypeML {
	Term("mrow", true), Sum("mfenced", true), Exponential("msup", true), Fraction(
			"mfrac", true), Variable("mi", false), Number("mn", false), Operation(
			"mo", false), Equation("math", true);
	
//, Aesthetic("mglyph", false)
	private String tag;
	private boolean hasChildren;
	public final String IN_PREFIX = "in-";

	TypeML(String tag, boolean hasChildren) {
		this.tag = tag;
		this.hasChildren = hasChildren;
	}

	public String asChild() {
		return (IN_PREFIX + toString().toLowerCase());
	}
	public String asChild(TypeArgument arg) {
		return (IN_PREFIX + toString().toLowerCase()+arg.get());
	}

	public String getTag() {
		return tag;
	}

	public boolean hasChildren() {
		return hasChildren;
	}

	public static TypeML getType(String tag) throws NoSuchElementException {
		tag = tag.toLowerCase();
		TypeML type = null;
		if ("mfenced".equals(tag)) {
			type = TypeML.Sum;
		} else if ("mrow".equals(tag)) {
			type = TypeML.Term;
		} else if ("mi".equals(tag)) {
			type = TypeML.Variable;
		} else if ("mn".equals(tag)) {
			type = TypeML.Number;
		} else if ("msup".equals(tag)) {
			type = TypeML.Exponential;
		} else if ("mfrac".equals(tag)) {
			type = TypeML.Fraction;
		} else if ("mo".equals(tag)) {
			type = TypeML.Operation;
		} else if ("math".equals(tag)) {
			type = TypeML.Equation;
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

	public static enum TypeArgument {
		NUMERATOR("-numerator"), DENOMINATOR("-denominator"), BASE("-base"), EXPONENT(
				"-exponent");
		
		String convention;

		TypeArgument(String convention){
			this.convention = convention;
		}
		
		String get() {
			return convention;
		}
	}
	public static enum Operator {
		Equals("\u003D", "&#x3d;"),DOT("\u00B7", "&middot;"), SPACE("\u00A0", "&nbsp;"), CROSS("\u00D7",
//				"&times;"), PLUS("+", "&#43;"), MINUS("\u002D", "&#45;");
	"&times;"), PLUS("\u002B", "&#43;"), MINUS("\u002D", "&#45;"), DIVIDE("\u00F7","&#247;");

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