package com.sciencegadgets.client.algebra.transformations;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.SelectionPanel.Cell;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.AlgebaWrapper;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.transformations.InterFractionDrop.DropType;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
import com.sciencegadgets.shared.TypeML.TrigFunctions;

/**
 * This class contains the set of static methods used to perform algebraic
 * changes
 * 
 */
public class AlgebraicTransformations {

	public static Label response = new Label();
	final static HashSet<VariableEvaluateSpec> varSpecs = new HashSet<VariableEvaluateSpec>();

	public static void isolatedVariable_check(MathNode isolatedVar) {
		if (TypeML.Equation.equals(isolatedVar.getParentType())) {
			AlgebraActivity.algTransformMenu.add(new EvaluatePromptButton(
					isolatedVar));
			AlgebraActivity.algTransformMenu.add(new SubstituteButton(
					isolatedVar));
		}
	}

	static void evaluate_prompt(MathNode isolatedVar) {
		final Prompt prompt = new Prompt();

		varSpecs.clear();

		MathTree tree = isolatedVar.getTree();
		LinkedList<Wrapper> wrappers = tree.getWrappers();
		for (Wrapper wrap : wrappers) {
			MathNode node = wrap.getNode();
			if (TypeML.Variable.equals(node.getType())
					&& !node.equals(isolatedVar)) {
				VariableEvaluateSpec varSpec = new VariableEvaluateSpec(node);
				varSpecs.add(varSpec);
				prompt.add(varSpec);
			}
		}
		if (varSpecs.size() == 0) {
			Window.alert("There are no variables to evaluate");
			return;
		}

		prompt.addOkHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				// Make sure the entire spec is filled
				boolean completelyFilled = true;
				for (VariableEvaluateSpec varSpec : varSpecs) {
					Double value = varSpec.valueInput.getValue();
					if (value == null) {
						varSpec.valueInput.getElement().getStyle()
								.setBackgroundColor("red");
						completelyFilled = false;
					} else {
						varSpec.valueInput.getElement().getStyle()
								.clearBackgroundColor();
						varSpec.value = value;
					}
					String unit = varSpec.unitSelect.unitBox.getSelectedValue();
					if (unit == null) {
						varSpec.unitSelect.getElement().getStyle()
								.setColor("red");
						completelyFilled = false;
					} else {
						varSpec.unitSelect.getElement().getStyle().clearColor();
						varSpec.unit = unit;
					}
				}
				if (completelyFilled) {
					prompt.disappear();
					AlgebraicTransformations.evaluate(varSpecs);
				}

			}
		});

		prompt.appear();
	}

	static void evaluate(HashSet<VariableEvaluateSpec> varSpecs) {
		for (VariableEvaluateSpec varSpec : varSpecs) {

			String value = String.valueOf(varSpec.value).replaceAll(
					(String) "\\.0$", "");
			String unit = varSpec.unit;

			MathNode quantityPlugIn = Moderator.mathTree.NEW_NODE(
					TypeML.Number, value);
			quantityPlugIn.setAttribute(MathAttribute.Unit, unit);
			varSpec.mathNode.replace(quantityPlugIn);
		}
		AlgebraActivity.reloadEquationPanel(null, null);
	}

	static void substitute_prompt(final MathNode isolatedVar) {
		String quantityKind = isolatedVar.getUnitAttribute();
		if (quantityKind == null || quantityKind.equals("")) {
			Window.alert("No similar other equations with similar variables available");
			return;
		}
		final DialogBox dialog = new DialogBox(true, true);
		SelectionPanel eqSelect = new SelectionPanel("Equation");
		dialog.add(eqSelect);
		DataModerator.fill_EquationsByQuantities(quantityKind, eqSelect);
		eqSelect.addSelectionHandler(new SelectionHandler() {
			@Override
			public void onSelect(Cell selected) {

				MathNode eqNode = isolatedVar.getParent();

				Element substitute = null;
				if (isolatedVar.isLeftSide()) {
					substitute = isolatedVar.getTree().getRightSide()
							.getMLNode();
				} else if (isolatedVar.isRightSide()) {
					substitute = isolatedVar.getTree().getLeftSide()
							.getMLNode();
				} else {
					JSNICalls.error("Could not find the element to substitute"
							+ " in: \n" + eqNode);
					dialog.removeFromParent();
					dialog.hide();
					return;
				}

				String subIntoEqStr = selected.getValue();
				Element subIntoEqEl = new HTML(subIntoEqStr).getElement()
						.getFirstChildElement();

				substitute(isolatedVar, subIntoEqEl, substitute);

				dialog.removeFromParent();
				dialog.hide();
			}
		});
		dialog.setGlassEnabled(true);
		dialog.setAnimationEnabled(true);
		dialog.center();
	}

	private static void substitute(final MathNode isolatedVar,
			Element subIntoEqEl, Element substitute) {

		// Find the variable to substutute
		NodeList<Element> possibleSubs = subIntoEqEl
				.getElementsByTagName(TypeML.Variable.getTag());
		findSub: for (int j = 0; j < possibleSubs.getLength(); j++) {
			Element possibleSub = possibleSubs.getItem(j);
			if (!isolatedVar.getUnitAttribute().equals(
					possibleSub.getAttribute(MathAttribute.Unit.getName()))) {
				continue findSub;
			}
			Element subParent = possibleSub.getParentElement();
			String plugTag = substitute.getTagName();
			if (plugTag.equals(subParent.getTagName())
					&& (plugTag.equals(TypeML.Sum.getTag()) || plugTag
							.equals(TypeML.Term.getTag()))) {
				Node subPrev = possibleSub.getPreviousSibling();
				if (subPrev != null && subPrev.getNodeName().equals("mo")
						&& "-".equals(((Element) subPrev).getInnerText())) {
					// if a sum is plugged into a variable preceded by minus
					((Element) subPrev).setInnerText("+");
					Element negativeSubstitute = new HTML(
							"<mrow><mn>-1</mn><mo>\u00B7</mo><mfenced><substitute></substitute></mfenced></mrow>")
							.getElement().getFirstChildElement();
					subParent.replaceChild(negativeSubstitute, possibleSub);
					possibleSub = subParent.getElementsByTagName("substitute")
							.getItem(0);
					subParent = possibleSub.getParentElement();
				}
				NodeList<Node> substituteChildren = substitute.getChildNodes();
				// Must save count because it will change during the
				// loop
				int childCount = substituteChildren.getLength();
				for (int k = 0; k < childCount; k++) {
					subParent.insertBefore(substituteChildren.getItem(0),
							possibleSub);
				}
				possibleSub.removeFromParent();
			} else {
				subParent.replaceChild(substitute, possibleSub);
			}
			Moderator.switchToAlgebra(subIntoEqEl);
			break;

		}
	}

	/**
	 * Separates a negative number into the product of -1 and the absolute value
	 * of the number
	 * 
	 * @param negNode
	 *            - node of negative number
	 */
	public static void separateNegative_check(MathNode negNode) {
		if (negNode.getSymbol().startsWith(TypeML.Operator.MINUS.getSign())
				&& !negNode.getSymbol().equals("-1")) {
			AlgebraActivity.algTransformMenu
					.add(new SeperateNegButton(negNode));
		}
	}

	static void separateNegative(MathNode negNode) {
		MathNode prevSib = negNode.getPrevSibling();
		String newSymbol = negNode.getSymbol().replaceFirst(
				TypeML.Operator.MINUS.getSign(), "");
		negNode.setSymbol(newSymbol);

		if (prevSib != null && TypeML.Operation.equals(prevSib.getType())) {
			if (Operator.PLUS.getSign().equals(prevSib.getSymbol())) {
				prevSib.setSymbol(Operator.MINUS.getSign());
			} else if (Operator.MINUS.getSign().equals(prevSib.getSymbol())) {
				prevSib.setSymbol(Operator.PLUS.getSign());
			} else {
				JSNICalls.error("The previous operator contains "
						+ "neither a plus or minus");
			}
		} else {
			MathNode parent = negNode.getParent();
			parent = negNode.encase(TypeML.Term);
			int nodeIndex = negNode.getIndex();
			parent.addBefore(nodeIndex, TypeML.Operation, TypeML.Operator
					.getMultiply().getSign());
			parent.addBefore(nodeIndex, TypeML.Number, "-1");
		}
		AlgebraActivity.reloadEquationPanel("-" + newSymbol + " = -1"
				+ Operator.getMultiply().getSign() + newSymbol, null);
	}

	/**
	 * Place drop targets on drag controller, allowing for operations between
	 * terms of the numerator and denominator. This allows users to cancel,
	 * divide, and combine terms on either side of the fraction
	 * 
	 * @param node
	 */
	public static void interFractionDrop_check(MathNode node) {

		MathNode thisSide = null;
		MathNode parent = node.getParent();
		switch (parent.getType()) {
		case Fraction:
			thisSide = node;
			break;
		case Term:
			if (TypeML.Fraction.equals(parent.getParentType())) {
				thisSide = parent;
				break;
			}// else fall through
		default:
			return;
		}

		MathNode otherSide = thisSide.getIndex() == 0 ? thisSide
				.getNextSibling() : thisSide.getPrevSibling();

		HashMap<MathNode, DropType> dropTargets = new HashMap<MathTree.MathNode, DropType>();

		if (node.isLike(otherSide)) {// Cancel drop on entire other sides
			dropTargets.put(otherSide, DropType.CANCEL);

		} else if (TypeML.Term.equals(otherSide.getType())) {

			for (MathNode child : otherSide.getChildren()) {
				if (node.isLike(child)) {// Cancel drop on child
					dropTargets.put(child, DropType.CANCEL);
				} else {// Drop on child
					addDropTarget(child, node, dropTargets);
				}
			}

		} else {// Drop on entire other side
			addDropTarget(otherSide, node, dropTargets);
		}

		if (dropTargets.size() > 0) {
			WrapDragController dragController = node.getWrapper()
					.addDragController();
			for (Entry<MathNode, DropType> dropTarget : dropTargets.entrySet()) {
				dragController.registerDropController(new InterFractionDrop(
						(AlgebaWrapper) dropTarget.getKey().getWrapper(),
						dropTarget.getValue()));
			}
		}

	}

	private static void addDropTarget(MathNode target, MathNode drag,
			HashMap<MathTree.MathNode, DropType> dropTargets) {

		if (!drag.getType().equals(target.getType())) {
			return;
		}

		switch (target.getType()) {
		case Number:
			dropTargets.put(target, DropType.DIVIDE);
			break;
		case Exponential:
			if (drag.getFirstChild().isLike(target.getFirstChild())) {
				dropTargets.put(target, DropType.EXPONENTIAL);
			}
			break;
		case Log:
			if (drag.getAttribute(MathAttribute.LogBase).equals(
					target.getAttribute(MathAttribute.LogBase))
					&& TypeML.Number.equals(drag.getFirstChild().getType())) {
				dropTargets.put(target, DropType.LOG_COMBINE);
			}
			break;
		case Trig:
			if (target.getFirstChild().isLike(drag.getFirstChild())) {
				// Make sure (drag or target) is sin and other is cos
				TrigFunctions dragFunc = TrigFunctions.valueOf(drag
						.getAttribute(MathAttribute.Function));
				TrigFunctions targetfunc = TrigFunctions.valueOf(target
						.getAttribute(MathAttribute.Function));
				TrigFunctions sin = TrigFunctions.sin;
				TrigFunctions cos = TrigFunctions.cos;
				if ((sin.equals(dragFunc) && cos.equals(targetfunc))
						|| (cos.equals(dragFunc) && sin.equals(targetfunc))) {
				}
				dropTargets.put(target, DropType.TRIG_COMBINE);
			}
			break;
		}
	}

	/**
	 * List the factors of the number as buttons to choose factor
	 * 
	 * @param node
	 */
	public static void factorizeNumbers_check(MathNode node) {
		Integer number;
		try {
			number = Integer.parseInt(node.getSymbol());
		} catch (NumberFormatException e) {
			return;
		}
		if (number == 1) {
			return;
		}

		AlgebraActivity.algTransformMenu.add(new FactorPromptButton(number,
				node));
	}

	static LinkedHashSet<Integer> findPrimeFactors(Integer number) {
		LinkedHashSet<Integer> factors = new LinkedHashSet<Integer>();

		if (number < 0) {
			number = Math.abs(number);
		}
		factors.add(1);

		int start = 2;
		byte inc = 1;
		if (number % 2 == 1) {// odd numbers can't have even factors
			start = 3;
			inc = 2;
		}
		for (int i = start; i <= Math.sqrt(number); i = i + inc) {
			if (number % i == 0) {
				factors.add(i);
			}
		}
		return factors;
	}

	static void factorNumber(Integer factor, MathNode node) {

		String original = node.getSymbol();
		int factored = Integer.parseInt(original) / factor;

		node.highlight();

		MathNode parent = node.encase(TypeML.Term);
		int index = node.getIndex();
		parent.addBefore(index, TypeML.Operation, Operator.getMultiply()
				.getSign());
		parent.addBefore(index, TypeML.Number, factor.toString());

		node.setSymbol(factored + "");

		AlgebraActivity.reloadEquationPanel(original + " = " + factor + " "
				+ Operator.getMultiply().getSign() + " " + factored,
				Rule.INTEGER_FACTORIZATION);
	}

	/**
	 * Performs the operation specified by the node
	 * 
	 * @param opNode
	 *            - operation to perform
	 */
	public static void operation(MathNode opNode) {
		MathNode left, right = null;

		left = opNode.getPrevSibling();
		right = opNode.getNextSibling();

		if (left != null && left != null) {
			switch (opNode.getOperation()) {
			case CROSS:
			case DOT:
			case SPACE:
				MultiplyTransformations.assign(left, opNode, right);
				break;
			case MINUS:
				AdditionTransformations.assign(left, opNode, right, false);
				break;
			case PLUS:
				AdditionTransformations.assign(left, opNode, right, true);
				break;
			}
		}
	}

	public static void propagateNegative(MathNode toNegate) {

		switch (toNegate.getType()) {
		case Number:
		case Variable:
			String symbol = toNegate.getSymbol();
			if (symbol.startsWith(Operator.MINUS.getSign())) {
				symbol = symbol.replaceFirst(Operator.MINUS.getSign(), "");
			} else {
				symbol = Operator.MINUS.getSign() + symbol;
			}
			toNegate.setSymbol(symbol);
			break;
		case Term:
			propagateNegative(toNegate.getFirstChild());
			break;
		case Sum:
			for (MathNode sumChild : toNegate.getChildren()) {
				if (!TypeML.Operation.equals(sumChild.getType())) {
					propagateNegative(sumChild);
				}
			}
			break;
		case Fraction:
			propagateNegative(toNegate.getFirstChild());
			break;
		default:
			MathNode casing = toNegate.encase(TypeML.Term);
			casing.addBefore(0, TypeML.Operation, Operator.getMultiply()
					.getSign());
			casing.addBefore(0, TypeML.Number, "-1");

		}
	}

	public static void unitConversion_check(MathNode node) {
		if (!"".equals(node.getUnitAttribute())) {
			AlgebraActivity.algTransformMenu
					.add(new UnitConversionButton(node));
		}
	}

	public static void unitConversion(MathNode node) {
		Moderator.switchToConversion(node);
	}

	public static void denominatorFlip_check(MathNode node) {
		AlgebraActivity.algTransformMenu.add(new DenominatorFlipButton(node));
	}

	static void denominatorFlip(MathNode node) {
		node.highlight();

		if (!TypeML.Fraction.equals(node.getType())) {
			node = node.encase(TypeML.Fraction);
			node.append(TypeML.Number, "1");
		}
		// Flip
		node.append(node.getChildAt(0));

		MathNode parentFraction = node.getParent();
		MathNode grandParent = parentFraction.getParent();
		int index = parentFraction.getIndex();

		if (!TypeML.Term.equals(grandParent.getType())) {
			grandParent = parentFraction.encase(TypeML.Term);
			index = 0;
		}

		grandParent.addBefore(index, parentFraction.getChildAt(1));
		grandParent.addBefore(index, TypeML.Operation, Operator.getMultiply()
				.getSign());
		grandParent.addBefore(index, parentFraction.getChildAt(0));

		parentFraction.remove();

		AlgebraActivity.reloadEquationPanel("Multiply by Resiprocal",
				Rule.FRACTION_DIVISION);
	}

	public static void unravelLogExp_check(MathNode log) {
		MathNode exponential = log.getFirstChild();
		if (TypeML.Exponential.equals(exponential.getType())) {
			MathNode exponentialBase = exponential.getFirstChild();
			if (TypeML.Number.equals(exponentialBase.getType())
					&& exponentialBase.getSymbol().equals(
							log.getAttribute(MathAttribute.LogBase))) {
				MathNode exponentialExp = exponential.getChildAt(1);
				AlgebraActivity.algTransformMenu.add(new UnravelButton(log,
						exponentialExp, Rule.LOGARITHM));
			}
		}
	}

	public static void unravelExpLog_check(MathNode exponential) {
		MathNode log = exponential.getChildAt(1);
		if (TypeML.Log.equals(log.getType())) {
			String logBase = log.getAttribute(MathAttribute.LogBase);
			MathNode exponentialBase = exponential.getFirstChild();
			if (TypeML.Number.equals(exponentialBase.getType())
					&& exponentialBase.getSymbol().equals(logBase)) {
				AlgebraActivity.algTransformMenu.add(new UnravelButton(
						exponential, log.getFirstChild(), Rule.LOGARITHM));

			}
		}
	}

	public static void inverseTrig_check(MathNode trig) {
		MathNode trigChild = trig.getFirstChild();
		if (TypeML.Trig.equals(trigChild.getType())) {
			String trigChildFunc = trigChild
					.getAttribute(MathAttribute.Function);
			String trigChildFuncInverse = TrigFunctions
					.getInverse(trigChildFunc);
			String trigFunc = trig.getAttribute(MathAttribute.Function);
			if (trigFunc.equals(trigChildFuncInverse)) {
				AlgebraActivity.algTransformMenu.add(new UnravelButton(trig,
						trigChild.getFirstChild(),
						Rule.INVERSE_TRIGONOMETRIC_FUNCTIONS));
			}
		}
	}

}

