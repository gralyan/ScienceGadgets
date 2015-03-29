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

import java.util.ArrayList;
import java.util.HashSet;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.TypeSGET;

public class ChallengeBrowser extends SelectionPanel {


	public ChallengeBrowser() {
		super("Challenges", new ChallengeSelectHandler());

		getElement().setId(CSS.ALG_BROWSER);

		HashSet<Badge> badges = Moderator.getStudent().getBadges();

		if (badges == null || badges.size() == 0) {
			setDefaultProblem();
		} else {
			DataModerator.database.getProblemsByBadges(badges,
					new AsyncCallback<ArrayList<Problem>>() {
						public void onFailure(Throwable caught) {
							setDefaultProblem();
							JSNICalls.error("Can't find Badges");
							JSNICalls.error(caught.toString());
							JSNICalls.error(caught.getMessage().toString());
							JSNICalls.error(caught.getCause().toString());
						}

						public void onSuccess(ArrayList<Problem> problemList) {
							if (problemList.size() > 0) {
								addProblems(problemList);
							} else {
								setDefaultProblem();
							}
						}
					});
		}
	}

	private void setDefaultProblem() {

		// Display default a=1+2 if student has no badges
		String eq = TypeSGET.Equation.getTag();
		String var = TypeSGET.Variable.getTag();
		String num = TypeSGET.Number.getTag();
		String op = TypeSGET.Operation.getTag();
		String sum = TypeSGET.Sum.getTag();
		add("<div>a=1+2<div>",//
				/**/"<" + eq + ">" + //
						/*    */"<" + var + ">a</" + var + ">" + //
						/*    */"<" + op + ">=</" + op + ">" + //
						/*    */"<" + sum + ">" + //
						/*        */"<" + num + ">1</" + num + ">" + //
						/*        */"<" + op + ">+</" + op + ">" + //
						/*        */"<" + num + ">2</" + num + ">" + //
						/*    */"</" + sum + ">" + //
						/**/"</" + eq + ">");

	}

	public void addProblemsForNewBadge(Badge earnedBadge) {

		// Add problems requiring new badge to available problem list
		DataModerator.database.getProblemsByBadge(earnedBadge,
				new AsyncCallback<ArrayList<Problem>>() {
					@Override
					public void onFailure(Throwable caught) {
					}

					@Override
					public void onSuccess(ArrayList<Problem> newProblems) {
						addProblems(newProblems);
					}
				});
	}

	void addProblems(ArrayList<Problem> problemList) {
		if (problemList != null && problemList.size() > 0) {
			for (Problem problem : problemList) {
				add(problem.getTitle(), problem.getDescription(), problem);
			}
		}
	}
}

class ChallengeSelectHandler implements SelectionHandler {

	@Override
	public void onSelect(Cell selected) {
		Problem problem = (Problem) selected.getEntity();
		Moderator.switchToProblem(problem);
	}
}
