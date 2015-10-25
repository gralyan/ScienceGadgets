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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.EquationPanel;
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
	static HighlightSubstitutionAnimation subAnimation = new HighlightSubstitutionAnimation();

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
		title.getElement().getStyle().setFontSize(200, Unit.PCT);
		title.autoFillParent = false;
		Element varEl = title.getHTMLElement(variableNode);
		if (varEl != null) {
			varEl.addClassName(CSS.HIGHLIGHT);
		}
		dialog.add(title);

		// UnitAttribute unit = variableNode.getUnitAttribute();
		// if (unit == null || unit.toString().equals("")) {
		// dialog.add(new Label(
		// "Variable must have units"));
		// return;
		// }

		SelectionPanel eqSelect = new SelectionPanel("Substitute Into");
		UnitMap substituteUnitMap = variableNode.getUnitMap();
		String varSymbol = variableNode.getSymbol();

		// Collect possible substitution sets (set of variables similar in
		// symbol and unit to substitution variable)
		HashMap<EquationTree, ArrayList<EquationNode>> substituteSets = new HashMap<EquationTree, ArrayList<EquationNode>>();
		for (EquationTree eqTree : systemOfEq.getNonWorkingTrees().keySet()) {

			for (EquationNode eqNode : eqTree.getNodes()) {
				if (TypeSGET.Variable.equals(eqNode.getType())
						&& eqNode.getSymbol().equals(varSymbol)
						&& eqNode.getUnitMap().isConvertableTo(
								substituteUnitMap)) {
					ArrayList<EquationNode> set = substituteSets.get(eqTree);
					if (set == null) {
						set = new ArrayList<EquationNode>();
						substituteSets.put(eqTree, set);
					}
					set.add(eqNode);
				}
			}
		}

		for (Entry<EquationTree, ArrayList<EquationNode>> possible : substituteSets
				.entrySet()) {
			ArrayList<EquationNode> set = possible.getValue();
			EquationHTML disp = possible.getKey().getDisplayClone();
			disp.addStyleName(CSS.SUBSTITUTION_CELL);
			for (EquationNode eqNode : set) {
				Element el = disp.getHTMLElement(eqNode);
				if (el != null) {
					el.addClassName(CSS.HIGHLIGHT);
				}
			}
			Cell cell = eqSelect.add(disp.toString(), null, set);
			if (systemOfEq.getInfo(possible.getKey()).isArchived()) {
				cell.addStyleName(CSS.ARCHIVED);
			}
		}

		if (eqSelect.getCells().isEmpty()) {
			dialog.add(new Label(
					"No equations with similar variables available"));
		} else {
			eqSelect.addSelectionHandler(new SelectionHandler() {
				@Override
				public void onSelect(Cell selected) {

					@SuppressWarnings("unchecked")
					ArrayList<EquationNode> subInto = (ArrayList<EquationNode>) selected
							.getEntity();

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

	private void substitute(EquationNode isolatedVar,
			ArrayList<EquationNode> subIntoList) {

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

		EquationTree newTree = subIntoList.get(0).getTree();
		systemOfEq.moveToWorkingTree(newTree);
		systemOfEq.archive(new EquationTree(newTree.getEquationXMLClone(), false));

		LinkedList<EquationNode> subs = new LinkedList<EquationNode>();
		for (EquationNode subIntoNode : subIntoList) {
			subIntoNode.highlight();
		}
		for (EquationNode subIntoNode : subIntoList) {
			EquationNode newNode = newTree.newNode(substitute.getXMLClone());
			subs.add(newNode);
			subs.addAll(newNode.getChildren());
			subIntoNode.replace(newNode);
		}
		

//		Moderator.switchToAlgebra(newTree, ActivityType.interactiveequation,
//				true);
		String comment = JSNICalls.elementToString(isolatedVar.getTree().getDisplay().getElement());
		Moderator.reloadEquationPanel(comment, Skill.SUBSTITUTION, null);

		subAnimation.init(newTree.getRoot().getId(), subs);
		subAnimation.run(2000);
	}
}

class HighlightSubstitutionAnimation extends Animation {
	LinkedList<Style> subStyles = new LinkedList<Style>();

	void init(String rootID, LinkedList<EquationNode> subs) {
		subStyles.clear();
		for (EquationNode eNode : subs) {
			Element el = DOM.getElementById(eNode.getId()
					+ EquationPanel.OF_LAYER + rootID);
			if (el != null) {
				subStyles.add(el.getStyle());
			}
		}
	}

	@Override
	protected void onUpdate(double progress) {
		double inverseProgress = 1-progress;
		for(Style style : subStyles) {
			style.setBackgroundColor("rgba(0,255,0,"+inverseProgress+")");
		}
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
		AlgebraActivity.NUMBER_SPEC_PROMPT(variableNode, true, true, variableNode.getUnitAttribute());
	}
}