package com.sciencegadgets.client.algebra;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.transformations.Rule;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class BothSidesMenu extends FlowPanel {
	private MathNode node;
	private MathNode parentNode;
	private MathTree tree;
	Focusable focusable = null;
	Widget responseNotes = null;
	boolean isTopLevel = false;
	boolean isNestedInFraction = false;
	boolean isSide = false;

	private final String PLUS = TypeML.Operator.PLUS.getSign();
	private final String MINUS = TypeML.Operator.MINUS.getSign();
	private final String CROSS = TypeML.Operator.CROSS.getSign();
	private final String DOT = TypeML.Operator.DOT.getSign();
	private final String SPACE = TypeML.Operator.SPACE.getSign();

	public BothSidesMenu(MathNode node, String width) {

		this.node = node;
		tree = node.getTree();
		parentNode = node.getParent();

		if (TypeML.Operation.equals(node.getType())) {
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
			if (TypeML.Fraction.equals(parentNode.getParent().getType())
					&& !TypeML.Operation.equals(node.getType())) {
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
			addMouseDownHandler(new AlternativeHTML(operation,
					node.getHTMLString()));
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

		@Override
		public void onClick(ClickEvent event) {
			node.highlight();
		}

		protected String doubleChangeComment() {
			return changeComment = changeComment + "&nbsp;&nbsp;&nbsp; both sides";

			// return changeComment = changeComment
			// + "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + changeComment;
		}
	}

	class AddOrSubBothHandler extends BothSidesHandler {
		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);
			// Prepare Target side
			targetSide = targetSide.encase(TypeML.Sum);

			// Leave 0 in old side if top node
			if (isSide) {
				node.getParent().addBefore(node.getIndex(), TypeML.Number, "0");
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
				targetSide.append(TypeML.Operation, MINUS);
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
			AlgebraActivity.reloadEquationPanel(doubleChangeComment(), Rule.Solving);
		}
	}

	class DivideBothHandler extends BothSidesHandler {
		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);
			MathNode operator = null;

			if (TypeML.Fraction.equals(oldParent.getType())) {
				// leave 1 in numerator
				oldParent.addBefore(0, TypeML.Number, "1");
			} else if (isSide) {
				// Leave 1 in old side if top node
				oldParent.addBefore(node.getIndex(), TypeML.Number, "1");
			} else {
				// take operation
				if (node.getIndex() > 0) {
					operator = node.getPrevSibling();
				} else {
					operator = node.getNextSibling();
				}
			}

			// Prepare Target side
			if (!TypeML.Fraction.equals(targetSide.getType())) {
				targetSide = targetSide.encase(TypeML.Fraction);
				if (operator != null)
					operator.remove();
			} else {
				targetSide = targetSide.getChildAt(1);
				if (!TypeML.Term.equals(targetSide.getType())) {
					targetSide = targetSide.encase(TypeML.Term);
				}
				if (operator == null) {
					targetSide.append(TypeML.Operation, TypeML.Operator
							.getMultiply().getSign());
				} else {
					targetSide.append(operator);
				}
			}

			// move node to other side
			targetSide.append(node);

			// clean source side
			MathNode oldFirstSib = oldParent.getFirstChild();
			if (oldFirstSib != null
					&& TypeML.Operation.equals(oldFirstSib.getType())) {
				oldFirstSib.remove();
			}

			oldParent.decase();

			changeComment += "\u00F7" + node.toString();
			AlgebraActivity.reloadEquationPanel(doubleChangeComment(), Rule.Solving);
		}
	}

	class MultiplyBothHandler extends BothSidesHandler {

		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);
			// Prepare Target side
			targetSide = targetSide.encase(TypeML.Term);

			if (isNestedInFraction) {
				MathNode operation = null;
				if (node.getIndex() == 0) {
					operation = node.getNextSibling();
				} else {
					operation = node.getPrevSibling();
				}
				if (operation != null
						&& TypeML.Operation.equals(operation.getType())) {
					targetSide.append(operation);
				}
			} else if (isTopLevel) {
				targetSide.append(TypeML.Operation, TypeML.Operator.getMultiply()
						.getSign());
			} else {
				JSNICalls
						.warn("Multiplying somethimg that's not top level or nested in a top level fraction: "
								+ node.toString());
			}

			if (TypeML.Term.equals(node.getType())) {// Termception
				for (MathNode transplants : node.getChildren()) {
					targetSide.append(transplants);
				}
				node.remove();
			} else {
				targetSide.append(node);
			}

			// clean source side
			if (TypeML.Fraction.equals(oldParent.getType())) {
				// remove unnecessary intermediate fraction
				oldParent.getParent().addBefore(oldParent.getIndex(),
						oldParent.getFirstChild());
				oldParent.remove();
			} else if (TypeML.Term.equals(oldParent.getType())) {
				oldParent.decase();
			} else {
				JSNICalls
						.warn("The parent of the divideBothSides must either be a term or fraction with index=0");
			}

			changeComment += TypeML.Operator.getMultiply().getSign()
					+ node.toString();
			AlgebraActivity.reloadEquationPanel(doubleChangeComment(), Rule.Solving);
		}
	}

	// TODO finish, implement, test
	class RootBothHandler extends BothSidesHandler {

		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);
			// Prepare Target side

			if (!TypeML.Exponential.equals(targetSide.getType())) {
				targetSide = targetSide.encase(TypeML.Exponential);
				MathNode frac = targetSide.append(TypeML.Fraction, "");
				frac.append(TypeML.Number, "1");
				frac.append(node);
			} else {
				MathNode targetExp = targetSide.getChildAt(1);
				if (!TypeML.Term.equals(targetExp.getType())) {
					targetExp = targetExp.encase(TypeML.Term);
				}
				targetExp.append(TypeML.Operation, Operator.getMultiply()
						.getSign());
				MathNode frac = targetExp.append(TypeML.Fraction, "");
				frac.append(TypeML.Number, "1");
				frac.append(node);
			}

			// clean source side
			oldParent.getParent().addBefore(oldParent.getIndex(),
					oldParent.getFirstChild());
			oldParent.remove();

			changeComment += "\u221A" + node.toString();
			AlgebraActivity.reloadEquationPanel(doubleChangeComment(), Rule.Solving);
		}
	}
}

class AlternativeHTML implements MouseDownHandler {
	com.sciencegadgets.client.algebra.BothSidesMenu.Math math = null;
	String html = null;

	public AlternativeHTML(
			com.sciencegadgets.client.algebra.BothSidesMenu.Math math,
			String htmlString) {
		this.math = math;
		this.html = htmlString;
	}

	@Override
	public void onMouseDown(MouseDownEvent event) {
		((Button) event.getSource())
				.setHTML(html + "&nbsp;&nbsp;&nbsp;" + html);
	}

}
