package com.sciencegadgets.client.algebra;

import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

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
				multiply(left, opNode, right);
				break;
			case MINUS://TODO
				break;
			case PLUS://TODO
				break;
			}
		} catch (IndexOutOfBoundsException e) {
		}
	}

	public static void multiply(MathNode left, MathNode opNode,
			MathNode right) {
		Type leftType = left.getType();
		Type rightType = right.getType();
		
		first: switch (leftType) {
		case Term:
				JSNICalls.consoleError("Illegal node within Term1: "+rightType);
			break first;
		case Sum:
			second: switch (rightType) {
			case Sum:
				multiplyFoil(left, opNode, right);
				break second;
			case Exponential:
			case Fraction:
			case Variable:
			case Number:
				multiplyDistribution(right, opNode, left);
				break second;
			case Operation:
				JSNICalls.consoleError("Operation in wrong place6: "+left.getParent().toString());
			default:
				JSNICalls.consoleError("Illegal node within Term2: "+rightType);
				break second;
			}
			break first;
		case Exponential:
			second: switch (rightType) {
			case Sum:
				multiplyFoil(left, opNode, right);
				break second;
			case Exponential:
				multiplyCombineBases(left, opNode, right);
				multiplyCombineExponents(left, opNode, right);
				break second;
			case Fraction:
				multiplyWithFraction(left, opNode, right);
				break second;
			case Variable:
			case Number:
				multiplyCombineBases(left, opNode, right);
				break second;
			case Operation:
				JSNICalls.consoleError("Operation in wrong place6: "+left.getParent().toString());
			default:
				JSNICalls.consoleError("Illegal node within Term3: "+rightType);
				break second;
			}
			break first;
		case Fraction:
			second: switch (rightType) {
			case Sum:
				multiplyFoil(left, opNode, right);
				break second;
			case Fraction:
				multiplyFractions(left, opNode, right);
				break second;
			case Exponential:
			case Variable:
			case Number:
				multiplyWithFraction(right, opNode, left);
				break second;
			case Operation:
				JSNICalls.consoleError("Operation in wrong place6: "+left.getParent().toString());
			default:
				JSNICalls.consoleError("Illegal node within Term4: "+rightType);
				break second;
			}
			break first;
		case Variable:
		case Number:
			second: switch (rightType) {
			case Sum:
				multiplyFoil(left, opNode, right);
				break second;
			case Exponential:
				multiplyCombineBases(left, opNode, right);
				break second;
			case Fraction:
				multiplyWithFraction(left, opNode, right);
				break second;
			case Variable:
				multiplyOperationToSpace(left, opNode, right);
				break second;
			case Number:
				if(Type.Variable.equals(leftType)){
					multiplyOperationToSpace(left, opNode, right);
				}else if(Type.Number.equals(leftType)){
					multiplyNumbers(left, opNode, right);
				}
				break second;
			case Operation:
				JSNICalls.consoleError("Operation in wrong place6: "+left.getParent().toString());
			default:
				JSNICalls.consoleError("Illegal node within Term5: "+rightType);
				break second;
			}
			break first;
		case Operation:
			JSNICalls.consoleError("Operation in wrong place6: "+left.getParent().toString());
		default:
			JSNICalls.consoleError("Illegal node within Term7: "+leftType);
			break first;
		}
	}
	private static void multiplyFoil(MathNode left, MathNode opNode,
			MathNode right) {

		System.out.println(left.getType()+"\t\t"+right.getType()+"\t\tfoil\n\n");
	}
	private static void multiplyDistribution(MathNode dist, MathNode opNode,
			MathNode sum) {
		
		System.out.println(dist.getType()+"\t\t"+sum.getType()+"\t\tDISTRIBUTION\n\n");
	}
	private static void multiplyCombineExponents(MathNode left, MathNode opNode,
			MathNode right) {
		
		System.out.println(left.getType()+"\t\t"+right.getType()+"\t\tcombineExp\n\n");
	}
	private static void multiplyCombineBases(MathNode left, MathNode opNode,
			MathNode right) {
		
		System.out.println(left.getType()+"\t\t"+right.getType()+"\t\tcombineBase\n\n");
	}
	private static void multiplyWithFraction(MathNode nonFrac, MathNode opNode,
			MathNode fraction) {
		
		System.out.println(nonFrac.getType()+"\t\t"+fraction.getType()+"\t\tWITH FRACTION\n\n");
	}
	private static void multiplyFractions(MathNode left, MathNode opNode,
			MathNode right) {
		
		System.out.println(left.getType()+"\t\t"+right.getType()+"\t\tFractions\n\n");
	}
	private static void multiplyOperationToSpace(MathNode left, MathNode opNode,
			MathNode right) {
		
		System.out.println(left.getType()+"\t\t"+right.getType()+"\t\topToSpace\n\n");
	}
	private static void multiplyNumbers(MathNode left, MathNode opNode,
			MathNode right) {

		System.out.println(left.getType()+"\t\t"+right.getType()+"\t\tArithmetic\n\n");
	}
}
