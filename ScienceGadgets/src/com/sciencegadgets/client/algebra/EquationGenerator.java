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
package com.sciencegadgets.client.algebra;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.google.gwt.user.client.Random;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class EquationGenerator {

	public static EquationTree GENERATE(//
			LinkedHashMap<TypeSGET, Integer> expressionsVariableSide,//
			LinkedHashMap<TypeSGET, Integer> expressionsOtherSide,//
			boolean mustBeWholeAnswer, //
			boolean mustBePositives, //
			int minAdd,//
			int minMultiply,//
			int minFraction,//
			int minExp,//

			int maxAdd,//
			int maxMultiply,//
			int maxFraction,//
			int maxExp) {

		EquationTree eTree = new EquationTree(false);
		EquationNode var = eTree.getLeftSide().replace(TypeSGET.Variable, "a");
		EquationNode other = eTree.getRightSide().replace(TypeSGET.Number, "1");

		GERERATE_SIDE(var, expressionsVariableSide,//
				mustBeWholeAnswer, //
				mustBePositives, //
				minAdd,//
				minMultiply,//
				minFraction,//
				minExp,//
				maxAdd,//
				maxMultiply,//
				maxFraction,//
				maxExp);
		GERERATE_SIDE(other, expressionsOtherSide,//
				mustBeWholeAnswer, //
				mustBePositives, //
				minAdd,//
				minMultiply,//
				minFraction,//
				minExp,//
				maxAdd,//
				maxMultiply,//
				maxFraction,//
				maxExp);

		int maxOtherSeed = maxMultiply;
		switch (other.getParentType()) {
		case Sum:
			maxOtherSeed = maxAdd;
			break;
		case Term:
			maxOtherSeed = maxAdd;
			break;
		case Exponential:
			maxOtherSeed = maxExp;
			break;
		case Fraction:
			maxOtherSeed = maxFraction;
			break;
		default:
			break;
		}
		other.setSymbol(Random.nextInt(maxOtherSeed) + "");

		eTree.validateTree();

		return eTree;
	}

	private static void GERERATE_SIDE(EquationNode side,//
			LinkedHashMap<TypeSGET, Integer> expressions,//
			boolean mustBeWholeAnswer, //
			boolean mustBePositives, //
			int minAdd,//
			int minMultiply,//
			int minFraction,//
			int minExp,//
			int maxAdd,//
			int maxMultiply,//
			int maxFraction,//
			int maxExp) {

		HashSet<TypeSGET> toRemove = new HashSet<TypeSGET>();

		for (Entry<TypeSGET, Integer> entry : expressions.entrySet()) {
			if (entry.getValue() == 0) {
				toRemove.add(entry.getKey());
			}
		}
		for (TypeSGET type : toRemove) {
			expressions.remove(type);
		}

		while (!expressions.isEmpty()) {
			int index = Random.nextInt(expressions.size());
			TypeSGET[] array = expressions.keySet().toArray(
					new TypeSGET[expressions.size()]);
			TypeSGET type = array[index];

			switch (type) {
			case Sum:
				int valueAdd = Random.nextInt(maxAdd-minAdd)+minAdd;
				if (!mustBePositives && Random.nextBoolean()) {
					valueAdd *= -1;
				}
				side = ADD_SUB(side, valueAdd, mustBePositives);
				break;
			case Term:
				int valueMultiply = Random.nextInt(maxMultiply-minMultiply)+minMultiply;
				if (!mustBePositives && Random.nextBoolean()) {
					valueMultiply *= -1;
				}
				side = MULTIPLY(side, valueMultiply);
				break;
			case Fraction:
				side = FRACTION(side, mustBeWholeAnswer, maxMultiply, minFraction,
						maxFraction);
				break;
			case Exponential:
				side = EXP(side, minExp, maxExp);
				break;
			case Trig:
				side = TRIG(side);
				break;
			case Log:
				side = LOG(side);
				break;
			}

			int expressionCount = expressions.get(type) - 1;
			if (expressionCount > 0) {
				expressions.put(type, expressionCount);
			} else {
				expressions.remove(type);
			}
		}
	}

	private static EquationNode MULTIPLY(EquationNode node, int value) {
		// int value = random.nextInt(maxMultiply);
		EquationNode term;
		if (TypeSGET.Fraction.equals(node.getType())) {
			term = node.getFirstChild().encase(TypeSGET.Term);
		} else {
			node = term = node.encase(TypeSGET.Term);
		}
		term.append(TypeSGET.Operation, Operator.getMultiply().getSign());
		term.append(TypeSGET.Number, value + "");
		return node;
	}

	private static EquationNode FRACTION(EquationNode node,
			boolean mustBeWholeAnswer, int maxMultiply,int minFraction, int maxFraction) {

		int smaller = Random.nextInt(maxMultiply) + 1+minFraction;
		int bigger = smaller * Random.nextInt(maxFraction)+2;

		int numeratorValue, denominatorValue;
		if (!mustBeWholeAnswer && Random.nextBoolean()) {
			numeratorValue = smaller;
			denominatorValue = bigger;
		} else {
			numeratorValue = bigger;
			denominatorValue = smaller;
		}

		EquationNode fraction;
		if (TypeSGET.Fraction.equals(node.getType())) {
			fraction = node;
			MULTIPLY(fraction.getChildAt(0), numeratorValue);
			MULTIPLY(fraction.getChildAt(1), denominatorValue);
		} else {
			node = MULTIPLY(node, numeratorValue);
			fraction = node.encase(TypeSGET.Fraction);
			fraction.append(TypeSGET.Number, denominatorValue + "");
		}
		return fraction;
	}

	private static EquationNode ADD_SUB(EquationNode node, int value,
			boolean mustBePositives) {
		Operator op = !mustBePositives && Random.nextBoolean() ? Operator.MINUS
				: Operator.PLUS;

		node = node.encase(TypeSGET.Sum);
		node.append(TypeSGET.Operation, op.getSign());
		node.append(TypeSGET.Number, value + "");
		return node;
	}

	private static EquationNode EXP(EquationNode node, int minExp, int maxExp) {
		node = node.encase(TypeSGET.Exponential);
		node.append(TypeSGET.Number, Random.nextInt(maxExp-minExp)+minExp + "");
		return node;
	}

	private static EquationNode TRIG(EquationNode node) {

		int rand6 = Random.nextInt(6);

		TrigFunctions function = TrigFunctions.sin;
		switch (rand6) {
		// case 0 is initial, sin
		case 1:
			function = TrigFunctions.cos;
			break;
		case 2:
			function = TrigFunctions.tan;
			break;
		case 3:
			function = TrigFunctions.csc;
			break;
		case 4:
			function = TrigFunctions.sec;
			break;
		case 5:
			function = TrigFunctions.cot;
			break;
		}

		node = node.encase(TypeSGET.Trig);
		node.setAttribute(MathAttribute.Function, function.toString());
		return node;
	}

	private static EquationNode LOG(EquationNode node) {

		int rand3 = Random.nextInt(3);

		String logBase = "10";
		switch (rand3) {
		// case 0 is initial, "10"
		case 1:
			logBase = "2";
			break;
		case 2:
			logBase = "e";
			break;
		}

		node = node.encase(TypeSGET.Log);
		node.setAttribute(MathAttribute.LogBase, logBase);
		return node;
	}

}
