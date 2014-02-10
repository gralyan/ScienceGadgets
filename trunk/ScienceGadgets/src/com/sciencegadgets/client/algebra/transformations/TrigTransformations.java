package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class TrigTransformations extends TransformationList {

	private static final long serialVersionUID = 2158189067374424843L;

	MathNode trig;
	MathNode argument;
	TypeML argumentType;
	TrigFunctions function;

	public TrigTransformations(MathNode trigNode) {
		trig = trigNode;
		argument = trigNode.getChildAt(0);
		argumentType = argument.getType();
		function = TrigFunctions.valueOf(trigNode
				.getAttribute(MathAttribute.Function));

		add(trigReciprocal_check());
		add(trigDefinition_check());
	}

	private TrigReciprocalButton trigReciprocal_check() {
		if (!function.isArc()) {
			return new TrigReciprocalButton(this);
		}
		return null;
	}
	private TrigDefineButton trigDefinition_check() {
		if (!function.isArc()) {
			return new TrigDefineButton(this);
		}
		return null;
	}

}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
class TrigTransformButton extends TransformationButton {
	final MathNode trig;
	final MathNode argument;
	final TypeML argumentType;
	final TrigFunctions function;

	TrigTransformButton(TrigTransformations context) {
		super();
		addStyleName(CSS.TRIG +" "+CSS.DISPLAY_WRAPPER);
		this.trig = context.trig;
		this.argument = context.argument;
		this.argumentType = context.argumentType;
		this.function = context.function;

		trig.highlight();
	}
}

/**
 * All 3 derivations of each of the following<br/>
 * tan(x) = sin(x)/cos(x)<br/>
 * cot(x) = csc(x)/sec(x)<br/>
 * <br/>
 * tanh(x) = sinh(x)/cosh(x)<br/>
 * coth(x) = csch(x)/sec(x)<br/>
 */
class TrigDefineButton extends TrigTransformButton {

	public TrigDefineButton(TrigTransformations context) {
		super(context);

		final TrigFunctions[] funcDef = function.getDefinition();

		final boolean defIsTerm =  funcDef[1] != null;
		String html = defIsTerm ? "*" + funcDef[1] : "/" + funcDef[2];
		html = funcDef[0] + html;

		setHTML(html);

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				trig.setSymbol(funcDef[0].toString());
				MathNode otherTrig;
				
				if(defIsTerm) {
					MathNode term = trig.encase(TypeML.Term);
					int trigIndex = trig.getIndex();
					otherTrig = term.addAfter(trigIndex, TypeML.Trig, funcDef[1].toString());
					term.addAfter(trigIndex, TypeML.Operation, Operator.getMultiply().getSign());
				}else {
					MathNode frac = trig.encase(TypeML.Fraction);
					otherTrig = frac.addAfter(0, TypeML.Trig, funcDef[2].toString());
				}
				otherTrig.append(argument.clone());

				Moderator.reloadEquationPanel(getHTML(), Rule.TRIGONOMETRIC_FUNCTIONS);
			}
		});
	}
}

//
/**
 * Both ways for each:<br/>
 * sin(x) = 1/csc(x)<br/>
 * cos(x) = 1/sec(x)<br/>
 * tan(x) = 1/cot(x)<br/>
 * <br/>
 * sinh(x) = 1/csch(x)<br/>
 * cosh(x) = 1/sech(x)<br/>
 * tanh(x) = 1/coth(x)<br/>
 */
class TrigReciprocalButton extends TrigTransformButton {

	public TrigReciprocalButton(TrigTransformations context) {
		super(context);

		final TrigFunctions reciprocalFunction = function.getReciprocal();

		setHTML("1/" + reciprocalFunction);

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				AlgebraicTransformations.reciprocate(trig);

				trig.setAttribute(MathAttribute.Function,
						reciprocalFunction.toString());

				Moderator.reloadEquationPanel(getHTML(), Rule.TRIGONOMETRIC_FUNCTIONS);
			}
		});
	}
}
