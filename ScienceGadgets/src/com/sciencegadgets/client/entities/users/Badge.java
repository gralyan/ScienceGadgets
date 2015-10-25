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
package com.sciencegadgets.client.entities.users;

import java.util.HashSet;

import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.shared.TypeSGET;

public enum Badge {

	// Evaluate
	FACTOR_NUMBERS(Skill.FACTOR_NUMBERS, 1), //
	DIVIDE_NUMBERS(Skill.DIVIDE_NUMBERS, 1), //
	EXPONENTIATE_NUMBERS(Skill.EXPONENTIATE_NUMBERS, 1), //

	// DROP
	DROP_CANCEL(Skill.DROP_CANCEL, 1), //
	DROP_DIVIDE(Skill.DROP_DIVIDE, 1), //
	DROP_EXPONENTIAL(Skill.DROP_EXPONENTIAL, 1), //
	DROP_LOG(Skill.DROP_LOG, 1), //
	DROP_TRIG(Skill.DROP_TRIG, 1), //
	SIMPLIFY_FRACTIONS(Skill.SIMPLIFY_FRACTIONS, 1),

	// ADD
	ADD(Skill.ADD, 1),//
	ADD_WITH_ZERO(Skill.ADD_WITH_ZERO, 1), //
	ADD_NUMBERS_10(Skill.ADD_NUMBERS_10, 20), //
	ADD_NUMBERS_100(Skill.ADD_NUMBERS_100, 10), //
	ADD_NUMBERS_LARGE(Skill.ADD_NUMBERS_LARGE, 1), //
	SUBTRACTION(Skill.SUBTRACTION, 6), //
	ADDITION_WITH_NEGATIVES(Skill.ADDITION_WITH_NEGATIVES, 6), //
	ADD_FRACTIONS(Skill.ADD_FRACTIONS, 1), //
	COMMON_DENOMINATOR(Skill.COMMON_DENOMINATOR, 1), //
	COMBINE_LIKE_TERMS(Skill.COMBINE_LIKE_TERMS, 1), //
	FACTOR_POLYNOMIAL(Skill.FACTOR_POLYNOMIAL, 1), //
	ADD_LOGS(Skill.ADD_LOGS, 1), //

	// Both Sides
	BOTH_SIDES_ADD(Skill.BOTH_SIDES_ADD, 1), //
	BOTH_SIDES_SUBTRACT(Skill.BOTH_SIDES_SUBTRACT, 1), //
	BOTH_SIDES_MULTIPLY(Skill.BOTH_SIDES_MULTIPLY, 1), //
	BOTH_SIDES_DIVIDE(Skill.BOTH_SIDES_DIVIDE, 1), //
	BOTH_SIDES_INVERSE_EXPONENT(Skill.BOTH_SIDES_INVERSE_EXPONENT, 1), //
	BOTH_SIDES_RAISE(Skill.BOTH_SIDES_RAISE, 1), //
	BOTH_SIDES_LOG(Skill.BOTH_SIDES_LOG, 1), //
	BOTH_SIDES_INVERSE_TRIG(Skill.BOTH_SIDES_INVERSE_TRIG, 1), //

	// Exponential
	EXPONENT_BASE_ZERO(Skill.EXPONENT_BASE_ZERO, 1), //
	EXPONENT_BASE_ONE(Skill.EXPONENT_BASE_ONE, 1), //
	EXPONENT_EXPAND(Skill.EXPONENT_EXPAND, 1), //
	EXPONENT_EXPONENTIATE(Skill.EXPONENT_EXPONENTIATE, 1), //
	EXPONENT_EXPRESSION(Skill.EXPONENT_EXPRESSION, 1), //
	EXPONENT_NEGATIVE(Skill.EXPONENT_NEGATIVE, 1), //
	EXPONENTIAL_INVERSE(Skill.EXPONENTIAL_INVERSE, 1), //

