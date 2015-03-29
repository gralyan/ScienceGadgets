/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.LinkedList;

import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.FactorTransformations.Match;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class FactorTransformations extends
		TransformationList<AddTransformButton> {
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
	private LinkedList<Match> matches;

	FactorButton(AdditionTransformations context,
			final LinkedList<Match> matches) {
		super(context);
		this.matches = matches;
		
		String matchHTML = "";
		for(Match m : matches) {
			matchHTML = matchHTML + m.leftFactor.getHTMLString(true, true);
		}
		setHTML("Factor: "+matchHTML);
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.FACTOR_POLYNOMIAL;
	}

	@Override
	public void transform() {

		for (Match match : matches) {
			match.leftFactor.highlight();
			match.rightFactor.highlight();
		}

		EquationNode term = parent
				.addAfter(right.getIndex(), TypeSGET.Term, "");
		for (Match match : matches) {
			term.append(match.leftFactor.clone());
			term.append(TypeSGET.Operation, Operator.getMultiply().getSign());
		}
		EquationNode sum = term.append(TypeSGET.Sum, "");
		sum.append(left);
		sum.append(operation);
		sum.append(right);

		if (isMinusBeforeLeft) {
			minusBeforeLeft.setSymbol(Operator.PLUS.getSign());
			sum.addFirst(TypeSGET.Operation, Operator.MINUS.getSign());
		}

		for (Match match : matches) {
			factorOut(match.leftFactor, left);
			factorOut(match.rightFactor, right);
		}

		// Combine like terms if appropriate
		if (Moderator.meetsRequirement(Badge.COMBINE_LIKE_TERMS)
				&& sum.getChildCount() == 3) {
			EquationNode sumLeft = sum.getChildAt(0);
			EquationNode sumOP = sum.getChildAt(1);
			EquationNode sumRight = sum.getChildAt(2);
			if (TypeSGET.Number.equals(sumLeft.getType())
					&& TypeSGET.Operation.equals(sumOP.getType())
					&& TypeSGET.Number.equals(sumRight.getType())) {
				BigDecimal leftValue, rightValue, combinedValue;
				leftValue = new BigDecimal(sumLeft.getSymbol());
				rightValue = new BigDecimal(sumRight.getSymbol());
				if (isMinus) {
					combinedValue = leftValue.subtract(rightValue);
				} else {
					combinedValue = leftValue.add(rightValue);
				}
				sum = sum.replace(TypeSGET.Number, combinedValue.toString());
				term.addFirst(sum.getPrevSibling());
				term.addFirst(sum);
			}
		}

		parent.decase();

		onTransformationEnd("Factor " + getHTML());
	}

	private void factorOut(EquationNode toFactor, EquationNode inSide) {
		if (toFactor == inSide) {
			toFactor.replace(TypeSGET.Number, "1");
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
			toFactor.replace(TypeSGET.Number, "1");
			break;
		case Exponential:
			EquationNode exponent = toFactor.getNextSibling();
			if (Moderator.meetsRequirement(Badge.EXP_ONE)
					&& TypeSGET.Number.equals(exponent.getType())) {
				BigDecimal expValue = new BigDecimal(exponent.getSymbol());
				if (expValue.compareTo(new BigDecimal("2")) == 0) {
					// No need to display 1 in exponent after 2 - 1
					toFactorParent.replace(toFactor);
				} else {
					exponent.setSymbol(expValue.subtract(new BigDecimal("1"))
							.toString());
				}
			} else {
				exponent = exponent.encase(TypeSGET.Term);
				exponent.append(TypeSGET.Operation, Operator.MINUS.getSign());
				exponent.append(TypeSGET.Number, "1");
			}
		}
	}

}
