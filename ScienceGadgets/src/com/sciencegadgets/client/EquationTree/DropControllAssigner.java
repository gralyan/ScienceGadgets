package com.sciencegadgets.client.EquationTree;

import java.util.List;

import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;

public class DropControllAssigner {

	private JohnNode root;
	private JohnNode rightSide;
	private JohnNode leftSide;

	public DropControllAssigner(JohnTree jTree) {
		root = jTree.getRoot();
		leftSide = jTree.getLeftSide();
		rightSide = jTree.getRightSide();

		doChildren(root);
	}

	private void doChildren(JohnNode jNode) {
		List<JohnNode> children = jNode.getChildren();

		for (JohnNode child : children) {
			if (child.getTag().equalsIgnoreCase("mi")) {
				
				//Handle errors better
				
				JohnNode sib = null;
				try {
					sib = child.getNextSibling();
				} catch (IndexOutOfBoundsException e) {

				}
				if (sib != null) {
					child.getWrapper().addDropTarget(sib.getWrapper());
				}

				System.out.println("added " + child.toString() + "to "
						+ child.getNextSibling().toString());

			}
			if (child.getChildCount() > 0) {
				doChildren(child);
			}
		}
	}
}
