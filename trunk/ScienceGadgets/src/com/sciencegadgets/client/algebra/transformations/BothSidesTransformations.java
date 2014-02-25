package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class BothSidesTransformations extends TransformationList {

	private static final long serialVersionUID = 1L;

	private MathNode node;
	private MathNode parentNode;
	private MathTree tree;
	Focusable focusable = null;
	Widget responseNotes = null;
	boolean isTopLevel = false;
	boolean isNestedInFraction = false;
	boolean isSide = false;

	public static final String BOTH_SIDES = ChangeNodeMenu.NOT_SET;

	private static final String PLUS = TypeML.Operator.PLUS.getSign();
	private static final String MINUS = TypeML.Operator.MINUS.getSign();
	private static final String MULTIPLY = TypeML.Operator.getMultiply()
			.getSign();
	private static final String DIVIDE = TypeML.Operator.DIVIDE.getSign();

	public BothSidesTransformations(MathNode node) {
		super(node);
		
		TypeML type = node.getType();
		if (TypeML.Operation.equals(type)) {
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
			if (TypeML.Fraction.equals(parentNode.getParent().getType())
					&& !TypeML.Operation.equals(node.getType())) {
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
				} else if (node.getIndex() == 0 && TypeML.Number.equals(type)) {
					this.add(new BothSidesButton(Math.LOG, this));
				}
			}
			break parent;
		case Equation:
			if("0".equals(node.getSymbol())) {
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
				setHTML(BOTH_SIDES + PLUS + " " + node.getHTMLString());
				addClickHandler(new AddOrSubBothHandler());
				break;
			case SUBTRACT:
				setHTML(BOTH_SIDES + MINUS + " " + node.getHTMLString());
				addClickHandler(new AddOrSubBothHandler());
				break;
			case MULTIPLY:
				setHTML(BOTH_SIDES + MULTIPLY + " " + node.getHTMLString());
				addClickHandler(new MultiplyBothHandler());
				break;
			case DIVIDE:
				setHTML(BOTH_SIDES + DIVIDE + " " + node.getHTMLString());
				addClickHandler(new DivideBothHandler());
				break;
			case INVERSE_EXPONENT:
				setHTML(BOTH_SIDES + "<sup>1/" + node.getHTMLString()
						+ "</sup>");
				addClickHandler(new RootBothHandler());
				break;
			case RAISE:
				String base = node.getAttribute(MathAttribute.LogBase);
				setHTML(base + "<sup>" + BOTH_SIDES + "</sup>");
				addClickHandler(new RaiseBothHandler(base));
				break;
			case LOG:
				setHTML("log<sub>" + node.getHTMLString() + "</sub>"
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
		protected MathNode targetSide = null;
		// renamed for clarity
		protected MathNode oldParent = parentNode;
		protected MathNode oldNextSib;
		protected String changeComment;

		BothSidesHandler() {

			MathNode topParent = null;
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
			targetSide = targetSide.encase(TypeML.Sum);

			// take operation
			MathNode operator = node.getPrevSibling();
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
				targetSide.append(TypeML.Operation, MINUS);
			}

			if (Moderator.isInEasyMode) {
				// Leave 0 in old side if top node
				if (isSide) {
					node.getParent().addBefore(node.getIndex(), TypeML.Number,
							"0");
				}
				// move node to other side
				targetSide.append(node);

				// clean source side
				MathNode oldFirstSib = oldParent.getFirstChild();
				if (oldFirstSib != null && PLUS.equals(oldFirstSib.getSymbol())) {
					oldFirstSib.remove();
				}

				oldParent.decase();

			} else {
				// create node on both sides
				targetSide.append(node.clone());
				oldParent.encase(TypeML.Sum);
				if(operator!=null) {
					oldParent.append(operator.clone());
				}else {
					oldParent.append(TypeML.Operation, MINUS);
				}
				oldParent.append(node.clone());
			}

			Moderator.reloadEquationPanel(changeComment,
					Rule.SOLVING_ALGEBRAIC_EQUATIONS);
		}
	}

	class DivideBothHandler extends BothSidesHandler {
		@Override
		public void onClick(ClickEvent event) {
			super.onClick(event);

			MathNode operator = null;
			
			if (Moderator.isInEasyMode) {
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
			}

			// Prepare Target side
			if (!TypeML.Fraction.equals(targetSide.getType())) {
				targetSide = targetSide.encase(TypeML.Fraction);
				if (operator != null)
					operator.remove();
			} else {
				targetSide = targetSide.getChildAt(1);
				targetSide = targetSide.encase(TypeML.Term);
				if (operator == null) {
					targetSide.append(TypeML.Operation, TypeML.Operator
							.getMultiply().getSign());
				} else {
					targetSide.append(operator);
				}
			}

			if (Moderator.isInEasyMode) {
				// move node to other side
				targetSide.append(node);

				// clean source side
				MathNode oldFirstSib = oldParent.getFirstChild();
				if (oldFirstSib != null
						&& TypeML.Operation.equals(oldFirstSib.getType())) {
					oldFirstSib.remove();
				}

				oldParent.decase();

			} else {
				// create node on other side
				targetSide.append(node.clone());
				if(TypeML.Fraction.equals(oldParent.getType())) {
					MathNode denominator = oldParent.getChildAt(1).encase(TypeML.Term);
					denominator.append(node.clone());
				}else {
					oldParent= oldParent.encase(TypeML.Fraction);
					oldParent.append(node.clone());
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
			targetSide = targetSide.encase(TypeML.Term);

			if (isNestedInFraction && Moderator.isInEasyMode) {
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
			} else if (isTopLevel || !Moderator.isInEasyMode) {
				targetSide.append(TypeML.Operation, TypeML.Operator
						.getMultiply().getSign());
			} else {
				JSNICalls
						.warn("Multiplying somethimg that's not top level or nested in a top level fraction: "
								+ node.toString());
			}

			if (!Moderator.isInEasyMode) {
				node = node.clone();
				oldParent=oldParent.encase(TypeML.Term);
				oldParent.append(TypeML.Operation, Operator.getMultiply().getSign());
				oldParent.append(node.clone());
			}

			if (TypeML.Term.equals(node.getType())) {// Termception
				for (MathNode transplants : node.getChildren()) {
					targetSide.append(transplants);
				}
				node.remove();
			} else {
				targetSide.append(node);
			}

			if (Moderator.isInEasyMode) {
				// clean source side
				if (TypeML.Fraction.equals(oldParent.getType())) {
					// remove unnecessary intermediate fraction
					oldParent.replace(oldParent.getFirstChild());
				} else if (TypeML.Term.equals(oldParent.getType())) {
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
			MathNode target = null;
			if (!TypeML.Exponential.equals(targetSide.getType())) {
				targetSide = targetSide.encase(TypeML.Exponential);
				target = targetSide;
			} else {
				MathNode targetExp = targetSide.getChildAt(1);
				targetExp = targetExp.encase(TypeML.Term);
				targetExp.append(TypeML.Operation, Operator.getMultiply()
						.getSign());
				target = targetExp;
			}

			if (!Moderator.isInEasyMode) {
				node = node.clone();
			}

			if (TypeML.Fraction.equals(node.getType())) {
				MathNode numerator = node.getChildAt(0);
				if (TypeML.Number.equals(numerator.getType())
						&& "1".equals(numerator.getSymbol())) {
					target.append(node.getChildAt(1));
					node.remove();
				} else {
					node.append(numerator);// flip
					target.append(node);
				}
			} else {
				MathNode frac = target.append(TypeML.Fraction, "");
				frac.append(TypeML.Number, "1");
				frac.append(node);
			}

			if (Moderator.isInEasyMode) {
				// clean source side
				oldParent.replace(oldParent.getFirstChild());
			}else {
				oldParent.encase(TypeML.Exponential);
				MathNode frac = oldParent.append(TypeML.Fraction, "");
				frac.append(TypeML.Number, "1");
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

			MathNode targetExp = targetSide.encase(TypeML.Exponential);
			targetExp.addBefore(0, TypeML.Number, base);

			if (Moderator.isInEasyMode) {
				// clean source side
				node.replace(node.getFirstChild());
			}else {
				MathNode fromExp = node.encase(TypeML.Exponential);
				fromExp.addBefore(0, TypeML.Number, base);
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

			MathNode targetLog = targetSide.encase(TypeML.Log);
			targetLog.setAttribute(MathAttribute.LogBase, node.getSymbol());

			if (Moderator.isInEasyMode) {
				// clean source side
				node.replace(node.getChildAt(1));
			}else {
				MathNode fromLog = node.encase(TypeML.Log);
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

			MathNode targetTrig = targetSide.encase(TypeML.Trig);
			targetTrig.setAttribute(MathAttribute.Function, toFunction);

			if (Moderator.isInEasyMode) {
				// clean source side
				node.replace(node.getFirstChild());
			}else {
				MathNode fromTrig = node.encase(TypeML.Trig);
				fromTrig.setAttribute(MathAttribute.Function, toFunction);
			}

			Moderator.reloadEquationPanel(changeComment,
					Rule.INVERSE_TRIGONOMETRIC_FUNCTIONS);
		}
	}
}
