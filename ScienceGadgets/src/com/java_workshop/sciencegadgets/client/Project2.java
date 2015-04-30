package com.java_workshop.sciencegadgets.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class Project2 implements EntryPoint {

	public void onModuleLoad() {

		RootPanel body = RootPanel.get("examplePanel");
		body.getElement().getStyle().setPosition(Position.RELATIVE);
		
		Label hi = new Label("OUT");
		hi.addStyleName("dragBox");
		
		body.add(hi, 10, 10);
		
		PickupDragController dragC = new PickupDragController(body, true);
		
		dragC.makeDraggable(hi);
		
		FlowPanel box = new FlowPanel();
		box.addStyleName("box");
		body.add(box, 100, 100);
		
		BoxDropController dropC = new BoxDropController(box);
		
		dragC.registerDropController(dropC);
		
	}
}