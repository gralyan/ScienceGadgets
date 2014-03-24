package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;

public class BothSidesTransformations extends TransformationList {

	private static final long serialVersionUID = 1L;

	private EquationNode node;
	private EquationNode parentNode;
	private EquationTree tree;
	Focusable focusable = null;
	Widget responseNotes = null;
	boolean isTopLevel = false;
	boolean isNestedInFraction = false;
	boolean isSide = false;

	public static final String BOTH_SIDES = ChangeNodeMenu.NOT_SET;

	private static final String PLUS = TypeEquationXML.Operator.PLUS.getSign();
	private static final String MINUS = TypeEquationXML.Operator.MINUS
			.getSign();
	private static final String MULTIPLY = TypeEquationXML.Operator
			.getMultiply().getSign();
	private static final String DIVIDE = TypeEquationXML.Operator.DIVIDE
			.getSign();

	public BothSidesTransformations(EquationNode node) {
		super(node);

		TypeEquationXML type = node.getType();
		if (TypeEquationXML.Operation.equals(type)) {
			return;
		}

		this.node = node;
		tree = node.getTree();
		parentNode = node.getParent();

		if (parentNode.isLeftSide() || parentNode.isRightSide()) {
			switch (type) {
			case Sum:
			case Term:
			case Exponential:
			case Fraction:
			case Variable:
			case Number:
			case Log:
			case Trig:
				isTopLevel = true;
			}
		}

		parent: switch (parentNode.getType()) {
		case Sum:
			if (isTopLevel) {
				if (node.getIndex() > 0) {
					String opNode = node.getPrevSibling().getSymbol();
					if (MINUS.equals(opNode)) {
						this.add(new BothSidesButton(Math.ADD, this));
					} else if (PLUS.equals(opNode)) {
						this.add(new BothSidesButton(Math.SUBTRACT, this));
					} else {
						JSNICalls.warn("The opperator should be + or -");
					}
				} else {
					this.add(new BothSidesButton(Math.SUBTRACT, this));
				}
			}
			break parent;
		case Term:
			if (isTopLevel) {
				this.add(new BothSidesButton(Math.DIVIDE, this));
			}
			if (TypeEquationXML.Fraction.equals(parentNode.getParent()
					.getType())
					&& !TypeEquationXML.Operation.equals(node.getType())) {
				if (parentNode.getParent().isRightSide()
						|| parentNode.getParent().isLeftSide()) {

					isNestedInFraction = true;
					if (parentNode.getIndex() == 0) {
						this.add(new BothSidesButton(Math.DIVIDE, this));
					} else {
						this.add(new BothSidesButton(Math.MULTIPLY, this));
					}
				}
			}
			break parent;
		case Fraction:
			if (isTopLevel) {
				if (node.getIndex() == 0) {
					this.add(new BothSidesButton(Math.DIVIDE, this));
				} else {
					this.add(new BothSidesButton(Math.MULTIPLY, this));
				}
			}
			break parent;
		case Exponential:
			if (isTopLevel) {
				if (node.getIndex() == 1) {
					this.add(new BothSidesButton(Math.INVERSE_EXPONENT, this));
				} else if (node.getIndex() == 0
						&& TypeEquationXML.Number.equals(type)) {
					this.add(new BothSidesButton(Math.LOG, this));
				}
			}
			break parent;
		case Equation:
			if ("0".equals(node.getSymbol())) {
				break;
			}
			isSide = true;
			this.add(new BothSidesButton(Math.SUBTRACT, this));
			this.add(new BothSidesButton(Math.DIVIDE, this));
			eq: switch (type) {
			case Log:
				this.add(new BothSidesButton(Math.RAISE, this));
				break eq;
			case Trig:
				this.add(new BothSidesButton(Math.INVERSE_TRIG, this));
				break eq;
			}
			break parent;
		}

	}

	enum Math {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, INVERSE_EXPONENT, RAISE, LOG, INVERSE_TRIG
	}

	class BothSidesButton extends TransformationButton {

