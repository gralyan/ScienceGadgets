package com.sciencegadgets.client.algebra;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.NumberSpecification;
import com.sciencegadgets.client.algebra.edit.SaveButtonHandler;
import com.sciencegadgets.client.algebra.edit.VariableSpecification;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;
import com.sciencegadgets.client.algebra.transformations.Rule;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;

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

	private static Button saveEquationButton = new Button("Save Equation",
			new SaveButtonHandler());

	private FlowPanel transformMain = new FlowPanel();
	private TransformationPanel bothSidesPanelLeft = new TransformationPanel();
	private TransformationPanel simplifyPanel = new TransformationPanel();
	private TransformationPanel bothSidesPanelRight = new TransformationPanel();
	// private TransformationButton simplifyButton = new
	// TransformationButton("Simplify", null);
	// private BothSidesPanelButton bothSidesLeft = new BothSidesPanelButton();
	// private BothSidesPanelButton bothSidesRight = new BothSidesPanelButton();

	public String focusLayerId = null;
	public boolean inEditMode = false;
	public static VariableSpecification varSpec;
	public static NumberSpecification numSpec;
	private EquationTree equationTree = null;

	public AlgebraActivity(Element equationXML, boolean inEditMode) {
		this(new EquationTree(equationXML, inEditMode), inEditMode);
	}

	public AlgebraActivity(EquationTree eTree, boolean inEditMode) {
		add(mainPanel);

		this.addStyleName(CSS.FILL_PARENT);
		mainPanel.addStyleName(CSS.FILL_PARENT);

		this.inEditMode = inEditMode;

		optionsButton.addClickHandler(new OptionsHandler(this));
		this.equationTree = eTree;

		if (inEditMode) {
			saveEquationButton.setStyleName("saveEquationButton");
			upperMidEqArea.add(saveEquationButton);
		} else {
			algOut = new AlgebraHistory(this);
			upperMidEqArea.add(algOut);

			transformMain.add(bothSidesPanelLeft);
			transformMain.add(simplifyPanel);
			transformMain.add(bothSidesPanelRight);
			transformMain.addStyleName(CSS.FILL_PARENT);
			bothSidesPanelLeft.addStyleName(CSS.BOTH_SIDES_PANEL);
			simplifyPanel.addStyleName(CSS.SIMPLIFY_PANEL);
			bothSidesPanelRight.addStyleName(CSS.BOTH_SIDES_PANEL);

		}

	}

	public EquationTree getEquationTree() {
		return equationTree;
	}

	public EquationTree reCreateEquationTree(Element equationXML,
			boolean inEditMode) {
		equationTree = new EquationTree(equationXML, inEditMode);
		return equationTree;
	}
	public void setEquationTree(EquationTree eTree) {
		equationTree = eTree;
	}

	public void reloadEquationPanel(String changeComment, Rule rule) {
		reloadEquationPanel(changeComment, rule, true);
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param changeComment
	 *            - use null for simple reload, specify change to add to AlgOut
	 */
	public void reloadEquationPanel(String changeComment, Rule rule,
			boolean updateHistory) {
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

		if (!inEditMode && changeComment != null) {
			algOut.updateAlgebraHistory(changeComment, rule, equationTree);
		}

		eqPanelHolder.clear();
		selectionDetails.clear();

		equationTree.reloadDisplay(true, true);
		eqPanel = new EquationPanel(this);
		eqPanelHolder.add(eqPanel);

		if (!inEditMode) {
			lowerEqArea.clear();
			algOut.scrollToBottom();
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

	public void fillTransformLists(
			TransformationList<TransformationButton> transSimplify,
			BothSidesTransformations transBothSides) {

		bothSidesPanelLeft.clear();
		simplifyPanel.clear();
		bothSidesPanelRight.clear();
		simplifyPanel.addAll(transSimplify);
		bothSidesPanelLeft.addAll(transBothSides);

		for (int i = transBothSides.size(); i > 0; i--) {
			BothSidesButton button = transBothSides.get(i - 1);
			bothSidesPanelRight.add(button.getJoinedButton());
		}
		lowerEqArea.clear();
		lowerEqArea.add(transformMain);
	}

	public static void NUMBER_SPEC_PROMPT(EquationNode equationNode,
			boolean clearDisplays) {

		if (AlgebraActivity.numSpec == null) {
			AlgebraActivity.numSpec = new NumberSpecification(equationNode,
					clearDisplays);
		} else {
			AlgebraActivity.numSpec.reload(equationNode, clearDisplays);
		}
		AlgebraActivity.numSpec.appear();
	}

	public static void VARIABLE_SPEC_PROMPT(EquationNode equationNode,
			boolean clearDisplays) {
		if (AlgebraActivity.varSpec == null) {
			AlgebraActivity.varSpec = new VariableSpecification(equationNode,
					clearDisplays);
		} else {
			AlgebraActivity.varSpec.reload(equationNode, clearDisplays);
		}
		AlgebraActivity.varSpec.appear();
	}

	@Override
	protected void onDetach() {
		eqPanelHolder.clear();
		super.onDetach();
	}

	class TransformationPanel extends CommunistPanel {

		public TransformationPanel() {
			super(true);
			addStyleName(CSS.FILL_PARENT);
		}

		@Override
		protected void onAttach() {
			super.onAttach();
			for (Widget child : getChildren()) {
				((TransformationButton) child).resize();
			}
		}
	}
}
