package com.sciencegadgets.client.entities.users;

import java.util.HashSet;

import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.shared.TypeSGET;

public enum Badge {
	//Both Sides
	BOTH_SIDES_ADD(Skill.SOLVING_EQUATIONS_ADD,1), //
	BOTH_SIDES_SUBTRACT(Skill.SOLVING_EQUATIONS_SUBTRACT,1), //
	BOTH_SIDES_MULTIPLY(Skill.SOLVING_EQUATIONS_MULTIPLY,1), //
	BOTH_SIDES_DIVIDE(Skill.SOLVING_EQUATIONS_DIVIDE,1), //
	BOTH_SIDES_INVERSE_EXPONENT(Skill.SOLVING_EQUATIONS_EXPONENTIAL,1), //
	BOTH_SIDES_RAISE(Skill.SOLVING_EQUATIONS_RAISE,1), //
	BOTH_SIDES_LOG(Skill.SOLVING_EQUATIONS_LOG,1), //
	BOTH_SIDES_INVERSE_TRIG(Skill.SOLVING_EQUATIONS_INVERSE_TRIG,1), //
	// Simplify
	ADD_ZERO(Skill.ADDITION_WITH_ZERO, 1),
	ADD_NUMBERS_10(Skill.ADD_NUMBERS_TO_10, 2),
	ADD_NUMBERS_100(Skill.ADD_NUMBERS_TO_100, 10),
	ADD_NUMBERS_LARGE(Skill.ADD_NUMBERS_LARGE, 10),
	ADD_NUMBERS_NEGATIVE(Skill.ADDITION_WITH_NEGATIVES, 50),
	SUBTRACTION(Skill.SUBTRACTION, 10),
	COMBINE_LIKE_TERMS(Skill.COMBINING_LIKE_TERMS,1), //
	COMMON_DENOMINATOR(Skill.COMMON_DENOMINATOR, 1),
	ADD_FRACTIONS(Skill.ADDING_FRACTIONS, 1),
	ADD_LOGS(Skill.LOG_ADDITION, 1),
	FACTORIZATION(Skill.FACTORIZATION, 1),
	//
	MULTIPLY_ONE(Skill.MULTIPLY_WITH_ONE, 10),
	MULTIPLY_ZERO(Skill.MULTIPLY_WITH_ZERO, 10),
	MULTIPLY_NEGATIVE_ONE(Skill.MULTIPLY_WITH_NEGATIVE_ONE, 10),
	MULTIPLY_NUMBERS_10(Skill.MULTIPLY_NUMBERS_TO_10, 10),
	MULTIPLY_NUMBERS_100(Skill.MULTIPLY_NUMBERS_TO_100, 100),
	MULTIPLY_NUMBERS_LARGE(Skill.MULTIPLY_NUMBERS_LARGE, 100),
	MULTIPLY_WITH_FRACTION(Skill.MULTIPLYING_WITH_FRACTIONS,1), //
	MULTIPLY_FRACTIONS(Skill.MULTIPLYING_FRACTIONS,1), //
	MULTIPLY_COMBINE_BASES(Skill.MULTIPLY_SIMILAR_BASES,1),
	MULTIPLY_COMBINE_EXPONENTS(Skill.MULTIPLY_SIMILAR_EXPONENTS,1),
	MULTIPLY_DISTRIBUTE(Skill.DISTRIBUTIVE_PROPERTY, 1),
	MULTIPLY_LOG_RULE(Skill.MULTIPLY_WITH_LOG, 1),
//
	DIVIDE_NUMBERS(Skill.DIVISION, 20),
	//
	EXPONENTIATE_NUMBERS(Skill.EVALUATING_EXPONENTS, 10),
	EXPONENT_BASE_ZERO(Skill.EXPONENT_BASE_ZERO, 1),
	EXPONENT_EXPAND(Skill.EXPANDED_EXPONENTIAL, 1),
	EXPONENT_EXPONENTIATE(Skill.EXPONENTIAL_WITH_EXPONENT, 1),
	EXPONENTIAL_EXPRESSION(Skill.EXPRESSIONS_WITH_EXPONENTS, 1),
	EXPONENT_NEGATIVE(Skill.EXPONENT_NEGATIVE, 1),
	EXPONENTIAL_INVERSE(Skill.EXPONENTIAL_INVERSE, 1),
	//
	LOG_PRODUCT(Skill.LOG_PRODUCT, 1),
	LOG_QUOTIENT(Skill.LOG_QUOTIENT, 1),
	LOG_POWER(Skill.LOG_POWER, 1),
	LOG_ONE(Skill.LOG_ONE, 1),
	LOG_SAME_BASE_ARGUMENT(Skill.LOG_SAME_BASE_ARGUMENT, 1),
	LOG_INVERSE(Skill.LOG_INVERSE, 1),
	//
	TRIGONOMETRIC_FUNCTIONS(Skill.TRIGONOMETRIC_FUNCTIONS, 1),
	TRIG_FUNCTIONS_RECIPROCAL(Skill.TRIG_FUNCTIONS_RECIPROCAL, 1),
	TRIG_FUNCTIONS_INVERSE(Skill.TRIG_FUNCTIONS_INVERSE, 1),
	// Specific
	BOTH_SIDES_DIVIDE_INTO_DENOMINATOR(Skill.SOLVING_EQUATIONS_DIVIDE,1), //
	DENIMINATOR_FLIP_MULTIPLY(Skill.DIVIDING_FRACTIONS,1), //
	EXP_ONE(Skill.EXPANDED_EXPONENTIAL,1), //
	EXP_DROP_ARITHMETIC(Skill.DIVIDING_EXPONENTIALS,1), //
	CANCEL(Skill.CANCELLING_FRACTIONS, 10),
	FACTOR_NUMBERS(Skill.FACTORIZATION, 10);
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
			//fall through
		case PLUS:
//			Integer leftValue = Integer.parseInt(arg0.getSymbol());
//			Integer rightValue = Integer.parseInt(arg1.getSymbol());
//			if(leftValue > )
//			required.add(ADD_NUMBERS_10);
//			break;
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
		
		for(Badge badge : values()) {
			if(badge.getSkill().equals(skill) && badge.getSkillLevelRequired() <= level) {
				badgesEarned.add(badge);
			}
		}
		return badgesEarned;
	}
}
