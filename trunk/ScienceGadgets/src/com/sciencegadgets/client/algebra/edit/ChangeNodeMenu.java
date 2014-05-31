package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.dom.client.Style;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.EquationWrapper;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.specification.LogBaseSpecification;
import com.sciencegadgets.client.algebra.transformations.specification.TrigFunctionSpecification;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class ChangeNodeMenu extends CommunistPanel {

	public static final String NOT_SET = "\u25A1";
	private TransformationButton removeButton;
	private EquationNode node;
	private LogBaseSpecification logBaseSpec = null;
	private TrigFunctionSpecification trigFuncSpec = null;
	AlgebraActivity algebraActivity = null;
	TransformationList<TransformationButton> changeButtons;

	private static final Object[][] types = {//
			{ TypeSGET.Number, "#" }, //
			{ TypeSGET.Variable, "a" },//
			{ TypeSGET.Sum, NOT_SET + "+" + NOT_SET },//
			{ TypeSGET.Term, NOT_SET + Operator.DOT.getSign() + NOT_SET },//
			{ TypeSGET.Fraction,
					"<div style='border-bottom: thin solid;'>"//
							+ NOT_SET + "</div><div>" + NOT_SET + "</div>" },//
			{ TypeSGET.Exponential,
					NOT_SET + "<sup>" + NOT_SET + "</sup>" },//
			{ TypeSGET.Log,
					"log<sub>" + NOT_SET + "</sub>(" + NOT_SET + ")" },//
			{ TypeSGET.Trig, "sin(" + NOT_SET + ")" } //
	};

	public ChangeNodeMenu() {
		super(true);
		addStyleName(CSS.FILL_PARENT);

		changeButtons = new TransformationList<TransformationButton>(node);
		if(node !=null) {
		algebraActivity = ((EquationWrapper)node.getWrapper()).getAlgebraActivity();
		}

		// Change buttons
		for (Object[] type : types) {
			TypeSGET toType = (TypeSGET) type[0];
			TransformationButton changeButton = new ChangeNodeButton(
					(String) type[1], changeButtons, toType);
			changeButton.addStyleName(CSS.CHANGE_NODE_BUTTON + " "
					+ toType.toString() + " " + CSS.DISPLAY_WRAPPER);
			changeButtons.add(changeButton);
		}

		// Remove button
		this.removeButton = new RemoveNodeButton(changeButtons);
		changeButtons.add(removeButton);

		addAll(changeButtons);

	}

	public void setNode(EquationNode node) {
		this.node = node;

		algebraActivity = ((EquationWrapper)node.getWrapper()).getAlgebraActivity();
		changeButtons.setNode(node);

		TypeSGET type = node.getType();
		if (!TypeSGET.Number.equals(type)
				&& !TypeSGET.Variable.equals(type)) {
			getWidget(0).removeFromParent();
			getWidget(0).removeFromParent();
			redistribute();
		}
	}

	// //////////////////////////////////////////
	// Handle Remove
	// /////////////////////////////////////////
	private class RemoveNodeButton extends TransformationButton {
		RemoveNodeButton(TransformationList<TransformationButton> changeButtons) {
			super("Remove", changeButtons);
			Style style = getElement().getStyle();
			style.setColor("red");
			style.setBackgroundColor("black");
		}
		@Override
		public boolean meetsAutoTransform() {
			return true;
		}

		public void transform() {
			EquationNode parent = node.getParent();

			switch (parent.getType()) {
			case Term:
			case Sum:
				if (node.getIndex() == 0) {
					EquationNode nextOp = node.getNextSibling();
					if (nextOp != null
							&& TypeSGET.Operation.equals(nextOp
									.getType())
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
				node.replace(TypeSGET.Variable, NOT_SET);
				break;
			}
			algebraActivity.reloadEquationPanel(null, null, false);
//			Moderator.reloadEquationPanel();
		}
	}

	// //////////////////////////////////////////
	// Handle Change
	// /////////////////////////////////////////
	class ChangeNodeButton extends TransformationButton {
		TypeSGET toType;

		ChangeNodeButton(String html,
				TransformationList<TransformationButton> changeButtons,
				TypeSGET toType) {
			super(html, changeButtons);
			this.toType = toType;
		}
		@Override
		public boolean meetsAutoTransform() {
			return true;
		}

		@Override
		public void transform() {

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
							algebraActivity.reloadEquationPanel(null, null, false);
//							Moderator.reloadEquationPanel();
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
							EquationNode func = node
									.encase(TypeSGET.Trig);
							func.setAttribute(MathAttribute.Function, function);
							algebraActivity.reloadEquationPanel(null, null, false);
//							Moderator.reloadEquationPanel();
						}
					};
				}
				trigFuncSpec.reload();
				return;
			case Number:
				AlgebraActivity.NUMBER_SPEC_PROMPT(node,
						!(isSameTypeNode && !NOT_SET.equals(node.getSymbol())), false);
				return;
			case Variable:
				AlgebraActivity.VARIABLE_SPEC_PROMPT(node,
						!(isSameTypeNode && !NOT_SET.equals(node.getSymbol())));
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
					node.append(TypeSGET.Variable, NOT_SET);
					break;
				} else if (isSameTypeParent) {
					// don't add sum in sum just extend parent sum
					parent.addAfter(nodeindex, TypeSGET.Variable,
							NOT_SET);
					parent.addAfter(nodeindex, TypeSGET.Operation,
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
					newNode.append(TypeSGET.Operation,
							operator.getSign());
				}
				newNode.append(TypeSGET.Variable, NOT_SET);
				break;
			}
			algebraActivity.reloadEquationPanel(null, null, false);
//			Moderator.reloadEquationPanel();

		}

	}
}
