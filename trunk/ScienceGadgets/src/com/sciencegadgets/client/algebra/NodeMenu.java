package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.algebra.MathMLBindingTree.Operator;
import com.sciencegadgets.client.algebra.MathMLBindingTree.Type;

public class NodeMenu extends VerticalPanel {
	private MLElementWrapper mlWrapper;
	private MathMLBindingNode node;
	private MathMLBindingNode parentNode;
	private MathMLBindingTree tree;
	Focusable focusable = null;
	Widget responseNotes = null;
	boolean isTopLevel = false;
	boolean isNestedInFraction = false;

	private final String ADD_BOTH = "Add this to both sides";
	private final String SUB_BOTH = "Subtract both sides by this";
	private final String MULTIPLY_BOTH = "Multiply both sides by this";
	private final String DIVIDE_BOTH = "Divide Both sides by this";

	public NodeMenu(MLElementWrapper mlWrapper, String width) {

		this.mlWrapper = mlWrapper;
		this.node = mlWrapper.getNode();
		tree = node.getTree();
		parentNode = node.getParent();

		if (parentNode.isLeftSide() || parentNode.isRightSide()) {
			if (!Type.Operation.equals(node.getType())) {
				isTopLevel = true;
			}
		}

		switch (parentNode.getType()) {
		case Sum:
			if (isTopLevel) {
				if (node.getIndex() > 0) {
					Operator opNode = node.getPrevSibling().getOperation();
					if (Operator.MINUS.equals(opNode)) {
						this.add(new BothSidesButton(Math.ADD));
					} else if (Operator.PLUS.equals(opNode)) {
						this.add(new BothSidesButton(Math.SUBTRACT));
					} else {
						JSNICalls.consoleWarn("The opperator should be + or -");
					}
				} else {
					this.add(new BothSidesButton(Math.SUBTRACT));
				}
			}
			break;
		case Term:
			if (isTopLevel) {
				this.add(new BothSidesButton(Math.DIVIDE));
			}
			if (Type.Fraction.equals(parentNode.getParent().getType())
					&& !Type.Operation.equals(node.getType())) {
				if (parentNode.getParent().isRightSide()
						|| parentNode.getParent().isLeftSide()) {

					isNestedInFraction = true;
					if (node.getIndex() == 0) {
						this.add(new BothSidesButton(Math.DIVIDE));
					} else {
						this.add(new BothSidesButton(Math.MULTIPLY));
					}
				}
			}
			break;
		case Fraction:
			if (isTopLevel) {
				if (node.getIndex() == 0) {
					this.add(new BothSidesButton(Math.DIVIDE));
				} else {
					this.add(new BothSidesButton(Math.MULTIPLY));
				}
			}
			break;
		case Exponential:
			break;
		case Equation:
			break;
		}

	}

	enum Math {
		ADD, SUBTRACT, MULTIPLY, DIVIDE
	}

	class BothSidesButton extends Button {

		BothSidesButton(Math operation) {
			switch (operation) {
			case ADD:
				setText("+");
				setTitle("Add " + node.getSymbol() + " to both sides");
				addClickHandler(new AddOrSubBothHandler());
				break;
			case SUBTRACT:
				setText("-");
				setTitle("Subtract both sides by " + node.getSymbol());
				addClickHandler(new AddOrSubBothHandler());
				break;
			case MULTIPLY:
				setText("x");
				setTitle("Multiply both sides by " + node.getSymbol());
				addClickHandler(new MultiplyBothHandler());
				break;
			case DIVIDE:
				setText("/");
				setTitle("Divide Both sides by " + node.getSymbol());
				addClickHandler(new DivideBothHandler());
				break;

			}
		}
	}

	abstract class BothSidesHandler implements ClickHandler {

		protected boolean isOnTopLeft;
		protected boolean isOnTopRight;
		protected MathMLBindingNode targetSide = null;
		// renamed for clarity
		protected MathMLBindingNode oldParent = parentNode;

