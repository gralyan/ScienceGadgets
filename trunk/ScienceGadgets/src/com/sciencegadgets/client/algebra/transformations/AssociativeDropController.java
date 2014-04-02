package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.EquationWrapper;
import com.sciencegadgets.client.algebra.ResponseNote;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class AssociativeDropController extends TransformationDropController {
	
	public AssociativeDropController(EquationWrapper dropWrapper) {
		super(dropWrapper);
		
		response.setText(ResponseNote.Switch.toString());
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);

		EquationNode drag = ((Wrapper) context.draggable).getNode();
		EquationNode target = ((Wrapper) getDropTarget())
				.getNode();
		EquationNode parent = drag.getParent();
		boolean isSum = TypeSGET.Sum.equals(parent.getType());

		drag.highlight();
		target.highlight();

		EquationNode dragOp = drag.getPrevSibling();
		if (dragOp == null || !TypeSGET.Operation.equals(dragOp.getType())) {
			if (isSum) {
				dragOp = drag.getTree().NEW_NODE(TypeSGET.Operation,
						Operator.PLUS.getSign());
			} else {
				dragOp = drag.getTree().NEW_NODE(TypeSGET.Operation,
						Operator.getMultiply().getSign());
			}
		}

		if (drag.getIndex() < target.getIndex()) {// add after drop

			parent.addAfter(target.getIndex(), dragOp);
			parent.addAfter(dragOp.getIndex(), drag);

			EquationNode firstNode = parent.getFirstChild();
			if (TypeSGET.Operation.equals(firstNode.getType()) && !Operator.MINUS.getSign().equals(firstNode.getSymbol())) {
				firstNode.remove();
			}
		} else {// add before drop
			
			if(target.getIndex() == 0){
				if(isSum){
					parent.addFirst(TypeSGET.Operation, Operator.PLUS.getSign());
				}else{
				parent.addFirst(TypeSGET.Operation, Operator.getMultiply().getSign());
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
		Moderator.reloadEquationPanel("Associative Property", Rule.COMMUNATIVE_PROPERTY);
	}

}
