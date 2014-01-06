package com.sciencegadgets.client.algebra.transformations;

import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TypeML;

public class LogarithmicTransformations {
	static MathNode log;
	static MathNode logChild;

	public static void assign(MathNode logNode) {
		try {
			log = logNode;
			logChild = logNode.getFirstChild();
			TypeML logChildType = logChild.getType();

			switch (logChildType) {
			case Term:
				expandTerm();
				break;
			case Fraction:
				expandSum();
				break;
			case Exponential:
				simplifyExponential();
				break;
			}

		} catch (NumberFormatException e) {
			e.printStackTrace();
			JSNICalls
					.error("A number node couldn't be parsed: " + e.toString());
		}
	}
	
	/**
	 * log<sub>b</sub>(x y) = log<sub>b</sub>(x) + log<sub>b</sub>(y)
	 */
	private static void expandTerm(){
		
	}
	/**
	 * log<sub>b</sub>(x/y) = log<sub>b</sub>(x) - log<sub>b</sub>(y)
	 */
	private static void expandSum(){
		
	}
	/**
	 * log<sub>b</sub>(x<sup>y</sup>) = y log<sub>b</sub>(x)
	 */
	private static void simplifyExponential(){
		
	}
}
