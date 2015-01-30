package com.sciencegadgets.client.algebra;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.LinkPrompt_Equation;
import com.sciencegadgets.client.algebra.edit.NumberSpecification;
import com.sciencegadgets.client.algebra.edit.SaveButtonHandler;
import com.sciencegadgets.client.algebra.edit.VariableSpecification;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.specification.SimplifyQuiz;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.client.ui.SelectionButton;
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
	public Button optionsButton;
	private OptionsHandler optionsHandler = null;
	@UiField
	public FlowPanel upperMidEqArea;
	@UiField
	public FlowPanel selectionDetails;

	@UiField
	public SimplePanel eqPanelHolder;

	@UiField
	public FlowPanel lowerEqArea;

	public EquationPanel eqPanel = null;

	public AlgebraHistory algOut = null;

	private FlowPanel transformMain = new FlowPanel();
	private TransformationPanel bothSidesPanelLeft = null;;
	private TransformationPanel simplifyButtonsPanel = null;
	private TransformationPanel bothSidesPanelRight = null;
	public SimplifyQuiz simplifyQuiz = null;
	private SimplifyPromptButton simplifyPromptButton = new SimplifyPromptButton();

	public String focusLayerId = null;
	public boolean inProgramaticTransformMode = false;
	public static VariableSpecification varSpec;
	public static NumberSpecification numSpec;
	private ActivityType activityType = null;

	private Equation equation = null;
	private EquationTree equationTree = null;
	private EquationTree goalTree = null;

	public AlgebraActivity(EquationTree eTree, Equation equation,
			ActivityType activityType) {
		add(mainPanel);

		this.addStyleName(CSS.FILL_PARENT);
		mainPanel.addStyleName(CSS.FILL_PARENT);

		this.equation = equation;
		this.activityType = activityType;

		switch (activityType) {
		case algebrasolve:
			if (optionsHandler == null) {
				optionsHandler = new OptionsHandler(this);
				optionsButton.addClickHandler(optionsHandler);
			}
			optionsButton.setVisible(true);

			algOut = new AlgebraHistory(this);
			upperMidEqArea.add(algOut);

			transformMain
					.add(bothSidesPanelLeft = new TransformationPanel(true));
			transformMain.add(simplifyButtonsPanel = new TransformationPanel(
					false));
			transformMain.add(bothSidesPanelRight = new TransformationPanel(
					true));
			transformMain.addStyleName(CSS.FILL_PARENT);
			break;
		case algebraedit:
			if (optionsHandler == null) {
				optionsHandler = new OptionsHandler(this);
				optionsButton.addClickHandler(optionsHandler);
			}
			optionsButton.setVisible(true);

			Button createLinkButton = new Button("Create Link",
					new ClickHandler() {
						@Override
						public void onClick(ClickEvent arg0) {
							new LinkPrompt_Equation(AlgebraActivity.this, null)
									.appear();
						}
					});
			createLinkButton.setStyleName(CSS.SAVE_EQUATION_BUTTON);
			upperMidEqArea.add(createLinkButton);

			Button saveEquationButton = new Button("Create Challenge",
					new SaveButtonHandler());
			saveEquationButton.setStyleName(CSS.SAVE_EQUATION_BUTTON);
			upperMidEqArea.add(saveEquationButton);
			break;
		case algebrasetgoal:
			optionsButton.setVisible(false);

			CreateLinkWithGoalButton createLinkWithGoalButton = new CreateLinkWithGoalButton(
					"Create Link with this Goal", eTree);
			upperMidEqArea.add(createLinkWithGoalButton);

			transformMain
					.add(bothSidesPanelLeft = new TransformationPanel(true));
			transformMain.add(simplifyButtonsPanel = new TransformationPanel(
					false));
			transformMain.add(bothSidesPanelRight = new TransformationPanel(
					true));
			transformMain.addStyleName(CSS.FILL_PARENT);
			break;
		case algebrasimplifyquiz:
			optionsButton.setVisible(false);
			break;
		default:
			break;
		}

		this.equationTree = eTree;

		String goal = URLParameters.getParameter(Parameter.goal);
		if (goal != null && !"".equals(goal)) {
			Element goalEl = new HTML(goal).getElement().getFirstChildElement();
			goalTree = new EquationTree(goalEl, false);
		}
	}

	public Equation getEquation() {
		return equation;
	}

	public void updateEquation() {
		equation.reCreate(equationTree);
	}

	public EquationTree getEquationTree() {
		return equationTree;
	}

	public void setEquationTree(EquationTree eTree, Equation equation) {
		this.equation = equation;
		equationTree = eTree;
	}

	public boolean isInEditMode() {
		return activityType.isInEditMode();
	}

	public ActivityType getActivityType() {
		return activityType;
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param changeComment
	 *            - use null for simple reload, specify change to add to AlgOut
	 */
	public void reloadEquationPanel(String changeComment,
			HashMap<Skill, Integer> skillsIncrease, boolean updateParameters) {

		if (activityType == ActivityType.algebrasolve && changeComment != null
				&& skillsIncrease != null && !skillsIncrease.isEmpty()) {
			algOut.updateAlgebraHistory(changeComment, (Skill) skillsIncrease
					.keySet().toArray()[0], equationTree);
		}

		if (inProgramaticTransformMode) {
			return;
		} else if (activityType == ActivityType.algebrasimplifyquiz) {
			equationTree.getEquals().setSymbol(
					TypeSGET.Operator.ARROW_RIGHT.getSign());
		} else if (simplifyQuiz != null) {
			simplifyQuiz.disappear();
			simplifyQuiz = null;
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
		selectionDetails.clear();

		equationTree.reloadDisplay(true, true);
		eqPanel = new EquationPanel(this);

		eqPanelHolder.add(eqPanel);

		if (activityType == ActivityType.algebrasolve) {
			lowerEqArea.clear();
			algOut.scrollToBottom();

			TypeSGET rightType = equationTree.getRightSide().getType();
			TypeSGET leftType = equationTree.getLeftSide().getType();
			// Check if evaluated
			if ((TypeSGET.Variable.equals(leftType) && TypeSGET.Number
					.equals(rightType))
					|| (TypeSGET.Variable.equals(rightType) && TypeSGET.Number
							.equals(leftType))) {
				SolvedPrompt solvedPrompt = new SolvedPrompt();
				solvedPrompt.solved(this);
			} else if (goalTree != null && equationTree.isLike(goalTree)) {
				// Check if equation matches goal
				SolvedPrompt solvedPrompt = new SolvedPrompt();
				solvedPrompt.solved(this);
			}
		}

		if (updateParameters
				&& activityType != ActivityType.algebrasimplifyquiz
				&& activityType != ActivityType.algebrasetgoal) {
			HashMap<Parameter, String> parameterMap = new HashMap<Parameter, String>();
			// if (isInEditMode()) {
			// parameterMap.put(Parameter.activity,
			// ActivityType.algebraedit.toString());
			// } else {
			// parameterMap.put(Parameter.activity,
			// ActivityType.algebrasolve.toString());
			// }
			parameterMap.put(Parameter.equation,
					equationTree.getEquationXMLString());
			parameterMap.put(Parameter.activity, activityType.toString());
			if (goalTree != null) {
				parameterMap.put(Parameter.goal,
						goalTree.getEquationXMLString());
			}
			URLParameters.setParameters(parameterMap, false);
		}
	}

	public void clearTransformLists() {
		bothSidesPanelLeft.clear();
		bothSidesPanelRight.clear();
		simplifyButtonsPanel.clear();
		lowerEqArea.clear();
	}

	public void fillTransformLists(
			TransformationList<TransformationButton> transSimplify,
			BothSidesTransformations transBothSides) {

		clearTransformLists();

		// Both Sides Buttons
		bothSidesPanelLeft.addAll(transBothSides);

		for (int i = transBothSides.size(); i > 0; i--) {
			BothSidesButton button = transBothSides.get(i - 1);
			bothSidesPanelRight.add(button.getJoinedButton());
		}

		// Simplify Buttons
		LinkedList<SelectionButton> buttonsShown = new LinkedList<SelectionButton>();
		simplifyPromptButton.setTransformationList(transSimplify);

		for (TransformationButton tButt : transSimplify) {
			if (tButt.meetsAutoTransform()) {
				buttonsShown.add(tButt);
			}
		}
		if (transSimplify.size() > buttonsShown.size()) {
			buttonsShown.add(simplifyPromptButton);
		}

		simplifyButtonsPanel.addAll(buttonsShown);

		lowerEqArea.add(transformMain);
	}

	public static void NUMBER_SPEC_PROMPT(EquationNode equationNode,
			boolean clearDisplays, boolean mustCheckUnits) {

		if (AlgebraActivity.numSpec == null) {
			AlgebraActivity.numSpec = new NumberSpecification(equationNode,
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
			AlgebraActivity.varSpec = new VariableSpecification(equationNode,
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

	public class TransformationPanel extends CommunistPanel {

		public TransformationPanel(boolean isBothSidesPanel) {
			super(true);
			addStyleName(CSS.FILL_PARENT);

			if (isBothSidesPanel) {
				addStyleName(CSS.BOTH_SIDES_PANEL);
			} else {
				addStyleName(CSS.SIMPLIFY_PANEL);
			}
		}

		@Override
		protected void onAttach() {
			super.onAttach();
			for (Widget child : getChildren()) {
				((SelectionButton) child).resize();
			}
		}
	}

	class SimplifyPromptButton extends SelectionButton {

		TransformationList<TransformationButton> transSimplify;

		SimplifyPromptButton() {
			setHTML("Simplify");
			addStyleName(CSS.TRANSFORMATION_BUTTON + " " + CSS.LAYOUT_ROW);
		}

		@Override
		protected void onSelect() {
			simplifyQuiz = new SimplifyQuiz(eqPanel.selectedWrapper.node,
					transSimplify);
			simplifyQuiz.appear();
		}

		public void setTransformationList(
				TransformationList<TransformationButton> transSimplify) {
			this.transSimplify = transSimplify;
		}
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
