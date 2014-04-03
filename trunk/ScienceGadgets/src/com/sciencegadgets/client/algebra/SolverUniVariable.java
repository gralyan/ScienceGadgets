package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;
import java.util.LinkedList;

import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;
import com.sciencegadgets.shared.TypeSGET;

public class SolverUniVariable {

	public static BigDecimal EVALUATE(EquationTree equationTree) {

		// Varify Uni-Variable
		LinkedList<EquationNode> variables = equationTree
				.getNodesByType(TypeSGET.Variable);
		if (variables.size() != 1) {
			Window.alert("There must be exactly one variable in the equation to evaluate it");
			return null;
		}

		ISOLATE_NODE(variables.getFirst());

		return new BigDecimal("0");
	}

	private static EquationNode ISOLATE_NODE(EquationNode toIsolate)
			throws IllegalArgumentException {

		// Varify Uni-Variable and get the variable side
		LinkedList<EquationNode> leftVariables = toIsolate.getTree()
				.getLeftSide().getNodesByType(toIsolate.getType());
		LinkedList<EquationNode> rightVariables = toIsolate.getTree()
				.getRightSide().getNodesByType(toIsolate.getType());

		final int leftIndex = 0;
		final int equalsIndex = 1;
		final int rightIndex = 2;
		int sideIndex = equalsIndex;
		for (EquationNode node : leftVariables) {
			if (node == toIsolate) {
				sideIndex = leftIndex;
				break;
			}
		}
		if (sideIndex != leftIndex) {
			for (EquationNode node : rightVariables) {
				if (node == toIsolate) {
					sideIndex = rightIndex;
					break;
				}
			}
		}
		boolean isVariableInLeftSide;
		if (sideIndex == leftIndex) {
			isVariableInLeftSide = true;
		} else if (sideIndex == rightIndex) {
			isVariableInLeftSide = false;
		} else {
			throw new IllegalArgumentException(
					"The specivied node is not found: \nnode: "
							+ toIsolate.toString());
		}

		boolean isEasy = Moderator.isInEasyMode;
		Moderator.isInEasyMode = true;
		Moderator.getCurrentAlgebraActivity().inProgramaticTransformMode = true;
		EquationNode isolated = ISOLATE_NODE_RECURSION(toIsolate,
				isVariableInLeftSide, 0);
		Moderator.isInEasyMode = isEasy;
		Moderator.getCurrentAlgebraActivity().inProgramaticTransformMode = false;
		return isolated;

	}

	static EquationNode ISOLATE_NODE_RECURSION(EquationNode toIsolate,
			boolean isVariableInLeftSide, int count)
			throws IllegalArgumentException {

		EquationNode side = null;
		if (isVariableInLeftSide) {
			side = toIsolate.getTree().getLeftSide();
		} else {
			side = toIsolate.getTree().getRightSide();
		}

		if (toIsolate == side) {
			return toIsolate;
		}

		EquationNode toMove = null;
		switch (side.getType()) {
		case Sum:
		case Term:
		case Fraction:
		case Exponential:
			LinkedList<EquationNode> children = side.getChildren();
			for (EquationNode child : children) {
				if (TypeSGET.Operation.equals(child.getType())
						|| child == toIsolate) {
					continue;
				}
				LinkedList<EquationNode> childsDescendants = child
						.getNodesByType(toIsolate.getType());
				if (childsDescendants.contains(toIsolate)) {
					continue;
				}
				toMove = child;
				break;
			}
			break;
		case Log:
		case Trig:
			toMove = side;
			break;
		case Equation:
		case Number:
		case Variable:
			throw new IllegalArgumentException(
					"The specified node cannot be isolated: \nnode: "
							+ toIsolate.toString() + "\nfrom side: " + side);
		default:
			JSNICalls.error("Case not found");
			return null;
		}
		if (toMove == null) {
			throw new IllegalArgumentException(
					"There is nothing to isolate from: \nnode: "
							+ toIsolate.toString() + "\nfrom side: " + side);
		}
		BothSidesTransformations bsTrans = new BothSidesTransformations(toMove);
		if (bsTrans.size() == 0) {
			return null;
		}
		toIsolate.getTree().reloadDisplay(true, true);
		BothSidesButton bsButton = bsTrans.getFirst();
		bsButton.getJoinedButton().select();
		bsButton.transform();

		return ISOLATE_NODE_RECURSION(toIsolate, isVariableInLeftSide, ++count);

	}

}
