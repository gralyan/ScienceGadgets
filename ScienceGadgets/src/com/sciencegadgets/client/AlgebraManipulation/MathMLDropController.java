package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
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
		MLElementWrapper source = ((MLElementWrapper) context.draggable);

		int src = Integer.parseInt(source.getElementWrapped().getInnerText());
		int targ = Integer
				.parseInt((target).getElementWrapped().getInnerText());
		int ans = src + targ;

//		for (MLElementWrapper wrap : target.getJohnNode().getTree()
//				.getWrappers()) {
//			((PickupDragController) wrap.getDragControl())
//					.unregisterDropControllers();
//			((PickupDragController) wrap.getJoinedWrapper().getDragControl())
//			.unregisterDropControllers();
//		}
		target.getJohnNode().setString("" + ans);
//		source.getJohnNode().remove();
		EquationTransporter.tCanvas.reDraw();

	}

	@Override
	public void onEnter(DragContext context) {
		target.addStyleName("mouseOverlay");
	}

}
