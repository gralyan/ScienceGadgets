package com.sciencegadgets.client.algebra.transformations;

import java.util.LinkedHashSet;
import java.util.LinkedList;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathWrapper;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.Type.Operator;
import com.sciencegadgets.client.algebra.WrapDragController;

public class AlgebraicTransformations {
	
	public static Label response = new Label();

	/**
	 * Separates a negative number into the product of -1 and the absolute value
	 * of the number
	 * 
	 * @param negNode
	 *            - node of negative number
	 */
	public static void separateNegative_check(MathNode negNode) {
		if (negNode.getSymbol().startsWith(Type.Operator.MINUS.getSign())
				&& !negNode.getSymbol().equals("-1")) {
			AlgebraActivity.contextMenuArea.add(new SeperateNegButton(negNode));
		}
	}

	public static void separateNegative(MathNode negNode) {
		MathNode parent = negNode.getParent();
		String newSymbol = negNode.getSymbol().replaceFirst(
				Type.Operator.MINUS.getSign(), "");
		negNode.setSymbol(newSymbol);
		parent = negNode.encase(Type.Term);

		int nodeIndex = negNode.getIndex();
		parent.addBefore(nodeIndex, Type.Operation, Type.Operator.getMultiply()
				.getSign());
		parent.addBefore(nodeIndex, Type.Number, "-1");
		Moderator.reloadEquationPanel("-"+newSymbol+" = -1"+Operator.getMultiply().getSign()+newSymbol, null);
	}

	/**
	 * Place button allowing for cancellation
	 * 
	 * @param node
	 */
	public static void cancellation_check(MathNode node) {
		MathNode thisSide = null;
		MathNode parent = node.getParent();
		if (Type.Fraction.equals(parent.getType())) {
			thisSide = node;
		} else if (Type.Term.equals(parent.getType())
				&& Type.Fraction.equals(parent.getParentType())) {
			thisSide = parent;
		} else {
			return;
		}

		MathNode otherSide = null;
		if (thisSide.getIndex() == 0) {
			otherSide = thisSide.getNextSibling();
		} else {
			otherSide = thisSide.getPrevSibling();
		}
		if (otherSide == null) {
			return;
		}

		if (node.isLike(otherSide)) {
			LinkedList<MathNode> list = new LinkedList<MathNode>();
			list.add(otherSide);
			cancellation_addDragDrops(node, list);
		} else if (Type.Term.equals(otherSide.getType())) {
			LinkedList<MathNode> list = new LinkedList<MathNode>();
			for (MathNode child : otherSide.getChildren()) {
				if (node.isLike(child)) {
					list.add(child);
				}
			}
			if (list.size() > 0) {
				cancellation_addDragDrops(node, list);
			}
		}

	}

	private static void cancellation_addDragDrops(MathNode node,
			LinkedList<MathNode> dropNodes) {

		WrapDragController dragController = node.getWrapper()
				.addDragController();
		for (MathNode dropNode : dropNodes) {
			dragController
					.registerDropController(new CancellationDropController(
							(MathWrapper) dropNode.getWrapper()));
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
		if(number == 1){
			return;
		}

		LinkedHashSet<Integer> primeFactors = findPrimeFactors(number);

		for (Integer factor : primeFactors) {
			AlgebraActivity.contextMenuArea.add(new FactorNumberButton(factor,
					number / factor, node));
		}
	}

	private static LinkedHashSet<Integer> findPrimeFactors(Integer number) {
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

		MathNode parent = node.encase(Type.Term);
		int index = node.getIndex();
		parent.addBefore(index, Type.Operation, Operator.getMultiply()
				.getSign());
		parent.addBefore(index, Type.Number, factor.toString());

		node.setSymbol(factored + "");

		Moderator.reloadEquationPanel(original + " = " + factor + " "+ Operator.getMultiply().getSign() + " " + factored, Rule.FactorizationInteger);
	}

	/**
	 * Performs the operation specified by the node
	 * 
	 * @param opNode
	 *            - operation to perform
	 */
	public static void operation(MathNode opNode) {
		MathNode left, right = null;

		AlgebraActivity.contextMenuArea.clear();

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
			System.out.println("negating " + symbol);
			if (symbol.startsWith(Operator.MINUS.getSign())) {
				symbol = symbol.replaceFirst(Operator.MINUS.getSign(), "");
			} else {
				symbol = Operator.MINUS.getSign() + symbol;
			}
			System.out.println("negated " + symbol);
			toNegate.setSymbol(symbol);
			break;
		case Term:
			propagateNegative(toNegate.getFirstChild());
			break;
		case Sum:
			for (MathNode sumChild : toNegate.getChildren()) {
				propagateNegative(sumChild);
			}
			break;
		case Fraction:
			propagateNegative(toNegate.getFirstChild());
			break;
		default:
			MathNode casing = toNegate.encase(Type.Term);
			casing.addBefore(0, Type.Operation, Operator.getMultiply()
					.getSign());
			casing.addBefore(0, Type.Number, "-1");

		}
	}

	public static void denominatorFlip_check(MathNode node) {
		AlgebraActivity.contextMenuArea.add(new DenominatorFlipButton(node));
	}

	public static void denominatorFlip(MathNode node) {
		node.highlight();
		
		if(!Type.Fraction.equals(node.getType())){
			node = node.encase(Type.Fraction);
			node.append(Type.Number, "1");
		}
		//Flip
		node.append(node.getChildAt(0));
		
		MathNode parentFraction = node.getParent();
		MathNode grandParent = parentFraction.getParent();
		int index = parentFraction.getIndex();
		
		if(!Type.Term.equals(grandParent.getType())){
			grandParent = parentFraction.encase(Type.Term);
			index = 0;
		}
		
		grandParent.addBefore(index, parentFraction.getChildAt(1));
		grandParent.addBefore(index, Type.Operation, Operator.getMultiply().getSign());
		grandParent.addBefore(index, parentFraction.getChildAt(0));
		
		parentFraction.remove();
		
		Moderator.reloadEquationPanel("Multiply by Resiprocal", Rule.FractionDivision);
	}

}

// /////////////////////////////////////////////////////////////////////
// Button choices
// //////////////////////////////////////////////////////////////////////
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

class FactorNumberButton extends Button {
	FactorNumberButton(final int factor, final int cofactor, final MathNode node) {

			setHTML("Factor " + "("+factor+")" + Operator.getMultiply().getSign()+"("
					+ cofactor+")");
		
		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgebraicTransformations.factorNumber(factor, node);
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
