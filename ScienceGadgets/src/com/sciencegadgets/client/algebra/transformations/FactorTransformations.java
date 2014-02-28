package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.transformations.FactorTransformations.Match;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class FactorTransformations extends TransformationList {
	private static final long serialVersionUID = -6071971827855796001L;

	private AdditionTransformations addT;
	LinkedList<MathNode> factorablesLeft = new LinkedList<MathNode>();
	LinkedList<MathNode> factorablesRight = new LinkedList<MathNode>();
	LinkedList<Match> matches = new LinkedList<Match>();

	public FactorTransformations(AdditionTransformations additionTransformations) {
		super(additionTransformations.operation);
		this.addT = additionTransformations;

		addAll(factor_check());
	}

	LinkedList<FactorButton> factor_check() {

		collectFactorables(addT.left, factorablesLeft);
		collectFactorables(addT.right, factorablesRight);

		for (MathNode fLeft : factorablesLeft) {
			for (MathNode fRight : factorablesRight) {
				if (fLeft.isLike(fRight)) {
					climbMatch(fLeft, fRight);
				}
			}
		}

		LinkedList<FactorButton> factorButtons = new LinkedList<FactorButton>();
		for (Match match : matches) {
			factorButtons.add(new FactorButton(addT, match.siblingMatches));
		}
		return factorButtons;
	}

	private void collectFactorables(MathNode node,
			LinkedList<MathNode> factorables) {
		switch (node.getType()) {
		case Variable:
		case Number:
		case Trig:
		case Log:
		case Sum:
			factorables.add(node);
			break;
		case Exponential:
			factorables.add(node.getFirstChild());
			break;
		case Fraction:
			collectFactorables(node.getFirstChild(), factorables);
			break;
		case Term:
			for (MathNode termChild : node.getChildren()) {
				collectFactorables(termChild, factorables);
			}
			break;
		}
	}

	private void climbMatch(MathNode leftFactor, MathNode rightFactor) {

		// Don't climb past selected node
		if (addT.left == leftFactor || addT.right == rightFactor) {
			addMatch(new Match(leftFactor, rightFactor));
			return;
		}

		MathNode leftFactorParent = leftFactor.getParent();
		MathNode rightFactorParent = rightFactor.getParent();

		if (leftFactorParent.isLike(rightFactorParent)) {
			climbMatch(leftFactorParent, rightFactorParent);
		} else {
			addMatch(new Match(leftFactor, rightFactor));
			return;
		}
	}

	private void addMatch(Match newMatch) {

		for (Match match : matches) {
			if ((match.leftFactor == newMatch.leftFactor && match.rightFactorParent == newMatch.rightFactorParent)
					|| (match.rightFactor == newMatch.rightFactor && match.leftFactorParent == newMatch.leftFactorParent)) {
				return;
			} else if ((match.leftFactorParent == newMatch.leftFactorParent && match.rightFactorParent == newMatch.rightFactorParent)
					|| (match.rightFactorParent == newMatch.rightFactorParent && match.leftFactorParent == newMatch.leftFactorParent)) {
				match.siblingMatches.add(newMatch);
				return;
			}
		}
		matches.add(newMatch);
	}

	class Match {
		MathNode leftFactor;
		MathNode leftFactorParent;
		MathNode rightFactor;
		MathNode rightFactorParent;

		LinkedList<Match> siblingMatches = new LinkedList<Match>();

		Match(MathNode leftFactor, MathNode rightFactor) {
			this.leftFactor = leftFactor;
			this.leftFactorParent = leftFactor.getParent();
			this.rightFactor = rightFactor;
			this.rightFactorParent = rightFactor.getParent();

			siblingMatches.add(this);
		}

		@Override
		public String toString() {
			return leftFactor.getHTMLString();
		}
	}
}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////

/**
 * All Factors<br/>
 * x + x&middot;x = x&middot;(1 + x)<br/>
 * x&middot;x + x/y = x&middot;(x + 1/y)<br/>
 * x/y + x<sup>a</sup> = x&middot;(1/y + x<sup>a-1</sup>)<br/>
 */
class FactorButton extends AddTransformButton {
	FactorButton(AdditionTransformations context,
			final LinkedList<Match> matches) {
		super(context, "Factor " + matches);

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				for (Match match : matches) {
					match.leftFactor.highlight();
					match.rightFactor.highlight();
				}

				MathNode term = parent.addAfter(right.getIndex(), TypeML.Term,
						"");
				for (Match match : matches) {
					term.append(match.leftFactor.clone());
					term.append(TypeML.Operation, Operator.getMultiply()
							.getSign());
				}
				MathNode sum = term.append(TypeML.Sum, "");
				sum.append(left);
				sum.append(operation);
				sum.append(right);

				if (isMinusBeforeLeft) {
					minusBeforeLeft.setSymbol(Operator.PLUS.getSign());
					sum.addBefore(0, TypeML.Operation, Operator.MINUS.getSign());
				}

				for (Match match : matches) {
					factorOut(match.leftFactor, left);
					factorOut(match.rightFactor, right);
				}

				// Combine like terms if appropriate
				if (Moderator.isInEasyMode && sum.getChildCount() == 3) {
					MathNode sumLeft = sum.getChildAt(0);
					MathNode sumOP = sum.getChildAt(1);
					MathNode sumRight = sum.getChildAt(2);
					if (TypeML.Number.equals(sumLeft.getType())
							&& TypeML.Operation.equals(sumOP.getType())
							&& TypeML.Number.equals(sumRight.getType())) {
						BigDecimal leftValue, rightValue, combinedValue;
						leftValue = new BigDecimal(sumLeft.getSymbol());
						rightValue = new BigDecimal(sumRight.getSymbol());
						if (isMinus) {
							combinedValue = leftValue.subtract(rightValue);
						} else {
							combinedValue = leftValue.add(rightValue);
						}
						sum = sum.replace(TypeML.Number,
								combinedValue.toString());
						term.addBefore(0, sum.getPrevSibling());
						term.addBefore(0, sum);
					}
				}

				parent.decase();

				if (reloadAlgebraActivity) {
					Moderator.reloadEquationPanel("Factor " + getHTML(),
							Rule.FACTORIZATION);
				}
			}
		});
	}

	private void factorOut(MathNode toFactor, MathNode inSide) {
		if (toFactor == inSide) {
			toFactor.replace(TypeML.Number, "1");
			return;
		}

		MathNode toFactorParent = toFactor.getParent();

		switch (toFactorParent.getType()) {
		case Term:
			MathNode opPrev = toFactor.getPrevSibling();
			MathNode opNext = toFactor.getNextSibling();
			if (opPrev != null) {
				opPrev.remove();
			} else if (opNext != null) {
				opNext.remove();
			}
			toFactor.remove();
			toFactorParent.decase();
			break;
		case Fraction:
			toFactor.replace(TypeML.Number, "1");
			break;
		case Exponential:
			MathNode exponent = toFactor.getNextSibling();
			if (Moderator.isInEasyMode
					&& TypeML.Number.equals(exponent.getType())) {
				BigDecimal expValue = new BigDecimal(exponent.getSymbol());
				if (expValue.compareTo(new BigDecimal("2")) == 0) {
					// No need to display 1 in exponent after 2 - 1
					toFactorParent.replace(toFactor);
				} else {
					exponent.setSymbol(expValue.subtract(new BigDecimal("1"))
							.toString());
				}
			} else {
				exponent = exponent.encase(TypeML.Term);
				exponent.append(TypeML.Operation, Operator.MINUS.getSign());
				exponent.append(TypeML.Number, "1");
			}
		}
	}

}
