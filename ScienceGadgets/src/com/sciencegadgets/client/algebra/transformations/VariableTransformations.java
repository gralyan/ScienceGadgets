package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;

public class VariableTransformations extends
		TransformationList<VariableTransformationButton> {
	private static final long serialVersionUID = -7278266823179858612L;
	EquationNode variableNode;

	public VariableTransformations(EquationNode variableNode) {
		super(variableNode);

		this.variableNode = variableNode;

		isolatedVariable_check();
		plugIn_check();
	}

	private void isolatedVariable_check() {
		if (TypeSGET.Equation.equals(variableNode.getParentType())) {
			// add(new EvaluatePromptButton(this));
			add(new SubstituteButton(this));
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
		this.variableNode = context.variableNode;
	}
}

/**
 * Allows for substitution method with another equation into another variable of
 * the same quantity kind
 * 
 */
class SubstituteButton extends VariableTransformationButton {
	SubstituteButton(VariableTransformations context) {
		super("Substitute", context);
	}

	@Override
	public
	void transform() {
		String quantityKind = variableNode.getUnitAttribute()
				.getUnitMultiples()[0].getUnitName().getQuantityKind();
		if (quantityKind == null || quantityKind.equals("")) {
			Window.alert("No similar other equations with similar variables available");
			return;
		}
		final DialogBox dialog = new DialogBox(true, true);
		SelectionPanel eqSelect = new SelectionPanel(CSS.EQUATION);
		dialog.add(eqSelect);
		DataModerator.fill_EquationsByQuantities(quantityKind, eqSelect);
		eqSelect.addSelectionHandler(new SelectionHandler() {
			@Override
			public void onSelect(Cell selected) {

				EquationNode eqNode = variableNode.getParent();

				Element substitute = null;
				if (variableNode.isLeftSide()) {
					substitute = variableNode.getTree().getRightSide()
							.getXMLNode();
				} else if (variableNode.isRightSide()) {
					substitute = variableNode.getTree().getLeftSide()
							.getXMLNode();
				} else {
					JSNICalls.error("Could not find the element to substitute"
							+ " in: \n" + eqNode);
					dialog.removeFromParent();
					dialog.hide();
					return;
				}

				String subIntoEqStr = selected.getValue();
				Element subIntoEqEl = new HTML(subIntoEqStr).getElement()
						.getFirstChildElement();

				substitute(variableNode, subIntoEqEl, substitute);

				dialog.removeFromParent();
				dialog.hide();
			}
		});
		dialog.setGlassEnabled(true);
		dialog.setAnimationEnabled(true);
		dialog.center();
	}

	private void substitute(final EquationNode isolatedVar,
			Element subIntoEqEl, Element substitute) {

		// Find the variable to substutute
		NodeList<Element> possibleSubs = subIntoEqEl
				.getElementsByTagName(TypeSGET.Variable.getTag());
		findSub: for (int j = 0; j < possibleSubs.getLength(); j++) {
			Element possibleSub = possibleSubs.getItem(j);
			if (!isolatedVar.getUnitAttribute().equals(
					possibleSub.getAttribute(MathAttribute.Unit
							.getAttributeName()))) {
				continue findSub;
			}
			Element subParent = possibleSub.getParentElement();
			String plugTag = substitute.getTagName();
			if (plugTag.equals(subParent.getTagName())
					&& (plugTag.equals(TypeSGET.Sum.getTag()) || plugTag
							.equals(TypeSGET.Term.getTag()))) {
				Node subPrev = possibleSub.getPreviousSibling();
				if (subPrev != null && subPrev.getNodeName().equals("mo")
						&& "-".equals(((Element) subPrev).getInnerText())) {
					// if a sum is plugged into a variable preceded by minus
					((Element) subPrev).setInnerText("+");
					Element negativeSubstitute = new HTML(
							"<mrow><mn>-1</mn><mo>\u00B7</mo><mfenced><substitute></substitute></mfenced></mrow>")
							.getElement().getFirstChildElement();
					subParent.replaceChild(negativeSubstitute, possibleSub);
					possibleSub = subParent.getElementsByTagName("substitute")
							.getItem(0);
					subParent = possibleSub.getParentElement();
				}
				NodeList<Node> substituteChildren = substitute.getChildNodes();
				// Must save count because it will change during the
				// loop
				int childCount = substituteChildren.getLength();
				for (int k = 0; k < childCount; k++) {
					subParent.insertBefore(substituteChildren.getItem(0),
							possibleSub);
				}
				possibleSub.removeFromParent();
			} else {
				subParent.replaceChild(substitute, possibleSub);
			}
			Moderator.switchToAlgebra(subIntoEqEl, false);
			break;

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
	public
	void transform() {
		AlgebraActivity.NUMBER_SPEC_PROMPT(variableNode, true);
	}
}