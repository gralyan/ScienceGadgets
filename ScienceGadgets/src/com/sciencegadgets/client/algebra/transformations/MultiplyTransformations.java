package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.Type.Operator;

public class MultiplyTransformations {
	private static MathNode operation;
	private static MathNode parent;
	private static MathNode grandParent;
	private static final int same = 0;

	public static void assign(MathNode left, MathNode multiplyNode,
			MathNode right) {
		operation = multiplyNode;
		parent = multiplyNode.getParent();
		grandParent = parent.getParent();

		Type leftType = left.getType();
		Type rightType = right.getType();
		
		//Check for improper types for left and right
		switch (leftType) {
		case Aesthetic:
		case Equation:
		case Term:
			JSNICalls
			.error("Illegal node within Term: " + leftType);
			break;
		case Operation:
			JSNICalls.error("Operation in wrong place: "
					+ left.getParent().toString());
			break;
		case Number:
			BigDecimal leftValue = new BigDecimal(left.getSymbol());
			if(leftValue.compareTo(new BigDecimal(0)) == same){
				multiplyZero(right, left);
				return;
			}else if(leftValue.compareTo(new BigDecimal(1)) == same){
				multiplyOne(right, left);
				return;
			}else if(leftValue.compareTo(new BigDecimal(-1)) == same){
				multiplyNegOne(right, left);
				return;
			}
			break;
		}
		switch (rightType) {
		case Aesthetic:
		case Equation:
		case Term:
			JSNICalls
			.error("Illegal node within Term: " + rightType);
			break;
		case Operation:
			JSNICalls.error("Operation in wrong place: "
					+ right.getParent().toString());
			break;
		case Number:
			BigDecimal rightValue = new BigDecimal(right.getSymbol());
			if(rightValue.compareTo(new BigDecimal(0)) == same){
				multiplyZero(left, right);
				return;
			}else if(rightValue.compareTo(new BigDecimal(1)) == same){
				multiplyOne(left, right);
				return;
			}else if(rightValue.compareTo(new BigDecimal(-1)) == same){
				multiplyNegOne(left, right);
				return;
			}
			break;
		}

		//Assignments in 2d switch (leftType, rightType)
		first: switch (leftType) {
		case Sum:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Exponential:
			case Fraction:
			case Variable:
			case Number:
				multiplyDistribution(right, left, true);
				break second;
			}
			break first;
		case Exponential:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Exponential:
				multiplyCombineBases(left, right);
				break second;
			case Fraction:
				multiplyWithFraction(left, right, false);
				break second;
			case Variable:
			case Number:
				multiplyCombineBases(left, right);
				break second;
			}
			break first;
		case Fraction:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Fraction:
				multiplyFractions(left, right);
				break second;
			case Exponential:
			case Variable:
			case Number:
				multiplyWithFraction(right, left, true);
				break second;
			}
			break first;
		case Variable:
			if (Type.Variable.equals(rightType)) {
				multiplyCombineBases(left, right);
				break first;
			} else if (Type.Number.equals(rightType)) {
				multiplyOperationToSpace();
				break first;
			}
			//fall through
		case Number:
			second: switch (rightType) {
			case Sum:
				multiplyDistribution(left, right, false);
				break second;
			case Exponential:
				multiplyCombineBases(left, right);
				break second;
			case Fraction:
				multiplyWithFraction(left, right, false);
				break second;
			case Variable:
				multiplyOperationToSpace();
				break second;
			case Number:
				multiplyNumbers(left, right);
				break second;
			}
			break first;
		}
	}

	private static void multiplyDistribution(MathNode dist, MathNode sum,
			boolean reversed) {

		for (MathNode sumChild : sum.getChildren()) {
			if (!Type.Operation.equals(sumChild.getType())) {

				MathNode sumChildCasings = sumChild.encase(Type.Term);

				int index = reversed ? -1 : 0;
				sumChildCasings.add(index, operation.getType(),
						operation.getSymbol());
				sumChildCasings.add(index, dist.clone());
			}
		}

		dist.remove();
		operation.remove();

		parent.decase();
		
		Moderator.reloadEquationPanel("Distribute");
	}

	private static boolean multiplyCombineExponents(MathNode left,
			MathNode right) {

		if (!left.getChildAt(1).isLike(right.getChildAt(1))) {
			return false;
		}

		MathNode leftBase = left.getChildAt(0);
		MathNode rightBase = right.getChildAt(0);

		MathNode rightCasing = rightBase.encase(Type.Term);

		rightCasing.add(0, operation);
		rightCasing.add(0, leftBase);

		left.remove();

		parent.decase();

		Moderator.reloadEquationPanel("Combine Exponents");
		return true;
	}

	private static boolean multiplyCombineBases(MathNode left, MathNode right) {

		// May not already be in exponent eg. a = a^1
		MathNode leftBase;
		if (Type.Exponential.equals(left.getType())) {
			leftBase = left.getChildAt(0);
		} else {
			leftBase = left;
		}
		MathNode rightBase;
		if (Type.Exponential.equals(right.getType())) {
			rightBase = right.getChildAt(0);
		} else {
			rightBase = right;
		}

		if (!leftBase.isLike(rightBase)) {
			// TODO only in automatic mode
			if (Type.Exponential.equals(left.getType())
					&& Type.Exponential.equals(right.getType())) {
				multiplyCombineExponents(left, right);
			} else if (Type.Variable.equals(left.getType())
					&& Type.Variable.equals(right.getType())) {
				multiplyOperationToSpace();
			}
			return false;
		} else {
			if (!Type.Exponential.equals(left.getType())) {
				left = left.encase(Type.Exponential);
				left.add(Type.Number, "1");
			}
			if (!Type.Exponential.equals(right.getType())) {
				right = right.encase(Type.Exponential);
				right.add(Type.Number, "1");
			}
		}

		MathNode leftExp = left.getChildAt(1);
		MathNode rightExp = right.getChildAt(1);

		MathNode rightCasing = rightExp.encase(Type.Sum);

		rightCasing.add(0, Type.Operation, Operator.PLUS.getSign());
		rightCasing.add(0, leftExp);

		left.remove();
		operation.remove();

		parent.decase();

		Moderator.reloadEquationPanel("Combine Bases");
		return true;
	}

	private static void multiplyWithFraction(MathNode nonFrac,
			MathNode fraction, boolean reversed) {
		MathNode numerator = fraction.getChildAt(0);
		numerator = numerator.encase(Type.Term);

		int index = reversed ? -1 : 0;
		numerator.add(index, operation);
		numerator.add(index, nonFrac);

		parent.decase();
		
		Moderator.reloadEquationPanel("Multiply with Fraction");
	}

	private static void multiplyFractions(MathNode left, MathNode right) {
		MathNode numerator = right.getChildAt(0);
		numerator = numerator.encase(Type.Term);
		numerator.add(0, operation);
		numerator.add(0, left.getChildAt(0));

		MathNode denominator = right.getChildAt(1);
		denominator = denominator.encase(Type.Term);
		denominator.add(0, Type.Operation, operation.getSymbol());
		denominator.add(0, left.getChildAt(1));

		left.remove();

		parent.decase();

		Moderator.reloadEquationPanel("Multiply Fractions");
	}

	private static void multiplyOperationToSpace() {
		operation.setSymbol(Operator.SPACE.getSign());

		Moderator.reloadEquationPanel("Op to Space");
	}

	private static void multiplyNumbers(MathNode left, MathNode right) {

		BigDecimal leftValue = new BigDecimal(left.getSymbol());
		BigDecimal rightValue = new BigDecimal(right.getSymbol());

		BigDecimal productValue = leftValue.multiply(rightValue);

		right.setSymbol(productValue.toString());

		left.remove();
		operation.remove();

		parent.decase();

		Moderator.reloadEquationPanel(leftValue.toString()+" x "+rightValue.toString()+" = "+productValue.toString());
	}
	
	private static void multiplyZero(MathNode other, MathNode zero){
		zero.remove();
		operation.remove();
		other.remove();
		
		parent.decase();

		Moderator.reloadEquationPanel("a x 0 = 0");
	}
	private static void multiplyOne(MathNode other, MathNode one){
		one.remove();
		operation.remove();
		
		parent.decase();
		
		Moderator.reloadEquationPanel("a x 1 = a");
	}

	private static void multiplyNegOne(MathNode other, MathNode negOne){
		other.remove();
		operation.remove();
		negOne.remove();
		AlgebraicTransformations.propagateNegative(other);
		
		parent.decase();
		
		Moderator.reloadEquationPanel("a x -1 = -a");
	}

