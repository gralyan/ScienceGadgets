package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.AlgebaWrapper;
import com.sciencegadgets.client.algebra.ResponseNote;
import com.sciencegadgets.shared.TypeML;

public class CancellationDropController extends AbstractDropController {

	Label cancelResponse = new Label(ResponseNote.Cancel.toString());

	public CancellationDropController(AlgebaWrapper dropTarget) {
		super(dropTarget);
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		MathNode drag = ((AlgebaWrapper) context.draggable).getNode();
		MathNode target = ((AlgebaWrapper) getDropTarget()).getNode();

		drag.highlight();
		target.highlight();

		MathNode denomToRemove = null;

		MathNode[] dragAndTarget = { drag, target };
		for (MathNode side : dragAndTarget) {

			MathNode sideParent = side.getParent();
			if (TypeML.Fraction.equals(sideParent.getType())) {
				if (side.getIndex() == 0) {// numerator
					side.replace(TypeML.Number, "1");
				} else {// denominator
					denomToRemove = side;
				}
			} else {
				MathNode sideOp = null;
				if (side.getIndex() == 0) {
					sideOp = side.getNextSibling();
				} else {
					sideOp = side.getPrevSibling();
				}
				if (sideOp != null && TypeML.Operation.equals(sideOp.getType())) {
					sideOp.remove();
				}
				side.remove();
				sideParent.decase();
			}
		}
		
		if (denomToRemove != null) {
			MathNode frac = denomToRemove.getParent();
			MathNode fracParent = frac.getParent();
			fracParent.addBefore(frac.getIndex(), frac.getChildAt(0));
			denomToRemove.remove();
			frac.remove();
			fracParent.decase();
		}
		
		AlgebraActivity.reloadEquationPanel("Cancellation", Rule.Cancellation);
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName("selectedDropWrapper");
		AlgebraActivity.algTransformMenu.add(cancelResponse);
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName("selectedDropWrapper");
		AlgebraActivity.algTransformMenu.remove(cancelResponse);
	}

}
