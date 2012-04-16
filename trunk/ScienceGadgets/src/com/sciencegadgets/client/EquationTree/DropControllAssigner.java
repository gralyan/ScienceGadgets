package com.sciencegadgets.client.EquationTree;

import java.util.List;

import com.google.gwt.user.client.Window;
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
				// Handle errors better

				try {
					JohnNode sib = child.getNextSibling();
					if (child.getTag().equalsIgnoreCase("mi")) {
						
						child.getWrapper().addDropTarget(sib.getWrapper());
						child.getWrapper().getJoinedWrapper().addDropTarget(sib.getWrapper().getJoinedWrapper());
					}
				} catch (IndexOutOfBoundsException e) {

				}

			}
			if (child.getChildCount() > 0) {
				doChildren(child);
			}
		}
	}
}
