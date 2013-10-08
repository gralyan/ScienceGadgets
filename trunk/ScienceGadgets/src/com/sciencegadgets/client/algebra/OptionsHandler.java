package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.sciencegadgets.client.Moderator;

public class OptionsHandler implements ClickHandler {

	PopupPanel optionsPopup = new PopupPanel();
	FlowPanel optionsPanel = new FlowPanel();
	MenuOption easyHardOption = new MenuOption(new EasyHardClickHandler(this));
	MenuOption editSolveOption = new MenuOption(new EditSolveClickHandler(this));

	public OptionsHandler() {

		AbsolutePanel mainPanel = Moderator.scienceGadgetArea;

		int optionsButtonHeight = Moderator.upperEqArea.getOffsetHeight();
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

		if (!Moderator.inEditMode) {
			String easyOptionText = Moderator.isInEasyMode ? "<b>Easy</b> Hard"
					: "Easy <b>Hard</b>";
			easyHardOption.setHTML(easyOptionText);
			optionsPanel.add(easyHardOption);
		}
		
		String editOptionText = Moderator.inEditMode ? "<b>Edit</b> Solve"
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
		System.out.println("easy Clicked");
		optionsHandler.optionsPopup.hide();
		System.out.println("inEasy: " + Moderator.isInEasyMode);
		Moderator.isInEasyMode = !Moderator.isInEasyMode;
		System.out.println("inEasy: " + Moderator.isInEasyMode);
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
		System.out.println("edit Clicked");
		optionsHandler.optionsPopup.hide();
		System.out.println("inEdit: " + Moderator.inEditMode);
		Moderator.inEditMode = !Moderator.inEditMode;
		System.out.println("inEdit: " + Moderator.inEditMode);
		Moderator.makeAlgebraWorkspace(Moderator.mathTree.getMathMLClone());
	}

}
