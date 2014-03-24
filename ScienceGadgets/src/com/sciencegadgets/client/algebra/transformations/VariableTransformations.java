package com.sciencegadgets.client.algebra.transformations;

import java.util.HashSet;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.NumberSpecification;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.UnitSelection;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;

public class VariableTransformations extends TransformationList {
	private static final long serialVersionUID = -7278266823179858612L;
	EquationNode variableNode;

	public VariableTransformations(EquationNode variableNode) {
		super(variableNode);

		this.variableNode = variableNode;
		
		isolatedVariable_check();
		plugIn_check();
	}

	private void isolatedVariable_check() {
		if (TypeEquationXML.Equation.equals(variableNode.getParentType())) {
//			add(new EvaluatePromptButton(this));
			add(new SubstituteButton(this));
		}
	}
	
	private void plugIn_check() {
		add(new PlugInButton(this));
	}
}

//class VariableEvaluateSpec extends FlowPanel {
//	protected final DoubleBox valueInput = new DoubleBox();
//	protected UnitSelection unitSelect = null;;
//	protected MathNode mathNode = null;
//	protected Double value = null;
//	protected String unit;
//
//	VariableEvaluateSpec(MathNode mathNode) {
//		this.mathNode = mathNode;
//		String qKind = mathNode.getUnitAttribute().getUnitMultiples()[0].getUnitName().getQuantityKind();
//		unitSelect = new UnitSelection(qKind);
//		Label symbol = new Label(mathNode.getSymbol() + " =");
//
//		symbol.addStyleName(CSS.LAYOUT_ROW);
//		valueInput.addStyleName(CSS.LAYOUT_ROW);
//		unitSelect.addStyleName(CSS.LAYOUT_ROW);
//		unitSelect.setWidth("50%");
//
//		add(symbol);
//		add(valueInput);
//		add(unitSelect);
//
//	}
//}

///////////////////////////////////////////////////////////////
//Transformation Buttons
//////////////////////////////////////////////////////////////

class VariableTransformationButton extends TransformationButton{
	EquationNode variableNode;
	VariableTransformationButton(VariableTransformations context){
		super(context);
	this.variableNode = context.variableNode;
	}
}

/**
 * Allows for the evaluation of the other side by substituting numbers into the
 * appropriate variables
 * 
 */
//class EvaluatePromptButton extends VariableTransformationButton {
//	final static HashSet<VariableEvaluateSpec> varSpecs = new HashSet<VariableEvaluateSpec>();
//
//	EvaluatePromptButton(VariableTransformations context) {
//		super(context);
//
//		setHTML("Evaluate");
//
//		this.addClickHandler(new ClickHandler() {
//			@Override
//			public void onClick(ClickEvent event) {
//				final Prompt prompt = new Prompt();
//
//				varSpecs.clear();
//
//				MathTree tree = variableNode.getTree();
//				LinkedList<Wrapper> wrappers = tree.getWrappers();
//				for (Wrapper wrap : wrappers) {
//					MathNode node = wrap.getNode();
//					if (TypeML.Variable.equals(node.getType())
//							&& !node.equals(variableNode)) {
//						VariableEvaluateSpec varSpec = new VariableEvaluateSpec(
//								node);
//						varSpecs.add(varSpec);
//						prompt.add(varSpec);
//					}
//				}
//				if (varSpecs.size() == 0) {
//					Window.alert("There are no variables to evaluate");
//					return;
//				}
//
//				prompt.addOkHandler(new ClickHandler() {
//
//					@Override
//					public void onClick(ClickEvent event) {
//						// Make sure the entire spec is filled
//						boolean completelyFilled = true;
//						for (VariableEvaluateSpec varSpec : varSpecs) {
//							Double value = varSpec.valueInput.getValue();
//							if (value == null) {
//								varSpec.valueInput.getElement().getStyle()
//										.setBackgroundColor("red");
//								completelyFilled = false;
//							} else {
//								varSpec.valueInput.getElement().getStyle()
//										.clearBackgroundColor();
//								varSpec.value = value;
//							}
//							String unit = varSpec.unitSelect.unitBox
//									.getSelectedValue();
//							if (unit == null) {
//								varSpec.unitSelect.getElement().getStyle()
//										.setColor("red");
//								completelyFilled = false;
//							} else {
//								varSpec.unitSelect.getElement().getStyle()
//										.clearColor();
//								varSpec.unit = unit;
//							}
//						}
//
//						// Execute
//						if (completelyFilled) {
//							prompt.disappear();
//							for (VariableEvaluateSpec varSpec : varSpecs) {
//
//								String value = String.valueOf(varSpec.value)
//										.replaceAll((String) "\\.0$", "");
//								String unit = varSpec.unit;
//
//								MathNode quantityPlugIn = varSpec.mathNode
//										.replace(TypeML.Number, value);
//								quantityPlugIn.setAttribute(MathAttribute.Unit,
//										unit);
//							}
//							Moderator.reloadEquationPanel(null, null);
//						}
//
//					}
//				});
//
//				prompt.appear();
//
//			}
//		});
//	}
//}

/**
 * Allows for substitution method with another equation into another variable of
 * the same quantity kind
 * 
 */
class SubstituteButton extends VariableTransformationButton {
	SubstituteButton(VariableTransformations context) {
		super(context);
		
		setHTML("Substitute");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				String quantityKind = variableNode.getUnitAttribute().getUnitMultiples()[0].getUnitName().getQuantityKind();
				if (quantityKind == null || quantityKind.equals("")) {
					Window.alert("No similar other equations with similar variables available");
					return;
				}
				final DialogBox dialog = new DialogBox(true, true);
				SelectionPanel eqSelect = new SelectionPanel(CSS.EQUATION);
				dialog.add(eqSelect);
				DataModerator
						.fill_EquationsByQuantities(quantityKind, eqSelect);
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
							JSNICalls
									.error("Could not find the element to substitute"
											+ " in: \n" + eqNode);
							dialog.removeFromParent();
							dialog.hide();
							return;
						}

						String subIntoEqStr = selected.getValue();
						Element subIntoEqEl = new HTML(subIntoEqStr)
								.getElement().getFirstChildElement();

						substitute(variableNode, subIntoEqEl, substitute);

						dialog.removeFromParent();
						dialog.hide();
					}
				});
				dialog.setGlassEnabled(true);
				dialog.setAnimationEnabled(true);
				dialog.center();
			}
		});
	}

	private void substitute(final EquationNode isolatedVar, Element subIntoEqEl,
			Element substitute) {

		// Find the variable to substutute
		NodeList<Element> possibleSubs = subIntoEqEl
				.getElementsByTagName(TypeEquationXML.Variable.getTag());
		findSub: for (int j = 0; j < possibleSubs.getLength(); j++) {
			Element possibleSub = possibleSubs.getItem(j);
			if (!isolatedVar.getUnitAttribute().equals(
					possibleSub.getAttribute(MathAttribute.Unit.getAttributeName()))) {
				continue findSub;
			}
			Element subParent = possibleSub.getParentElement();
			String plugTag = substitute.getTagName();
			if (plugTag.equals(subParent.getTagName())
					&& (plugTag.equals(TypeEquationXML.Sum.getTag()) || plugTag
							.equals(TypeEquationXML.Term.getTag()))) {
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
		super(context);
		
		setHTML("Plug In");
		
		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgebraActivity.NUMBER_SPEC_PROMPT(variableNode, true);
				
			}
		});
	}
}