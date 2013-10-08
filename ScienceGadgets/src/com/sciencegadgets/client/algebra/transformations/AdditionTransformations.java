package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.Type.Operator;

public class AdditionTransformations {

	protected static MathNode operation;
	protected static MathNode parent;
	protected static MathNode grandParent;
	protected static boolean isPlus;
	private static final int same = 0;

	public static void assign(MathNode left, MathNode sign, MathNode right,
			boolean isPlusSign) {
		operation = sign;
		parent = operation.getParent();
		grandParent = parent.getParent();
		isPlus = isPlusSign;// Operator.PLUS.getSign().equals(sign.getSymbol());
		boolean assigned = false;

		// if (!isPlus && !Operator.MINUS.getSign().equals(sign.getSymbol())) {
		// throw new Exception(
		// "Sign is neither Operator.MINUS or Operator.PLUS: "
		// + operation.toString());
		// }

		Type leftType = left.getType();
		Type rightType = right.getType();

		// Check for improper types for left and right
		switch (rightType) {
		case Equation:
		case Sum:
			JSNICalls.error("Illegal node within Sum: " + rightType);
			break;
		case Operation:
			JSNICalls.error("Operation in wrong place: "
					+ right.getParent().toString());
			break;
		case Number:
			BigDecimal rightValue = new BigDecimal(right.getSymbol());
			if (rightValue.compareTo(new BigDecimal(0)) == same) {
				addZero(left, right);
				return;
			}
			break;
		}
		switch (leftType) {
		case Equation:
		case Sum:
			JSNICalls.error("Illegal node within Sum: " + leftType);
			break;
		case Operation:
			JSNICalls.error("Operation in wrong place: "
					+ left.getParent().toString());
			break;
		case Number:
			BigDecimal leftValue = new BigDecimal(left.getSymbol());
			if (leftValue.compareTo(new BigDecimal(0)) == same) {
				addZero(right, left);
				return;
			}
			break;
		}

		// Assignments in 2d switch (leftType, rightType)
		first: switch (leftType) {
		case Term:
			second: switch (rightType) {
			case Term:
				assigned = addSimilar_check(left, right);
				if (!Moderator.isInEasyMode || !assigned)
					factorLikeTerms_check(left, right);
				break second;
			case Exponential:
				assigned = factorWithBase_check(left, right);
				if (!Moderator.isInEasyMode || !assigned)
					factorWithTermChild_check(right, left);
				break second;
			case Fraction:
				assigned = factorWithTermChild_check(right, left);
				if (!Moderator.isInEasyMode || !assigned)
					factorLikeTerms_check(left, right);
				break second;
			case Variable:
				factorWithTermChild_check(right, left);
				break second;
			case Number:
				factorWithTermChild_check(right, left);
				break second;
			}
			break first;
		case Exponential:
			second: switch (rightType) {
			case Term:
				assigned = factorWithBase_check(right, left);
				if (!Moderator.isInEasyMode || !assigned)
					factorWithTermChild_check(left, right);
				break second;
			case Exponential:
				assigned = addSimilar_check(left, right);
				if (!Moderator.isInEasyMode || !assigned)
					assigned = factorWithBase_check(left, right);
				if (!Moderator.isInEasyMode || !assigned)
					factorWithBase_check(right, left);
				break second;
			case Fraction:
				assigned = factorWithTermChild_check(left, right);
				if (!Moderator.isInEasyMode || !assigned)
					factorWithBase_check(right, left);
				break second;
			case Variable:
				factorWithBase_check(right, left);
				break second;
			case Number:
				factorWithBase_check(right, left);
				break second;
			}
			break first;
		case Fraction:
			second: switch (rightType) {
			case Term:
				assigned = factorWithTermChild_check(left, right);
				if (!Moderator.isInEasyMode || !assigned)
					factorLikeTerms_check(left, right);
				break second;
			case Exponential:
				assigned = factorWithTermChild_check(right, left);
				if (!Moderator.isInEasyMode || !assigned)
					factorWithBase_check(right, left);
				break second;
			case Fraction:
				assigned = addSimilar_check(left, right);
				if (!Moderator.isInEasyMode || !assigned)
					assigned = addFractions_check(left, right);
				if (!Moderator.isInEasyMode || !assigned)
					factorLikeTerms_check(left, right);
				break second;
			case Variable:
				factorWithTermChild_check(right, left);
				break second;
			case Number:
				factorWithTermChild_check(right, left);
				break second;
			}
			break first;
		case Variable:
			second: switch (rightType) {
			case Term:
				factorWithTermChild_check(left, right);
				break second;
			case Exponential:
				factorWithBase_check(left, right);
				break second;
			case Fraction:
				factorWithTermChild_check(left, right);
				break second;
			case Variable:
				addSimilar_check(left, right);
				break second;
			}
			break first;
		case Number:
			second: switch (rightType) {
			case Term:
				factorWithTermChild_check(left, right);
				break second;
			case Exponential:
				factorWithBase_check(left, right);
				break second;
			case Fraction:
				factorWithTermChild_check(left, right);
				break second;
			case Number:
				addNumbers_prompt(left, right);
				break second;
			}
			break first;
		}

	}

