package com.sciencegadgets.client.conversion;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.TextDecoration;
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
	AbsolutePanel wrapperArea;
	@UiField
	Button convertButton;

	final UnitSelection unitSelection = new UnitSelection(false, true, false);
	private MathTree mTree = null;
	private MathNode node;

	// wrapped nodes and history nodes

	LinkedList<UnitDisplay> unitDisplays = new LinkedList<UnitDisplay>();

	private Unit selectedUnit = null;
	static ConversionWrapper selectedWrapper = null;

	private MathNode totalNode;
	private MathNode wrapperFraction;

	public ConversionActivity() {

		add(conversionUiBinder.createAndBindUi(this));

		getElement().setAttribute("id", "conversionActivity");

		unitSelection.unitBox.addSelectionHandler(new ConvertSelectHandler());
		unitSelection.addStyleName("fillParent");
		unitSelectionArea.add(unitSelection);

		wrapperArea.addStyleName("fillParent");

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
			System.out.println(unitAttribute);
			node.setSymbol(totalNode.getSymbol());
			node.setAttribute(MathAttribute.Unit, unitAttribute);
			Moderator.reloadAlgebraActivity();
		}
	}

	public void load(MathNode node) {
		this.node = node;
		unitDisplays.clear();
		selectedUnit = null;
		selectedWrapper = null;

		// Element equationNode = (new HTML("<math><mo>=</mo></math>"))
		// .getElement().getFirstChildElement();

		Element root = DOM.createElement(TypeML.Equation.getTag());

		Element dummySide = DOM.createElement(TypeML.Number.getTag());
		Element eq = DOM.createElement(TypeML.Operation.getTag());
		eq.setInnerText("=");
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
			String symbol = UnitUtil.getSymbol(UnitUtil.getUnitNames(base)[0]);
			String exp = UnitUtil.getExponent(base);
			if (base.contains("-")) {
				MathNode unitNode = mTree.NEW_NODE(TypeML.Exponential, "");
				unitNode.setAttribute(MathAttribute.Unit, base);
				unitNode.append(TypeML.Variable, symbol);
				unitNode.append(TypeML.Number, exp.replace("-", ""));
				MathNode dl = unitNode.clone();
				MathNode dr = unitNode;
				denomLeft.append(dl);
				denomRight.append(dr);
				unitDisplays.add(new UnitDisplay(dl, dr, false, false));
			} else {
				MathNode unitNode = mTree.NEW_NODE(TypeML.Exponential, "");
				unitNode.setAttribute(MathAttribute.Unit, base);
				unitNode.append(TypeML.Variable, symbol);
				unitNode.append(TypeML.Number, exp.replace("-", ""));
				MathNode unitNodeClone = unitNode.clone();
				numerLeft.append(unitNodeClone);
				numerRight.append(unitNode);
				unitDisplays.add(new UnitDisplay(unitNodeClone, unitNode,
						false, true));
			}
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
		// makeLeftSide();
		reloadEquation();
	}

	void reloadEquation() {
		// Make and add entire equation before moving left
		dimensionalAnalysisArea.clear();
		EquationHTML eqHTML = mTree.reloadDisplay(false);
		eqHTML.autoFillParent = true;

		dimensionalAnalysisArea.add(eqHTML);

		// Move left side of equation into wrapper area, resize
		NodeList<Node> children = wrapperArea.getElement().getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			children.getItem(i).removeFromParent();
		}
		String size = eqHTML.getElement().getStyle().getFontSize();
		Element workingHTML = mTree.getLeftSide().getHTML();
		workingHTML.getStyle().setFontSize(
				Double.parseDouble(size.replace("%", "")),
				com.google.gwt.dom.client.Style.Unit.PCT);
		wrapperArea.getElement().appendChild(workingHTML);

		// No need for equals sign either
		mTree.getEquals().getHTML().removeFromParent();

		// Recreate wrappers
		placeWrappers();

	}

	private void placeWrappers() {
		for (UnitDisplay unitDisplay : unitDisplays) {
			MathNode jointNode = unitDisplay.historyNode;
			if (!unitDisplay.isCanceled) {
				mTree.getWrappers().add(
						new ConversionWrapper(unitDisplay, wrapperArea, this));
			} else {
				unitDisplay.wrappedNode.getHTML().getStyle().setDisplay(Display.NONE);
				if (TypeML.Number.equals(jointNode.getType())) {
					Element[] units = jointNode.getHTMLofUnits();
					for (Element unit : units) {
//						Style style = unit.getStyle();
//						style.setTextDecoration(TextDecoration.LINE_THROUGH);
//						style.setColor("red");
						unit.addClassName("lineThrough");
					}
				} else {
//					Style style = jointNode.getHTML().getStyle();
//					style.setTextDecoration(TextDecoration.LINE_THROUGH);
//					style.setColor("red");
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

		MathNode frac = mTree.getRightSide().append(TypeML.Fraction, "");
		MathNode fromNode = mTree.NEW_NODE(TypeML.Number,
				selectedUnit.getConversionMultiplier());
		MathNode toNode = mTree.NEW_NODE(TypeML.Number,
				toUnit.getConversionMultiplier());

		toNode.setAttribute(MathAttribute.Unit, toUnit.getName()
				+ UnitUtil.EXP_DELIMITER + 1);
		fromNode.setAttribute(MathAttribute.Unit, selectedUnit.getName()
				+ UnitUtil.EXP_DELIMITER + 1);

		MathNode numNode, denNode = null;
		String numUnitName, denUnitName = null;
		boolean selectIsNumerator = selectedWrapper.getUnitDisplay().inNumerator;
		if (selectIsNumerator) {
			numNode = toNode;
			denNode = fromNode;
			numUnitName = toUnit.getName();
			denUnitName = selectedUnit.getName();
		} else {
			numNode = fromNode;
			denNode = toNode;
			numUnitName = selectedUnit.getName();
			denUnitName = toUnit.getName();
		}

		if (expAbs == 1) {
			frac.append(numNode);
			frac.append(denNode);
		} else {
			MathNode num = frac.append(TypeML.Exponential, "");
			MathNode den = frac.append(TypeML.Exponential, "");
			num.append(numNode);
			den.append(denNode);
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
			MathNode denWrapExp = numWrapExp.clone();
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

		wrapperFraction.getChildAt(0).addBefore(
				selectedWrapper.getNode().getIndex(), numWrap);
		wrapperFraction.getChildAt(1).append(denWrap);

		// Calculate conversion
		BigDecimal total = new BigDecimal(totalNode.getSymbol());
		for (int i = 0; i < expAbs; i++) {
			total = total.multiply(new BigDecimal(numNode.getSymbol()),
					MathContext.DECIMAL32);
			total = total.divide(new BigDecimal(denNode.getSymbol()),
					MathContext.DECIMAL32);
		}
		totalNode.setSymbol(total.stripTrailingZeros().toEngineeringString());

		selectedWrapper.getUnitDisplay().isCanceled = true;
		
		unitDisplays.add(new UnitDisplay(numWrap, numNode, !selectIsNumerator, true));
		unitDisplays.add(new UnitDisplay(denWrap, denNode, selectIsNumerator, false));

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
