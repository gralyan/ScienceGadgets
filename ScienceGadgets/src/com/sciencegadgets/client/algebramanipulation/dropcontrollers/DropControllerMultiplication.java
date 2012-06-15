package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class DropControllerMultiplication extends AbstractMathDropController {

	private int answer;

	public DropControllerMultiplication(Widget dropTarget) {
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
		int sourceValue = Integer.parseInt(sourceNode.toString());
		int targetValue = Integer.parseInt(targetNode.toString());
		answer = sourceValue * targetValue;
		
		change = targetValue + " * " + sourceValue + " = " + answer;
		return change;
	}
	
	@Override
	String changeComment() {
		return "Simplify: " + change;
	}

}
