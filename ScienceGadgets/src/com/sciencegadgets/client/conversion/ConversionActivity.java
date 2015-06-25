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
package com.sciencegadgets.client.conversion;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.ui.UnitSelection;
import com.sciencegadgets.client.ui.specification.NumberSpecification;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;
import com.sciencegadgets.shared.dimensions.CommonDerivedUnits;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitHTML;
import com.sciencegadgets.shared.dimensions.UnitMap;
import com.sciencegadgets.shared.dimensions.UnitMultiple;
import com.sciencegadgets.shared.dimensions.UnitName;

public class ConversionActivity extends AbsolutePanel {

	interface ConversionUiBinder extends
			UiBinder<FlowPanel, ConversionActivity> {
	}

	private static ConversionUiBinder conversionUiBinder = GWT
			.create(ConversionUiBinder.class);

	@UiField
	FlowPanel dimensionalAnalysisArea;
	@UiField
	FlowPanel unitSelectionArea;
	@UiField
	FlowPanel deriveUnitArea;
	@UiField
	AbsolutePanel wrapperArea;
	@UiField
	Button convertButton;
	@UiField
	Label valueDisplay;

	static final UnitSelection unitSelection = new UnitSelection(false, true);
	static final SelectionPanel derivedUnitsSelection = new SelectionPanel(
			"Base Units");
	private EquationTree mTree = null;
	private EquationNode node;

	// wrapped nodes and history nodes

	LinkedList<UnitDisplay> unitDisplays = new LinkedList<UnitDisplay>();

//	private Unit selectedUnit = null;
	static ConversionWrapper selectedWrapper = null;

	private EquationNode totalNode;
	private EquationNode wrapperFraction;

	private Element workingHTML;

	public Equation variableEquation;

	public ConversionActivity() {

		add(conversionUiBinder.createAndBindUi(this));

		getElement().setAttribute("id", CSS.CONVERSION_ACTIVITY);

		unitSelection.unitBox.addSelectionHandler(new ConvertSelectHandler());
		unitSelection.addStyleName(CSS.FILL_PARENT);
		unitSelectionArea.add(unitSelection);

		derivedUnitsSelection.addSelectionHandler(new ConvertSelectHandler());
		derivedUnitsSelection.addStyleName(CSS.FILL_PARENT);
		deriveUnitArea.add(derivedUnitsSelection);

		convertButton.addClickHandler(new ConvertCompleteClickHandler());
	}

	public void setNode(EquationNode node) {
		this.node = node;
	}

	public void setVariableEquation(Equation variableEquation) {
		this.variableEquation = variableEquation;
	}

	public void load(String initialValue, UnitAttribute unitAttribute, boolean allowConvertButton) {
		unitDisplays.clear();
//		selectedUnit = null;
		selectedWrapper = null;
		convertButton.setVisible(allowConvertButton);

		mTree = new EquationTree(TypeSGET.Term, "", TypeSGET.Term, "", false);

		totalNode = mTree.getLeftSide().append(TypeSGET.Number,
				initialValue);
		mTree.getRightSide().append(totalNode.clone());
		valueDisplay.setText(initialValue);

		UnitMultiple[] bases = unitAttribute.getUnitMultiples();

		EquationNode fracHistory = mTree.newNode(TypeSGET.Fraction, "");
		EquationNode numerHistory = fracHistory.append(TypeSGET.Term, "");
		EquationNode denomHistory = fracHistory.append(TypeSGET.Term, "");

		EquationNode fracWorking = wrapperFraction = fracHistory.clone();
		EquationNode numerWorking = fracWorking.getChildAt(0);
		EquationNode denomWorking = fracWorking.getChildAt(1);

		for (UnitMultiple base : bases) {
			boolean negExp = base.toString().contains("-");
			String symbol = base.getUnitName().getSymbol();
			String exp = base.getUnitExponent().replace("-", "");

			EquationNode unitNode;
			if ("1".equals(exp)) {
				unitNode = mTree.newNode(TypeSGET.Variable, symbol);
			} else {
				unitNode = mTree.newNode(TypeSGET.Exponential, "");
				unitNode.append(TypeSGET.Variable, symbol);
				unitNode.append(TypeSGET.Number, exp);
			}
			unitNode.setAttribute(MathAttribute.Unit,
					base.toString().replace("-", ""));
			EquationNode unitNodeClone = unitNode.clone();

			if (negExp) {
				denomWorking.append(unitNodeClone);
				denomHistory.append(unitNode);
			} else {
				numerWorking.append(unitNodeClone);
				numerHistory.append(unitNode);
			}
			unitDisplays.add(new UnitDisplay(unitNodeClone, unitNode, false,
					!negExp));
		}

		EquationNode[] numAndDens = { numerHistory ,denomHistory, numerWorking,denomWorking};
		for (EquationNode numOrDen : numAndDens) {
			if (numOrDen.getChildCount() == 0) {
				numOrDen.append(TypeSGET.Number, "1");
			}
		}

		mTree.getRightSide().append(fracHistory);
		mTree.getLeftSide().append(fracWorking);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		// Dynamically fills parent
		reloadEquation();
	}

