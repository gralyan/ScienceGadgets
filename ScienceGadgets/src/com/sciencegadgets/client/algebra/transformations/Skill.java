package com.sciencegadgets.client.algebra.transformations;

import java.util.HashSet;


	public enum Skill {
		CONVERSION, //
		COMMUNATIVE_PROPERTY, //
		FACTORIZATION, //
		INTEGER_FACTORIZATION,//
		//
		ADDITION,//
		ADDITION_WITH_ZERO(ADDITION),//
		ADD_NUMBERS_TO_10,//
		ADD_NUMBERS_TO_100(ADD_NUMBERS_TO_10),//
		ADD_NUMBERS_LARGE(ADD_NUMBERS_TO_100),//
		SUBTRACTION(ADDITION),//
		ADDITION_WITH_NEGATIVES,//
		COMBINING_LIKE_TERMS(ADDITION), //
		ADDING_FRACTIONS(ADDITION),//
		COMMON_DENOMINATOR,//
		//
		MULTIPLICATION,//
		MULTIPLY_WITH_ZERO,//
		MULTIPLY_WITH_ONE,//
		MULTIPLY_WITH_NEGATIVE_ONE,//
		MULTIPLY_NUMBERS_TO_10,//
		MULTIPLY_NUMBERS_TO_100(MULTIPLY_NUMBERS_TO_10),//
		MULTIPLY_NUMBERS_LARGE(MULTIPLY_NUMBERS_TO_100),//
		MULTIPLYING_FRACTIONS,//
		MULTIPLYING_WITH_FRACTIONS,//
		DISTRIBUTIVE_PROPERTY,//
		MULTIPLY_WITH_LOG,//
		//
		DIVISION,//
		DIVIDING_FRACTIONS,//
		CANCELLING_FRACTIONS,//
		SIMPLIFY_FRACTIONS,//
		//
		EVALUATING_EXPONENTS,//
		DIVIDING_EXPONENTIALS,//
		EXPANDED_EXPONENTIAL,//
		EXPONENTIAL_WITH_EXPONENT,//
		EXPONENT_NEGATIVE,//
		EXPRESSIONS_WITH_EXPONENTS,//
		MULTIPLY_SIMILAR_BASES,//
		MULTIPLY_SIMILAR_EXPONENTS,//
		EXPONENT_BASE_ZERO,//
		EXPONENTIAL_INVERSE,//
		//
		TRIG_FUNCTIONS_INVERSE,//
		TRIG_FUNCTIONS_RECIPROCAL,//
		TRIGONOMETRIC_FUNCTIONS,//
		COMBINE_TRIG,//
		//
		LOG_CHANGE_BASE,//
		LOG_CHANGE_BASE_DIVIDE,//
		LOG_ADDITION,//
		LOG_EVALUATE,//
		LOG_ONE,//
		LOG_POWER,//
		LOG_PRODUCT,//
		LOG_QUOTIENT,//
		LOG_SAME_BASE_ARGUMENT,//
		LOG_INVERSE,//
		//
		SOLVING_EQUATIONS_ADD, //
		SOLVING_EQUATIONS_SUBTRACT, //
		SOLVING_EQUATIONS_MULTIPLY, //
		SOLVING_EQUATIONS_DIVIDE, //
		SOLVING_EQUATIONS_EXPONENTIAL, //
		SOLVING_EQUATIONS_RAISE, //
		SOLVING_EQUATIONS_LOG, //
		SOLVING_EQUATIONS_INVERSE_TRIG; //
		
		
		private Skill[] prerequisiteSkills;

		private Skill(Skill...prerequisiteSkills) {
			this.prerequisiteSkills = prerequisiteSkills;
		}
		
		public HashSet<Skill> getPrerequisiteSkills(){
			HashSet<Skill> preSkills = new HashSet<Skill>();
			fillWithPrerequisiteSkills(preSkills);
			return preSkills;
		}
		private void fillWithPrerequisiteSkills(HashSet<Skill> preSkills){
			for(Skill pSkill : prerequisiteSkills) {
				preSkills.add(pSkill);
				pSkill.fillWithPrerequisiteSkills(preSkills);
			}
		}

		public static final String GOOGLE_SEARCH_PREFIX = "https://www.google.com/#q=";

		public String getPage() {
			return GOOGLE_SEARCH_PREFIX + toString().toLowerCase().replace("_", "+");
		}

}
