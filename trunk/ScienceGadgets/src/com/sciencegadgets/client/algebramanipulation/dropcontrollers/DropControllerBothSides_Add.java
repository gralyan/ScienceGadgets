package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;
import com.sciencegadgets.client.equationtree.JohnTree.Type;

public class DropControllerBothSides_Add extends AbstractMathDropController {

	public DropControllerBothSides_Add(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	void onChange() {

		/*
		 * Remove the (+) from this side, to be moved to the other side
		 */
		JohnNode operatorToMove = null;

		if (sourceNode.getIndex() > 0) {// Remove the (+) or (-) associated with
			// source
			JohnNode prevChild = sourceNode.getPrevSibling();
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
		
		findChange(sourceNode);

		sourceNode.setString(change);

		// Move nodes to other side
		if (Type.Series.equals(targetNode.getType())) {
			targetNode.add(operatorToMove);
			targetNode.add(sourceNode);
		} else {
			JohnTree tree = targetNode.getTree();

			JohnNode encasingSeriese = targetNode.encase("mrow", Type.Series);

			// If making new encasing series, set it as the tree's leftSide or
			// rightSide
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
	public String findChange(JohnNode sourceNode){
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
		if(change.startsWith("-")){
			return change+" &nbsp; &nbsp; &nbsp; &nbsp; "+change;
		}else{
		return "+"+change+" &nbsp; &nbsp; &nbsp; &nbsp; +"+change;
	}}

}
