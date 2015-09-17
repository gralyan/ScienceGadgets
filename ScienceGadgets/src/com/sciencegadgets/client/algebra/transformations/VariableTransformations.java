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

import java.util.Map.Entry;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.EquationWrapper;
import com.sciencegadgets.client.algebra.SystemOfEquations;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.UnitMap;

public class VariableTransformations extends
		TransformationList<VariableTransformationButton> {
	private static final long serialVersionUID = -7278266823179858612L;
	
	EquationNode variableNode;
	SystemOfEquations systemOfEq;

	public VariableTransformations(EquationNode variableNode) {
		super(variableNode);

		this.variableNode = variableNode;

		try {
			systemOfEq = ((EquationWrapper) variableNode.getWrapper())
					.getAlgebraActivity().getSystem();
		} catch (NullPointerException e) {
			systemOfEq = Moderator.getCurrentAlgebraActivity().getSystem();
			JSNICalls
					.error("Can't get system of equations from node in SubstituteOutButton");
		}

		isolatedVariable_check();
		plugIn_check();
	}

	private void isolatedVariable_check() {
		if (TypeSGET.Equation.equals(variableNode.getParentType())) {
			// add(new EvaluatePromptButton(this));
			add(new SubstituteOutButton(this));
		}
	}

	private void plugIn_check() {
		add(new PlugInButton(this));
	}
}

// /////////////////////////////////////////////////////////////
// Transformation Buttons
// ////////////////////////////////////////////////////////////

abstract class VariableTransformationButton extends TransformationButton {
	EquationNode variableNode;

	VariableTransformationButton(String html, VariableTransformations context) {
		super(html, context);
		addStyleName(CSS.VARIABLE);
		this.variableNode = context.variableNode;
	}
}

/**
 * Allows for substitution method with another equation into another variable of
 * the same quantity kind
 * 
 */
class SubstituteOutButton extends VariableTransformationButton {
	SystemOfEquations systemOfEq;

	SubstituteOutButton(VariableTransformations context) {
		super("Substitute", context);
		systemOfEq = context.systemOfEq;
	}

	@Override
	public Badge getAssociatedBadge() {
		return null;
	}

	@Override
	public boolean meetsAutoTransform() {
		return true;
	}

	@Override
	public void transform() {
		final Prompt dialog = new Prompt(false);
		EquationHTML title = variableNode.getTree().getDisplayClone();
		title.autoFillParent = false;
		dialog.add(title);

		// UnitAttribute unit = variableNode.getUnitAttribute();
		// if (unit == null || unit.toString().equals("")) {
		// dialog.add(new Label(
		// "Variable must have units"));
		// return;
		// }

		SelectionPanel eqSelect = new SelectionPanel("Substitute");
		UnitMap substituteUnitMap = variableNode.getUnitMap();
		for (EquationTree eqTree : systemOfEq.getNonCurrentList()) {
			for (EquationNode eqNode : eqTree.getNodes()) {
				if (TypeSGET.Variable.equals(eqNode.getType())
						&& eqNode.getUnitMap().isConvertableTo(
								substituteUnitMap)) {
					EquationHTML disp = eqTree.getDisplayClone();
					disp.addStyleName(CSS.SUBSTITUTION_CELL);
					a: for (Entry<Element, EquationNode> entry : disp.displayMap
							.entrySet()) {
						if (eqNode.equals(entry.getValue())) {
							entry.getKey().addClassName(CSS.HIGHLIGHT);
							break a;
						}
					}
					eqSelect.add(disp.toString(), null, eqNode);
				}
			}
		}
		if (eqSelect.getCells().isEmpty()) {
			dialog.add(new Label(
					"No equations with similar variables available"));
		} else {
			eqSelect.addSelectionHandler(new SelectionHandler() {
				@Override
				public void onSelect(Cell selected) {

					EquationNode subInto = (EquationNode) selected.getEntity();

					substitute(variableNode, subInto);

					dialog.removeFromParent();
					dialog.hide();
				}
			});
		}
		dialog.add(eqSelect);
		dialog.setGlassEnabled(true);
		dialog.setAnimationEnabled(true);
		dialog.center();
	}

	private void substitute(EquationNode isolatedVar, EquationNode subInto) {

		EquationNode substitute = null;
		if (variableNode.isLeftSide()) {
			substitute = variableNode.getTree().getRightSide();
		} else if (variableNode.isRightSide()) {
			substitute = variableNode.getTree().getLeftSide();
		} else {
			JSNICalls.error("Could not find the element to substitute"
					+ " in: \n" + variableNode.getParent());
			return;
		}

		EquationTree newTree = subInto.getTree();
		systemOfEq.moveToWorkingTree(newTree);
		systemOfEq.add(new EquationTree(newTree.getEquationXMLClone(), false));

		EquationNode newNode = newTree.newNode(substitute.getXMLClone());
		subInto.replace(newNode);

		Moderator.switchToAlgebra(newTree, ActivityType.interactiveequation,
				true);

	}
}

/**
 * Allows for substitution method with another equation into another variable of
 * the same quantity kind
 * 
 */
class PlugInButton extends VariableTransformationButton {
	PlugInButton(VariableTransformations context) {
		super("Plug In", context);
	}

	@Override
	public Badge getAssociatedBadge() {
		return null;
	}

	@Override
	public boolean meetsAutoTransform() {
		return true;
	}

	@Override
	public void transform() {
		AlgebraActivity.NUMBER_SPEC_PROMPT(variableNode, true, true);
	}
}