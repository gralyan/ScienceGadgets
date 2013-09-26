package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;

import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.Type.Operator;

public class MultiplyTransformations {
	protected static MathNode operation;
	protected static MathNode parent;
	protected static MathNode grandParent;
	private static final int same = 0;

	public static void assign(MathNode left, MathNode multiplyNode,
			MathNode right) {
		operation = multiplyNode;
		parent = multiplyNode.getParent();
		grandParent = parent.getParent();

		Type leftType = left.getType();
		Type rightType = right.getType();
		
		//Check for improper types for left and right
		switch (leftType) {
		case Equation:
		case Term:
			JSNICalls
			.error("Illegal node within Term: " + leftType);
			break;
		case Operation:
			JSNICalls.error("Operation in wrong place: "
					+ left.getParent().toString());
			break;
		case Number:
			BigDecimal leftValue = new BigDecimal(left.getSymbol());
			if(leftValue.compareTo(new BigDecimal(0)) == same){
				multiplyZero(right, left);
				return;
			}else if(leftValue.compareTo(new BigDecimal(1)) == same){
				multiplyOne(right, left);
				return;
			}else if(leftValue.compareTo(new BigDecimal(-1)) == same){
				multiplyNegOne(right, left);
				return;
			}
			break;
		}
		switch (rightType) {
		case Equation:
		case Term:
			JSNICalls
			.error("Illegal node within Term: " + rightType);
			break;
		case Operation:
			JSNICalls.error("Operation in wrong place: "
					+ right.getParent().toString());
			break;
		case Number:
			BigDecimal rightValue = new BigDecimal(right.getSymbol());
			if(rightValue.compareTo(new BigDecimal(0)) == same){
				multiplyZero(left, right);
				return;
			}else if(rightValue.compareTo(new BigDecimal(1)) == same){
				multiplyOne(left, right);
				return;
			}else if(rightValue.compareTo(new BigDecimal(-1)) == same){
				multiplyNegOne(left, right);
				return;
			}
			break;
		}

		//Assignments in 2d switch (leftType, rightType)
		first: switch (leftType) {
		case Sum:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Exponential:
			case Fraction:
			case Variable:
			case Number:
				multiplyDistribution(right, left, true);
				break second;
			}
			break first;
		case Exponential:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Exponential:
				multiplyCombineBases(left, right);
				break second;
			case Fraction:
				multiplyWithFraction(left, right, false);
				break second;
			case Variable:
			case Number:
				multiplyCombineBases(left, right);
				break second;
			}
			break first;
		case Fraction:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Fraction:
				multiplyFractions(left, right);
				break second;
			case Exponential:
			case Variable:
			case Number:
				multiplyWithFraction(right, left, true);
				break second;
			}
			break first;
		case Variable:
			if (Type.Variable.equals(rightType)) {
				multiplyCombineBases(left, right);
				break first;
			} else if (Type.Number.equals(rightType)) {
				multiplyOperationToSpace();
				break first;
			}
			//fall through
		case Number:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Exponential:
				multiplyCombineBases(left, right);
				break second;
			case Fraction:
				multiplyWithFraction(left, right, false);
				break second;
			case Variable:
				multiplyOperationToSpace();
				break second;
			case Number:
				multiplyNumbers(left, right);
				break second;
			}
			break first;
		}
	}

	private static void multiplyDistribution(MathNode dist, MathNode sum,
			boolean reversed) {

		for (MathNode sumChild : sum.getChildren()) {
			if (!Type.Operation.equals(sumChild.getType())) {

				MathNode sumChildCasings = sumChild.encase(Type.Term);

				int index = reversed ? -1 : 0;
				sumChildCasings.add(index, operation.getType(),
						operation.getSymbol());
				sumChildCasings.add(index, dist.clone());
			}
		}

		dist.remove();
		operation.remove();

		parent.decase();
		
		Moderator.reloadEquationPanel("Distribute");
	}

	private static boolean multiplyCombineExponents(MathNode left,
			MathNode right) {

		if (!left.getChildAt(1).isLike(right.getChildAt(1))) {
			return false;
		}

		MathNode leftBase = left.getChildAt(0);
		MathNode rightBase = right.getChildAt(0);

		MathNode rightCasing = rightBase.encase(Type.Term);

		rightCasing.add(0, operation);
		rightCasing.add(0, leftBase);

		left.remove();

		parent.decase();

		Moderator.reloadEquationPanel("Combine Exponents");
		return true;
	}

	private static boolean multiplyCombineBases(MathNode left, MathNode right) {

		// May not already be in exponent eg. a = a^1
		MathNode leftBase;
		if (Type.Exponential.equals(left.getType())) {
			leftBase = left.getChildAt(0);
		} else {
			leftBase = left;
		}
		MathNode rightBase;
		if (Type.Exponential.equals(right.getType())) {
			rightBase = right.getChildAt(0);
		} else {
			rightBase = right;
		}

		if (!leftBase.isLike(rightBase)) {
			// TODO only in automatic mode
			if (Type.Exponential.equals(left.getType())
					&& Type.Exponential.equals(right.getType())) {
				multiplyCombineExponents(left, right);
			} else if (Type.Variable.equals(left.getType())
					&& Type.Variable.equals(right.getType())) {
				multiplyOperationToSpace();
			}
			return false;
		} else {
			if (!Type.Exponential.equals(left.getType())) {
				left = left.encase(Type.Exponential);
				left.add(Type.Number, "1");
			}
			if (!Type.Exponential.equals(right.getType())) {
				right = right.encase(Type.Exponential);
				right.add(Type.Number, "1");
			}
		}

		MathNode leftExp = left.getChildAt(1);
		MathNode rightExp = right.getChildAt(1);

		MathNode rightCasing = rightExp.encase(Type.Sum);

		rightCasing.add(0, Type.Operation, Operator.PLUS.getSign());
		rightCasing.add(0, leftExp);

		left.remove();
		operation.remove();

		parent.decase();

		Moderator.reloadEquationPanel("Combine Bases");
		return true;
	}

	private static void multiplyWithFraction(MathNode nonFrac,
			MathNode fraction, boolean reversed) {
		MathNode numerator = fraction.getChildAt(0);
		numerator = numerator.encase(Type.Term);

		int index = reversed ? -1 : 0;
		numerator.add(index, operation);
		numerator.add(index, nonFrac);

		parent.decase();
		
		Moderator.reloadEquationPanel("Multiply with Fraction");
	}

	private static void multiplyFractions(MathNode left, MathNode right) {
		MathNode numerator = right.getChildAt(0);
		numerator = numerator.encase(Type.Term);
		numerator.add(0, operation);
		numerator.add(0, left.getChildAt(0));

		MathNode denominator = right.getChildAt(1);
		denominator = denominator.encase(Type.Term);
		denominator.add(0, Type.Operation, operation.getSymbol());
		denominator.add(0, left.getChildAt(1));

		left.remove();

		parent.decase();

		Moderator.reloadEquationPanel("Multiply Fractions");
	}

	private static void multiplyOperationToSpace() {
		operation.setSymbol(Operator.SPACE.getSign());

		Moderator.reloadEquationPanel("Op to Space");
	}

	private static void multiplyNumbers(MathNode left, MathNode right) {

		BigDecimal leftValue = new BigDecimal(left.getSymbol());
		BigDecimal rightValue = new BigDecimal(right.getSymbol());

		BigDecimal productValue = leftValue.multiply(rightValue);

		right.setSymbol(productValue.toString());

		left.remove();
		operation.remove();

		parent.decase();

		Moderator.reloadEquationPanel(leftValue.toString()+" x "+rightValue.toString()+" = "+productValue.toString());
	}
	
	private static void multiplyZero(MathNode other, MathNode zero){
		zero.remove();
		operation.remove();
		other.remove();
		
		parent.decase();

		Moderator.reloadEquationPanel("a x 0 = 0");
	}
	private static void multiplyOne(MathNode other, MathNode one){
		one.remove();
		operation.remove();
		
		parent.decase();
		
		Moderator.reloadEquationPanel("a x 1 = a");
	}

	private static void multiplyNegOne(MathNode other, MathNode negOne){
		other.remove();
		operation.remove();
		negOne.remove();
		AlgebraicTransformations.propagateNegative(other);
		
		parent.decase();
		
		Moderator.reloadEquationPanel("a x -1 = -a");
	}

//	private static void parentClean() {
//
//		if (parent.getChildCount() == 1) {
//			grandParent.add(parent.getIndex(), parent.getFirstChild());
//			parent.remove();
//		}
//	}


}
