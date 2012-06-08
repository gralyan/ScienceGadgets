package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class DropControllerMultiplication extends AbstractMathDropController {

	public DropControllerMultiplication(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	void onChange() {

		// Parse source values
		int sourceValue = Integer.parseInt(source.getJohnNode().toString());
		// .getElementWrapped().getInnerText());
		int targetValue = Integer.parseInt((target).getJohnNode().toString());
		// .getElementWrapped().getInnerText()); 
		int answer = sourceValue * targetValue;

		change = targetValue + " * " + sourceValue + " = " + answer;

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
	String changeComment() {
		return "Simplify: " + change;
	}

}
