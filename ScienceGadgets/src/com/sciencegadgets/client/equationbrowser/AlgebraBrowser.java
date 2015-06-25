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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.equationbrowser.AlgebraPracticeProblems.Subject;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;

public class AlgebraBrowser extends SelectionPanel {

	public AlgebraBrowser(String title, ActivityType activityOfIntention) {
		super(title, new ProblemSelectHandler(activityOfIntention));

		getElement().setId(CSS.ALG_BROWSER);

		Subject lastSubject = null;
		for (AlgebraPracticeProblems problem : AlgebraPracticeProblems.values()) {
			if(!problem.getSubject().equals(lastSubject)) {
				lastSubject = problem.getSubject();
				addSectionTitle(lastSubject.toString());
			}
			add("<div>"+problem.toString()+"</div>"+problem.getEquationHTML(), null, problem);
		}
	}


}
class ProblemSelectHandler implements SelectionHandler {
	ActivityType activityOfIntention;
	public ProblemSelectHandler(ActivityType activityOfIntention) {
		this.activityOfIntention = activityOfIntention;
	}

	@Override
	public void onSelect(Cell selected) {
		if(selected.getEntity() == null) {
			return;
		}
		AlgebraPracticeProblems problem = (AlgebraPracticeProblems) selected.getEntity();
		Element equationEl = new HTML(problem.getEquationXML()).getElement().getFirstChildElement();
		if(problem.getGoalXML() != null && !"".equals(problem.getGoalXML())) {
			URLParameters.addParameter(Parameter.goal, problem.getGoalXML(), false);
		}
		if(activityOfIntention == ActivityType.algebrasolve) {
			Moderator.isInEasyMode = false;
		}
		Moderator.switchToAlgebra(equationEl, null, activityOfIntention, true);
	}
}
