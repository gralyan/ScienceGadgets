package com.sciencegadgets.client.conversion;

import com.sciencegadgets.shared.UnitAttribute;

public enum BaseUnit {
	m("Length", "m"), //
	kg("Mass", "kg"), //
	s("Time", "s"), //
	A("ElectricCurrent", "A"), //
	K("ThermodynamicTemperature", "K"), //
	mol("AmountOfSubstance", "mol"), //
	cd("LuminousIntensity", "cd");

	private final String name;
	private final String symbol;
	private final String quantityKind;

	private BaseUnit(String quantityKind, String symbol) {
		this.name = quantityKind + UnitAttribute.NAME_DELIMITER + symbol;
		this.quantityKind = quantityKind;
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public String getSymbol() {
		return symbol;
	}

	public String getQuantityKind() {
		return quantityKind;
	}
}