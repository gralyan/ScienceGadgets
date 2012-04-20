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
		JohnNode sourceNode = source.getJohnNode();
		JohnNode targetNode = target.getJohnNode();


		
		// Add drop source value to target value
		int src = Integer.parseInt(source.getElementWrapped().getInnerText());
		int targ = Integer
				.parseInt((target).getElementWrapped().getInnerText());
		int ans = src + targ;

		// All drop controllers must me unregistered
		for (MLElementWrapper wrap : targetNode.getTree().getWrappers()) {
			((PickupDragController) wrap.getDragControl())
					.unregisterDropControllers();
			((PickupDragController) wrap.getJoinedWrapper().getDragControl())
					.unregisterDropControllers();
		}

		// Main change
		targetNode.setString("" + ans);

		// Peripheral changes
		int sIndex = sourceNode.getIndex();
		if (sIndex > 0) {
			JohnNode prevChild = sourceNode.getParent().getChildAt(sIndex-1);
			if ("mo".equals(prevChild.getTag())) {
				prevChild.remove();
			}
		}else if("+".equals(sourceNode.getNextSibling().toString())){
			sourceNode.getNextSibling().remove();
		}

		sourceNode.getTree().getWrappers().remove(sourceNode.getWrapper());
		sourceNode.getWrapper().removeFromParent();
		sourceNode.getWrapper().getJoinedWrapper().removeFromParent();
		sourceNode.remove();

		// Updates
		HTML mathML = targetNode.getTree().toMathML();
		EquationTransporter.tCanvas.reDraw();
		AlgOutEntry.updateAlgOut(mathML);

		EquationTransporter.changeEquation(targetNode.getTree().toMathML());

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
