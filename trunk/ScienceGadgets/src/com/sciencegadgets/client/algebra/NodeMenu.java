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

	private final String PLUS = Operator.PLUS.getSign();
	private final String MINUS = Operator.MINUS.getSign();
	private final String CROSS = Operator.CROSS.getSign();
	private final String DOT = Operator.DOT.getSign();
	private final String SPACE = Operator.SPACE.getSign();

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
					String opNode = node.getPrevSibling().getSymbol();
					if (MINUS.equals(opNode)) {
						this.add(new BothSidesButton(Math.ADD));
					} else if (PLUS.equals(opNode)) {
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
					if (parentNode.getIndex() == 0) {
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
				setText(PLUS);
				setTitle("Add " + node.getSymbol() + " to both sides");
				addClickHandler(new AddOrSubBothHandler());
				break;
			case SUBTRACT:
				setText(MINUS);
				setTitle("Subtract both sides by " + node.getSymbol());
				addClickHandler(new AddOrSubBothHandler());
				break;
			case MULTIPLY:
				setText(CROSS);
				setTitle("Multiply both sides by " + node.getSymbol());
				addClickHandler(new MultiplyBothHandler());
				break;
			case DIVIDE:
				setText("divide");
				setTitle("Divide both sides by " + node.getSymbol());
				addClickHandler(new DivideBothHandler());
				break;

			}
		}
	}

	// ////////////////////////////////////////////////////////
	// Both Sides Handlers
	// ///////////////////////////////////////////////////////

	abstract class BothSidesHandler implements ClickHandler {

		protected boolean isOnTopLeft;
		protected boolean isOnTopRight;
		protected MathMLBindingNode targetSide = null;
		// renamed for clarity
		protected MathMLBindingNode oldParent = parentNode;
		protected MathMLBindingNode oldNextSib;

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
				if (MINUS.equals(operator.getSymbol())) {
					operator.setSymbol(PLUS);
				} else if (PLUS.equals(operator.getSymbol())) {
					operator.setSymbol(MINUS);
				} else {
					JSNICalls.consoleWarn("Unknown operation, can't flip");
				}
				targetSide.add(-1, operator);
			} else {
				targetSide.add(-1, Type.Operation, "-");
			}

			// move node to other side
			targetSide.add(-1, node);

			// clean source side
			MathMLBindingNode oldFirstSib = oldParent.getFirstChild();
			if (oldFirstSib != null && PLUS.equals(oldFirstSib.getSymbol())) {
				oldFirstSib.remove();
			}

			switch (oldParent.getChildCount()) {
			case 0:
				JSNICalls
						.consoleWarn("There shouldn't be zero children in a sum");
				oldParent.remove();
				break;
			case 2:
				if (MINUS.equals(oldParent.getFirstChild().getSymbol())) {
					// Merge
					MathMLBindingNode secondChild = oldParent.getChildAt(1);
					String secondChildSymbol = secondChild.getSymbol();
					if (secondChildSymbol.startsWith(MINUS)) {
						secondChild.setSymbol(secondChildSymbol.replaceFirst(
								MINUS, ""));
					} else {
						secondChild.setSymbol(MINUS + secondChildSymbol);
					}
					oldParent.getFirstChild().remove();
				} else {
					JSNICalls
							.consoleWarn("There Shouldn't be two children in a sum: "
									+ oldParent.getMLNode().getInnerHTML());
				}
				// no break, go on to case 1
			case 1:
				// No need to be encased in sum anymore
				oldParent.getParent().add(oldParent.getIndex(),
						oldParent.getFirstChild());
				oldParent.remove();
				break;
			}

			tree.validateTree();
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
				if (operator != null)
					operator.remove();
			} else {
				targetSide = targetSide.getChildAt(1);
				if (!Type.Term.equals(targetSide.getType())) {
					targetSide = targetSide.encase(Type.Term);
				}
				if (operator == null) {
					targetSide.add(-1, Type.Operation, DOT);
				} else {
					targetSide.add(-1, operator);
				}
			}

			// move node to other side
			targetSide.add(-1, node);

			// clean source side
			MathMLBindingNode oldFirstSib = oldParent.getFirstChild();
			if (oldFirstSib != null) {
				String OldFirstSymbol = oldFirstSib.getSymbol();
				if (CROSS.equals(OldFirstSymbol) || DOT.equals(OldFirstSymbol)
						|| SPACE.equals(OldFirstSymbol))
					oldFirstSib.remove();
			}

			if (Type.Fraction.equals(oldParent.getType())) {
				// leave 1 in numerator
				oldParent.add(0, Type.Number, "1");
			} else if (Type.Term.equals(oldParent.getType())) {
				if (oldParent.getChildCount() == 1) {
					// No need to be encased in term anymore
					oldParent.getParent().add(oldParent.getIndex(),
							oldParent.getFirstChild());
					oldParent.remove();
				} else if (oldParent.getChildCount() == 2) {
					JSNICalls
							.consoleWarn("There shouldn't be two children in a term");
				}
			} else {
				JSNICalls
						.consoleWarn("The parent of the divideBothSides must either be a term or fraction with index=0");
			}

			tree.validateTree();
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
			if (isNestedInFraction) {
				if (node.getIndex() > 0) {
					targetSide.add(-1, node.getPrevSibling());
				} else {
					targetSide.add(-1, node.getNextSibling());
				}
			} else if (isTopLevel) {
				targetSide.add(-1, Type.Operation, DOT);
			} else {
				JSNICalls
						.consoleWarn("Multiplying somethimg that's not top level or nested in a top level fraction: "
								+ node.toString());
			}
			if(Type.Term.equals(node.getType())){//Termception
				for(MathMLBindingNode transplants : node.getChildren()){
					targetSide.add(-1, transplants);
				}
				node.remove();
			}else{
				targetSide.add(-1, node);
			}
			// clean source side

			if (Type.Fraction.equals(oldParent.getType())) {
				// remove unnecessary intermediate fraction
				oldParent.getParent().add(oldParent.getIndex(),
						oldParent.getFirstChild());
				oldParent.remove();
			} else if (Type.Term.equals(oldParent.getType())) {
				if (oldParent.getChildCount() == 1) {
					// No need to be encased in term anymore
					oldParent.getParent().add(oldParent.getIndex(),
							oldParent.getFirstChild());
					oldParent.remove();
				} else if (oldParent.getChildCount() == 2) {
					JSNICalls
							.consoleWarn("There shouldn't be two children in a term");
				}
			} else {
				JSNICalls
						.consoleWarn("The parent of the divideBothSides must either be a term or fraction with index=0");
			}

			tree.validateTree();
			Moderator.reloadEquationPanel("");
		}
	}
}
