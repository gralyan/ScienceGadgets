package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;
import com.sciencegadgets.client.equationtree.JohnTree.Type;

public class DropController_Simplify_Divide extends AbstractMathDropController {

	private float answer;

	public DropController_Simplify_Divide(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	void onChange() {

		findChange(sourceNode);

		JohnNode parentNode = targetNode.getParent();
		JohnNode simplified = parentNode.encase("mn", Type.Number);
		simplified.setString("" + answer);

		parentNode.remove();
		sourceNode.remove();
		targetNode.remove();
	}

	@Override
	public String findChange(JohnNode sourceNode) {

		// Parse source values
		float sourceValue = Float.parseFloat(sourceNode.toString());
		float targetValue = Float.parseFloat(targetNode.toString());

		if (sourceNode.getIndex() == 0) {
			answer = sourceValue / targetValue;
			answer = Math.round(answer);
			change = sourceValue + " / " + targetValue + " = " + answer;
		} else {
			answer = targetValue / sourceValue;
			change = targetValue + " / " + sourceValue + " = " + answer;
		}

		return change;
	}

	@Override
	String changeComment() {
		return "Simplify: " + change;
	}

}
