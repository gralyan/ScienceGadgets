package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.algebramanipulation.EquationTransporter;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public abstract class AbstractMathDropController extends AbstractDropController {
	protected MLElementWrapper source;
	protected MLElementWrapper target;
	protected JohnNode sourceNode;
	protected JohnNode targetNode;
	public String change = "";

	public AbstractMathDropController(Widget dropTarget) {
		super(dropTarget);
		target = (MLElementWrapper) dropTarget;
		targetNode = target.getJohnNode();

	}

	@Override
	public void onDrop(DragContext context) {

		source = ((MLElementWrapper) context.draggable);
		sourceNode = source.getJohnNode();
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

	/**
	 * The changes that occur in the tree. findChange should be called first.
	 */
	abstract void onChange();
	
	/**
	 * A description of the change that occurs, to be used for hints
	 * @param sourceNode
	 * @return
	 */
	public abstract String findChange(JohnNode sourceNode);
	
	/**
	 * A comment for the change, to be used for the AlgOut comment
	 * @return
	 */
	abstract String changeComment();

}
