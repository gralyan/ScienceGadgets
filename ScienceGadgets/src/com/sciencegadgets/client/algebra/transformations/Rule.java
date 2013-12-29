package com.sciencegadgets.client.algebra.transformations;


	public enum Rule {
		COMMUNATIVE_PROPERTY("http://en.wikipedia.org/wiki/Commutative_property"), 
		ADDITION("http://en.wikipedia.org/wiki/Addition"),
		MULTIPLICATION("http://en.wikipedia.org/wiki/Multiplication"),
		SOLVING_ALGEBRAIC_EQUATIONS("http://en.wikipedia.org/wiki/Elementary_algebra#Solving_algebraic_equations"), 
		COMBINING_LIKE_TERMS("http://en.wikipedia.org/wiki/Like_terms"), 
		FACTORIZATION("http://en.wikipedia.org/wiki/Factorization"), 
		FRACTION_ADDITION("http://en.wikipedia.org/wiki/Fraction_%28mathematics%29#Addition"),
		FRACTION_DIVISION("http://en.wikipedia.org/wiki/Fraction_%28mathematics%29#Division"),
		FRACTION_MULTIPLICATION("http://en.wikipedia.org/wiki/Fraction_%28mathematics%29#Multiplication"),
		CANCELLING_FRACTIONS("http://en.wikipedia.org/wiki/Fraction_%28mathematics%29#Multiplying_a_fraction_by_another_fraction"),
		DISTRIBUTIVE_PROPERTY("http://en.wikipedia.org/wiki/Distributive_property"),
		EXPONENT_PROPERTIES("http://simple.wikipedia.org/wiki/Exponentiation"),
		INTEGER_FACTORIZATION("http://en.wikipedia.org/wiki/Prime_factorization");
		

		private String page;
		public static final String GOOGLE_SEARCH_PREFIX = "https://www.google.com/#q=";

		Rule(String page) {
			this.page = page;
		}

		public String getPage() {
			return GOOGLE_SEARCH_PREFIX + toString().toLowerCase().replace("_", "+");
		}

}
