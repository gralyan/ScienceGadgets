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
import com.sciencegadgets.client.equationtree.MathMLBindingTree;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

/**
 * @author jojo
 * 
 */
public class DropController_BothSides_Divide extends AbstractMathDropController {

	public DropController_BothSides_Divide(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	void onChange() {

		findChange(sourceNode);

		/*
		 * Remove the (*) from this side
		 */
		if (sourceNode.getIndex() > 0) {// Remove the (*)associated with source
			MathMLBindingNode prevChild = sourceNode.getPrevSibling();
			if ("mo".equals(prevChild.getTag())) {
				prevChild.remove();
			}
		} else {// (index=0) remove (*) leftover in front
			if ("mo".equals(sourceNode.getNextSibling().getTag())) {
				sourceNode.getNextSibling().remove();
			}
		}

		sourceNode.remove();

		// Move nodes to other side
		if (Type.Fraction.equals(targetNode.getType())) {

			MathMLBindingNode denominator = targetNode.getChildAt(1);

			if (Type.Term.equals(denominator.getType())) {
				denominator.add("mo", null, "*");
				denominator.add(sourceNode);
			} else {
				MathMLBindingNode encasedDenominator = denominator.encase("mrow",
						Type.Term);
				encasedDenominator.add("mo", null, "*");
				encasedDenominator.add(sourceNode);
			}

		} else {
			MathMLBindingNode encasingFraction = targetNode.encase("mfrac",
					Type.Fraction);

			MathMLBindingTree tree = targetNode.getTree();

			// Set new encasing fraction as the tree's leftSide or rightSide
			if (targetNode.equals(tree.getLeftSide())) {
				tree.setLeftSide(encasingFraction);
			} else if (targetNode.equals(tree.getRightSide())) {
				tree.setRightSide(encasingFraction);
			}

			encasingFraction.add(sourceNode);

//			targetNode.add(0, "mo", null, "(");
//			targetNode.add("mo", null, ")");
		}

	}

	@Override
	public String findChange(MathMLBindingNode sourceNode) {
		return change = sourceNode.toString();
	}

	@Override
	String changeComment() {
		return "&divide" + change + " &nbsp; &nbsp; &nbsp; &nbsp; &divide;"
				+ change;
	}

}
