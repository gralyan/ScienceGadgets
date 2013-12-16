package com.sciencegadgets.shared;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

/**
 * <b>data-unit</b> attribute <b>= </b>id^exp*id^exp... <br/>
 * <br/>
 * <b>basic</b> Unit<b>=</b> quantityKind_symbol^exp <br/>
 * <b>basic</b> Quantity<b>=</b> quantityKind^exp <br/>
 * <br/>
 * examples: <br/>
 * <b>liters:</b> Volume_L_1 <br/>
 * <b>meters per seconds squared:</b> Length_m^1*Time_s^-2
 */
public class UnitUtil {

	public static final String UNIT_CLASSNAME = "unit";
	public static final String BASE_DELIMITER = "*";
	public static final String BASE_DELIMITER_REGEX = "\\*";
	public static final String EXP_DELIMITER = "^";
	public static final String EXP_DELIMITER_REGEX = "\\^";
	public static final String NAME_DELIMITER = "_";

	public static Element element_From_MathNode(MathNode mathNode) {
		return element_From_attribute(mathNode.getUnitAttribute());
	}

	public static Element element_From_attribute(String dataUnit) {

		Element numerator = DOM.createDiv();
		Element denominator = DOM.createDiv();

		String[] dataUnitArray = dataUnit.split(BASE_DELIMITER_REGEX);

		for (String basic : dataUnitArray) {
			if ("".equals(basic)) {
				continue;
			}

			String[] baseAndExp = basic.split(EXP_DELIMITER_REGEX);
			String name = baseAndExp[0];
			String exponent = baseAndExp[1];

			String[] nameParts = name.split(NAME_DELIMITER);

			// If Unit: (baseParts ==Unit.getId()== QuantityKind_Symbol)
			// Else QuantityKind: (baseParts == QuantityKind)
			String symbol = nameParts.length == 2 ? nameParts[1] : name;

			Element unitDiv = DOM.createDiv();
			unitDiv.addClassName(TypeML.Term.asChild());

			if (exponent.startsWith("-")) {// Negative
				exponent = exponent.replace("-", "");
				denominator.appendChild(unitDiv);
			} else {
				numerator.appendChild(unitDiv);
			}
			if ("1".equals(exponent)) {
				exponent = TypeML.Operator.SPACE.getSign();
			}

			Element symbolDiv = DOM.createDiv();
			symbolDiv.addClassName(TypeML.Exponential.asChild() + "-base");
			symbolDiv.setInnerText(symbol);
			unitDiv.appendChild(symbolDiv);

			Element expDiv = DOM.createDiv();
			expDiv.addClassName(TypeML.Exponential.asChild() + "-exponent");
			expDiv.setInnerText(exponent);
			unitDiv.appendChild(expDiv);
		}

		if (denominator.getChildCount() > 0) {
			Element frac = DOM.createDiv();
			frac.addClassName(TypeML.Term.asChild());
			numerator.addClassName(TypeML.Fraction.asChild() + "-numerator");
			denominator
					.addClassName(TypeML.Fraction.asChild() + "-denominator");
			frac.appendChild(numerator);
			frac.appendChild(denominator);
			frac.addClassName(UNIT_CLASSNAME);
			
			if(numerator.getChildCount()==0) {
				Element oneDiv = DOM.createDiv();
				oneDiv.setInnerText("1");
				numerator.appendChild(oneDiv);
			}
			return frac;
		} else {
			numerator.addClassName(TypeML.Term.asChild());
			numerator.addClassName(UNIT_CLASSNAME);
			return numerator;
		}
	}

	public static String getQuantityKind(String unitName) {
		return unitName.split("_")[0];
	}
}
