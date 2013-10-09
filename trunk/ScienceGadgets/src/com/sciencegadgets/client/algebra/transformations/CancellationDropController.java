package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathWrapper;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;

public class CancellationDropController extends AbstractDropController {

	Label response = new Label("Cancel");

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

		MathNode dragParent = drag.getParent();
		if (Type.Fraction.equals(dragParent.getType())) {
			drag.replace(Type.Number, "1");
		} else {
			MathNode dragOp = null;
			if (drag.getIndex() == 0) {
				dragOp = drag.getNextSibling();
			} else {
				dragOp = drag.getPrevSibling();
			}
			if (dragOp != null && Type.Operation.equals(dragOp.getType())) {
				dragOp.remove();
			}
			drag.remove();
			dragParent.decase();
		}

		MathNode targetParent = target.getParent();
		if (Type.Fraction.equals(targetParent.getType())) {
			target.replace(Type.Number, "1");
		} else {
			MathNode TargetOp = null;
			if (target.getIndex() == 0) {
				TargetOp = target.getNextSibling();
			} else {
				TargetOp = target.getPrevSibling();
			}
			if (TargetOp != null && Type.Operation.equals(TargetOp.getType())) {
				TargetOp.remove();
			}
			target.remove();
			targetParent.decase();
		}

		Moderator.reloadEquationPanel("Cancellation");
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
