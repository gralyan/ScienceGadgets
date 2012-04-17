package com.sciencegadgets.client.AlgebraManipulation;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.EquationTree.TreeCanvas;
import com.sciencegadgets.client.EquationTree.TreeEntry;

public class MathMLDropController extends AbstractDropController {
	MLElementWrapper target;

	public MathMLDropController(Widget dropTarget) {
		super(dropTarget);
		target = (MLElementWrapper) dropTarget;
	}

	@Override
	  public void onDrop(DragContext context) {
		MLElementWrapper source = ((MLElementWrapper)context.draggable);
		
		int a = Integer.parseInt(source.getElementWrapped().getInnerText());
		int b = Integer.parseInt((target).getElementWrapped().getInnerText());
		
		Window.alert(target.getJohnNode().toString());
		target.getJohnNode().setString(a+"+"+b);
		source.getJohnNode().remove();
		EquationTransporter.tCanvas.reDraw();
		//Window.alert(a+" + "+b);
		
	  }
	@Override
	  public void onEnter(DragContext context) {
	    target.addStyleName("selectedVar");
	  }


	
}
