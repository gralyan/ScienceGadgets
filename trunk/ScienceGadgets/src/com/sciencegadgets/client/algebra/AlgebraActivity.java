package com.sciencegadgets.client.algebra;

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
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

	private FlowPanel transformMain = new FlowPanel();
	private TransformationPanel transformSimplify = new TransformationPanel();
	private TransformationPanel transformBothSides = new TransformationPanel();
	private TransformationButton simplifyButton = new TransformationButton("Simplify", null);
	private BothSidesPanelButton bothSidesLeft = new BothSidesPanelButton();
	private BothSidesPanelButton bothSidesRight = new BothSidesPanelButton();

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
			
			bothSidesLeft.setOther(bothSidesRight);
			bothSidesRight.setOther(bothSidesLeft);
			simplifyButton.addStyleName(CSS.SIMPLIFY_PANEL_BUTTON);
			simplifyButton.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					bothSidesLeft.deselect();
					bothSidesRight.deselect();
					lowerEqArea.clear();
					lowerEqArea.add(transformSimplify);
				}
			});

			transformMain.addStyleName(CSS.FILL_PARENT);
			transformMain.add(bothSidesLeft);
			transformMain.add(simplifyButton);
			transformMain.add(bothSidesRight);
			
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
		TransformationList[] transforms = TransformationList.FIND_ALL(node);
		
		transformSimplify.clear();
		transformBothSides.clear();
		transformSimplify.addAll(transforms[0]);
		transformBothSides.addAll(transforms[1]);
		
		bothSidesLeft.deselect();
		bothSidesRight.deselect();

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
	
	class TransformationPanel extends CommunistPanel{

		public TransformationPanel() {
			super(true);
			addStyleName(CSS.FILL_PARENT);
		}
		@Override
		protected void onAttach() {
			super.onAttach();
			for(Widget child : getChildren()) {
				((TransformationButton)child).resize();
			}
		}
	}
	
	class BothSidesPanelButton extends TransformationButton {
		BothSidesPanelButton other;
		private static final String UNSELECTED_TEXT = "\u2191";
		private static final String SELECTED_TEXT = "\u21E7";
		BothSidesPanelButton(){
			super(UNSELECTED_TEXT, null);
			addStyleName(CSS.BOTH_SIDES_PANEL_BUTTON);
		}
		void setOther(final BothSidesPanelButton other){
			this.other = other;
			
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if(other.isSelected()) {
						other.deselect();
						lowerEqArea.clear();
						lowerEqArea.add(transformBothSides);
					}else {
						select();
					}
				}
			});
		}
		boolean isSelected() {
			return SELECTED_TEXT.equals(getHTML());
		}
		void select(){
			setHTML(SELECTED_TEXT);
		}
		void deselect() {
			setHTML(UNSELECTED_TEXT);
		}
	}
}
