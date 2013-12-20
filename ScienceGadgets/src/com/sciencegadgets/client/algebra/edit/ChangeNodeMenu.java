package com.sciencegadgets.client.algebra.edit;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.CommunistPanel;
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

	public ChangeNodeMenu(MathNode node) {
		super(true);
		this.node = node;
		addStyleName("fillParent");

		LinkedList<Widget> allButtons = new LinkedList<Widget>();

		// Change buttons
		for (Object[] type : types) {
			Button changeButton = new Button((String) type[1],
					new ChangeNodeHandler((TypeML) type[0]));
			changeButton.addStyleName("changeNodeButton");
			allButtons.add(changeButton);
		}

		// Remove button
		this.removeButton = new Button("Remove", new RemoveNodeHandler());
		removeButton.getElement().getStyle().setColor("red");
		allButtons.add(removeButton);

		addAll(allButtons);

	}

	private class RemoveNodeHandler implements ClickHandler {

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
				AlgebraActivity.reloadEquationPanel(null, null);
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
				AlgebraActivity.reloadEquationPanel(null, null);
				break;
			case Equation:
				node.replace(TypeML.Number, "1");
			}
		}
	}

	private class ChangeNodeHandler implements ClickHandler {
		TypeML toType;

		ChangeNodeHandler(TypeML toType) {
			this.toType = toType;
		}

		@Override
		public void onClick(ClickEvent event) {

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
					Prompt numPrompt = new NumberSpecification(node);
					numPrompt.appear();
					break;
				case Variable:
					if (!isSameTypeNode
							|| NOT_SET.equals(node.getSymbol())) {
						node = node.replace(toType, NOT_SET);
					}
					Prompt varPrompt = new VariableSpecification(node);
					varPrompt.appear();
					break;
				case Sum:
					if (isSameTypeNode) {
						// don't encase sum in sum just extend this sum
						node.append(TypeML.Operation, "+");
						node.append(TypeML.Variable, NOT_SET);
						break;
					} else if (isSameTypeParent) {
						// don't add sum in sum just extend parent sum
						parent.addAfter(nodeindex, TypeML.Variable,
								NOT_SET);
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
						node.append(TypeML.Operation, TypeML.Operator
								.getMultiply().getSign());
						node.append(TypeML.Variable, NOT_SET);
						break;
					} else if (isSameTypeParent) {
						// don't add sum in sum just extend parent sum
						parent.addAfter(nodeindex, TypeML.Variable,
								NOT_SET);
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
}
