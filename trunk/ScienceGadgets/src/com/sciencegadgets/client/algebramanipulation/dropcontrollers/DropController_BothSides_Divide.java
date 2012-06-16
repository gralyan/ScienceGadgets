/**
 * 
 */
package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;
import com.sciencegadgets.client.equationtree.JohnTree.Type;

/**
 * @author jojo
 * 
 */
public class DropController_BothSides_Divide extends AbstractMathDropController {

	public DropController_BothSides_Divide(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	void onChange() {

		findChange(sourceNode);

		/*
		 * Remove the (*) from this side
		 */
		if (sourceNode.getIndex() > 0) {// Remove the (*)associated with source
			JohnNode prevChild = sourceNode.getPrevSibling();
			if ("mo".equals(prevChild.getTag())) {
				prevChild.remove();
			}
		} else {// (index=0) remove (*) leftover in front
			if ("mo".equals(sourceNode.getNextSibling().getTag())) {
				sourceNode.getNextSibling().remove();
			}
		}

		sourceNode.remove();

		// Move nodes to other side
		if (Type.Fraction.equals(targetNode.getType())) {

			JohnNode denominator = targetNode.getChildAt(1);

			if (Type.Term.equals(denominator.getType())) {
				denominator.add("mo", null, "*");
				denominator.add(sourceNode);
			} else {
				JohnNode encasedDenominator = denominator.encase("mrow",
						Type.Term);
				encasedDenominator.add("mo", null, "*");
				encasedDenominator.add(sourceNode);
			}

		} else {
			JohnNode encasingFraction = targetNode.encase("mfrac",
					Type.Fraction);

			JohnTree tree = targetNode.getTree();

			// Set new encasing fraction as the tree's leftSide or rightSide
			if (targetNode.equals(tree.getLeftSide())) {
				tree.setLeftSide(encasingFraction);
			} else if (targetNode.equals(tree.getRightSide())) {
				tree.setRightSide(encasingFraction);
			}

			encasingFraction.add(sourceNode);

//			targetNode.add(0, "mo", null, "(");
//			targetNode.add("mo", null, ")");
		}

	}

	@Override
	public String findChange(JohnNode sourceNode) {
		return change = sourceNode.toString();
	}

	@Override
	String changeComment() {
		return "&divide" + change + " &nbsp; &nbsp; &nbsp; &nbsp; &divide;"
				+ change;
	}

}
