package com.sciencegadgets.client.algebra.transformations;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;

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
	public static TransformationList operation(EquationNode opNode) {
		EquationNode left, right = null;

		left = opNode.getPrevSibling();
		right = opNode.getNextSibling();

		if (left != null && left != null) {
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
				if (TypeEquationXML.Operation.equals(sumChild.getType())) {
					continue;
				}
				EquationNode prevOp = sumChild.getPrevSibling();
				if (prevOp != null && TypeEquationXML.Operation.equals(prevOp.getType())) {
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
			EquationNode casing = toNegate.encase(TypeEquationXML.Term);
			casing.addFirst(TypeEquationXML.Operation, Operator.getMultiply()
					.getSign());
			casing.addFirst(TypeEquationXML.Number, "-1");
			break;
		}
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

		if (TypeEquationXML.Fraction.equals(parent.getType())) {

			boolean inNumerator = toReciprocate.getIndex() == 0;
			EquationNode otherSide = inNumerator ? toReciprocate.getNextSibling()
					: toReciprocate.getPrevSibling();
			otherSide = otherSide.encase(TypeEquationXML.Term);

			otherSide
					.append(TypeEquationXML.Operation, Operator.getMultiply().getSign());
			otherSide.append(toReciprocate);

			if (inNumerator) {
				parent.addFirst(TypeEquationXML.Number, "1");
			} else {
				parent.replace(otherSide);
			}

		} else if (TypeEquationXML.Term.equals(parent.getType())
				&& TypeEquationXML.Fraction.equals(parent.getParentType())) {

			if (toReciprocate.getIndex() == 0) {
				toReciprocate.getNextSibling().remove();
			} else {
				toReciprocate.getPrevSibling().remove();
			}

			boolean inNumerator = parent.getIndex() == 0;
			EquationNode otherSide = inNumerator ? parent.getNextSibling() : parent
					.getPrevSibling();

			otherSide = otherSide.encase(TypeEquationXML.Term);
			otherSide
					.append(TypeEquationXML.Operation, Operator.getMultiply().getSign());
			otherSide.append(toReciprocate);

			parent.decase();

		} else {
			EquationNode frac = toReciprocate.encase(TypeEquationXML.Fraction);
			frac.addFirst(TypeEquationXML.Number, "1");
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
			TransformationList context) {
		if (negNode.getSymbol().startsWith(TypeEquationXML.Operator.MINUS.getSign())
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
			if (TypeEquationXML.Fraction.equals(parent.getParentType())) {
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

		} else if (TypeEquationXML.Term.equals(otherSide.getType())) {

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

		if (!drag.getType().equals(target.getType())) {
			return;
		}

		switch (target.getType()) {
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
					&& TypeEquationXML.Number.equals(drag.getFirstChild().getType())) {
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
			TransformationList context) {
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
	SeperateNegButton(final EquationNode negNode, TransformationList context) {
		super(context);

		setHTML("Seperate (-)");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				EquationNode prevSib = negNode.getPrevSibling();
				String newSymbol = negNode.getSymbol().replaceFirst(
						TypeEquationXML.Operator.MINUS.getSign(), "");
				negNode.setSymbol(newSymbol);

				if (prevSib != null
						&& TypeEquationXML.Operation.equals(prevSib.getType())) {
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
					parent = negNode.encase(TypeEquationXML.Term);
					int nodeIndex = negNode.getIndex();
					parent.addBefore(nodeIndex, TypeEquationXML.Operation,
							TypeEquationXML.Operator.getMultiply().getSign());
					parent.addBefore(nodeIndex, TypeEquationXML.Number, "-1");
				}
				Moderator.reloadEquationPanel("-" + newSymbol + " = -1"
						+ Operator.getMultiply().getSign() + newSymbol, null);
			}
		});
	}
}

/**
 * x / (y/z) = x &middot; (z/y)<br/>
 * x / y = x &middot; (1/y)
 */
class DenominatorFlipButton extends TransformationButton {
	DenominatorFlipButton(final EquationNode node, TransformationList context) {
		super(context);

		setHTML("Flip Denominator");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				node.highlight();

				EquationNode frac = node;
				if (!TypeEquationXML.Fraction.equals(node.getType())) {
					frac = node.encase(TypeEquationXML.Fraction);
					frac.append(TypeEquationXML.Number, "1");
				}
				// Flip
				frac.append(frac.getChildAt(0));

				EquationNode parentFraction = frac.getParent();
				EquationNode grandParent = parentFraction.getParent();
				int index = parentFraction.getIndex();

				if (!TypeEquationXML.Term.equals(grandParent.getType())) {
					grandParent = parentFraction.encase(TypeEquationXML.Term);
					index = 0;
				}

				grandParent.addBefore(index, parentFraction.getChildAt(1));
				grandParent.addBefore(index, TypeEquationXML.Operation, Operator
						.getMultiply().getSign());
				grandParent.addBefore(index, parentFraction.getChildAt(0));

				parentFraction.remove();

				Moderator.reloadEquationPanel("Multiply by Resiprocal",
						Rule.FRACTION_DIVISION);
			}
		});
	}
}

/**
 * Unravel function within inverse function or inverse function within function<br/>
 * sin(arcsin(x)) = x<br/>
 * arcsin(sin(x)) = x<br/>
 * log<sub>b</sub>(b<sup>x</sup>) = x<br/>
 * b<sup>log<sub>b</sub>(x)</sup> = x<br/>
 */
class UnravelButton extends TransformationButton {

	public UnravelButton(final EquationNode toReplace, final EquationNode replacement,
			final Rule rule, TransformationList context) {
		super(context);

		setHTML(replacement.getHTMLString(true, true));

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String changeComment = toReplace.getHTMLString(true, true) + " = "
						+ replacement.getHTMLString(true, true);
				
				toReplace.replace(replacement);
				
				Moderator.reloadEquationPanel(changeComment, rule);
			}
		});
	}
}
