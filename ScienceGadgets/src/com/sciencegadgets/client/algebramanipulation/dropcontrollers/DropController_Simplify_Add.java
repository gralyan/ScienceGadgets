/*   Copyright 2012 John Gralyan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class DropController_Simplify_Add extends AbstractMathDropController {

	private float answer;

	public DropController_Simplify_Add(Widget dropTarget) {
		super(dropTarget);
	}
	
	@Override
	protected Boolean askQuestion() {
		final String[] questionAndAnswer = findChange(sourceNode).split("=");
		DialogBox dialogBox = new InputDialogBox(questionAndAnswer);
		return true;
	}

	protected void onChange() {

		findChange(sourceNode);

		/*
		 * Main changes
		 */
		targetNode.setSymbol("" + answer);
		targetNode.getWrapper().getElementWrapped().setInnerText("" + answer);

		/*
		 * Peripheral operation changes
		 */
		if (sourceNode.getIndex() > 0) {// Remove the (+) associated with source
			MathMLBindingNode prevChild = sourceNode.getPrevSibling();
			if ("+".equals(prevChild.toString())) {
				prevChild.remove();
			}
		} else {// (index=0) remove (+) leftover in front
			if ("+".equals(sourceNode.getNextSibling().toString())) {
				sourceNode.getNextSibling().remove();
			}
		}

		// Get rid of () if unnecessary
		MathMLBindingNode targetParent = null;
		MathMLBindingNode leftPerentheses = null;
		MathMLBindingNode rightPerentheses = null;

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

				MathMLBindingNode leftNode = null;
				MathMLBindingNode rightNode = null;

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
	public String findChange(MathMLBindingNode sourceNode) {

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
