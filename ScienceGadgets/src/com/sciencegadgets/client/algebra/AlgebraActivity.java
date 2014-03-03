package com.sciencegadgets.client.algebra;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.algebra.edit.NumberSpecification;
import com.sciencegadgets.client.algebra.edit.SaveButtonHandler;
import com.sciencegadgets.client.algebra.edit.VariableSpecification;
import com.sciencegadgets.client.algebra.transformations.Rule;
import com.sciencegadgets.client.conversion.Constant;

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

	public AlgOut algOut = null;
	
	private static Button saveEquationButton = new Button("Save Equation",
			new SaveButtonHandler());

	public String focusLayerId = null;
	public boolean inEditMode = false;
	public static VariableSpecification varSpec;
	public static NumberSpecification numSpec;
	private MathTree mathTree = null;

	public AlgebraActivity(Element mathML, boolean inEditMode) {
		initWidget(algebraUiBinder.createAndBindUi(this));
		
		this.inEditMode = inEditMode;
		mathTree = new MathTree(mathML, inEditMode);

		optionsButton.addClickHandler(new OptionsHandler(this));

		if (inEditMode) {
			saveEquationButton.setStyleName("saveEquationButton");
			upperMidEqArea.add(saveEquationButton);
		} else {
			algOut = new AlgOut(this);
			upperMidEqArea.add(algOut);
		}
		

	}
	
	public MathTree getMathTree() {
		return mathTree;
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param changeComment
	 *            - use null for simple reload, specify change to add to AlgOut
	 */
	public void reloadEquationPanel(String changeComment, Rule rule) {
		if (!inEditMode && changeComment != null) {
			algOut.updateAlgOut(changeComment, rule,
					mathTree);
		}
		
		eqPanelHolder.clear();
		selectionDetails.clear();

		mathTree.validateTree();
		mathTree.reloadDisplay(true);
		eqPanel = new EquationPanel(this);
		eqPanelHolder.add(eqPanel);

		if (!inEditMode) {
			lowerEqArea.clear();
			algOut.scrollToBottom();
		}

	}

	@Override
	protected void onDetach() {
		eqPanelHolder.clear();
		super.onDetach();
	}
	
}
