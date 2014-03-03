package com.sciencegadgets.client.conversion;

import com.sciencegadgets.shared.UnitMap;

public enum Constant {// ("symbol","value", m, kg, s, A, K, mol, cd)

	// Math
	EULER("Euler's", "e", "2.71828182845904523536028747135266249", 0, 0, 0, 0, 0, 0, 0),//
	PI("Archimedes'", "π", "3.14159265358979323846264338327950288", 0, 0, 0, 0, 0, 0, 0),//
	SQRT2("Pythagoras'", "√2", "1.41421356237309504880168872420969807", 0, 0, 0, 0, 0, 0, 0),//
	SQRT3("Theodorus'", "√3", "1.73205080756887729352744634150587236", 0, 0, 0, 0, 0, 0, 0),//
	SQRT5("square root 5", "√5", "2.23606797749978969640917366873127623", 0, 0, 0, 0, 0, 0, 0),//

	// Universal
	SPEED_OF_LIGHT("speed of light in vacuum", "c", "299792458", 1, 0, -1, 0, 0, 0, 0),//
	NEWTONIAN_GRAVITATION("Newtonian gravitation", "G", "6.67384E-11", 3, -1, -2, 0, 0, 0, 0),//
	PLANK("Planck", "h", "6.62606957E-34", 2, 1, -1, 0, 0, 0, 0),//
	PLANK_REDUCED("reduced Planck", "ħ", "1.054571726E-34", 2, 1, -1, 0, 0, 0, 0),//
	
	// Adopted
	JOSEPHSON_90("conventional Josephson", "KJ-90", "4.835979E+14", -2, -1, 2, 1, 0, 0, 0),//
	KLITZING_90("conventional von Klitzing", "R K-90", "25812.807", 2, 1, -3, -2, 0, 0, 0),//
	MOLAR_MASS("molar mass", "Mu", "1E-3", 0, 1, 0, 0, 0, -1, 0),//
	ACCELERATION_GRAVITY("acceleration of gravity on Earth", "gn", "9.80665", 1, 0, -2, 0, 0, 0, 0),//
	ATMOSPHERE_STANDARD("standard atmosphere", "atm", "101325", -1, 1, -2, 0, 0, 0, 0),//
	
	// Electromagnetic
	MAGNETIC_VACUUM("vacuum permeability", "μ0", "1.256637061E-6", 1, 1, -2, -2, 0, 0, 0),//
	Electric_VACUUM("vacuum permittivity", "ε0", "8.854187817E-12", -3, -1, 4, 2, 0, 0, 0),//
	IMPEDANCE_VACUUM("characteristic impedance of vacuum", "Z0", "376.730313461", 2, 1, -3, -2, 0, 0, 0),//
	COULOMB("Coulomb's", "Z0", "8.987551787E+9", 3, 1, -3, -1, 0, 0, 0),//
	E_CHARGE("elementary charge", "e", "1.602176565E-19", 0, 0, 1, 1, 0, 0, 0),//
	BHOR_MAGNETON("Bohr magneton", "μB", "9.27400968E-24", 2, 0, 0, 1, 0, 0, 0),//
	CONDUCTANCE_QUANTUM("conductance quantum", "G0", "7.7480917346E-5", -2, -1, 3, 2, 0, 0, 0),//
	JOSEPHSON("Josephson", "KJ", "4.83597870E+14", -2, -1, 2, 1, 0, 0, 0),//
	MAGNETIC_FLUX_QUANTUM("magnetic flux quantum", "φ0", "2.067833758E-15", 2, 1, -2, -1, 0, 0, 0),//
	NUCLEAR_MAGNETON("nuclear magneton", "μN", "5.05078353E-27", 2, 0, 0, 1, 0, 0, 0),//
	KLITZING("von Klitzing", "RK", "25812.8074434", 2, 1, -3, -2, 0, 0, 0),//
	