	private static void factor(Collection<MathNode> factors,
			MathNode inBinomialA, MathNode inBinomialB) {

		MathNode inBinomialFirst = inBinomialA;
		MathNode inBinomialSecond = inBinomialB;
		if (inBinomialA.getIndex() > inBinomialB.getIndex()) {
			inBinomialFirst = inBinomialB;
			inBinomialSecond = inBinomialA;
		}

		MathNode termCasing = operation.getParent().addBefore(operation.getIndex(),
				Type.Term, "");

		for (MathNode factor : factors) {
			termCasing.append(factor);
			termCasing.append(Type.Operation, Operator.getMultiply().getSign());
		}

		MathNode binomialCasing = termCasing.append(Type.Sum, "");
		binomialCasing.append(inBinomialFirst);
		binomialCasing.append(operation);
		binomialCasing.append(inBinomialSecond);

		 parent.decase();
	}

	private static boolean factorLikeTerms_check(MathNode left, MathNode right) {
		MathNode leftTerm = left;
		MathNode rightTerm = right;

		// If sides are fractions, like terms come from numerators
		if (Type.Fraction.equals(left.getType())) {
			if (Type.Term.equals(left.getChildAt(0).getType())) {
				leftTerm = left.getChildAt(0);
			} else {
				return false;
			}
		}
		if (Type.Fraction.equals(right.getType())) {
			if (Type.Term.equals(right.getChildAt(0).getType())) {
				rightTerm = right.getChildAt(0);
			} else {
				return false;
			}
		}

		// Collect like terms
		LinkedHashMap<MathNode, MathNode> likeTerms = new LinkedHashMap<MathNode, MathNode>();
		a: for (MathNode leftChild : leftTerm.getChildren()) {
			if (Type.Operation.equals(leftChild.getType())) {
				continue a;
			}
			b: for (MathNode rightChild : rightTerm.getChildren()) {
				if (likeTerms.containsValue(rightChild)
						|| Type.Operation.equals(rightChild.getType())) {
					continue b;
				}
				if (leftChild.isLike(rightChild)) {
					likeTerms.put(leftChild, rightChild);
					continue a;
				}
			}
		}
		// Check
		if (likeTerms.size() == 0) {
			return false;
		}

		if (Moderator.isInEasyMode) {
			factorLikeTerms(left, right, likeTerms);
		} else {
			Moderator.selectedMenu.add(new FLTButton(left, right, likeTerms));
		}

		return true;
	}

	/**
	 * Factors out all like entities within the term or numerator
	 * 
	 * @param left
	 *            - term, fraction
	 * @param right
	 *            - term, fraction
	 */
	static void factorLikeTerms(MathNode left, MathNode right,
			LinkedHashMap<MathNode, MathNode> likeTerms) {
		RootPanel.get().add(new HTML("factor like terms"));
		
		//Highlight terms in AlgOut
		for(MathNode key : likeTerms.keySet()){
			likeTerms.get(key).highlight();
			key.highlight();
		}

		//Like terms are cloned to simplify cleanup
		LinkedList<MathNode> factors = new LinkedList<MathNode>();
		for (MathNode factor : likeTerms.values()) {
			factors.add(factor.clone());
		}

		// Remove operation on like term
		if (likeTerms.size() * 2 - 1 == left.getChildCount()) {
			left = left.replace(Type.Term, "");// will be MathNode.decase[d]
			left.append(Type.Number, "1");
		} else {
			for (MathNode extraFactor : likeTerms.keySet()) {

				if (extraFactor.getIndex() == 0) {
					MathNode nextOp = extraFactor.getNextSibling();
					if (nextOp != null
							&& Type.Operation.equals(nextOp.getType())) {
						nextOp.remove();
					}
				} else {
					MathNode prevOp = extraFactor.getPrevSibling();
					if (Type.Operation.equals(prevOp.getType())) {
						prevOp.remove();
					}
				}
				extraFactor.remove();
			}
		}

		if (likeTerms.size() * 2 - 1 == right.getChildCount()) {
			right = right.replace(Type.Term, "");
			right.append(Type.Number, "1");
		} else {
			for (MathNode fact : likeTerms.values()) {
				if (fact.getIndex() == 0) {
					MathNode nextOp = fact.getNextSibling();
					if (nextOp != null
							&& Type.Operation.equals(nextOp.getType())) {
						nextOp.remove();
					}
				} else {
					MathNode prevOp = fact.getPrevSibling();
					if (Type.Operation.equals(prevOp.getType())) {
						prevOp.remove();
					}
				}
				fact.remove();
			}
		}

		factor(factors, left, right);

		left.decase();
		right.decase();

		Moderator.reloadEquationPanel("Factor Like Terms");
	}

