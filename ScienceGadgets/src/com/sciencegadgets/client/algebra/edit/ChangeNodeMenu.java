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
package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.EquationWrapper;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.specification.LogBaseSpecification;
import com.sciencegadgets.client.algebra.transformations.specification.TrigFunctionSpecification;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.client.ui.FitParentHTML;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class ChangeNodeMenu extends CommunistPanel {

	private TransformationButton removeButton;
	private CopyNodeButton copyButton;
	private PasteNodeButton pasteButton;
	private EquationNode node;
	private LogBaseSpecification logBaseSpec = null;
	private TrigFunctionSpecification trigFuncSpec = null;
	AlgebraActivity algebraActivity = null;
	TransformationList<TransformationButton> changeButtons;

	public static FitParentHTML copiedNodeHTML = new FitParentHTML("Paste");
	private static Element copiedNodeXML = null;

	// private static final Object[][] ExpressionTypes = {//
	// { TypeSGET.Number, "#" }, //
	// { TypeSGET.Variable, "a" },//
	// { TypeSGET.Sum, TypeSGET.NOT_SET + "+" + TypeSGET.NOT_SET },//
	// { TypeSGET.Term, TypeSGET.NOT_SET + Operator.DOT.getSign() +
	// TypeSGET.NOT_SET },//
	// { TypeSGET.Fraction,
	// "<div style='border-bottom: thin solid;'>"//
	// + TypeSGET.NOT_SET + "</div><div>" + TypeSGET.NOT_SET + "</div>" },//
	// { TypeSGET.Exponential,
	// TypeSGET.NOT_SET + "<sup>" + TypeSGET.NOT_SET + "</sup>" },//
	// { TypeSGET.Log,
	// "log<sub>" + TypeSGET.NOT_SET + "</sub>(" + TypeSGET.NOT_SET + ")" },//
	// { TypeSGET.Trig, "sin(" + TypeSGET.NOT_SET + ")" } //
	// };

	public ChangeNodeMenu() {
		super(true);
		addStyleName(CSS.FILL_PARENT);

		changeButtons = new TransformationList<TransformationButton>(node);
		if (node != null) {
			algebraActivity = ((EquationWrapper) node.getWrapper())
					.getAlgebraActivity();
		}

		TypeSGET[] typesDisplayed = { 
				TypeSGET.Number, 
				TypeSGET.Variable,
				TypeSGET.Sum, 
				TypeSGET.Term,
				TypeSGET.Fraction,
				TypeSGET.Exponential,
				TypeSGET.Log,
				TypeSGET.Trig };
		// Change buttons
		for (TypeSGET type : typesDisplayed) {
			// TypeSGET toType = (TypeSGET) type[0];
			TypeSGET toType = type;
			ChangeNodeButton changeButton = new ChangeNodeButton(
					type.getIcon(), changeButtons, toType);
			changeButtons.add(changeButton);
		}

		// Remove button
		this.removeButton = new RemoveNodeButton(changeButtons);
		changeButtons.add(removeButton);

		// Copy button
		this.copyButton = new CopyNodeButton(changeButtons);
		changeButtons.add(copyButton);

		// Paste button
		this.pasteButton = new PasteNodeButton(changeButtons);
		changeButtons.add(pasteButton);

		addAll(changeButtons);

	}

	public void setNode(EquationNode node) {
		this.node = node;

		algebraActivity = ((EquationWrapper) node.getWrapper())
				.getAlgebraActivity();
		changeButtons.setNode(node);

		TypeSGET type = node.getType();
		if (!TypeSGET.Number.equals(type) && !TypeSGET.Variable.equals(type)) {
//			getWidget(0).removeFromParent();
//			getWidget(0).removeFromParent();
			((ChangeNodeButton)getWidget(0)).toDummy();
			((ChangeNodeButton)getWidget(1)).toDummy();
			redistribute();
		}
	}

	public void updatePaste() {
		pasteButton.clear();
		pasteButton.add(copiedNodeHTML);
	}

	// //////////////////////////////////////////
	// Copy Handle
	// /////////////////////////////////////////
	private class CopyNodeButton extends TransformationButton {

		CopyNodeButton(TransformationList<TransformationButton> changeButtons) {
			super("Copy", changeButtons);
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
			copiedNodeXML = node.getXMLClone();

			String html = node.getHTMLString(true, true);
			copiedNodeHTML = new FitParentHTML(html);
			updatePaste();
		}
	}

	// //////////////////////////////////////////
	// Paste Handle
	// /////////////////////////////////////////
	private class PasteNodeButton extends TransformationButton {
		PasteNodeButton(TransformationList<TransformationButton> changeButtons) {
			super("Paste", changeButtons);
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
			Element replacementEl = (Element) copiedNodeXML.cloneNode(true);
			EquationNode replacement = node.getTree().newNode(replacementEl);
			node.replace(replacement);

			algebraActivity.reloadEquationPanel(null, null, true, replacement.getId());
		}
	}

	// //////////////////////////////////////////
	// Remove Handle
	// /////////////////////////////////////////
	private class RemoveNodeButton extends TransformationButton {
		RemoveNodeButton(TransformationList<TransformationButton> changeButtons) {
			super("Remove", changeButtons);
			Style style = getElement().getStyle();
			style.setColor("red");
			style.setBackgroundColor("black");
		}

		@Override
		public Badge getAssociatedBadge() {
			return null;
		}

		@Override
		public boolean meetsAutoTransform() {
			return true;
		}

		public void transform() {
			String nodeidToAutoSelect=null;
			EquationNode parent = node.getParent();

			switch (parent.getType()) {
			case Term:
			case Sum:
				if (node.getIndex() == 0) {
					EquationNode nextOp = node.getNextSibling();
					if (nextOp != null
							&& TypeSGET.Operation.equals(nextOp.getType())
							&& !Operator.MINUS.getSign().equals(
									nextOp.getSymbol()))
						nextOp.remove();
				} else {
					EquationNode prevOp = node.getPrevSibling();
					if (TypeSGET.Operation.equals(prevOp.getType()))
						prevOp.remove();
				}
				node.remove();
				parent.decase();
				
				if(parent != null && parent.getFirstChild() != null) {
					JSNICalls.error(parent.toString());
					nodeidToAutoSelect = parent.getFirstChild().getId();
				}else if(!TypeSGET.Equation.equals(parent.getParentType())){
					nodeidToAutoSelect = parent.getParent().getId();
				}
				break;
			case Exponential:
			case Fraction:

				switch (node.getIndex()) {
				case 0:
					EquationNode replacement = node.replace(TypeSGET.Variable, TypeSGET.NOT_SET);
					nodeidToAutoSelect = replacement.getId();
					break;
				case 1:
					EquationNode siblingStaying = node.getPrevSibling();
					parent.getParent().addBefore(parent.getIndex(),
							siblingStaying);
					parent.remove();
					nodeidToAutoSelect = siblingStaying.getId();
					break;
				}
				break;
			// Shouldn't remove these, set them to a default node
			case Equation:
			case Trig:
			case Log:
				EquationNode repl = node.replace(TypeSGET.Variable, TypeSGET.NOT_SET);
				nodeidToAutoSelect = repl.getId();
				break;
			}
			algebraActivity.reloadEquationPanel(null, null, true, nodeidToAutoSelect);
			// Moderator.reloadEquationPanel();
		}
	}

	// //////////////////////////////////////////
	// Change Handle
	// /////////////////////////////////////////
	class ChangeNodeButton extends TransformationButton {
		TypeSGET toType;
		private boolean isDummy = false;

		ChangeNodeButton(String html,
				TransformationList<TransformationButton> changeButtons,
				TypeSGET toType) {
			super(html, changeButtons);
			this.toType = toType;
			
			addStyleName(CSS.CHANGE_NODE_BUTTON + " "
					+ toType.toString() + " " + CSS.PARENT_WRAPPER);
		}
		
		private void toDummy(){
			this.isDummy = true;
			setHTML("");
			getElement().getStyle().setOpacity(0.5);
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
			if(isDummy) {
				return;
			}

			String nodeIdToAutoSelect = null;
			EquationNode parent = node.getParent();
			boolean isSameTypeNode = toType.equals(node.getType());
			boolean isSameTypeParent = toType.equals(node.getParentType());
			int nodeindex = node.getIndex();

			TypeSGET.Operator operator = null;

			switch (toType) {
			case Log:
				if (logBaseSpec == null) {
					logBaseSpec = new LogBaseSpecification() {
						@Override
						public void onSpecify(String base) {
							super.onSpecify(base);
							EquationNode log = node.encase(TypeSGET.Log);
							log.setAttribute(MathAttribute.LogBase, base);
							algebraActivity.reloadEquationPanel(null, null,
									true, log.getId());
							// Moderator.reloadEquationPanel();
						}
					};
				}
				logBaseSpec.reload();
				return;
			case Trig:
				if (trigFuncSpec == null) {
					trigFuncSpec = new TrigFunctionSpecification() {
						@Override
						protected void onSpecify(String function) {
							super.onSpecify(function);
							EquationNode func = node.encase(TypeSGET.Trig);
							func.setAttribute(MathAttribute.Function, function);
							algebraActivity.reloadEquationPanel(null, null,
									true, func.getId());
							// Moderator.reloadEquationPanel();
						}
					};
				}
				trigFuncSpec.reload();
				return;
			case Number:
				AlgebraActivity.NUMBER_SPEC_PROMPT(node,
						!(isSameTypeNode && !TypeSGET.NOT_SET.equals(node
								.getSymbol())), false);
				return;
			case Variable:
				AlgebraActivity.VARIABLE_SPEC_PROMPT(node,
						!(isSameTypeNode && !TypeSGET.NOT_SET.equals(node
								.getSymbol())));
				return;

			case Sum:
				operator = TypeSGET.Operator.PLUS;
				// fall through
			case Term:
				if (operator == null) {
					operator = TypeSGET.Operator.getMultiply();
				}
				if (isSameTypeNode) {
					// don't encase term in term just extend this term
					node.append(TypeSGET.Operation, operator.getSign());
					EquationNode newNode = node.append(TypeSGET.Variable, TypeSGET.NOT_SET);
					nodeIdToAutoSelect = newNode.getId();
					break;
				} else if (isSameTypeParent) {
					// don't add sum in sum just extend parent sum
					EquationNode newNode = parent.addAfter(nodeindex, TypeSGET.Variable,
							TypeSGET.NOT_SET);
					parent.addAfter(nodeindex, TypeSGET.Operation,
							operator.getSign());
					nodeIdToAutoSelect = newNode.getId();
					break;

				}// else encase in term
					// fall through
			case Exponential:
			case Fraction:

				// For Sum, Term, Exponential and Fraction
				// Encase in the new type

				EquationNode newParent = parent.addBefore(nodeindex, toType, "");
				newParent.append(node);
				if (operator != null) {
					newParent.append(TypeSGET.Operation, operator.getSign());
				}
				EquationNode newSibling = newParent.append(TypeSGET.Variable, TypeSGET.NOT_SET);
				nodeIdToAutoSelect = newSibling.getId();
				break;
			}
			algebraActivity.reloadEquationPanel(null, null, true, nodeIdToAutoSelect);

		}

	}

}
