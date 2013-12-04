package com.sciencegadgets.client.algebra.transformations;

import com.sciencegadgets.shared.TypeML.Operator;

	public enum Rule {
		Commutative("http://en.wikipedia.org/wiki/Commutative_property"), 
		Addition("http://en.wikipedia.org/wiki/Addition"),
		Multiplication("http://en.wikipedia.org/wiki/Multiplication"),
		Solving("http://en.wikipedia.org/wiki/Elementary_algebra#Solving_algebraic_equations"), 
		LikeTerms("http://en.wikipedia.org/wiki/Like_terms"), 
		Factorization("http://en.wikipedia.org/wiki/Factorization"), 
		FractionAddition("http://en.wikipedia.org/wiki/Fraction_%28mathematics%29#Addition"),
		FractionDivision("http://en.wikipedia.org/wiki/Fraction_%28mathematics%29#Division"),
		FractionMultiplication("http://en.wikipedia.org/wiki/Fraction_%28mathematics%29#Multiplication"),
		Cancellation("http://en.wikipedia.org/wiki/Fraction_%28mathematics%29#Multiplying_a_fraction_by_another_fraction"),
		Distribution("http://en.wikipedia.org/wiki/Distributive_property"),
		Exponents("http://simple.wikipedia.org/wiki/Exponentiation"),
		FactorizationInteger("http://en.wikipedia.org/wiki/Prime_factorization");

		private String page;

		Rule(String page) {
			this.page = page;
		}

		public String getPage() {
			return page;
		}

}
