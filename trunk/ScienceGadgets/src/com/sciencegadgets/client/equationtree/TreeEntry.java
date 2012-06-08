package com.sciencegadgets.client.equationtree;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

public class TreeEntry implements EntryPoint {

	public static final AbsolutePanel mlTree = new AbsolutePanel();
	public static AbsolutePanel apTree = new AbsolutePanel();
	
	public void onModuleLoad() {
		apTree.setStyleName("apTree");
		RootPanel.get().add(apTree);
		
		////////////////////////////
		//Just to visualize ml
		//////////////////////////
//		mlTree.setStyleName("apTree");
//		RootPanel.get().add(mlTree);
	}
}