//	private static void parentClean() {
//
//		if (parent.getChildCount() == 1) {
//			grandParent.add(parent.getIndex(), parent.getFirstChild());
//			parent.remove();
//		}
//	}

	// public static void deployTestBot() throws TopNodesNotFoundException {
	// String sum =
	// "<mfenced separators=\"\" open=\"\" close=\"\"><mi>s</mi><mo>+</mo><mi>u</mi><mo>+</mo><mrow><mi>m</mi><mo>\u00B7</mo><mi>m</mi></mrow></mfenced>";
	// String term =
	// "<mrow><mi>t</mi><mo>\u00B7</mo><msup><mi>e</mi><mi>r</mi></msup><mo>\u00A0</mo><mi>m</mi></mrow>";
	// String exp =
	// "<msup><mrow><mi>e</mi><mo>\u00B7</mo><mi>x</mi></mrow><mi>p</mi></msup>";
	// String frac =
	// "<mfrac><mrow><mi>n</mi><mo>\u00B7</mo><mi>u</mi><mo>\u00B7</mo><mi>m</mi></mrow><mrow><mi>d</mi><mo>\u00B7</mo><mi>e</mi><mo>\u00B7</mo><mi>n</mi></mrow></mfrac>";
	// String var = "<mi>v</mi>";
	// String num = "<mn>2</mn>";
	//
	// String expA =
	// "<msup><mi>a</mi><mfenced><mi>s</mi><mo>+</mo><mi>u</mi><mo>+</mo><mrow><mi>m</mi><mo>\u00B7</mo><mi>m</mi></mrow></mfenced></msup>";
	// String expB =
	// "<msup><mi>a</mi><mfenced><mi>u</mi><mo>+</mo><mrow><mi>m</mi><mo>\u00B7</mo><mi>m</mi></mrow><mo>+</mo><mi>s</mi></mfenced></msup>";
	// String expC =
	// "<msup><mi>a</mi><mfenced><mi>m</mi><mo>+</mo><mrow><mi>u</mi><mo>\u00B7</mo><mi>m</mi></mrow><mo>+</mo><mi>s</mi></mfenced></msup>";
	//
	// String[] types = { expA, expB, expC};
	// // String[] types = { sum, exp, frac, var, num };
	// for (String type : types) {
	// // for (String tipe : types) {
	//
	// Element leftElement = (Element) new HTML(expA).getElement()
	// .getFirstChildElement().cloneNode(true);
	// Element multiplyElement = (Element) new HTML(
	// "<mo> \u00D7 </mo>").getElement()
	// .getFirstChildElement().cloneNode(true);
	// Element rightElement = (Element) new HTML(type).getElement()
	// .getFirstChildElement().cloneNode(true);
	//
	// HTML disp = new HTML(
	// "<math><mpadded class=\"parentDummy\"></mpadded><mo>=</mo><mi>inEquation</mi></math>");
	// Element mathElement = disp.getElement().getFirstChildElement();
	// Element parentElement = new HTML(
	// "<mrow><mi>L</mi><mo>( </mo><mo> )</mo><mi>R</mi></mrow>")
	// .getElement().getFirstChildElement();
	// Element parentdummy = mathElement.getElementsByTagName(
	// "mpadded").getItem(0);
	// Element grandParentElement = parentdummy.getParentElement();
	// grandParentElement.insertBefore(parentElement, parentdummy);
	// parentdummy.removeFromParent();
	//
	// parentElement.insertAfter(leftElement, parentElement
	// .getElementsByTagName("mo").getItem(0));
	// parentElement.insertAfter(multiplyElement, leftElement);
	// parentElement.insertAfter(rightElement, multiplyElement);
	//
	// MathTree mathTree = new MathTree(mathElement, false);
	//
	// MathNode leftNode = mathTree.idMap.get(leftElement
	// .getAttribute("id"));
	// MathNode rightNode = mathTree.idMap.get(rightElement
	// .getAttribute("id"));
	// multiply = mathTree.idMap.get(multiplyElement
	// .getAttribute("id"));
	// parent = multiply.getParent();
	// grandParent = parent.getParent();
	//
	// HTML dispBefore = new HTML(disp.getElement()
	// .getFirstChildElement().getString());
	//
	// // System.out.println("grandParent\t: " + grandParent.toString());
	// // System.out.println("parent\t: " + parent.toString());
	// // System.out.println("leftNode\t: " + leftNode.toString());
	// // System.out.println("multiply\t: " + multiply.toString());
	// // System.out.println("rightNode\t: " + rightNode.toString());
	//
	// RootPanel.get().add(new HTML("before"));
	// RootPanel.get().add(dispBefore);
	// // System.out.println("before");
	// // System.out.println(dispBefore.getElement()
	// // .getFirstChildElement().getString());
	// assign(leftNode, multiply, rightNode);
	// RootPanel.get().add(new HTML("after"));
	// RootPanel.get().add(
	// new HTML(disp.getElement().getFirstChildElement()
	// .getString()));
	// RootPanel.get().add(new HTML("&nbsp;"));
	// // System.out.println("after");
	// // System.out.println(disp.getElement().getFirstChildElement()
	// // .getString());
	//
	// // }
	// }
	//
	// }
}
