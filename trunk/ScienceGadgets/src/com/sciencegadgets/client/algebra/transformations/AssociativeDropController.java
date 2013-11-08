package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type.Operator;

public class AssociativeDropController extends AbstractDropController {
	
	Label response = new Label("Switch");

	public AssociativeDropController(Wrapper dropWrapper) {
		super(dropWrapper);
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);

		MathNode drag = ((Wrapper) context.draggable).getNode();
		MathNode target = ((Wrapper) getDropTarget())
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
		Moderator.reloadEquationPanel("Associative Property", Rule.Commutative);
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
