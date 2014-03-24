package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.specification.LogBaseSpecification;
import com.sciencegadgets.client.algebra.transformations.specification.TrigFunctionSpecification;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;

public class ChangeNodeMenu extends CommunistPanel {

	public static final String NOT_SET = "\u25A1";
	private TransformationButton removeButton;
	private EquationNode node;
	private LogBaseSpecification logBaseSpec = null;
	private TrigFunctionSpecification trigFuncSpec = null;

	private static final Object[][] types = {//
	{ TypeEquationXML.Number, "#" }, //
			{ TypeEquationXML.Variable, "a" },//
			{ TypeEquationXML.Sum, NOT_SET + "+" + NOT_SET },//
			{ TypeEquationXML.Term, NOT_SET + Operator.DOT.getSign() + NOT_SET },//
			{ TypeEquationXML.Fraction, "<div style='border-bottom: thin solid;'>"//
					+ NOT_SET + "</div><div>" + NOT_SET + "</div>" },//
			{ TypeEquationXML.Exponential, NOT_SET + "<sup>" + NOT_SET + "</sup>" },//
			{ TypeEquationXML.Log, "log<sub>" + NOT_SET + "</sub>(" + NOT_SET + ")" },//
			{ TypeEquationXML.Trig, "sin(" + NOT_SET + ")" } //
	};

	public ChangeNodeMenu() {
		super(true);
		addStyleName(CSS.FILL_PARENT);

		TransformationList allButtons = new TransformationList(node);

		// Change buttons
		for (Object[] type : types) {
			TypeEquationXML toType = (TypeEquationXML) type[0];
			TransformationButton changeButton = new TransformationButton(
					(String) type[1], allButtons);
			changeButton.addStyleName(CSS.CHANGE_NODE_BUTTON + " "
					+ toType.toString() + " " + CSS.DISPLAY_WRAPPER);
			changeButton.addClickHandler(new ChangeNodeClick(toType));
			allButtons.add(changeButton);
		}

		// Remove button
		this.removeButton = new TransformationButton("Remove", allButtons);
		removeButton.addClickHandler(new RemoveNodeClick());

		removeButton.getElement().getStyle().setColor("red");
		allButtons.add(removeButton);

		addAll(allButtons);

	}

	public void setNode(EquationNode node) {
		this.node = node;

		TypeEquationXML type = node.getType();
		if (!TypeEquationXML.Number.equals(type) && !TypeEquationXML.Variable.equals(type)) {
			getWidget(0).removeFromParent();
			getWidget(0).removeFromParent();
			redistribute();
		}
	}

	// //////////////////////////////////////////
	// Handle Remove
	// /////////////////////////////////////////
	private class RemoveNodeClick implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {

			EquationNode parent = node.getParent();

			switch (parent.getType()) {
			case Term:
			case Sum:
				if (node.getIndex() == 0) {
					EquationNode nextOp = node.getNextSibling();
					if (nextOp != null
							&& TypeEquationXML.Operation.equals(nextOp.getType())
							&& !Operator.MINUS.getSign().equals(
									nextOp.getSymbol()))
						nextOp.remove();
				} else {
					EquationNode prevOp = node.getPrevSibling();
					if (TypeEquationXML.Operation.equals(prevOp.getType()))
						prevOp.remove();
				}
				node.remove();
				parent.decase();
				break;
			case Exponential:
			case Fraction:
				EquationNode newParent = parent.getParent();

				switch (node.getIndex()) {
				case 0:
					newParent.addBefore(parent.getIndex(),
							node.getNextSibling());
					break;
				case 1:
					newParent.addBefore(parent.getIndex(),
							node.getPrevSibling());
					break;
				}
				parent.remove();
				break;
			// Shouldn't remove these, set them to a default node
			case Equation:
			case Trig:
			case Log:
				node.replace(TypeEquationXML.Variable, NOT_SET);
				break;
			}
			Moderator.reloadEquationPanel(null, null);
		}
	}

	// //////////////////////////////////////////
	// Handle Change
	// /////////////////////////////////////////
	class ChangeNodeClick implements ClickHandler {
		TypeEquationXML toType;

		ChangeNodeClick(TypeEquationXML toType) {
			this.toType = toType;
		}

		@Override
		public void onClick(ClickEvent event) {

			EquationNode parent = node.getParent();
			boolean isSameTypeNode = toType.equals(node.getType());
			boolean isSameTypeParent = toType.equals(node.getParentType());
			int nodeindex = node.getIndex();

			TypeEquationXML.Operator operator = null;

			switch (toType) {
			case Log:
				if (logBaseSpec == null) {
					logBaseSpec = new LogBaseSpecification() {
						@Override
						public void onSpecify(String base) {
							super.onSpecify(base);
							EquationNode log = node.encase(TypeEquationXML.Log);
							log.setAttribute(MathAttribute.LogBase, base);
							Moderator.reloadEquationPanel(null, null);
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
							EquationNode func = node.encase(TypeEquationXML.Trig);
							func.setAttribute(MathAttribute.Function, function);
							Moderator.reloadEquationPanel(null, null);
						}
					};
				}
				trigFuncSpec.reload();
				return;
			case Number:
				AlgebraActivity.NUMBER_SPEC_PROMPT(node, !(isSameTypeNode && !NOT_SET.equals(node.getSymbol())));
				return;
			case Variable:
				AlgebraActivity.VARIABLE_SPEC_PROMPT(node, !(isSameTypeNode && !NOT_SET.equals(node.getSymbol())));
				return;

			case Sum:
				operator = TypeEquationXML.Operator.PLUS;
				// fall through
			case Term:
				if (operator == null) {
					operator = TypeEquationXML.Operator.getMultiply();
				}
				if (isSameTypeNode) {
					// don't encase term in term just extend this term
					node.append(TypeEquationXML.Operation, operator.getSign());
					node.append(TypeEquationXML.Variable, NOT_SET);
					break;
				} else if (isSameTypeParent) {
					// don't add sum in sum just extend parent sum
					parent.addAfter(nodeindex, TypeEquationXML.Variable, NOT_SET);
					parent.addAfter(nodeindex, TypeEquationXML.Operation,
							operator.getSign());
					break;

				}// else encase in term
					// fall through
			case Exponential:
			case Fraction:

				// For Sum, Term, Exponential and Fraction
				// Encase in the new type

				EquationNode newNode = parent.addBefore(nodeindex, toType, "");
				newNode.append(node);
				if (operator != null) {
					newNode.append(TypeEquationXML.Operation, operator.getSign());
				}
				newNode.append(TypeEquationXML.Variable, NOT_SET);
				break;
			}
			Moderator.reloadEquationPanel(null, null);

		}
	}
}
