package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class AssociativeDropController extends AbstractDropController {
	
	Label switchResponse = new Label("Switch");

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
		boolean isSum = TypeML.Sum.equals(parent.getType());

		drag.highlight();
		target.highlight();

		MathNode dragOp = drag.getPrevSibling();
		if (dragOp == null || !TypeML.Operation.equals(dragOp.getType())) {
			if (isSum) {
				dragOp = drag.getTree().NEW_NODE(TypeML.Operation,
						Operator.PLUS.getSign());
			} else {
				dragOp = drag.getTree().NEW_NODE(TypeML.Operation,
						Operator.getMultiply().getSign());
			}
		}

		if (drag.getIndex() < target.getIndex()) {// add after drop

			parent.addAfter(target.getIndex(), dragOp);
			parent.addAfter(dragOp.getIndex(), drag);

			MathNode firstNode = parent.getFirstChild();
			if (TypeML.Operation.equals(firstNode.getType()) && !Operator.MINUS.getSign().equals(firstNode.getSymbol())) {
				firstNode.remove();
			}
		} else {// add before drop
			
			if(target.getIndex() == 0){
				if(isSum){
					parent.addBefore(0, TypeML.Operation, Operator.PLUS.getSign());
				}else{
				parent.addBefore(0, TypeML.Operation, Operator.getMultiply().getSign());
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
		AlgebraActivity.reloadEquationPanel("Associative Property", Rule.COMMUNATIVE_PROPERTY);
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName("selectedDropWrapper");
		AlgebraActivity.algTransformMenu.clear();
		AlgebraActivity.algTransformMenu.add(switchResponse);
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName("selectedDropWrapper");
		AlgebraActivity.algTransformMenu.remove(switchResponse);
	}
}
