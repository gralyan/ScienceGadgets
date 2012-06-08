package com.sciencegadgets.client.algebramanipulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.Log;

public class WrapDragController extends PickupDragController {

	// private ArrayList<DropController> dropList;
	// private ArrayList<MLElementWrapper> dropWrapperList;
	private Map<DropController, MLElementWrapper> dropMap;

	public WrapDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
		// dropList = new ArrayList<DropController>();
		// dropWrapperList = new ArrayList<MLElementWrapper>();
		dropMap = new HashMap<DropController, MLElementWrapper>();
		this.setBehaviorDragStartSensitivity(5);
	}

	@Override
	public void dragStart() {
		super.dragStart();
		MLElementWrapper wrap = (MLElementWrapper) context.draggable;
		wrap.setText(wrap.getJohnNode().toString());//getElementWrapped().getInnerText());
	}

	@Override
	public void dragEnd() {
		super.dragEnd();
		MLElementWrapper wrap = (MLElementWrapper) context.draggable;
		wrap.setText("");
		// TODO This should clear the drop target highlights as well as the
		// wrapper being dragged. The drop targets aren't being cleared.
		wrap.select(false);
	}

	Set<DropController> getDropList() {
		// return dropList;
		return dropMap.keySet();
	}

	Collection<MLElementWrapper> getDropWrapList() {
		// return dropWrapperList;
		return dropMap.values();
	}

	@Override
	public void registerDropController(DropController dropController) {
		super.registerDropController(dropController);
		if (dropController.getDropTarget() instanceof MLElementWrapper) {
			// dropWrapperList.add((MLElementWrapper) dropController
			// .getDropTarget());
			// dropList.add(dropController);
			dropMap.put(dropController,
					(MLElementWrapper) dropController.getDropTarget());
		} else if (!(dropController.getDropTarget() instanceof AbsolutePanel)) {
			Log.severe("Didn't register "
					+ dropController.getDropTarget().toString());
			throw new ClassCastException(
					"The Drop controller must have an MLElementWrapper as a target");
		}
	}

	@Override
	public void unregisterDropController(DropController dropController) {
		super.unregisterDropController(dropController);
		// dropWrapperList.remove((MLElementWrapper) dropController
		// .getDropTarget());
		// dropList.remove(dropController);
		dropMap.remove(dropController);
	}

	@Override
	public void unregisterDropControllers() {
		super.unregisterDropControllers();
		// dropWrapperList.clear();
		// dropList.clear();
		dropMap.clear();
	}

}