class VariableEvaluateSpec extends FlowPanel {
	protected final DoubleBox valueInput = new DoubleBox();
	protected UnitSelection unitSelect = null;;
	protected MathNode mathNode = null;
	protected Double value = null;
	protected String unit;

	VariableEvaluateSpec(MathNode mathNode) {
		this.mathNode = mathNode;
		unitSelect = new UnitSelection(mathNode.getUnitAttribute());
		Label symbol = new Label(mathNode.getSymbol() + " =");

		symbol.addStyleName("layoutRow");
		valueInput.addStyleName("layoutRow");
		unitSelect.addStyleName("layoutRow");
		unitSelect.setWidth("50%");

		add(symbol);
		add(valueInput);
		add(unitSelect);

	}
}

// /////////////////////////////////////////////////////////////////////
// Button choices
// //////////////////////////////////////////////////////////////////////

class FactorPromptButton extends Button {
	public FactorPromptButton(final Integer number, final MathNode node) {
		setHTML("Factor");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				LinkedHashSet<Integer> primeFactors = AlgebraicTransformations
						.findPrimeFactors(number);

				Prompt prompt = new Prompt(false);
				Label title = new Label();
				title.setText("" + number);
				title.setHeight("20%");
				prompt.add(title);
				for (Integer factor : primeFactors) {
					prompt.add(new FactorNumberButton(factor, number / factor,
							node, prompt));
				}
				prompt.appear();
			}
		});

	}
}

