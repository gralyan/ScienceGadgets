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
package com.sciencegadgets.client.ui;

import java.util.Map.Entry;

import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.AlgebraHistory;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.entities.users.Badge;

public class SolvedPrompt extends Prompt {
	private final SimplePanel historyContainer = new SimplePanel();
	private final FlowPanel skillContainer = new FlowPanel();

	public SolvedPrompt() {
		super();

		setModal(true);
		setAutoHideEnabled(false);
		setAutoHideOnHistoryEventsEnabled(true);

		Label title = new Label("Congradulations!!! You solved it! :)");
		title.setHeight("10%");
		add(title);

		historyContainer.setSize("100%", "70%");
		add(historyContainer);
		skillContainer.setSize("100%", "20%");
		skillContainer.getElement().getStyle().setBackgroundColor("lime");
		add(skillContainer);

		addOkHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				disappear();
				Moderator.switchBackToProblem();
			}
		});
	}

	public void solved(AlgebraActivity algebraActivity) {
		solved(algebraActivity, "");
	}
	public void solved(AlgebraActivity algebraActivity, String evaluation) {
		// Moderator.SOUNDS.RESPONSE_SUCCESS.play();
		historyContainer.clear();
		AlgebraHistory algOut = algebraActivity.getAlgebraHistory();
		algOut.solvedUpdate(algebraActivity.getEquationTree(), evaluation);
		algOut.isSolved = true;
		historyContainer.getElement().appendChild(
				algOut.getElement().cloneNode(true));

		skillContainer.clear();

		//Show new badged
		if (!Moderator.newBadgeCollection.isEmpty()) {
			for (Badge newBadge : Moderator.newBadgeCollection) {
				Label newBadgeResponse = new Label();
				newBadgeResponse.addStyleName(CSS.DROP_ENTER_RESPONSE);
				newBadgeResponse.setText("New Badge! - " + newBadge.toString());
				skillContainer.add(newBadgeResponse);
			}
		}
		
		//Show skills increased
		if (!Moderator.skillsIncreasedCollection.isEmpty()) {
			Label skillLabel = new Label("Skills Increased");
			skillLabel.getElement().getStyle().setFontWeight(FontWeight.BOLDER);
			skillContainer.add(skillLabel);
			for (Entry<Skill, Integer> skillEntry : Moderator.skillsIncreasedCollection
					.entrySet()) {
				skillContainer.add(new Label(skillEntry.getKey() + ": +"
						+ skillEntry.getValue()));
			}
			Moderator.skillsIncreasedCollection.clear();
		}

		appear();
	}
}
