package com.sciencegadgets.client.AlgebraManipulation;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;

public class MathMLDropController extends AbstractDropController {
	MLElementWrapper target;

	public MathMLDropController(Widget dropTarget) {
		super(dropTarget);
		target = (MLElementWrapper) dropTarget;
	}

	@Override
	  public void onDrop(DragContext context) {
		Window.alert("dropped");
	  }
	@Override
	  public void onEnter(DragContext context) {
	    target.addStyleName("selectedVar");
	  }


	
}
