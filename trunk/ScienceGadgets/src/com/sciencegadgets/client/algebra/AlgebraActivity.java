package com.sciencegadgets.client.algebra;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PopupPanel.PositionCallback;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.NumberSpecification;
import com.sciencegadgets.client.algebra.edit.SaveButtonHandler;
import com.sciencegadgets.client.algebra.edit.VariableSpecification;
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

	private CommunistPanel transformMain = new CommunistPanel(true);
	private TransformPanel transformSimplify = new TransformPanel();
	private TransformPanel transformBothSides = new TransformPanel();
	private TransformPanel transformFactorize = new TransformPanel();

	public String focusLayerId = null;
	public boolean inEditMode = false;
	public static VariableSpecification varSpec;
	public static NumberSpecification numSpec;
	private EquationTree equationTree = null;

	public AlgebraActivity(Element equationXML, boolean inEditMode) {
		
		add(mainPanel);

		this.addStyleName(CSS.FILL_PARENT);
		mainPanel.addStyleName(CSS.FILL_PARENT);

		this.inEditMode = inEditMode;
		reCreateEquationTree(equationXML, inEditMode);

		optionsButton.addClickHandler(new OptionsHandler(this));

		if (inEditMode) {
			saveEquationButton.setStyleName("saveEquationButton");
			upperMidEqArea.add(saveEquationButton);
		} else {
			algOut = new AlgebraHistory(this);
			upperMidEqArea.add(algOut);

			transformMain.addStyleName(CSS.FILL_PARENT);
			transformMain.add(new TransformPanelButton("Factorize",transformFactorize));
			transformMain.add(new TransformPanelButton("Both Sides",transformBothSides));
			transformMain.add(new TransformPanelButton("Simplify",transformSimplify));
			
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
	public void reloadEquationPanel(String changeComment, Rule rule,
			boolean updateHistory) {
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
			transformSimplify.hide();
			transformBothSides.hide();
			transformFactorize.hide();
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

	public void fillTransformLists(EquationNode node) {
		TransformationList[] transorms = TransformationList.FIND_ALL(node);
		transformFactorize.clearTransformationButtons();
		for (TransformationButton button : transorms[0]) {
			transformFactorize.addTransformationButton(button);
		}
		transformBothSides.clearTransformationButtons();
		for (TransformationButton button : transorms[1]) {
			transformBothSides.addTransformationButton(button);
		}
		transformSimplify.clearTransformationButtons();
		for (TransformationButton button : transorms[2]) {
			transformSimplify.addTransformationButton(button);
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

	class TransformPanelButton extends Button {
		TransformPanel transformPanel;
		TransformPanelButton(String label, TransformPanel transformPanel) {
			this.transformPanel = transformPanel;
			
			setText(label);
			
			if (Moderator.isTouch) {
				addTouchEndHandler(new TouchEndHandler() {
					@Override
					public void onTouchEnd(TouchEndEvent event) {
						select();
					}
				});
			} else {
				addClickHandler(new ClickHandler() {
					@Override
					public void onClick(ClickEvent event) {
						select();
					}
				});
			}
		}

		void select() {
			int buttonWidth = this.getOffsetWidth();
			int buttonHeight = this.getOffsetHeight();
			int panelHeight = buttonHeight * transformPanel.getWidgetCount();
			
			transformPanel.setPixelSize(buttonWidth, panelHeight);
//			mainPanel.add(transformPanel, this.getAbsoluteLeft(), this.getAbsoluteTop()-panelHeight);
			transformPanel.showRelativeTo(this);
			
			for(int i = 0 ; i<transformPanel.getWidgetCount() ; i++) {
				TransformationButton button = transformPanel.getTransformationButton(i);
				button.setPixelSize(buttonWidth, buttonHeight);
				button.resize();
			}
		}
	}
	
	class TransformPanel extends PopupPanel{
		FlowPanel flowPanel = new FlowPanel();
		
		TransformPanel(){
			super(true);
			flowPanel.addStyleName(CSS.FILL_PARENT);
			this.getElement().getStyle().setZIndex(3);
			this.add(flowPanel);
		}
		
		public void addTransformationButton(TransformationButton w) {
			flowPanel.add(w);
		}
		public void clearTransformationButtons() {
			flowPanel.clear();
		}
		int getWidgetCount(){
			return flowPanel.getWidgetCount();
		}
		TransformationButton getTransformationButton(int i){
			return (TransformationButton) flowPanel.getWidget(i);
		}
	}
}
