package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;
import java.util.LinkedList;

import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;
import com.sciencegadgets.shared.TypeSGET;

public class SolverUniVariable {

	public static BigDecimal SOLVE(EquationTree equationTree) {

		// Varify Uni-Variable
		LinkedList<EquationNode> variables = equationTree
				.getNodesByType(TypeSGET.Variable);
		if (variables.size() != 1) {
			Window.alert("There must be exactly one variable in the equation to evaluate it");
			return null;
		}

		EquationNode variable = ISOLATE_NODE(variables.getFirst());

//		boolean areNumbersOnLeft;
//		if (equationTree.getLeftSide() == variable) {
//			areNumbersOnLeft = true;
//		} else if (equationTree.getRightSide() == variable) {
//			areNumbersOnLeft = false;
//		} else {
//			Window.alert("Solve didn't work :(");
//			return null;
//		}
//
//		return EVALUATE(equationTree, areNumbersOnLeft);
		return new BigDecimal("0");
	}

	private static EquationNode ISOLATE_NODE(EquationNode toIsolate)
			throws IllegalArgumentException {

		// Find the variable side
		LinkedList<EquationNode> leftVariables = toIsolate.getTree()
				.getLeftSide().getNodesByType(toIsolate.getType());
		LinkedList<EquationNode> rightVariables = toIsolate.getTree()
				.getRightSide().getNodesByType(toIsolate.getType());

		boolean isVariableInLeftSide;
		if (leftVariables.contains(toIsolate)) {
			isVariableInLeftSide = true;
		} else if (rightVariables.contains(toIsolate)) {
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
				isVariableInLeftSide);

		Moderator.isInEasyMode = isEasy;
		Moderator.getCurrentAlgebraActivity().inProgramaticTransformMode = false;
		return isolated;

	}

	static EquationNode ISOLATE_NODE_RECURSION(EquationNode toIsolate,
			boolean isVariableInLeftSide) throws IllegalArgumentException {

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
		BothSidesTransformations bsTrans = TransformationList
				.FIND_ALL_BOTHSIDES(toMove);
		if (bsTrans.size() == 0) {
			return null;
		}
		toIsolate.getTree().reloadDisplay(true, true);
		BothSidesButton bsButton = bsTrans.getFirst();
		bsButton.getJoinedButton().select();
		bsButton.transform();

		return ISOLATE_NODE_RECURSION(toIsolate, isVariableInLeftSide);

	}

	public static BigDecimal EVALUATE(EquationTree eTree,
			boolean areNumbersOnLeft) {

		EquationNode sideToEvaluate = null;
		if (areNumbersOnLeft) {
			sideToEvaluate = eTree.getLeftSide();
		} else {
			sideToEvaluate = eTree.getRightSide();
		}

		LinkedList<EquationNode> variables = sideToEvaluate
				.getNodesByType(TypeSGET.Variable);
		if (variables.size() != 0) {
			Window.alert("There must not be and variables in the expression to evaluate");
			return null;
		}

		boolean isEasy = Moderator.isInEasyMode;
		Moderator.isInEasyMode = true;
		Moderator.getCurrentAlgebraActivity().inProgramaticTransformMode = true;

		EquationNode simplifiedNumber = EVALUATE_RECURSION(eTree,
				areNumbersOnLeft);

		Moderator.isInEasyMode = isEasy;
		Moderator.getCurrentAlgebraActivity().inProgramaticTransformMode = false;

		return new BigDecimal(simplifiedNumber.getSymbol());
	}

	private static EquationNode EVALUATE_RECURSION(EquationTree eTree,
			boolean areNumbersOnLeft) throws IllegalArgumentException {

		EquationNode side = null;
		if (areNumbersOnLeft) {
			side = eTree.getLeftSide();
		} else {
			side = eTree.getRightSide();
		}

		EquationNode toSimplify = null;
		switch (side.getType()) {
		case Number:
			return side;
		case Sum:
		case Term:
			for (EquationNode child : side.getChildren()) {
				if (TypeSGET.Operation.equals(child.getType())) {
					toSimplify = child;
					break;
				}
			}
			break;
		case Fraction:
			// TODO simplify if possible
			return side;
			// break;
		case Exponential:
		case Trig:
		case Log:
			toSimplify = side;
			break;
		}

		if (toSimplify == null) {
			throw new IllegalArgumentException(
					"There is nothing to simplify from: \n side: " + side);
		}
		TransformationList<TransformationButton> trans = TransformationList
				.FIND_ALL_SIMPLIFY(toSimplify);
		if (trans.size() == 0) {
			return null;
		}
		eTree.reloadDisplay(true, true);
		TransformationButton transButton = trans.getFirst();
		transButton.transform();

		return EVALUATE_RECURSION(eTree, areNumbersOnLeft);
	}

}
