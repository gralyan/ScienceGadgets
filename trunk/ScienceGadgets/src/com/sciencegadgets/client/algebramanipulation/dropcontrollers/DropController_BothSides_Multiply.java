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
			
			//Rearrange fraction the source was in
//			sourceNode.getPrevSibling().rc();
			
		} else {
//			JohnTree tree = targetNode.getTree();
//
//			JohnNode encasingSeriese = targetNode.encase("mrow", Type.Series);
//
//			// If making new encasing series, set it as the tree's leftSide or
//			// rightSide
//			if (targetNode.equals(tree.getLeftSide())) {
//				tree.setLeftSide(encasingSeriese);
//			} else if (targetNode.equals(tree.getRightSide())) {
//				tree.setRightSide(encasingSeriese);
//			}
//
//			encasingSeriese.add(operatorToMove);
//			encasingSeriese.add(sourceNode);
		}		
	}

	@Override
	public String findChange(JohnNode sourceNode) {
		return change = "-" + sourceNode.toString();
	}

	@Override
	String changeComment() {
			return "*"+change + " &nbsp; &nbsp; &nbsp; &nbsp; *" + change;
	}

}
