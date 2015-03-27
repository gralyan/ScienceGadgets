package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.shared.TypeSGET;

public class MakeEquationBrowser extends FlowPanel {
	AlgebraBrowser algebraBrowser = new AlgebraBrowser("Template Equations", ActivityType.algebraedit);
	SelectionButton blankEqButton = new SelectionButton("Blank Equation") {
		@Override
		protected void onSelect() {
			EquationTree blankEq = new EquationTree(TypeSGET.Variable,
					TypeSGET.NOT_SET, TypeSGET.Variable, TypeSGET.NOT_SET, true);
			Moderator.switchToAlgebra(blankEq, null,
					Moderator.ActivityType.algebraedit, true);
		}
	};

	MakeEquationBrowser() {
		setSize("100%", "100%");
		algebraBrowser.setHeight("80%");
		blankEqButton.setHeight("20%");
		blankEqButton.addStyleName(CSS.MAKE_EQ_BUTTON);

		add(algebraBrowser);
		add(blankEqButton);
	}
}
