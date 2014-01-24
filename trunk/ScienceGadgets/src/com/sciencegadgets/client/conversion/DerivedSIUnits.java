package com.sciencegadgets.client.conversion;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.shared.UnitMap;
import com.sciencegadgets.shared.UnitUtil;

public class DerivedSIUnits extends SelectionPanel {

	private static final String DELIMENTER = UnitUtil.NAME_DELIMITER;

	static enum Base {
		m("Length" + DELIMENTER + "m"), //
		kg("Mass" + DELIMENTER + "kg"), //
		s("Time" + DELIMENTER + "s"), //
		A("ElectricCurrent" + DELIMENTER + "A"), //
		K("ThermodynamicTemperature" + DELIMENTER + "K"), //
		mol("AmountOfSubstance" + DELIMENTER + "mol"), //
		cd("LuminousIntensity" + DELIMENTER + "cd");

		String name;

		private Base(String name) {
			this.name = name;
		}
	}

	static enum DerivedUnit {
		Hz("Frequency" + DELIMENTER + "Hz", 0, 0, -1, 0, 0, 0, 0), //
		N("Force" + DELIMENTER + "N", 1, 1, -2, 0, 0, 0, 0), //
		Pa("ForcePerArea" + DELIMENTER + "Pa", -1, 1, -2, 0, 0, 0, 0), //
		J("EnergyAndWork" + DELIMENTER + "J", 2, 1, -2, 0, 0, 0, 0), //
		W("Power" + DELIMENTER + "W", 2, 1, -3, 0, 0, 0, 0), //
		C("ElectricCharge" + DELIMENTER + "C", 0, 0, 1, 1, 0, 0, 0), //
		V("EnergyPerElectricCharge" + DELIMENTER + "V", 2, 1, -3, -1, 0, 0, 0), //
		F("Capacitance" + DELIMENTER + "F", -2, -1, 4, 2, 0, 0, 0), //
		ohm("Resistance" + DELIMENTER + "ohm", 2, 1, -3, -2, 0, 0, 0), //
		S("ElectricConductivity" + DELIMENTER + "S", -2, -1, 3, 2, 0, 0, 0), //
		Wb("MagneticFlux" + DELIMENTER + "Wb", 2, 1, -2, -1, 0, 0, 0), //
		T("MagneticField" + DELIMENTER + "T", 0, 1, -2, -1, 0, 0, 0), //
		H("Inductance" + DELIMENTER + "H", 2, 1, -2, -2, 0, 0, 0), //
		lm("LuminousFlux" + DELIMENTER + "lm", 0, 0, 0, 0, 0, 0, 1), //
		lx("LuminousFluxPerArea" + DELIMENTER + "lx", -2, 0, 0, 0, 0, 0, 1), //
		Bq("Radioactivity" + DELIMENTER + "Bq", 0, 0, -1, 0, 0, 0, 0), //
		Gy("SpecificEnergy" + DELIMENTER + "Gy", 2, 0, -2, 0, 0, 0, 0), //
		Sv("SpecificEnergy" + DELIMENTER + "Sv", 2, 0, -2, 0, 0, 0, 0), //
		kat("CatalyticActivity" + DELIMENTER + "kat", 0, 0, -1, 0, 0, 1, 0);

		UnitMap derivedMap = new UnitMap();
		String name;

		private DerivedUnit(String name, int m, int kg, int s, int A, int K,
				int mol, int cd) {
			this.name = name;

			if (m != 0)
				derivedMap.put(Base.m.name, m);
			if (kg != 0)
				derivedMap.put(Base.kg.name, kg);
			if (s != 0)
				derivedMap.put(Base.s.name, s);
			if (A != 0)
				derivedMap.put(Base.A.name, A);
			if (K != 0)
				derivedMap.put(Base.K.name, K);
			if (mol != 0)
				derivedMap.put(Base.mol.name, mol);
			if (cd != 0)
				derivedMap.put(Base.cd.name, cd);
		}

		public String getSymbol() {
			return UnitUtil.getSymbol(name);
		}

	}

	public DerivedSIUnits() {
		super("Derived Units");

		for (DerivedUnit derivedUnit : DerivedUnit.values()) {

			String dataUnitAttribute = derivedUnit.derivedMap
					.getUnitAttribute();
			Element derivedUnitElement = UnitUtil
					.element_From_attribute(dataUnitAttribute,null, false);
			derivedUnitElement.getStyle().setVerticalAlign(VerticalAlign.MIDDLE);
			String derivedUnitHTML = JSNICalls
					.elementToString(derivedUnitElement);
			
			String conversionAttribute = dataUnitAttribute + UnitUtil.BASE_DELIMITER
					+ derivedUnit.name + UnitUtil.EXP_DELIMITER + "-1";

			add(derivedUnit.getSymbol() + "=" + derivedUnitHTML,
					conversionAttribute);
		}
	}

}
