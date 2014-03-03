package com.sciencegadgets.client.conversion;

import com.sciencegadgets.shared.UnitMap;
import com.sciencegadgets.shared.UnitUtil;

public enum DerivedUnit {// ("conversion", m, kg, s, A, K, mol, cd)

	// SI definitions
	/* Farad */Capacitance_F("1", -2, -1, 4, 2, 0, 0, 0),
	/* Katal */CatalyticActivity_kat("1", 0, 0, -1, 0, 0, 1, 0),
	/* Coulomb */ElectricCharge_C("1", 0, 0, 1, 1, 0, 0, 0),
	/* Siemens */ElectricConductivity_S("1", -2, -1, 3, 2, 0, 0, 0),
	/* Joule */EnergyAndWork_J("1", 2, 1, -2, 0, 0, 0, 0),
	/* Volt */EnergyPerElectricCharge_V("1", 2, 1, -3, -1, 0, 0, 0),
	/* Newton */Force_N("1", 1, 1, -2, 0, 0, 0, 0),
	/* Pascal */ForcePerArea_Pa("1", -1, 1, -2, 0, 0, 0, 0),
	/* Hertz */Frequency_Hz("1", 0, 0, -1, 0, 0, 0, 0),
	/* Henry */Inductance_H("1", 2, 1, -2, -2, 0, 0, 0),
	/* Lumen */LuminousFlux_lm("1", 0, 0, 0, 0, 0, 0, 1),
	/* Lux */LuminousFluxPerArea_lx("1", -2, 0, 0, 0, 0, 0, 1),
	/* Tesla */MagneticField_T("1", 0, 1, -2, -1, 0, 0, 0),
	/* Weber */MagneticFlux_Wb("1", 2, 1, -2, -1, 0, 0, 0),
	/* Watt */Power_W("1", 2, 1, -3, 0, 0, 0, 0),
	/* Becquerel */Radioactivity_Bq("1", 0, 0, -1, 0, 0, 0, 0),
	/* Ohm */Resistance_ohm("1", 2, 1, -3, -2, 0, 0, 0),
	/* Gray */SpecificEnergy_Gy("1", 2, 0, -2, 0, 0, 0, 0),
	/* Sievert */SpecificEnergy_Sv("1", 2, 0, -2, 0, 0, 0, 0),

	// non-SI definitions
	/* Are */Area_a("100", 2, 0, 0, 0, 0, 0, 0),
	/* Barn */Area_b("1e-28", 2, 0, 0, 0, 0, 0, 0),
	/* Hectare */Area_ha("10000", 2, 0, 0, 0, 0, 0, 0),
	/* Oersted */AuxillaryMagneticField_Oe("79.5774715459424", -1, 0, 0, 1, 0,
			0, 0),
	/* Diopter */Curvature_D("1", -1, 0, 0, 0, 0, 0, 0),
	/* Poise */DynamicViscosity_P("0.1", -1, 1, -1, 0, 0, 0, 0),
	/* Reotgen */ElectricChargePerMass_R("2.58e−4", 0, -1, 1, 1, 0, 0, 0),
	/* Stokes */KinematicViscosity_St("1e−4", 2, 0, -1, 0, 0, 0, 0),
	/* Gravity */LinearAcceleration_G("9.80665", 1, 0, -2, 0, 0, 0, 0),
	/* Knot */LinearVelocity_kn("0.514444444", 1, 0, -1, 0, 0, 0, 0),
	/* Lambert */Luminance_L("3183.098862", -2, 0, 0, 0, 0, 0, 1),
	/* Stilb */Luminance_sb("1e4", -2, 0, 0, 0, 0, 0, 1),
	/* Clo */ThermalInsulance_clo("0.155", 0, -1, 3, 0, 1, 0, 0),
	/* BoardFoot */Volume_Bf("0.00235973722", 3, 0, 0, 0, 0, 0, 0),
	/* Liter */Volume_L("0.001", 3, 0, 0, 0, 0, 0, 0);

	private final UnitMap derivedMap = new UnitMap();
	private final String unitName;
	private final String symbol;
	private final String quantityKind;
	private final String conversionMultiplier;

	private DerivedUnit(String conversionMultiplier, int m, int kg, int s,
			int A, int K, int mol, int cd) {
		this.unitName = toString();
		this.quantityKind = UnitUtil.getQuantityKind(unitName);
		this.symbol = UnitUtil.getSymbol(unitName);
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

	
	 public String getUnitName() {
	 return unitName;
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