package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.AlgebraicTransformations;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;
import com.sciencegadgets.client.algebra.transformations.ExponentialTransformations;
import com.sciencegadgets.client.algebra.transformations.FractionTransformations;
import com.sciencegadgets.client.algebra.transformations.InterFractionButton;
import com.sciencegadgets.client.algebra.transformations.InterFractionTransformations;
import com.sciencegadgets.client.algebra.transformations.InterFractionTransformations.DropType;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.shared.TypeSGET;

public class SolverUniVariable {

	public static EquationNode SOLVE(EquationTree equationTree) {

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
		
//		EVALUATE(toIsolate.getTree(), !isVariableInLeftSide);

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

	public static EquationNode EVALUATE(EquationTree eTree,
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
				areNumbersOnLeft, 0);

		Moderator.isInEasyMode = isEasy;
		Moderator.getCurrentAlgebraActivity().inProgramaticTransformMode = false;

		return simplifiedNumber;
	}

	private static EquationNode EVALUATE_RECURSION(EquationTree eTree,
			boolean areNumbersOnLeft, int count)
			throws IllegalArgumentException {

		EquationNode side = null;
		if (areNumbersOnLeft) {
			side = eTree.getLeftSide();
		} else {
			side = eTree.getRightSide();
		}

		if (count > 10) {
//			 return side;
		}
		if (TypeSGET.Number.equals(side.getType())) {
			return side;
		} else if (TypeSGET.Fraction.equals(side.getType())
				&& TypeSGET.Number.equals(side.getChildAt(0).getType())
				&& TypeSGET.Number.equals(side.getChildAt(1).getType())) {
			FractionTransformations.SIMPLIFY_FRACTION(side.getChildAt(0),
					side.getChildAt(1), true);
			return side;
		}

		TransformationList<? extends TransformationButton> list = FIND_BEST_TRANSFORMATION(eTree);
		if (list == null || list.size() == 0) {
			return side;
		}
		TransformationButton transButton = list.getFirst();
		transButton.transform();
		eTree.reloadDisplay(true, true);

		return EVALUATE_RECURSION(eTree, areNumbersOnLeft, ++count);
	}

