package com.sciencegadgets.client.algebra.edit;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.sciencegadgets.client.ui.LinkPrompt;
import com.sciencegadgets.shared.TypeSGET;

public class LinkPrompt_Equation extends LinkPrompt {

	// final ToggleSlide easyToggle = new ToggleSlide("Normal", "Easy", true,
	// new ClickHandler() {
	// @Override
	// public void onClick(ClickEvent event) {
	// ToggleSlide toggle = (ToggleSlide) event.getSource();
	// if (toggle.isFistSelected()) {
	// pMap.put(Parameter.easy, URLParameters.TRUE);
	// } else {
	// pMap.remove(Parameter.easy);
	// }
	//
	// updateLinks();
	// }
	// });

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

				final Button setGoalButton = new Button("Set Goal Equation",
						new ClickHandler() {
							@Override
							public void onClick(ClickEvent arg0) {
								LinkPrompt_Equation.this.disappear();
								Moderator.switchToAlgebra(algebraActivity.getEquationTree(), algebraActivity.getEquation(), ActivityType.algebrasetgoal, true);
							}
						});
				upperArea.add(setGoalButton);
			}
		} else {
			upperArea.add(new Label("Goal: "));
			upperArea.add(algebraActivity.getEquationTree().getDisplayClone());
			
			addOkHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					Moderator.switchToAlgebra(algebraActivity.getEquationTree(), algebraActivity.getEquation(), ActivityType.algebraedit, true);
				}
			});
		}

	}

	@Override
	public void appear() {

		try {
			EquationTree eTree = algebraActivity.getEquationTree();
			eTree.validateTree();
			eTree.getValidator().validateQuantityKinds(eTree);
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

		super.appear();
	}

	public void setMapParameters() {
		String eqString = URLParameters.getParameter(Parameter.equation);

		pMap = new HashMap<Parameter, String>();
		pMap.put(Parameter.activity, ActivityType.algebrasolve.toString());
		if (initialEquation == null) {
			pMap.put(Parameter.equation, eqString);
		} else {
			pMap.put(Parameter.equation, initialEquation.getEquationXMLString());
			pMap.put(Parameter.goal, eqString);

		}
	}

	protected void updateLinks() {

		// Replaces link text with equation HTML

		if(initialEquation==null) {
			html = algebraActivity.getEquationTree().getDisplayClone();
		}else {
			html = initialEquation.getDisplayClone();
		}
		// linkDisplay.setHTML(html.getHTML());

		Element styleLink = new HTML(
				"<link type=\"text/css\" rel=\"stylesheet\" href=\"http://sciencegadgets.org/CSStyles/equation.css\"></link>")
				.getElement();
		styleLink.appendChild(html.getElement());
		Element linkEl = linkDisplay.getElement();
		linkEl.removeAllChildren();
		linkEl.appendChild(styleLink);

		iframeDisplay.setName("Interactive Equation");

		super.updateLinks();
	}

}