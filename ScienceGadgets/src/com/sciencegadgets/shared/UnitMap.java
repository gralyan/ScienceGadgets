package com.sciencegadgets.shared;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.sciencegadgets.client.algebra.MathTree.MathNode;

public class UnitMap extends LinkedHashMap<String, Integer> {
	private static final long serialVersionUID = 6852786194775839979L;

	public UnitMap() {
		super();
	}

	public UnitMap(MathNode mNode) {
		this(mNode, false);
	}

	public UnitMap(MathNode mNode, boolean isOnlyQuantityKind) {
		this();


		if (isOnlyQuantityKind) {
			UnitMap unitMap = new UnitMap(mNode);
			for (Entry<String, Integer> entry : unitMap.entrySet()) {
				String entryQuantityKind = UnitUtil.getQuantityKind(entry
						.getKey());
				Integer quantityKindEntryValue = this.get(entryQuantityKind);
				if (quantityKindEntryValue == null) {
					quantityKindEntryValue = 0;
				}
				this.put(entryQuantityKind, entry.getValue()
						+ quantityKindEntryValue);
			}
		} else {

			if (!"".equals(mNode.getUnitAttribute())) {
				String[] basics = UnitUtil.getUnits(mNode);
				for (String basic : basics) {
					String[] baseAndExp = basic
							.split(UnitUtil.EXP_DELIMITER_REGEX);
					this.put(baseAndExp[0], Integer.parseInt(baseAndExp[1]));
				}
			}
		}
	}

	@Override
	public Integer put(String key, Integer value) {
		if (value == 0) {
			return remove(key);
		} else {
			return super.put(key, value);
		}
	}

	/**
	 * This method returns a new copy of this map with values <b>increased</b>
	 * by the value of the similar entry from the parameter map. This is useful
	 * when evaluating units of a multiplication
	 * 
	 * @param otherUnitMap
	 *            - The map of values to increase these values by
	 * @return The resulting UnitMap
	 */
	public UnitMap getMultiple(UnitMap otherUnitMap) {
		return combineUnits(otherUnitMap, false);
	}

	/**
	 * This method returns a new copy of this map with values <b>decreased</b>
	 * by the value of the similar entry from the parameter map. This is useful
	 * when evaluating units of a division
	 * 
	 * @param exponent
	 *            - Integer exponent of this exponential base
	 * @return The resulting UnitMap
	 */
	public UnitMap getDivision(UnitMap denominatorUnitMap) {
		return combineUnits(denominatorUnitMap, false);
	}

	private UnitMap combineUnits(UnitMap otherUnitMap, boolean directly) {
		UnitMap combinedMap = new UnitMap();
		int direction = directly ? 1 : -1;

		for (Entry<String, Integer> otherEntry : otherUnitMap.entrySet()) {
			Integer thisValue = this.get(otherEntry.getKey());
			if (thisValue == null) {
				thisValue = 0;
			}
			combinedMap.put(otherEntry.getKey(),
					thisValue + (direction * otherEntry.getValue()));
		}
		return combinedMap;
	}

	/**
	 * This method returns a new copy of this map with values increased by
	 * multiplying it with the exponent integer. This is useful when evaluating
	 * an exponent
	 * 
	 * @param exponent
	 *            - Integer exponent of this exponential base
	 * @return The resulting UnitMap
	 */
	public UnitMap getExponential(Integer exponent) {
		UnitMap exponentiatedMap = new UnitMap();

		for (Entry<String, Integer> baseEntry : this.entrySet()) {
			exponentiatedMap.put(baseEntry.getKey(), baseEntry.getValue()
					* exponent);
		}
		return exponentiatedMap;
	}

	public String getUnitAttribute() {

		String dataUnitAttribute = "";
		for (Entry<String, Integer> unitEntry : this.entrySet()) {
			if (unitEntry.getValue() != 0) {
				dataUnitAttribute = dataUnitAttribute + UnitUtil.BASE_DELIMITER
						+ unitEntry.getKey() + UnitUtil.EXP_DELIMITER
						+ unitEntry.getValue();
			}
		}
		dataUnitAttribute = dataUnitAttribute.replaceFirst(
				UnitUtil.BASE_DELIMITER_REGEX, "");
		return dataUnitAttribute;
	}

}