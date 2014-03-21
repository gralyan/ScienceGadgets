package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;

public class TrigTransformations extends TransformationList {

	private static final long serialVersionUID = 2158189067374424843L;

	EquationNode trig;
	EquationNode argument;
	
	TypeEquationXML argumentType;
	
	TrigFunctions function;

	public TrigTransformations(EquationNode trigNode) {
		super(trigNode);
		
		trig = trigNode;
		argument = trigNode.getChildAt(0);
		
		argumentType = argument.getType();
		
		function = TrigFunctions.valueOf(trigNode
				.getAttribute(MathAttribute.Function));

		add(trigReciprocal_check());
		add(trigDefinition_check());
		add(inverseTrig_check());
	}

	TrigReciprocalButton trigReciprocal_check() {
		if (!function.isArc()) {
			return new TrigReciprocalButton(this);
		}
		return null;
	}
	
	TrigDefineButton trigDefinition_check() {
		if (!function.isArc()) {
			return new TrigDefineButton(this);
		}
		return null;
	}

	/**
	 * Check if function within its inverse function or function of its inverse<br/>
	 * sin(arcsin(x)) = x<br/>
	 * arcsin(sin(x)) = x<br/>
	 */
	TransformationButton inverseTrig_check() {
		EquationNode trigChild = trig.getFirstChild();
		if (TypeEquationXML.Trig.equals(trigChild.getType())) {
			String trigChildFunc = trigChild
					.getAttribute(MathAttribute.Function);
			String trigChildFuncInverse = TrigFunctions
					.getInverseName(trigChildFunc);
			String trigFunc = trig.getAttribute(MathAttribute.Function);
			if (trigFunc.equals(trigChildFuncInverse)) {
				return new UnravelButton(trig, trigChild.getFirstChild(),
						Rule.INVERSE_TRIGONOMETRIC_FUNCTIONS, this);
			}
		}
		return null;
	}
}

// ////////////////////////////////////////////////
// Transform buttons
// ///////////////////////////////////////////////
class TrigTransformButton extends TransformationButton {
	final EquationNode trig;
	final EquationNode argument;
	
	final TypeEquationXML argumentType;
	final TrigFunctions function;

	protected boolean reloadAlgebraActivity;
	protected TrigTransformations previewContext;

	TrigTransformButton(TrigTransformations context) {
		super(context);
		addStyleName(CSS.TRIG +" "+CSS.DISPLAY_WRAPPER);
		
		this.trig = context.trig;
		this.argument = context.argument;
		this.argumentType = context.argumentType;
		this.function = context.function;

		this.reloadAlgebraActivity = context.reloadAlgebraActivity;

		trig.highlight();
	}

	@Override
	TransformationButton getPreviewButton(EquationNode trig) {
		previewContext = new TrigTransformations(trig);
		previewContext.reloadAlgebraActivity = false;
		return null;
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
				EquationNode otherTrig;
				
				if(defIsTerm) {
					EquationNode term = trig.encase(TypeEquationXML.Term);
					int trigIndex = trig.getIndex();
					otherTrig = term.addAfter(trigIndex, TypeEquationXML.Trig, funcDef[1].toString());
					term.addAfter(trigIndex, TypeEquationXML.Operation, Operator.getMultiply().getSign());
				}else {
					EquationNode frac = trig.encase(TypeEquationXML.Fraction);
					otherTrig = frac.addAfter(0, TypeEquationXML.Trig, funcDef[2].toString());
				}
				otherTrig.append(argument.clone());

				if (reloadAlgebraActivity) {
				Moderator.reloadEquationPanel(getHTML(), Rule.TRIGONOMETRIC_FUNCTIONS);
			}}
		});
	}
	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.trigDefinition_check();
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

				if (reloadAlgebraActivity) {
				Moderator.reloadEquationPanel(getHTML(), Rule.TRIGONOMETRIC_FUNCTIONS);
			}}
		});
	}
	@Override
	TransformationButton getPreviewButton(EquationNode operation) {
		super.getPreviewButton(operation);
		return previewContext.trigReciprocal_check();
	}
}
