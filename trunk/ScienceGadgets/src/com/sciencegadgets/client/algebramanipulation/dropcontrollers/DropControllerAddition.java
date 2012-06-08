package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.equationtree.JohnTree;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class DropControllerAddition extends AbstractMathDropController {

	public DropControllerAddition(Widget dropTarget) {
		super(dropTarget);
	}

	void onChange() {

		// Parse source values
		int sourceValue = Integer.parseInt(source.getJohnNode().toString());
		// getElementWrapped().getInnerText());
		int targetValue = Integer.parseInt((target).getJohnNode().toString());
		// .getElementWrapped().getInnerText());
		int answer = sourceValue + targetValue;

		change = targetValue + " + " + sourceValue + " = " + answer;

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
	String changeComment() {
		return "Simplify: " + change;
	}
}
