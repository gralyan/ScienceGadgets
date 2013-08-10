package com.sciencegadgets.client.algebra;

import java.util.HashMap;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.algebra.MathMLBindingTree.Operator;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.algebra.edit.EditWrapper;

public class NodeMenu extends VerticalPanel {
	MLElementWrapper mlWrapper;
	MathMLBindingNode node;
	Focusable focusable = null;
	Widget responseNotes = null;
	
	private final String ADD_BOTH = "Add this to both sides";
	private final String SUB_BOTH = "Subtract both sides by this";
	private final String MULTIPLY_BOTH = "Multiply both sides by this";
	private final String DIVIDE_BOTH = "Divide Both sides by this";
	private final String SWITCH_LEFT = "Switch with left";
	private final String SWITCH_RIGHT = "Switch with right";
	
	public NodeMenu(MLElementWrapper mlWrapper, String width) {

		this.mlWrapper = mlWrapper;
		this.node = mlWrapper.getNode();

		switch (node.getParent().getType()) {
		case Sum:
//			Button extendSum = new Button("+"+ChangeNodeMenu.NOT_SET,new ExtendSumTermHandler());
//			extendSum.setTitle(SUM_EXTEND);
//			this.add(extendSum);
			break;
		case Term:
//			Button extendTerm = new Button("x"+ChangeNodeMenu.NOT_SET,new ExtendSumTermHandler());
//			extendTerm.setTitle(TERM_EXTEND);
//			this.add(extendTerm);
			break;
		case Fraction:
			break;
		case Exponential:
			break;
		}
		
		//TODO
//		if(node.getParent().getParent().get)
	}
}
