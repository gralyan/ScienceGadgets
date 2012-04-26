package com.sciencegadgets.client.AlgebraManipulation;

import java.util.ArrayList;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.AbsolutePanel;

class WrapDragController extends PickupDragController {

	private ArrayList<DropController> dropList;
	private ArrayList<MLElementWrapper> dropWrapperList;

	public WrapDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
		dropList = new ArrayList<DropController>();
		dropWrapperList = new ArrayList<MLElementWrapper>();
		this.setBehaviorDragStartSensitivity(5);
	}

	@Override
	public void dragStart() {
		super.dragStart();
		MLElementWrapper wrap = (MLElementWrapper) context.draggable;
		wrap.setText(wrap.getElementWrapped().getInnerText());
	}

	@Override
	public void dragEnd() {
		super.dragEnd();
		MLElementWrapper wrap = (MLElementWrapper) context.draggable;
		wrap.setText("");
	}

	ArrayList<DropController> getDropList() {
		return dropList;
	}

	ArrayList<MLElementWrapper> getDropWrapList() {
		return dropWrapperList;
	}

	@Override
	public void registerDropController(DropController dropController) {
		super.registerDropController(dropController);
		if (dropController.getDropTarget() instanceof MLElementWrapper) {
			dropWrapperList.add((MLElementWrapper) dropController
					.getDropTarget());
			dropList.add(dropController);
		}
	}

	@Override
	public void unregisterDropController(DropController dropController) {
		super.unregisterDropController(dropController);
		dropWrapperList.remove((MLElementWrapper) dropController
				.getDropTarget());
		dropList.remove(dropController);
	}

	@Override
	public void unregisterDropControllers() {
		super.unregisterDropControllers();
		dropWrapperList.clear();
		dropList.clear();
	}

}