package com.sciencegadgets.client.algebra.edit;

import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.objectify.annotation.OnSave;
import com.sciencegadgets.client.CommunistPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
import com.sciencegadgets.shared.TypeML.TrigFunctions;
import com.sciencegadgets.client.algebra.transformations.LogBaseSpecification;
import com.sciencegadgets.client.algebra.transformations.TrigFunctionSpecification;

public class ChangeNodeMenu extends CommunistPanel {

	public static final String NOT_SET = "\u25A1";
	private Button removeButton;
	private MathNode node;
	private LogBaseSpecification logBaseSpec = null;
	private TrigFunctionSpecification trigFuncSpec = null;

	private static final Object[][] types = {//
	{ TypeML.Number, "#" }, //
			{ TypeML.Variable, "x" },//
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
		addStyleName("fillParent");

		LinkedList<Widget> allButtons = new LinkedList<Widget>();

		// Change buttons
		for (Object[] type : types) {
			TypeML toType = (TypeML) type[0];
			Button changeButton = new Button((String) type[1]);
			if (Moderator.isTouch) {
				changeButton.addTouchEndHandler(new ChangeNodeTouch(toType));
			} else {
				changeButton.addClickHandler(new ChangeNodeClick(toType));
			}
			changeButton.addStyleName("changeNodeButton");
			allButtons.add(changeButton);
		}

		// Remove button
		this.removeButton = new Button("Remove");
		if (Moderator.isTouch) {
			removeButton.addTouchStartHandler(new RemoveNodeTouch());
		} else {
			removeButton.addClickHandler(new RemoveNodeClick());
		}

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
	private void removeNode() {

		MathNode parent = node.getParent();

		switch (parent.getType()) {
		case Term:
		case Sum:
			if (node.getIndex() == 0) {
				MathNode nextOp = node.getNextSibling();
				if (nextOp != null && TypeML.Operation.equals(nextOp.getType())
						&& !Operator.MINUS.getSign().equals(nextOp.getSymbol()))
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
				newParent.addBefore(parent.getIndex(), node.getNextSibling());
				break;
			case 1:
				newParent.addBefore(parent.getIndex(), node.getPrevSibling());
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

	// //////////////////////////////////////////
	// Handle Change
	// /////////////////////////////////////////
	private void changeNode(TypeML toType) {

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
			if (!isSameTypeNode || NOT_SET.equals(node.getSymbol())) {
				node = node.replace(toType, NOT_SET);
			}
			if (AlgebraActivity.numSpec == null) {
				AlgebraActivity.numSpec = new NumberSpecification(node);
			} else {
				AlgebraActivity.numSpec.reload(node);
			}
			AlgebraActivity.numSpec.appear();
			break;
		case Variable:
			if (!isSameTypeNode || NOT_SET.equals(node.getSymbol())) {
				node = node.replace(toType, NOT_SET);
			}
			if (AlgebraActivity.varSpec == null) {
				AlgebraActivity.varSpec = new VariableSpecification(node);
			} else {
				AlgebraActivity.varSpec.reload(node);
			}
			AlgebraActivity.varSpec.appear();
			break;

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
				parent.addAfter(nodeindex, TypeML.Operation, operator.getSign());
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

	// //////////////////////////////////////////////////////
	// Click and Touch handler classes
	// /////////////////////////////////////////////

	private class RemoveNodeTouch implements TouchStartHandler {
		@Override
		public void onTouchStart(TouchStartEvent event) {
			removeNode();
		}
	}

	private class RemoveNodeClick implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			removeNode();
		}
	}

	private class ChangeNodeClick implements ClickHandler {
		TypeML toType;

		ChangeNodeClick(TypeML toType) {
			this.toType = toType;
		}

		@Override
		public void onClick(ClickEvent event) {
			changeNode(toType);
		}
	}

	private class ChangeNodeTouch implements TouchEndHandler {
		TypeML toType;

		ChangeNodeTouch(TypeML toType) {
			this.toType = toType;
		}

		@Override
		public void onTouchEnd(TouchEndEvent arg0) {
			changeNode(toType);
		}
	}

}
