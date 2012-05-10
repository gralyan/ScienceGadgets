package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class DropControllerAddition extends AbstractMathDropController {

	public DropControllerAddition(Widget dropTarget) {
		super(dropTarget);
	}

	void onChange() {

		// Parse source values
		int sourceValue = Integer.parseInt(source.getElementWrapped().getInnerText());
		int targetValue = Integer
				.parseInt((target).getElementWrapped().getInnerText());
		int answer = sourceValue + targetValue;

		/*
		 * Main changes
		 */
		targetNode.setString("" + answer);
		targetNode.getWrapper().getElementWrapped().setInnerText("" + answer);

		/*
		 * Peripheral changes
		 */
		int sourceIndex = sourceNode.getIndex();
		if (sourceIndex > 0) {// Remove the + or - associated with source
			JohnNode prevChild = sourceNode.getPrevSibling();
			if ("mo".equals(prevChild.getTag())) {
				prevChild.remove();
			}
		} else {// (sIndex=0) remove + leftover in front
			if ("+".equals(sourceNode.getNextSibling().toString())) {
				sourceNode.getNextSibling().remove();

			}
		}

		// Get rid of () if unnecessary
		JohnNode targetParent;
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
//					leftNode = null;
				}
				try {
					rightNode = rightPerentheses.getNextSibling();
				} catch (IndexOutOfBoundsException e) {
//					rightNode = null;
				}

				if ((leftNode == null || "mo".equals(leftNode.getTag())) && (rightNode == null || "mo".equals(rightNode.getTag()))) {
					leftPerentheses.remove();
					rightPerentheses.remove();
				}

			}
		}
		sourceNode.remove();
	}
}
