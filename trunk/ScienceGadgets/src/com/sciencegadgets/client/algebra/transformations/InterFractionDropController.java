package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.AlgebaWrapper;
import com.sciencegadgets.client.algebra.ResponseNote;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
import com.sciencegadgets.shared.TypeML.TrigFunctions;
import com.sciencegadgets.shared.UnitUtil;

public class InterFractionDropController extends AbstractDropController {

	Label response = new Label();
	private DropType dropType;
	private MathNode drag;
	private MathNode target;

	public enum DropType {
		CANCEL, DIVIDE, LOG_COMBINE, TRIG_COMBINE;
	}

	public InterFractionDropController(AlgebaWrapper dropTarget,
			DropType dropType) {
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

		switch (dropType) {
		case CANCEL:
			cleanUp();
			break;
		case DIVIDE:
			dividePrompt();
			break;
		case LOG_COMBINE:
			logDrop();
			break;
		case TRIG_COMBINE:
			trigDropPrompt();
			break;
		}

	}

	private void dividePrompt() {
		try {
			BigDecimal targetNumber = new BigDecimal(target.getSymbol());
			BigDecimal dragNumber = new BigDecimal(drag.getSymbol());
			final BigDecimal total = targetNumber.divide(dragNumber,
					MathContext.DECIMAL32);

			if (AlgebraActivity.isInEasyMode) {
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
		
		// Combine units by subtracting exponent of the drop map units from
		// matching target units
		LinkedHashMap<String, Integer> targetMap = target.getUnitMap();
		LinkedHashMap<String, Integer> dragMap = drag.getUnitMap();
		for (String dragName : dragMap.keySet()) {
			Integer targetExp = targetMap.get(dragName);
			if (targetExp == null) {
				targetExp = new Integer(0);
			}
			targetMap.put(dragName, targetExp - dragMap.get(dragName));
		}
		
		MathNode division = target.replace(TypeML.Number, total
				.stripTrailingZeros().toEngineeringString());
		String divisionUnits = UnitUtil.getUnitAttribute(targetMap);
		division.setAttribute(MathAttribute.Unit, divisionUnits);
		
		cleanUp();
	}
	
	//TODO
	private void trigDropPrompt() {
			TrigFunctions targetFunction = TrigFunctions.valueOf(target.getAttribute(MathAttribute.Function));
			TrigFunctions dragFunction = TrigFunctions.valueOf(drag.getAttribute(MathAttribute.Function));
			System.out.println(targetFunction +"/"+dragFunction);
			
//			if (AlgebraActivity.isInEasyMode) {
////				divide(total);
//				
//			} else {// prompt
//				
//				String question = target.getSymbol() + " "
//						+ Operator.DIVIDE.getSign() + " " + drag.toString()
//						+ " = ";
//				NumberPrompt prompt = new NumberPrompt(question, total) {
//					@Override
//					void onCorrect() {
////						divide(total);
//					}
//				};
//				prompt.appear();
//			}
			
	}
	
	//TODO
	private void logDrop() {
		MathNode targetChild = target.getFirstChild();
		MathNode dragChild = drag.getFirstChild();
		
		System.out.println(targetChild +"\n / \n"+dragChild);
	}

	private void cleanUp() {
		ArrayList<MathNode> sidesToRemove = new ArrayList<MathNode>();
		switch (dropType) {
		case CANCEL:
			sidesToRemove.add(drag);
			sidesToRemove.add(target);
			break;
		case DIVIDE:
			sidesToRemove.add(drag);
			break;
		case LOG_COMBINE:
			break;
		case TRIG_COMBINE:
			break;
		}

		MathNode denomToRemove = null;

		// Clean up both drag side and target side
		for (MathNode side : sidesToRemove) {

			MathNode sideParent = side.getParent();
			if (TypeML.Fraction.equals(sideParent.getType())) {
				if (side.getIndex() == 0) {// numerator
					side.replace(TypeML.Number, "1");
				} else {// denominator
					denomToRemove = side;
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

		if (denomToRemove != null) {
			MathNode frac = denomToRemove.getParent();
			MathNode fracParent = frac.getParent();
			fracParent.addBefore(frac.getIndex(), frac.getChildAt(0));
			denomToRemove.remove();
			frac.remove();
			fracParent.decase();
		}

		drag.lineThrough();
		target.highlight();

		switch (dropType) {
		case CANCEL:
			AlgebraActivity.reloadEquationPanel("Cancellation",
					Rule.CANCELLING_FRACTIONS);
			break;
		case DIVIDE:
			AlgebraActivity.reloadEquationPanel(target.getHTMLString() + "/"
					+ drag.getHTMLString(), Rule.DIVISION);
			break;
		case LOG_COMBINE:
			AlgebraActivity.reloadEquationPanel(target.getHTMLString() + "/"
					+ drag.getHTMLString(), Rule.LOGARITHM);
			break;
		case TRIG_COMBINE:
			AlgebraActivity.reloadEquationPanel(target.getHTMLString() + "/"
					+ drag.getHTMLString(), Rule.TRIGONOMETRIC_FUNCTIONS);
			break;
		}
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName("selectedDropWrapper");
		AlgebraActivity.algTransformMenu.add(response);
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName("selectedDropWrapper");
		AlgebraActivity.algTransformMenu.remove(response);
	}

}
