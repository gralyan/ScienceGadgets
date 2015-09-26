package com.sciencegadgets.shared.dimensions;

public enum QuantityKindEnum {

	Acceleration("0,1,0,-2,0,0,0,0"), //
	AmountOfSubstance("0,0,0,0,0,0,1,0"), //
	Angle("1,0,0,0,0,0,0,0"), //
	Area("0,2,0,0,0,0,0,0"), //
	AuxillaryMagneticField("0,-1,0,0,1,0,0,0"), //
	Capacitance("0,-1,0,2,0,0,0,0"), //
	CatalyticActivity("0,0,0,-1,0,0,1,0"), //
	Curvature("0,-1,0,0,0,0,0,0"), //
	Dimensionless("1,0,0,0,0,0,0,0"), //
	DynamicViscosity("0,-1,1,-1,0,0,0,0"), //
	ElectricCharge("0,1.5,0.5,-1,0,0,0,0"), //
	ElectricChargePerMass("0,0,-1,1,1,0,0,0"), //
	ElectricConductivity("0,-1,0,1,0,0,0,0"), //
	ElectricCurrent("0,1.5,0.5,-2,0,0,0,0"), //
	ElectricDipoleMoment("0,1,0,1,1,0,0,0"), //
	Energy("0,2,1,-2,0,0,0,0"), //
	EnergyPerElectricCharge("0,0.5,0.5,-1,0,0,0,0"), //
	Force("0,1,1,-2,0,0,0,0"), //
	ForcePerArea("0,-1,1,-2,0,0,0,0"), //
	Frequency("0,0,0,-1,0,0,0,0"), //
	Inductance("0,-1,0,2,0,0,0,0"), //
	InformationEntropy("1,0,0,0,0,0,0,0"), //
	KinematicViscosity("0,2,0,-1,0,0,0,0"), //
	Length("0,1,0,0,0,0,0,0"), //
	Luminance("0,-2,0,0,0,0,0,1"), //
	LuminousFlux("1,0,0,0,0,0,0,1"), //
	LuminousFluxPerArea("1,-2,0,0,0,0,0,1"), //
	LuminousIntensity("0,0,0,0,0,0,0,1"), //
	MagneticField("0,-1.5,0.5,0,0,0,0,0"), //
	MagneticFlux("0,0.5,0.5,0,0,0,0,0"), //
	MagnetomotiveForce("1,0,0,0,1,0,0,0"), //
	Mass("0,0,1,0,0,0,0,0"), //
	Power("0,2,1,-3,0,0,0,0"), //
	Prefix("1,0,0,0,0,0,0,0"), //
	PrefixBinary("1,0,0,0,0,0,0,0"), //
	Radioactivity("0,0,0,-1,0,0,0,0"), //
	Resistance("0,-1,0,1,0,0,0,0"), //
	SolidAngle("2,0,0,0,0,0,0,0"), //
	SpecificEnergy("0,2,0,-2,0,0,0,0"), //
	Temperature("0,0,0,0,0,1,0,0"), //
	ThermalInsulance("0,0,-1,3,0,1,0,0"), //
	Time("0,0,0,1,0,0,0,0"), //
	Velocity("0,1,0,-1,0,0,0,0"), //
	Volume("0,3,0,0,0,0,0,0");

	private String dimension;

	private QuantityKindEnum(String dimension) {
		this.dimension = dimension;
	}

	public String getDimension() {
		return dimension;
	}

	public String getFormattedName() {
		String preFormatted = toString();
		String formatted = ""+preFormatted.charAt(0);
		
		String space = "";
		for(int i = 1 ; i<preFormatted.length() ; i++) {
			char cur = preFormatted.charAt(i);
			space = Character.isUpperCase(cur) ?" ":"";
			formatted = formatted +space+ cur;
			
		}
		
		return formatted;
	}

}
