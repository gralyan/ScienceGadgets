package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;

public class OptionsHandler implements ClickHandler {

	PopupPanel optionsPopup = new PopupPanel();
	FlowPanel optionsPanel = new FlowPanel();
	MenuOption easyHardOption = new MenuOption(new EasyHardClickHandler(this));
	MenuOption editSolveOption = new MenuOption(new EditSolveClickHandler(this));

	public OptionsHandler() {

		AbsolutePanel mainPanel = Moderator.scienceGadgetArea;

		int optionsButtonHeight = AlgebraActivity.upperEqArea.getOffsetHeight();
		optionsPopup.getElement().getStyle().setBackgroundColor("white");
		optionsPopup.setPixelSize(mainPanel.getOffsetWidth() / 4,
				mainPanel.getOffsetHeight() - optionsButtonHeight);
		optionsPopup.setPopupPosition(mainPanel.getAbsoluteLeft(),
				mainPanel.getAbsoluteTop() + optionsButtonHeight);
		optionsPopup.getElement().getStyle().setZIndex(10);

		optionsPopup.add(optionsPanel);
		optionsPopup.hide();
		optionsPopup.setAutoHideEnabled(true);
	}

	@Override
	public void onClick(ClickEvent event) {
		optionsPanel.clear();

		if (!AlgebraActivity.inEditMode) {
			String easyOptionText = AlgebraActivity.isInEasyMode ? "<b>Easy</b> Hard"
					: "Easy <b>Hard</b>";
			easyHardOption.setHTML(easyOptionText);
			optionsPanel.add(easyHardOption);
		}
		
		String editOptionText = AlgebraActivity.inEditMode ? "<b>Edit</b> Solve"
				: "Edit <b>Solve</b>";
		editSolveOption.setHTML(editOptionText);
		optionsPanel.add(editSolveOption);
		
		optionsPopup.show();
	}

}

class MenuOption extends Button {

	MenuOption(ClickHandler clickHandler) {
		super();
		addClickHandler(clickHandler);
		getElement().getStyle().setDisplay(Display.BLOCK);

	}
}

class EasyHardClickHandler implements ClickHandler {
	OptionsHandler optionsHandler;

	public EasyHardClickHandler(OptionsHandler optionsHandler) {
		this.optionsHandler = optionsHandler;
	}

	@Override
	public void onClick(ClickEvent event) {
		optionsHandler.optionsPopup.hide();
		AlgebraActivity.isInEasyMode = !AlgebraActivity.isInEasyMode;
		Moderator.reloadEquationPanel(null);
	}

}

class EditSolveClickHandler implements ClickHandler {
	OptionsHandler optionsHandler;

	public EditSolveClickHandler(OptionsHandler optionsHandler) {
		this.optionsHandler = optionsHandler;
	}

	@Override
	public void onClick(ClickEvent event) {
		String equation = Moderator.mathTree.getRoot().toString();
		if (equation.contains(ChangeNodeMenu.NOT_SET)) {
			Window.alert("All new entities (" + ChangeNodeMenu.NOT_SET
					+ ") must be set or removed before solving");
			return;
		}

		optionsHandler.optionsPopup.hide();
		if (EquationPanel.selectedWrapper != null) {
			EquationPanel.selectedWrapper.unselect(AlgebraActivity.inEditMode);
		}
		AlgebraActivity.inEditMode = !AlgebraActivity.inEditMode;
		Moderator.makeAlgebraWorkspace(Moderator.mathTree.getMathMLClone());
	}

}
