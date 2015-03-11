package com.sciencegadgets.shared.dimensions;

/**
 * [quantityKind] + UnitAttribute.NAME_DELIMITER + [symbol]
 * +UnitAttribute.EXP_DELIMITER_REGEX+ [exp]
 * 
 * <br/>
 * examples: <br/>
 * <b>liter:</b> Volume_L^1 <br/>
 * <b>meter squared:</b> Length_m^2
 */
public class UnitMultiple {

	String unitMultiple;

	UnitMultiple(String unitMultiple) {
		this.unitMultiple = unitMultiple;
	}

	@Override
	public String toString() {
		return unitMultiple;
	}

	public UnitName getUnitName() {
		return new UnitName(
				unitMultiple.split(UnitAttribute.EXP_DELIMITER_REGEX)[0]);
	}

	public String getUnitExponent() {
		try {
			return unitMultiple.split(UnitAttribute.EXP_DELIMITER_REGEX)[1];
		} catch (IndexOutOfBoundsException e) {
			return "1";
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnitMultiple) {
			if (toString().equals(((UnitMultiple) obj).toString())) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}