package com.sciencegadgets.client.algebra.transformations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeSGET;

public class InterFractionTransformations extends
		TransformationList<InterFractionButton> {
	private static final long serialVersionUID = -5789993931319675147L;

	EquationNode drag;
	EquationNode drop;

	public enum DropType {
		CANCEL, REMOVE_ONE, DIVIDE, EXPONENTIAL, LOG_COMBINE, TRIG_COMBINE;
	}

	/**
	 * Place drop targets on drag controller, allowing for operations between
	 * terms of the numerator and denominator. This allows users to cancel,
	 * divide, and combine terms on either side of the fraction
	 * 
	 * @param node
	 * @return
	 */
	public InterFractionTransformations(EquationNode drag) {
		super(drag);
		JSNICalls.TIME_ELAPSED("inter1 ");
		this.drag = drag;

		JSNICalls.TIME_ELAPSED("inter2 ");
		EquationNode thisSide = null;
		EquationNode parent = drag.getParent();
		switch (parent.getType()) {
		case Fraction:
			thisSide = drag;
			break;
		case Term:
			if (TypeSGET.Fraction.equals(parent.getParentType())
					&& !TypeSGET.Operation.equals(drag.getType())) {
				thisSide = parent;
				break;
			}// else fall through
		default:
			return;
		}

		JSNICalls.TIME_ELAPSED("inter3 ");
		EquationNode otherSide = thisSide.getIndex() == 0 ? thisSide
				.getNextSibling() : thisSide.getPrevSibling();

		HashMap<EquationNode, DropType> dropTargets = new HashMap<EquationTree.EquationNode, DropType>();

		JSNICalls.TIME_ELAPSED("inter4 ");
		if (drag.isLike(otherSide)) {// Cancel drop on entire other sides
			dropTargets.put(otherSide, DropType.CANCEL);

		} else if (TypeSGET.Term.equals(otherSide.getType())) {
			// Drop on each term child
			for (EquationNode child : otherSide.getChildren()) {
				if (TypeSGET.Operation.equals(child.getType())) {
					continue;
				}
				JSNICalls.TIME_ELAPSED("inter4.1 ");
				if (drag.isLike(child)) {// Cancel drop on child
					JSNICalls.TIME_ELAPSED("inter4.2 ");
					dropTargets.put(child, DropType.CANCEL);
					JSNICalls.TIME_ELAPSED("inter4.3 ");
				} else {// Drop on child
					JSNICalls.TIME_ELAPSED("inter4.4 ");
					addDropTarget(child, drag, dropTargets);
					JSNICalls.TIME_ELAPSED("inter4.5 ");
				}
				JSNICalls.TIME_ELAPSED("inter4.6 ");
			}

			JSNICalls.TIME_ELAPSED("inter5 ");
		} else {// Drop on entire other side
			addDropTarget(otherSide, drag, dropTargets);
		}
		JSNICalls.TIME_ELAPSED("inter6 ");

		LinkedList<InterFractionDrop> dropControllers = new LinkedList<InterFractionDrop>();
		if (dropTargets.size() > 0) {

			WrapDragController dragController = null;
			if (drag.getWrapper() != null) {
				dragController = drag.getWrapper().addDragController();
			}
			JSNICalls.TIME_ELAPSED("inter7 ");
			for (Entry<EquationNode, DropType> dropTarget : dropTargets
					.entrySet()) {

				JSNICalls.TIME_ELAPSED("interFOR 1");
				InterFractionButton butt = new InterFractionButton(this, drag,
						dropTarget.getKey(), dropTarget.getValue());
				JSNICalls.TIME_ELAPSED("interFOR 2");
				add(butt);
				JSNICalls.TIME_ELAPSED("interFOR 3");

				if (dragController != null && butt.getTarget().getWrapper() != null) {
					InterFractionDrop drop = new InterFractionDrop(
							dragController, butt);
					JSNICalls.TIME_ELAPSED("interFOR 4");
					dropControllers.add(drop);
					JSNICalls.TIME_ELAPSED("interFOR 5");
					dragController.registerDropController(drop);
					JSNICalls.TIME_ELAPSED("interFOR 6");
				}
			}
		}
	}

	/**
	 * To be used by {@link #interFractionDrop_check}
	 */
	private static void addDropTarget(EquationNode target, EquationNode drag,
			HashMap<EquationTree.EquationNode, DropType> dropTargets) {

		TypeSGET dragType = drag.getType();

		if (TypeSGET.Number.equals(dragType)
				&& "1".equals(drag.getSymbol())
				// Can't remove one if it's the numerator
				&& !(TypeSGET.Fraction.equals(drag.getParentType()) && drag.getIndex() == 0)) {
			dropTargets.put(target, DropType.REMOVE_ONE);
			return;
		}

		// The rest of this method is only applicable if
		if (!dragType.equals(target.getType())) {
			return;
		}

		switch (dragType) {
		case Number:
			if (!"0".equals(drag.getSymbol())) {
				dropTargets.put(target, DropType.DIVIDE);
			}
			break;
		case Exponential:
			if (drag.getFirstChild().isLike(target.getFirstChild())) {
				dropTargets.put(target, DropType.EXPONENTIAL);
			}
			break;
		case Log:
			if (drag.getAttribute(MathAttribute.LogBase).equals(
					target.getAttribute(MathAttribute.LogBase))
					&& TypeSGET.Number.equals(drag.getFirstChild().getType())) {
				dropTargets.put(target, DropType.LOG_COMBINE);
			}
			break;
		case Trig:
			if (target.getFirstChild().isLike(drag.getFirstChild())) {
				// Make sure (drag or target) is sin and other is cos
				TrigFunctions dragFunc = TrigFunctions.valueOf(drag
						.getAttribute(MathAttribute.Function));
				TrigFunctions targetfunc = TrigFunctions.valueOf(target
						.getAttribute(MathAttribute.Function));
				TrigFunctions sin = TrigFunctions.sin;
				TrigFunctions cos = TrigFunctions.cos;
				if ((sin.equals(dragFunc) && cos.equals(targetfunc))
						|| (cos.equals(dragFunc) && sin.equals(targetfunc))) {
					dropTargets.put(target, DropType.TRIG_COMBINE);
				}
			}
			break;
		}
	}
}
