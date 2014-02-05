package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.math.MathContext;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebaWrapper;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.ResponseNote;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
import com.sciencegadgets.shared.TypeML.TrigFunctions;
import com.sciencegadgets.shared.UnitMap;

public class InterFractionDrop extends AbstractDropController {

	Label response = new Label();
	private DropType dropType;
	private MathNode drag;
	private MathNode target;
	private String dropHTML = "";

	public enum DropType {
		CANCEL, DIVIDE, EXPONENTIAL, LOG_COMBINE, TRIG_COMBINE;
	}

	public InterFractionDrop(AlgebaWrapper dropTarget, DropType dropType) {
		super(dropTarget);

		this.dropType = dropType;
		response.getElement().getStyle().setBackgroundColor("white");

		switch (dropType) {
		case CANCEL:
			response.setText(ResponseNote.Cancel.toString());
			break;
		case DIVIDE:
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

		dropHTML = "<div style=\"display:inline-block;\"><div>"
				+ target.getHTMLString() + "</div><div>" + drag.getHTMLString()
				+ "</div><div>";

		switch (dropType) {
		case CANCEL:
			complete();
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

	/**
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of type {@link TypeML#Number}<br/>
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
					void onCorrect() {
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

		MathNode division = target.replace(TypeML.Number, total
				.stripTrailingZeros().toEngineeringString());
		String divisionUnits = combinedMap.getUnitAttribute();
		division.setAttribute(MathAttribute.Unit, divisionUnits);

		complete();
	}

	/**
	 * sin(x) / cos(x) = tan(x)<br/>
	 * cos(x) / sin(x) = cot(x)<br/>
	 * Always target / drag<br/>
	 * <br/>
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of type {@link TypeML#Trig}<br/>
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

		target.setAttribute(MathAttribute.Function, finalFunction.toString());

		complete();
	}

	/**
	 * log<sub>a</sub>(x) / log<sub>a</sub>(y) = log<sub>y</sub>(x)<br/>
	 * Always target / drag<br/>
	 * <br/>
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of type {@link TypeML#Log}<br/>
	 * 2. The bases of both nodes (drag and target) are the same<br/>
	 * 3. The drag's child is a number<br/>
	 */
	private void logDrop() {
		String newBase = drag.getFirstChild().getSymbol();

		target.setAttribute(MathAttribute.LogBase, newBase);

		complete();
	}

	/**
	 * b<sup>x</sup> / b<sup>y</sup> = b<sup>x-y</sup><br/>
	 * Always target / drag<br/>
	 * <br/>
	 * Already assured from {@link AlgebraicTransformations#addDropTarget} that:<br/>
	 * 1. Both nodes (drag and target) are of type {@link TypeML#Exponential}<br/>
	 * 2. The bases of both nodes (drag and target) are the same<br/>
	 */
	private void exponentialDrop() {
		MathNode dragExp = drag.getChildAt(1);
		MathNode targetExp = target.getChildAt(1);
		if (Moderator.isInEasyMode && TypeML.Number.equals(dragExp.getType())
				&& TypeML.Number.equals(targetExp.getType())) {
			BigDecimal dragValue = new BigDecimal(dragExp.getSymbol());
			BigDecimal targetValue = new BigDecimal(targetExp.getSymbol());
			BigDecimal total = targetValue.subtract(dragValue);
			targetExp.setSymbol(total+"");
		} else {
			targetExp = targetExp.encase(TypeML.Sum);

			targetExp.append(TypeML.Operation, Operator.MINUS.getSign());
			targetExp.append(dragExp);
		}
		complete();
	}

	private void complete() {

		switch (dropType) {
		case CANCEL:
			cleanSide(target);
		case DIVIDE:
		case EXPONENTIAL:
		case LOG_COMBINE:
		case TRIG_COMBINE:
			cleanSide(drag);
			break;
		}

		drag.lineThrough();
		target.highlight();

		switch (dropType) {
		case CANCEL:
			Moderator.reloadEquationPanel("Cancellation",
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
	private void cleanSide(MathNode side) {

		MathNode sideParent = side.getParent();
		if (TypeML.Fraction.equals(sideParent.getType())) {
			if (side.getIndex() == 0) {// numerator
				side.replace(TypeML.Number, "1");
			} else {// denominator
				MathNode frac = side.getParent();
				frac.replace(frac.getChildAt(0));
			}
		} else {
			MathNode sideOp = null;
			if (side.getIndex() == 0) {
				sideOp = side.getNextSibling();
			} else {
				sideOp = side.getPrevSibling();
			}
			if (sideOp != null && TypeML.Operation.equals(sideOp.getType())) {
				sideOp.remove();
			}
			side.remove();
			sideParent.decase();
		}
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName(CSS.SELECTED_DROP_WRAPPER);
		Moderator.getCurrentAlgebraActivity().lowerEqArea.clear();
		Moderator.getCurrentAlgebraActivity().lowerEqArea.add(response);
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName(CSS.SELECTED_DROP_WRAPPER);
		Moderator.getCurrentAlgebraActivity().lowerEqArea.remove(response);
	}

}