	// Simplify
	MULTIPLY(Skill.MULTIPLY, 1),//
	MULTIPLY_ONE(Skill.MULTIPLY_WITH_ONE, 1), //
	MULTIPLY_ZERO(Skill.MULTIPLY_WITH_ZERO, 1), //
	MULTIPLY_NEGATIVE_ONE(Skill.MULTIPLY_WITH_NEGATIVE_ONE, 1), //
	MULTIPLY_NUMBERS_10(Skill.MULTIPLY_NUMBERS_TO_10, 20), //
	MULTIPLY_NUMBERS_100(Skill.MULTIPLY_NUMBERS_TO_100, 10), //
	MULTIPLY_NUMBERS_LARGE(Skill.MULTIPLY_NUMBERS_LARGE, 1), //
	MULTIPLY_WITH_FRACTION(Skill.MULTIPLY_WITH_FRACTION, 1), //
	MULTIPLY_FRACTIONS(Skill.MULTIPLY_FRACTIONS, 1), //
	MULTIPLY_DISTRIBUTE(Skill.MULTIPLY_DISTRIBUTE, 1), //
	MULTIPLY_LOG_RULE(Skill.MULTIPLY_LOG_RULE, 1), //
	MULTIPLY_COMBINE_BASES(Skill.MULTIPLY_COMBINE_BASES, 1), //
	MULTIPLY_COMBINE_EXPONENTS(Skill.MULTIPLY_COMBINE_EXPONENTS, 1), //
	// //
	LOG_EVALUATE(Skill.LOG_EVALUATE, 1),
	LOG_PRODUCT(Skill.LOG_PRODUCT, 1), //
	LOG_QUOTIENT(Skill.LOG_QUOTIENT, 1), //
	LOG_POWER(Skill.LOG_POWER, 1), //
	LOG_ONE(Skill.LOG_ONE, 1), //
	LOG_SAME_BASE_ARGUMENT(Skill.LOG_SAME_BASE_ARGUMENT, 1), //
	LOG_INVERSE(Skill.LOG_INVERSE, 1), //
	LOG_CHANGE_BASE(Skill.LOG_CHANGE_BASE, 1),
	//
	TRIG_EVALUATE(Skill.TRIG_EVALUATE, 1),
	TRIGONOMETRIC_FUNCTIONS(Skill.TRIGONOMETRIC_FUNCTIONS, 1), //
	TRIG_FUNCTIONS_RECIPROCAL(Skill.TRIG_FUNCTIONS_RECIPROCAL, 1), //
	TRIG_FUNCTIONS_INVERSE(Skill.TRIG_FUNCTIONS_INVERSE, 1), //
	// Specific
	MULTIPLY_COMBINE_BASES_ARITHMETIC(Skill.MULTIPLY_COMBINE_BASES_ARITHMETIC, 1),
	BOTH_SIDES_DIVIDE_INTO_DENOMINATOR(Skill.BOTH_SIDES_DIVIDE, 1), //
	DENIMINATOR_FLIP_MULTIPLY(Skill.DENIMINATOR_FLIP_MULTIPLY, 1), //
	DIVIDING_FRACTIONS(Skill.DIVIDING_FRACTIONS,1),//
	EXP_ONE(Skill.EXPONENT_EXPAND, 1);//
	//

	private Skill skill;
	private int skillLevelRequired;

	private Badge(Skill skill, int skillLevelRequired) {
		this.skill = skill;
		this.skillLevelRequired = skillLevelRequired;
	}

	public Skill getSkill() {
		return skill;
	}

	public int getSkillLevelRequired() {
		return skillLevelRequired;
	}

	public static HashSet<Badge> getRequiredBadges(TypeSGET.Operator operation,
			EquationNode arg0, EquationNode arg1) {

		HashSet<Badge> required = new HashSet<Badge>();

		switch (operation) {
		case MINUS:
			required.add(SUBTRACTION);
			// fall through
		case PLUS:
			// Integer leftValue = Integer.parseInt(arg0.getSymbol());
			// Integer rightValue = Integer.parseInt(arg1.getSymbol());
			// if(leftValue > )
			// required.add(ADD_NUMBERS_10);
			// break;
		case DOT:
		case CROSS:
		case SPACE:

			break;
		case DIVIDE:

			break;
		case POW:

			break;
		}

		return required;
	}

	public static HashSet<Badge> getBadgesEarned(Skill skill, int level) {
		HashSet<Badge> badgesEarned = new HashSet<Badge>();

		for (Badge badge : values()) {
			if (badge.getSkill().equals(skill)
					&& badge.getSkillLevelRequired() <= level) {
				badgesEarned.add(badge);
			}
		}
		return badgesEarned;
	}
}
