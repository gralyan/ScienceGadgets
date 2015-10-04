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
import java.math.MathContext;

import com.google.gwt.core.shared.GWT;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.InterFractionTransformations.DropType;
import com.sciencegadgets.client.algebra.transformations.specification.NumberQuiz;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;
import com.sciencegadgets.shared.dimensions.UnitMap;

public class InterFractionButton extends TransformationButton {

	private EquationNode drag;
	private EquationNode target;
	private String dropHTML = "";
	private DropType dropType;
	private EquationNode zoomTo;
	protected Boolean reloadAlgebraActivity;

	public InterFractionButton(InterFractionTransformations context,
			EquationNode drag, EquationNode target, DropType dropType) {
		super(context);
		addStyleName(CSS.FRACTION);
		
		reloadAlgebraActivity = context.reloadAlgebraActivity;
		this.drag = drag;
		this.target = target;
		this.dropType = dropType;
		zoomTo = target;
		
		dropHTML = initDropHTML();
		setHTML(dropHTML);

	}

	String initDropHTML() {
		return dropHTML = "<div style=\"display:inline-block; vertical-align:middle;\">"
				+ "<div style=\"border-bottom:1px solid;\">"
				+ target.getHTMLString(true, true)
				+ "</div>"
				+ "<div>"
				+ drag.getHTMLString(true, true) + "</div>" + "</div>";
	}

	@Override
	public void transform() {

		switch (dropType) {
		case CANCEL:
			cancelDrop();
			break;
		case REMOVE_ONE:
			complete(true);
			break;
		case DIVIDE:
			dividePrompt();
			break;
		case EXPONENTIAL:
			exponentialDrop();
			break;
		case LOG_COMBINE:
			logDrop();
			break;
		case TRIG_COMBINE:
			trigDrop();
			break;
		}
	}

	private void setDropHTMLTotal(String total) {
		dropHTML = dropHTML
				+ "<div style=\"display:inline-block; vertical-align:middle;\">="
				+ total + "</div>";
	}

	public EquationNode getTarget() {
		return target;
	}

	public DropType getDropType() {
		return dropType;
	}

	/**
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of equivalent<br/>
	 */
	private void cancelDrop() {

		if(reloadAlgebraActivity) {
			target.lineThrough();
		}
		
		if (TypeSGET.Fraction.equals(drag.getParentType())
				&& TypeSGET.Fraction.equals(target.getParentType())) {
			EquationNode one = target.getParent().replace(TypeSGET.Number, "1");
			zoomTo = one;
			complete(false);
		} else {
			zoomTo = cleanSide(target);
			complete(true);
		}
	}

	/**
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of type {@link TypeSGET#Number}<br/>
	 */
	private void dividePrompt() {
		try {
			// if (Moderator.meetsRequirements(Badge.DROP_CANCEL,
			// Badge.FACTOR_NUMBERS)
			// //TODO simplify method not complete
			// //&& FractionTransformations.SIMPLIFY_FRACTION(drag, target,true)
			// ) {
			// complete(false);
			// return;
			// }

			BigDecimal targetNumber = new BigDecimal(target.getSymbol());
			BigDecimal dragNumber = new BigDecimal(drag.getSymbol());
			final BigDecimal total = targetNumber.divide(dragNumber,
					MathContext.DECIMAL128);

			boolean meetsRequirements = Moderator
					.meetsRequirements(Badge.DIVIDE_NUMBERS);

			if (meetsRequirements) {
				divide(total);

			} else {// prompt

				String question = target.getSymbol() + " "
						+ Operator.DIVIDE.getSign() + " " + drag.getSymbol();
				NumberQuiz prompt = new NumberQuiz(question, total) {
					@Override
					public void onIncorrect() {
						super.onIncorrect();
					}

					@Override
					public void onCorrect() {
						super.onCorrect();
						divide(total);
						Moderator.getStudent().increaseSkill(
								Skill.DIVIDE_NUMBERS, 1);
					}
				};
				prompt.appear();
			}

		} catch (NumberFormatException e) {
			return;
		}
	}

	private void divide(BigDecimal total) {

		UnitMap combinedMap = target.getUnitMap()
				.getDivision(drag.getUnitMap());

		String totalString = total.stripTrailingZeros().toEngineeringString();
		EquationNode division = target.replace(TypeSGET.Number, totalString);
		String divisionUnits = combinedMap.getUnitAttribute().toString();
		division.setAttribute(MathAttribute.Unit, divisionUnits);

		setDropHTMLTotal(totalString);

		zoomTo = division;
		complete(true);
	}

