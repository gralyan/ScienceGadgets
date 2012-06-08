package com.sciencegadgets.client.equationtree;

import java.util.LinkedList;
import java.util.List;

import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropControllerAddition;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropControllerBothSides_Add;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropControllerMultiplication;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;
import com.sciencegadgets.client.equationtree.JohnTree.Type;

public class DropControllAssigner {

	public DropControllAssigner(LinkedList<MLElementWrapper> wrappers,
			Boolean hasJoiner) {
		assign(wrappers, hasJoiner);
	}

	public static void assign(LinkedList<MLElementWrapper> wrappers,
			Boolean hasJoiner) {

		JohnNode jNode;
		JohnNode jParent;

		wrapFor: for (MLElementWrapper wrap : wrappers) {
			jNode = wrap.getJohnNode();
			jParent = jNode.getParent();
			DropType bothSideDropType = null;

			// Make sure jParent has a type
			Type parentType = null;
			try {
				parentType = jParent.getType();
			} catch (NullPointerException e) {
				Log.severe("No Type for: " + jParent.toString());
				e.printStackTrace();
			}
			if (parentType == null) {
				continue wrapFor;
			}

			assignments: switch (jNode.getType()) {
			case Number:
//			case Variable:

				List<JohnNode> siblings = jParent.getChildren();
				DropType dropType = null;

				sipmlifySiblings: switch (parentType) {
				case Series:
					dropType = DropType.AddSimplify;
					bothSideDropType = DropType.AddBothSides;
					break sipmlifySiblings;
				case Term:
					dropType = DropType.MultiplySimplify;
					bothSideDropType = DropType.MultiplyBothSides;
					break sipmlifySiblings;
				}

				for (JohnNode sib : siblings) {
					if (Type.Number.equals(sib.getType()) && !jNode.equals(sib) && dropType != null) {

						addDropTarget(wrap, sib.getWrapper(), dropType,
								hasJoiner);
					}
				}
			}
			
			if (bothSideDropType != null) {
				JohnTree tree = jNode.getTree();
				if (tree.getLeftSide().equals(jParent)) {
					addDropTarget(wrap, tree.getRightSide().getWrapper(),
							bothSideDropType, hasJoiner);
				} else if (tree.getRightSide().equals(jParent)) {
					addDropTarget(wrap, tree.getLeftSide().getWrapper(),
							bothSideDropType, hasJoiner);
				}
			}
		}
	}

	static AbstractMathDropController addDropTarget(MLElementWrapper source,
			MLElementWrapper target, DropControllAssigner.DropType dropType,
			Boolean hasJoiner) {

		AbstractMathDropController dropCtrl = null;

		switch (dropType) {
		case AddSimplify:
			dropCtrl = new DropControllerAddition(target);
			break;
		case MultiplySimplify:
			dropCtrl = new DropControllerMultiplication(target);
			break;
		case AddBothSides:
			dropCtrl = new DropControllerBothSides_Add(target);
			break;
		case MultiplyBothSides:
			dropCtrl = new DropControllerBothSides_Add(target);
			break;
		}

		try {
			source.getDragControl().registerDropController(dropCtrl);
		} catch (NullPointerException e) {
			// e.printStackTrace();
		}

		if (hasJoiner) {
			addDropTarget(source.getJoinedWrapper(), target.getJoinedWrapper(),
					dropType, false);
		}

		return dropCtrl;
	}

	public static enum DropType {
		AddSimplify, MultiplySimplify, AddBothSides, MultiplyBothSides;
	}
}
