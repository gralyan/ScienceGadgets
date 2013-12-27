package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TypeML;

public class ExponentialTransformations {

	public static void assign(MathNode exponential) {
		MathNode base = exponential.getChildAt(0);
		MathNode exponent = exponential.getChildAt(1);
		TypeML baseType = base.getType();
		TypeML exponentType = exponent.getType();

		System.out.println("assigning");
		switch (exponentType) {
		case Number:
			System.out.println("in switch");
			try {
				double exp = Double.parseDouble(exponent.getSymbol());
				// Integer between 0-11
				if ((exp == Math.floor(exp)) && !Double.isInfinite(exp)
						&& exp < 11 && exp > 0) {
					System.out.println("adding button");
					AlgebraActivity.algTransformMenu
							.add(new ExpandExponentialButton(exponential, exp));
				}
			} catch (NumberFormatException e) {
			}
			break;

		}
	}

}

class ExpandExponentialButton extends Button {
	ExpandExponentialButton(final MathNode exponential, final Double exp) {
		
		setText("Expand Exponential");
		
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				MathNode term = exponential.getParent().addBefore(
						exponential.getIndex(), TypeML.Term, "");
				MathNode base = term.append(exponential.getChildAt(0));
				for (int i = 1; i < exp; i++) {
					term.append(TypeML.Operation, TypeML.Operator.getMultiply()
							.getSign());
					term.append(base.clone());
					exponential.remove();
					
					AlgebraActivity.reloadEquationPanel("", null);
				}
			}
		});
	}
}
