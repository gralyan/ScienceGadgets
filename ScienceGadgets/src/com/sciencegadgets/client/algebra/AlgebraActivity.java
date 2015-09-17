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
package com.sciencegadgets.client.algebra;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.LinkPrompt_Equation;
import com.sciencegadgets.client.algebra.edit.NumberPrompt;
import com.sciencegadgets.client.algebra.edit.VariablePrompt;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SolvedPrompt;
import com.sciencegadgets.shared.TypeSGET;

public class AlgebraActivity extends SimplePanel {

	interface AlgebraUiBinder extends UiBinder<AbsolutePanel, AlgebraActivity> {
	}

	private static AlgebraUiBinder algebraUiBinder = GWT
			.create(AlgebraUiBinder.class);

	AbsolutePanel mainPanel = algebraUiBinder.createAndBindUi(this);

	@UiField
	FlowPanel upperEqArea;
	@UiField
	Button optionsButton;
	@UiField
	SimplePanel upperMidEqArea;
	@UiField
	FlowPanel detailsArea;

	@UiField
	SimplePanel eqPanelHolder;

	@UiField
	public FlowPanel lowerEqArea;

	private EquationPanel eqPanel = null;

	public AlgebraHistory algOut = null;

	private TransformationPanel transformPanel = null;;

	public boolean inProgramaticTransformMode = false;
	public static VariablePrompt varSpec;
	public static NumberPrompt numSpec;
	private ActivityType activityType = null;
	public Widget defaultUpperMidWidget = null;

	// private Equation equation = null;
	private EquationTree goalTree = null;
	private SystemOfEquations systemOfEquations = null;

	public AlgebraActivity(EquationTree eTree, ActivityType activityType) {
		add(mainPanel);

		this.addStyleName(CSS.FILL_PARENT);
		mainPanel.addStyleName(CSS.FILL_PARENT);

		this.activityType = activityType;

		activateOptionsButton(activityType);

		systemOfEquations = new SystemOfEquations(eTree);
		String sysOfEq = URLParameters.getParameter(Parameter.system);
		if (sysOfEq != null && !"".equals(sysOfEq)) {
			systemOfEquations.adoptURLParam(sysOfEq);
		}

		String goal = URLParameters.getParameter(Parameter.goal);
		if (goal != null && !"".equals(goal)) {
			Element goalEl = new HTML(goal).getElement().getFirstChildElement();
			goalTree = new EquationTree(goalEl, false);
		}

		switch (activityType) {
		case interactiveequation:
			activateBackButton(true);

			reCreateAlgHistory();

			transformPanel = new TransformationPanel(this);
			break;
		case editequation:
			activateBackButton(true);

			Button createLinkButton = new Button("Create Link",
					new ClickHandler() {
						@Override
						public void onClick(ClickEvent arg0) {
							new LinkPrompt_Equation(AlgebraActivity.this, null)
									.appear();
						}
					});
			createLinkButton.setStyleName(CSS.SAVE_EQUATION_BUTTON);

			// Button saveEquationButton = new Button("Create Challenge",
			// new SaveButtonHandler(AlgebraActivity.this));
			// saveEquationButton.setStyleName(CSS.SAVE_EQUATION_BUTTON);

			FlowPanel upMidHolder = new FlowPanel();
			upMidHolder.setStyleName(CSS.FILL_PARENT);
			upMidHolder.add(createLinkButton);
			// upMidHolder.add(saveEquationButton);
			setDefaultUpperMidWidget(upMidHolder);

			break;
		case editsolvegoal:

			transformPanel = new TransformationPanel(this);

			// fall through
		case editcreategoal:
			activateBackButton(false);

			CreateLinkWithGoalButton createLinkWithGoalButton = new CreateLinkWithGoalButton(
					"Create Link with Goal", eTree);
			setDefaultUpperMidWidget(createLinkWithGoalButton);
			break;
		case simplifyquiz:
			activateBackButton(false);
			break;
		default:
			break;
		}
	}

	private void activateOptionsButton(ActivityType activityType) {
		switch (activityType) {
		case interactiveequation:
		case editequation:
			optionsButton.setVisible(true);
			optionsButton.addClickHandler(new OptionsHandler(this));
			break;
		default:
			optionsButton.setVisible(false);
			break;
		}
	}