		BothSidesButton(Math operation, TransformationList context) {
			super(context);

			addStyleName(CSS.BOTH_SIDES_BUTTON);

			switch (operation) {
			case ADD:
				setHTML(BOTH_SIDES + PLUS + " "
						+ node.getHTMLString(true, true));
				addClickHandler(new AddOrSubBothHandler());
				break;
			case SUBTRACT:
				setHTML(BOTH_SIDES + MINUS + " "
						+ node.getHTMLString(true, true));
				addClickHandler(new AddOrSubBothHandler());
				break;
			case MULTIPLY:
				setHTML(BOTH_SIDES + MULTIPLY + " "
						+ node.getHTMLString(true, true));
				addClickHandler(new MultiplyBothHandler());
				break;
			case DIVIDE:
				setHTML(BOTH_SIDES + DIVIDE + " "
						+ node.getHTMLString(true, true));
				addClickHandler(new DivideBothHandler());
				break;
			case INVERSE_EXPONENT:
				setHTML(BOTH_SIDES + "<sup>1/" + node.getHTMLString(true, true)
						+ "</sup>");
				addClickHandler(new RootBothHandler());
				break;
			case RAISE:
				String base = node.getAttribute(MathAttribute.LogBase);
				setHTML(base + "<sup>" + BOTH_SIDES + "</sup>");
				addClickHandler(new RaiseBothHandler(base));
				break;
			case LOG:
				setHTML("log<sub>" + node.getHTMLString(true, true) + "</sub>"
						+ BOTH_SIDES);
				addClickHandler(new LogBothHandler());
				break;
			case INVERSE_TRIG:
				String func = node.getAttribute(MathAttribute.Function);
				String inverseFunc = TrigFunctions.getInverseName(func);
				setHTML(inverseFunc + "(" + BOTH_SIDES + ")");
				addClickHandler(new InverseTrigBothHandler(inverseFunc));
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
		protected EquationNode targetSide = null;
		// renamed for clarity
		protected EquationNode oldParent = parentNode;
		protected EquationNode oldNextSib;
		protected String changeComment;
		EquationNode topParent = null;

		BothSidesHandler() {

			if (isSide) {
				topParent = node;
			} else if (isTopLevel) {
				topParent = oldParent;
			} else if (isNestedInFraction) {
				topParent = oldParent.getParent();
			} else {
				JSNICalls.warn("Added bothSidesHandler to wrong node: " + node);
			}
			if (topParent.isLeftSide()) {
				isOnTopLeft = true;
				targetSide = tree.getRightSide();
			} else if (topParent.isRightSide()) {
				isOnTopRight = true;
				targetSide = tree.getLeftSide();
			} else {
				JSNICalls.warn("bothSidesHandler on wrong node: " + node
						+ "\nparent: " + topParent);
			}
		}

		@Override
		public void onClick(ClickEvent event) {
			changeComment = ((BothSidesButton) event.getSource()).getHTML();

			node.lineThrough();
		}
	}

	class AddOrSubBothHandler extends BothSidesHandler {
		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);
			// Prepare Target side
			targetSide = targetSide.encase(TypeEquationXML.Sum);

			// take operation
			EquationNode operator = node.getPrevSibling();
			if (node.getIndex() > 0 && !isSide) {
				if (!Moderator.isInEasyMode) {
					operator = operator.clone();
				}
				// Flip sign
				if (MINUS.equals(operator.getSymbol())) {
					operator.setSymbol(PLUS);
				} else if (PLUS.equals(operator.getSymbol())) {
					operator.setSymbol(MINUS);
				} else {
					JSNICalls.warn("Unknown operation, can't flip");
				}
				targetSide.append(operator);
			} else {
				targetSide.append(TypeEquationXML.Operation, MINUS);
			}

			if (Moderator.isInEasyMode) {
				// Leave 0 in old side if top node
				if (isSide) {
					node.getParent().addBefore(node.getIndex(),
							TypeEquationXML.Number, "0");
				}
				// move node to other side
				targetSide.append(node);

				// clean source side
				if (!isSide) {
					EquationNode oldFirstSib = oldParent.getFirstChild();
					if (oldFirstSib != null
							&& PLUS.equals(oldFirstSib.getSymbol())) {
						oldFirstSib.remove();
					}

					oldParent.decase();
				}
			} else {// create node on both sides
				targetSide.append(node.clone());
				topParent = topParent.encase(TypeEquationXML.Sum);
				if (operator != null) {
					topParent.append(operator.clone());
				} else {
					topParent.append(TypeEquationXML.Operation, MINUS);
				}
				topParent.append(node.clone());
			}

			Moderator.reloadEquationPanel(changeComment,
					Rule.SOLVING_ALGEBRAIC_EQUATIONS);
		}
	}

	class DivideBothHandler extends BothSidesHandler {
		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);

			EquationNode operator = null;

			if (Moderator.isInEasyMode) {
				if (TypeEquationXML.Fraction.equals(oldParent.getType())) {
					// leave 1 in numerator
					oldParent.addFirst(TypeEquationXML.Number, "1");
				} else if (isSide) {
					// Leave 1 in old side if top node
					oldParent.addBefore(node.getIndex(),
							TypeEquationXML.Number, "1");
				} else {
					// take operation
					if (node.getIndex() > 0) {
						operator = node.getPrevSibling();
					} else {
						operator = node.getNextSibling();
					}
				}
			}

