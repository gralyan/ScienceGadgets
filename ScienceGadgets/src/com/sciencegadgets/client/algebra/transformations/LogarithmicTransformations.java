/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.algebra.transformations;

import java.math.BigDecimal;
import java.util.LinkedList;

import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.specification.LogBaseSpecification;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;
import com.sciencegadgets.shared.dimensions.CommonConstants;

public class LogarithmicTransformations extends
		TransformationList<LogTransformButton> {

	private static final long serialVersionUID = -1226259041452624481L;

	EquationNode log;
	EquationNode argument;
	String base;

	TypeSGET argumentType;

	static LogBaseSpecification logBaseSpec = null;

	public LogarithmicTransformations(EquationNode logNode) {
		super(logNode);

		log = logNode;
		argument = logNode.getFirstChild();
		base = log.getAttribute(MathAttribute.LogBase);

		argumentType = argument.getType();

		add(logChangeBase_check());
		add(unravelLogExp_check());
		if (add(logChild_check())) {
			return;
		}
		add(logEvaluate_check());

	}

	LogChangeBaseButton logChangeBase_check() {
		return new LogChangeBaseButton(this);
	}

	LogEvaluateButton logEvaluate_check() {
		if (TypeSGET.Number.equals(argumentType)) {
			try {
				Double argValue = Double.parseDouble(argument.getSymbol());
				return new LogEvaluateButton(this, argValue);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	LogTransformButton logChild_check() {
		switch (argumentType) {
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
		default:
			return null;
		}
	}

	/**
	 * Check if: (log base = exponential base)<br/>
	 * log<sub>b</sub>(b<sup>x</sup>) = x
	 */
	LogUnravelButton unravelLogExp_check() {
		EquationNode exponential = log.getFirstChild();
		if (TypeSGET.Exponential.equals(exponential.getType())) {
			EquationNode exponentialBase = exponential.getFirstChild();
			if (TypeSGET.Number.equals(exponentialBase.getType())
					&& exponentialBase.getXMLNode().getInnerText()
							.equals(log.getAttribute(MathAttribute.LogBase))) {
				EquationNode exponentialExp = exponential.getChildAt(1);
				return new LogUnravelButton(log, exponentialExp, this);
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
	final EquationNode argument;
	final String base;

	protected boolean reloadAlgebraActivity;
	protected LogarithmicTransformations previewContext;

	LogTransformButton(LogarithmicTransformations context, String html) {
		super(context);
		addStyleName(CSS.LOG);

		this.log = context.log;
		this.argument = context.argument;
		this.base = context.base;

		this.reloadAlgebraActivity = context.reloadAlgebraActivity;

	}

	@Override
	protected void onTransformationEnd(String changeComment,
			EquationNode nodeToSelect) {
		if (reloadAlgebraActivity) {
			log.highlight();
		}
		super.onTransformationEnd(changeComment, nodeToSelect);
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
	public Badge getAssociatedBadge() {
		return Badge.LOG_EVALUATE;
	}

	@Override
	public boolean meetsAutoTransform() {
		return true;
	}

	@Override
	public void transform() {
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

		EquationNode evaluated = log.replace(TypeSGET.Number, total + "");

		onTransformationEnd("log<sub>" + base + "</sub>(" + argValue + ") = "
				+ total, evaluated);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logEvaluate_check();
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
	public Badge getAssociatedBadge() {
		return Badge.LOG_CHANGE_BASE;
	}

	@Override
	public boolean meetsAutoTransform() {
		return true;
	}

	@Override
	public void transform() {
		if (!reloadAlgebraActivity) {
			log.replace(TypeSGET.Variable, "Change Base");
		} else {
			if (LogarithmicTransformations.logBaseSpec == null) {
				LogarithmicTransformations.logBaseSpec = new LogBaseSpecification() {
					@Override
					public void onSpecify(String newBase) {
						super.onSpecify(newBase);

						EquationNode fraction = log.encase(TypeSGET.Fraction);
						log.setAttribute(MathAttribute.LogBase, newBase);

						EquationNode denom = fraction.append(TypeSGET.Log, "");
						denom.append(TypeSGET.Number, base);
						denom.setAttribute(MathAttribute.LogBase, newBase);

						onTransformationEnd(
								"log<sub>b</sub>(x) = log<sub>c</sub>(x) / log<sub>c</sub>(b)",
								log);
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
	public Badge getAssociatedBadge() {
		return Badge.LOG_PRODUCT;
	}

	@Override
	public void transform() {
		EquationNode sum = log.encase(TypeSGET.Sum);
		int logIndex = log.getIndex();

		LinkedList<EquationNode> termChildren = argument.getChildren();
		for (int i = 0; i < termChildren.size(); i++) {
			EquationNode termChild = termChildren.get(i);
			if (TypeSGET.Operation.equals(termChild.getType())) {
				sum.addBefore(logIndex + i, TypeSGET.Operation,
						Operator.PLUS.getSign());
			} else {
				EquationNode termChildLog = sum.addBefore(logIndex + i,
						TypeSGET.Log, "");
				termChildLog.setAttribute(MathAttribute.LogBase, base);
				termChildLog.append(termChild);
			}
		}

		log.remove();

		onTransformationEnd(
				"log<sub>b</sub>(x y) = log<sub>b</sub>(x) + log<sub>b</sub>(y)",
				sum);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChild_check();
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
	public Badge getAssociatedBadge() {
		return Badge.LOG_QUOTIENT;
	}

	@Override
	public void transform() {
		EquationNode sum = log.encase(TypeSGET.Sum);

		EquationNode numerator = argument.getFirstChild();
		EquationNode denominator = argument.getChildAt(1);
		int logIndex = log.getIndex();

		EquationNode denominatorLog = sum.addBefore(logIndex, TypeSGET.Log, "");
		denominatorLog.setAttribute(MathAttribute.LogBase, base);
		denominatorLog.append(denominator);

		sum.addBefore(logIndex, TypeSGET.Operation, Operator.MINUS.getSign());

		EquationNode numeratorLog = sum.addBefore(logIndex, TypeSGET.Log, "");
		numeratorLog.setAttribute(MathAttribute.LogBase, base);
		numeratorLog.append(numerator);

		log.remove();

		onTransformationEnd(
				"log<sub>b</sub>(x/y) = log<sub>b</sub>(x) - log<sub>b</sub>(y)",
				sum);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChild_check();
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
	public Badge getAssociatedBadge() {
		return Badge.LOG_POWER;
	}

	@Override
	public void transform() {
		EquationNode term = log.encase(TypeSGET.Term);

		int logIndex = log.getIndex();
		EquationNode exponent = argument.getChildAt(1);

		term.addBefore(logIndex, TypeSGET.Operation, Operator.getMultiply()
				.getSign());
		term.addBefore(logIndex, exponent);

		argument.replace(argument.getFirstChild());

		onTransformationEnd(
				"log<sub>b</sub>(x<sup>y</sup>) = y log<sub>b</sub>(x)", term);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChild_check();
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
	public Badge getAssociatedBadge() {
		return Badge.LOG_ONE;
	}

	@Override
	public void transform() {
		EquationNode evaluated = log.replace(TypeSGET.Number, "0");

		onTransformationEnd("log<sub>b</sub>(1) = 0", evaluated);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChild_check();
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
	public Badge getAssociatedBadge() {
		return Badge.LOG_SAME_BASE_ARGUMENT;
	}

	@Override
	public void transform() {
		EquationNode evaluated = log.replace(TypeSGET.Number, "1");

		onTransformationEnd("log<sub>b</sub>(b) = 1", evaluated);
	}

	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.logChild_check();
	}
}

/**
 * log<sub>b</sub>(b<sup>x</sup>) = x<br/>
 */
class LogUnravelButton extends LogTransformButton {

	private EquationNode toReplace;
	private EquationNode replacement;

	public LogUnravelButton(final EquationNode toReplace,
			final EquationNode replacement, LogarithmicTransformations context) {
		super(context, replacement.getHTMLString(true, true));
		this.toReplace = toReplace;
		this.replacement = replacement;
	}

	@Override
	public Badge getAssociatedBadge() {
		return Badge.LOG_INVERSE;
	}

	@Override
	public void transform() {
		String changeComment = toReplace.getHTMLString(true, true) + " = "
				+ replacement.getHTMLString(true, true);
		toReplace.replace(replacement);
		onTransformationEnd(changeComment, replacement);
	}
}