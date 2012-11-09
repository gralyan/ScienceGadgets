package com.sciencegadgets.client.equationtree;

import com.google.gwt.user.client.ui.AbsolutePanel;

public class EquationLayer extends AbsolutePanel {
	AbsolutePanel parentBackPanel = new AbsolutePanel();
	AbsolutePanel backPanel = new AbsolutePanel();
	AbsolutePanel eqPanel = new AbsolutePanel();
	AbsolutePanel wrapPanel = new AbsolutePanel();

	EquationLayer(){
		super();

		wrapPanel.getElement().getStyle().setZIndex(4);
		eqPanel.getElement().getStyle().setZIndex(3);
		backPanel.getElement().getStyle().setZIndex(2);
		parentBackPanel.getElement().getStyle().setZIndex(1);
		
		add(parentBackPanel);
		add(backPanel,0,0);
		add(eqPanel, 0, 0);
		add(wrapPanel, 0, 0);
		
		 eqPanel.setStyleName("textCenter");
	}
	
	@Override
	public void setSize(String width, String height){
		super.setSize(width, height);
		
		parentBackPanel.setSize(width, height);
		backPanel.setSize(width, height);
		eqPanel.setSize(width, height);
		wrapPanel.setSize(width, height);
	}

	public void setOpacity(double opacity) {
		getElement().getStyle().setOpacity(opacity);
//		wrapPanel.getElement().getStyle().setOpacity(equation);
//		eqPanel.getElement().getStyle().setOpacity(equation);
//		backPanel.getElement().getStyle().setOpacity(background);
//		parentBackPanel.getElement().getStyle().setOpacity(background);
	}

}
