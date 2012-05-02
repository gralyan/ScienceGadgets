package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class DropControllerAddition extends AbstractMathDropController {

	public DropControllerAddition(Widget dropTarget) {
		super(dropTarget);
	}

	void onChange(){

		// Add drop source value to target value
		int src = Integer.parseInt(source.getElementWrapped().getInnerText());
		int targ = Integer
				.parseInt((target).getElementWrapped().getInnerText());
		int ans = src + targ;
		
		// Main changes
		targetNode.setString("" + ans);
		targetNode.getWrapper().getElementWrapped().setInnerText("" + ans);

		// Peripheral changes
		int sIndex = sourceNode.getIndex();
		if (sIndex > 0) {
			JohnNode prevChild = sourceNode.getParent().getChildAt(sIndex - 1);
			if ("mo".equals(prevChild.getTag())) {
				prevChild.remove();
			}
		} else if ("+".equals(sourceNode.getNextSibling().toString())) {
			sourceNode.getNextSibling().remove();
		}
		sourceNode.remove();
	}
	
}
