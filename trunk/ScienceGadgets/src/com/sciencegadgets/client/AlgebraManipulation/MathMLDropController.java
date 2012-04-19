package com.sciencegadgets.client.AlgebraManipulation;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;
import com.sciencegadgets.client.EquationTree.JohnTree.Type;

public class MathMLDropController extends AbstractDropController {
	MLElementWrapper target;

	public MathMLDropController(Widget dropTarget) {
		super(dropTarget);
		target = (MLElementWrapper) dropTarget;
	}

	@Override
	public void onDrop(DragContext context) {
		MLElementWrapper source = ((MLElementWrapper) context.draggable);

		// Add drop source to terget
		int src = Integer.parseInt(source.getElementWrapped().getInnerText());
		int targ = Integer
				.parseInt((target).getElementWrapped().getInnerText());
		int ans = src + targ;

		// All drop controllers must me unregistered
		for (MLElementWrapper wrap : target.getJohnNode().getTree()
				.getWrappers()) {
			((PickupDragController) wrap.getDragControl())
					.unregisterDropControllers();
			((PickupDragController) wrap.getJoinedWrapper().getDragControl())
			.unregisterDropControllers();
		}
		target.getJohnNode().setString("" + ans);
		
		JohnNode prevChild = target.getJohnNode().getParent().getChildAt(target.getJohnNode().getIndex()-1);
		if("mo".equals(prevChild.getTag())){
			prevChild.remove();
		}
		source.getJohnNode().remove();
		
		EquationTransporter.tCanvas.reDraw();
		AlgOutEntry.updateAlgOut();
		
		HTML b = target.getJohnNode().getTree().toMathML();
		RootPanel.get().add(b);
		EquationTransporter.parseJQMath(b.getElement());
//		EquationTransporter.changeEquation(target.getJohnNode().getTree().toMathML());
		

	}

	@Override
	public void onEnter(DragContext context) {
		target.addStyleName("mouseOverlay");
	}

	@Override
	public void onLeave(DragContext context) {
		target.removeStyleName("mouseOverlay");
		super.onLeave(context);
	}
	

}
