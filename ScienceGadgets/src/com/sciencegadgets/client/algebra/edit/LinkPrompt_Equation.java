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
package com.sciencegadgets.client.algebra.edit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.SystemOfEquations;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.LinkPrompt;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;

public class LinkPrompt_Equation extends LinkPrompt {

	EquationHTML html;
	AlgebraActivity algebraActivity;
	private EquationTree initialEquation;

	public LinkPrompt_Equation(final AlgebraActivity algebraActivity,
			EquationTree initialEquation) {
		super();
		this.initialEquation = initialEquation;
		this.algebraActivity = algebraActivity;
		// params.add(easyToggle);

		if (initialEquation == null) {
			LinkedList<EquationNode> variables = algebraActivity
					.getEquationTree().getNodesByType(TypeSGET.Variable);
			int varCount = variables.size();
			if (varCount == 0) {
				Window.alert("You should consider adding a variable in the equation");
			} else {

				final Button solveGoalButton = new Button("Solve Goal",
						new ClickHandler() {
							@Override
							public void onClick(ClickEvent arg0) {
								LinkPrompt_Equation.this.disappear();
								Moderator.switchToAlgebra(
										algebraActivity.getEquationTree(),
										ActivityType.editsolvegoal, true);
							}
						});
				solveGoalButton
						.setTitle("Faster and more accurate but can't use with random numbers");
				solveGoalButton.addStyleName(CSS.CREATE_GOAL_BUTTON);
				solveGoalButton.addStyleName(CSS.BORDER_RADIUS_SMALL);
				upperArea.add(solveGoalButton);

				final Button createGoalButton = new Button("Edit Goal",
						new ClickHandler() {
							@Override
							public void onClick(ClickEvent arg0) {
								LinkPrompt_Equation.this.disappear();
								Moderator.switchToAlgebra(
										algebraActivity.getEquationTree(),
										ActivityType.editcreategoal, true);
							}
						});
				createGoalButton
						.setTitle("Can use with random numbers but prone to error!");
				createGoalButton.addStyleName(CSS.CREATE_GOAL_BUTTON);
				createGoalButton.addStyleName(CSS.BORDER_RADIUS_SMALL);
				upperArea.add(createGoalButton);
			}

			Unit[] units = Unit.values();
			ArrayList<String> unitStrings = new ArrayList<String>();
			for (Unit u : units) {
				unitStrings.add(u.toString());
			}
			widthUnits.setAcceptableValues(unitStrings);
			heightUnits.setAcceptableValues(unitStrings);

			SpecChangeHandler specChange = new SpecChangeHandler();
			KeyUpToSpecChange keyUp = new KeyUpToSpecChange();
			widthTextBox.addValueChangeHandler(specChange);
			widthUnits.addValueChangeHandler(specChange);
			heightTextBox.addValueChangeHandler(specChange);
			heightUnits.addValueChangeHandler(specChange);
			widthTextBox.addKeyUpHandler(keyUp);
			heightTextBox.addKeyUpHandler(keyUp);

			colorTextBox.addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					setColor(colorTextBox.getValue());
				}
			});
			colorTextBox.addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					setColor(colorTextBox.getValue());
				}
			});

			colorTextBox.getElement().setId("colorTextBox");
			Element colorInput = DOM.createElement("input");
			colorInput.setAttribute("type", "color");
			colorInput.setAttribute("value", initialColor);
			colorInput.setAttribute("id", "colorInputElement");
			colorInput
					.setAttribute(
							"onchange",
							"colorTextBox.value=colorInputElement.value.replace('#', ''); colorTextBox.focus()");
			Style colorInputStyle = colorInput.getStyle();
			colorInputStyle.setProperty("border", "none");
			colorInputStyle.setPadding(0, Unit.PX);
			colorInputStyle.setWidth(100, Unit.PCT);
			colorInputArea.getElement().appendChild(colorInput);
		} else {
			setModal(true);
			setAutoHideEnabled(false);

			upperArea.add(new Label("Goal: "));
			upperArea.add(algebraActivity.getEquationTree().getDisplayClone());

			addOkHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					Moderator.switchToAlgebra(
							algebraActivity.getEquationTree(),
							ActivityType.editequation, true);
				}
			});
		}

	}

	@Override
	public void appear() {

		EquationTree eTree = algebraActivity.getEquationTree();
		String mathXML = eTree.getEquationXMLString();

		if (mathXML.contains(TypeSGET.NOT_SET)) {
			Window.alert("All new entities (" + TypeSGET.NOT_SET
					+ ") must be set or removed before saving");
			return;
		}

		try {
			eTree.validateTree(true);
		} catch (IllegalStateException e) {
			String message = e.getMessage();
			if (message == null) {
				Window.alert("This equation is invalid, please rebuild it and try again");
			} else {
				Window.alert(message);
			}
			JSNICalls.log(e.getCause().toString());
			return;
		}
		
		if (initialColor == null || "".equals(initialColor)) {
			initialColor = "000000";
		}
		colorTextBox.setText(initialColor);
		setColor(initialColor);

		super.appear();
	}

	public HashMap<Parameter, String> setMapParameters() {
		EquationTree eTree = algebraActivity.getEquationTree();
		String eqString = eTree.getEquationXMLString();

		HashMap<Parameter, String> pMap = new HashMap<Parameter, String>();

		// Random provided
		String randProvided = createRandomProvidedParameter(eTree);
		if (randProvided != null && !"".equals(randProvided)) {
			pMap.put(Parameter.randomprovided, randProvided);
		}

		// Activity
		pMap.put(Parameter.activity,
				ActivityType.interactiveequation.toString());

		// Equation
		if (initialEquation == null) {
			pMap.put(Parameter.equation, eqString);
		} else {
			pMap.put(Parameter.equation, initialEquation.getEquationXMLString());
			pMap.put(Parameter.goal, eqString);
		}

		// System of Equations
		SystemOfEquations system = algebraActivity.getSystem();
		if (system != null && system.hasMultipleEquations()) {
			pMap.put(Parameter.system, system.getURLParam());
		}

		return pMap;
	}

	private String createRandomProvidedParameter(EquationTree eTree) {
		String param = "";
		LinkedList<EquationNode> numbers = eTree
				.getNodesByType(TypeSGET.Number);

		for (EquationNode num : numbers) {
			String numRand = num.getAttribute(MathAttribute.Randomness);
			if (RandomSpecPanel.RANDOM_PROVIDED.equals(numRand)) {
				param = param + URLParameters.RANDOM_PROVIDED_DELIMITER + "0";
			}
		}

		return param.replaceFirst(URLParameters.RANDOM_PROVIDED_DELIMITER, "");
	}

	protected void updateLinks(HashMap<Parameter, String> pMap) {

		// Replaces link text with equation HTML

		if (initialEquation == null) {
			html = algebraActivity.getEquationTree().getDisplayClone();
		} else {
			html = initialEquation.getDisplayClone();
		}
		// linkDisplay.setHTML(html.getHTML());

		NodeList<Element> allEl = html.getElement().getElementsByTagName("*");
		for (int i = 0; i < allEl.getLength(); i++) {
			Element el = allEl.getItem(i);
			el.removeAttribute("id");
		}

		Element styleLink = new HTML(
				"<link type=\"text/css\" rel=\"stylesheet\" href=\""
						+ URLParameters.URL_TOP
						+ "/CSStyles/equation.css\"></link>").getElement();
		styleLink.appendChild(html.getElement());
		Element linkEl = linkDisplay.getElement();
		linkEl.removeAllChildren();
		linkEl.appendChild(styleLink);

		iframeDisplay.setName("InteractiveEquation");
		iframeDisplay.setFrameBorder(0);

		super.updateLinks(pMap);

	}

	class SpecChangeHandler implements ValueChangeHandler<String> {
		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			updateIframeCode();
		}
	}

	class KeyUpToSpecChange implements KeyUpHandler {
		@Override
		public void onKeyUp(KeyUpEvent event) {
			updateIframeCode();
		}
	}

}