			// Prepare Target side
			if (!TypeEquationXML.Fraction.equals(targetSide.getType())) {
				targetSide = targetSide.encase(TypeEquationXML.Fraction);
				if (operator != null)
					operator.remove();
			} else {
				targetSide = targetSide.getChildAt(1);
				targetSide = targetSide.encase(TypeEquationXML.Term);
				if (operator == null) {
					targetSide.append(TypeEquationXML.Operation,
							TypeEquationXML.Operator.getMultiply().getSign());
				} else {
					targetSide.append(operator);
				}
			}

			if (Moderator.isInEasyMode) {
				// move node to other side
				targetSide.append(node);

				// clean source side
				if(!isSide) {
				EquationNode oldFirstSib = oldParent.getFirstChild();
				if (oldFirstSib != null
						&& TypeEquationXML.Operation.equals(oldFirstSib
								.getType())) {
					oldFirstSib.remove();
				}

				oldParent.decase();
			}
			} else {// create node on other side
				targetSide.append(node.clone());
				if (TypeEquationXML.Fraction.equals(oldParent.getType())) {
					EquationNode denominator = oldParent.getChildAt(1).encase(
							TypeEquationXML.Term);
					denominator.append(TypeEquationXML.Operation, TypeEquationXML.Operator.getMultiply().getSign());
					denominator.append(node.clone());
				} else {
					topParent = topParent.encase(TypeEquationXML.Fraction);
					topParent.append(node.clone());
				}
			}

