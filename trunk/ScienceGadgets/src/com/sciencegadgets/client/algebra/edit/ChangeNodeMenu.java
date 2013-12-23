package com.sciencegadgets.client.algebra.edit;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.CommunistPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class ChangeNodeMenu extends CommunistPanel {

	public static final String NOT_SET = "\u25A1";
	public static final String REFERENCE = "\u2191";
	private Button removeButton;
	private MathNode node;

	private static final Object[][] types = {//
			{ TypeML.Number, "#" }, //
			{ TypeML.Variable, "x" },//
			{ TypeML.Sum, REFERENCE + "+" + NOT_SET },//
			{ TypeML.Term, REFERENCE + Operator.DOT.getSign() + NOT_SET },//
			{
					TypeML.Fraction,
					"<div style='border-bottom: thin solid;'>" + REFERENCE
							+ "</div><div>" + NOT_SET + "</div>" },//
			{ TypeML.Exponential, REFERENCE + "<sup>" + NOT_SET + "</sup>" } };

	public ChangeNodeMenu() {
		super(true);
		addStyleName("fillParent");

		LinkedList<Widget> allButtons = new LinkedList<Widget>();

		// Change buttons
		for (Object[] type : types) {
			TypeML toType = (TypeML) type[0];
			Button changeButton = new Button((String) type[1]);
			if (Moderator.isTouch) {
				changeButton.addTouchStartHandler(new ChangeNodeTouch(toType));
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

	private void removeNode() {

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
		case Equation:
			node.replace(TypeML.Number, "1");
			break;
		}
		AlgebraActivity.reloadEquationPanel(null, null);
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

	private class ChangeNodeTouch implements TouchStartHandler {
		TypeML toType;

		ChangeNodeTouch(TypeML toType) {
			this.toType = toType;
		}

		@Override
		public void onTouchStart(TouchStartEvent event) {
			changeNode(toType);
		}
	}

	private void changeNode(TypeML toType) {

		MathNode parent = node.getParent();
		boolean isSameTypeNode = toType.equals(node.getType());
		boolean isSameTypeParent = toType.equals(node.getParentType());
		int nodeindex = node.getIndex();
		try {
			TypeML.Operator operator = null;

			switch (toType) {
			case Number:
				if (!isSameTypeNode) {
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
				if (isSameTypeNode) {
					// don't encase sum in sum just extend this sum
					node.append(TypeML.Operation, "+");
					node.append(TypeML.Variable, NOT_SET);
					break;
				} else if (isSameTypeParent) {
					// don't add sum in sum just extend parent sum
					parent.addAfter(nodeindex, TypeML.Variable, NOT_SET);
					parent.addAfter(nodeindex, TypeML.Operation, "+");
					break;
				} else {
					// encase in sum
					operator = TypeML.Operator.PLUS;
					// fall through
				}
			case Term:
				if (isSameTypeNode) {
					// don't encase term in term just extend this term
					node.append(TypeML.Operation, TypeML.Operator.getMultiply()
							.getSign());
					node.append(TypeML.Variable, NOT_SET);
					break;
				} else if (isSameTypeParent) {
					// don't add sum in sum just extend parent sum
					parent.addAfter(nodeindex, TypeML.Variable, NOT_SET);
					parent.addAfter(nodeindex, TypeML.Operation,
							TypeML.Operator.getMultiply().getSign());
					break;
				} else {
					// encase in term
					if (operator == null) {
						operator = TypeML.Operator.getMultiply();
					}
					// fall through
				}
			case Exponential:
				// fall through
			case Fraction:

				// For Sum, Term, Exponential and Fraction

				MathNode newNode = parent.addBefore(nodeindex, toType, "");
				newNode.append(node);
				if (operator != null) {
					newNode.append(TypeML.Operation, operator.getSign());
				}
				newNode.append(TypeML.Variable, NOT_SET);
				break;
			}
			AlgebraActivity.reloadEquationPanel(null, null);

		} catch (NoSuchElementException e) {
			e.printStackTrace();
		}
	}

}
