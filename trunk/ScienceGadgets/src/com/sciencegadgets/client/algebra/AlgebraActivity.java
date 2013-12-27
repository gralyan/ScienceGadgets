package com.sciencegadgets.client.algebra;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
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
	static FlowPanel upperEqArea;
	@UiField
	public static Button optionsButton;
	@UiField
	public static FlowPanel upperRightEqArea;

	@UiField
	public static SimplePanel eqPanelHolder;

	@UiField
	public static FlowPanel lowerEqArea;
	
	public static EquationPanel eqPanel = null;

	public static FlowPanel algTransformMenu = new FlowPanel();
	public static FlowPanel bothSidesButtonMenu = new FlowPanel();
	public static AlgOut algOut = null;
	
	private static Button saveEquationButton = new Button("Save Equation",
			new SaveButtonHandler());

	public static String focusLayerId = null;
	public static boolean isInEasyMode = false;
	public static boolean inEditMode = true;
	public static VariableSpecification varSpec;
	public static NumberSpecification numSpec;

	public AlgebraActivity() {
		initWidget(algebraUiBinder.createAndBindUi(this));

		optionsButton.addClickHandler(new OptionsHandler());

		if (inEditMode) {
			saveEquationButton.setStyleName("saveEquationButton");
			upperRightEqArea.add(saveEquationButton);
		} else {
			algOut = new AlgOut();
			upperRightEqArea.add(algOut);
			
			algTransformMenu.addStyleName("layoutRow");
			algTransformMenu.setSize("30%", "100%");
			lowerEqArea.add(algTransformMenu);
			
			bothSidesButtonMenu.addStyleName("layoutRow");
			bothSidesButtonMenu.setSize("70%", "100%");
			lowerEqArea.add(bothSidesButtonMenu);
		}
		

	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param changeComment
	 *            - use null for simple reload, specify change to add to AlgOut
	 */
	public static void reloadEquationPanel(String changeComment, Rule rule) {
		if (changeComment != null) {
			algOut.updateAlgOut(changeComment, rule,
					Moderator.mathTree.getHTML());
		}
		eqPanelHolder.clear();

		Moderator.mathTree.validateTree();
		Moderator.mathTree.reloadEqHTML(true);
		eqPanel = new EquationPanel(Moderator.mathTree);
		eqPanelHolder.add(eqPanel);

		if (!inEditMode) {
			algTransformMenu.clear();
			bothSidesButtonMenu.clear();
		}

	}
}
