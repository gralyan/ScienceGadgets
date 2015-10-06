package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.sciencegadgets.client.algebra.ResponseNote;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;

public class BothSidesDrop extends TransformationDropController{
	final BothSidesButton button;

	public BothSidesDrop(WrapDragController dragController, BothSidesButton button, Wrapper targetWrapper) {
		super(targetWrapper);
		this.button = button;

		response.setText(ResponseNote.BothSides.toString());
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		
		button.execute();;
		
	}
}