		BothSidesHandler() {

			MathMLBindingNode topParent = null;
			if (isTopLevel) {
				topParent = oldParent;
			} else if (isNestedInFraction) {
				topParent = oldParent.getParent();
			} else {
				JSNICalls.consoleWarn("Added bothSidesHandler to wrong node");
			}
			if (topParent.isLeftSide()) {
				isOnTopLeft = true;
				targetSide = tree.getRightSide();
			} else if (topParent.isRightSide()) {
				isOnTopRight = true;
				targetSide = tree.getLeftSide();
			} else {
				JSNICalls.consoleWarn("Added bothSidesHandler to wrong node");
			}
		}
	}

	class AddOrSubBothHandler extends BothSidesHandler {
		@Override
		public void onClick(ClickEvent event) {
			// Prepare Target side
			if (!Type.Sum.equals(targetSide.getType())) {
				targetSide = targetSide.encase(Type.Sum);
			}
			// take operation
			if (node.getIndex() > 0) {
				MathMLBindingNode operator = node.getPrevSibling();
				// Flip sign
				if (Operator.MINUS.equals(operator.getOperation())) {
					operator.setSymbol("+");
				} else if (Operator.PLUS.equals(operator.getOperation())) {
					operator.setSymbol("-");
				} else {
					JSNICalls.consoleWarn("Unknown operation, can't flip");
				}
				targetSide.add(-1, operator);
			} else {
				targetSide.add(-1, Type.Operation, "-");
			}

			targetSide.add(-1, node);

			// clean source side
			// TODO
			if (oldParent.getChildCount() > 3) {
				LinkedList<MathMLBindingNode> oldSibs = oldParent.getChildren();
				for (int i = 0; i < oldSibs.size(); i++) {
					MathMLBindingNode oldSib = oldSibs.get(i);
					JSNICalls.consoleDebug(oldSib.getSymbol());
					oldParent.getParent().add(oldParent.getIndex() + i, oldSib);
					oldParent.remove();
				}
			}

			Moderator.reloadEquationPanel("");
		}
	}

	class DivideBothHandler extends BothSidesHandler {
		@Override
		public void onClick(ClickEvent event) {
			// take operation
			MathMLBindingNode operator = null;
			if (node.getIndex() > 0) {
				operator = node.getPrevSibling();
			}
			// Prepare Target side
			if (!Type.Fraction.equals(targetSide.getType())) {
				targetSide = targetSide.encase(Type.Fraction);
			} else {
				targetSide = targetSide.getChildAt(1);
				if (!Type.Term.equals(targetSide.getType())) {
					targetSide = targetSide.encase(Type.Term);
				}
				if (operator == null) {
					targetSide.add(-1, Type.Operation, Operator.DOT.getSign());
				} else {
					targetSide.add(-1, operator);
				}
			}

			targetSide.add(-1, node);
			// clean source side
			// TODO
			JSNICalls.consoleDebug("oldSide children: "
					+ oldParent.getChildCount());
			if (Type.Fraction.equals(oldParent.getType())) {
				oldParent.add(0, Type.Number, "1");
			}

			Moderator.reloadEquationPanel("");
		}
	}

	class MultiplyBothHandler extends BothSidesHandler {

		@Override
		public void onClick(ClickEvent event) {
			// Prepare Target side
			if (!Type.Term.equals(targetSide.getType())) {
				targetSide = targetSide.encase(Type.Term);
			}
			if (!isNestedInFraction) {
				targetSide.add(-1, Type.Operation, Operator.DOT.getSign());
			} else {
				targetSide.add(-1, node.getPrevSibling());
			}
			targetSide.add(-1, node);

			// clean source side
			// TODO
			JSNICalls.consoleDebug("oldSide children: "
					+ oldParent.getChildCount());

			Moderator.reloadEquationPanel("");
		}
	}
}
