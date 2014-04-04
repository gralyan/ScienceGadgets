package com.sciencegadgets.client.algebra.transformations;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebaWrapper;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.transformations.InterFractionDrop.DropType;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

/**
 * This class contains the set of static methods used to perform algebraic
 * changes by multiple type specific TransformationsList's
 */
public class AlgebraicTransformations {

	public static Label response = new Label();

	/**
	 * Performs the operation specified by the node
	 * 
	 * @param opNode
	 *            - operation to perform
	 */
	public static TransformationList<? extends TransformationButton> operation(EquationNode opNode) {

		if (opNode.getPrevSibling() != null && opNode.getNextSibling() != null) {
			switch (opNode.getOperation()) {
			case CROSS:
			case DOT:
			case SPACE:
				return new MultiplyTransformations(opNode);
			case MINUS:
			case PLUS:
				return new AdditionTransformations(opNode);
			}
		}
		return null;
	}

	/**
	 * Propagates negative into the node depending on its type<br/>
	 * <b>operation</b> - do nothing<br/>
	 * <b>number or variable</b> - change symbol<br/>
	 * <b>term</b> - propagate to first child<br/>
	 * <b>sum</b> - propagate to each child<br/>
	 * <b>fraction</b> - propagate to first child<br/>
	 * <b>everything else</b> - encase in term with the number negative one<br/>
	 * 
	 * @param toNegate
	 */
	public static void propagateNegative(EquationNode toNegate) {

		switch (toNegate.getType()) {
		case Operation:
			break;
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
			for (EquationNode sumChild : toNegate.getChildren()) {
				if (TypeSGET.Operation.equals(sumChild.getType())) {
					continue;
				}
				EquationNode prevOp = sumChild.getPrevSibling();
				if (prevOp != null && TypeSGET.Operation.equals(prevOp.getType())) {
					Operator opValue = prevOp.getOperation();
					opValue = Operator.PLUS.equals(opValue) ? Operator.MINUS
							: Operator.PLUS;
					prevOp.setSymbol(opValue.getSign());
				} else {
					propagateNegative(sumChild);
				}
			}
			break;
		case Fraction:
			propagateNegative(toNegate.getFirstChild());
			break;
		default:
			EquationNode casing = toNegate.encase(TypeSGET.Term);
			casing.addFirst(TypeSGET.Operation, Operator.getMultiply()
					.getSign());
			casing.addFirst(TypeSGET.Number, "-1");
			break;
		}
	}

