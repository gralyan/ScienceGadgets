package com.sciencegadgets.client.conversion;

import com.sciencegadgets.shared.UnitAttribute;
import com.sciencegadgets.shared.UnitMap;

public enum Constant {// ("symbol","value", m, kg, s, A, K, mol, cd)

	// Math
	EULER("Euler's", "e", "2.71828182845904523536028747135266249", ""),//
	PI("Archimedes'", "π", "3.14159265358979323846264338327950288", ""),//
	SQRT2("Pythagoras'", "√2", "1.41421356237309504880168872420969807", ""),//
	SQRT3("Theodorus'", "√3", "1.73205080756887729352744634150587236", ""),//
	SQRT5("square root 5", "√5", "2.23606797749978969640917366873127623", ""),//

	// Universal
	SPEED_OF_LIGHT("speed of light in vacuum", "c", "299792458", "Length_m^1*Time_s^-1"),//
	NEWTONIAN_GRAVITATION("Newtonian gravitation", "G", "6.67384E-11", "Length_m^3*Mass_kg^-1*Time_s^-2"),//
	PLANK("Planck", "h", "6.62606957E-34", "Length_m^2*Mass_kg^1*Time_s^-1"),//
	PLANK_REDUCED("reduced Planck", "ħ", "1.054571726E-34", "Length_m^2*Mass_kg^1*Time_s^-1"),//
	
	// Adopted
	JOSEPHSON_90("conventional Josephson", "KJ-90", "4.835979E+14", "Length_m^-2*Mass_kg^-1*Time_s^2*ElectricCurrent_A^1"),//
	KLITZING_90("conventional von Klitzing", "R K-90", "25812.807", "Length_m^2*Mass_kg^1*Time_s^-3*ElectricCurrent_A^-2"),//
	MOLAR_MASS("molar mass", "Mu", "1E-3", "Mass_kg^1*AmountOfSubstance_mol^-1"),//
	ACCELERATION_GRAVITY("acceleration of gravity on Earth", "gn", "9.80665", "Length_m^1*Time_s^-2"),//
	ATMOSPHERE_STANDARD("standard atmosphere", "atm", "101325", "Length_m^-1*Mass_kg^1*Time_s^-2"),//
	
	// Electromagnetic
	MAGNETIC_VACUUM("vacuum permeability", "μ0", "1.256637061E-6", "Length_m^1*Mass_kg^1*Time_s^-2*ElectricCurrent_A^-2"),//
	Electric_VACUUM("vacuum permittivity", "ε0", "8.854187817E-12", "Length_m^-3*Mass_kg^-1*Time_s^4*ElectricCurrent_A^2"),//
	IMPEDANCE_VACUUM("characteristic impedance of vacuum", "Z0", "376.730313461", "Length_m^2*Mass_kg^1*Time_s^-3*ElectricCurrent_A^-2"),//
	COULOMB("Coulomb's", "Z0", "8.987551787E+9", "Length_m^3*Mass_kg^1*Time_s^-3*ElectricCurrent_A^-1"),//
	E_CHARGE("elementary charge", "e", "1.602176565E-19", "Time_s^1*ElectricCurrent_A^1"),//
	BHOR_MAGNETON("Bohr magneton", "μB", "9.27400968E-24", "Length_m^2*ElectricCurrent_A^1"),//
	CONDUCTANCE_QUANTUM("conductance quantum", "G0", "7.7480917346E-5", "Length_m^-2*Mass_kg^-1*Time_s^3*ElectricCurrent_A^2"),//
	JOSEPHSON("Josephson", "KJ", "4.83597870E+14", "Length_m^-2*Mass_kg^-1*Time_s^2*ElectricCurrent_A^1"),//
	MAGNETIC_FLUX_QUANTUM("magnetic flux quantum", "φ0", "2.067833758E-15", "Length_m^2*Mass_kg^1*Time_s^-2*ElectricCurrent_A^-1"),//
	NUCLEAR_MAGNETON("nuclear magneton", "μN", "5.05078353E-27", "Length_m^2*ElectricCurrent_A^1"),//
	KLITZING("von Klitzing", "RK", "25812.8074434", "Length_m^2*Mass_kg^1*Time_s^-3*ElectricCurrent_A^-2"),//
	
