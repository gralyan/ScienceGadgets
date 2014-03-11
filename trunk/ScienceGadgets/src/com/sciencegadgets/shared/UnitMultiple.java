package com.sciencegadgets.shared;

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
		return new UnitName(unitMultiple.split(UnitAttribute.EXP_DELIMITER_REGEX)[0]);
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
		if(obj instanceof UnitMultiple) {
			if(unitMultiple.equals(((UnitMultiple)obj).toString())) {
				return true;
			}else {
				return false;
			}
		} else {
			return false;
		}
	}
}