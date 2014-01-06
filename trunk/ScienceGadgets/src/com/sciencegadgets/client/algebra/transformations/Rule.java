package com.sciencegadgets.client.algebra.transformations;


	public enum Rule {
		COMMUNATIVE_PROPERTY, //
		ADDITION,//
		MULTIPLICATION,//
		SOLVING_ALGEBRAIC_EQUATIONS, //
		COMBINING_LIKE_TERMS, //
		FACTORIZATION, //
		FRACTION_ADDITION,//
		FRACTION_DIVISION,//
		FRACTION_MULTIPLICATION,//
		CANCELLING_FRACTIONS,//
		DISTRIBUTIVE_PROPERTY,//
		EXPONENT_PROPERTIES,//
		DIVISION,//
		INTEGER_FACTORIZATION,//
		INVERSE_TRIGONOMETRIC_FUNCTIONS,//
		LOGARITHM;//
		

		public static final String GOOGLE_SEARCH_PREFIX = "https://www.google.com/#q=";

		public String getPage() {
			return GOOGLE_SEARCH_PREFIX + toString().toLowerCase().replace("_", "+");
		}

}
