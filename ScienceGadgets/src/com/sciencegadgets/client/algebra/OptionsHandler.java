package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.ui.ToggleSlide;

public class OptionsHandler implements ClickHandler {

	public static PopupPanel optionsPopup = new PopupPanel();
	OptionsPanel optionsPanel = new OptionsPanel();
	Button closeOption = new Button("Close",new CloseClickHandler());
	AlgebraActivity algebraActivity;
	private ToggleSlide editSolveOption;
	private ToggleSlide easyHardOption;

	public OptionsHandler(AlgebraActivity algebraActivity) {
		this.algebraActivity = algebraActivity;
		
		optionsPopup.clear();

		AbsolutePanel mainPanel = Moderator.scienceGadgetArea;

		int optionsButtonHeight = algebraActivity.upperEqArea.getOffsetHeight();
		optionsPopup.getElement().getStyle().setBackgroundColor("white");
		optionsPopup.setPixelSize(mainPanel.getOffsetWidth() / 4,
				mainPanel.getOffsetHeight() - optionsButtonHeight);
		optionsPopup.setPopupPosition(mainPanel.getAbsoluteLeft(),
				mainPanel.getAbsoluteTop() + optionsButtonHeight);
		optionsPopup.getElement().getStyle().setZIndex(10);

		optionsPanel.getElement().getStyle().setOverflowY(Overflow.SCROLL);
		optionsPopup.add(optionsPanel);
		
		optionsPopup.hide();
		optionsPopup.setAutoHideEnabled(true);
	    }

	@Override
	public void onClick(ClickEvent event) {
		optionsPanel.clear();
		
		optionsPanel.add(closeOption);

		if (!algebraActivity.isInEditMode()) {
			easyHardOption = new ToggleSlide("Easy", "Hard", Moderator.isInEasyMode, new EasyHardClickHandler());
			optionsPanel.add(easyHardOption);
		}
		
		editSolveOption = new ToggleSlide("Edit", "Solve", algebraActivity.isInEditMode(), new EditSolveClickHandler(algebraActivity));
		optionsPanel.add(editSolveOption);
		
		optionsPopup.show();
	}

	public void enableEditSolve(boolean enable) {
		editSolveOption.enable(enable);
	}
}

class OptionsPanel extends FlowPanel{
	@Override
	public void add(Widget w) {
		w.setSize("100%", "15%");
		super.add(w);
	}
}

class CloseClickHandler implements ClickHandler {
	
	@Override
	public void onClick(ClickEvent event) {
		OptionsHandler.optionsPopup.hide();
	}
	
}
class EasyHardClickHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {
		OptionsHandler.optionsPopup.hide();
		Moderator.isInEasyMode = !Moderator.isInEasyMode;
		Moderator.reloadEquationPanel();
	}

}

class EditSolveClickHandler implements ClickHandler {
	AlgebraActivity algebraActivity;
	EditSolveClickHandler(AlgebraActivity algebraActivity){
		this.algebraActivity = algebraActivity;
	}
	@Override
	public void onClick(ClickEvent event) {
		String equation = algebraActivity.getEquationTree().getRoot().toString();
		if (equation.contains(ChangeNodeMenu.NOT_SET)) {
			Window.alert("All new entities (" + ChangeNodeMenu.NOT_SET
					+ ") must be set or removed before solving");
			return;
		}

		OptionsHandler.optionsPopup.hide();
		if (algebraActivity.eqPanel != null && algebraActivity.eqPanel.selectedWrapper != null) {
			algebraActivity.eqPanel.selectedWrapper.unselect();
		}
		ActivityType activityType = algebraActivity.getActivityType() == ActivityType.algebraedit ? ActivityType.algebrasolve : ActivityType.algebraedit;
		Moderator.switchToAlgebra(Moderator.getCurrentEquationTree().getEquationXMLClone(), true, activityType, true);
	}

}
