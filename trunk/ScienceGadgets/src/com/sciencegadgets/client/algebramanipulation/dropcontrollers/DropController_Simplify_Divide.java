package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class DropController_Simplify_Divide extends AbstractMathDropController{
	
	private float answer;

	public DropController_Simplify_Divide(Widget dropTarget) {
		super(dropTarget);
	}

	@Override
	void onChange() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String findChange(JohnNode sourceNode) {

		// Parse source values
		float sourceValue = Float.parseFloat(sourceNode.toString());
		float targetValue = Float.parseFloat(targetNode.toString());
		answer = sourceValue * targetValue;
		
		change = targetValue + " / " + sourceValue + " = " + answer;
		return change;
	}

	@Override
	String changeComment() {
		return "Simplify: " + change;
	}

}
