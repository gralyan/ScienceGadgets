package com.sciencegadgets.shared;

public class UnitName {

	String unitName;

	public UnitName(String unitName) {
		this.unitName = unitName;
	}

	@Override
	public String toString() {
		return unitName;
	}

	public String getSymbol() {
		String[] parts = unitName.split(UnitAttribute.NAME_DELIMITER);
		if(parts.length >1) {
			return parts[1];
		}else {
			return "";
		}
	}

	public String getQuantityKind() {
		return unitName.split(UnitAttribute.NAME_DELIMITER)[0];
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof UnitName) {
			if (unitName.equals(((UnitName) obj).toString())) {
//				System.out.println(toString()+" == "+((UnitName) obj).toString());
				return true;
			} else {
//				System.out.println(toString()+" != "+((UnitName) obj).toString());
				return false;
			}
		} else {
//			System.out.println(3+obj.getClass().getName());
			return false;
		}
	}
}