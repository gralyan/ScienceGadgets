package com.sciencegadgets.client.algebra;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.LinkPrompt;
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
	private TransformationPanel bothSidesPanelLeft = new TransformationPanel();
	private TransformationPanel simplifyButtonsPanel = new TransformationPanel();
	private TransformationPanel bothSidesPanelRight = new TransformationPanel();
	public SimplifyQuiz simplifyQuiz;
	private SimplifyPromptButton simplifyPromptButton = new SimplifyPromptButton();

	public String focusLayerId = null;
	public boolean inEditMode = false;
	public boolean isSimplifyQuiz = false;
	public boolean inProgramaticTransformMode = false;
	public static VariableSpecification varSpec;
	public static NumberSpecification numSpec;
	private EquationTree equationTree = null;
	private Equation equation = null;

//	public AlgebraActivity(Element equationXML, boolean inEditMode,
//			boolean isSimplifyQuiz) {
//		this(new EquationTree(equationXML, inEditMode), inEditMode,
//				isSimplifyQuiz);
//	}

	public AlgebraActivity(EquationTree eTree, Equation equation, boolean inEditMode,
			boolean isSimplifyQuiz) {
		add(mainPanel);

		this.addStyleName(CSS.FILL_PARENT);
		mainPanel.addStyleName(CSS.FILL_PARENT);

		this.equation = equation;
		this.inEditMode = inEditMode;
		this.isSimplifyQuiz = isSimplifyQuiz;

		if (isSimplifyQuiz) {
			optionsButton.setVisible(false);
		} else {
			optionsHandler = new OptionsHandler(this);
			optionsButton.addClickHandler(optionsHandler);
		}

		if (inEditMode) {
			if (!isSimplifyQuiz) {
				String user = URLParameters.getParameter(Parameter.user);
				if(URLParameters.USER_ADMIN.equals(user)) {
					Button createLinkButton = new Button("Create Link",
							new ClickHandler() {
								@Override
								public void onClick(ClickEvent arg0) {
									new LinkPrompt(AlgebraActivity.this).appear();
								}
							});
					createLinkButton.setStyleName(CSS.SAVE_EQUATION_BUTTON);
					upperMidEqArea.add(createLinkButton);
				}else{
					Button saveEquationButton = new Button("Save Equation",
							new SaveButtonHandler());
					saveEquationButton.setStyleName(CSS.SAVE_EQUATION_BUTTON);
					upperMidEqArea.add(saveEquationButton);
				}
			}
		} else {
			algOut = new AlgebraHistory(this);
			upperMidEqArea.add(algOut);

			transformMain.add(bothSidesPanelLeft);
			transformMain.add(simplifyButtonsPanel);
			transformMain.add(bothSidesPanelRight);
			transformMain.addStyleName(CSS.FILL_PARENT);
			bothSidesPanelLeft.addStyleName(CSS.BOTH_SIDES_PANEL);
			simplifyButtonsPanel.addStyleName(CSS.SIMPLIFY_PANEL);
			bothSidesPanelRight.addStyleName(CSS.BOTH_SIDES_PANEL);
		}

		this.equationTree = eTree;
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

	// public EquationTree reCreateEquationTree(Element equationXML,
	// boolean inEditMode) {
	// equationTree = new EquationTree(equationXML, inEditMode);
	// return equationTree;
	// }
	//
	// public void reloadEquationPanel(String changeComment, HashMap<Skill,
	// Integer> skillsIncrease) {
	// reloadEquationPanel(changeComment, skillsIncrease, true);
	// }

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param changeComment
	 *            - use null for simple reload, specify change to add to AlgOut
	 */
	public void reloadEquationPanel(String changeComment,
			HashMap<Skill, Integer> skillsIncrease, boolean updateHistory) {

		if (!inEditMode && changeComment != null && skillsIncrease != null
				&& !skillsIncrease.isEmpty()) {
			algOut.updateAlgebraHistory(changeComment, (Skill) skillsIncrease
					.keySet().toArray()[0], equationTree);
		}

		if (inProgramaticTransformMode) {
			return;
		} else if (isSimplifyQuiz) {
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

		if (!inEditMode) {
			lowerEqArea.clear();
			algOut.scrollToBottom();
			
			TypeSGET rightType = equationTree.getRightSide().getType();
			TypeSGET leftType = equationTree.getLeftSide().getType();
			if ((TypeSGET.Variable.equals(leftType) && TypeSGET.Number.equals(rightType))
			|| (TypeSGET.Variable.equals(rightType) && TypeSGET.Number.equals(leftType))) {
				SolvedPrompt solvedPrompt = new SolvedPrompt();
				solvedPrompt.solved(this);
			}
		}

		if (updateHistory) {
			HashMap<Parameter, String> parameterMap = new HashMap<Parameter, String>();
			if (inEditMode) {
				parameterMap.put(Parameter.activity,
						ActivityType.algebraedit.toString());
			} else {
				parameterMap.put(Parameter.activity,
						ActivityType.algebrasolve.toString());
			}
			parameterMap.put(Parameter.equation,
					equationTree.getEquationXMLString());
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

		public TransformationPanel() {
			super(true);
			addStyleName(CSS.FILL_PARENT);
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
}