			Moderator.reloadEquationPanel(changeComment,
					Rule.SOLVING_ALGEBRAIC_EQUATIONS);
		}
	}

	class MultiplyBothHandler extends BothSidesHandler {

		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);

			// Prepare Target side
			targetSide = targetSide.encase(TypeEquationXML.Term);

			if (isNestedInFraction && Moderator.isInEasyMode) {
				EquationNode operation = null;
				if (node.getIndex() == 0) {
					operation = node.getNextSibling();
				} else {
					operation = node.getPrevSibling();
				}
				if (operation != null
						&& TypeEquationXML.Operation
								.equals(operation.getType())) {
					targetSide.append(operation);
				}
			} else if (isTopLevel || !Moderator.isInEasyMode) {
				targetSide.append(TypeEquationXML.Operation,
						TypeEquationXML.Operator.getMultiply().getSign());
			} else {
				JSNICalls
						.warn("Multiplying somethimg that's not top level or nested in a top level fraction: "
								+ node.toString());
			}

			if (!Moderator.isInEasyMode) {
				node = node.clone();
				oldParent = oldParent.encase(TypeEquationXML.Term);
				oldParent.append(TypeEquationXML.Operation, Operator
						.getMultiply().getSign());
				oldParent.append(node.clone());
			}

			if (TypeEquationXML.Term.equals(node.getType())) {// Termception
				for (EquationNode transplants : node.getChildren()) {
					targetSide.append(transplants);
				}
				node.remove();
			} else {
				targetSide.append(node);
			}

			if (Moderator.isInEasyMode) {
				// clean source side
				if (TypeEquationXML.Fraction.equals(oldParent.getType())) {
					// remove unnecessary intermediate fraction
					oldParent.replace(oldParent.getFirstChild());
				} else if (TypeEquationXML.Term.equals(oldParent.getType())) {
					oldParent.decase();
				} else {
					JSNICalls
							.warn("The parent of the divideBothSides must either be a term or fraction with index=0");
				}
			}

			Moderator.reloadEquationPanel(changeComment,
					Rule.SOLVING_ALGEBRAIC_EQUATIONS);
		}
	}

	/**
	 * node clicked: e<br/>
	 * b<sup>e</sup> = targetSide<br/>
	 * <br/>
	 * execute ROOT<br/>
	 * b<sup>e x 1/e</sup> = targetSide<sup>1/e</sup><br/>
	 * <br/>
	 * clean up<br/>
	 * b = targetSide<sup>1/e</sup></sup>
	 */
	class RootBothHandler extends BothSidesHandler {

		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);

			// Prepare Target side
			EquationNode target = null;
			if (!TypeEquationXML.Exponential.equals(targetSide.getType())) {
				targetSide = targetSide.encase(TypeEquationXML.Exponential);
				target = targetSide;
			} else {
				EquationNode targetExp = targetSide.getChildAt(1);
				targetExp = targetExp.encase(TypeEquationXML.Term);
				targetExp.append(TypeEquationXML.Operation, Operator
						.getMultiply().getSign());
				target = targetExp;
			}

			if (!Moderator.isInEasyMode) {
				node = node.clone();
			}

			if (TypeEquationXML.Fraction.equals(node.getType())) {
				EquationNode numerator = node.getChildAt(0);
				if (TypeEquationXML.Number.equals(numerator.getType())
						&& "1".equals(numerator.getSymbol())) {
					target.append(node.getChildAt(1));
					node.remove();
				} else {
					node.append(numerator);// flip
					target.append(node);
				}
			} else {
				EquationNode frac = target.append(TypeEquationXML.Fraction, "");
				frac.append(TypeEquationXML.Number, "1");
				frac.append(node);
			}

			if (Moderator.isInEasyMode) {
				// clean source side
				oldParent.replace(oldParent.getFirstChild());
			} else {
				oldParent.encase(TypeEquationXML.Exponential);
				EquationNode frac = oldParent.append(TypeEquationXML.Fraction,
						"");
				frac.append(TypeEquationXML.Number, "1");
				frac.append(node.clone());
			}

			Moderator.reloadEquationPanel(changeComment,
					Rule.SOLVING_ALGEBRAIC_EQUATIONS);
		}
	}

	/**
	 * node clicked: log<sub>b</sub>(x)<br/>
	 * log<sub>b</sub>(x) = targetSide<br/>
	 * <br/>
	 * execute RAISE<br/>
	 * b<sup>log<sub>b</sub>(x)</sup> = b<sup>targetSide</sup><br/>
	 * <br/>
	 * clean up<br/>
	 * x = b<sup>targetSide</sup>
	 */
	class RaiseBothHandler extends BothSidesHandler {

		String base;

		RaiseBothHandler(String base) {
			this.base = base;
		}

		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);
			// Prepare Target side

			EquationNode targetExp = targetSide
					.encase(TypeEquationXML.Exponential);
			targetExp.addFirst(TypeEquationXML.Number, base);

			if (Moderator.isInEasyMode) {
				// clean source side
				node.replace(node.getFirstChild());
			} else {
				EquationNode fromExp = node.encase(TypeEquationXML.Exponential);
				fromExp.addFirst(TypeEquationXML.Number, base);
			}

			Moderator.reloadEquationPanel(changeComment, Rule.LOGARITHM);
		}
	}

	/**
	 * node clicked: b<br/>
	 * b<sup>e</sup> = targetSide<br/>
	 * <br/>
	 * execute LOG<br/>
	 * log<sub>b</sub>(b<sup>e</sup>) = log<sub>b</sub>(targetSide)<br/>
	 * <br/>
	 * clean up<br/>
	 * e = log<sub>b</sub>(targetSide)</sup>
	 */
	class LogBothHandler extends BothSidesHandler {

		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);
			// Prepare Target side

			EquationNode targetLog = targetSide.encase(TypeEquationXML.Log);
			targetLog.setAttribute(MathAttribute.LogBase, node.getSymbol());

			if (Moderator.isInEasyMode) {
				// clean source side
				node.replace(node.getChildAt(1));
			} else {
				EquationNode fromLog = node.encase(TypeEquationXML.Log);
				fromLog.setAttribute(MathAttribute.LogBase, node.getSymbol());
			}

			Moderator.reloadEquationPanel(changeComment, Rule.LOGARITHM);
		}
	}

	/**
	 * node clicked: sin(x)<br/>
	 * sin(x) = targetSide<br/>
	 * <br/>
	 * execute INVERSE_TRIG<br/>
	 * arcsin(sin(x)) = arcsin(targetSide)<br/>
	 * <br/>
	 * clean up<br/>
	 * x = arcsin(targetSide)</sup>
	 */
	class InverseTrigBothHandler extends BothSidesHandler {
		String toFunction;

		InverseTrigBothHandler(String toFunction) {
			this.toFunction = toFunction;
		}

		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);
			// Prepare Target side

			EquationNode targetTrig = targetSide.encase(TypeEquationXML.Trig);
			targetTrig.setAttribute(MathAttribute.Function, toFunction);

			if (Moderator.isInEasyMode) {
				// clean source side
				node.replace(node.getFirstChild());
			} else {
				EquationNode fromTrig = node.encase(TypeEquationXML.Trig);
				fromTrig.setAttribute(MathAttribute.Function, toFunction);
			}

			Moderator.reloadEquationPanel(changeComment,
					Rule.INVERSE_TRIGONOMETRIC_FUNCTIONS);
		}
	}
}
