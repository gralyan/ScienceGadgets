/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.WrapDragController;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.Math;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class BothSidesTransformations extends
		TransformationList<BothSidesButton> {

	private static final long serialVersionUID = 9132644612826212329L;

	private EquationNode node;
	private EquationNode oldParent;
	private EquationTree tree;
	Focusable focusable = null;
	Widget responseNotes = null;
	boolean isTopLevel = false;
	boolean isNestedInFraction = false;
	boolean isSide = false;

	public static final String UP_ARROW = "\u2191";
	public static final String UP_ARROW_HTML_START ="<div style=\"display:inline-block;vertical-align:middle;\">";//+ UP_ARROW;
	public static final String UP_ARROW_HTML_END = "</div>";

	private static final String PLUS = TypeSGET.Operator.PLUS.getSign();
	private static final String MINUS = TypeSGET.Operator.MINUS.getSign();
	private static final String MULTIPLY = TypeSGET.Operator.getMultiply()
			.getSign();
	private static final String DIVIDE = TypeSGET.Operator.DIVIDE.getSign();

	public BothSidesTransformations(EquationNode node) {
		super(node);

		TypeSGET type = node.getType();
		if (TypeSGET.Operation.equals(type)) {
			return;
		}

		this.node = node;
		tree = node.getTree();
		oldParent = node.getParent();

		if (oldParent.isLeftSide() || oldParent.isRightSide()) {
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

		parent: switch (oldParent.getType()) {
		case Sum:
			if (isTopLevel) {
				if (node.getIndex() > 0) {
					String opNode = node.getPrevSibling().getSymbol();
					if (MINUS.equals(opNode)) {
						this.add(Math.ADD);
					} else if (PLUS.equals(opNode)) {
						this.add(Math.SUBTRACT);
					} else {
						JSNICalls.warn("The opperator should be + or -");
					}
				} else {
					this.add(Math.SUBTRACT);
				}
			}
			break parent;
		case Term:
			if (isTopLevel) {
				this.add(Math.DIVIDE);
			}
			if (TypeSGET.Fraction.equals(oldParent.getParent().getType())
					&& !TypeSGET.Operation.equals(node.getType())) {
				if (oldParent.getParent().isRightSide()
						|| oldParent.getParent().isLeftSide()) {

					isNestedInFraction = true;
					if (oldParent.getIndex() == 0) {
						this.add(Math.DIVIDE);
					} else {
						this.add(Math.MULTIPLY);
					}
				}
			}
			break parent;
		case Fraction:
			if (isTopLevel) {
				if (node.getIndex() == 0) {
					this.add(Math.DIVIDE);
				} else {
					this.add(Math.MULTIPLY);
				}
			}
			break parent;
		case Exponential:
			if (isTopLevel) {
				if (node.getIndex() == 1) {
					this.add(Math.INVERSE_EXPONENT);
				} else if (node.getIndex() == 0 && TypeSGET.Number.equals(type)) {
					this.add(Math.LOG);
				}
			}
			break parent;
		case Equation:
			if ("0".equals(node.getSymbol())) {
				break;
			}
			isSide = true;
			this.add(Math.SUBTRACT);
			this.add(Math.DIVIDE);
			eq: switch (type) {
			case Log:
				this.add(Math.RAISE);
				break eq;
			case Trig:
				this.add(Math.INVERSE_TRIG);
				break eq;
			}
			break parent;
		}

	}

	private void add(Math operation) {
		add(makeButton(operation));
	}

	private BothSidesButton makeButton(Math operation) {
		return makeButton(operation, null);
	}

	private BothSidesButton makeButton(Math operation,
			BothSidesButton joinedButton) {
		String html;
		BothSidesButton button = null;
		Badge badge = null;
		
		String reference = UP_ARROW;
		if(TypeSGET.ChildRequirement.TERMINAL.equals(node.getType().childRequirement())) {
			Element referenceEl = node.getHTMLClone(true,
					true);
			referenceEl.addClassName(CSS.MATH_FONT);
			reference = JSNICalls.elementToString(referenceEl);
		}

		switch (operation) {
		case ADD:
			badge = Badge.BOTH_SIDES_ADD;
			html = UP_ARROW_HTML_START + PLUS + UP_ARROW_HTML_END
					+ reference;
			button = new AddOrSubBothButton(operation, html, this, joinedButton);
			break;
		case SUBTRACT:
			badge = Badge.BOTH_SIDES_SUBTRACT;
			html = UP_ARROW_HTML_START + MINUS + UP_ARROW_HTML_END
					+ reference;
			button = new AddOrSubBothButton(operation, html, this, joinedButton);
			break;
		case MULTIPLY:
			badge = Badge.BOTH_SIDES_MULTIPLY;
			html = UP_ARROW_HTML_START + MULTIPLY + UP_ARROW_HTML_END
					+ reference;
			button = new MultiplyBothButton(operation, html, this, joinedButton);
			break;
		case DIVIDE:
			badge = Badge.BOTH_SIDES_DIVIDE;
			html = UP_ARROW_HTML_START + DIVIDE + UP_ARROW_HTML_END
					+ reference;
			button = new DivideBothButton(operation, html, this, joinedButton);
			break;
		case INVERSE_EXPONENT:
			badge = Badge.BOTH_SIDES_INVERSE_EXPONENT;
			html = UP_ARROW + "<sup>1/" + reference
					+ "</sup>";
			button = new RootBothButton(operation, html, this, joinedButton);
			break;
		case RAISE:
			badge = Badge.BOTH_SIDES_RAISE;
			String base = node.getAttribute(MathAttribute.LogBase);
			html = base + "<sup>" + UP_ARROW + "</sup>";
			button = new RaiseBothButton(operation, base, html, this,
					joinedButton);
			break;
		case LOG:
			badge = Badge.BOTH_SIDES_LOG;
			html = "log<sub>" + reference + "</sub>"
					+ UP_ARROW;
			button = new LogBothButton(operation, html, this, joinedButton);
			break;
		case INVERSE_TRIG:
			badge = Badge.BOTH_SIDES_INVERSE_TRIG;
			String func = node.getAttribute(MathAttribute.Function);
			String inverseFunc = TrigFunctions.getInverseName(func);
			html = inverseFunc + "(" + UP_ARROW + ")";
			button = new InverseTrigBothButton(operation, inverseFunc, html,
					this, joinedButton);
			break;
		}
		Boolean hasShortcuts = Moderator.meetsRequirement(badge);
		button.autoCancel = hasShortcuts;
		
		if(joinedButton == null && hasShortcuts) {
			WrapDragController dragC = node.getWrapper().getDragController();
			BothSidesDrop dropC = new BothSidesDrop(dragC, button, tree.getEquals().getWrapper());
			dragC.registerDropController(dropC);
		}
		
		return button;
	}

	enum Math {
		ADD, SUBTRACT, MULTIPLY, DIVIDE, INVERSE_EXPONENT, RAISE, LOG, INVERSE_TRIG
	}

	public abstract class BothSidesButton extends TransformationButton {

		private BothSidesButton joinedButton;

		protected EquationNode targetSide = null;
		protected String changeComment;
		EquationNode topParent = null;
		boolean autoCancel = false;

		private Math operation;

		BothSidesButton(Math operation, String html,
				BothSidesTransformations context) {
			this(operation, html, context, null);
		}

		BothSidesButton(Math operation, String html,
				BothSidesTransformations context, BothSidesButton joinedButton) {
			super(html, context);

			this.autoUnselect = false;
			this.operation = operation;

			if (joinedButton == null) {
				this.joinedButton = makeButton(operation, this);
			} else {
				this.joinedButton = joinedButton;
			}

			addStyleName(CSS.BOTH_SIDES_BUTTON);

			if (isSide) {
				topParent = node;
			} else if (isTopLevel) {
				topParent = oldParent;
			} else if (isNestedInFraction) {
				topParent = oldParent.getParent();
			} else {
				JSNICalls
						.error("Added bothSidesHandler to wrong node: " + node);
			}
			if (topParent.isLeftSide()) {
				targetSide = tree.getRightSide();
			} else if (topParent.isRightSide()) {
				targetSide = tree.getLeftSide();
			} else {
				JSNICalls.error("bothSidesHandler on wrong node: " + node
						+ "\nparent: " + topParent);
			}

		}

		public BothSidesButton getJoinedButton() {
			return joinedButton;
		}
		
		public boolean isSelected() {
			return getStyleName().contains(CSS.BOTH_SIDES_BUTTON_SELECTED);
		}

		public void select() {
			addStyleName(CSS.BOTH_SIDES_BUTTON_SELECTED);
		}

		public void deselect() {
			removeStyleName(CSS.BOTH_SIDES_BUTTON_SELECTED);
		}

		void flash() {
			final Style style = getElement().getStyle();
			Animation flash = new Animation() {
				boolean isGettingDarker;
				int flashCounter = 0;
				final int FLASH_COUNT = 20;

				@Override
				protected void onUpdate(double progress) {
					if (isGettingDarker) {
						progress = 1 - progress;
					}
					// Go white to gray (225 tp 128 in hexadecimal)
					String colorProgress = Integer
							.toHexString(225 - (int) (progress * 200));
					style.setBackgroundColor("#" + colorProgress
							+ colorProgress + colorProgress);
				}

				@Override
				protected void onComplete() {
					super.onComplete();
					flashCounter++;
					if (joinedButton.isSelected() && flashCounter < FLASH_COUNT) {
						isGettingDarker = !isGettingDarker;
						run(500);
					} else {
						style.clearBackgroundColor();
					}
				}
			};
			flash.run(500);
		}

		@Override
		public Badge getAssociatedBadge() {
			switch (operation) {
			case ADD:
				return Badge.BOTH_SIDES_ADD;
			case SUBTRACT:
				return Badge.BOTH_SIDES_SUBTRACT;
			case MULTIPLY:
				return Badge.BOTH_SIDES_MULTIPLY;
			case DIVIDE:
				return Badge.BOTH_SIDES_DIVIDE;
			case INVERSE_EXPONENT:
				return Badge.BOTH_SIDES_INVERSE_EXPONENT;
			case RAISE:
				return Badge.BOTH_SIDES_RAISE;
			case LOG:
				return Badge.BOTH_SIDES_LOG;
			case INVERSE_TRIG:
				return Badge.BOTH_SIDES_INVERSE_TRIG;
			}
			throw new IllegalArgumentException(
					"No associated badge for operation: " + operation);
		}

		@Override
		public boolean meetsAutoTransform() {
			return true;
		}

		@Override
		public void transform() {
			BothSidesButton joinedButton = this.getJoinedButton();

			if (joinedButton.isSelected()) {
				joinedButton.deselect();
				execute();
			} else {
				this.select();
				joinedButton.flash();
				return;
			}
		}

		void execute() {
			changeComment = this.getHTML();
			
			if(reloadAlgebraActivity) {
				node.lineThrough();
			}
			execution();
		}
		protected abstract void execution();
	}

	// ////////////////////////////////////////////////////////
	// Both Sides Buttons
	// ///////////////////////////////////////////////////////

	class AddOrSubBothButton extends BothSidesButton {
		AddOrSubBothButton(Math operation, String html,
				BothSidesTransformations context, BothSidesButton joinedButton) {
			super(operation, html, context, joinedButton);
		}

		@Override
		protected void execution() {

			// Prepare Target side
			targetSide = targetSide.encase(TypeSGET.Sum);

			// take operation
			EquationNode operator = node.getPrevSibling();
			if (operator != null && Operator.EQUALS.equals(operator.getOperation())) {
				operator = null;
			}
			if (node.getIndex() > 0 && !isSide) {
				if (!autoCancel) {
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
				targetSide.append(TypeSGET.Operation, MINUS);
			}

			if (autoCancel) {
				// Leave 0 in old side if top node
				if (isSide) {
					node.getParent().addBefore(node.getIndex(),
							TypeSGET.Number, "0");
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
				topParent = topParent.encase(TypeSGET.Sum);
				if (operator != null) {
					topParent.append(operator.clone());
				} else {
					topParent.append(TypeSGET.Operation, MINUS);
				}
				topParent.append(node.clone());
			}

			onTransformationEnd(changeComment, node);
		}

	}

	class DivideBothButton extends BothSidesButton {
		DivideBothButton(Math operation, String html,
				BothSidesTransformations context, BothSidesButton joinedButton) {
			super(operation, html, context, joinedButton);
		}

		@Override
		protected void execution() {

			EquationNode operator = null;

			if (autoCancel) {
				if (TypeSGET.Fraction.equals(oldParent.getType())) {
					// leave 1 in numerator
					oldParent.addFirst(TypeSGET.Number, "1");
				} else if (isSide) {
					// Leave 1 in old side if top node
					oldParent.addBefore(node.getIndex(), TypeSGET.Number, "1");
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
			if (Moderator
					.meetsRequirement(Badge.BOTH_SIDES_DIVIDE_INTO_DENOMINATOR)
					&& TypeSGET.Fraction.equals(targetSide.getType())) {
				targetSide = targetSide.getChildAt(1);
				targetSide = targetSide.encase(TypeSGET.Term);
				if (operator == null) {
					targetSide.append(TypeSGET.Operation, TypeSGET.Operator
							.getMultiply().getSign());
				} else {
					targetSide.append(operator);
				}
			} else {
				targetSide = targetSide.encase(TypeSGET.Fraction);
				if (operator != null) {
					operator.remove();
				}
			}

			if (autoCancel) {
				// move node to other side
				targetSide.append(node);

				// clean source side
				if (!isSide) {
					EquationNode oldFirstSib = oldParent.getFirstChild();
					if (oldFirstSib != null
							&& TypeSGET.Operation.equals(oldFirstSib.getType())) {
						oldFirstSib.remove();
					}

					oldParent.decase();
				}
			} else {// create node on other side
				targetSide.append(node.clone());
				if (TypeSGET.Fraction.equals(topParent.getType()) && !isSide) {
					EquationNode denominator = topParent.getChildAt(1).encase(
							TypeSGET.Term);
					denominator.append(TypeSGET.Operation, TypeSGET.Operator
							.getMultiply().getSign());
					denominator.append(node.clone());
				} else {
					topParent = topParent.encase(TypeSGET.Fraction);
					topParent.append(node.clone());
				}
			}

			onTransformationEnd(changeComment, node);
		}
	}

	class MultiplyBothButton extends BothSidesButton {
		MultiplyBothButton(Math operation, String html,
				BothSidesTransformations context, BothSidesButton joinedButton) {
			super(operation, html, context, joinedButton);
		}

		@Override
		protected void execution() {

			boolean AutoIntoFraction = Moderator
					.meetsRequirement(Badge.MULTIPLY_WITH_FRACTION);

			// Prepare Target side
			if (AutoIntoFraction
					&& TypeSGET.Fraction.equals(targetSide.getType())) {
				targetSide = targetSide.getFirstChild();
			}
			targetSide = targetSide.encase(TypeSGET.Term);

			if (isNestedInFraction && autoCancel) {
				EquationNode operation = null;
				if (node.getIndex() == 0) {
					operation = node.getNextSibling();
				} else {
					operation = node.getPrevSibling();
				}
				if (operation != null
						&& TypeSGET.Operation.equals(operation.getType())) {
					targetSide.append(operation);
				}
			} else if (isTopLevel || !autoCancel) {
				targetSide.append(TypeSGET.Operation, TypeSGET.Operator
						.getMultiply().getSign());
			} else {
				JSNICalls
						.warn("Multiplying somethimg that's not top level or nested in a top level fraction: "
								+ node.toString());
			}

			if (autoCancel) {
				targetSide.append(node);

				// clean source side
				if (TypeSGET.Fraction.equals(oldParent.getType())) {
					// remove unnecessary intermediate fraction
					oldParent.replace(oldParent.getFirstChild());
				} else if (TypeSGET.Term.equals(oldParent.getType())) {
					oldParent.decase();
				} else {
					JSNICalls
							.warn("The parent of the divideBothSides must either be a term or fraction with index=0");
				}
			} else {
				targetSide.append(node.clone());

				// node = node.clone();
				EquationNode sameSideTarget = topParent;
				if (AutoIntoFraction) {
					sameSideTarget = topParent.getChildAt(0);
				}
				sameSideTarget = sameSideTarget.encase(TypeSGET.Term);
				sameSideTarget.append(TypeSGET.Operation, Operator
						.getMultiply().getSign());
				sameSideTarget.append(node.clone());
			}

			onTransformationEnd(changeComment, node);
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
	class RootBothButton extends BothSidesButton {
		RootBothButton(Math operation, String html,
				BothSidesTransformations context, BothSidesButton joinedButton) {
			super(operation, html, context, joinedButton);
		}

		@Override
		protected void execution() {

			// Prepare Target side
			EquationNode target = null;
			if (!TypeSGET.Exponential.equals(targetSide.getType())) {
				targetSide = targetSide.encase(TypeSGET.Exponential);
				target = targetSide;
			} else {
				EquationNode targetExp = targetSide.getChildAt(1);
				targetExp = targetExp.encase(TypeSGET.Term);
				targetExp.append(TypeSGET.Operation, Operator.getMultiply()
						.getSign());
				target = targetExp;
			}

			if (!autoCancel) {
				node = node.clone();
			}

			if (TypeSGET.Fraction.equals(node.getType())) {
				EquationNode numerator = node.getChildAt(0);
				if (TypeSGET.Number.equals(numerator.getType())
						&& "1".equals(numerator.getSymbol())) {
					target.append(node.getChildAt(1));
					node.remove();
				} else {
					node.append(numerator);// flip
					target.append(node);
				}
			} else {
				EquationNode frac = target.append(TypeSGET.Fraction, "");
				frac.append(TypeSGET.Number, "1");
				frac.append(node);
			}

			if (autoCancel) {
				// clean source side
				oldParent.replace(oldParent.getFirstChild());
			} else {
				oldParent = oldParent.encase(TypeSGET.Exponential);
				EquationNode frac = oldParent.append(TypeSGET.Fraction, "");
				frac.append(TypeSGET.Number, "1");
				frac.append(node.clone());
			}

			onTransformationEnd(changeComment, node);
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
	class RaiseBothButton extends BothSidesButton {
		String base;

		RaiseBothButton(Math operation, String base, String html,
				BothSidesTransformations context, BothSidesButton joinedButton) {
			super(operation, html, context, joinedButton);
			this.base = base;
		}

		@Override
		protected void execution() {

			// Prepare Target side
			EquationNode targetExp = targetSide.encase(TypeSGET.Exponential);
			targetExp.addFirst(TypeSGET.Number, base);

			if (autoCancel) {
				// clean source side
				node.replace(node.getFirstChild());
			} else {
				EquationNode fromExp = node.encase(TypeSGET.Exponential);
				fromExp.addFirst(TypeSGET.Number, base);
			}

			onTransformationEnd(changeComment, node);
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
	class LogBothButton extends BothSidesButton {
		LogBothButton(Math operation, String html,
				BothSidesTransformations context, BothSidesButton joinedButton) {
			super(operation, html, context, joinedButton);
		}

		@Override
		protected void execution() {

			// Prepare Target side
			EquationNode targetLog = targetSide.encase(TypeSGET.Log);
			targetLog.setAttribute(MathAttribute.LogBase, node.getSymbol());

			if (autoCancel) {
				// clean source side
				node.replace(node.getChildAt(1));
			} else {
				EquationNode fromLog = node.encase(TypeSGET.Log);
				fromLog.setAttribute(MathAttribute.LogBase, node.getSymbol());
			}

			onTransformationEnd(changeComment, node);
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
	class InverseTrigBothButton extends BothSidesButton {
		String toFunction;

		InverseTrigBothButton(Math operation, String toFunction, String html,
				BothSidesTransformations context, BothSidesButton joinedButton) {
			super(operation, html, context, joinedButton);
			this.toFunction = toFunction;
		}

		@Override
		protected void execution() {

			// Prepare Target side
			EquationNode targetTrig = targetSide.encase(TypeSGET.Trig);
			targetTrig.setAttribute(MathAttribute.Function, toFunction);

			if (autoCancel) {
				// clean source side
				node.replace(node.getFirstChild());
			} else {
				EquationNode fromTrig = node.encase(TypeSGET.Trig);
				fromTrig.setAttribute(MathAttribute.Function, toFunction);
			}

			onTransformationEnd(changeComment, node);
		}
	}
}