	public void reloadEquation() {
		// Make and add entire equation before moving left
		dimensionalAnalysisArea.clear();
		EquationHTML eqHTML = mTree.reloadDisplay(false, false);

		// Unit font
		eqHTML.addStyleName(CSS.UNIT);
		wrapperArea.addStyleName(CSS.UNIT);

		// Recreate wrappers
		placeWrappers();

		// Place entire equation in history area first
		eqHTML.autoFillParent = true;
		dimensionalAnalysisArea.add(eqHTML);

		// No need for equals sign
		mTree.getEquals().getHTML(true, true).removeFromParent();

		// Move left side of equation into wrapper area
		Element newWorkingHTML = mTree.getLeftSide().getHTML(false, false);
		if (workingHTML == null) {
			wrapperArea.getElement().appendChild(newWorkingHTML);
		} else {
			wrapperArea.getElement().replaceChild(newWorkingHTML, workingHTML);
		}
		workingHTML = newWorkingHTML;

		// Resize working area
		double widthRatio = (double) wrapperArea.getOffsetWidth()
				/ workingHTML.getOffsetWidth();
		double heightRatio = (double) wrapperArea.getOffsetHeight()
				/ workingHTML.getOffsetHeight();
		double smallerRatio = (widthRatio > heightRatio) ? heightRatio
				: widthRatio;
		double fontPercent = smallerRatio * 95;// *95 for looser fit
		workingHTML.getStyle().setFontSize(fontPercent,
				com.google.gwt.dom.client.Style.Unit.PCT);

		// HashMap<Parameter, String> parameterMap = new HashMap<Parameter,
		// String>();
		// parameterMap
		// .put(Parameter.activity, ActivityType.conversion.toString());
		// parameterMap.put(Parameter.equation, mTree.getMathXMLString());
		// URLParameters.setParameters(parameterMap, false);
	}

	private void placeWrappers() {
		LinkedList<Wrapper> wrappers = mTree.getWrappers();
		wrappers.clear();

		for (UnitDisplay unitDisplay : unitDisplays) {
			EquationNode jointNode = unitDisplay.historyNode;
			if (!unitDisplay.isCanceled) {
				new ConversionWrapper(unitDisplay, wrapperArea, this);
			} else {
				unitDisplay.wrappedNode.getHTML(false, false)
						.removeFromParent();

				if (TypeSGET.Number.equals(jointNode.getType())) {
					Element[] units = jointNode.getHTMLofUnits();
					for (Element unit : units) {
						unit.addClassName("lineThrough");
					}
				} else {
					jointNode.getHTML(false, false).addClassName("lineThrough");
				}
			}
		}

		for (Wrapper wrap : wrappers) {
			wrap.addAssociativeDragDrop();
			((ConversionWrapper) wrap).addUnitCancelDropControllers();
		}
	}

