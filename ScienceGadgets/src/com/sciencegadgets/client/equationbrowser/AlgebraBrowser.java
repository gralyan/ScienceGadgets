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
		Moderator.switchToAlgebra(equationEl, null, activityOfIntention, true);
	}
}
