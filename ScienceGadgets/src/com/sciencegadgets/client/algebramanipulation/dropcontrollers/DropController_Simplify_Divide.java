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

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class DropController_Simplify_Divide extends AbstractMathDropController {

	private float answer;

	public DropController_Simplify_Divide(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	void onChange() {

		findChange(sourceNode);

		MathMLBindingNode parentNode = targetNode.getParent();
		MathMLBindingNode simplified = parentNode.encase("mn", Type.Number);
		simplified.setString("" + answer);

		parentNode.remove();
		sourceNode.remove();
		targetNode.remove();
	}

	@Override
	public String findChange(MathMLBindingNode sourceNode) {

		// Parse source values
		float sourceValue = Float.parseFloat(sourceNode.toString());
		float targetValue = Float.parseFloat(targetNode.toString());

		if (sourceNode.getIndex() == 0) {
			answer = sourceValue / targetValue;
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