	private void activateBackButton(boolean activate) {
//		backToBrowserButton.setVisible(activate);
		if (activate) {
			optionsButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (event.isControlKeyDown()) {
						Moderator.switchToBrowser();
					}
				}
			});
		}
	}

	public void revertUpperMidAreaToDefault() {
		if (defaultUpperMidWidget != upperMidEqArea.getWidget()) {
			upperMidEqArea.clear();
			if (defaultUpperMidWidget != null) {
				upperMidEqArea.add(defaultUpperMidWidget);
			}
		}
		if (defaultUpperMidWidget instanceof AlgebraHistory) {
			((AlgebraHistory) defaultUpperMidWidget).setHeightToLastRow();;
		}
	}

	public void setDefaultUpperMidWidget(Widget widget) {
		if (widget != null) {
			defaultUpperMidWidget = widget;
			revertUpperMidAreaToDefault();
		}
	}

	public EquationTree getEquationTree() {
		return systemOfEquations.getCurrentTree();
	}

	public EquationPanel getEquationPanel() {
		return eqPanel;
	}

	public void setEquationTree(EquationTree eTree) {
		systemOfEquations.setCurrentTree(eTree);
	}

	public EquationTree getGoalTree() {
		return goalTree;
	}

	public SystemOfEquations getSystem() {
		return systemOfEquations;
	}

	public boolean isInEditMode() {
		return activityType.isInEditMode();
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	public TransformationPanel gettTransformationPanel() {
		return transformPanel;
	}

	public void reCreateAlgHistory() {
		algOut = new AlgebraHistory(this);
		setDefaultUpperMidWidget(algOut);
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param changeComment
	 *            - use null for simple reload, specify change to add to AlgOut
	 */
	public void reloadEquationPanel(String changeComment,
			HashMap<Skill, Integer> skillsIncrease, boolean updateParameters,
			final String nodeIdToSelect) {

		EquationTree equationTree = getEquationTree();

		if (activityType == ActivityType.interactiveequation
				&& changeComment != null && skillsIncrease != null
				&& !skillsIncrease.isEmpty()) {
			algOut.updateAlgebraHistory(changeComment, (Skill) skillsIncrease
					.keySet().toArray()[0], equationTree);
		}

		if (inProgramaticTransformMode) {
			return;
		} else if (activityType == ActivityType.simplifyquiz) {
			// equationTree.getEquals().setSymbol(
			// TypeSGET.Operator.ARROW_RIGHT.getSign());
			// equationTree.getEquals().setSymbol(
			// TypeSGET.Operator.SPACE.getSign());
		} else if (transformPanel != null
				&& transformPanel.simplifyQuiz != null) {
			transformPanel.simplifyQuiz.disappear();
			transformPanel.simplifyQuiz = null;
		}
		try {
			equationTree.validateTree();
		} catch (IllegalStateException e) {
			String message = e.getMessage();
			if (message == null) {
				Window.alert("Oops, an error occured, please refresh the page");
			} else {
				Window.alert(message);
			}
			JSNICalls.error(e.getCause().toString());
			return;
		}

		eqPanelHolder.clear();

		// TODO
		// revertUpperMidAreaToDefault();

		equationTree.reloadDisplay(true, true);
		if (eqPanel != null && eqPanel.isAttached()) {
			eqPanel.removeFromParent();
		}
		eqPanel = new EquationPanel(this);
		eqPanelHolder.add(eqPanel);

		if (activityType == ActivityType.interactiveequation) {
			lowerEqArea.clear();
			algOut.scrollToBottom();

			for (Badge newBadge : Moderator.newBadgeCollection) {
				Label newBadgeResponse = new Label();
				newBadgeResponse.addStyleName(CSS.DROP_ENTER_RESPONSE);
				newBadgeResponse.setText("New Badge! - " + newBadge.toString());
			}

			TypeSGET rightType = equationTree.getRightSide().getType();
			TypeSGET leftType = equationTree.getLeftSide().getType();
			// Check if evaluated
			if ((TypeSGET.Variable.equals(leftType) && TypeSGET.Number
					.equals(rightType))) {
				SolvedPrompt solvedPrompt = new SolvedPrompt();
				solvedPrompt.solved(this, equationTree.getRightSide()
						.getSymbol());
			} else if ((TypeSGET.Variable.equals(rightType) && TypeSGET.Number
					.equals(leftType))) {
				SolvedPrompt solvedPrompt = new SolvedPrompt();
				solvedPrompt.solved(this, equationTree.getLeftSide()
						.getSymbol());
			} else if (goalTree != null && equationTree.isLike(goalTree)) {
				// Check if equation matches goal
				SolvedPrompt solvedPrompt = new SolvedPrompt();
				solvedPrompt.solved(this);
			}

			Moderator.newBadgeCollection.clear();
		}

		if (updateParameters
				&& (activityType == ActivityType.interactiveequation || activityType == ActivityType.editequation)) {
			HashMap<Parameter, String> parameterMap = new HashMap<Parameter, String>();
			parameterMap.put(Parameter.equation,
					equationTree.getEquationXMLString());
			parameterMap.put(Parameter.activity, activityType.toString());
			if (goalTree != null) {
				parameterMap.put(Parameter.goal,
						goalTree.getEquationXMLString());
			}
			if (systemOfEquations.hasMultipleEquations()) {
				parameterMap.put(Parameter.system,
						systemOfEquations.getURLParam());
			}

			URLParameters.setParameters(parameterMap, false);

		}

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				eqPanel.zoomToAndSelect(nodeIdToSelect);
			}
		});
	}

	public static void NUMBER_SPEC_PROMPT(EquationNode equationNode,
			boolean clearDisplays, boolean mustCheckUnits) {

		if (AlgebraActivity.numSpec == null) {
			AlgebraActivity.numSpec = new NumberPrompt(equationNode,
					clearDisplays, mustCheckUnits);
		} else {
			AlgebraActivity.numSpec.reload(equationNode, clearDisplays,
					mustCheckUnits);
		}
		AlgebraActivity.numSpec.appear();
	}

	public static void VARIABLE_SPEC_PROMPT(EquationNode equationNode,
			boolean clearDisplays) {
		if (AlgebraActivity.varSpec == null) {
			AlgebraActivity.varSpec = new VariablePrompt(equationNode,
					clearDisplays, false);
		} else {
			AlgebraActivity.varSpec.reload(equationNode, clearDisplays, false);
		}
		AlgebraActivity.varSpec.appear();
	}

	@Override
	protected void onDetach() {
		eqPanelHolder.clear();
		super.onDetach();
	}

	class CreateLinkWithGoalButton extends Button {

		CreateLinkWithGoalButton(String display, EquationTree initial) {
			super(display);

			final EquationTree initialCopy = new EquationTree(
					initial.getEquationXMLClone(), true);
			setStyleName(CSS.SAVE_EQUATION_BUTTON);
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					new LinkPrompt_Equation(AlgebraActivity.this, initialCopy)
							.appear();
				}
			});
		}
	}

}