	void fillUnitSelection(final String unitName) {

//		DataModerator.database.getUnitQuantityKindName(unitName, new AsyncCallback<String>() {
//			@Override
//			public void onFailure(Throwable caught) {
//				JSNICalls.error("getUnit Failed: "+caught.getMessage());
//			}
//
//			@Override
//			public void onSuccess(String qkName) {
////				selectedUnit = result;
////				String quantityKind = selectedUnit.getQuantityKindName();
////				String excludedUnitName = selectedUnit.getName().toString();
//
////				unitSelection.reloadUnitBox(quantityKind, excludedUnitName);
//				unitSelection.reloadUnitBox(qkName, unitName, false);
//			}
//		});
		
		String qkName = DataModerator.getQuantityKindByUnitName(unitName);
		if(qkName != null) {
			unitSelection.reloadUnitBox(qkName, unitName, false);
		}

		derivedUnitsSelection.clear();

		try {// Deconstruct option for derived units
			CommonDerivedUnits derivedUnit = CommonDerivedUnits
					.valueOf(unitName);
			UnitAttribute dataUnitAttribute = derivedUnit.getDerivedMap()
					.getUnitAttribute();
			Element derivedUnitElement = UnitHTML.create(dataUnitAttribute,
					null, false);
			derivedUnitElement.getStyle()
					.setVerticalAlign(VerticalAlign.MIDDLE);
			String derivedUnitHTML = JSNICalls
					.elementToString(derivedUnitElement);

			derivedUnitsSelection.add(derivedUnit.getSymbol() + "="
					+ derivedUnit.getConversionMultiplier() + derivedUnitHTML,
					derivedUnit.getUnitName().toString(), derivedUnit);
		} catch (IllegalArgumentException e) {
		}
	}

	private void convert(UnitMap toMap, String toMultiplier) {

		// Keep unit exponent constant, all get cancelled
		String exp = selectedWrapper.getNode().getUnitAttribute()
				.getUnitMultiples()[0].getUnitExponent();
		int expAbs = Math.abs(Integer.parseInt(exp));
		
		Unit selectedUnit = selectedWrapper.getUnit();
		if(selectedUnit == null) {
			return;
		}

		UnitAttribute fromUnitAttribute = new UnitAttribute(
				selectedUnit.getName() + UnitAttribute.EXP_DELIMITER + 1);
		UnitMap fromMap = new UnitMap(fromUnitAttribute);
		String fromMultiplier = selectedUnit.getConversionMultiplier();
		toMap = toMap.getExponential(expAbs,1);
		fromMap = fromMap.getExponential(expAbs,1);

		boolean isSelectNum = selectedWrapper.getUnitDisplay().inNumerator;
		UnitMap newUnitMap = isSelectNum ? toMap.getDivision(fromMap) : fromMap
				.getDivision(toMap);
		String numMultiplier = isSelectNum ? fromMultiplier : toMultiplier;
		String denMultiplier = !isSelectNum ? fromMultiplier : toMultiplier;

		// History fraction of multipliers
		mTree.getRightSide().append(TypeSGET.Operation,
				Operator.getMultiply().getSign());
		EquationNode newHistoryFrac = mTree.getRightSide().append(
				TypeSGET.Fraction, "");
		EquationNode newHistoryNum = newHistoryFrac.append(TypeSGET.Sum, "");
		EquationNode newHistoryDen = newHistoryFrac.append(TypeSGET.Sum, "");

		EquationNode numMultiplierNode = mTree.newNode(TypeSGET.Number,
				numMultiplier);
		EquationNode denMultiplierNode = mTree.newNode(TypeSGET.Number,
				denMultiplier);

		if (expAbs == 1) {
			newHistoryNum.append(numMultiplierNode);
			newHistoryDen.append(denMultiplierNode);
		} else {
			EquationNode numExp = newHistoryNum
					.append(TypeSGET.Exponential, "");
			EquationNode denExp = newHistoryDen
					.append(TypeSGET.Exponential, "");
			numExp.append(numMultiplierNode);
			denExp.append(denMultiplierNode);
			numExp.append(TypeSGET.Number, expAbs + "");
			denExp.append(TypeSGET.Number, expAbs + "");
		}

		// Update Working area
		int selectedIndex = selectedWrapper.getNode().getIndex();
		selectedIndex = newUnitMap.size() == 2 ? selectedIndex : -1;
		for (Entry<UnitName, Integer> entry : newUnitMap.entrySet()) {
			UnitName unitName = entry.getKey();
			Integer unitExp = entry.getValue();
			Integer unitExpAbs = Math.abs(unitExp);
			boolean inNum = unitExp > 0;

			String numSymbol = unitName.getSymbol();

			EquationNode workingNode = mTree.newNode(TypeSGET.Variable,
					numSymbol);
			if (unitExp > 1 || unitExp < -1) {
				EquationNode workingExp = mTree.newNode(TypeSGET.Exponential,
						"");
				workingExp.append(workingNode);
				workingExp.append(TypeSGET.Number, "" + unitExpAbs);
				workingNode = workingExp;
			}
			workingNode.setAttribute(MathAttribute.Unit, unitName
					+ UnitAttribute.EXP_DELIMITER + unitExpAbs);
			EquationNode historyNode = workingNode.clone();

			int numOrDen = inNum ? 0 : 1;
			wrapperFraction.getChildAt(numOrDen).addBefore(selectedIndex,
					workingNode);
			newHistoryFrac.getChildAt(numOrDen).append(historyNode);

			unitDisplays.add(new UnitDisplay(workingNode, historyNode, fromMap
					.containsKey(unitName), inNum));
		}

		// Calculate conversion
		BigDecimal total = new BigDecimal(totalNode.getSymbol());
		for (int i = 0; i < expAbs; i++) {
			total = total.multiply(new BigDecimal(numMultiplier),
					MathContext.DECIMAL128);
			total = total.divide(new BigDecimal(denMultiplier),
					MathContext.DECIMAL128);
		}
		totalNode.setSymbol(total.stripTrailingZeros().toEngineeringString());
		valueDisplay.setText(total.stripTrailingZeros().toPlainString());

		selectedWrapper.getUnitDisplay().isCanceled = true;
		selectedWrapper.unselect();

		reloadEquation();
	}

