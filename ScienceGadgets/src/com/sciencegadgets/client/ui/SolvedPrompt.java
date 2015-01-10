package com.sciencegadgets.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgebraActivity;

public class SolvedPrompt extends Prompt {
	private final SimplePanel historyContainer = new SimplePanel();

	public SolvedPrompt() {

		Label title = new Label("Congradulations!!! You solved it! :)");
		title.setHeight("10%");
		add(title);

		historyContainer.setSize("100%", "90%");
		add(historyContainer);

		addOkHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				disappear();
				Moderator.switchToProblem(null);
			}
		});
	}

	public void solved(AlgebraActivity algebraActivity) {
//		Moderator.SOUNDS.RESPONSE_SUCCESS.play();
		historyContainer.clear();
		algebraActivity.algOut.isSolved = true;
		historyContainer.add(algebraActivity.algOut);
		algebraActivity.updateEquation();
		appear();
	}
}
