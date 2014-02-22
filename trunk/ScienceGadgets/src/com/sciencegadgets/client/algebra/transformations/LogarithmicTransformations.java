package com.sciencegadgets.client.algebra.transformations;

import java.util.LinkedList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class LogarithmicTransformations extends TransformationList {

	private static final long serialVersionUID = -1226259041452624481L;

	MathNode log;
	MathNode argument;
	String base;
	TypeML logChildType;

	static LogBaseSpecification logBaseSpec = null;

	public LogarithmicTransformations(MathNode logNode) {
		super(logNode);
		
		log = logNode;
		argument = logNode.getFirstChild();
		base = log.getAttribute(MathAttribute.LogBase);
		logChildType = argument.getType();

		add(new LogChangeBaseButton(this));
		add(logChildCheck());
		add(logEvaluateCheck());
		add(unravelLogExp_check());

	}

	private LogTransformButton logEvaluateCheck() {
		if (TypeML.Number.equals(argument.getType())) {
			try {
				Double argValue = Double.parseDouble(argument.getSymbol());
				return new LogEvaluateButton(this, argValue);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	private LogTransformButton logChildCheck() {
		switch (logChildType) {
		case Term:
			return new LogProductButton(this);
		case Fraction:
			return new LogQuotientButton(this);
		case Exponential:
			return new LogPowerButton(this);
		case Number:
			if ("1".equals(argument.getSymbol())) {
				return new LogOneButton(this);
			} else if (base.equals(argument.getSymbol())) {
				return new LogSameBaseAsArgumentButton(this);
			}
		}
		return null;
	}
	
	/**
	 * Check if: (log base = exponential base)<br/>
	 * log<sub>b</sub>(b<sup>x</sup>) = x
	 */
	public TransformationButton unravelLogExp_check() {
		MathNode exponential = log.getFirstChild();
		if (TypeML.Exponential.equals(exponential.getType())) {
			MathNode exponentialBase = exponential.getFirstChild();
			if (TypeML.Number.equals(exponentialBase.getType())
					&& exponentialBase.getSymbol().equals(
							log.getAttribute(MathAttribute.LogBase))) {
				MathNode exponentialExp = exponential.getChildAt(1);
				return new UnravelButton(log, exponentialExp, Rule.LOGARITHM, this);
			}
		}
		return null;
	}

}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
class LogTransformButton extends TransformationButton {
	final MathNode log;
	final MathNode logChild;
	final String base;

	LogTransformButton(LogarithmicTransformations context, String html) {
		super(html, context);
		addStyleName(CSS.LOG +" "+CSS.DISPLAY_WRAPPER);
		this.log = context.log;
		this.logChild = context.argument;
		this.base = context.base;

		log.highlight();
	}
}

/**
 * Evaluate
 */
class LogEvaluateButton extends LogTransformButton {
	LogEvaluateButton(LogarithmicTransformations context, final Double argValue) {
		super(context, "Evaluate");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Double total;

				if (MathTree.E.equals(base)) {
					total = Math.log(argValue);
				} else if ("10".equals(base)) {
					total = Math.log10(argValue);
				} else {
					try {
						Double baseValue = Double.parseDouble(base);
						total = Math.log(argValue) / Math.log(baseValue);
					} catch (Exception e) {
						return;
					}
				}

				log.replace(TypeML.Number, total + "");

				Moderator.reloadEquationPanel("log<sub>" + base + "</sub>("
						+ argValue + ") = " + total, Rule.LOGARITHM);
			}
		});
	}

}

/**
 * log<sub>b</sub>(x) = log<sub>c</sub>(x) / log<sub>c</sub>(y)
 */
class LogChangeBaseButton extends LogTransformButton {
	LogChangeBaseButton(LogarithmicTransformations context) {
		super(context, "Change base");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (LogarithmicTransformations.logBaseSpec == null) {
					LogarithmicTransformations.logBaseSpec = new LogBaseSpecification() {
						@Override
						public void onSpecify(String newBase) {
							super.onSpecify(newBase);

							MathNode fraction = log.encase(TypeML.Fraction);
							log.setAttribute(MathAttribute.LogBase, newBase);

							MathNode denom = fraction.append(TypeML.Log, "");
							denom.append(TypeML.Number, base);
							denom.setAttribute(MathAttribute.LogBase, newBase);

							Moderator
									.reloadEquationPanel(
											"log<sub>b</sub>(x) = log<sub>c</sub>(x) / log<sub>c</sub>(y)",
											Rule.LOGARITHM);
						}
					};
				}
				LogarithmicTransformations.logBaseSpec.reload();
			}
		});

	}

}

