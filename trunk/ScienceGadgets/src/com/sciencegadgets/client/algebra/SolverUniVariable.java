package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.AlgebraicTransformations;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.Rule;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
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

		boolean areNumbersOnLeft;
		if (equationTree.getLeftSide() == variable) {
			areNumbersOnLeft = false;
		} else if (equationTree.getRightSide() == variable) {
			areNumbersOnLeft = true;
		} else {
			Window.alert("Solve didn't work :(");
			return null;
		}

		return EVALUATE(equationTree, areNumbersOnLeft);
		// return new BigDecimal("0");
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
		if (TypeSGET.Number.equals(side.getType())) {
			return side;
		} else if (TypeSGET.Fraction.equals(side.getType())
				&& TypeSGET.Number.equals(side.getChildAt(0).getType())
				&& TypeSGET.Number.equals(side.getChildAt(1).getType())) {
			SIMPLIFY_FRACTION(side.getChildAt(0), side.getChildAt(1), true);
			return side;
		}

		EquationNode toSimplify = GET_DEEPEST_EVALUATABLE(side);

		if (TypeSGET.Fraction.equals(toSimplify.getType())) {
			boolean simplified = SIMPLIFY_FRACTION(toSimplify.getChildAt(0),
					toSimplify.getChildAt(1), true);

		} else {
			TransformationList<TransformationButton> trans = TransformationList
					.FIND_ALL_SIMPLIFY(toSimplify);
			if (trans.size() == 0) {
				return side;
				// throw new IllegalArgumentException(
				// "There is no way to simplify:\n" + toSimplify);
			}
			System.out.println("");
			System.out.println("trans " + trans);
			System.out.println("fisrt " + trans.getFirst().getClass());
			eTree.reloadDisplay(true, true);
			TransformationButton transButton = trans.getFirst();
			transButton.transform();
		}
		return EVALUATE_RECURSION(eTree, areNumbersOnLeft);
	}

	private static boolean SIMPLIFY_FRACTION(EquationNode numerator,
			EquationNode denominator, boolean execute)
			throws ArithmeticException {
		Integer numValue = Integer.parseInt(numerator.getSymbol());
		Integer denValue = Integer.parseInt(denominator.getSymbol());

		LinkedHashSet<Integer> numFactors, denFactors;
		numFactors = AlgebraicTransformations.FIND_PRIME_FACTORS(numValue);
		denFactors = AlgebraicTransformations.FIND_PRIME_FACTORS(denValue);

		HashMap<Integer, Integer> matches = new HashMap<Integer, Integer>();
		a: for (Integer num : numFactors) {
			for (Integer den : denFactors) {
				if (num.equals(den) && !matches.containsValue(den)) {
					matches.put(num, den);
					continue a;
				}
			}
		}

		if (matches.size() < 2) {// "1" is always a factor
			System.out.println("no simpl: " + numValue + " " + denValue);
			return false;
		} else if (!execute) {
			return true;
		}
		for (Entry<Integer, Integer> match : matches.entrySet()) {
			numFactors.remove(match.getKey());
			denFactors.remove(match.getValue());
		}
		Integer numNewValue = 1;
		Integer denNewValue = 1;
		for (Integer num : numFactors) {
			numNewValue *= num;
		}
		for (Integer den : denFactors) {
			denNewValue *= den;
		}
		numerator.setSymbol(numNewValue.toString());
		denominator.setSymbol(denNewValue.toString());
		System.out.println("simpl: " + numValue + "-" + numNewValue + " "
				+ denValue + "-" + denNewValue);

		numerator.highlight();
		denominator.highlight();
		Moderator.getCurrentAlgebraActivity().algOut.updateAlgebraHistory(
				"Simplify Fraction", Rule.SIMPLIFY_FRACTIONS,
				numerator.getTree());
		return true;
	}

	private static EquationNode GET_DEEPEST_EVALUATABLE(EquationNode node)
			throws IllegalArgumentException {

		switch (node.getType()) {
		case Sum:
		case Term:
			LinkedList<EquationNode> children = node.getChildren();
			for (int i = 1; i < children.size(); i++) {
				EquationNode child = children.get(i);
				if (!TypeSGET.Operation.equals(child.getType())) {
					continue;
				}
				EquationNode left = child.getPrevSibling();
				EquationNode right = child.getNextSibling();
				if (CAN_SIMPLIFY(left)) {
					return GET_DEEPEST_EVALUATABLE(left);
				} else if (CAN_SIMPLIFY(right)) {
					return GET_DEEPEST_EVALUATABLE(right);
				} else {
					return child;
				}
			}
			throw new IllegalArgumentException(
					"No evaluatable operation in: \n" + node.toString());
		case Fraction:
		case Exponential:
			EquationNode first = node.getChildAt(0);
			EquationNode second = node.getChildAt(1);
			if (CAN_SIMPLIFY(first)) {
				return GET_DEEPEST_EVALUATABLE(first);
			} else if (CAN_SIMPLIFY(second)) {
				return GET_DEEPEST_EVALUATABLE(second);
			} else {
				return node;
			}
		case Trig:
		case Log:
			if (CAN_SIMPLIFY(node.getFirstChild())) {
				return GET_DEEPEST_EVALUATABLE(node);
			} else {
				return node;
			}
		default:
			throw new IllegalArgumentException("Cannot go deeper into node: \n"
					+ node.toString());
		}
	}

	private static boolean CAN_SIMPLIFY(EquationNode node) {
		switch (node.getType()) {
		case Number:
			return false;
		case Fraction:
			if (TypeSGET.Number.equals(node.getChildAt(0).getType())
					&& TypeSGET.Number.equals(node.getChildAt(1).getType())) {
				return SIMPLIFY_FRACTION(node.getChildAt(0),
						node.getChildAt(1), false);
			} else {
				return false;
			}
		default:
			return true;
		}
	}

}
