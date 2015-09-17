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
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.shared.TypeSGET;

public class MakeEquationBrowser extends FlowPanel {
	AlgebraBrowser algebraBrowser = new AlgebraBrowser("Template Equations", ActivityType.editequation);
	SelectionButton blankEqButton = new SelectionButton("Blank Equation") {
		@Override
		protected void onSelect() {
			EquationTree blankEq = new EquationTree(TypeSGET.Variable,
					TypeSGET.NOT_SET, TypeSGET.Variable, TypeSGET.NOT_SET, true);
			Moderator.switchToAlgebra(blankEq,
					Moderator.ActivityType.editequation, true);
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