/**
 * log<sub>b</sub>(x &middot; y) = log<sub>b</sub>(x) + log<sub>b</sub>(y)
 */
class LogProductButton extends LogTransformButton {
	LogProductButton(LogarithmicTransformations context) {
		super(context, "Log Product");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				MathNode sum = log.encase(TypeML.Sum);
				int logIndex = log.getIndex();

				LinkedList<MathNode> termChildren = logChild.getChildren();
				for (int i = 0; i < termChildren.size(); i++) {
					MathNode termChild = termChildren.get(i);
					if (TypeML.Operation.equals(termChild.getType())) {
						sum.addBefore(logIndex + i, TypeML.Operation,
								Operator.PLUS.getSign());
					} else {
						MathNode termChildLog = sum.addBefore(logIndex + i,
								TypeML.Log, "");
						termChildLog.setAttribute(MathAttribute.LogBase, base);
						termChildLog.append(termChild);
					}
				}

				log.remove();

				Moderator
						.reloadEquationPanel(
								"log<sub>b</sub>(x y) = log<sub>b</sub>(x) + log<sub>b</sub>(y)",
								Rule.LOGARITHM);
			}
		});
	}
}

/**
 * log<sub>b</sub>(x/y) = log<sub>b</sub>(x) - log<sub>b</sub>(y)
 */
class LogQuotientButton extends LogTransformButton {
	LogQuotientButton(LogarithmicTransformations context) {
		super(context, "Log Quotient");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				MathNode sum = log.encase(TypeML.Sum);

				MathNode numerator = logChild.getFirstChild();
				MathNode denominator = logChild.getChildAt(1);
				int logIndex = log.getIndex();

				MathNode denominatorLog = sum.addBefore(logIndex, TypeML.Log,
						"");
				denominatorLog.setAttribute(MathAttribute.LogBase, base);
				denominatorLog.append(denominator);

				sum.addBefore(logIndex, TypeML.Operation,
						Operator.MINUS.getSign());

				MathNode numeratorLog = sum.addBefore(logIndex, TypeML.Log, "");
				numeratorLog.setAttribute(MathAttribute.LogBase, base);
				numeratorLog.append(numerator);

				log.remove();

				Moderator
						.reloadEquationPanel(
								"log<sub>b</sub>(x/y) = log<sub>b</sub>(x) - log<sub>b</sub>(y)",
								Rule.LOGARITHM);
			}
		});
	}

}

/**
 * log<sub>b</sub>(x<sup>y</sup>) = y &middot; log<sub>b</sub>(x)
 */
class LogPowerButton extends LogTransformButton {
	LogPowerButton(LogarithmicTransformations context) {
		super(context, "Log Power");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				MathNode term = log.encase(TypeML.Term);

				int logIndex = log.getIndex();
				MathNode exponent = logChild.getChildAt(1);

				term.addBefore(logIndex, TypeML.Operation, Operator
						.getMultiply().getSign());
				term.addBefore(logIndex, exponent);

				logChild.replace(logChild.getFirstChild());

				Moderator
						.reloadEquationPanel(
								"log<sub>b</sub>(x<sup>y</sup>) = y log<sub>b</sub>(x)",
								Rule.LOGARITHM);
			}
		});
	}

}

/**
 * log<sub>b</sub>(1) = 0
 */
class LogOneButton extends LogTransformButton {
	LogOneButton(LogarithmicTransformations context) {
		super(context, "Log of One");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				log.replace(TypeML.Number, "0");

				Moderator.reloadEquationPanel("log<sub>b</sub>(1) = 0",
						Rule.LOGARITHM);
			}
		});
	}

}

/**
 * log<sub>b</sub>(b) = 1
 */
class LogSameBaseAsArgumentButton extends LogTransformButton {
	LogSameBaseAsArgumentButton(LogarithmicTransformations context) {
		super(context, "log<sub>b</sub>(b) = 1");

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				log.replace(TypeML.Number, "1");

				Moderator.reloadEquationPanel("log<sub>b</sub>(b) = 1",
						Rule.LOGARITHM);
			}
		});
	}

}
