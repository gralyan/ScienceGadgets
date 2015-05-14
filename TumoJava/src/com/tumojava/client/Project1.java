package com.tumojava.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

public class Project1 implements EntryPoint {

	@Override
	public void onModuleLoad() {
		
		GWT.log("in");

		AbsolutePanel body = RootPanel.get("examplePanel");
		body.getElement().getStyle().setPosition(Position.RELATIVE);
		
		Counter counter = new Counter();
		body.add(counter, 10, 10);
	
		
		for(int i=0 ; i<6 ; i++){
			Box box = new Box(counter);
			body.add(box, i*100, 100);
		}
		
	}
}