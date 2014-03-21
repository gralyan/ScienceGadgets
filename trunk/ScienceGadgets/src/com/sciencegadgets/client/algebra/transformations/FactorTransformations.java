package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.FactorTransformations.Match;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;

public class FactorTransformations extends TransformationList {
	private static final long serialVersionUID = -6071971827855796001L;

	private AdditionTransformations addT;
	LinkedList<EquationNode> factorablesLeft = new LinkedList<EquationNode>();
	LinkedList<EquationNode> factorablesRight = new LinkedList<EquationNode>();
	LinkedList<Match> matches = new LinkedList<Match>();

	public FactorTransformations(AdditionTransformations additionTransformations) {
		super(additionTransformations.operation);
		this.addT = additionTransformations;

		addAll(factor_check());
	}

	LinkedList<FactorButton> factor_check() {

		collectFactorables(addT.left, factorablesLeft);
		collectFactorables(addT.right, factorablesRight);

		for (EquationNode fLeft : factorablesLeft) {
			for (EquationNode fRight : factorablesRight) {
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

	private void collectFactorables(EquationNode node,
			LinkedList<EquationNode> factorables) {
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
			for (EquationNode termChild : node.getChildren()) {
				collectFactorables(termChild, factorables);
			}
			break;
		}
	}

	private void climbMatch(EquationNode leftFactor, EquationNode rightFactor) {

		// Don't climb past selected node
		if (addT.left == leftFactor || addT.right == rightFactor) {
			addMatch(new Match(leftFactor, rightFactor));
			return;
		}

		EquationNode leftFactorParent = leftFactor.getParent();
		EquationNode rightFactorParent = rightFactor.getParent();

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
		EquationNode leftFactor;
		EquationNode leftFactorParent;
		EquationNode rightFactor;
		EquationNode rightFactorParent;

		LinkedList<Match> siblingMatches = new LinkedList<Match>();

		Match(EquationNode leftFactor, EquationNode rightFactor) {
			this.leftFactor = leftFactor;
			this.leftFactorParent = leftFactor.getParent();
			this.rightFactor = rightFactor;
			this.rightFactorParent = rightFactor.getParent();

			siblingMatches.add(this);
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

				EquationNode term = parent.addAfter(right.getIndex(), TypeEquationXML.Term,
						"");
				for (Match match : matches) {
					term.append(match.leftFactor.clone());
					term.append(TypeEquationXML.Operation, Operator.getMultiply()
							.getSign());
				}
				EquationNode sum = term.append(TypeEquationXML.Sum, "");
				sum.append(left);
				sum.append(operation);
				sum.append(right);

				if (isMinusBeforeLeft) {
					minusBeforeLeft.setSymbol(Operator.PLUS.getSign());
					sum.addFirst(TypeEquationXML.Operation, Operator.MINUS.getSign());
				}

				for (Match match : matches) {
					factorOut(match.leftFactor, left);
					factorOut(match.rightFactor, right);
				}

				// Combine like terms if appropriate
				if (Moderator.isInEasyMode && sum.getChildCount() == 3) {
					EquationNode sumLeft = sum.getChildAt(0);
					EquationNode sumOP = sum.getChildAt(1);
					EquationNode sumRight = sum.getChildAt(2);
					if (TypeEquationXML.Number.equals(sumLeft.getType())
							&& TypeEquationXML.Operation.equals(sumOP.getType())
							&& TypeEquationXML.Number.equals(sumRight.getType())) {
						BigDecimal leftValue, rightValue, combinedValue;
						leftValue = new BigDecimal(sumLeft.getSymbol());
						rightValue = new BigDecimal(sumRight.getSymbol());
						if (isMinus) {
							combinedValue = leftValue.subtract(rightValue);
						} else {
							combinedValue = leftValue.add(rightValue);
						}
						sum = sum.replace(TypeEquationXML.Number,
								combinedValue.toString());
						term.addFirst(sum.getPrevSibling());
						term.addFirst(sum);
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

	private void factorOut(EquationNode toFactor, EquationNode inSide) {
		if (toFactor == inSide) {
			toFactor.replace(TypeEquationXML.Number, "1");
			return;
		}

		EquationNode toFactorParent = toFactor.getParent();

		switch (toFactorParent.getType()) {
		case Term:
			EquationNode opPrev = toFactor.getPrevSibling();
			EquationNode opNext = toFactor.getNextSibling();
			if (opPrev != null) {
				opPrev.remove();
			} else if (opNext != null) {
				opNext.remove();
			}
			toFactor.remove();
			toFactorParent.decase();
			break;
		case Fraction:
			toFactor.replace(TypeEquationXML.Number, "1");
			break;
		case Exponential:
			EquationNode exponent = toFactor.getNextSibling();
			if (Moderator.isInEasyMode
					&& TypeEquationXML.Number.equals(exponent.getType())) {
				BigDecimal expValue = new BigDecimal(exponent.getSymbol());
				if (expValue.compareTo(new BigDecimal("2")) == 0) {
					// No need to display 1 in exponent after 2 - 1
					toFactorParent.replace(toFactor);
				} else {
					exponent.setSymbol(expValue.subtract(new BigDecimal("1"))
							.toString());
				}
			} else {
				exponent = exponent.encase(TypeEquationXML.Term);
				exponent.append(TypeEquationXML.Operation, Operator.MINUS.getSign());
				exponent.append(TypeEquationXML.Number, "1");
			}
		}
	}

}
