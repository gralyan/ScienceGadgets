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
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.SelectionPanel.Cell;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
import com.sciencegadgets.shared.UnitMap;
import com.sciencegadgets.shared.UnitUtil;

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

	static final UnitSelection unitSelection = new UnitSelection(false, true,false);
	static final SelectionPanel derivedUnitsSelection = new SelectionPanel("Base Units");
	private MathTree mTree = null;
	private MathNode node;

	// wrapped nodes and history nodes

	LinkedList<UnitDisplay> unitDisplays = new LinkedList<UnitDisplay>();

	private Unit selectedUnit = null;
	static ConversionWrapper selectedWrapper = null;

	private MathNode totalNode;
	private MathNode wrapperFraction;

	private Element workingHTML;

	public ConversionActivity() {

		add(conversionUiBinder.createAndBindUi(this));

		getElement().setAttribute("id", "conversionActivity");

		unitSelection.unitBox.addSelectionHandler(new ConvertSelectHandler());
		unitSelection.addStyleName("fillParent");
		unitSelectionArea.add(unitSelection);

		derivedUnitsSelection.addSelectionHandler(new ConvertSelectHandler());
		derivedUnitsSelection.addStyleName("fillParent");
		deriveUnitArea.add(derivedUnitsSelection);

		convertButton.addClickHandler(new ConvertCompleteClickHandler());
	}

	public void load(MathNode node) {
		this.node = node;
		unitDisplays.clear();
		selectedUnit = null;
		selectedWrapper = null;

		Element root = DOM.createElement(TypeML.Equation.getTag());

		Element dummySide = DOM.createElement(TypeML.Number.getTag());
		Element eq = DOM.createElement(TypeML.Operation.getTag());
		eq.setInnerText("=");
		dummySide.setAttribute(MathAttribute.Value.getName(), "1");
		dummySide.setInnerText("1");
		root.appendChild(dummySide);
		root.appendChild(eq);
		root.appendChild(dummySide.cloneNode(true));

		mTree = new MathTree(root, false);

		mTree.getRightSide().replace(TypeML.Term, "");
		mTree.getLeftSide().replace(TypeML.Term, "");

		totalNode = mTree.getLeftSide().append(TypeML.Number, node.getSymbol());
		mTree.getRightSide().append(totalNode.clone());

		String[] bases = UnitUtil.getUnits(node);

		MathNode fracRight = mTree.NEW_NODE(TypeML.Fraction, "");
		MathNode numerRight = fracRight.append(TypeML.Term, "");
		MathNode denomRight = fracRight.append(TypeML.Term, "");

		MathNode fracLeft = wrapperFraction = fracRight.clone();
		MathNode numerLeft = fracLeft.getChildAt(0);
		MathNode denomLeft = fracLeft.getChildAt(1);

		for (String base : bases) {
			boolean negExp = base.contains("-");
			String symbol = UnitUtil.getSymbol(UnitUtil.getUnitNames(base)[0]);
			String exp = UnitUtil.getExponent(base).replace("-", "");

			MathNode unitNode;
			if ("1".equals(exp)) {
				unitNode = mTree.NEW_NODE(TypeML.Variable, symbol);
			} else {
				unitNode = mTree.NEW_NODE(TypeML.Exponential, "");
				unitNode.append(TypeML.Variable, symbol);
				unitNode.append(TypeML.Number, exp);
			}
			unitNode.setAttribute(MathAttribute.Unit, base.replace("-", ""));
			MathNode unitNodeClone = unitNode.clone();

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

		MathNode[] numAndDens = { denomLeft, denomRight, numerLeft, numerRight };
		for (MathNode numOrDen : numAndDens) {
			if (numOrDen.getChildCount() == 0) {
				numOrDen.append(TypeML.Number, "1");

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
		EquationHTML eqHTML = mTree.reloadDisplay(false);

		// Recreate wrappers
		placeWrappers();

		// Place entire equation in history area first
		eqHTML.autoFillParent = true;
		dimensionalAnalysisArea.add(eqHTML);

		// No need for equals sign
		mTree.getEquals().getHTML().removeFromParent();

		// Move left side of equation into wrapper area
		Element newWorkingHTML = mTree.getLeftSide().getHTML();
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
	}

	private void placeWrappers() {
		LinkedList<Wrapper> wrappers = mTree.getWrappers();
		wrappers.clear();

		for (UnitDisplay unitDisplay : unitDisplays) {
			MathNode jointNode = unitDisplay.historyNode;
			if (!unitDisplay.isCanceled) {
				new ConversionWrapper(unitDisplay, wrapperArea, this);
			} else {
				unitDisplay.wrappedNode.getHTML().removeFromParent();

				if (TypeML.Number.equals(jointNode.getType())) {
					Element[] units = jointNode.getHTMLofUnits();
					for (Element unit : units) {
						unit.addClassName("lineThrough");
					}
				} else {
					jointNode.getHTML().addClassName("lineThrough");
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
				String excludedUnitName = selectedUnit.getName();

				unitSelection.reloadUnitBox(quantityKind, excludedUnitName);
			}
		});

		derivedUnitsSelection.clear();

		try {// Deconstruct option for derived units
			DerivedUnit derivedUnit = DerivedUnit.valueOf(unitName);
			String dataUnitAttribute = derivedUnit.getDerivedMap()
					.getUnitAttribute();
			Element derivedUnitElement = UnitUtil.element_From_attribute(
					dataUnitAttribute, null, false);
			derivedUnitElement.getStyle()
					.setVerticalAlign(VerticalAlign.MIDDLE);
			String derivedUnitHTML = JSNICalls
					.elementToString(derivedUnitElement);

			derivedUnitsSelection.add(derivedUnit.getSymbol() + "="
					+ derivedUnit.getConversionMultiplier() + derivedUnitHTML,
					derivedUnit.getUnitName(), derivedUnit);
		} catch (IllegalArgumentException e) {
		}
	}

	private void convert(UnitMap toMap, String toMultiplier) {

		// Keep unit exponent constant, all get cancelled
		String exp = UnitUtil.getExponent(selectedWrapper.getNode()
				.getUnitAttribute());
		int expAbs = Math.abs(Integer.parseInt(exp));

		UnitMap fromMap = new UnitMap(selectedUnit.getName()
				+ UnitUtil.EXP_DELIMITER + 1);
		String fromMultiplier = selectedUnit.getConversionMultiplier();
		toMap = toMap.getExponential(expAbs);
		fromMap = fromMap.getExponential(expAbs);

		boolean isSelectNum = selectedWrapper.getUnitDisplay().inNumerator;
		UnitMap newUnitMap = isSelectNum ? toMap.getDivision(fromMap) : fromMap
				.getDivision(toMap);
		String numMultiplier = isSelectNum ? fromMultiplier : toMultiplier;
		String denMultiplier = !isSelectNum ? fromMultiplier : toMultiplier;

		// History fraction of multipliers
		mTree.getRightSide().append(TypeML.Operation,
				Operator.getMultiply().getSign());
		MathNode newHistoryFrac = mTree.getRightSide().append(TypeML.Fraction,
				"");
		MathNode newHistoryNum = newHistoryFrac.append(TypeML.Sum, "");
		MathNode newHistoryDen = newHistoryFrac.append(TypeML.Sum, "");

		MathNode numMultiplierNode = mTree.NEW_NODE(TypeML.Number,
				numMultiplier);
		MathNode denMultiplierNode = mTree.NEW_NODE(TypeML.Number,
				denMultiplier);

		if (expAbs == 1) {
			newHistoryNum.append(numMultiplierNode);
			newHistoryDen.append(denMultiplierNode);
		} else {
			MathNode numExp = newHistoryNum.append(TypeML.Exponential, "");
			MathNode denExp = newHistoryDen.append(TypeML.Exponential, "");
			numExp.append(numMultiplierNode);
			denExp.append(denMultiplierNode);
			numExp.append(TypeML.Number, expAbs + "");
			denExp.append(TypeML.Number, expAbs + "");
		}

		// Update Working area
		int selectedIndex = selectedWrapper.getNode().getIndex();
		selectedIndex = newUnitMap.size() == 2 ? selectedIndex : -1;
		for (Entry<String, Integer> entry : newUnitMap.entrySet()) {
			String untiName = entry.getKey();
			Integer unitExp = entry.getValue();
			Integer unitExpAbs = Math.abs(unitExp);
			boolean inNum = unitExp > 0;

			String numSymbol = UnitUtil.getSymbol(untiName);

			MathNode workingNode = mTree.NEW_NODE(TypeML.Variable, numSymbol);
			if (unitExp > 1 || unitExp < -1) {
				MathNode workingExp = mTree.NEW_NODE(TypeML.Exponential, "");
				workingExp.append(workingNode);
				workingExp.append(TypeML.Number, "" + unitExpAbs);
				workingNode = workingExp;
			}
			workingNode.setAttribute(MathAttribute.Unit, untiName
					+ UnitUtil.EXP_DELIMITER + unitExpAbs);
			MathNode historyNode = workingNode.clone();

			int numOrDen = inNum ? 0 : 1;
			wrapperFraction.getChildAt(numOrDen).addBefore(selectedIndex,
					workingNode);
			newHistoryFrac.getChildAt(numOrDen).append(historyNode);

			unitDisplays.add(new UnitDisplay(workingNode, historyNode, fromMap
					.containsKey(untiName), inNum));
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
		MathNode wrappedNode;
		MathNode historyNode;
		boolean isCanceled = false;
		boolean inNumerator = true;

		public UnitDisplay(MathNode wrappedNode, MathNode historyNode,
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
			if (selectedEntity instanceof DerivedUnit) {
				DerivedUnit deriveUnit = (DerivedUnit) selectedEntity;
				convert(deriveUnit.getDerivedMap(),
						deriveUnit.getConversionMultiplier());
			}else if (selectedEntity instanceof Unit) {
				Unit toUnit = (Unit) selectedEntity;
				convert(new UnitMap(toUnit.getName() + UnitUtil.EXP_DELIMITER
						+ 1), toUnit.getConversionMultiplier());
			}
		}
	}

	class ConvertCompleteClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			String unitAttribute = "";
			for (UnitDisplay unitDisplay : unitDisplays) {
				if (!unitDisplay.isCanceled) {
					String unitAtt = unitDisplay.wrappedNode.getUnitAttribute();
					if (!unitDisplay.inNumerator && !unitAtt.contains("-")) {
						String exp = UnitUtil.getExponent(unitAtt);
						unitAtt = unitAtt.replace(exp, "-" + exp);
					}
					unitAttribute = unitAttribute + "*" + unitAtt;
				}
			}
			unitAttribute = unitAttribute.replaceFirst(
					UnitUtil.BASE_DELIMITER_REGEX, "");
			node.setSymbol(totalNode.getSymbol());
			node.setAttribute(MathAttribute.Unit, unitAttribute);
			Moderator.switchToAlgebra();
		}
	}
}
