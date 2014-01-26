package com.sciencegadgets.client.conversion;

import com.sciencegadgets.shared.UnitMap;
import com.sciencegadgets.shared.UnitUtil;

public enum DerivedUnit {
	// SI definitions
	// "quantityKind", "symbol", "conversion", m,kg,s,A,K,mol,cd
	Farad("Capacitance", "F", "1", -2, -1, 4, 2, 0, 0, 0), //
	Katal("CatalyticActivity", "kat", "1", 0, 0, -1, 0, 0, 1, 0), //
	Coulomb("ElectricCharge", "C", "1", 0, 0, 1, 1, 0, 0, 0), //
	Siemens("ElectricConductivity", "S", "1", -2, -1, 3, 2, 0, 0, 0), //
	Joule("EnergyAndWork", "J", "1", 2, 1, -2, 0, 0, 0, 0), //
	Volt("EnergyPerElectricCharge", "V", "1", 2, 1, -3, -1, 0, 0, 0), //
	Newton("Force", "N", "1", 1, 1, -2, 0, 0, 0, 0), //
	Pascal("ForcePerArea", "Pa", "1", -1, 1, -2, 0, 0, 0, 0), //
	Hertz("Frequency", "Hz", "1", 0, 0, -1, 0, 0, 0, 0), //
	Henry("Inductance", "H", "1", 2, 1, -2, -2, 0, 0, 0), //
	Lumen("LuminousFlux", "lm", "1", 0, 0, 0, 0, 0, 0, 1), //
	Lux("LuminousFluxPerArea", "lx", "1", -2, 0, 0, 0, 0, 0, 1), //
	Tesla("MagneticField", "T", "1", 0, 1, -2, -1, 0, 0, 0), //
	Weber("MagneticFlux", "Wb", "1", 2, 1, -2, -1, 0, 0, 0), //
	Watt("Power", "W", "1", 2, 1, -3, 0, 0, 0, 0), //
	Becquerel("Radioactivity", "Bq", "1", 0, 0, -1, 0, 0, 0, 0), //
	Ohm("Resistance", "ohm", "1", 2, 1, -3, -2, 0, 0, 0), //
	Gray("SpecificEnergy", "Gy", "1", 2, 0, -2, 0, 0, 0, 0), //
	Sievert("SpecificEnergy", "Sv", "1", 2, 0, -2, 0, 0, 0, 0), //
	// non-SI definitions
	Are("Area", "a", "100", 2, 0, 0, 0, 0, 0, 0), //
	Barn("Area", "b", "1e-28", 2, 0, 0, 0, 0, 0, 0), //
	Hectare("Area", "ha", "10000", 2, 0, 0, 0, 0, 0, 0), //
	Oersted("AuxillaryMagneticField", "Oe", "79.5774715459424", -1, 0, 0, 1, 0,
			0, 0), //
	Diopter("Curvature", "D", "1", -1, 0, 0, 0, 0, 0, 0), //
	Poise("DynamicViscosity", "P", "0.1", -1, 1, -1, 0, 0, 0, 0), //
	Reotgen("ElectricChargePerMass", "R", "2.58e−4", 0, -1, 1, 1, 0, 0, 0), //
	Stokes("KinematicViscosity", "St", "1e−4", 2, 0, -1, 0, 0, 0, 0), //
	Gravity("LinearAcceleration", "G", "9.80665", 1, 0, -2, 0, 0, 0, 0), //
	Knot("LinearVelocity", "kn", "0.514444444", 1, 0, -1, 0, 0, 0, 0), //
	Lambert("Luminance", "L", "3183.098862", -2, 0, 0, 0, 0, 0, 1), //
	Stilb("Luminance", "sb", "1e4", -2, 0, 0, 0, 0, 0, 1), //
	Clo("ThermalInsulance", "clo", "0.155", 0, -1, 3, 0, 1, 0, 0), //
	BoardFoot("Volume", "Bf", "0.00235973722", 3, 0, 0, 0, 0, 0, 0), //
	Liter("Volume", "L", "0.001", 3, 0, 0, 0, 0, 0, 0) //
	;

	private UnitMap derivedMap = new UnitMap();
	private String name;
	private String symbol;
	private String quantityKind;
	private String conversionMultiplier;

	private DerivedUnit(String quantityKind, String symbol,
			String conversionMultiplier, int m, int kg, int s, int A, int K,
			int mol, int cd) {
		this.name = quantityKind + UnitUtil.NAME_DELIMITER + symbol;
		this.quantityKind = quantityKind;
		this.symbol = symbol;
		this.conversionMultiplier = conversionMultiplier;

		if (m != 0)
			derivedMap.put(BaseUnit.m.getName(), m);
		if (kg != 0)
			derivedMap.put(BaseUnit.kg.getName(), kg);
		if (s != 0)
			derivedMap.put(BaseUnit.s.getName(), s);
		if (A != 0)
			derivedMap.put(BaseUnit.A.getName(), A);
		if (K != 0)
			derivedMap.put(BaseUnit.K.getName(), K);
		if (mol != 0)
			derivedMap.put(BaseUnit.mol.getName(), mol);
		if (cd != 0)
			derivedMap.put(BaseUnit.cd.getName(), cd);
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

	public UnitMap getDerivedMap() {
		return derivedMap;
	}

	public String getConversionMultiplier() {
		return conversionMultiplier;
	}

}

enum BaseUnit {
	m("Length", "m"), //
	kg("Mass", "kg"), //
	s("Time", "s"), //
	A("ElectricCurrent", "A"), //
	K("ThermodynamicTemperature", "K"), //
	mol("AmountOfSubstance", "mol"), //
	cd("LuminousIntensity", "cd");

	private String name;
	private String symbol;
	private String quantityKind;

	private BaseUnit(String quantityKind, String symbol) {
		this.name = quantityKind + UnitUtil.NAME_DELIMITER + symbol;
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