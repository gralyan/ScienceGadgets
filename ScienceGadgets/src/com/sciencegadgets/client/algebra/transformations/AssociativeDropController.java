package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathWrapper;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type.Operator;

public class AssociativeDropController extends AbstractDropController {

	public AssociativeDropController(MathWrapper dropWrapper) {
		super(dropWrapper);
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);

		MathNode drag = ((MathWrapper) context.draggable).getNode();
		MathNode target = ((MathWrapper) context.dropController.getDropTarget())
				.getNode();
		MathNode parent = drag.getParent();
		boolean isSum = Type.Sum.equals(parent.getType());

		drag.highlight();
		target.highlight();

		MathNode dragOp = drag.getPrevSibling();
		if (dragOp == null || !Type.Operation.equals(dragOp.getType())) {
			if (isSum) {
				dragOp = drag.getTree().NEW_NODE(Type.Operation,
						Operator.PLUS.getSign());
			} else {
				dragOp = drag.getTree().NEW_NODE(Type.Operation,
						Operator.getMultiply().getSign());
			}
		}

		if (drag.getIndex() < target.getIndex()) {// add after drop

			parent.addAfter(target.getIndex(), dragOp);
			parent.addAfter(dragOp.getIndex(), drag);

			MathNode firstNode = parent.getFirstChild();
			if (Type.Operation.equals(firstNode.getType()) && !Operator.MINUS.getSign().equals(firstNode.getSymbol())) {
				firstNode.remove();
			}
		} else {// add before drop
			
			if(target.getIndex() == 0){
				if(isSum){
					parent.addBefore(0, Type.Operation, Operator.PLUS.getSign());
				}else{
				parent.addBefore(0, Type.Operation, Operator.getMultiply().getSign());
				}
			}
			int dropIndex = target.getPrevSibling().getIndex();

			if (dropIndex == 0
					&& !Operator.MINUS.getSign().equals(dragOp.getSymbol())) {
				dragOp.remove();
			} else {
				parent.addBefore(dropIndex, dragOp);
				dropIndex++;
			}
			parent.addBefore(dropIndex, drag);

		}
		Moderator.reloadEquationPanel("Associative Property");
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		context.dropController.getDropTarget().addStyleName("selectedWrapper");
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		context.dropController.getDropTarget().removeStyleName("selectedWrapper");
	}

}