	private static boolean factorWithBase_check(MathNode other,
			MathNode exponential) {

		if (!other.isLike(exponential.getChildAt(0))) {
			return false;
		}

		if (Moderator.isInEasyMode) {
			factorWithBase(other, exponential);
		} else {
			Moderator.selectedMenu.add(new FWBButton(other, exponential));
		}

		return true;
	}

	/**
	 * Factors out a single multiple of the base and other entity if equal
	 * 
	 * @param other
	 *            - term, exponential, fraction, variable, number
	 * @param exponential
	 */
	static void factorWithBase(MathNode other, MathNode exponential) {
		RootPanel.get().add(new HTML("factor with base"));
		
		other.highlight();
		operation.highlight();
		exponential.getChildAt(0).highlight();

		MathNode inBinomialB = exponential;
		MathNode inBinomialA = other.getParent().addBefore(other.getIndex(),
				Type.Number, "1");

		LinkedList<MathNode> factors = new LinkedList<MathNode>();
		factors.add(other);
		factor(factors, inBinomialA, inBinomialB);

		MathNode exp = exponential.getChildAt(1).encase(Type.Sum);
		exp.append(Type.Operation, Operator.MINUS.getSign());
		exp.append(Type.Number, "1");

		Moderator.reloadEquationPanel("Factor with Base");
	}