class FactorNumberButton extends Button {
	FactorNumberButton(final int factor, final int cofactor,
			final MathNode node, final Prompt prompt) {

		setHTML(factor + " " + Operator.getMultiply().getSign() + " "
				+ cofactor);
		setSize("50%", "50%");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgebraicTransformations.factorNumber(factor, node);
				prompt.disappear();
			}
		});
	}
}

class SeperateNegButton extends Button {
	SeperateNegButton(final MathNode negNode) {

		setHTML("Seperate (-)");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgebraicTransformations.separateNegative(negNode);
			}
		});
	}
}

class DenominatorFlipButton extends Button {
	DenominatorFlipButton(final MathNode node) {

		setHTML("Flip Denominator");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgebraicTransformations.denominatorFlip(node);
			}
		});
	}
}

class UnitConversionButton extends Button {
	UnitConversionButton(final MathNode node) {

		setHTML("Convert Units");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgebraicTransformations.unitConversion(node);
			}
		});
	}
}

class EvaluatePromptButton extends Button {
	EvaluatePromptButton(final MathNode isolatedVarNode) {

		setHTML("Evaluate");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgebraicTransformations.evaluate_prompt(isolatedVarNode);
			}
		});
	}
}

class SubstituteButton extends Button {
	SubstituteButton(final MathNode isolatedVarNode) {

		setHTML("Substitute");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgebraicTransformations.substitute_prompt(isolatedVarNode);
			}
		});
	}
}

class UnravelButton extends Button {

	public UnravelButton(final MathNode toReplace, final MathNode replacement,
			final Rule rule) {

		setHTML(replacement.getHTMLString());

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String changeComment = toReplace.getHTMLString() + " = "
						+ replacement.getHTMLString();
				toReplace.replace(replacement);
				AlgebraActivity.reloadEquationPanel(changeComment, rule);
			}
		});
	}
}
