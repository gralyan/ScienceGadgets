package com.sciencegadgets.shared;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.sciencegadgets.client.JSNICalls;

/**
 * <b>data-unit</b> = baseUnit*baseUnit... <br/>
 * <b>baseUnit</b> = quantityKind_symbol_exponent <br/>
 * <b>unitName</b> = quantityKind_symbol <br/>
 * <br/>
 * examples: <br/>
 * <b>liters:</b> Volume_L_1 <br/>
 * <b>meters per seconds squared:</b> Length_m_1*Time_s_-2
 */
public class UnitUtil {

	public static final String UNIT_CLASSNAME = "unit";

	public static Element attributeToHTML(String dataUnit) {

		Element numerator = DOM.createDiv();
		Element denominator = DOM.createDiv();

		String[] baseUnits = dataUnit.split("\\*");

		for (String baseUnit : baseUnits) {

			String[] unitParts = baseUnit.split("_");
			String symbol = unitParts[1];
			String exponent = unitParts[2];

			Element unitDiv = DOM.createDiv();
			unitDiv.setInnerText(symbol);

			if (exponent.equals("0")) {
				JSNICalls
						.error("There should never be a unit with exponent of 0: "
								+ dataUnit);
			} else if (exponent.equals("1")) {
				numerator.appendChild(unitDiv);
			} else if (exponent.equals("-1")) {
				denominator.appendChild(unitDiv);
			} else if (exponent.startsWith("-")) {// Negative
				denominator.appendChild(unitDiv);

				Element expDiv = DOM.createDiv();
				expDiv.setInnerText(exponent);
				denominator.appendChild(expDiv);

				unitDiv.addClassName(TypeML.Exponential.asChild() + "-base");
				expDiv.addClassName(TypeML.Exponential.asChild() + "-exponent");
			} else {// positive number
				numerator.appendChild(unitDiv);

				Element expDiv = DOM.createDiv();
				expDiv.setInnerText(exponent);
				numerator.appendChild(expDiv);

				unitDiv.addClassName(TypeML.Exponential.asChild() + "-base");
				expDiv.addClassName(TypeML.Exponential.asChild() + "-exponent");

			}
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
