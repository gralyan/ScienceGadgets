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

public class DropController_BothSides_Add extends AbstractMathDropController {

	public DropController_BothSides_Add(Widget dropTarget) {
		super(dropTarget);
	}
	
	@Override
	protected Boolean askQuestion() {
		return true;
	}

	@Override
	protected void onChange() {

		findChange(sourceNode);

		sourceNode.setString(change);

		/*
		 * Remove the (+) from this side, to be moved to the other side
		 */
		MathMLBindingNode operatorToMove = null;

		if (sourceNode.getIndex() > 0) {// Remove the (+) or (-) associated with
			// source
			MathMLBindingNode prevChild = sourceNode.getPrevSibling();
			if ("mo".equals(prevChild.getTag())) {
				operatorToMove = prevChild;
				prevChild.remove();
			}
		} else {// (index=0) remove (+) leftover in front
			if ("mo".equals(sourceNode.getNextSibling().getTag())) {
				operatorToMove = sourceNode.getNextSibling();
				sourceNode.getNextSibling().remove();
			}
		}

		sourceNode.remove();

		// Move nodes to other side
		if (Type.Series.equals(targetNode.getType())) {
			targetNode.add(operatorToMove);
			targetNode.add(sourceNode);
		} else {
			MathMLBindingNode encasingSeriese = targetNode.encase("mrow");

			MathMLBindingTree tree = targetNode.getTree();

			// Set new encasing series as the tree's leftSide or rightSide
			if (targetNode.equals(tree.getLeftSide())) {
				tree.setLeftSide(encasingSeriese);
			} else if (targetNode.equals(tree.getRightSide())) {
				tree.setRightSide(encasingSeriese);
			}

			encasingSeriese.add(operatorToMove);
			encasingSeriese.add(sourceNode);
		}

	}

	@Override
	public String findChange(MathMLBindingNode sourceNode) {
		// Change sign on opposite side
		if (sourceNode.toString().startsWith("-")) {
			change = sourceNode.toString().replaceFirst("-", "");
		} else {
			change = "-" + sourceNode.toString();
		}

		return change;
	}

	@Override
	String changeComment() {
		if (change.startsWith("-")) {
			return change + " &nbsp; &nbsp; &nbsp; &nbsp; " + change;
		} else {
			return "+" + change + " &nbsp; &nbsp; &nbsp; &nbsp; +" + change;
		}
	}

}
