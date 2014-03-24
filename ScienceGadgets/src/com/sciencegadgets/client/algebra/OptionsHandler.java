package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.ToggleSlide;

public class OptionsHandler implements ClickHandler {

	public static PopupPanel optionsPopup = new PopupPanel();
	OptionsPanel optionsPanel = new OptionsPanel();
	Button closeOption = new Button("Close",new CloseClickHandler());
	AlgebraActivity algebraActivity;

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

		if (algebraActivity.inEditMode) {
			//TODO Equation HTML requires CSS to display
//			Button exportOption = new Button("Export", new ExportClickHandler(algebraActivity));
//			optionsPanel.add(exportOption);
		}else {
			ToggleSlide easyHardOption = new ToggleSlide("Easy", "Hard", Moderator.isInEasyMode, new EasyHardClickHandler());
			optionsPanel.add(easyHardOption);
		}
		
		ToggleSlide editSolveOption = new ToggleSlide("Edit", "Solve", algebraActivity.inEditMode, new EditSolveClickHandler(algebraActivity));
		optionsPanel.add(editSolveOption);
		
		optionsPopup.show();
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
		Moderator.reloadEquationPanel(null, null);
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
		Moderator.switchToAlgebra(Moderator.getCurrentEquationTree().getEquationXMLClone(), !algebraActivity.inEditMode);
	}

}

//class ExportClickHandler implements ClickHandler {
//	AlgebraActivity algebraActivity;
//	ExportClickHandler(AlgebraActivity algebraActivity){
//		this.algebraActivity = algebraActivity;
//	}
//	@Override
//	public void onClick(ClickEvent event) {
//		OptionsHandler.optionsPopup.hide();
//		
//		EquationHTML html = algebraActivity.getMathTree().reloadDisplay(true);
//		
//		Prompt prompt = new Prompt();
//		TextBox text = new TextBox();
//		text.setSize("100%", "100%");
//		text.setText(html.getHTML());
//		prompt.add(text);
//		prompt.appear();
//		
//	}
//	
//}
