package com.sciencegadgets.client.algebra.transformations;

import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class TestBot_Transformations {

	public void testAddition() {
		MathTree mTree = new MathTree(TypeML.Sum, "", TypeML.Sum, "", false);
		MathNode leftSide = mTree.getLeftSide();
		MathNode rightSide = mTree.getRightSide();
		
		

		MathNode number1 = mTree.NEW_NODE(TypeML.Number, "5");

		MathNode number2 = mTree.NEW_NODE(TypeML.Number, "7");

		MathNode var1 = mTree.NEW_NODE(TypeML.Variable, "x");

		MathNode var2 = mTree.NEW_NODE(TypeML.Variable, "y");

		MathNode termNumVar = mTree.NEW_NODE(TypeML.Term, "");
		termNumVar.append(TypeML.Number, "2");
		termNumVar.append(TypeML.Operation, Operator.getMultiply().getSign());
		termNumVar.append(TypeML.Variable, "a");

		MathNode termVars = mTree.NEW_NODE(TypeML.Term, "");
		termVars.append(TypeML.Variable, "a");
		termVars.append(TypeML.Operation, Operator.getMultiply().getSign());
		termVars.append(TypeML.Variable, "b");

		MathNode sumPlus = mTree.NEW_NODE(TypeML.Sum, "");
		sumPlus.append(TypeML.Number, "2");
		sumPlus.append(TypeML.Operation, Operator.PLUS.getSign());
		sumPlus.append(TypeML.Variable, "a");

		MathNode sumMinus = mTree.NEW_NODE(TypeML.Sum, "");
		sumMinus.append(TypeML.Number, "2");
		sumMinus.append(TypeML.Operation, Operator.MINUS.getSign());
		sumMinus.append(TypeML.Variable, "a");

		MathNode frac1 = mTree.NEW_NODE(TypeML.Fraction, "");
		frac1.append(TypeML.Variable, "numerator1");
		frac1.append(TypeML.Variable, "denominator1");

		MathNode frac2 = mTree.NEW_NODE(TypeML.Fraction, "");
		frac2.append(TypeML.Variable, "numerator2");
		frac2.append(TypeML.Variable, "denominator2");

		MathNode exp1 = mTree.NEW_NODE(TypeML.Exponential, "");
		exp1.append(TypeML.Variable, "base1");
		exp1.append(TypeML.Variable, "exp1");

		MathNode exp2 = mTree.NEW_NODE(TypeML.Exponential, "");
		exp2.append(TypeML.Variable, "base2");
		exp2.append(TypeML.Variable, "exp2");

		MathNode log1 = mTree.NEW_NODE(TypeML.Log, "10");
		log1.append(TypeML.Variable, "arg1");

		MathNode log2 = mTree.NEW_NODE(TypeML.Log, "2");
		log2.append(TypeML.Variable, "arg1");

		MathNode log3 = mTree.NEW_NODE(TypeML.Log, "10");
		log3.append(TypeML.Variable, "arg3");

		MathNode trig1 = mTree.NEW_NODE(TypeML.Trig,
				TrigFunctions.sin.toString());
		trig1.append(TypeML.Variable, "arg1");

		MathNode trig2 = mTree.NEW_NODE(TypeML.Trig,
				TrigFunctions.cos.toString());
		trig2.append(TypeML.Variable, "arg1");

		MathNode trig3 = mTree.NEW_NODE(TypeML.Trig,
				TrigFunctions.sin.toString());
		trig3.append(TypeML.Variable, "arg3");

		// leftSide.append(TypeML., symbol);
		//
		// AdditionTransformations addT = new AdditionTransformations(left,
		// operation, right, isPlusSign);
	}

}
