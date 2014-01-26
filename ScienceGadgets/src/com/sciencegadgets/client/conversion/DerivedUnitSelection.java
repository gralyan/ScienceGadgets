package com.sciencegadgets.client.conversion;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.shared.UnitUtil;

public class DerivedUnitSelection extends SelectionPanel {

	public DerivedUnitSelection() {
		super("Derived Units");

		for (DerivedUnit derivedUnit : DerivedUnit.values()) {

			String dataUnitAttribute = derivedUnit.getDerivedMap()
					.getUnitAttribute();
			Element derivedUnitElement = UnitUtil.element_From_attribute(
					dataUnitAttribute, null, false);
			derivedUnitElement.getStyle()
					.setVerticalAlign(VerticalAlign.MIDDLE);
			String derivedUnitHTML = JSNICalls
					.elementToString(derivedUnitElement);

			String conversionAttribute = dataUnitAttribute
					+ UnitUtil.BASE_DELIMITER + derivedUnit.getName()
					+ UnitUtil.EXP_DELIMITER + "-1";

			add("("+derivedUnit+") "+derivedUnit.getSymbol() + "=" +derivedUnit.getConversionMultiplier()+ derivedUnitHTML,
					conversionAttribute);
		}
	}

}
