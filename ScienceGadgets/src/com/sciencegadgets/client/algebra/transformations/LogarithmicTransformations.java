package com.sciencegadgets.client.algebra.transformations;

import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class LogarithmicTransformations {
	static MathNode log;
	static MathNode logChild;
	static String base;

	static LogBaseSpecification logBaseSpec = null;

	public static void assign(MathNode logNode) {
		try {
			log = logNode;
			logChild = logNode.getFirstChild();
			base = log.getAttribute(MathAttribute.LogBase);
			TypeML logChildType = logChild.getType();

			changeBase();

			switch (logChildType) {
			case Term:
				expandTerm();
				break;
			case Fraction:
				expandFraction();
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
	 * log<sub>b</sub>(x) = log<sub>c</sub>(x) / log<sub>c</sub>(y)
	 */
	private static void changeBase() {
		ClickHandler changeBaseHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (logBaseSpec == null) {
					logBaseSpec = new LogBaseSpecification() {
						@Override
						public void onSpecify(String newBase) {
							super.onSpecify(newBase);

							log.highlight();

							MathNode fraction = log.encase(TypeML.Fraction);
							log.setAttribute(MathAttribute.LogBase, newBase);
							fraction.append(TypeML.Number, base).setAttribute(
									MathAttribute.LogBase, newBase);

							AlgebraActivity
									.reloadEquationPanel(
											"log<sub>b</sub>(x) = log<sub>c</sub>(x) / log<sub>c</sub>(y)",
											Rule.LOGARITHM);
						}
					};
				}
				logBaseSpec.reload();
			}
		};

		AlgebraActivity.algTransformMenu.add(new Button("Change base",
				changeBaseHandler));

	}

	/**
	 * log<sub>b</sub>(x y) = log<sub>b</sub>(x) + log<sub>b</sub>(y)
	 */
	private static void expandTerm() {
		ClickHandler expandTermHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				log.highlight();

				MathNode sum = log.encase(TypeML.Sum);
				int logIndex = log.getIndex();

				LinkedList<MathNode> termChildren = logChild
						.getChildren();
				for (int i = 0 ; i< termChildren.size() ; i++) {
					MathNode termChild = termChildren.get(i);
					if (TypeML.Operation.equals(termChild.getType())) {
						sum.addBefore(logIndex+i, TypeML.Operation, Operator.PLUS.getSign());
					} else {
						MathNode termChildLog = sum.addBefore(logIndex+i, TypeML.Log, "");
						termChildLog.setAttribute(MathAttribute.LogBase, base);
						termChildLog.append(termChild);
					}
				}
				
				log.remove();

				AlgebraActivity
						.reloadEquationPanel(
								"log<sub>b</sub>(x y) = log<sub>b</sub>(x) + log<sub>b</sub>(y)",
								Rule.LOGARITHM);
			}
		};

		AlgebraActivity.algTransformMenu.add(new Button("Log Product",
				expandTermHandler));
	}

	/**
	 * log<sub>b</sub>(x/y) = log<sub>b</sub>(x) - log<sub>b</sub>(y)
	 */
	private static void expandFraction() {
		ClickHandler expandFractionHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				log.highlight();

				MathNode sum = log.encase(TypeML.Sum);

				MathNode numerator = logChild.getFirstChild();
				MathNode denominator = logChild.getChildAt(1);
				int logIndex = log.getIndex();
				
				MathNode denominatorLog = sum.addBefore(logIndex,TypeML.Log, "");
				denominatorLog.setAttribute(MathAttribute.LogBase, base);
				denominatorLog.append(denominator);
				
				sum.addBefore(logIndex,TypeML.Operation, Operator.MINUS.getSign());
				
				MathNode numeratorLog = sum.addBefore(logIndex,TypeML.Log, "");
				numeratorLog.setAttribute(MathAttribute.LogBase, base);
				numeratorLog.append(numerator);
				
				log.remove();
				
				AlgebraActivity
						.reloadEquationPanel(
								"log<sub>b</sub>(x/y) = log<sub>b</sub>(x) - log<sub>b</sub>(y)",
								Rule.LOGARITHM);
			}
		};

		AlgebraActivity.algTransformMenu.add(new Button("Log Quotient",
				expandFractionHandler));
	}

	/**
	 * log<sub>b</sub>(x<sup>y</sup>) = y log<sub>b</sub>(x)
	 */
	private static void simplifyExponential() {
		ClickHandler expandFractionHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				log.highlight();

				MathNode term = log.encase(TypeML.Term);

				int logIndex = log.getIndex();
				MathNode exponent = logChild.getChildAt(1);
				
				term.addBefore(logIndex, TypeML.Operation, Operator.getMultiply().getSign());
				term.addBefore(logIndex, exponent);
				
				logChild.replace(logChild.getFirstChild());
				
				AlgebraActivity
						.reloadEquationPanel(
								"log<sub>b</sub>(x<sup>y</sup>) = y log<sub>b</sub>(x)",
								Rule.LOGARITHM);
			}
		};

		AlgebraActivity.algTransformMenu.add(new Button("Log Power",
				expandFractionHandler));
	}
}
