package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;
import com.sciencegadgets.client.equationtree.JohnTree.Type;

public class DropController_BothSides_Multiply extends
		AbstractMathDropController {

	public DropController_BothSides_Multiply(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	void onChange() {

		findChange(sourceNode);

		sourceNode.remove();

		// Move nodes to other side
		if (Type.Term.equals(targetNode.getType())) {
			targetNode.add("mo", null, "*");
			targetNode.add(sourceNode);

			// Rearrange fraction the source was in
			// ??? for some reason it already rearranges itself ???
			
			// sourceNode.getParent().getParent().add(sourceNode.getPrevSibling());
			// sourceNode.getParent().remove();

		} else {
			JohnNode encasingTerm = targetNode.encase("mrow", Type.Term);

			JohnTree tree = targetNode.getTree();
			
			// Set new encasing term as the tree's leftSide or rightSide
			if (targetNode.equals(tree.getLeftSide())) {
				tree.setLeftSide(encasingTerm);
			} else if (targetNode.equals(tree.getRightSide())) {
				tree.setRightSide(encasingTerm);
			}

			encasingTerm.add(0, "mo", null, "*");
			encasingTerm.add(0, sourceNode);

			targetNode.add(0, "mo", null, "(");
			targetNode.add("mo", null, ")");
		}
	}

	@Override
	public String findChange(JohnNode sourceNode) {
		return change = sourceNode.toString();
	}

	@Override
	String changeComment() {
		return "*" + change + " &nbsp; &nbsp; &nbsp; &nbsp; *" + change;
	}

}
