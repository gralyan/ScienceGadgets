package com.sciencegadgets.client.conversion;

import java.util.LinkedList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;

public class ReorderDropController extends AbstractDropController {

	private ConversionActivity conversionActivity;
	private ConversionWrapper targetWrapper;

	public ReorderDropController(ConversionWrapper targetWrapper) {
		super(targetWrapper);
		conversionActivity = targetWrapper.getConversionActivity();
		this.targetWrapper = targetWrapper;
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);

		MathNode drag = ((Wrapper) context.draggable).getNode();
		MathNode target = ((Wrapper) getDropTarget()).getNode();
		MathNode parent = drag.getParent();

		LinkedList<UnitDisplay> displayList = conversionActivity.unitDisplays;
		ConversionWrapper dragWraper = ((ConversionWrapper) context.draggable);
		UnitDisplay dragDisplay = dragWraper.getUnitDisplay();
		UnitDisplay targetDisplay = targetWrapper.getUnitDisplay();

		if (drag.getIndex() < target.getIndex()) {// add after drop
			parent.addAfter(target.getIndex(), drag);
			displayList.remove(targetDisplay);
			displayList.add(displayList.indexOf(dragDisplay), targetDisplay);

		} else {// add before drop
			parent.addBefore(target.getIndex(), drag);
			displayList.remove(dragDisplay);
			displayList.add(displayList.indexOf(targetDisplay), dragDisplay);
		}
		conversionActivity.reloadEquation();
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName(CSS.SELECTED_DROP_WRAPPER);
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName(CSS.SELECTED_DROP_WRAPPER);
	}
}
