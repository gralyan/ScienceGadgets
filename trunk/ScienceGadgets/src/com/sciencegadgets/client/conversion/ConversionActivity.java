package com.sciencegadgets.client.conversion;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.SelectionPanel.Cell;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.ToggleSlide;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
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
	FlowPanel unitSelectionToggleArea;
	@UiField
	AbsolutePanel wrapperArea;
	@UiField
	Button convertButton;

	static final UnitSelection unitSelection = new UnitSelection(false, true,
			false);
	static final DerivedUnitSelection derivedUnitsSelection = new DerivedUnitSelection();
	static final ToggleSlide unitSelectionToggle = new ToggleSlide("Convert",
			"Definitions", true);
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

		unitSelectionToggle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				unitSelectionArea.clear();
				if (unitSelectionToggle.isFistSelected()) {
					unitSelectionArea.add(derivedUnitsSelection);
				} else {
					unitSelectionArea.add(unitSelection);
				}
			}
		});
		derivedUnitsSelection.addStyleName("fillParent");
		unitSelectionToggleArea.add(unitSelectionToggle);

		convertButton.addClickHandler(new ConvertClickHandler());
	}

	class ConvertClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			String unitAttribute = "";
			for (UnitDisplay unitDisplay : unitDisplays) {
				if (!unitDisplay.isCanceled) {
					unitAttribute = unitAttribute + "*"
							+ unitDisplay.wrappedNode.getUnitAttribute();
				}
			}
			unitAttribute = unitAttribute.replaceFirst(
					UnitUtil.BASE_DELIMITER_REGEX, "");
			node.setSymbol(totalNode.getSymbol());
			node.setAttribute(MathAttribute.Unit, unitAttribute);
			Moderator.switchToAlgebra();
		}
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
			unitNode.setAttribute(MathAttribute.Unit, base);
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
		for (UnitDisplay unitDisplay : unitDisplays) {
			MathNode jointNode = unitDisplay.historyNode;
			if (!unitDisplay.isCanceled) {
				mTree.getWrappers().add(
						new ConversionWrapper(unitDisplay, wrapperArea, this));
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

	}

	void fillUnitSelection(final String unitName) {

		DataModerator.database.getUnit(unitName, new AsyncCallback<Unit>() {
			@Override
			public void onFailure(Throwable caught) {
			}

			@Override
			public void onSuccess(Unit result) {
				selectedUnit = result;
				unitSelection.reloadUnitBox(selectedUnit.getQuantityKindName());
			}
		});
	}

	private void convert(Unit toUnit) {

		// Keep unit exponent constant, all get cancelled
		String exp = UnitUtil.getExponent(selectedWrapper.getNode()
				.getUnitAttribute());
		int expAbs = Math.abs(Integer.parseInt(exp));

		// Update History area
		mTree.getRightSide().append(TypeML.Operation,
				Operator.getMultiply().getSign());

		MathNode newHistoryFrac = mTree.getRightSide().append(TypeML.Fraction, "");
		MathNode fromHistoryNode = mTree.NEW_NODE(TypeML.Number,
				toUnit.getConversionMultiplier());
		MathNode toHistoryNode = mTree.NEW_NODE(TypeML.Number,
				selectedUnit.getConversionMultiplier());

		toHistoryNode.setAttribute(MathAttribute.Unit, toUnit.getName()
				+ UnitUtil.EXP_DELIMITER + 1);
		fromHistoryNode.setAttribute(MathAttribute.Unit, selectedUnit.getName()
				+ UnitUtil.EXP_DELIMITER + 1);

		MathNode numHistoryNode, denHistoryNode = null;
		String numUnitName, denUnitName = null;
		boolean selectIsNumerator = selectedWrapper.getUnitDisplay().inNumerator;
		if (selectIsNumerator) {
			numHistoryNode = toHistoryNode;
			denHistoryNode = fromHistoryNode;
			numUnitName = toUnit.getName();
			denUnitName = selectedUnit.getName();
		} else {
			numHistoryNode = fromHistoryNode;
			denHistoryNode = toHistoryNode;
			numUnitName = selectedUnit.getName();
			denUnitName = toUnit.getName();
		}

		if (expAbs == 1) {
			newHistoryFrac.append(numHistoryNode);
			newHistoryFrac.append(denHistoryNode);
		} else {
			MathNode num = newHistoryFrac.append(TypeML.Exponential, "");
			MathNode den = newHistoryFrac.append(TypeML.Exponential, "");
			num.append(numHistoryNode);
			den.append(denHistoryNode);
			num.append(TypeML.Number, expAbs + "");
			den.append(TypeML.Number, expAbs + "");
		}

		// Update Working area
		String numSymbol = UnitUtil.getSymbol(numUnitName);
		String denSymbol = UnitUtil.getSymbol(denUnitName);

		MathNode numWrap = mTree.NEW_NODE(TypeML.Variable, numSymbol);
		MathNode denWrap = mTree.NEW_NODE(TypeML.Variable, denSymbol);
		if (expAbs > 1) {
			MathNode numWrapExp = mTree.NEW_NODE(TypeML.Exponential, "");
			MathNode denWrapExp = mTree.NEW_NODE(TypeML.Exponential, "");
			numWrapExp.append(numWrap);
			denWrapExp.append(denWrap);
			numWrapExp.append(TypeML.Number, "" + expAbs);
			denWrapExp.append(TypeML.Number, "" + expAbs);
			numWrap = numWrapExp;
			denWrap = denWrapExp;
		}

		numWrap.setAttribute(MathAttribute.Unit, numUnitName
				+ UnitUtil.EXP_DELIMITER + exp);
		denWrap.setAttribute(MathAttribute.Unit, denUnitName
				+ UnitUtil.EXP_DELIMITER + exp);

		int selectedIndex = selectedWrapper.getNode().getIndex();
		int numIndex = selectIsNumerator ? selectedIndex : -1;
		int denIndex = !selectIsNumerator ? selectedIndex : -1;
		wrapperFraction.getChildAt(0).addBefore(numIndex, numWrap);
		wrapperFraction.getChildAt(1).addBefore(denIndex, denWrap);

		unitDisplays.add(new UnitDisplay(numWrap, numHistoryNode, !selectIsNumerator,
				true));
		unitDisplays.add(new UnitDisplay(denWrap, denHistoryNode, selectIsNumerator,
				false));
		
		// Calculate conversion
		BigDecimal total = new BigDecimal(totalNode.getSymbol());
		for (int i = 0; i < expAbs; i++) {
			total = total.multiply(new BigDecimal(numHistoryNode.getSymbol()),
					MathContext.DECIMAL128);
			total = total.divide(new BigDecimal(denHistoryNode.getSymbol()),
					MathContext.DECIMAL128);
		}
		totalNode.setSymbol(total.stripTrailingZeros().toEngineeringString());
		
		selectedWrapper.getUnitDisplay().isCanceled = true;
		selectedWrapper.unselect();
		
		reloadEquation();
	}

	class ConvertSelectHandler implements SelectionHandler {
		@Override
		public void onSelect(Cell selected) {
			convert((Unit) selected.getEntity());
		}
	}

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
}
