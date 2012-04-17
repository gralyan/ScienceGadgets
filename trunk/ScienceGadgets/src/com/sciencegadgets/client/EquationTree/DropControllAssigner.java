package com.sciencegadgets.client.EquationTree;

import java.util.List;

import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;
import com.sciencegadgets.client.EquationTree.JohnTree.Type;

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

			if (Type.Number.equals(child.getType())) {
				List<JohnNode> siblings = child.getParent().getChildren();
				for (JohnNode sib : siblings) {
					if (Type.Number.equals(sib.getType())) {

						child.getWrapper().addDropTarget(sib.getWrapper());
						child.getWrapper()
								.getJoinedWrapper()
								.addDropTarget(
										sib.getWrapper().getJoinedWrapper());
					}
				}
			}
			if (child.getChildCount() > 0) {
				doChildren(child);
			}
		}
	}
}
