package com.sciencegadgets.shared;

import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.conversion.DerivedUnit;
import com.sciencegadgets.client.conversion.DerivedUnitSelection;

public class UnitMap extends LinkedHashMap<String, Integer> {
	
	private static final long serialVersionUID = 6852786194775839979L;

	public UnitMap() {
		super();
	}

	public UnitMap(MathNode mNode) {
		if (!"".equals(mNode.getUnitAttribute())) {
			String[] basics = UnitUtil.getUnits(mNode);
			for (String basic : basics) {
				String[] baseAndExp = basic
						.split(UnitUtil.EXP_DELIMITER_REGEX);
				this.put(baseAndExp[0], Integer.parseInt(baseAndExp[1]));
			}
		}
	}
	
	public UnitMap getQuantityKindMap() {
		UnitMap qkMap = new UnitMap();
		for (Entry<String, Integer> entry : this.entrySet()) {
				String entryQuantityKind = UnitUtil.getQuantityKind(entry
						.getKey());
				qkMap.changeValue(entryQuantityKind, entry.getValue());
			}
		return qkMap;
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
	 * Changes the value of the specified key by the specified value
	 * 
	 * @param key
	 * @param change
	 *            - positive to increase, negative to decrease value
	 * @return
	 */
	public Integer changeValue(String key, Integer change) {
		Integer thisValue = this.get(key);
		if (thisValue == null) {
			thisValue = 0;
		}
		return this.put(key, thisValue + change);
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
		return combineUnits(otherUnitMap, true);
	}

	/**
	 * This method returns a new copy of this map with values <b>decreased</b>
	 * by the value of the similar entry from the parameter map. This is useful
	 * when evaluating units of a division
	 * 
	 * @param exponent
	 *            - Integer exponent of this exponential base
	 * @param isAdditive
	 *            - combine directly by increasing unit count if true, or
	 *            combine inversely by decreasing unit count if false
	 * @return The resulting UnitMap
	 */
	public UnitMap getDivision(UnitMap denominatorUnitMap) {
		return combineUnits(denominatorUnitMap, false);
	}

	private UnitMap combineUnits(UnitMap otherUnitMap, boolean isAdditive) {
		UnitMap combinedMap = new UnitMap();
		combinedMap.putAll(this);
		int direction = isAdditive ? 1 : -1;

		for (Entry<String, Integer> otherEntry : otherUnitMap.entrySet()) {

			combinedMap.changeValue(otherEntry.getKey(),
					direction * otherEntry.getValue());
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

	/**
	 * Replaces each derived unit of this map with its base units
	 * 
	 * @return
	 */
	public UnitMap getBaseQKMap() {
		UnitMap baseQKMap = new UnitMap();
		UnitMap qkMap = this.getQuantityKindMap();
		qkMap.remove(UnitUtil.PREFIX_QUANTITY_KIND);
		
		a:for (Entry<String, Integer> entry : qkMap.entrySet()) {
			for(DerivedUnit derivedUnit :DerivedUnit.values()) {
				if (derivedUnit.getQuantityKind().equals(entry.getKey())) {
					UnitMap derivedQKMap = derivedUnit.getDerivedMap().getQuantityKindMap();
					baseQKMap = baseQKMap.getMultiple(derivedQKMap);
					continue a;
				}
			}
			//Not derived unit, put it strait in
			baseQKMap.put(entry.getKey(), entry.getValue());
		}
		return baseQKMap;
	}

	/**
	 * Breaks down all derived units into their base units before comparing
	 * @param otherMap
	 * @return
	 */
	public boolean isConvertableTo(UnitMap otherMap) {
		return this.getBaseQKMap().equals(otherMap.getBaseQKMap());
	}
}