	private static TransformationList<? extends TransformationButton> FIND_BEST_TRANSFORMATION(
			EquationTree eTree) {
		
		System.out.println("");
		System.out.println("Find best");
		
		LinkedList<EquationNode> operationNodes = eTree
				.getNodesByType(TypeSGET.Operation);

		operationNodes.remove(eTree.getEquals());

		// check for <number><operation><number>
		for (EquationNode opNode : operationNodes) {
			System.out.println("\tcheck for operation Number");
			EquationNode left = opNode.getPrevSibling();
			EquationNode right = opNode.getNextSibling();
			if (left == null || right == null) {
				continue;
			}
			TypeSGET leftType = left.getType();
			TypeSGET rightType = right.getType();
			if (TypeSGET.Number.equals(leftType)
					&& TypeSGET.Number.equals(rightType)) {
				System.out.println("\t\toperating Number "+left.getSymbol()+" "+opNode.getSymbol()+" "+right.getSymbol());
				return TransformationList.FIND_ALL_SIMPLIFY(opNode);
			}
		}

		// check for <any><operation><any>
		for (EquationNode opNode : operationNodes) {
			TransformationList<? extends TransformationButton> oList = AlgebraicTransformations.operation(opNode);
			// TODO Maybe take out and "MultiplyDistributionButton"
			if (oList.size() != 0) {
				System.out.println("\t\toperating any "+opNode.getSymbol()+" "+oList.get(0));
				return oList;
			}
		}

		LinkedList<EquationNode> fractionNodes = eTree
				.getNodesByType(TypeSGET.Fraction);

		LinkedList<EquationNode> inFractionNodes = new LinkedList<EquationNode>();
		for(EquationNode fNode : fractionNodes) {
			for(EquationNode numAndDen : fNode.getChildren()) {
				inFractionNodes.add(numAndDen);
				if(TypeSGET.Term.equals(numAndDen.getType())) {
					inFractionNodes.addAll(numAndDen.getChildren());
				}
			}
		}

		// check for InterFraction operations
		for (EquationNode inFracNode : inFractionNodes) {
			System.out.println("\tcheck for InterFraction");
			InterFractionTransformations drops = new InterFractionTransformations(inFracNode);
			
			for (InterFractionButton drop : drops) {
				System.out.println("\t\tdrop: "+drop.getDropType());
				if (drop != null && !drop.getDropType().equals(DropType.DIVIDE)) {
					System.out.println("\t\t\tdropping: "+drop.getDropType());
					return drops;
				}else {
					drops.remove(drop);
				}
			}
		}

		// check for fractions in fractions, flip denominator
		for (EquationNode fNode : fractionNodes) {
			System.out.println("\tcheck denom flip");
			if (fractionNodes.contains(fNode.getChildAt(0))
					|| fractionNodes.contains(fNode.getChildAt(1))) {
				System.out.println("\t\tDenom flip "+ fNode);
				return TransformationList
						.FIND_ALL_SIMPLIFY(fNode.getChildAt(1));
			}
		}

		LinkedList<EquationNode> exponentialNodes = eTree
				.getNodesByType(TypeSGET.Exponential);

		// check exponentials
		for (EquationNode eNode : exponentialNodes) {
			System.out.println("\tcheck exp");
			ExponentialTransformations eList = new ExponentialTransformations(eNode);
			if (eList.size() != 0) {
				System.out.println("\t\tEXP "+eNode);
				return eList;
			}
		}

		return null;
	}
//
//	@SuppressWarnings("unchecked")
//	private static TransformationList<TransformationButton> FIND_NUMBER_TRANSFORMATION(
//			EquationTree eTree) {
//
//		LinkedList<EquationNode> numberNodes = eTree
//				.getNodesByType(TypeSGET.Number);
//
//		for (EquationNode nNode : numberNodes) {
//			switch (nNode.getParentType()) {
//			case Sum:
//			case Term:
//				EquationNode op = nNode.getNextSibling();
//				if (op == null) {
//					break;
//				}
//				EquationNode right = op.getNextSibling();
//				if (right != null && numberNodes.contains(right)) {
//					return TransformationList.FIND_ALL_SIMPLIFY(op);
//				}
//				break;
//			case Fraction:
//				EquationNode frac = nNode.getParent();
//				EquationNode numerator = frac.getChildAt(0);
//				EquationNode denominator = frac.getChildAt(1);
//				if (TypeSGET.Number.equals(numerator.getType())
//						|| TypeSGET.Number.equals(denominator.getType())) {
//					FractionTransformations list = new FractionTransformations(
//							frac);
//					TransformationButton simplifier = list
//							.simplifyFraction_check();
//					if (simplifier != null) {
//						return (TransformationList) list;
//					}
//				}
//				break;
//			case Exponential:
//				EquationNode exponential = nNode.getParent();
//				EquationNode base = exponential.getChildAt(0);
//				EquationNode exponent = exponential.getChildAt(1);
//				if (TypeSGET.Number.equals(base.getType())
//						|| TypeSGET.Number.equals(exponent.getType())) {
//					return TransformationList.FIND_ALL_SIMPLIFY(exponential);
//				}
//				break;
//			case Log:
//				// case Trig: //TODO implement trig evaluation
//				return TransformationList.FIND_ALL_SIMPLIFY(nNode.getParent());
//			}
//		}
//		return FIND_FRACTION_TRANSFORMATION(eTree);
//	}
//
//	private static TransformationList<TransformationButton> FIND_FRACTION_TRANSFORMATION(
//			EquationTree eTree) {
//
//		LinkedList<EquationNode> fractionNodes = eTree
//				.getNodesByType(TypeSGET.Fraction);
//
//		for (EquationNode fNode : fractionNodes) {
//			switch (fNode.getParentType()) {
//			case Sum:
//			case Term:
//				EquationNode opRight = fNode.getNextSibling();
//				if (opRight != null) {
//					EquationNode right = opRight.getNextSibling();
//					if (right != null) {
//						return TransformationList.FIND_ALL_SIMPLIFY(opRight);
//					}
//				}
//				EquationNode opLeft = fNode.getPrevSibling();
//				if (opLeft != null) {
//					EquationNode left = opLeft.getPrevSibling();
//					if (left != null) {
//						return TransformationList.FIND_ALL_SIMPLIFY(opLeft);
//					}
//				}
//				break;
//			case Fraction:
//				EquationNode frac = fNode.getParent();
//				EquationNode denominator = frac.getChildAt(1);
//				return TransformationList.FIND_ALL_SIMPLIFY(denominator);
//			case Log:
//				return TransformationList.FIND_ALL_SIMPLIFY(fNode.getParent());
//			}
//		}
//		return null;
//	}

}
