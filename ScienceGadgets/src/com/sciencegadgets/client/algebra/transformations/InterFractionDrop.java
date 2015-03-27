package com.sciencegadgets.client.algebra.transformations;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.sciencegadgets.client.algebra.ResponseNote;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.transformations.InterFractionTransformations.DropType;

public class InterFractionDrop extends TransformationDropController {

	private DropType dropType;
	private InterFractionButton button;


	public InterFractionDrop(WrapDragController dragController, InterFractionButton button) {
		super(button.getTarget().getWrapper());

		this.button = button;
		this.dropType = button.getDropType();

		switch (dropType) {
		case CANCEL:
			response.setText(ResponseNote.Cancel.toString());
			break;
		case DIVIDE:
		case REMOVE_ONE:
			response.setText(ResponseNote.Divide.toString());
			break;
		case EXPONENTIAL:
		case LOG_COMBINE:
		case TRIG_COMBINE:
			response.setText(ResponseNote.Combine.toString());
			break;
		}
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);
		
		button.transform();
		
//		System.out.println("context "+context);
//		System.out.println("context.draggable "+context.draggable);
//		System.out.println("((AlgebaWrapper) context.draggable).getNode() "+((AlgebaWrapper) context.draggable).getNode());
//		drag = ((AlgebaWrapper) context.draggable).getNode();
//		target = ((AlgebaWrapper) getDropTarget()).getNode();
//
//		dropHTML = "<div style=\"display:inline-block; vertical-align:middle;\">"
//				+ "<div style=\"border-bottom:1px solid;\">"
//				+ target.getHTMLString(true, true)
//				+ "</div>"
//				+ "<div>"
//				+ drag.getHTMLString(true, true) + "</div>" + "</div>";
//
//		switch (dropType) {
//		case CANCEL:
//			cancelDrop();
//			break;
//		case REMOVE_ONE:
//			complete(true);
//			break;
//		case DIVIDE:
//			dividePrompt();
//			break;
//		case EXPONENTIAL:
//			exponentialDrop();
//			break;
//		case LOG_COMBINE:
//			logDrop();
//			break;
//		case TRIG_COMBINE:
//			trigDrop();
//			break;
//		}

	}
	
	public DropType getDropType() {
		return dropType;
	}
	

}
