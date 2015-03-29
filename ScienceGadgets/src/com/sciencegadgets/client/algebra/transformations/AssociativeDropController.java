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
		EquationNode target = ((Wrapper) getDropTarget()).getNode();
		EquationNode parent = drag.getParent();
		boolean isSum = TypeSGET.Sum.equals(parent.getType());

		drag.highlight();
		target.highlight();

		EquationNode dragOp = drag.getPrevSibling();
		if (dragOp == null || !TypeSGET.Operation.equals(dragOp.getType())) {
			if (isSum) {
				dragOp = drag.getTree().newNode(TypeSGET.Operation,
						Operator.PLUS.getSign());
			} else {
				dragOp = drag.getTree().newNode(TypeSGET.Operation,
						Operator.getMultiply().getSign());
			}
		}

		if (drag.getIndex() < target.getIndex()) {// add after drop

			parent.addAfter(target.getIndex(), dragOp);
			parent.addAfter(dragOp.getIndex(), drag);

			EquationNode firstNode = parent.getFirstChild();
			if (TypeSGET.Operation.equals(firstNode.getType())
					&& !Operator.MINUS.getSign().equals(firstNode.getSymbol())) {
				firstNode.remove();
			}
		} else {// add before drop

			if (target.getIndex() == 0) {
				if (isSum) {
					parent.addFirst(TypeSGET.Operation, Operator.PLUS.getSign());
				} else {
					parent.addFirst(TypeSGET.Operation, Operator.getMultiply()
							.getSign());
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
		HashMap<Skill, Integer> skills = new HashMap<Skill, Integer>();
		skills.put(Skill.COMMUNATIVE_PROPERTY, 1);
		((EquationWrapper) getDropTarget()).getAlgebraActivity()
				.reloadEquationPanel("Associative Property", skills, true, drag.getId());
		// Moderator.reloadEquationPanel("Associative Property",
		// Skill.COMMUNATIVE_PROPERTY);
	}

}
