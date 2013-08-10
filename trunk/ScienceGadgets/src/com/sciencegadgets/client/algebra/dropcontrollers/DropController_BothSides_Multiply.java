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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.MathMLBindingTree;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class DropController_BothSides_Multiply extends
		AbstractMathDropController {

	public DropController_BothSides_Multiply(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	protected Boolean askQuestion() {
		return true;
	}

	@Override
	protected void onChange() {

		findChange(sourceNode);

		sourceNode.remove();

		// Move nodes to other side
		if (Type.Term.equals(targetNode.getType())) {
//			targetNode.add("mo", "*");
			targetNode.add(sourceNode);

			// Rearrange fraction the source was in
			// ??? for some reason it already rearranges itself ???
			
			// sourceNode.getParent().getParent().add(sourceNode.getPrevSibling());
			// sourceNode.getParent().remove();

		} else {
			MathMLBindingNode encasingTerm = targetNode.encase("mrow");

			MathMLBindingTree tree = targetNode.getTree();
			
			// Set new encasing term as the tree's leftSide or rightSide
			if (targetNode.equals(tree.getLeftSide())) {
				tree.setLeftSide(encasingTerm);
			} else if (targetNode.equals(tree.getRightSide())) {
				tree.setRightSide(encasingTerm);
			}

//			encasingTerm.add(0, "mo", "*");
			encasingTerm.add(0, sourceNode);

//			targetNode.add(0, "mo", "(");
//			targetNode.add("mo", ")");
		}
	}

	@Override
	public String findChange(MathMLBindingNode sourceNode) {
		return change = sourceNode.toString();
	}

	@Override
	String changeComment() {
		return "*" + change + " &nbsp; &nbsp; &nbsp; &nbsp; *" + change;
	}

}
