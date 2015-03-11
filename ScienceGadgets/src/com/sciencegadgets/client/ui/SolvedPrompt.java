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
		// Moderator.SOUNDS.RESPONSE_SUCCESS.play();
		historyContainer.clear();
		algebraActivity.algOut.solvedUpdate(algebraActivity.getEquationTree());
		algebraActivity.algOut.isSolved = true;
		historyContainer.getElement().appendChild(
				algebraActivity.algOut.getElement().cloneNode(true));

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

		if (algebraActivity.getEquation() != null) {
			algebraActivity.updateEquation();
		}
		appear();
	}
}
