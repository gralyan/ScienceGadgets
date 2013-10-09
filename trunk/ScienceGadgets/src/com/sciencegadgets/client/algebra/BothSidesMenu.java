package com.sciencegadgets.client.algebra;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type.Operator;

public class BothSidesMenu extends FlowPanel {
	private MathWrapper mlWrapper;
	private MathNode node;
	private MathNode parentNode;
	private MathTree tree;
	Focusable focusable = null;
	Widget responseNotes = null;
	boolean isTopLevel = false;
	boolean isNestedInFraction = false;
	boolean isSide = false;

	private final String PLUS = Type.Operator.PLUS.getSign();
	private final String MINUS = Type.Operator.MINUS.getSign();
	private final String CROSS = Type.Operator.CROSS.getSign();
	private final String DOT = Type.Operator.DOT.getSign();
	private final String SPACE = Type.Operator.SPACE.getSign();

	public BothSidesMenu(MathWrapper mlWrapper, String width) {

		this.mlWrapper = mlWrapper;
		this.node = mlWrapper.getNode();
		tree = node.getTree();
		parentNode = node.getParent();

		if (Type.Operation.equals(node.getType())) {
			return;
		}

		// zIndex eqPanel=1 wrapper=2 menu=3
		this.getElement().getStyle().setZIndex(3);
		this.addStyleName("fillParent");

		if (parentNode.isLeftSide() || parentNode.isRightSide()) {
			switch (node.getType()) {
			case Sum:
			case Term:
			case Exponential:
			case Fraction:
			case Variable:
			case Number:
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
						JSNICalls.warn("The opperator should be + or -");
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
			if (isTopLevel && node.getIndex() == 1) {
				this.add(new BothSidesButton(Math.ROOT));
			}
			break;
		case Equation:
			isSide = true;
			BothSidesButton sub = new BothSidesButton(Math.SUBTRACT);
			sub.removeStyleName("bothSidesButton");
			sub.addStyleName("bothSidesButtonHalf");
			BothSidesButton div = new BothSidesButton(Math.DIVIDE);
			div.removeStyleName("bothSidesButton");
			div.addStyleName("bothSidesButtonHalf");
			this.add(sub);
			this.add(div);
			break;
		}

	}

	enum Math {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, ROOT
	}

	class BothSidesButton extends Button {

		BothSidesButton(Math operation) {
			this.addStyleName("bothSidesButton");
			switch (operation) {
			case ADD:
				setHTML("Add " + node.getHTMLString() + " to both sides");
				addClickHandler(new AddOrSubBothHandler());
				break;
			case SUBTRACT:
				setHTML("Subtract both sides by " + node.getHTMLString());
				addClickHandler(new AddOrSubBothHandler());
				break;
			case MULTIPLY:
				setHTML("Multiply both sides by " + node.getHTMLString());
				addClickHandler(new MultiplyBothHandler());
				break;
			case DIVIDE:
				setHTML("Divide both sides by " + node.getHTMLString());
				addClickHandler(new DivideBothHandler());
				break;
			case ROOT:
				setHTML("Root both sides by " + node.getHTMLString());
				addClickHandler(new RootBothHandler());
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
		protected MathNode targetSide = null;
		// renamed for clarity
		protected MathNode oldParent = parentNode;
		protected MathNode oldNextSib;
		protected String changeComment = "";

		BothSidesHandler() {

			MathNode topParent = null;
			if (isTopLevel) {
				topParent = oldParent;
			} else if (isNestedInFraction) {
				topParent = oldParent.getParent();
			} else if (isSide) {
				topParent = node;
			} else {
				JSNICalls.warn("Added bothSidesHandler to wrong node");
			}
			if (topParent.isLeftSide()) {
				isOnTopLeft = true;
				targetSide = tree.getRightSide();
			} else if (topParent.isRightSide()) {
				isOnTopRight = true;
				targetSide = tree.getLeftSide();
			} else {
				JSNICalls.warn("bothSidesHandler on wrong node");
			}
		}

		protected String doubleChangeComment() {
			return changeComment = changeComment
					+ "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + changeComment;
		}
	}

	class AddOrSubBothHandler extends BothSidesHandler {
		@Override
		public void onClick(ClickEvent event) {
			// Prepare Target side
				targetSide = targetSide.encase(Type.Sum);
				
			// Leave 0 in old side if top node
			if (isSide) {
				node.getParent().addBefore(node.getIndex(), Type.Number, "0");
			}
			// take operation
			if (node.getIndex() > 0 && !isSide) {
				MathNode operator = node.getPrevSibling();
				// Flip sign
				if (MINUS.equals(operator.getSymbol())) {
					operator.setSymbol(PLUS);
					changeComment += PLUS;
				} else if (PLUS.equals(operator.getSymbol())) {
					operator.setSymbol(MINUS);
					changeComment += MINUS;
				} else {
					JSNICalls.warn("Unknown operation, can't flip");
				}
				targetSide.append(operator);
			} else {
				targetSide.append(Type.Operation, MINUS);
				changeComment += MINUS;
			}

			// move node to other side
			targetSide.append(node);

			// clean source side
			MathNode oldFirstSib = oldParent.getFirstChild();
			if (oldFirstSib != null && PLUS.equals(oldFirstSib.getSymbol())) {
				oldFirstSib.remove();
			}

			oldParent.decase();

			changeComment += node.toString();
			Moderator.reloadEquationPanel(doubleChangeComment());
		}
	}

	class DivideBothHandler extends BothSidesHandler {
		@Override
		public void onClick(ClickEvent event) {
			MathNode operator = null;

			// Leave 1 in old side if top node
			if (isSide) {
				oldParent.addBefore(node.getIndex(), Type.Number, "1");
			} else {
				// take operation
				if (node.getIndex() > 0) {
					operator = node.getPrevSibling();
				}
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
					targetSide.append(Type.Operation, Type.Operator
							.getMultiply().getSign());
				} else {
					targetSide.append(operator);
				}
			}

			// move node to other side
			targetSide.append(node);

			// clean source side
			MathNode oldFirstSib = oldParent.getFirstChild();
			if (oldFirstSib != null) {
				String OldFirstSymbol = oldFirstSib.getSymbol();
				if (CROSS.equals(OldFirstSymbol) || DOT.equals(OldFirstSymbol)
						|| SPACE.equals(OldFirstSymbol))
					oldFirstSib.remove();
			}

			if (Type.Fraction.equals(oldParent.getType())) {
				// leave 1 in numerator
				oldParent.addBefore(0, Type.Number, "1");
			} else if (Type.Term.equals(oldParent.getType())) {
				if (oldParent.getChildCount() == 1) {
					// No need to be encased in term anymore
					oldParent.getParent().addBefore(oldParent.getIndex(),
							oldParent.getFirstChild());
					oldParent.remove();
				} else if (oldParent.getChildCount() == 2) {
					JSNICalls.warn("There shouldn't be two children in a term");
				}
			} else {
				JSNICalls
						.warn("The parent of the divideBothSides must either be a term or fraction with index=0");
			}

			changeComment += "/" + node.toString();
			Moderator.reloadEquationPanel(doubleChangeComment());
		}
	}

	class MultiplyBothHandler extends BothSidesHandler {

		@Override
		public void onClick(ClickEvent event) {
			// Prepare Target side
			targetSide = targetSide.encase(Type.Term);
			
			if (isNestedInFraction) {
				if (node.getIndex() == 0) {
					MathNode nextOp = node.getNextSibling();
					if (nextOp != null
							&& Type.Operation.equals(nextOp.getType())) {
						targetSide.append(nextOp);
					}
				} else {
					MathNode PrevOp = node.getPrevSibling();
					if (Type.Operation.equals(PrevOp.getType())) {
						targetSide.append(PrevOp);
					}
				}
			} else if (isTopLevel) {
				targetSide.append(Type.Operation, Type.Operator.getMultiply()
						.getSign());
			} else {
				JSNICalls
						.warn("Multiplying somethimg that's not top level or nested in a top level fraction: "
								+ node.toString());
			}
			if (Type.Term.equals(node.getType())) {// Termception
				for (MathNode transplants : node.getChildren()) {
					targetSide.append(transplants);
				}
				node.remove();
			} else {
				targetSide.append(node);
			}

			// clean source side
			if (Type.Fraction.equals(oldParent.getType())) {
				// remove unnecessary intermediate fraction
				oldParent.getParent().addBefore(oldParent.getIndex(),
						oldParent.getFirstChild());
				oldParent.remove();
			} else if (Type.Term.equals(oldParent.getType())) {
				if (oldParent.getChildCount() == 1) {
					// No need to be encased in term anymore
					oldParent.getParent().addBefore(oldParent.getIndex(),
							oldParent.getFirstChild());
					oldParent.remove();
				} else if (oldParent.getChildCount() == 2) {
					JSNICalls.warn("There shouldn't be two children in a term");
				}
			} else {
				JSNICalls
						.warn("The parent of the divideBothSides must either be a term or fraction with index=0");
			}

			changeComment += Type.Operator.getMultiply().getSign()
					+ node.toString();
			Moderator.reloadEquationPanel(doubleChangeComment());
		}
	}
	
	class RootBothHandler extends BothSidesHandler {
		
		@Override
		public void onClick(ClickEvent event) {
			// Prepare Target side
			
			if (!Type.Exponential.equals(targetSide.getType())) {
				targetSide = targetSide.encase(Type.Exponential);
				MathNode frac = targetSide.append(Type.Fraction, "");
				frac.append(Type.Number, "1");
				frac.append(node);
			}else{
				MathNode targetExp = targetSide.getChildAt(1);
				if(!Type.Term.equals(targetExp.getType())){
					targetExp = targetExp.encase(Type.Term);
				}
				targetExp.append(Type.Operation, Operator.getMultiply().getSign());
				MathNode frac = targetExp.append(Type.Fraction, "");
				frac.append(Type.Number, "1");
				frac.append(node);
			}
			
			// clean source side
			oldParent.getParent().addBefore(oldParent.getIndex(), oldParent.getFirstChild());
			oldParent.remove();
			
			changeComment += "\u221A"
					+ node.toString();
			Moderator.reloadEquationPanel(doubleChangeComment());
		}
	}
}