	/**
	 * sin(x) / cos(x) = tan(x)<br/>
	 * cos(x) / sin(x) = cot(x)<br/>
	 * Always target / drag<br/>
	 * <br/>
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of type {@link TypeSGET#Trig}<br/>
	 * 2. One of the nodes(drag or target) is Sin and the other is Cos<br/>
	 * 3. Both nodes(drag or target) have same argument.<br/>
	 */
	private void trigDrop() {
		TrigFunctions targetFunction = TrigFunctions.valueOf(target
				.getAttribute(MathAttribute.Function));
		TrigFunctions finalFunction = null;

		if (TrigFunctions.sin.equals(targetFunction)) {
			finalFunction = TrigFunctions.tan;
		} else if (TrigFunctions.cos.equals(targetFunction)) {
			finalFunction = TrigFunctions.cot;
		} else {
			// This should never happen if this Drop Controller was created
			// appropriately
			JSNICalls.error("Target node must be sin or cos " + target);
			return;
		}

		String finalFunctionString = finalFunction.toString();

		target.setAttribute(MathAttribute.Function, finalFunctionString);

		setDropHTMLTotal(finalFunctionString);

		complete(true);
	}

	/**
	 * log<sub>a</sub>(x) / log<sub>a</sub>(y) = log<sub>y</sub>(x)<br/>
	 * Always target / drag<br/>
	 * <br/>
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of type {@link TypeSGET#Log}<br/>
	 * 2. The bases of both nodes (drag and target) are the same<br/>
	 * 3. The drag's child is a number<br/>
	 */
	private void logDrop() {
		String newBase = drag.getFirstChild().getSymbol();

		target.setAttribute(MathAttribute.LogBase, newBase);

		setDropHTMLTotal(newBase);

		complete(true);
	}

	/**
	 * b<sup>x</sup> / b<sup>y</sup> = b<sup>x-y</sup><br/>
	 * Always target / drag<br/>
	 * <br/>
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of type {@link TypeSGET#Exponential}<br/>
	 * 2. The bases of both nodes (drag and target) are the same<br/>
	 */
	private void exponentialDrop() {
		EquationNode dragExp = drag.getChildAt(1);
		EquationNode targetExp = target.getChildAt(1);
		if (// Moderator.meetsRequirement(Badge.EXP_DROP_ARITHMETIC)&&
		TypeSGET.Number.equals(dragExp.getType())
				&& TypeSGET.Number.equals(targetExp.getType())) {
			BigDecimal dragValue = new BigDecimal(dragExp.getSymbol());
			BigDecimal targetValue = new BigDecimal(targetExp.getSymbol());
			BigDecimal total = targetValue.subtract(dragValue);
			targetExp.setSymbol(total + "");
		} else {
			EquationNode targetExpSum = targetExp.encase(TypeSGET.Sum);

			targetExpSum.append(TypeSGET.Operation, Operator.MINUS.getSign());
			targetExpSum.append(dragExp);
		}

		setDropHTMLTotal(target.getHTMLString(true, true) + "<sup>("
				+ targetExp.getHTMLString(true, true) + "-"
				+ dragExp.getHTMLString(true, true) + ")</sup>");

		complete(true);
	}

	private void complete(boolean removeDrag) {

		if (removeDrag) {
			cleanSide(drag);
		}

		if(reloadAlgebraActivity) {
			drag.lineThrough();
			target.highlight();
		}
		onTransformationEnd(dropHTML, zoomTo);

	}

	/**
	 * Clean up both drag side and target side
	 * 
	 * @param side
	 * @return appropriate node to zoom to
	 */
	public static EquationNode cleanSide(EquationNode side) {

		EquationNode sideParent = side.getParent();
		EquationNode sideGrandParent = sideParent.getParent();
		if (TypeSGET.Fraction.equals(sideParent.getType())) {
			if (side.getIndex() == 0) {// numerator
				return side.replace(TypeSGET.Number, "1");
			} else {// denominator
				EquationNode frac = side.getParent();
				return frac.replace(frac.getChildAt(0));
			}
		} else {
			EquationNode sideOp = null;
			if (side.getIndex() == 0) {
				sideOp = side.getNextSibling();
			} else {
				sideOp = side.getPrevSibling();
			}
			if (sideOp != null && TypeSGET.Operation.equals(sideOp.getType())) {
				sideOp.remove();
			}

			side.remove();
			sideParent.decase();
			return sideGrandParent;
		}
	}

	@Override
	public Badge getAssociatedBadge() {

		switch (dropType) {
		case CANCEL:
			return Badge.DROP_CANCEL;
		case DIVIDE:
			return Badge.DROP_DIVIDE;
		case EXPONENTIAL:
			return Badge.DROP_EXPONENTIAL;
		case LOG_COMBINE:
			return Badge.DROP_LOG;
		case TRIG_COMBINE:
			return Badge.DROP_TRIG;
		case REMOVE_ONE:
			return null;
		}
		return null;
	}

	@Override
	public boolean meetsAutoTransform() {
		return true;
	}
}