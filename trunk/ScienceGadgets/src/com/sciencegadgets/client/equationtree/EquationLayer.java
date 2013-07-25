package com.sciencegadgets.client.equationtree;

import java.util.LinkedList;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.Wrapper;

public class EquationLayer extends HTML {
	
	LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();
	EquationLayer parentLayer;
	AbsolutePanel ContextMenuPanel = new AbsolutePanel();

	EquationLayer() {
		super();
	}
	
	public void setOpacity(double opacity) {
		getElement().getStyle().setOpacity(opacity);
		ContextMenuPanel.getElement().getStyle().setOpacity(opacity);
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
	
	public AbsolutePanel getContextMenuPanel(){
		return ContextMenuPanel;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		ContextMenuPanel.setVisible(visible);
	}
	
}
