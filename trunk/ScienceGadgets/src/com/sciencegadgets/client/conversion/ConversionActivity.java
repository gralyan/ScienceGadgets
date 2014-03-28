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
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.ui.UnitSelection;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;
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

	static final UnitSelection unitSelection = new UnitSelection(false, true,
			false);
	static final SelectionPanel derivedUnitsSelection = new SelectionPanel(
			"Base Units");
	private EquationTree mTree = null;
	private EquationNode node;

	// wrapped nodes and history nodes

	LinkedList<UnitDisplay> unitDisplays = new LinkedList<UnitDisplay>();

	private Unit selectedUnit = null;
	static ConversionWrapper selectedWrapper = null;

	private EquationNode totalNode;
	private EquationNode wrapperFraction;

	private Element workingHTML;

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

	public void load(EquationNode node) {
		this.node = node;
		unitDisplays.clear();
		selectedUnit = null;
		selectedWrapper = null;

		mTree = new EquationTree(TypeEquationXML.Term, "", TypeEquationXML.Term, "", false);

		totalNode = mTree.getLeftSide().append(TypeEquationXML.Number, node.getSymbol());
		mTree.getRightSide().append(totalNode.clone());

		UnitMultiple[] bases = node.getUnitAttribute().getUnitMultiples();

		EquationNode fracRight = mTree.NEW_NODE(TypeEquationXML.Fraction, "");
		EquationNode numerRight = fracRight.append(TypeEquationXML.Term, "");
		EquationNode denomRight = fracRight.append(TypeEquationXML.Term, "");

		EquationNode fracLeft = wrapperFraction = fracRight.clone();
		EquationNode numerLeft = fracLeft.getChildAt(0);
		EquationNode denomLeft = fracLeft.getChildAt(1);

		for (UnitMultiple base : bases) {
			boolean negExp = base.toString().contains("-");
			String symbol = base.getUnitName().getSymbol();
			String exp = base.getUnitExponent().replace("-", "");

			EquationNode unitNode;
			if ("1".equals(exp)) {
				unitNode = mTree.NEW_NODE(TypeEquationXML.Variable, symbol);
			} else {
				unitNode = mTree.NEW_NODE(TypeEquationXML.Exponential, "");
				unitNode.append(TypeEquationXML.Variable, symbol);
				unitNode.append(TypeEquationXML.Number, exp);
			}
			unitNode.setAttribute(MathAttribute.Unit,
					base.toString().replace("-", ""));
			EquationNode unitNodeClone = unitNode.clone();

			if (negExp) {
				denomLeft.append(unitNodeClone);
				denomRight.append(unitNode);
			} else {
				numerLeft.append(unitNodeClone);
				numerRight.append(unitNode);
			}
			unitDisplays.add(new UnitDisplay(unitNodeClone, unitNode, false,
					!negExp));
		}

		EquationNode[] numAndDens = { denomLeft, denomRight, numerLeft, numerRight };
		for (EquationNode numOrDen : numAndDens) {
			if (numOrDen.getChildCount() == 0) {
				numOrDen.append(TypeEquationXML.Number, "1");

			}
		}

		mTree.getRightSide().append(fracRight);
		mTree.getLeftSide().append(fracLeft);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		// Dynamically fills parent
		reloadEquation();
	}

	void reloadEquation() {
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

//		HashMap<Parameter, String> parameterMap = new HashMap<Parameter, String>();
//		parameterMap
//				.put(Parameter.activity, ActivityType.conversion.toString());
//		parameterMap.put(Parameter.equation, mTree.getMathXMLString());
//		URLParameters.setParameters(parameterMap, false);
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

				if (TypeEquationXML.Number.equals(jointNode.getType())) {
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

		DataModerator.database.getUnit(unitName, new AsyncCallback<Unit>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Unit result) {
				selectedUnit = result;
				String quantityKind = selectedUnit.getQuantityKindName();
				String excludedUnitName = selectedUnit.getName().toString();

				unitSelection.reloadUnitBox(quantityKind, excludedUnitName);
			}
		});

		derivedUnitsSelection.clear();

		try {// Deconstruct option for derived units
			CommonDerivedUnits derivedUnit = CommonDerivedUnits.valueOf(unitName);
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

		UnitAttribute fromUnitAttribute = new UnitAttribute(
				selectedUnit.getName() + UnitAttribute.EXP_DELIMITER + 1);
		UnitMap fromMap = new UnitMap(fromUnitAttribute);
		String fromMultiplier = selectedUnit.getConversionMultiplier();
		toMap = toMap.getExponential(expAbs);
		fromMap = fromMap.getExponential(expAbs);

		boolean isSelectNum = selectedWrapper.getUnitDisplay().inNumerator;
		UnitMap newUnitMap = isSelectNum ? toMap.getDivision(fromMap) : fromMap
				.getDivision(toMap);
		String numMultiplier = isSelectNum ? fromMultiplier : toMultiplier;
		String denMultiplier = !isSelectNum ? fromMultiplier : toMultiplier;

		// History fraction of multipliers
		mTree.getRightSide().append(TypeEquationXML.Operation,
				Operator.getMultiply().getSign());
		EquationNode newHistoryFrac = mTree.getRightSide().append(TypeEquationXML.Fraction,
				"");
		EquationNode newHistoryNum = newHistoryFrac.append(TypeEquationXML.Sum, "");
		EquationNode newHistoryDen = newHistoryFrac.append(TypeEquationXML.Sum, "");

		EquationNode numMultiplierNode = mTree.NEW_NODE(TypeEquationXML.Number,
				numMultiplier);
		EquationNode denMultiplierNode = mTree.NEW_NODE(TypeEquationXML.Number,
				denMultiplier);

		if (expAbs == 1) {
			newHistoryNum.append(numMultiplierNode);
			newHistoryDen.append(denMultiplierNode);
		} else {
			EquationNode numExp = newHistoryNum.append(TypeEquationXML.Exponential, "");
			EquationNode denExp = newHistoryDen.append(TypeEquationXML.Exponential, "");
			numExp.append(numMultiplierNode);
			denExp.append(denMultiplierNode);
			numExp.append(TypeEquationXML.Number, expAbs + "");
			denExp.append(TypeEquationXML.Number, expAbs + "");
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

			EquationNode workingNode = mTree.NEW_NODE(TypeEquationXML.Variable, numSymbol);
			if (unitExp > 1 || unitExp < -1) {
				EquationNode workingExp = mTree.NEW_NODE(TypeEquationXML.Exponential, "");
				workingExp.append(workingNode);
				workingExp.append(TypeEquationXML.Number, "" + unitExpAbs);
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
						deriveUnit.getConversionMultiplier());
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
			Moderator.switchToAlgebra(node.getTree().getEquationXMLClone(), false);
		}
	}
}
