package com.tumojava.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class BoxDropController extends AbstractDropController {

	boolean isDropping = false;

	public BoxDropController(FlowPanel dropTarget) {
		super(dropTarget);
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		Label drag = ((Label) context.draggable);
		drag.setText("IN");
		drag.addStyleName("boxS");
		((FlowPanel) getDropTarget()).getElement().getStyle().setBackgroundColor("yellow");
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		if (!isDropping) {
			Label drag = ((Label) context.draggable);
			drag.setText("OUT");
			drag.removeStyleName("boxS");
			((FlowPanel) getDropTarget()).getElement().getStyle().clearBackgroundColor();
		}
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		isDropping = true;
		((FlowPanel) getDropTarget()).add(context.draggable);
		getDropTarget().getElement().getStyle().setBackgroundColor("green");
		context.dragController.makeNotDraggable(context.draggable);
	}

}