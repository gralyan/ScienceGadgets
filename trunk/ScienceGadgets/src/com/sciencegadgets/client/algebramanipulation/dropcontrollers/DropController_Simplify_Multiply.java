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

public class DropController_Simplify_Multiply extends AbstractMathDropController {

	private float answer;

	public DropController_Simplify_Multiply(Widget dropTarget) {
		super(dropTarget);
	}
	
	@Override
	protected Boolean askQuestion() {
		final String[] questionAndAnswer = findChange(sourceNode).split("=");
		DialogBox dialogBox = new InputDialogBox(questionAndAnswer);
		return true;
	}

	@Override
	protected void onChange() {

		findChange(sourceNode);

		// Main changes
		targetNode.setString("" + answer);
		targetNode.getWrapper().getElementWrapped().setInnerText("" + answer);

		// Peripheral changes
		int sIndex = sourceNode.getIndex();
		if (sIndex > 0) {
			MathMLBindingNode prevChild = sourceNode.getParent().getChildAt(sIndex - 1);
			if ("mo".equals(prevChild.getTag())) {
				prevChild.remove();
			}
		} else if ("*".equals(sourceNode.getNextSibling().toString())) {
			sourceNode.getNextSibling().remove();
		}
		sourceNode.remove();

	}

	@Override
	public String findChange(MathMLBindingNode sourceNode){

		// Parse source values
		float sourceValue = Float.parseFloat(sourceNode.toString());
		float targetValue = Float.parseFloat(targetNode.toString());
		answer = sourceValue * targetValue;
		
		change = targetValue + " * " + sourceValue + " = " + answer;
		return change;
	}
	
	@Override
	String changeComment() {
		return "Simplify: " + change;
	}

}
