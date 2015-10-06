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

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
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

	public OptionsHandler(AlgebraActivity algebraActivity) {
		this.algebraActivity = algebraActivity;

		optionsPanel.getElement().getStyle().setOverflowY(Overflow.AUTO);

		optionsPopup = new OptionsPopup();
		optionsPopup.addStyleName(CSS.OPTIONS_POPUP);
		optionsPopup.clear();

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

		SystemOfEquations system = algebraActivity.getSystem();
		for (EquationTree eTree : system.getNonWorkingTrees().keySet()) {
			EquationButton eqButton = new EquationButton(algebraActivity, eTree);
			if (system.getInfo(eTree).isArchived()) {
				eqButton.addStyleName(CSS.ARCHIVED);
			}
			optionsPanel.add(eqButton);
		}

		if (algebraActivity.isInEditMode()) {
			InsertEquationButton insEqButton = new InsertEquationButton(
					algebraActivity);
			optionsPanel.add(insEqButton);
		}
		// optionsPopup.showRelativeTo(optionsButton);
		optionsPopup.show();
	}

}

class OptionsPopup extends PopupPanel {
	SlideAnimation slideAnimation;

	OptionsPopup() {
		this.getElement().getStyle().setBackgroundColor("gray");
		this.getElement().getStyle().setOverflowY(Overflow.AUTO);
		slideAnimation = new SlideAnimation(this);
	}

	@Override
	public void show() {
		slideAnimation.init();
		super.show();
		slideAnimation.run(500);
	}
}

class SlideAnimation extends Animation {
	OptionsPopup pop;
	int startWidth;
	int endWidth;

	public SlideAnimation(OptionsPopup pop) {
		this.pop = pop;
	}

	void init() {
		AbsolutePanel mainPanel = Moderator.scienceGadgetArea;
		startWidth = 0; //optionsButton.getOffsetWidth();
		endWidth = (int) (mainPanel.getOffsetWidth() * 0.7);
		pop.setPixelSize(startWidth, mainPanel.getOffsetHeight());
	}

	@Override
	protected void onUpdate(double progress) {
		 pop.setWidth(startWidth + (progress*(endWidth-startWidth))+"px");
	}
}

//
// Option Handlers
//

class EquationButton extends Button {
	public EquationButton(final AlgebraActivity algebraActivity,
			final EquationTree eqTree) {
		super(JSNICalls.elementToString(eqTree.reloadDisplay(true, true)
				.getElement()));

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
		Moderator.switchToAlgebra(equationTree.getEquationXMLClone(),// true,
				activityType, true);
	}

}
