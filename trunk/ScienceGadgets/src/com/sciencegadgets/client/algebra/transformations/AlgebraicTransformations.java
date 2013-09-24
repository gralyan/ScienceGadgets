package com.sciencegadgets.client.algebra.transformations;

import java.util.LinkedList;

import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type.Operator;

public class AlgebraicTransformations {

	/**
	 * Separates a negative number into the product of -1 and the absolute value
	 * of the number
	 * 
	 * @param negNode
	 *            - node of negative number
	 */
	public static void separateNegative(MathNode negNode) {
		MathNode parent = negNode.getParent();
		negNode.setSymbol(negNode.getSymbol().replaceFirst(
				Type.Operator.MINUS.getSign(), ""));
		if (!Type.Term.equals(parent.getType())) {
			parent = negNode.encase(Type.Term);
		}
		int nodeIndex = negNode.getIndex();
		parent.add(nodeIndex, Type.Operation, Type.Operator.getMultiply()
				.getSign());
		parent.add(nodeIndex, Type.Number, "-1");
		Moderator.reloadEquationPanel(null);
	}

	/**
	 * Performs the operation specified by the node
	 * 
	 * @param opNode
	 *            - operation to perform
	 */
	public static void operation(MathNode opNode) {
		MathNode left, right = null;

		try {
			left = opNode.getPrevSibling();
			right = opNode.getNextSibling();

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
		} catch (IndexOutOfBoundsException e) {
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
				propagateNegative(sumChild);
			}
			break;
		case Fraction:
			propagateNegative(toNegate.getFirstChild());
			break;
		default:
			MathNode casing = toNegate.encase(Type.Term);
			casing.add(0, Type.Operation, Operator.getMultiply().getSign());
			casing.add(0, Type.Number, "-1");

		}
	}

}
