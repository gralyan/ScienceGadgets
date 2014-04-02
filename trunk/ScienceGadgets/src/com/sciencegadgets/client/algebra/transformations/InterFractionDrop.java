package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.math.MathContext;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebaWrapper;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.ResponseNote;
import com.sciencegadgets.client.algebra.transformations.specification.NumberPrompt;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;
import com.sciencegadgets.shared.dimensions.UnitMap;

public class InterFractionDrop extends TransformationDropController {

	private DropType dropType;
	private EquationNode drag;
	private EquationNode target;
	private String dropHTML = "";

	public enum DropType {
		CANCEL, REMOVE_ONE, DIVIDE, EXPONENTIAL, LOG_COMBINE, TRIG_COMBINE;
	}

	public InterFractionDrop(AlgebaWrapper dropTarget, DropType dropType) {
		super(dropTarget);

		this.dropType = dropType;

		switch (dropType) {
		case CANCEL:
			response.setText(ResponseNote.Cancel.toString());
			break;
		case DIVIDE:
		case REMOVE_ONE:
			response.setText(ResponseNote.Divide.toString());
			break;
		case EXPONENTIAL:
		case LOG_COMBINE:
		case TRIG_COMBINE:
			response.setText(ResponseNote.Combine.toString());
			break;
		}
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		drag = ((AlgebaWrapper) context.draggable).getNode();
		target = ((AlgebaWrapper) getDropTarget()).getNode();

		dropHTML = "<div style=\"display:inline-block; vertical-align:middle;\">"
				+ "<div style=\"border-bottom:1px solid;\">"
				+ target.getHTMLString(true, true)
				+ "</div>"
				+ "<div>"
				+ drag.getHTMLString(true, true) + "</div>" + "</div>";

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

	/**
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of equivalent<br/>
	 */
	private void cancelDrop() {

		target.lineThrough();

		if (TypeSGET.Fraction.equals(drag.getParentType())
				&& TypeSGET.Fraction.equals(target.getParentType())
				&& TypeSGET.Equation.equals(target.getParent()
						.getParentType())) {
			target.getParent().replace(TypeSGET.Number, "1");
			complete(false);
		} else {
			cleanSide(target);
			complete(true);
		}
	}

	/**
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of type
	 * {@link TypeSGET#Number}<br/>
	 */
	private void dividePrompt() {
		try {
			BigDecimal targetNumber = new BigDecimal(target.getSymbol());
			BigDecimal dragNumber = new BigDecimal(drag.getSymbol());
			final BigDecimal total = targetNumber.divide(dragNumber,
					MathContext.DECIMAL128);

			if (Moderator.isInEasyMode) {
				divide(total);

			} else {// prompt

				String question = target.getSymbol() + " "
						+ Operator.DIVIDE.getSign() + " " + drag.toString()
						+ " = ";
				NumberPrompt prompt = new NumberPrompt(question, total) {
					@Override
					public void onCorrect() {
						divide(total);
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
		EquationNode division = target.replace(TypeSGET.Number,
				totalString);
		String divisionUnits = combinedMap.getUnitAttribute().toString();
		division.setAttribute(MathAttribute.Unit, divisionUnits);

		setDropHTMLTotal(totalString);

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
	 * 1. Both nodes (drag and target) are of type
	 * {@link TypeSGET#Exponential}<br/>
	 * 2. The bases of both nodes (drag and target) are the same<br/>
	 */
	private void exponentialDrop() {
		EquationNode dragExp = drag.getChildAt(1);
		EquationNode targetExp = target.getChildAt(1);
		if (Moderator.isInEasyMode
				&& TypeSGET.Number.equals(dragExp.getType())
				&& TypeSGET.Number.equals(targetExp.getType())) {
			BigDecimal dragValue = new BigDecimal(dragExp.getSymbol());
			BigDecimal targetValue = new BigDecimal(targetExp.getSymbol());
			BigDecimal total = targetValue.subtract(dragValue);
			targetExp.setSymbol(total + "");
		} else {
			EquationNode targetExpSum = targetExp.encase(TypeSGET.Sum);

			targetExpSum.append(TypeSGET.Operation,
					Operator.MINUS.getSign());
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

		drag.lineThrough();
		target.highlight();

		switch (dropType) {
		case CANCEL:
		case REMOVE_ONE:
			Moderator.reloadEquationPanel(response.getText(),
					Rule.CANCELLING_FRACTIONS);

			break;
		case DIVIDE:
			Moderator.reloadEquationPanel(dropHTML, Rule.DIVISION);
			break;
		case EXPONENTIAL:
			Moderator.reloadEquationPanel(dropHTML, Rule.EXPONENT_PROPERTIES);
			break;
		case LOG_COMBINE:
			Moderator.reloadEquationPanel(dropHTML, Rule.LOGARITHM);
			break;
		case TRIG_COMBINE:
			Moderator.reloadEquationPanel(dropHTML,
					Rule.TRIGONOMETRIC_FUNCTIONS);
			break;
		}
	}

	// Clean up both drag side and target side
	private void cleanSide(EquationNode side) {

		EquationNode sideParent = side.getParent();
		if (TypeSGET.Fraction.equals(sideParent.getType())) {
			if (side.getIndex() == 0) {// numerator
				side.replace(TypeSGET.Number, "1");
			} else {// denominator
				EquationNode frac = side.getParent();
				frac.replace(frac.getChildAt(0));
			}
		} else {
			EquationNode sideOp = null;
			if (side.getIndex() == 0) {
				sideOp = side.getNextSibling();
			} else {
				sideOp = side.getPrevSibling();
			}
			if (sideOp != null
					&& TypeSGET.Operation.equals(sideOp.getType())) {
				sideOp.remove();
			}
			side.remove();
			sideParent.decase();
		}
	}

}