	//Atomic and Nuclear
	BHOR_RADIUS("Bohr radius", "a0", "5.2917721092E-11", 1, 0, 0, 0, 0, 0, 0),//
	ELECTRON_RADIUS_CLASSIC("classical electron radius", "re", "2.8179403267E-15", 1, 0, 0, 0, 0, 0, 0),//
	ELECTRON_MASS("electron mass", "me", "9.10938291E-31", 0, 1, 0, 0, 0, 0, 0),//
	FINE_STRUCTURE("fine-structure", "α", "7.2973525698E-3",0, 0, 0, 0, 0, 0, 0 ),//
	HARTREE("Hartree energy", "Eh", "	4.35974434E-18", 2, 1, -2, 0, 0, 0, 0),//
	PROTON_MASS("proton mass", "mp", "1.672621777E-27", 0, 1, 0, 0, 0, 0, 0),//
	RYDBERG("Rydberg", "R∞", "10973731.568539", -1, 0, 0, 0, 0, 0, 0),//
	
	// Physical/Chemical
	ATOMIC_MASS_UNIT("Atomic mass unit", "mu", "1.660538921E-27", 0, 1, 0, 0, 0, 0, 0),//
	AVOGADRO("Avogadro's", "NA", "6.02214129E+23", 0, 0, 0, 0, 0, -1, 0),//
	BOLTZMANN("Boltzmann", "k", "1.3806488E-23", 2, 1, -2, 0, -1, 0, 0),//
	FARADAY("Faraday", "F", "96485.3365", 0, 0, 1, 1, 0, -1, 0),//
	FIRST_RADIATION("first radiation constant", "c1", "3.74177153E-16", 4, 1, -2, -1, 0, 0, 0),//
	LOSCHMIDT("Loschmidt", "n0", "2.6867805E+25", -3, 0, 0, 0, 0, 0, 0),//
	IDEAL_GAS("ideal gas", "R", "8.3144621", 2, 1, -2, 0, -1, -1, 0),//
	MOLAR_PLANK("molar Planck", "NAh", "3.9903127176E-10", 2, 1, -1, 0, 0, -1, 0),//
	SECOND_RADIATION("second radiation", "c2", "1.4387770E-2", 1, 0, 0, 0, 1, 0, 0),//
	STEFAN_BOLTZMANN("Stefan–Boltzmann", "σ", "5.670373E-8", 0, 1, -2, -1, -4, 0, 0),//
	WIEN_DISPLACEMENT("Wien displacement", "b", "2.8977721E-3", 1, 0, 0, 0, 1, 0, 0),//
	
	// Plank/Natural units
	PLANK_LENGTH("Plank length", "lP", "1.616199E-35", 1, 0, 0, 0, 0, 0, 0),//
	PLANK_MASS("Plank mass", "mP", "2.17651E-8", 0, 1, 0, 0, 0, 0, 0),//
	PLANK_TIME("Plank time", "tP", "5.39106E-44", 0, 0, 1, 0, 0, 0, 0),//
	PLANK_CHARGE("Plank charge", "qP", "1.875545956E-18", 0, 0, 1, 1, 0, 0, 0),//
	PLANK_TEMPERATURE("Plank temperature", "TP", "1.416833E+32", 0, 0, 0, 0, 1, 0, 0),//
	;
	
	private final String name;
	private final String symbol;
	private final String value;
	private UnitMap unitMap = new UnitMap();

	private Constant(String name, String symbol, String value, int m, int kg, int s,
			int A, int K, int mol, int cd) {
		this.name = name;
		this.symbol = symbol;
		this.value = value;

		if (m != 0)
			unitMap.put(BaseUnit.m.getName(), m);
		if (kg != 0)
			unitMap.put(BaseUnit.kg.getName(), kg);
		if (s != 0)
			unitMap.put(BaseUnit.s.getName(), s);
		if (A != 0)
			unitMap.put(BaseUnit.A.getName(), A);
		if (K != 0)
			unitMap.put(BaseUnit.K.getName(), K);
		if (mol != 0)
			unitMap.put(BaseUnit.mol.getName(), mol);
		if (cd != 0)
			unitMap.put(BaseUnit.cd.getName(), cd);
	}
	
	public String getSymbol() {
		return symbol;
	}
	
	public String getValue() {
		return value;
	}
	public UnitMap getUnitMap() {
		return unitMap;
	}
}
