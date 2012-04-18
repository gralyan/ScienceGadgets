package com.sciencegadgets.client.EquationTree;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

public class TreeEntry implements EntryPoint {

	public static AbsolutePanel apTree = new AbsolutePanel();
	public static AbsolutePanel parsedTreePanel= new AbsolutePanel();
	
	public void onModuleLoad() {
		RootPanel.get().add(apTree);
		apTree.setStyleName("apTree");
		
	}
}
