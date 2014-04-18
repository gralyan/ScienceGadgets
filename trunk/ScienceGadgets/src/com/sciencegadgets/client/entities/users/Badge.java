package com.sciencegadgets.client.entities.users;

import java.util.HashSet;

import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.shared.TypeSGET;

public enum Badge {
	BOTH_SIDES_ADD(Skill.SOLVING_EQUATIONS_ADD,1), //
	BOTH_SIDES_SUBTRACT(Skill.SOLVING_EQUATIONS_SUBTRACT,1), //
	BOTH_SIDES_MULTIPLY(Skill.SOLVING_EQUATIONS_MULTIPLY,1), //
	BOTH_SIDES_DIVIDE(Skill.SOLVING_EQUATIONS_DIVIDE,1), //
	BOTH_SIDES_INVERSE_EXPONENT(Skill.SOLVING_EQUATIONS_EXPONENTIAL,1), //
	BOTH_SIDES_RAISE(Skill.SOLVING_EQUATIONS_RAISE,1), //
	BOTH_SIDES_LOG(Skill.SOLVING_EQUATIONS_LOG,1), //
	BOTH_SIDES_INVERSE_TRIG(Skill.SOLVING_EQUATIONS_INVERSE_TRIG,1), //
	//
	ADD_ZERO(Skill.ADDITION_WITH_ZERO, 5),
	ADD_NUMBERS_10(Skill.ADD_NUMBERS_TO_10, 10),
	ADD_NUMBERS_100(Skill.ADD_NUMBERS_TO_100, 10),
	ADD_NUMBERS_LARGE(Skill.ADD_NUMBERS_LARGE, 10),
	ADD_NUMBERS_NEGATIVE(Skill.ADDITION_WITH_NEGATIVES, 50),
	SUBTRACTION(Skill.SUBTRACTION, 10),
	MULTIPLY_NUMBERS_10(Skill.MULTIPLICATION, 10),
	MULTIPLY_NUMBERS_100(Skill.MULTIPLICATION, 100),
	DIVIDE_NUMBERS(Skill.DIVISION, 20),
	EXPONENTIATE_NUMBERS(Skill.EVALUATING_EXPONENTS, 10),
	//
	BOTH_SIDES_DIVIDE_INTO_DENOMINATOR(Skill.SOLVING_EQUATIONS_DIVIDE,1), //
	DENIMINATOR_FLIP_MULTIPLY(Skill.DIVIDING_FRACTIONS,1), //
	COMBINE_LIKE_TERMS(Skill.COMBINING_LIKE_TERMS,1), //
	EXP_ONE(Skill.EXPANDED_EXPONENTIAL,1), //
	EXP_DROP_ARITHMETIC(Skill.DIVIDING_EXPONENTIALS,1), //
	MULTIPLY_WITH_FRACTION(Skill.MULTIPLYING_WITH_FRACTIONs,1), //
	MULTIPLY_COMBINE_BASES(Skill.MULTIPLY_SIMILAR_BASES,1),
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
			if(badge.getSkill().equals(skill) && badge.getSkillLevelRequired() < level) {
				badgesEarned.add(badge);
			}
		}
		return badgesEarned;
	}
}
