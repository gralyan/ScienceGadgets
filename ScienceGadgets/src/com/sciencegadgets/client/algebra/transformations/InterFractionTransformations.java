/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.algebra.transformations;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

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
		this.drag = drag;

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

		EquationNode otherSide = thisSide.getIndex() == 0 ? thisSide
				.getNextSibling() : thisSide.getPrevSibling();

		HashMap<EquationNode, DropType> dropTargets = new HashMap<EquationTree.EquationNode, DropType>();

		if (drag.isLike(otherSide)) {// Cancel drop on entire other sides
			dropTargets.put(otherSide, DropType.CANCEL);

		} else if (TypeSGET.Term.equals(otherSide.getType())) {
			// Drop on each term child
			for (EquationNode child : otherSide.getChildren()) {
				if (TypeSGET.Operation.equals(child.getType())) {
					continue;
				}
				if (drag.isLike(child)) {// Cancel drop on child
					dropTargets.put(child, DropType.CANCEL);
				} else {// Drop on child
					addDropTarget(child, drag, dropTargets);
				}
			}

		} else {// Drop on entire other side
			addDropTarget(otherSide, drag, dropTargets);
		}

		LinkedList<InterFractionDrop> dropControllers = new LinkedList<InterFractionDrop>();
		if (dropTargets.size() > 0) {

			WrapDragController dragController = null;
			if (drag.getWrapper() != null) {
				dragController = drag.getWrapper().getDragController();
			}
			for (Entry<EquationNode, DropType> dropTarget : dropTargets
					.entrySet()) {

				InterFractionButton butt = new InterFractionButton(this, drag,
						dropTarget.getKey(), dropTarget.getValue());
				add(butt);

				if (dragController != null && butt.getTarget().getWrapper() != null) {
					InterFractionDrop drop = new InterFractionDrop(
							dragController, butt);
					dropControllers.add(drop);
					dragController.registerDropController(drop);
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
