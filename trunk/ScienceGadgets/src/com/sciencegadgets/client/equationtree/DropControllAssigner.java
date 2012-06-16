package com.sciencegadgets.client.equationtree;

import java.util.LinkedList;
import java.util.List;

import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropController_BothSides_Multiply;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropController_Simplify_Add;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropController_BothSides_Add;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropController_Simplify_Divide;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.DropController_Simplify_Multiply;
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
			List<JohnNode> siblings = jParent.getChildren();

			DropType bothSideDropType = null;
			DropType dropType = null;

			
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


				sipmlifySiblings: switch (parentType) {
				case Series:
					dropType = DropType.Simplify_Add;
					bothSideDropType = DropType.BothSides_Add;
					break sipmlifySiblings;
				case Term:
					dropType = DropType.Simplify_Multiply;
//					bothSideDropType = DropType.BothSides_Divide;
					break sipmlifySiblings;
				case Fraction:
					dropType = DropType.Simplify_Divide;
					if(jNode.getIndex() == 1){
						bothSideDropType = DropType.BothSides_Multiply;
					}else{
//						bothSideDropType = DropType.BothSides_Divide;
					}
					break sipmlifySiblings;
				}
			}
			
			// Give "simplify" drop targets
			for (JohnNode sib : siblings) {
				if (Type.Number.equals(sib.getType()) && !jNode.equals(sib) && dropType != null) {
					
					addDropTarget(wrap, sib.getWrapper(), dropType,
							hasJoiner);
				}
			}
			
			//Give "bothSides" drop targets
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
		case Simplify_Add:
			dropCtrl = new DropController_Simplify_Add(target);
			break;
		case Simplify_Multiply:
			dropCtrl = new DropController_Simplify_Multiply(target);
			break;
		case Simplify_Divide:
			dropCtrl = new DropController_Simplify_Divide(target);
			break;
		case BothSides_Add:
			dropCtrl = new DropController_BothSides_Add(target);
			break;
		case BothSides_Multiply:
			//TODO
			dropCtrl = new DropController_BothSides_Multiply(target);
			break;
		case BothSides_Divide:
			//TODO
//			dropCtrl = new DropController_BothSides_Divide(target);
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

	public enum DropType {
		Simplify_Add, Simplify_Multiply, Simplify_Divide, BothSides_Add, BothSides_Multiply, BothSides_Divide;
	}
}
