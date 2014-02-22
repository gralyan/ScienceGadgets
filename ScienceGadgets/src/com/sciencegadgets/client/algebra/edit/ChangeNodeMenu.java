package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.CommunistPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.transformations.LogBaseSpecification;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.TrigFunctionSpecification;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class ChangeNodeMenu extends CommunistPanel {

	public static final String NOT_SET = "\u25A1";
	private TransformationButton removeButton;
	private MathNode node;
	private LogBaseSpecification logBaseSpec = null;
	private TrigFunctionSpecification trigFuncSpec = null;

	private static final Object[][] types = {//
	{ TypeML.Number, "#" }, //
			{ TypeML.Variable, "a" },//
			{ TypeML.Sum, NOT_SET + "+" + NOT_SET },//
			{ TypeML.Term, NOT_SET + Operator.DOT.getSign() + NOT_SET },//
			{ TypeML.Fraction, "<div style='border-bottom: thin solid;'>"//
					+ NOT_SET + "</div><div>" + NOT_SET + "</div>" },//
			{ TypeML.Exponential, NOT_SET + "<sup>" + NOT_SET + "</sup>" },//
			{ TypeML.Log, "log<sub>" + NOT_SET + "</sub>(" + NOT_SET + ")" },//
			{ TypeML.Trig, "sin(" + NOT_SET + ")" } //
	};

	public ChangeNodeMenu() {
		super(true);
		addStyleName(CSS.FILL_PARENT);

		TransformationList allButtons = new TransformationList(node);

		// Change buttons
		for (Object[] type : types) {
			TypeML toType = (TypeML) type[0];
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

	public void setNode(MathNode node) {
		this.node = node;
	}

	// //////////////////////////////////////////
	// Handle Remove
	// /////////////////////////////////////////
	private class RemoveNodeClick implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {

			MathNode parent = node.getParent();

			switch (parent.getType()) {
			case Term:
			case Sum:
				if (node.getIndex() == 0) {
					MathNode nextOp = node.getNextSibling();
					if (nextOp != null
							&& TypeML.Operation.equals(nextOp.getType())
							&& !Operator.MINUS.getSign().equals(
									nextOp.getSymbol()))
						nextOp.remove();
				} else {
					MathNode prevOp = node.getPrevSibling();
					if (TypeML.Operation.equals(prevOp.getType()))
						prevOp.remove();
				}
				node.remove();
				parent.decase();
				break;
			case Exponential:
			case Fraction:
				MathNode newParent = parent.getParent();

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
				node.replace(TypeML.Variable, NOT_SET);
				break;
			}
			Moderator.reloadEquationPanel(null, null);
		}
	}

	// //////////////////////////////////////////
	// Handle Change
	// /////////////////////////////////////////
	class ChangeNodeClick implements ClickHandler {
		TypeML toType;

		ChangeNodeClick(TypeML toType) {
			this.toType = toType;
		}

		@Override
		public void onClick(ClickEvent event) {

			MathNode parent = node.getParent();
			boolean isSameTypeNode = toType.equals(node.getType());
			boolean isSameTypeParent = toType.equals(node.getParentType());
			int nodeindex = node.getIndex();

			TypeML.Operator operator = null;

			switch (toType) {
			case Log:
				if (logBaseSpec == null) {
					logBaseSpec = new LogBaseSpecification() {
						@Override
						public void onSpecify(String base) {
							super.onSpecify(base);
							MathNode log = node.encase(TypeML.Log);
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
							MathNode func = node.encase(TypeML.Trig);
							func.setAttribute(MathAttribute.Function, function);
							Moderator.reloadEquationPanel(null, null);
						}
					};
				}
				trigFuncSpec.reload();
				return;
			case Number:
				boolean clearDisplays = true;
				if (isSameTypeNode && !NOT_SET.equals(node.getSymbol())) {
					clearDisplays = false;
				}
				if (AlgebraActivity.numSpec == null) {
					AlgebraActivity.numSpec = new NumberSpecification(node,
							clearDisplays);
				} else {
					AlgebraActivity.numSpec.reload(node, clearDisplays);
				}
				AlgebraActivity.numSpec.appear();
				return;
			case Variable:
				boolean clearDisplayz = true;
				if (isSameTypeNode && !NOT_SET.equals(node.getSymbol())) {
					clearDisplayz = false;
				}
				if (AlgebraActivity.varSpec == null) {
					AlgebraActivity.varSpec = new VariableSpecification(node,
							clearDisplayz);
				} else {
					AlgebraActivity.varSpec.reload(node, clearDisplayz);
				}
				AlgebraActivity.varSpec.appear();
				return;

			case Sum:
				operator = TypeML.Operator.PLUS;
				// fall through
			case Term:
				if (operator == null) {
					operator = TypeML.Operator.getMultiply();
				}
				if (isSameTypeNode) {
					// don't encase term in term just extend this term
					node.append(TypeML.Operation, operator.getSign());
					node.append(TypeML.Variable, NOT_SET);
					break;
				} else if (isSameTypeParent) {
					// don't add sum in sum just extend parent sum
					parent.addAfter(nodeindex, TypeML.Variable, NOT_SET);
					parent.addAfter(nodeindex, TypeML.Operation,
							operator.getSign());
					break;

				}// else encase in term
					// fall through
			case Exponential:
			case Fraction:

				// For Sum, Term, Exponential and Fraction
				// Encase in the new type

				MathNode newNode = parent.addBefore(nodeindex, toType, "");
				newNode.append(node);
				if (operator != null) {
					newNode.append(TypeML.Operation, operator.getSign());
				}
				newNode.append(TypeML.Variable, NOT_SET);
				break;
			}
			Moderator.reloadEquationPanel(null, null);

		}
	}
}
