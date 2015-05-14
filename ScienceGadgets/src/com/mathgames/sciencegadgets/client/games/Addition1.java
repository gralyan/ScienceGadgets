package com.mathgames.sciencegadgets.client.games;

import java.util.LinkedList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.mathgames.sciencegadgets.client.CSS;
import com.mathgames.sciencegadgets.client.Game;
import com.mathgames.sciencegadgets.client.ui.Box;


public class Addition1 extends ArithmeticGame {
	
	private static final String LEFT_ID = "symbolic-equation-left-id";
	private static final String RIGHT_ID = "symbolic-equation-right-id";
	private static final String TOTAL_ID = "symbolic-equation-total-id";
	
	private int leftValue;
	private int rightValue;
	private int totalValue;
	
	LinkedList<Box> boxes;
	private Label plus = new Label("+");;

	public Addition1() {
		super(Game.Addition_1);
		boxes = new LinkedList<Box>();
		plus.addStyleName(CSS.OP);
	}
	
	@Override
	HTML makeEquationWithIDS() {
		return new HTML("<div class=\"gwt-HTML\"><link type=\"text/css\" rel=\"stylesheet\" href=\"http://sciencegadgets.org/CSStyles/equation.css\"><div class=\"sg-Equation\"><div class=\"sg-in-equation Sum\"><div class=\"sg-in-sum Number\" id=\""+LEFT_ID+"\">0</div><div class=\"sg-in-sum Operation\">+</div><div class=\"sg-in-sum Number\" id=\""+RIGHT_ID+"\">0</div></div><div class=\"sg-in-equation Operation\">=</div><div class=\"sg-in-equation Number\" id=\""+TOTAL_ID+"\">0</div></div></div>");
	}

	@Override
	void refreshEquation() {
		leftValue = getRandom();
		rightValue = getRandom();
		totalValue = leftValue+rightValue;
		
		DOM.getElementById(LEFT_ID).setInnerText(leftValue+"");
		DOM.getElementById(RIGHT_ID).setInnerText(rightValue+"");
		DOM.getElementById(TOTAL_ID).setInnerText(totalValue+"");
	}

	@Override
	void refreshVisuals(FlowPanel visualArea) {
		visualArea.clear();
		
		int gapLeftRight = 5;
		int boxCount = leftValue+rightValue;
		int sideLength = (100-gapLeftRight)/(boxCount);
		
		
		for(int i=0 ; i<boxCount ; i++) {
			Box box = new Box();
			boxes.add(box);
			box.setSize(sideLength+"%", sideLength+"%");
			visualArea.add(box);
		}
		visualArea.insert(plus, leftValue);
	}
	

}
