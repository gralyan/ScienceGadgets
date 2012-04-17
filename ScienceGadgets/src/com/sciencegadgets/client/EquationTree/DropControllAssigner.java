package com.sciencegadgets.client.EquationTree;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.allen_sauer.gwt.dnd.client.DragController;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.sciencegadgets.client.AlgebraManipulation.MLElementWrapper;
import com.sciencegadgets.client.AlgebraManipulation.MathMLDropController;
import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;
import com.sciencegadgets.client.EquationTree.JohnTree.Type;

public class DropControllAssigner {

	// private JohnNode root;
	// private JohnNode rightSide;
	// private JohnNode leftSide;
	private JohnNode jNode;
//	private HashMap<MathMLDropController, PickupDragController> DropControllers = new HashMap<MathMLDropController, PickupDragController>();

	public DropControllAssigner(LinkedList<MLElementWrapper> wrappers,
			Boolean hasJoiner) {
//		MathMLDropController dropC = null;
//		MathMLDropController dropCJ = null;
		for (MLElementWrapper wrap : wrappers) {
			jNode = wrap.getJohnNode();

			if (Type.Number.equals(jNode.getType())) {
				List<JohnNode> siblings = jNode.getParent().getChildren();
				for (JohnNode sib : siblings) {
					if (Type.Number.equals(sib.getType())) {

						wrap.addDropTarget(sib.getWrapper());
						if (hasJoiner) {
							wrap.getJoinedWrapper().addDropTarget(
									sib.getWrapper().getJoinedWrapper());
						}
					}
				}
			}
//			if (dropC != null) {
//				DropControllers.put(dropC, wrap.getDragControl());
//				if (hasJoiner) {
//					DropControllers.put(dropCJ, wrap.getJoinedWrapper().getDragControl());
//				}
//			}
		}

	}

/*	public HashMap<MathMLDropController, PickupDragController> getDropControllers() {
		return DropControllers;
	}
	public void unregisterAll(){
			Iterator<MathMLDropController> it = DropControllers.keySet().iterator();
			while (it.hasNext()){
				PickupDragController a = DropControllers.get(it.next());
				a.unregisterDropControllers();
			}
		
	}
*/	/*
	 * public DropControllAssigner(JohnTree jTree) { root = jTree.getRoot();
	 * leftSide = jTree.getLeftSide(); rightSide = jTree.getRightSide();
	 * 
	 * doChildren(root); }
	 * 
	 * private void doChildren(JohnNode jNode) { List<JohnNode> children =
	 * jNode.getChildren(); for (JohnNode child : children) {
	 * 
	 * if (Type.Number.equals(child.getType())) { List<JohnNode> siblings =
	 * child.getParent().getChildren(); for (JohnNode sib : siblings) { if
	 * (Type.Number.equals(sib.getType())) {
	 * 
	 * child.getWrapper().addDropTarget(sib.getWrapper()); child.getWrapper()
	 * .getJoinedWrapper() .addDropTarget( sib.getWrapper().getJoinedWrapper());
	 * } } } if (child.getChildCount() > 0) { doChildren(child); } } }
	 */
}
