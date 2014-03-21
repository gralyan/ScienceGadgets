package com.sciencegadgets.client.algebra;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.NumberSpecification;
import com.sciencegadgets.client.algebra.edit.SaveButtonHandler;
import com.sciencegadgets.client.algebra.edit.VariableSpecification;
import com.sciencegadgets.client.algebra.transformations.Rule;

public class AlgebraActivity extends Composite {

	interface AlgebraUiBinder extends UiBinder<AbsolutePanel, AlgebraActivity> {
	}

	private static AlgebraUiBinder algebraUiBinder = GWT
			.create(AlgebraUiBinder.class);

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

	public String focusLayerId = null;
	public boolean inEditMode = false;
	public static VariableSpecification varSpec;
	public static NumberSpecification numSpec;
	private EquationTree equationTree = null;

	public AlgebraActivity(Element equationXML, boolean inEditMode) {
		initWidget(algebraUiBinder.createAndBindUi(this));

		this.inEditMode = inEditMode;
		reCreateEquationTree(equationXML, inEditMode);

		optionsButton.addClickHandler(new OptionsHandler(this));

		if (inEditMode) {
			saveEquationButton.setStyleName("saveEquationButton");
			upperMidEqArea.add(saveEquationButton);
		} else {
			algOut = new AlgebraHistory(this);
			upperMidEqArea.add(algOut);
		}

	}

	public EquationTree getEquationTree() {
		return equationTree;
	}
	
	public void reCreateEquationTree(Element equationXML, boolean inEditMode) {
		equationTree = new EquationTree(equationXML, inEditMode);
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
	public void reloadEquationPanel(String changeComment, Rule rule, boolean updateHistory) {
		if (!inEditMode && changeComment != null) {
			algOut.updateAlgebraHistory(changeComment, rule, equationTree);
		}

		eqPanelHolder.clear();
		selectionDetails.clear();

		equationTree.validateTree();
		equationTree.reloadDisplay(true, true);
		eqPanel = new EquationPanel(this);
		eqPanelHolder.add(eqPanel);

		if (!inEditMode) {
			lowerEqArea.clear();
			algOut.scrollToBottom();
		}

		if(updateHistory) {
		HashMap<Parameter, String> parameterMap = new HashMap<Parameter, String>();
		if (inEditMode) {
			parameterMap.put(Parameter.activity,
					ActivityType.algebraedit.toString());
		} else {
			parameterMap.put(Parameter.activity,
					ActivityType.algebrasolve.toString());
		}
		parameterMap.put(Parameter.equation, equationTree.getEquationXMLString());
		URLParameters.setParameters(parameterMap, false);
		}
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

}
