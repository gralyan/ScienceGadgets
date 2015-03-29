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

public enum CommonVariables {
	
	//Latin
Area("A", "Length^2", null),//
Acceleration("a", "Length^1*Time^-2", null),//
MagneticFluxDensity("B", "MagneticField^1", "Mass^1*Time^-2*ElectricCurrent^-1"),//
Capacitance("C", "Capacitance^1", "Length^-2*Mass^-1*Time^4*ElectricCurrent^2"),//
HeatCapacity("C", "Length^2*Mass^1*Time^-2*Temperature^-1", null),//
SpecificHeatCapacity("c", "Length^2*Time^-2*Temperature^-1", null),//
ViscousDampingCoefficient("c", "Mass^1*Time^-1", null),//
ElectricDisplacementField("D", "ElectricCharge^1*Length^-2", "Time^1*ElectricCurrent^1*Length^-2"),//
Distance("d", "Length^1", null),//
Diameter("d", "Length^1", null),//
ElectricField("E", "Force^1*ElectricCharge*-1", "Length^1*Mass^1*Time^-3*ElectricCurrent^-1"),//
Energy("E", "Energy^1", "Length^2*Mass^1*Time^-2"),//
Eccentricity("e", "", null),//
Force("F", "Force^1", "Length^1*Mass^1*Time^-2"),//
Frequency("f", "Frequency^1", "Time^-1"),//
Friction("f", "Force^1", "Length^1*Mass^1*Time^-2"),//
MagneticField("H", "ElectricCurrent^1*Length^-1", null),//
Hamiltonian("H", "Force^1", "Length^1*Mass^1*Time^-2"),//
Height("h", "Length^1",null ),//
Action("I", "Energy^1*Time^1", "Length^2*Mass^1*Time^-1"),//
Intensity("I", "Power^1*Length^-2", "Mass^1*Time^-3"),//
ElectricCurrent("I", "ElectricCurrent", null),//
MomentOfInertia("I", "Mass^1*Length^2", null),//
CurrentDensity("J", "ElectricCurrent^1*Length^-2", null),//
KineticEnergy("KE", "Energy^1", null),//
Wavenumber("k", "Length^-1", null),//
Luminosity("L", "Power^1", null),//
AngularMomentum("L", "Force^1*Length^1*Time^1", "Mass^1*Length^2*Time^-1"),//
Length("l", "Length^1", null),//
Magnetization("M", "ElectricCurrent^1*Length^-1", null),//
MomentOfForce("M", "Force^1*Length^1", null),//
Mass("m", "Mass^1", null),//
RefractiveIndex("n", "", null),//
PrincipalQuantumNumber("n", "", null),//
Power("P", "Power^1", null),//
Momentum("p", "Mass^1*Length^1*Time^-1", null),//
Pressure("p", "ForcePerArea^1", "Length^-1*Mass^1*Time^-2"),//
ElectricCharge("Q", "ElectricCharge^1", "Time^1*ElectricCurrent^1"),//
Heat("Q", "Energy^1", "Length^2*Mass^1*Time^-2"),//
ElectricalResistance("R", "Resistance^1", "Length^2*Mass^1*Time^-3*ElectricCurrent^-2"),//
Radius("R", "Length^1", null),//
SurfaceArea("S", "Length^2", null),//
Entropy("S", "Energy^1*Temperature^-1", "Length^2*Mass^1*Time^-2*Temperature^-1"),//
ArcLength("s", "Length^1", null),//
Period("T", "Time^1", null),//
Temperature("T", "Temperature^1", null),//
Time("t", "Time^1", null),//
FourVelocity("U", "Velocity^1", "Length^1*Time^-1"),//
PotentialEnergy("U", "Energy^1", "Length^2*Mass^1*Time^-2"),//
InternalEnergy("U", "Energy^1", "Length^2*Mass^1*Time^-2"),//
RelativisticMass("u", "Mass^1", null),//
EnegyDensity("u", "Energy^1*Length^-3", "Length^-1*Mass^1*Time^-2"),//
Voltage("V", "EnergyPerElectricCharge^1", "Length^2*Mass^1*Time^-3*ElectricCurrent^-1"),//
Volume("V", "Length^3", null),//
Velocity("v", "Velocity^1", "Length^1*Time^-1"),//
Work("W", "Energy^1", "Length^2*Mass^1*Time^-2"),//
Width("w", "Length^1", null),//
Displacement("x", "Length^1", null),//
ElectricalImpedance("Z", "Resistance^1", "Length^2*Mass^1*Time^-3*ElectricCurrent^-2"),//
AtomicNumber("z", "", null),//

//Greek
AngularAcceleration("α", "Angle^1*Time^2", null),//
Permittivity("ε", "Capacitance^1*Length^-1", "Length^-3*Mass^-1*Time^4*ElectricCurrent^2"),//
Viscosity("η", "ForcePerArea^1*Time^1", "Length^-1*Mass^1*Time^-1"),//
Angle("θ", "Angle^1", null),//
TorsionCoefficient("Κ", "Force^1*Length^1*Angle^-1", "Length^2*Mass^1*Time^-2*Angle^-1"),//
CosmologicalConstant("Λ", "Time^-2", null),//
Wavelength("λ", "Length^1", null),//
MagneticMoment("μ", "ElectricCurrent^1*Length^2", null),//
FrictionCoefficient("μ", "", null),//
DynamicViscosity("μ", "ForcePerArea^1*Time^1", "Length^-1*Mass^1*Time^-1"),//
ElectromagneticPermeability("μ", "Inductance^1*Length^-1", "Length^1*Mass^1*Time^-2*ElectricCurrent^-2"),//
PhaseVelocity("ν", "Frequency^1", "Time^-1"),//
KinematicViscosity("ν", "Length^2*Time^-1", null),//
Density("ρ", "Mass^1*Length^-3", null),//
ChargeDensityLine("λq", "ElectricCharge^1*Length^-1", "Time^1*ElectricCurrent^1*Length^-1"),//
ChargeDensitySurface("σq", "ElectricCharge^1*Length^-2", "Time^1*ElectricCurrent^1*Length^-2"),//
ChargeDensityVolume("ρq", "ElectricCharge^1*Length^-3", "Time^1*ElectricCurrent^1*Length^-3"),//
Resistivity("ρ", "Resistance^1*Length^1", "Length^3*Mass^1*Time^-3*ElectricCurrent^-2"),//
Conductivity("σ", "ElectricConductivity^1*Length^-1", "Length^-3*Mass^-1*Time^3*ElectricCurrent^2"),//
Torque("τ", "Force^1*Length^1", "Length^2*Mass^1*Time^-2"),//
MagneticFlux("Φ", "MagneticFlux^1", "Length^2*Mass^1*Time^-2*ElectricCurrent^-1"),//
AngularFrequency("ω", "Angle^1*Time^-1", null),//
;

	String symbol;
	String unitAttribute;
	String baseUnitAttribute;

	CommonVariables(String symbol, String unitAttribute,
			String baseUnitAttribute) {
		this.symbol = symbol;
		this.unitAttribute = unitAttribute;
		this.baseUnitAttribute = baseUnitAttribute;
	}

	public String getSymbol() {
		return symbol;
	}

	public UnitAttribute getUnitAttribute() {
		return new UnitAttribute(unitAttribute);
	}

	public UnitAttribute getBaseUnitAttribute() {
		if (baseUnitAttribute != null) {
			return new UnitAttribute(baseUnitAttribute);
		} else {
			return getUnitAttribute();
		}
	}

	public UnitMap getUnitMap() {
		return new UnitMap(getUnitAttribute());
	}
	
	public UnitMap getBaseUnitMap() {
		return new UnitMap(getBaseUnitAttribute());
	}
}
