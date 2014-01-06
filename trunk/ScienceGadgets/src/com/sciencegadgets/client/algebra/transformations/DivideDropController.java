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
import com.sciencegadgets.shared.UnitUtil;

public class DivideDropController extends AbstractDropController {

	Label response = new Label();
	private boolean isCancel;
	private MathNode drag;
	private MathNode target;

	public DivideDropController(AlgebaWrapper dropTarget, boolean isCancel) {
		super(dropTarget);

		this.isCancel = isCancel;
		if (isCancel) {
			response.setText(ResponseNote.Cancel.toString());
		} else {
			response.setText(ResponseNote.Divide.toString());
		}
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		drag = ((AlgebaWrapper) context.draggable).getNode();
		target = ((AlgebaWrapper) getDropTarget()).getNode();


		
		if (isCancel) {
			cleanUp();
		}else {
			try {
				dividePrompt();
			} catch (NumberFormatException e) {
				return;
			}
		}

	}

	private void cleanUp() {
		ArrayList<MathNode> sidesToRemove = new ArrayList<MathNode>();
		if (isCancel) {
			sidesToRemove.add(drag);
			sidesToRemove.add(target);
		} else {
			sidesToRemove.add(drag);
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
		target.lineThrough();

		if (isCancel) {
			AlgebraActivity.reloadEquationPanel("Cancellation",
					Rule.CANCELLING_FRACTIONS);
		} else {
			AlgebraActivity.reloadEquationPanel(target.getHTMLString() + "/"
					+ drag.getHTMLString(), Rule.DIVISION);
		}
	}

	private void dividePrompt() {

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