	private static boolean factorWithTermChild_check(MathNode other,
			MathNode termContainer) {
		MathNode term = termContainer;

		// If sides are fractions, like terms come from numerators
		if (Type.Fraction.equals(termContainer.getType())) {
			if (Type.Term.equals(termContainer.getChildAt(0).getType())) {
				term = termContainer.getChildAt(0);
			} else {
				return false;
			}
		}

		for (MathNode termChild : term.getChildren()) {
			if (termChild.isLike(other)) {
				if (Moderator.isInEasyMode) {
					factorWithTermChild(other, termContainer, termChild);
				} else {
					Moderator.selectedMenu.add(new FWTCButton(other,
							termContainer, termChild));
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Factors out a child of the term if similar to the other entity
	 * 
	 * @param other
	 *            - exponential, fraction, variable, number
	 * @param term
	 *            - term, fraction
	 */
	static void factorWithTermChild(MathNode other, MathNode term,
			MathNode termChild) {
		RootPanel.get().add(new HTML("factor with term child"));
		
		other.highlight();
		operation.highlight();
		termChild.highlight();

		MathNode inBinomialA = other;
		MathNode inBinomialB = term;

		if (termChild.getIndex() == 0) {
			MathNode nextOp = termChild.getNextSibling();
			if (nextOp != null && Type.Operation.equals(nextOp.getType())) {
				nextOp.remove();
			}
		} else {
			MathNode prevOp = termChild.getPrevSibling();
			if (Type.Operation.equals(prevOp.getType())) {
				prevOp.remove();
			}
		}

		// Idea: factor a single multiple of a base, other must be base

		// if (Type.Exponential.equals(other.getParentType())) {
		// inBinomialA = other.getParent();
		// MathNode exp = other.getNextSibling().encase(Type.Sum);
		// exp.add(Type.Operation, Operator.MINUS.getSign());
		// exp.add(Type.Number, "1");
		// } else {
		inBinomialA = other.getParent().addBefore(other.getIndex(), Type.Number, "1");
		other.remove();
		// }

		LinkedList<MathNode> factors = new LinkedList<MathNode>();
		factors.add(termChild);
		factor(factors, inBinomialA, inBinomialB);

		term.decase();

		Moderator.reloadEquationPanel("Factor with TermChild");
	}

	private static boolean addFractions_check(MathNode left, MathNode right) {
		// Common denominators
		if (!left.getChildAt(1).isLike(right.getChildAt(1))) {
			return false;
		}

		if (Moderator.isInEasyMode) {
			addFractions(left, right);
		} else {
			Moderator.selectedMenu.add(new AddFractionsButton(left, right));
		}

		return true;

	}

	static void addFractions(MathNode left, MathNode right) {
		
		right.highlight();
		operation.highlight();
		left.highlight();

		MathNode numeratorCasing = right.getChildAt(0).encase(Type.Sum);
		numeratorCasing.addBefore(0, operation);
		numeratorCasing.addBefore(0, left.getChildAt(0));

		left.remove();
		parent.decase();

		Moderator.reloadEquationPanel("Add Fractions");
	}

	private static boolean addSimilar_check(MathNode left, MathNode right) {

		if (!left.isLike(right)) {
			return false;
		}

		if (Moderator.isInEasyMode) {
			addSimilar(left, right);
		}
		return true;
	}

	static void addSimilar(MathNode left, MathNode right) {
		
		right.highlight();
		operation.highlight();
		left.highlight();

		if (isPlus) {
			MathNode casing = right.encase(Type.Term);
			casing.addBefore(0, Type.Operation, Operator.getMultiply().getSign());
			casing.addBefore(0, Type.Number, "2");
		} else {
			right.remove();
		}

		left.remove();
		operation.remove();
		parent.decase();

		Moderator.reloadEquationPanel("Add similar");
	}

	private static void addNumbers_prompt(final MathNode left,
			final MathNode right) {

		final BigDecimal leftValue = new BigDecimal(left.getSymbol());
		final BigDecimal rightValue = new BigDecimal(right.getSymbol());
		BigDecimal total;

		if (isPlus) {
			total = leftValue.add(rightValue);
		} else {
			total = leftValue.subtract(rightValue);
		}
		final BigDecimal totalValue = total;

		if (Moderator.isInEasyMode) {
			addNumbers(left, right, totalValue, leftValue, rightValue);
		} else {//prompt
			Moderator.selectedMenu.add(new HTML(leftValue.toString() + " "
					+ operation.getSymbol() + " " + rightValue.toString() +" ="));
			TextBox inp = new TextBox();
			inp.addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					BigDecimal inputValue = new BigDecimal(event.getValue());
					if (inputValue.compareTo(totalValue) == same) {
						addNumbers(left, right, totalValue, leftValue, rightValue);
					}
				}
			});
			Moderator.selectedMenu.add(inp);
			inp.setFocus(true);
		}

	}

	static void addNumbers(MathNode left, MathNode right, BigDecimal totalValue, BigDecimal leftValue, BigDecimal rightValue) {
		
		right.highlight();
		operation.highlight();
		left.highlight();

		right.setSymbol(totalValue.stripTrailingZeros().toString());
		
		left.remove();
		operation.remove();
		parent.decase();

		Moderator.reloadEquationPanel(leftValue.toString()+operation.toString()+rightValue.toString()+" = "+totalValue.toString());
	}

	static void addZero(MathNode other, MathNode zero) {

		zero.highlight();

//		if (zero.getIndex() > other.getIndex()) {
//			MathNode zeroOp = zero.getPrevSibling();
//			if (Type.Operation.equals(zeroOp.getType())) {
//				zeroOp.remove();
//			}
//		} else {
//			MathNode zeroOp = zero.getNextSibling();
//			if (Type.Operation.equals(zeroOp.getType())) {
//				zeroOp.remove();
//			}
//		}
		operation.remove();

		zero.remove();

		parent.decase();

		Moderator.reloadEquationPanel("Add zero");
	}
}

class FLTButton extends Button {

	FLTButton(final MathNode left, final MathNode right,
			final LinkedHashMap<MathNode, MathNode> likeTerms) {

		String factorString = "";
		for (MathNode fact : likeTerms.keySet()) {
			factorString.concat(fact.getHTMLString());
		}
		setHTML("Factor " + factorString);

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdditionTransformations.factorLikeTerms(left, right, likeTerms);
			}
		});
	}
}

class FWBButton extends Button {

	FWBButton(final MathNode other, final MathNode exponential) {

		setHTML("Factor " + other.getHTMLString());

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdditionTransformations.factorWithBase(other, exponential);
			}
		});
	}
}

class FWTCButton extends Button {

	FWTCButton(final MathNode other, final MathNode termContainer,
			final MathNode termChild) {

		setHTML("Factor " + other.getHTMLString());

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdditionTransformations.factorWithTermChild(other,
						termContainer, termChild);
			}
		});
	}
}

class AddFractionsButton extends Button {

	AddFractionsButton(final MathNode left, final MathNode right) {

		setHTML("Add Fractions");

		this.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AdditionTransformations.addFractions(left, right);
			}
		});
	}
}
