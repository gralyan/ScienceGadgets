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
package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.client.ui.FitParentHTML;
import com.sciencegadgets.client.ui.SelectionButton;

public class EquationBrowser extends FlowPanel {

	CommunistPanel activitySelectionPanel = new CommunistPanel(false);
	SimplePanel activityDetailsPanel = new SimplePanel();
	
	AlgebraBrowser algebraBrowser = new AlgebraBrowser("Algebra Practice", ActivityType.interactiveequation);
	GenerateSpec generatePanel = new GenerateSpec();
	ConversionSpecification conversionSpec = new ConversionSpecification();
//	public ChallengeBrowser challengeBrowser = new ChallengeBrowser();
	public MakeEquationBrowser makeEquationBrowser = new MakeEquationBrowser();
//	ScienceBrowser scienceBrowser = new ScienceBrowser(this);

	static ActivityButton selectedActivityButton = null;

	public EquationBrowser() {

		// Set up browsers
		this.getElement().setId(CSS.EQUATION_BROWSER);
		
//		Anchor title = new Anchor("ScienceGadgets", "/project/index.html");
		Label title = new Label("ScienceGadgets");
		title.addStyleName(CSS.MAIN_TITLE);
		this.add(title);

		activitySelectionPanel.getElement().setId(CSS.ACTIVITY_SELECTION_PANEL);
		this.add(activitySelectionPanel);

		activityDetailsPanel.getElement().setId(CSS.ACTIVITY_DETAILS_PANEL);
		this.add(activityDetailsPanel);

		// Add buttons
		generatePanel.getElement().setId(CSS.EQ_GENERATOR_PANEL);
		
		ActivityButton defaultButton = new ActivityButton("Algebra Generator",
				generatePanel);
		activitySelectionPanel.add(defaultButton);
		
		algebraBrowser.getElement().setId(CSS.ALG_BROWSER_PANEL);
		activitySelectionPanel.add(new ActivityButton("Algebra Practice",
				algebraBrowser));

		activitySelectionPanel.add(new ActivityButton("Conversion",
				conversionSpec));
//		
//		challengeBrowser.getElement().setId(CSS.CHALLENGE_GENERATOR_PANEL);
//		activitySelectionPanel.add(new ActivityButton("Challenges",
//				challengeBrowser));
		
		makeEquationBrowser.getElement().setId(CSS.CHALLENGE_GENERATOR_PANEL);
		activitySelectionPanel.add(new ActivityButton("Make Equation",
				makeEquationBrowser));
		
		// Add initial detail
		defaultButton.onSelect();
		;
	}

	class ActivityButton extends SelectionButton {
		Widget activityDetails;

		ActivityButton(String title, Widget activityDetails) {
			this.add(new FitParentHTML(title));
			this.activityDetails = activityDetails;
			addStyleName(CSS.ACTIVITY_SELECTION_BUTTON);
		}

		@Override
		protected void onSelect() {
			Widget currentWidget = activityDetailsPanel.getWidget();
			if (activityDetails != currentWidget) {
				if (currentWidget != null) {
					currentWidget.removeFromParent();
				}
				activityDetailsPanel.add(activityDetails);
			}
			if (this != selectedActivityButton) {
				if(selectedActivityButton != null) {
					selectedActivityButton.removeStyleName(CSS.ACTIVITY_SELECTION_BUTTON_SELECTED);
					selectedActivityButton.addStyleName(CSS.TRANSFORMATION_BUTTON);
					selectedActivityButton.addStyleName(CSS.ACTIVITY_SELECTION_BUTTON);
				}
				this.removeStyleName(CSS.ACTIVITY_SELECTION_BUTTON);
				this.removeStyleName(CSS.TRANSFORMATION_BUTTON);
				this.addStyleName(CSS.ACTIVITY_SELECTION_BUTTON_SELECTED);
				selectedActivityButton = this;
			}
		}

	}

}
