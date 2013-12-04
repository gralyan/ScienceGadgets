package com.sciencegadgets.client.conversion;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.DOM;
import com.sciencegadgets.shared.TypeML;

public class UnitUtil {
	
	public static final String UNIT_CLASSNAME = "unit";

	public static Element attributeToHTML(String dataUnit) {
		Element numerator = DOM.createDiv();
		Element denominator = DOM.createDiv();
		
		String[] unitsAndQK = dataUnit.split("\\*");
		
		//Last string in array is QuantityKind
		for(int i=0 ; i<unitsAndQK.length-1; i++){
			String unit = unitsAndQK[i];
			
			Element unitDiv = DOM.createDiv();
			if (unit.contains("^-1")) {
				unitDiv.setInnerText(unit.replace("^-1", ""));
				denominator.appendChild(unitDiv);
			} else if (unit.contains("^-")) {
				String[] u = unit.split("\\^-");
				unitDiv.setInnerText(u[0]);
				denominator.appendChild(unitDiv);
				Element expDiv = DOM.createDiv();
				expDiv.setInnerText(u[1]);
				unitDiv.addClassName(TypeML.Exponential.asChild() + "-base");
				expDiv.addClassName(TypeML.Exponential.asChild() + "-exponent");
				denominator.appendChild(expDiv);
			} else if (unit.contains("^")) {
				String[] u = unit.split("\\^");
				unitDiv.setInnerText(u[0]);
				numerator.appendChild(unitDiv);
				Element expDiv = DOM.createDiv();
				expDiv.setInnerText(u[1]);
				unitDiv.addClassName(TypeML.Exponential.asChild() + "-base");
				expDiv.addClassName(TypeML.Exponential.asChild() + "-exponent");
				numerator.appendChild(expDiv);
			} else {
				unitDiv.setInnerText(unit);
				numerator.appendChild(unitDiv);

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
			frac.addClassName("unit");
			return frac;
		} else {
			numerator.addClassName(UNIT_CLASSNAME);
			numerator.addClassName(TypeML.Term.asChild());
			return numerator;
		}
	}
	
	public static String getSymbol(String dataUnit){

		String[] unitsAndQK = dataUnit.split("\\*");
String symbol = "";
		for(int i=0 ; i<unitsAndQK.length-1; i++){
			symbol = symbol+unitsAndQK[i];
		}
		return symbol;
	}
	public static String getQuantityKind(String dataUnit){
		
		String[] unitsAndQK = dataUnit.split("\\*");
		return unitsAndQK[unitsAndQK.length-1];
	}
}
