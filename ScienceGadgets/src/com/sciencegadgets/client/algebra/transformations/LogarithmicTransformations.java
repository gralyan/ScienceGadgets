package com.sciencegadgets.client.algebra.transformations;

import java.util.LinkedList;

import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.specification.LogBaseSpecification;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;
import com.sciencegadgets.shared.dimensions.CommonConstants;

public class LogarithmicTransformations extends
		TransformationList<LogTransformButton> {

	private static final long serialVersionUID = -1226259041452624481L;

	EquationNode log;
	EquationNode argument;
	String base;

	TypeEquationXML logChildType;

	static LogBaseSpecification logBaseSpec = null;

	public LogarithmicTransformations(EquationNode logNode) {
		super(logNode);

		log = logNode;
		argument = logNode.getFirstChild();
		base = log.getAttribute(MathAttribute.LogBase);

		logChildType = argument.getType();

		add(logChangeBase_check());
		add(logChildCheck());
		add(logEvaluateCheck());
		add(unravelLogExp_check());

	}

	LogChangeBaseButton logChangeBase_check() {
		return new LogChangeBaseButton(this);
	}

	LogEvaluateButton logEvaluateCheck() {
		if (TypeEquationXML.Number.equals(argument.getType())) {
			try {
				Double argValue = Double.parseDouble(argument.getSymbol());
				return new LogEvaluateButton(this, argValue);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	LogTransformButton logChildCheck() {
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
	LogUnravelButton unravelLogExp_check() {
		EquationNode exponential = log.getFirstChild();
		if (TypeEquationXML.Exponential.equals(exponential.getType())) {
			EquationNode exponentialBase = exponential.getFirstChild();
			if (TypeEquationXML.Number.equals(exponentialBase.getType())
					&& exponentialBase.getSymbol().equals(
							log.getAttribute(MathAttribute.LogBase))) {
				EquationNode exponentialExp = exponential.getChildAt(1);
				return new LogUnravelButton(log, exponentialExp, Rule.LOGARITHM,
						this);
			}
		}
		return null;
	}

}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
abstract class LogTransformButton extends TransformationButton {
	final EquationNode log;
	final EquationNode logChild;
	final String base;

	protected boolean reloadAlgebraActivity;
	protected LogarithmicTransformations previewContext;

	LogTransformButton(LogarithmicTransformations context, String html) {
		super(context);
		addStyleName(CSS.LOG + " " + CSS.DISPLAY_WRAPPER);

		this.log = context.log;
		this.logChild = context.argument;
		this.base = context.base;

		this.reloadAlgebraActivity = context.reloadAlgebraActivity;

		log.highlight();
	}

	@Override
	TransformationButton getPreviewButton(EquationNode log) {
		previewContext = new LogarithmicTransformations(log);
		previewContext.reloadAlgebraActivity = false;
		return null;
	}
}

/**
 * Evaluate
 */
class LogEvaluateButton extends LogTransformButton {
	private double argValue;

	LogEvaluateButton(LogarithmicTransformations context, final Double argValue) {
		super(context, "Evaluate");
		this.argValue = argValue;
	}

	@Override
	protected
	void transform() {
		Double total;

		if (CommonConstants.EULER.getSymbol().equals(base)) {
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

		log.replace(TypeEquationXML.Number, total + "");

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("log<sub>" + base + "</sub>("
					+ argValue + ") = " + total, Rule.LOGARITHM);
		}
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logEvaluateCheck();
	}
}

/**
 * log<sub>b</sub>(x) = log<sub>c</sub>(x) / log<sub>c</sub>(b)
 */
class LogChangeBaseButton extends LogTransformButton {
	LogChangeBaseButton(LogarithmicTransformations context) {
		super(context, "Change base");
	}

	@Override
	protected
	void transform() {
		if (!reloadAlgebraActivity) {
			log.replace(TypeEquationXML.Variable, "Change Base");
		} else {
			if (LogarithmicTransformations.logBaseSpec == null) {
				LogarithmicTransformations.logBaseSpec = new LogBaseSpecification() {
					@Override
					public void onSpecify(String newBase) {
						super.onSpecify(newBase);

						EquationNode fraction = log
								.encase(TypeEquationXML.Fraction);
						log.setAttribute(MathAttribute.LogBase, newBase);

						EquationNode denom = fraction.append(
								TypeEquationXML.Log, "");
						denom.append(TypeEquationXML.Number, base);
						denom.setAttribute(MathAttribute.LogBase, newBase);

						if (reloadAlgebraActivity) {
							Moderator
									.reloadEquationPanel(
											"log<sub>b</sub>(x) = log<sub>c</sub>(x) / log<sub>c</sub>(b)",
											Rule.LOGARITHM);
						}
					}
				};
			}
			LogarithmicTransformations.logBaseSpec.reload();
		}
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChangeBase_check();
	}
}

/**
 * log<sub>b</sub>(x &middot; y) = log<sub>b</sub>(x) + log<sub>b</sub>(y)
 */
class LogProductButton extends LogTransformButton {
	LogProductButton(LogarithmicTransformations context) {
		super(context, "Log Product");

	}

	@Override
	protected
	void transform() {
		EquationNode sum = log.encase(TypeEquationXML.Sum);
		int logIndex = log.getIndex();

		LinkedList<EquationNode> termChildren = logChild.getChildren();
		for (int i = 0; i < termChildren.size(); i++) {
			EquationNode termChild = termChildren.get(i);
			if (TypeEquationXML.Operation.equals(termChild.getType())) {
				sum.addBefore(logIndex + i, TypeEquationXML.Operation,
						Operator.PLUS.getSign());
			} else {
				EquationNode termChildLog = sum.addBefore(logIndex + i,
						TypeEquationXML.Log, "");
				termChildLog.setAttribute(MathAttribute.LogBase, base);
				termChildLog.append(termChild);
			}
		}

		log.remove();

		if (reloadAlgebraActivity) {
			Moderator
					.reloadEquationPanel(
							"log<sub>b</sub>(x y) = log<sub>b</sub>(x) + log<sub>b</sub>(y)",
							Rule.LOGARITHM);
		}
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChildCheck();
	}
}

/**
 * log<sub>b</sub>(x/y) = log<sub>b</sub>(x) - log<sub>b</sub>(y)
 */
class LogQuotientButton extends LogTransformButton {
	LogQuotientButton(LogarithmicTransformations context) {
		super(context, "Log Quotient");
	}

	@Override
	protected
	void transform() {
		EquationNode sum = log.encase(TypeEquationXML.Sum);

		EquationNode numerator = logChild.getFirstChild();
		EquationNode denominator = logChild.getChildAt(1);
		int logIndex = log.getIndex();

		EquationNode denominatorLog = sum.addBefore(logIndex,
				TypeEquationXML.Log, "");
		denominatorLog.setAttribute(MathAttribute.LogBase, base);
		denominatorLog.append(denominator);

		sum.addBefore(logIndex, TypeEquationXML.Operation,
				Operator.MINUS.getSign());

		EquationNode numeratorLog = sum.addBefore(logIndex,
				TypeEquationXML.Log, "");
		numeratorLog.setAttribute(MathAttribute.LogBase, base);
		numeratorLog.append(numerator);

		log.remove();

		if (reloadAlgebraActivity) {
			Moderator
					.reloadEquationPanel(
							"log<sub>b</sub>(x/y) = log<sub>b</sub>(x) - log<sub>b</sub>(y)",
							Rule.LOGARITHM);
		}
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChildCheck();
	}
}

/**
 * log<sub>b</sub>(x<sup>y</sup>) = y &middot; log<sub>b</sub>(x)
 */
class LogPowerButton extends LogTransformButton {
	LogPowerButton(LogarithmicTransformations context) {
		super(context, "Log Power");
	}

	@Override
	protected
	void transform() {
		EquationNode term = log.encase(TypeEquationXML.Term);

		int logIndex = log.getIndex();
		EquationNode exponent = logChild.getChildAt(1);

		term.addBefore(logIndex, TypeEquationXML.Operation, Operator
				.getMultiply().getSign());
		term.addBefore(logIndex, exponent);

		logChild.replace(logChild.getFirstChild());

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel(
					"log<sub>b</sub>(x<sup>y</sup>) = y log<sub>b</sub>(x)",
					Rule.LOGARITHM);
		}
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChildCheck();
	}
}

/**
 * log<sub>b</sub>(1) = 0
 */
class LogOneButton extends LogTransformButton {
	LogOneButton(LogarithmicTransformations context) {
		super(context, "Log of One");
	}

	@Override
	protected
	void transform() {
		log.replace(TypeEquationXML.Number, "0");

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("log<sub>b</sub>(1) = 0",
					Rule.LOGARITHM);
		}
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChildCheck();
	}
}

/**
 * log<sub>b</sub>(b) = 1
 */
class LogSameBaseAsArgumentButton extends LogTransformButton {
	LogSameBaseAsArgumentButton(LogarithmicTransformations context) {
		super(context, "log<sub>b</sub>(b) = 1");
	}

	@Override
	protected
	void transform() {
		log.replace(TypeEquationXML.Number, "1");

		if (reloadAlgebraActivity) {
			Moderator.reloadEquationPanel("log<sub>b</sub>(b) = 1",
					Rule.LOGARITHM);
		}
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChildCheck();
	}
}

/**
 * log<sub>b</sub>(b<sup>x</sup>) = x<br/>
 */
class LogUnravelButton extends LogTransformButton {

	private EquationNode toReplace;
	private EquationNode replacement;
	private Rule rule;

	public LogUnravelButton(final EquationNode toReplace, final EquationNode replacement,
			final Rule rule, LogarithmicTransformations context) {
		super(context, replacement.getHTMLString(true, true));
		this.rule = rule;
		this.toReplace = toReplace;
		this.replacement = replacement;
	}
	@Override
	protected
	void transform() {
		String changeComment = toReplace.getHTMLString(true, true) + " = "
				+ replacement.getHTMLString(true, true);
		toReplace.replace(replacement);
		Moderator.reloadEquationPanel(changeComment, rule);
	}
}