	public static LinkedHashSet<Integer> FIND_PRIME_FACTORS(Integer number) {
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
	
	/**
	 * Reciprocates the given node and simplifies in one of two ways:<br/>
	 * 1) If the node is in a fraction or a term in a fraction, it is moved over
	 * to the other side of the fraction.<br/>
	 * 2) Encases the node in a fraction and adds the number one to the
	 * numerator
	 */
	public static void reciprocate(EquationNode toReciprocate) {

		EquationNode parent = toReciprocate.getParent();

		if (TypeSGET.Fraction.equals(parent.getType())) {

			boolean inNumerator = toReciprocate.getIndex() == 0;
			EquationNode otherSide = inNumerator ? toReciprocate.getNextSibling()
					: toReciprocate.getPrevSibling();
			otherSide = otherSide.encase(TypeSGET.Term);

			otherSide
					.append(TypeSGET.Operation, Operator.getMultiply().getSign());
			otherSide.append(toReciprocate);

			if (inNumerator) {
				parent.addFirst(TypeSGET.Number, "1");
			} else {
				parent.replace(otherSide);
			}

		} else if (TypeSGET.Term.equals(parent.getType())
				&& TypeSGET.Fraction.equals(parent.getParentType())) {

			if (toReciprocate.getIndex() == 0) {
				toReciprocate.getNextSibling().remove();
			} else {
				toReciprocate.getPrevSibling().remove();
			}

			boolean inNumerator = parent.getIndex() == 0;
			EquationNode otherSide = inNumerator ? parent.getNextSibling() : parent
					.getPrevSibling();

			otherSide = otherSide.encase(TypeSGET.Term);
			otherSide
					.append(TypeSGET.Operation, Operator.getMultiply().getSign());
			otherSide.append(toReciprocate);

			parent.decase();

		} else {
			EquationNode frac = toReciprocate.encase(TypeSGET.Fraction);
			frac.addFirst(TypeSGET.Number, "1");
		}
	}

	/**
	 * Separates a negative number into the product of -1 and the absolute value
	 * of the number
	 * 
	 * @param negNode
	 *            - node of negative number
	 */
	public static TransformationButton separateNegative_check(EquationNode negNode,
			TransformationList<TransformationButton> context) {
		if (negNode.getSymbol().startsWith(TypeSGET.Operator.MINUS.getSign())
				&& !negNode.getSymbol().equals("-1")) {
			return new SeperateNegButton(negNode, context);
		}
		return null;
	}

	/**
	 * Place drop targets on drag controller, allowing for operations between
	 * terms of the numerator and denominator. This allows users to cancel,
	 * divide, and combine terms on either side of the fraction
	 * 
	 * @param node
	 */
	public static void interFractionDrop_check(EquationNode node) {

		EquationNode thisSide = null;
		EquationNode parent = node.getParent();
		switch (parent.getType()) {
		case Fraction:
			thisSide = node;
			break;
		case Term:
			if (TypeSGET.Fraction.equals(parent.getParentType())) {
				thisSide = parent;
				break;
			}// else fall through
		default:
			return;
		}

		EquationNode otherSide = thisSide.getIndex() == 0 ? thisSide
				.getNextSibling() : thisSide.getPrevSibling();

		HashMap<EquationNode, DropType> dropTargets = new HashMap<EquationTree.EquationNode, DropType>();

		if (node.isLike(otherSide)) {// Cancel drop on entire other sides
			dropTargets.put(otherSide, DropType.CANCEL);

		} else if (TypeSGET.Term.equals(otherSide.getType())) {
			// Drop on each term child
			for (EquationNode child : otherSide.getChildren()) {
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
			for (Entry<EquationNode, DropType> dropTarget : dropTargets.entrySet()) {

				dragController.registerDropController(new InterFractionDrop(
						(AlgebaWrapper) dropTarget.getKey().getWrapper(),
						dropTarget.getValue()));
			}
		}

	}

	/**
	 * To be used by {@link #interFractionDrop_check}
	 */
	private static void addDropTarget(EquationNode target, EquationNode drag,
			HashMap<EquationTree.EquationNode, DropType> dropTargets) {
		
		TypeSGET dragType = drag.getType();
		
		if(TypeSGET.Number.equals(dragType) && "1".equals(drag.getSymbol())) {
			dropTargets.put(target, DropType.REMOVE_ONE);
			return;
		}

		// The rest of this method is only applicable if
		if (!dragType.equals(target.getType())) {
			return;
		}

		switch (dragType) {
		case Number:
			if (!"0".equals(drag.getSymbol())) {
				dropTargets.put(target, DropType.DIVIDE);
			}
			break;
		case Exponential:
			if (drag.getFirstChild().isLike(target.getFirstChild())) {
				dropTargets.put(target, DropType.EXPONENTIAL);
			}
			break;
		case Log:
			if (drag.getAttribute(MathAttribute.LogBase).equals(
					target.getAttribute(MathAttribute.LogBase))
					&& TypeSGET.Number.equals(drag.getFirstChild().getType())) {
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
					dropTargets.put(target, DropType.TRIG_COMBINE);
				}
			}
			break;
		}
	}

	public static TransformationButton denominatorFlip_check(EquationNode node,
			TransformationList<TransformationButton> context) {
		return new DenominatorFlipButton(node, context);
	}

}

// /////////////////////////////////////////////////////////////
// Transformation Buttons
// ////////////////////////////////////////////////////////////

/**
 * -x = -1 &middot; x
 * 
 */
class SeperateNegButton extends TransformationButton {
	private EquationNode negNode;
	SeperateNegButton(final EquationNode negNode, TransformationList<TransformationButton> context) {
		super("Seperate (-)", context);
		this.negNode = negNode;
	}
	@Override
	public
	void transform() {

		EquationNode prevSib = negNode.getPrevSibling();
		String newSymbol = negNode.getSymbol().replaceFirst(
				TypeSGET.Operator.MINUS.getSign(), "");
		negNode.setSymbol(newSymbol);

		if (prevSib != null
				&& TypeSGET.Operation.equals(prevSib.getType())) {
			if (Operator.PLUS.getSign().equals(prevSib.getSymbol())) {
				prevSib.setSymbol(Operator.MINUS.getSign());
			} else if (Operator.MINUS.getSign().equals(
					prevSib.getSymbol())) {
				prevSib.setSymbol(Operator.PLUS.getSign());
			} else {
				JSNICalls.error("The previous operator contains "
						+ "neither a plus or minus");
			}
		} else {
			EquationNode parent = negNode.getParent();
			parent = negNode.encase(TypeSGET.Term);
			int nodeIndex = negNode.getIndex();
			parent.addBefore(nodeIndex, TypeSGET.Operation,
					TypeSGET.Operator.getMultiply().getSign());
			parent.addBefore(nodeIndex, TypeSGET.Number, "-1");
		}
		Moderator.reloadEquationPanel("-" + newSymbol + " = -1"
				+ Operator.getMultiply().getSign() + newSymbol, null);
	}
}

/**
 * x / (y/z) = x &middot; (z/y)<br/>
 * x / y = x &middot; (1/y)
 */
class DenominatorFlipButton extends TransformationButton {
	private EquationNode node;
	DenominatorFlipButton(final EquationNode node, TransformationList<TransformationButton> context) {
		super("Flip Denominator",context);
		this.node = node;
	}
	@Override
	public
	void transform() {

		node.highlight();

		EquationNode frac = node;
		if (!TypeSGET.Fraction.equals(node.getType())) {
			frac = node.encase(TypeSGET.Fraction);
			frac.append(TypeSGET.Number, "1");
		}
		// Flip
		frac.append(frac.getChildAt(0));

		EquationNode parentFraction = frac.getParent();
		EquationNode grandParent = parentFraction.getParent();
		int index = parentFraction.getIndex();

		if (!TypeSGET.Term.equals(grandParent.getType())) {
			grandParent = parentFraction.encase(TypeSGET.Term);
			index = 0;
		}

		grandParent.addBefore(index, parentFraction.getChildAt(1));
		grandParent.addBefore(index, TypeSGET.Operation, Operator
				.getMultiply().getSign());
		grandParent.addBefore(index, parentFraction.getChildAt(0));

		parentFraction.remove();

		Moderator.reloadEquationPanel("Multiply by Resiprocal",
				Rule.FRACTION_DIVISION);
	}
}

