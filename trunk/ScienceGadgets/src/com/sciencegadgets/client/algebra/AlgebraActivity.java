package com.sciencegadgets.client.algebra;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.algebra.edit.SaveButtonHandler;
import com.sciencegadgets.client.algebra.transformations.Rule;

public class AlgebraActivity extends AbsolutePanel {

	public static SimplePanel eqPanelHolder = new SimplePanel();
	public static EquationPanel eqPanel = null;
	public static AlgOut algOut = null;
	public static FlowPanel upperEqArea = null;
	public static FlowPanel lowerEqArea = null;
	public static FlowPanel contextMenuArea = null;
	public static FlowPanel selectedMenu = null;
	public static ChangeNodeMenu changeNodeMenu = null;
	private static Button saveEquationButton = new Button("Save Equation",
			new SaveButtonHandler());
	public static Button optionsButton = null;
	public static String focusLayerId = null;
	public static boolean isInEasyMode = false;
	public static boolean inEditMode = false;

	public AlgebraActivity(){
		eqPanelHolder.clear();
		this.setSize("100%", "100%");
		this.clear();
		

		// Upper Area - 15%
		upperEqArea = new FlowPanel();
		upperEqArea.setSize("100%", "15%");
		this.add(upperEqArea);
		FlowPanel upperGap = new FlowPanel();
		upperGap.setSize("100%", "10%");
		this.add(upperGap);
		
		if(optionsButton == null){
		optionsButton = new Button("Options", new OptionsHandler());
		optionsButton.setSize("10%", "100%");
		optionsButton.addStyleName("layoutRow");
		}
		upperEqArea.add(optionsButton);
		
		
		if (inEditMode) {
			saveEquationButton.setSize("90%", "100%");
			saveEquationButton.setStyleName("saveEquationButton");
			saveEquationButton.addStyleName("layoutRow");
			upperEqArea.add(saveEquationButton);
		} else {
			algOut = new AlgOut();
			algOut.setSize("90%", "100%");
			algOut.addStyleName("layoutRow");
			upperEqArea.add(algOut);
		}

		// Equation Area - 70%
		eqPanelHolder.setSize("100%", "60%");
		this.add(eqPanelHolder);

		// Lower Area - 15%
		lowerEqArea = new FlowPanel();
		lowerEqArea.setSize("100%", "15%");
		this.add(lowerEqArea);
		// Context Menu Area
		contextMenuArea = new FlowPanel();
		contextMenuArea.addStyleName("layoutRow");
		contextMenuArea.setSize("30%", "100%");
		lowerEqArea.add(contextMenuArea);
		// SelectedMenuArea
		selectedMenu = new FlowPanel();
		selectedMenu.setSize("70%", "100%");
		selectedMenu.addStyleName("layoutRow");

		if (inEditMode) {
			if (changeNodeMenu == null) {
				changeNodeMenu = new ChangeNodeMenu();
			}
			selectedMenu.add(changeNodeMenu);
		}
		lowerEqArea.add(selectedMenu);
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
		}else{
			selectedMenu.clear();
		}
		
	}
}
