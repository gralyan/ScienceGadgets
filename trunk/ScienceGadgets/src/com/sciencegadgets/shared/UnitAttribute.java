package com.sciencegadgets.shared;

public class UnitAttribute {

	public static final String BASE_DELIMITER = "*";
	public static final String BASE_DELIMITER_REGEX = "\\*";
	public static final String EXP_DELIMITER = "^";
	public static final String EXP_DELIMITER_REGEX = "\\^";
	public static final String NAME_DELIMITER = "_";
	public static final String PREFIX_QUANTITY_KIND = "Prefix";
	public static final String PREFIXBINARY_QUANTITY_KIND = "PrefixBinary";

	private String unitAttribute;
	
	/**
	 * {@link UnitAttribute} <b>attribute = </b>UnitMultiple*UnitMultiple... <br/>
	 * <br/>
	 * {@link UnitMultiple} <b>Unit =</b> quantityKind_symbol^exp <br/>
	 * {@link UnitMultiple} <b>Quantity =</b> quantityKind^exp <br/>
	 * <br/>
	 * examples: <br/>
	 * <b>liters:</b> Volume_L^1 <br/>
	 * <b>meters per seconds squared:</b> Length_m^1*Time_s^-2
	 */
	public UnitAttribute(String unitAttribute) {
		this.unitAttribute = unitAttribute;
	}

	@Override
	public String toString() {
		return unitAttribute + "";
	}

	public void setString(String unitAttribute) {
		this.unitAttribute = unitAttribute;
	}
	
	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}

	public UnitMultiple[] getUnitMultiples() {
		if("".equals(toString())) {
			return new UnitMultiple[0];
		}
		String[] multiples = unitAttribute
				.split(UnitAttribute.BASE_DELIMITER_REGEX);
		int multipleCount = multiples.length;

		UnitMultiple[] unitMultiples = new UnitMultiple[multipleCount];
		for (int i = 0; i < multipleCount; i++) {
			unitMultiples[i] = new UnitMultiple(multiples[i]);
		}
		return unitMultiples;
	}
}
