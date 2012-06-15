package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class DropController_Simplify_Add extends AbstractMathDropController {

	private float answer;

	public DropController_Simplify_Add(Widget dropTarget) {
		super(dropTarget);
	}

	void onChange() {

		findChange(sourceNode);

		/*
		 * Main changes
		 */
		targetNode.setString("" + answer);
		targetNode.getWrapper().getElementWrapped().setInnerText("" + answer);

		/*
		 * Peripheral operation changes
		 */
		if (sourceNode.getIndex() > 0) {// Remove the (+) associated with source
			JohnNode prevChild = sourceNode.getPrevSibling();
			if ("+".equals(prevChild.toString())) {
				prevChild.remove();
			}
		} else {// (index=0) remove (+) leftover in front
			if ("+".equals(sourceNode.getNextSibling().toString())) {
				sourceNode.getNextSibling().remove();
			}
		}

		// Get rid of () if unnecessary
		JohnNode targetParent = null;
		JohnNode leftPerentheses = null;
		JohnNode rightPerentheses = null;

		try {
			targetParent = targetNode.getParent();
			leftPerentheses = targetParent.getPrevSibling();
			rightPerentheses = targetParent.getNextSibling();
		} catch (NullPointerException e) {
		} catch (IndexOutOfBoundsException e) {
		}

		if (leftPerentheses != null && rightPerentheses != null) {
			if ("(".equals(leftPerentheses.toString())
					&& ")".equals(rightPerentheses.toString())) {

				JohnNode leftNode = null;
				JohnNode rightNode = null;

				try {
					leftNode = leftPerentheses.getPrevSibling();
				} catch (IndexOutOfBoundsException n) {
					// leftNode = null;
				}
				try {
					rightNode = rightPerentheses.getNextSibling();
				} catch (IndexOutOfBoundsException e) {
					// rightNode = null;
				}

				if ((leftNode == null || "mo".equals(leftNode.getTag()))
						&& (rightNode == null || "mo"
								.equals(rightNode.getTag()))) {
					leftPerentheses.remove();
					rightPerentheses.remove();
				}

			}
		}
		sourceNode.remove();

	}
	
	@Override
	public String findChange(JohnNode sourceNode) {

		// Parse source values
		float sourceValue = Float.parseFloat(sourceNode.toString());
		float targetValue = Float.parseFloat(targetNode.toString());
		answer = sourceValue + targetValue;

		change = targetValue + " + " + sourceValue + " = " + answer;
		
		return change;
	}

	@Override
	String changeComment() {
		return "Simplify: " + change;
	}
}
