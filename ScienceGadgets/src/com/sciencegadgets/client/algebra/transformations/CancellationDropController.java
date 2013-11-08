package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathWrapper;
import com.sciencegadgets.client.algebra.ResponseNote;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;

public class CancellationDropController extends AbstractDropController {

	Label response = new Label(ResponseNote.Cancel.toString());

	public CancellationDropController(MathWrapper dropTarget) {
		super(dropTarget);
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		MathNode drag = ((MathWrapper) context.draggable).getNode();
		MathNode target = ((MathWrapper) getDropTarget()).getNode();

		drag.highlight();
		target.highlight();

		MathNode denomToRemove = null;

		MathNode[] dragAndTarget = { drag, target };
		for (MathNode side : dragAndTarget) {

			MathNode sideParent = side.getParent();
			if (Type.Fraction.equals(sideParent.getType())) {
				if (side.getIndex() == 0) {// numerator
					side.replace(Type.Number, "1");
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
				if (sideOp != null && Type.Operation.equals(sideOp.getType())) {
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
		
		Moderator.reloadEquationPanel("Cancellation", Rule.Cancellation);
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName("selectedDropWrapper");
		AlgebraActivity.contextMenuArea.add(response);
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName("selectedDropWrapper");
		AlgebraActivity.contextMenuArea.remove(response);
	}

}
