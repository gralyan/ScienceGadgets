/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.ToggleSlide;
import com.sciencegadgets.shared.TypeSGET;

public class OptionsHandler implements ClickHandler {

	public static OptionsPopup optionsPopup;
	FlowPanel optionsPanel = new FlowPanel();
	AlgebraActivity algebraActivity;
	private Button optionsButton;

	public OptionsHandler(AlgebraActivity algebraActivity) {
		this.algebraActivity = algebraActivity;
		optionsButton = algebraActivity.optionsButton;
		optionsPopup = new OptionsPopup(optionsButton);

		optionsPopup.clear();

		optionsPopup.getElement().getStyle().setZIndex(10);

		optionsPanel.getElement().getStyle().setOverflowY(Overflow.SCROLL);
		optionsPopup.add(optionsPanel);

		optionsPopup.hide();
		optionsPopup.setAutoHideEnabled(true);
	}

	@Override
	public void onClick(ClickEvent event) {

		optionsPanel.clear();

		// Show Edit/Solve and Easy/Hard options when clicked with SHIFT key
		if (event.isShiftKeyDown()) {
			if (!algebraActivity.isInEditMode()) {
				ToggleSlide easyHardOption = new ToggleSlide("Easy", "Hard",
						Moderator.isInEasyMode, new EasyHardClickHandler());
				easyHardOption.setHeight("70px");
				optionsPanel.add(easyHardOption);
			}
			ToggleSlide editSolveOption = new ToggleSlide("Edit", "Solve",
					algebraActivity.isInEditMode(), new EditSolveClickHandler(
							algebraActivity));
			editSolveOption.setHeight("70px");
			optionsPanel.add(editSolveOption);
		}

		for (EquationTree eqTree : algebraActivity.getSystem().getList()) {
			EquationButton eqButton  = new EquationButton(algebraActivity, eqTree);
			optionsPanel.add(eqButton);
		}

		if (algebraActivity.isInEditMode()) {
			InsertEquationButton insEqButton = new InsertEquationButton(
					algebraActivity);
			optionsPanel.add(insEqButton);
		}
		optionsPopup.showRelativeTo(optionsButton);
	}

}

class OptionsPopup extends PopupPanel {
	Button optionsButton;

	OptionsPopup(Button optionsButton) {
		this.optionsButton = optionsButton;
		this.getElement().getStyle().setBackgroundColor("white");
	}

	@Override
	public void show() {
		AbsolutePanel mainPanel = Moderator.scienceGadgetArea;
		int optionsButtonHeight = optionsButton.getOffsetHeight();

		this.setPixelSize(mainPanel.getOffsetWidth() / 2,
				mainPanel.getOffsetHeight() - optionsButtonHeight);

		super.show();
	}
}

//
// Option Handlers
//

class EquationButton extends Button{
	public EquationButton(final AlgebraActivity algebraActivity,
			final EquationTree eqTree) {
		super(JSNICalls.elementToString(eqTree.reloadDisplay(false, true).getElement()));

		addStyleName(CSS.EQUATION_SELECTION);
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				OptionsHandler.optionsPopup.hide();
				algebraActivity.getSystem().moveToWorkingTree(eqTree);
				Moderator.reloadEquationPanel();
			}
		});
	}
}

class InsertEquationButton extends Button {

	public InsertEquationButton(final AlgebraActivity algebraActivity) {
		super("New Equation");
		addStyleName(CSS.INSERT_EQUATION_OPTION);
		addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				OptionsHandler.optionsPopup.hide();
				algebraActivity.getSystem().newWorkingTree();
				Moderator.reloadEquationPanel();
			}
		});
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

	EditSolveClickHandler(AlgebraActivity algebraActivity) {
		this.algebraActivity = algebraActivity;
	}

	@Override
	public void onClick(ClickEvent event) {
		OptionsHandler.optionsPopup.hide();

		ActivityType activityType = algebraActivity.getActivityType() == ActivityType.editequation ? ActivityType.interactiveequation
				: ActivityType.editequation;
		EquationTree equationTree = algebraActivity.getEquationTree();
		String equationStr = equationTree.getRoot().toString();
		if (equationStr.contains(TypeSGET.NOT_SET)) {
			Window.alert("All new entities (" + TypeSGET.NOT_SET
					+ ") must be set or removed before solving");
			return;
		}
		try {
			equationTree.validateTree(true);
		} catch (IllegalStateException e) {
			Window.alert("Units must match");
			return;
		}

		if (algebraActivity.getEquationPanel() != null) {
			algebraActivity.getEquationPanel().unselectCurrentSelection();
		}
		Moderator.switchToAlgebra(equationTree.getEquationXMLClone(), true,
				activityType, true);
	}

}
