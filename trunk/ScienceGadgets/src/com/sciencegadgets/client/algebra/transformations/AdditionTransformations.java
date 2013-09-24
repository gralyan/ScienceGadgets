package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.Type.Operator;

public class AdditionTransformations {

	private static MathNode operation;
	private static MathNode parent;
	private static MathNode grandParent;
	private static boolean isPlus;
	private static final int same = 0;
	private static boolean isInEasyMode = Moderator.isInEasyMode;

	public static void assign(MathNode left, MathNode sign, MathNode right, boolean isPlusSign) {
		operation = sign;
		parent = operation.getParent();
		grandParent = parent.getParent();
		isPlus = isPlusSign;//Operator.PLUS.getSign().equals(sign.getSymbol());
		boolean assigned = false;

//		if (!isPlus && !Operator.MINUS.getSign().equals(sign.getSymbol())) {
//			throw new Exception(
//					"Sign is neither Operator.MINUS or Operator.PLUS: "
//							+ operation.toString());
//		}

		Type leftType = left.getType();
		Type rightType = right.getType();

		// Check for improper types for left and right
		switch (rightType) {
		case Aesthetic:
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
		case Aesthetic:
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
				if (!isInEasyMode || !assigned)
					factorLikeTerms_check(left, right);
				break second;
			case Exponential:
				assigned = factorWithBase_check(left, right);
				if (!isInEasyMode || !assigned)
					factorWithTermChild_check(right, left);
				break second;
			case Fraction:
				assigned = factorWithTermChild_check(right, left);
				if (!isInEasyMode || !assigned)
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
				if (!isInEasyMode || !assigned)
					factorWithTermChild_check(left, right);
				break second;
			case Exponential:
				assigned = addSimilar_check(left, right);
				if (!isInEasyMode || !assigned)
					assigned = factorWithBase_check(left, right);
				if (!isInEasyMode || !assigned)
					factorWithBase_check(right, left);
				break second;
			case Fraction:
				assigned = factorWithTermChild_check(left, right);
				if (!isInEasyMode || !assigned)
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
				if (!isInEasyMode || !assigned)
					factorLikeTerms_check(left, right);
				break second;
			case Exponential:
				assigned = factorWithTermChild_check(right, left);
				if (!isInEasyMode || !assigned)
					factorWithBase_check(right, left);
				break second;
			case Fraction:
				assigned = addSimilar_check(left, right);
				if (!isInEasyMode || !assigned)
					assigned = addFractions_check(left, right);
				if (!isInEasyMode || !assigned)
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
				addNumbers(left, right);
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

		MathNode termCasing = operation.getParent().add(operation.getIndex(),
				Type.Term, "");

		for (MathNode factor : factors) {
			termCasing.add(factor);
			termCasing.add(Type.Operation, Operator.getMultiply().getSign());
		}

		MathNode binomialCasing = termCasing.add(Type.Sum, "");
		binomialCasing.add(inBinomialFirst);
		binomialCasing.add(operation);
		binomialCasing.add(inBinomialSecond);

		// parent.decase();
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

		if (isInEasyMode) {
			factorLikeTerms(left, right, likeTerms);
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
	private static void factorLikeTerms(MathNode left, MathNode right,
			LinkedHashMap<MathNode, MathNode> likeTerms) {
		RootPanel.get().add(new HTML("factor like terms"));

		LinkedList<MathNode> factors = new LinkedList<MathNode>();
		for (MathNode factor : likeTerms.values()) {
			factors.add(factor.clone());
		}

		// Remove operation on like term
		System.out.println("likeTerms.size() " + likeTerms.size());
		System.out.println("left.getChildCount() " + left.getChildCount());
		if (likeTerms.size() * 2 - 1 == left.getChildCount()) {
			left = left.replace(Type.Term, "");// TODO do this another way
			left.add(Type.Number, "1");
		} else {
			for (MathNode extraFactor : likeTerms.keySet()) {

				if (extraFactor.getIndex() == 0) {
					try {
						MathNode nextOp = extraFactor.getNextSibling();
						if (Type.Operation.equals(nextOp.getType())) {
							nextOp.remove();
						}
					} catch (IndexOutOfBoundsException e) {
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

		System.out.println("right.getChildCount() " + right.getChildCount());
		if (likeTerms.size() * 2 - 1 == right.getChildCount()) {
			right = right.replace(Type.Term, "");
			right.add(Type.Number, "1");
		} else {
			for (MathNode fact : likeTerms.values()) {
				if (fact.getIndex() == 0) {
					try {
						MathNode nextOp = fact.getNextSibling();
						if (Type.Operation.equals(nextOp.getType())) {
							nextOp.remove();
						}
					} catch (IndexOutOfBoundsException e) {
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

		// Collection<MathNode> factors = likeTerms.values();
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

		if (isInEasyMode) {
			factorWithBase(other, exponential);
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
	private static void factorWithBase(MathNode other, MathNode exponential) {
		RootPanel.get().add(new HTML("factor with base"));

		MathNode inBinomialB = exponential;
		MathNode inBinomialA = other.getParent().add(other.getIndex(),
				Type.Number, "1");

		LinkedList<MathNode> factors = new LinkedList<MathNode>();
		factors.add(other);
		factor(factors, inBinomialA, inBinomialB);

		MathNode exp = exponential.getChildAt(1).encase(Type.Sum);
		exp.add(Type.Operation, Operator.MINUS.getSign());
		exp.add(Type.Number, "1");

		
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
				if (isInEasyMode) {
					factorWithTermChild(other, termContainer, termChild);
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
	private static void factorWithTermChild(MathNode other, MathNode term,
			MathNode termChild) {
		RootPanel.get().add(new HTML("factor with term child"));

		MathNode inBinomialA = other;
		MathNode inBinomialB = term;

		if (termChild.getIndex() == 0) {
			termChild.getNextSibling().remove();
		} else {
			termChild.getPrevSibling().remove();
		}

		// Idea: factor a single multiple of a base, other must be base

		// if (Type.Exponential.equals(other.getParentType())) {
		// inBinomialA = other.getParent();
		// MathNode exp = other.getNextSibling().encase(Type.Sum);
		// exp.add(Type.Operation, Operator.MINUS.getSign());
		// exp.add(Type.Number, "1");
		// } else {
		inBinomialA = other.getParent().add(other.getIndex(), Type.Number, "1");
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

		if (isInEasyMode) {
			addFractions(left, right);
		}

		return true;

	}

	private static void addFractions(MathNode left, MathNode right) {

		MathNode numeratorCasing = right.getChildAt(0).encase(Type.Sum);
		numeratorCasing.add(0, operation);
		numeratorCasing.add(0, left.getChildAt(0));

		left.remove();
		parent.decase();
		
		Moderator.reloadEquationPanel("Add Fractions");
	}

	private static boolean addSimilar_check(MathNode left, MathNode right) {

		if (!left.isLike(right)) {
			return false;
		}

		if (isInEasyMode) {
			addSimilar(left, right);
		}
		return true;
	}

	private static void addSimilar(MathNode left, MathNode right) {

		if (isPlus) {
			MathNode casing = right.encase(Type.Term);
			casing.add(0, Type.Operation, Operator.getMultiply().getSign());
			casing.add(0, Type.Number, "2");
		} else {
			right.remove();
		}

		left.remove();
		operation.remove();
		parent.decase();
		
		Moderator.reloadEquationPanel("Add similar");
	}

	private static void addNumbers(MathNode left, MathNode right) {

		BigDecimal leftValue = new BigDecimal(left.getSymbol());
		BigDecimal rightValue = new BigDecimal(right.getSymbol());

		if (isPlus) {
			rightValue = leftValue.add(rightValue);
		} else {
			rightValue = leftValue.subtract(rightValue);
		}
		right.setSymbol(rightValue.stripTrailingZeros().toString());

		left.remove();
		operation.remove();
		parent.decase();
		
		Moderator.reloadEquationPanel("Add Numbers");
	}

	private static void addZero(MathNode other, MathNode zero) {
			
		try {
			MathNode zeroOperation = zero.getPrevSibling();
			if (Type.Operation.equals(zeroOperation.getType())) {
				zero.getPrevSibling().remove();
			}
		} catch (IndexOutOfBoundsException e) {
		}

		zero.remove();

		parent.decase();
		
		Moderator.reloadEquationPanel("Add zero");
	}

	// private static void termClean(MathNode term) {
	// if (term.getChildCount() == 1) {
	// term.getParent().add(term.getIndex(), term.getFirstChild());
	// term.remove();
	// }
	// }

	// private static void parentClean() {
	//
	// if (parent.getChildCount() == 1) {
	// grandParent.add(parent.getIndex(), parent.getFirstChild());
	// parent.remove();
	// }
	// }

	public static void deployTestBot() throws Exception {
		String sum = "<mfenced separators=\"\" open=\"\" close=\"\"><mi>s</mi><mo>+</mo><mi>u</mi><mo>+</mo><mrow><mi>m</mi><mo>\u00B7</mo><mi>m</mi></mrow></mfenced>";
		String term = "<mrow><mi>a</mi><mo>\u00B7</mo><msup><mi>a</mi><mi>a</mi></msup><mo>\u00B7</mo><mi>a</mi></mrow>";
		String exp = "<msup><mi>a</mi><mi>a</mi></msup>";
		String frac = "<mfrac><mrow><mi>a</mi><mo>\u00B7</mo><mi>a</mi><mo>\u00B7</mo><mi>a</mi></mrow><mrow><mi>d</mi><mo>\u00B7</mo><mi>e</mi><mo>\u00B7</mo><mi>n</mi></mrow></mfrac>";
		String var = "<mi>a</mi>";
		String num = "<mn>7</mn>";

		String a = "<mn>0</mn>";
		String b = "<mn>7.333</mn>";
		String c = "<mn>-7</mn>";
		String d = "<mn>-7.33</mn>";

		String fracA = "<mfrac><mrow><mi>a</mi><mo>\u00B7</mo><mi>n</mi><mo>\u00B7</mo><mi>a</mi></mrow><mrow><mi>d</mi><mo>\u00B7</mo><mi>e</mi><mo>\u00B7</mo><mi>n</mi></mrow></mfrac>";
		String fracB = "<mfrac><mrow><mi>a</mi><mo>\u00B7</mo><mi>a</mi><mo>\u00B7</mo><mi>a</mi></mrow><mrow><mi>d</mi><mo>\u00B7</mo><mi>n</mi><mo>\u00B7</mo><mi>e</mi></mrow></mfrac>";

		String allTerm = "<mrow>" + exp + "<mo>\u00B7</mo>" + frac
				+ "<mo>\u00B7</mo>" + var + "<mo>\u00B7</mo>" + num + "</mrow>";
		String allTermm = "<mrow>" + exp + "<mo>\u00B7</mo>" + frac
				+ "<mo>\u00B7</mo>" + var + "<mo>\u00B7</mo>" + var + "</mrow>";
		// String baseTest = "<msup>" + type + "<mn>2</mn></msup>";

		// String[] types = { a, b, c, d };
		// String[] types = { term, exp, frac, var, num };
		String[] types = {term, exp, frac, var, num };
		 for (String type : types) {
//		 for (String tipe : types) {

		String leftTest = a;
		String rightTest = type;

		botCase(leftTest, rightTest);
		 }
//		 }

	}
	
	private static void botCase(String leftTest, String rightTest) throws Exception{

		Element leftElement = (Element) new HTML(leftTest).getElement()
				.getFirstChildElement().cloneNode(true);
		Element multiplyElement = (Element) new HTML("<mo>+</mo>").getElement()
				.getFirstChildElement().cloneNode(true);
		Element rightElement = (Element) new HTML(rightTest).getElement()
				.getFirstChildElement().cloneNode(true);

		HTML disp = new HTML(
				"<math><mpadded class=\"parentDummy\"></mpadded><mo>=</mo><mi>inEquation</mi></math>");
		Element mathElement = disp.getElement().getFirstChildElement();
		Element parentElement = new HTML(
				"<mfenced separators=\"\" open=\"\" close=\"\"><mi>L</mi><mo>( </mo><mo> )</mo><mi>R</mi></mfenced>")
				.getElement().getFirstChildElement();
		Element parentdummy = mathElement.getElementsByTagName("mpadded")
				.getItem(0);
		Element grandParentElement = parentdummy.getParentElement();
		grandParentElement.insertBefore(parentElement, parentdummy);
		parentdummy.removeFromParent();

		parentElement.insertAfter(leftElement, parentElement
				.getElementsByTagName("mo").getItem(0));
		parentElement.insertAfter(multiplyElement, leftElement);
		parentElement.insertAfter(rightElement, multiplyElement);

		MathTree mathTree = new MathTree(mathElement, false);
		MathNode leftNode = mathTree.idMap.get(leftElement.getAttribute("id"));
		MathNode rightNode = mathTree.idMap
				.get(rightElement.getAttribute("id"));
		operation = mathTree.idMap.get(multiplyElement.getAttribute("id"));
		parent = operation.getParent();
		grandParent = parent.getParent();

		isPlus = Operator.PLUS.getSign().equals(operation.getSymbol());

		if (!isPlus && !Operator.MINUS.getSign().equals(operation.getSymbol())) {
			throw new Exception(
					"Sign is neither Operator.MINUS or Operator.PLUS: "
							+ operation.toString());
		}

		HTML dispBefore = new HTML(disp.getElement().getFirstChildElement()
				.getString());

		RootPanel.get().add(dispBefore);
		 assign(leftNode, operation, rightNode, isPlus);
//		factorLikeTerms_check(leftNode, rightNobotCasebotCasebotCasebotCasede);
		RootPanel.get().add(
				new HTML(disp.getElement().getFirstChildElement().getString()));
		RootPanel.get().add(new HTML("&nbsp;"));
	}
}
