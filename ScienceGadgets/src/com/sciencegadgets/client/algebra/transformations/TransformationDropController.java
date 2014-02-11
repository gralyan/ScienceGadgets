package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.FitParentHTML;
import com.sciencegadgets.client.Moderator;

public class TransformationDropController extends AbstractDropController {

	protected FitParentHTML response = new FitParentHTML();
	
	public TransformationDropController(Widget dropTarget) {
		super(dropTarget);
		
		response.addStyleName(CSS.DROP_ENTER_RESPONSE);
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName(CSS.SELECTED_DROP_WRAPPER);
		Moderator.getCurrentAlgebraActivity().lowerEqArea.clear();
		Moderator.getCurrentAlgebraActivity().lowerEqArea.add(response);
	}
	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName(CSS.SELECTED_DROP_WRAPPER);
		Moderator.getCurrentAlgebraActivity().lowerEqArea.remove(response);
	}
}
