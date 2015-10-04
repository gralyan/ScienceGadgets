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
package com.sciencegadgets.shared.dimensions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

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
public class UnitHTML {

	public static final String UNIT_CLASSNAME = CSS.UNIT;
	public static final String UNIT_NODE_DELIMITER = "-unitNode-";
	private static final double UNIT_SIZE_FRAC = 20;// %
	private static final double UNIT_SIZE_NONFRAC = 40;// %

	public static Element create(EquationNode mathNode) {
		return create(mathNode.getUnitAttribute(), true);
	}

	public static Element create(EquationNode mathNode, String nodeId,
			boolean hasSmallUnits) {
		return create(mathNode.getUnitAttribute(), nodeId, hasSmallUnits);
	}

	public static Element create(UnitAttribute dataUnit, boolean hasSmallUnits) {
		return create(dataUnit, null, hasSmallUnits);
	}

	public static Element create(UnitAttribute dataUnit, String nodeId) {
		return create(dataUnit, nodeId, true);
	}

	public static Element create(UnitAttribute dataUnit, String nodeId,
			boolean hasSmallUnits) {

		Element numerator = DOM.createDiv();
		Element denominator = DOM.createDiv();

		UnitMultiple[] dataUnitArray = dataUnit.getUnitMultiples();

		for (UnitMultiple unitMultiple : dataUnitArray) {
			if ("".equals(unitMultiple.toString())) {
				continue;
			}

			UnitName name = unitMultiple.getUnitName();
			String exponent = unitMultiple.getUnitExponent();

			String symbol = name.getSymbol();
			if ("".equals(symbol)) {
				symbol = name.toString();
			}

			Element unitDiv = DOM.createDiv();
			unitDiv.setAttribute("id", symbol + UNIT_NODE_DELIMITER + nodeId);
			unitDiv.addClassName(TypeSGET.Term.asChild());

			Element side;
			if (exponent.startsWith("-")) {// Negative
				exponent = exponent.replace("-", "");
				side = denominator;
			} else {
				side = numerator;
			}
			side.appendChild(unitDiv);
			if ("1".equals(exponent)) {
				exponent = TypeSGET.Operator.SPACE.getSign();
			}

			if (!side.getFirstChild().equals(unitDiv)) {
				Element dotDiv = DOM.createDiv();
				dotDiv.addClassName(TypeSGET.Term.asChild(true));
				dotDiv.setInnerText(TypeSGET.Operator.DOT.getSign());
				side.insertBefore(dotDiv, unitDiv);
			}

			Element symbolDiv = DOM.createDiv();
			symbolDiv.addClassName(TypeSGET.Exponential.asChild(true));
			symbolDiv.setInnerText(symbol);
			unitDiv.appendChild(symbolDiv);

			Element expDiv = DOM.createDiv();
			expDiv.addClassName(TypeSGET.Exponential.asChild(false));
			expDiv.setInnerText(exponent);
			unitDiv.appendChild(expDiv);
		}

		if (denominator.getChildCount() > 0) {
			Element frac = DOM.createDiv();
			Element fracLine = DOM.createDiv();
			frac.addClassName(TypeSGET.Term.asChild());
			fracLine.addClassName(CSS.FRACTION_LINE);
			numerator.addClassName(TypeSGET.Fraction.asChild(true));
			denominator.addClassName(TypeSGET.Fraction.asChild(false));
			frac.appendChild(numerator);
			frac.appendChild(fracLine);
			frac.appendChild(denominator);
			frac.addClassName(UNIT_CLASSNAME);
			if (hasSmallUnits) {
				frac.getStyle().setFontSize(UNIT_SIZE_FRAC, Unit.PCT);
			}

			if (numerator.getChildCount() == 0) {
				Element oneDiv = DOM.createDiv();
				oneDiv.setInnerText("1");
				numerator.appendChild(oneDiv);
			}
			return frac;
		} else {
			numerator.addClassName(TypeSGET.Term.asChild());
			numerator.addClassName(UNIT_CLASSNAME);
			if (hasSmallUnits && !"Angle_°^1".equals(dataUnit.toString())) {
				numerator.getStyle().setFontSize(UNIT_SIZE_NONFRAC, Unit.PCT);
			}
			return numerator;
		}
	}
}
