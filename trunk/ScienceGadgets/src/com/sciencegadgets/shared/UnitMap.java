package com.sciencegadgets.shared;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.conversion.DerivedUnit;

public class UnitMap extends LinkedHashMap<UnitName, Integer> {

	private static final long serialVersionUID = 6852786194775839979L;

	public UnitMap() {
		super();
	}

	public UnitMap(MathNode mNode) {
		this(mNode.getUnitAttribute());
	}

	public UnitMap(UnitAttribute unitAttribute) {
		if (!"".equals(unitAttribute)) {
			UnitMultiple[] unitMultiples = unitAttribute.getUnitMultiples();
			for (UnitMultiple unitMultiple : unitMultiples) {
				this.put(unitMultiple.getUnitName(),
						Integer.parseInt(unitMultiple.getUnitExponent()));
			}
		}
	}

	public UnitMap getQuantityKindMap() {
		UnitMap qkMap = new UnitMap();
		for (Entry<UnitName, Integer> entry : this.entrySet()) {
			String entryQuantityKind = entry.getKey().getQuantityKind();
			qkMap.put(new UnitName(entryQuantityKind), entry.getValue());
		}
		return qkMap;
	}

	/**
	 * Changes the value of the specified key by the specified value
	 * 
	 * @param key
	 * @param change
	 *            - positive to increase, negative to decrease value
	 */
	@Override
	public Integer put(UnitName key, Integer change) {
		if("".equals(key.toString())) {
			return 0;
		}
		Entry<UnitName, Integer> entry = this.getEntry(key);
		Integer thisValue = 0;
		if(entry != null) {
			thisValue = entry.getValue();
			key = entry.getKey();
		}
		Integer newValue = thisValue + change;
		if (newValue == 0) {
			remove(key);
			return 0;
		} else {
			return super.put(key, newValue);
		}
	}

	@Override
	public Integer get(Object key) {
		if(key == null) {
			return null;
		}
		if (key instanceof UnitName) {
			Entry<UnitName, Integer> entry = getEntry((UnitName) key);
			return entry == null ? null : entry.getValue();
		} else if (key instanceof String){
			for(Entry<UnitName, Integer> entry : entrySet()) {
				if(key.equals(entry.getKey().toString())) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	public Entry<UnitName, Integer> getEntry(UnitName key) {
		if (key == null) {
			return null;
		}
		for (Entry<UnitName, Integer> entry : this.entrySet()) {
			if (entry.getKey().equals(key)) {
				return entry;
			}
		}
		return null;
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
	 * @param denominatorUnitMap
	 *            - UnitMap to divide by
	 * 
	 * @return The resulting UnitMap
	 */
	public UnitMap getDivision(UnitMap denominatorUnitMap) {
		return combineUnits(denominatorUnitMap, false);
	}

	private UnitMap combineUnits(UnitMap otherUnitMap, boolean isAdditive) {
		UnitMap combinedMap = new UnitMap();
		combinedMap.putAll(this);
		int direction = isAdditive ? 1 : -1;

		for (Entry<UnitName, Integer> otherEntry : otherUnitMap.entrySet()) {

			combinedMap.put(otherEntry.getKey(),
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

		for (Entry<UnitName, Integer> baseEntry : this.entrySet()) {
			exponentiatedMap.put(baseEntry.getKey(), baseEntry.getValue()
					* exponent);
		}
		return exponentiatedMap;
	}

	public UnitAttribute getUnitAttribute() {

		String dataUnitAttribute = "";
		for (Entry<UnitName, Integer> unitEntry : this.entrySet()) {
			if (unitEntry.getValue() != 0) {
				dataUnitAttribute = dataUnitAttribute
						+ UnitAttribute.BASE_DELIMITER + unitEntry.getKey()
						+ UnitAttribute.EXP_DELIMITER + unitEntry.getValue();
			}
		}
		dataUnitAttribute = dataUnitAttribute.replaceFirst(
				UnitAttribute.BASE_DELIMITER_REGEX, "");
		return new UnitAttribute(dataUnitAttribute);
	}

	/**
	 * Breaks down all derived units into their base units before comparing
	 * 
	 * @param otherMap
	 */
	public boolean isConvertableTo(UnitMap otherMap) {
		return this.getBaseQKMap().equals(otherMap.getBaseQKMap());
	}

	/**
	 * Returns a map with only simple base units. This is useful when complex
	 * derived units are unwanted.
	 */
	public UnitMap getBaseQKMap() {
		UnitMap baseQKMap = new UnitMap();
		UnitMap qkMap = this.getQuantityKindMap();
		
		LinkedList<UnitName> prefixes = new LinkedList<UnitName>();

		a: for (Entry<UnitName, Integer> entry : qkMap.entrySet()) {
			if(UnitAttribute.PREFIX_QUANTITY_KIND.equals(entry.getKey().toString())) {
				prefixes.add(entry.getKey());
				continue a;
			}
			for (DerivedUnit derivedUnit : DerivedUnit.values()) {
				if (derivedUnit.getQuantityKind().equals(entry.getKey().toString())) {
					UnitMap derivedQKMap = derivedUnit.getDerivedMap()
							.getQuantityKindMap();
					derivedQKMap = derivedQKMap
							.getExponential(entry.getValue());
					baseQKMap = baseQKMap.getMultiple(derivedQKMap);
					continue a;
				}
			}
			// Not derived unit, put it strait in
			baseQKMap.put(entry.getKey(), entry.getValue());
		}
		
		for(UnitName prefix : prefixes) {
			qkMap.remove(prefix);
		}
		
		return baseQKMap;
	}

}
