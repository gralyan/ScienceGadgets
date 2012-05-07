package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.algebramanipulation.AlgOutEntry;
import com.sciencegadgets.client.algebramanipulation.EquationTransporter;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public abstract class AbstractMathDropController extends AbstractDropController {
	protected MLElementWrapper source;
	protected MLElementWrapper target;
	protected JohnNode sourceNode;
	protected JohnNode targetNode;

	public AbstractMathDropController(Widget dropTarget) {
		super(dropTarget);
		target = (MLElementWrapper) dropTarget;
	}

	@Override
	public void onDrop(DragContext context) {
		source = ((MLElementWrapper) context.draggable);
		sourceNode = source.getJohnNode();
		targetNode = target.getJohnNode();

		// Actual changes, abstract method to be overridden
		onChange();
		
		for (MLElementWrapper wrap : targetNode.getTree().getWrappers()) {
			wrap.removeStyleName("selectedDropWrapper");
			wrap.getJoinedWrapper().removeStyleName("selectedDropWrapper");
		}

		// Updates
		HTML mathML = targetNode.getTree().toMathML();
		AlgOutEntry.updateAlgOut(new HTML(mathML.getHTML()));
		EquationTransporter.changeEquation(mathML);
	}

	@Override
	public void onEnter(DragContext context) {
		// Style of target when source is dragged over
		target.addStyleName("mouseOverlay");
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		target.removeStyleName("mouseOverlay");
	}

	abstract void onChange();

}
