package com.sciencegadgets.client.algebra.edit;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
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

				final Button solveGoalButton = new Button("Solve for Goal",
						new ClickHandler() {
							@Override
							public void onClick(ClickEvent arg0) {
								LinkPrompt_Equation.this.disappear();
								Moderator.switchToAlgebra(algebraActivity.getEquationTree(), algebraActivity.getEquation(), ActivityType.algebrasolvegoal, true);
							}
						});
				solveGoalButton.setTitle("Faster and more accurate but can't use with random numbers");
				upperArea.add(solveGoalButton);
				
				final Button createGoalButton = new Button("Create Goal",
						new ClickHandler() {
					@Override
					public void onClick(ClickEvent arg0) {
						LinkPrompt_Equation.this.disappear();
						Moderator.switchToAlgebra(algebraActivity.getEquationTree(), algebraActivity.getEquation(), ActivityType.algebracreategoal, true);
					}
				});
				solveGoalButton.setTitle("Can use with random numbers but prone to error!");
				upperArea.add(createGoalButton);
			}
		} else {
			setModal(true);
			setAutoHideEnabled(false);
			
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

		EquationTree eTree = algebraActivity.getEquationTree();
		String mathXML = eTree.getEquationXMLString();
		
		if (mathXML.contains(TypeSGET.NOT_SET)) {
			Window.alert("All new entities (" + TypeSGET.NOT_SET
					+ ") must be set or removed before saving");
			return;
		}
		
		try {
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
		String eqString = algebraActivity.getEquationTree().getEquationXMLString();

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
		
		NodeList<Element> allEl = html.getElement().getElementsByTagName("*");
		for(int i = 0 ; i<allEl.getLength() ; i++) {
			allEl.getItem(i).removeAttribute("id");
		}

		Element styleLink = new HTML(
				"<link type=\"text/css\" rel=\"stylesheet\" href=\"http://sciencegadgets.org/CSStyles/equation.css\"></link>")
				.getElement();
		styleLink.appendChild(html.getElement());
		Element linkEl = linkDisplay.getElement();
		linkEl.removeAllChildren();
		linkEl.appendChild(styleLink);

		iframeDisplay.setName("Interactive Equation");

		super.updateLinks();
		


		JSNICalls.log("html: "+JSNICalls.elementToString(html.getElement()).replace("\"", "\\\""));
		JSNICalls.log("xml: "+pMap.get(Parameter.equation).replace("\"", "\\\""));
		if(pMap.get(Parameter.goal) != null) {
			JSNICalls.log("goal: "+pMap.get(Parameter.goal).replace("\"", "\\\""));
		}
	}

}