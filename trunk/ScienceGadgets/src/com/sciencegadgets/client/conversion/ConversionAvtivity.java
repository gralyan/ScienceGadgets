package com.sciencegadgets.client.conversion;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
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

public class ConversionAvtivity extends AbsolutePanel {

	final UnitSelection unitSelection = new UnitSelection(false, true);
	final private FlowPanel dimensionalAnalysisArea = new FlowPanel();
	final private FlowPanel workArea = new FlowPanel();
	private HashSet<MathNode> cancelled = new HashSet<MathNode>();
	private MathTree mTree = null;
	private EquationHTML eqHTML = null;
	private Unit selectedUnit = null;
	static ConversionWrapper selectedWrapper = null;
	private MathNode lastNode = null;

	public ConversionAvtivity() {
		dimensionalAnalysisArea.setHeight("50%");
		workArea.setHeight("50%");
		this.add(dimensionalAnalysisArea);
		this.add(workArea);

		unitSelection.unitBox.addSelectionHandler(new ConvertSelectHandler());
		unitSelection.getElement().setAttribute("id", "conversionUnitSelect");

		workArea.add(unitSelection);
	}

	public void load(MathNode node) {
		cancelled.clear();
		selectedUnit = null;
		selectedWrapper = null;
		lastNode = null;

		Element equationNode = (new HTML("<math><mo>=</mo></math>"))
				.getElement().getFirstChildElement();
		Node nodeCloneLeft = node.getMLNode().cloneNode(true);
		Node nodeCloneRight = node.getMLNode().cloneNode(true);
		equationNode.insertFirst(nodeCloneLeft);
		equationNode.appendChild(nodeCloneRight);

		mTree = new MathTree(equationNode, false);

		dimensionalAnalysisArea.clear();

		// fillUnitSelection(node.getUnitAttribute());
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		// Dynamically fills parent
		reloadEquation();
	}

	void reloadEquation() {
		dimensionalAnalysisArea.clear();
		eqHTML = mTree.reloadEqHTML();
		eqHTML.autoFillParent = true;
		dimensionalAnalysisArea.add(eqHTML);

		MathNode rightSide = mTree.getRightSide();
		placeWrappers(rightSide);

		// Automatically select last wrapper
		if (lastNode == null) {
			lastNode = rightSide;
		}
		((ConversionWrapper) lastNode.getWrapper()).select();
	}

	private void placeWrappers(MathNode node) {
		if (TypeML.Number.equals(node.getType())) {
			ConversionWrapper wrap = new ConversionWrapper(node, this,
					node.getHTMLAlgOut());
			if (cancelled.contains(node)) {
				wrap.cancel();
			}
		} else {
			LinkedList<MathNode> children = node.getChildren();
			for (MathNode child : children) {
				placeWrappers(child);
			}
		}
	}

	void fillUnitSelection(final String unitName) {

		DataModerator.database.getUnit(unitName,
				new AsyncCallback<Unit>() {

					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(Unit result) {
						selectedUnit = result;
						unitSelection.reloadUnitBox(selectedUnit
								.getQuantityKindName());
					}
				});
	}

	private void convert(Unit toUnit) {
		MathNode rightSide = mTree.getRightSide().encase(TypeML.Term);
		rightSide.append(TypeML.Operation, Operator.getMultiply().getSign());
		MathNode frac = rightSide.append(TypeML.Fraction, "");

		MathNode toNode = null;
		MathNode fromNode = null;
		if (selectedWrapper.getNode().getIndex() == 0) {
			toNode = frac.append(TypeML.Number,
					toUnit.getConversionMultiplier());
			fromNode = frac.append(TypeML.Number,
					selectedUnit.getConversionMultiplier());
		} else {// Flip selected was in denominator
			fromNode = frac.append(TypeML.Number,
					selectedUnit.getConversionMultiplier());
			toNode = frac.append(TypeML.Number,
					toUnit.getConversionMultiplier());
		}
		lastNode = toNode;

		toNode.setAttribute(MathAttribute.Unit, toUnit.getName());
		fromNode.setAttribute(MathAttribute.Unit, selectedUnit.getName());

		cancelled.add(fromNode);
		cancelled.add(selectedWrapper.getNode());
		/*
		 * Consolidate right side into left
		 */
		// Collect units in numerators and denominators
		BigDecimal total = new BigDecimal(1);
		MathNode leftSide = mTree.getLeftSide().replace(TypeML.Term, "");
		HashSet<MathNode> numerators = new HashSet<MathNode>();
		HashSet<MathNode> denominators = new HashSet<MathNode>();
		for (MathNode rightChild : rightSide.getChildren()) {
			if (TypeML.Fraction.equals(rightChild.getType())) {
				MathNode num = rightChild.getChildAt(0);
				if (!cancelled.contains(num)) {
					total = total.multiply(new BigDecimal(num.getSymbol()));
					String unitN = num.getUnitAttribute();
					MathNode unitNodeN = mTree.NEW_NODE(TypeML.Number,
							TypeML.Operator.SPACE.getSign());
					unitNodeN.setAttribute(MathAttribute.Unit, unitN);
					numerators.add(unitNodeN);
				}
				MathNode den = rightChild.getChildAt(1);
				if (!cancelled.contains(den)) {
					total = total.divide(new BigDecimal(den.getSymbol()));
					String unitD = den.getUnitAttribute();
					MathNode unitNodeD = mTree.NEW_NODE(TypeML.Number,
							TypeML.Operator.SPACE.getSign());
					unitNodeD.setAttribute(MathAttribute.Unit, unitD);
					denominators.add(unitNodeD);
				}
			} else if (TypeML.Number.equals(rightChild.getType())) {
				if (!cancelled.contains(rightChild)) {
					total = total.multiply(new BigDecimal(rightChild
							.getSymbol()));
					String unit = rightChild.getUnitAttribute();
					MathNode unitNode = mTree.NEW_NODE(TypeML.Number,
							TypeML.Operator.SPACE.getSign());
					unitNode.setAttribute(MathAttribute.Unit, unit);
					numerators.add(unitNode);
				}
			}
		}
		leftSide.append(TypeML.Number, total.toPlainString());
		// Create consolidated unit on left side
		//TODO maybe just stick to units as an attribute rather than nodes, no real reason to make them wrappers, could just step through and cancel out stuff automatically
		MathNode leftNumerator=null;
		if (denominators.size() > 0) {
			MathNode leftFrac = leftSide.append(TypeML.Fraction, "");
			if (numerators.size() > 0) {
				leftNumerator = leftFrac.append(TypeML.Term, "");
				for (MathNode num : numerators) {
					leftNumerator.append(num);
				}
			} else {
				leftFrac.append(TypeML.Number, "1");
			}
			MathNode leftDenominator = leftFrac.append(TypeML.Term, "");
			for (MathNode den : denominators) {
				leftDenominator.append(den);
			}
		} else if (numerators.size() >0) {//No denominator
			leftNumerator = leftSide;
		}
		for (MathNode num : numerators) {
			leftNumerator.append(num);
		}

		selectedWrapper.unselect();
		reloadEquation();
	}

	class ConvertSelectHandler implements SelectionHandler {
		@Override
		public void onSelect(Cell selected) {
			convert((Unit) selected.getEntity());
		}
	}
}
