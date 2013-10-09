package com.sciencegadgets.client.algebra.transformations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.Type.Operator;

public class AlgebraicTransformations {

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
		negNode.setSymbol(negNode.getSymbol().replaceFirst(
				Type.Operator.MINUS.getSign(), ""));
		parent = negNode.encase(Type.Term);

		int nodeIndex = negNode.getIndex();
		parent.addBefore(nodeIndex, Type.Operation, Type.Operator.getMultiply()
				.getSign());
		parent.addBefore(nodeIndex, Type.Number, "-1");
		Moderator.reloadEquationPanel(null);
	}

	/**
	 * List the factors of the number as buttons to choose factor
	 * @param node
	 */
	public static void factorizeNumbers_check(MathNode node) {
		Integer number;
		try {
			number = Integer.parseInt(node.getSymbol());
		} catch (NumberFormatException e) {
			return;
		}

		LinkedHashSet<Integer> primeFactors = findPrimeFactors(number);
		
		for(Integer factor : primeFactors){
		AlgebraActivity.contextMenuArea.add(new FactorNumberButton(factor, node));
		}
	}

	private static LinkedHashSet<Integer> findPrimeFactors(Integer number) {
		int n = Math.abs(number);
		LinkedHashSet<Integer> factors = new LinkedHashSet<Integer>();

		int start = 2;
		byte inc = 1;
		if (n % 2 == 1) {//odd numbers can't have even factors
			start = 3;
			inc = 2;
		}
		for (int i = start; i <= Math.sqrt(n); i = i + inc) {
			if (n % i == 0) {
				factors.add(i);
			}
		}
		for (int i = 2; i <= n-1; i++) {
			if (n % i == 0) {
				System.out.println(i +" " +n/i);
			}
		}
		System.out.println("");
		return factors;
	}

	static void factorNumber(Integer factor, MathNode node) {

		String original = node.getSymbol();
		int factored = Integer.parseInt(original)/factor;
		
		node.highlight();
		
		MathNode parent = node.encase(Type.Term);
		int index = node.getIndex();
		parent.addBefore(index, Type.Operation, Operator.getMultiply().getSign());
		parent.addBefore(index, Type.Number, factor.toString());
		
		node.setSymbol(factored+"");
		
		Moderator.reloadEquationPanel(original+" = "+factor+" "+Operator.getMultiply().getSign()+" "+factored);
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

}

///////////////////////////////////////////////////////////////////////
//Button choices
////////////////////////////////////////////////////////////////////////
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
	FactorNumberButton(final Integer factor, final MathNode node) {
		
		setHTML("Factor "+factor);
		
		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgebraicTransformations.factorNumber(factor, node);
			}
		});
	}
}
