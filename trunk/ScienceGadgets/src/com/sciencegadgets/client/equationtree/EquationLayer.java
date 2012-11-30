package com.sciencegadgets.client.equationtree;

import java.util.LinkedList;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.Wrapper;

public class EquationLayer extends AbsolutePanel {
	AbsolutePanel parentBackPanel = new AbsolutePanel();
	AbsolutePanel backPanel = new AbsolutePanel();
	AbsolutePanel eqPanel = new AbsolutePanel();
	AbsolutePanel wrapPanel = new AbsolutePanel();
	
	LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();

	EquationLayer parentLayer;

	EquationLayer() {
		super();

		wrapPanel.getElement().getStyle().setZIndex(4);
		eqPanel.getElement().getStyle().setZIndex(3);
		backPanel.getElement().getStyle().setZIndex(2);
		parentBackPanel.getElement().getStyle().setZIndex(1);

		add(parentBackPanel);
		add(backPanel, 0, 0);
		add(eqPanel, 0, 0);
		add(wrapPanel, 0, 0);

		eqPanel.setStyleName("textCenter");
	}
	
	@Override
	public void setSize(String width, String height) {
		super.setSize(width, height);

		parentBackPanel.setSize(width, height);
		backPanel.setSize(width, height);
		eqPanel.setSize(width, height);
		wrapPanel.setSize(width, height);
	}

	public void setOpacity(double opacity) {
		getElement().getStyle().setOpacity(opacity);
	}

	void setParentLayer(EquationLayer parentLayer) {
		this.parentLayer = parentLayer;
	}

	public EquationLayer getParentLayer() {
		return parentLayer;
	}
	
	public LinkedList<Wrapper> getWrappers() {
		return wrappers;
	}
	
	public void addWrapper(Wrapper wrap) {
		wrappers.add(wrap);
	}
}
