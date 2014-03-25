package com.sciencegadgets.client.conversion;

import com.sciencegadgets.shared.UnitAttribute;
import com.sciencegadgets.shared.UnitMap;
import com.sciencegadgets.shared.UnitName;

public enum DerivedUnit {// ("conversion", m, kg, s, A, K, mol, cd)

	// SI definitions
	Capacitance_F("Farad","1", "Length_m^-2*Mass_kg^-1*Time_s^4*ElectricCurrent_A^2"),//
	CatalyticActivity_kat("Katal","1", "Time_s^-1*AmountOfSubstance_mol^1"),//
	ElectricCharge_C("Coulomb","1", "Time_s^1*ElectricCurrent_A^1"),//
	ElectricConductivity_S("Siemens","1", "Length_m^-2*Mass_kg^-1*Time_s^3*ElectricCurrent_A^2"),//
	EnergyAndWork_J("Joule","1", "Length_m^2*Mass_kg^1*Time_s^-2"),//
	EnergyPerElectricCharge_V("Volt","1", "Length_m^2*Mass_kg^1*Time_s^-3*ElectricCurrent_A^-1"),//
	Force_N("Newton","1", "Length_m^1*Mass_kg^1*Time_s^-2"),//
	ForcePerArea_Pa("Pascal","1", "Length_m^-1*Mass_kg^1*Time_s^-2"),//
	Frequency_Hz("Hertz","1", "Time_s^-1"),//
	Inductance_H("Henry","1", "Length_m^2*Mass_kg^1*Time_s^-2*ElectricCurrent_A^-2"),//
	LuminousFlux_lm("Lumen","1", "LuminousIntensity_cd^1"),//
	LuminousFluxPerArea_lx("Lux","1", "Length_m^-2*LuminousIntensity_cd^1"),//
	MagneticField_T("Tesla","1", "Mass_kg^1*Time_s^-2*ElectricCurrent_A^-1"),//
	MagneticFlux_Wb("Weber","1", "Length_m^2*Mass_kg^1*Time_s^-2*ElectricCurrent_A^-1"),//
	Power_W("Watt","1", "Length_m^2*Mass_kg^1*Time_s^-3"),//
	Radioactivity_Bq("Becquerel","1", "Time_s^-1"),//
	Resistance_ohm("Ohm","1", "Length_m^2*Mass_kg^1*Time_s^-3*ElectricCurrent_A^-2"),//
	SpecificEnergy_Gy("Gray","1", "Length_m^2*Time_s^-2"),//
	SpecificEnergy_Sv("Sievert","1", "Length_m^2*Time_s^-2"),//

	// non-SI definitions
	Area_a("Are","100", "Length_m^2"),//
	Area_b("Barn","1e-28", "Length_m^2"),//
	Area_ha("Hectare","10000", "Length_m^2"),//
	AuxillaryMagneticField_Oe("Oersted","79.5774715459424", "Length_m^-1*ElectricCurrent_A^1"),//
	Curvature_D("Diopter","1", "Length_m^-1"),//
	DynamicViscosity_P("Poise","0.1", "Length_m^-1*Mass_kg^1*Time_s^-1"),//
	ElectricChargePerMass_R("Reotgen","2.58e−4", "Mass_kg^-1*Time_s^1*ElectricCurrent_A^1"),//
	KinematicViscosity_St("Stokes","1e−4", "Length_m^2*Time_s^-1"),//
	LinearAcceleration_G("Gravity","9.80665", "Length_m^1*Time_s^-2"),//
	LinearVelocity_kn("Knot","0.514444444", "Length_m^1*Time_s^-1"),//
	Luminance_L("Lambert","3183.098862", "Length_m^-2*LuminousIntensity_cd^1"),//
	Luminance_sb("Stilb","1e4", "Length_m^-2*LuminousIntensity_cd^1"),//
	ThermalInsulance_clo("Clo","0.155", "Mass_kg^-1*Time_s^3*ThermodynamicTemperature_K^1"),//
	Volume_Bf("BoardFoot","0.00235973722", "Length_m^3"),//
	Volume_L("Liter","0.001", "Length_m^3");//

	private final UnitName unitName;
	private final String symbol;
	private final String quantityKind;
	private final String conversionMultiplier;
	private final String name;
	private final String derivedUnitAttribute;

	private DerivedUnit(String name, String conversionMultiplier, String derivedUnitAttribute) {
		this.unitName = new UnitName(toString());
		this.quantityKind = unitName.getQuantityKind();
		this.symbol = unitName.getSymbol();
		this.conversionMultiplier = conversionMultiplier;
		this.name = name;
		this.derivedUnitAttribute = derivedUnitAttribute;
	}

	
	 public UnitName getUnitName() {
	 return unitName;
	 }
	
	 public String getSymbol() {
	 return symbol;
	 }

	public String getQuantityKind() {
		return quantityKind;
	}

	public UnitMap getDerivedMap() {
		return new UnitMap(getDerivedUnitAttribute());
	}
	
	public UnitAttribute getDerivedUnitAttribute() {
		return new UnitAttribute(derivedUnitAttribute);
	}

	public String getConversionMultiplier() {
		return conversionMultiplier;
	}
	
	public String getName() {
		return name;
	}

}