	// /////////////////////////////////////////////////////
	// InnerClasses
	// /////////////////////////////////////////////////////

	class UnitDisplay {
		EquationNode wrappedNode;
		EquationNode historyNode;
		boolean isCanceled = false;
		boolean inNumerator = true;

		public UnitDisplay(EquationNode wrappedNode, EquationNode historyNode,
				boolean isCanceled, boolean inNumerator) {
			super();
			this.wrappedNode = wrappedNode;
			this.historyNode = historyNode;
			this.isCanceled = isCanceled;
			this.inNumerator = inNumerator;
		}
	}

	class ConvertSelectHandler implements SelectionHandler {
		@Override
		public void onSelect(Cell selected) {
			Object selectedEntity = selected.getEntity();
			if (selectedEntity instanceof CommonDerivedUnits) {
				CommonDerivedUnits deriveUnit = (CommonDerivedUnits) selectedEntity;
				convert(deriveUnit.getDerivedMap(),
						"1"/*deriveUnit.getConversionMultiplier()*/);
			} else if (selectedEntity instanceof Unit) {
				Unit toUnit = (Unit) selectedEntity;
				convert(new UnitMap(new UnitAttribute(toUnit.getName()
						+ UnitAttribute.EXP_DELIMITER + 1)),
						toUnit.getConversionMultiplier());
			}
		}
	}

	class ConvertCompleteClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			String unitAttribute = "";
			for (UnitDisplay unitDisplay : unitDisplays) {
				if (!unitDisplay.isCanceled) {
					UnitAttribute unitAtt = unitDisplay.wrappedNode
							.getUnitAttribute();
					if (!unitDisplay.inNumerator
							&& !unitAtt.toString().contains("-")) {
						String exp = unitAtt.getUnitMultiples()[0]
								.getUnitExponent();
						unitAtt.setString(unitAtt.toString().replace(exp,
								"-" + exp));
					}
					unitAttribute = unitAttribute + "*" + unitAtt;
				}
			}
			unitAttribute = unitAttribute.replaceFirst(
					UnitAttribute.BASE_DELIMITER_REGEX, "");
			node.setSymbol(totalNode.getSymbol());
			node.setAttribute(MathAttribute.Unit, unitAttribute);
			if (variableEquation == null) {
				Moderator.switchToAlgebra(node.getTree().getEquationXMLClone(), true,
						ActivityType.algebrasolve, true);
				AlgebraActivity aActivity = Moderator
						.getCurrentAlgebraActivity();
				aActivity.algOut.updateAlgebraHistory("Conversion",
						Skill.CONVERSION, aActivity.getEquationTree());
			} else {
				variableEquation.reCreate(node.getTree());
				Moderator.switchBackToProblem();
			}
		}
	}
}