	//Atomic and Nuclear
	BHOR_RADIUS("Bohr radius", "a0", "5.2917721092E-11", "Length_m^1"),//
	ELECTRON_RADIUS_CLASSIC("classical electron radius", "re", "2.8179403267E-15", "Length_m^1"),//
	ELECTRON_MASS("electron mass", "me", "9.10938291E-31", "Mass_kg^1"),//
	FINE_STRUCTURE("fine-structure", "α", "7.2973525698E-3","" ),//
	HARTREE("Hartree energy", "Eh", "	4.35974434E-18", "Length_m^2*Mass_kg^1*Time_s^-2"),//
	PROTON_MASS("proton mass", "mp", "1.672621777E-27", "Mass_kg^1"),//
	RYDBERG("Rydberg", "R∞", "10973731.568539", "Length_m^-1"),//
	
	// Physical/Chemical
	ATOMIC_MASS_UNIT("Atomic mass unit", "mu", "1.660538921E-27", "Mass_kg^1"),//
	AVOGADRO("Avogadro's", "NA", "6.02214129E+23", "AmountOfSubstance_mol^-1"),//
	BOLTZMANN("Boltzmann", "k", "1.3806488E-23", "Length_m^2*Mass_kg^1*Time_s^-2*ThermodynamicTemperature_K^-1"),//
	FARADAY("Faraday", "F", "96485.3365", "Time_s^1*ElectricCurrent_A^1*AmountOfSubstance_mol^-1"),//
	FIRST_RADIATION("first radiation constant", "c1", "3.74177153E-16", "Length_m^4*Mass_kg^1*Time_s^-2*ElectricCurrent_A^-1"),//
	LOSCHMIDT("Loschmidt", "n0", "2.6867805E+25", "Length_m^-3"),//
	IDEAL_GAS("ideal gas", "R", "8.3144621", "Length_m^2*Mass_kg^1*Time_s^-2*ThermodynamicTemperature_K^-1*AmountOfSubstance_mol^-1"),//
	MOLAR_PLANK("molar Planck", "NAh", "3.9903127176E-10", "Length_m^2*Mass_kg^1*Time_s^-1*AmountOfSubstance_mol^-1"),//
	SECOND_RADIATION("second radiation", "c2", "1.4387770E-2", "Length_m^1*ThermodynamicTemperature_K^1"),//
	STEFAN_BOLTZMANN("Stefan–Boltzmann", "σ", "5.670373E-8", "Mass_kg^1*Time_s^-2*ElectricCurrent_A^-1*ThermodynamicTemperature_K^-4"),//
	WIEN_DISPLACEMENT("Wien displacement", "b", "2.8977721E-3", "Length_m^1*ThermodynamicTemperature_K^1"),//
	
	// Plank/Natural units
	PLANK_LENGTH("Plank length", "lP", "1.616199E-35", "Length_m^1"),//
	PLANK_MASS("Plank mass", "mP", "2.17651E-8", "Mass_kg^1"),//
	PLANK_TIME("Plank time", "tP", "5.39106E-44", "Time_s^1"),//
	PLANK_CHARGE("Plank charge", "qP", "1.875545956E-18", "Time_s^1*ElectricCurrent_A^1"),//
	PLANK_TEMPERATURE("Plank temperature", "TP", "1.416833E+32", "ThermodynamicTemperature_K^1"),//
	;
	
	private final String name;
	private final String symbol;
	private final String value;
	private final String unitAttribute;

	private Constant(String name, String symbol, String value, String unitAttribute) {
		this.name = name;
		this.symbol = symbol;
		this.value = value;
		this.unitAttribute = unitAttribute;
	}
	
	public String getName() {
		return name;
	}
	public String getSymbol() {
		return symbol;
	}
	
	public String getValue() {
		return value;
	}
	public UnitMap getUnitMap() {
		return new UnitMap(getUnitAttribute());
	}
	public UnitAttribute getUnitAttribute() {
		return new UnitAttribute(unitAttribute);
	}
}
