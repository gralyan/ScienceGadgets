package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.algebramanipulation.AlgOutEntry;
import com.sciencegadgets.client.algebramanipulation.EquationTransporter;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationtree.JohnTree;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public abstract class AbstractMathDropController extends AbstractDropController {
	protected MLElementWrapper source;
	protected MLElementWrapper target;
	protected JohnNode sourceNode;
	protected JohnNode targetNode;
	protected String change = "";

	public AbstractMathDropController(Widget dropTarget) {
		super(dropTarget);
		target = (MLElementWrapper) dropTarget;
	}

	@Override
	public void onDrop(DragContext context) {

		source = ((MLElementWrapper) context.draggable);
		sourceNode = source.getJohnNode();
		targetNode = target.getJohnNode();
		JohnNode sourceParent = sourceNode.getParent();

		/**
		 * Actual changes, abstract method to be overridden
		 */
		onChange();

		// If the source is the last child, get rid of the parent
		try {
			if (sourceParent.getChildCount() == 1) {
				try {
					JohnNode baseParent = sourceParent.getParent();
					baseParent.add(sourceParent.getIndex(), sourceParent.getFirstChild());
					sourceParent.remove();
					Log.info("removed obsolete parent: "+sourceParent.toString());

				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		} catch (NullPointerException e) {
			Log.severe("source has no parent: " + sourceNode.toString());
			e.printStackTrace();
		}

		// Clean wrappers
		for (MLElementWrapper wrap : targetNode.getTree().getWrappers()) {
			wrap.removeStyleName("selectedDropWrapper");
			wrap.getJoinedWrapper().removeStyleName("selectedDropWrapper");
		}

		// Updates
		HTML mathML = targetNode.getTree().toMathML();
		Log.info("transporting: " + mathML.getHTML());
		// AlgOutEntry.updateAlgOut(new HTML(mathML.getHTML()));
		EquationTransporter.selectEquation(mathML, changeComment());
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
	abstract String changeComment();

}
