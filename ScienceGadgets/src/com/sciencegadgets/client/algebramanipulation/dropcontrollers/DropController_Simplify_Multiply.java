package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class DropController_Simplify_Multiply extends AbstractMathDropController {

	private float answer;

	public DropController_Simplify_Multiply(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	void onChange() {

		findChange(sourceNode);

		// Main changes
		targetNode.setString("" + answer);
		targetNode.getWrapper().getElementWrapped().setInnerText("" + answer);

		// Peripheral changes
		int sIndex = sourceNode.getIndex();
		if (sIndex > 0) {
			JohnNode prevChild = sourceNode.getParent().getChildAt(sIndex - 1);
			if ("mo".equals(prevChild.getTag())) {
				prevChild.remove();
			}
		} else if ("*".equals(sourceNode.getNextSibling().toString())) {
			sourceNode.getNextSibling().remove();
		}
		sourceNode.remove();

	}

	@Override
	public String findChange(JohnNode sourceNode){

		// Parse source values
		float sourceValue = Float.parseFloat(sourceNode.toString());
		float targetValue = Float.parseFloat(targetNode.toString());
		answer = sourceValue * targetValue;
		Math.round(answer);
		
		change = targetValue + " * " + sourceValue + " = " + answer;
		return change;
	}
	
	@Override
	String changeComment() {
		return "Simplify: " + change;
	}

}
