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
import com.sciencegadgets.client.algebra.edit.SaveButtonHandler;
import com.sciencegadgets.client.algebra.transformations.Rule;

public class AlgebraActivity extends Composite {

	interface AlgebraUiBinder extends UiBinder<AbsolutePanel, AlgebraActivity> {
	}

	private static AlgebraUiBinder algebraUiBinder = GWT
			.create(AlgebraUiBinder.class);

	@UiField
	public static SimplePanel eqPanelHolder;
	@UiField
	public static FlowPanel upperEqArea;
	@UiField
	public static FlowPanel upperRightEqArea;
	@UiField
	public static FlowPanel lowerEqArea;
	@UiField
	public static FlowPanel contextMenuArea;
	@UiField
	public static FlowPanel selectedMenu;
	@UiField
	public static Button optionsButton;
	public static EquationPanel eqPanel = null;
	public static AlgOut algOut = null;
	public static ChangeNodeMenu changeNodeMenu = new ChangeNodeMenu();
	private static Button saveEquationButton = new Button("Save Equation",
			new SaveButtonHandler());

	public static String focusLayerId = null;
	public static boolean isInEasyMode = false;
	public static boolean inEditMode = true;

	public AlgebraActivity() {
		initWidget(algebraUiBinder.createAndBindUi(this));

		optionsButton.addClickHandler(new OptionsHandler());

		if (inEditMode) {
			saveEquationButton.setSize("100%", "100%");
			saveEquationButton.setStyleName("saveEquationButton");
			upperRightEqArea.add(saveEquationButton);
		} else {
			algOut = new AlgOut();
			algOut.setSize("100%", "100%");
			upperRightEqArea.add(algOut);
		}

		if (inEditMode) {
			selectedMenu.add(changeNodeMenu);
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
					Moderator.mathTree.getHTMLAlgOut());
		}
		contextMenuArea.clear();
		eqPanelHolder.clear();

		Moderator.mathTree.validateTree();
		Moderator.mathTree.reloadEqHTML();
		eqPanel = new EquationPanel(Moderator.mathTree);
		eqPanelHolder.add(eqPanel);

		if (inEditMode) {
			changeNodeMenu.setVisible(false);
		} else {
			selectedMenu.clear();
		}

	}
}
