package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.TrigFunctions;

public class TrigFunctionSpecification extends Prompt {
	public TrigFunctionSpecification() {
		super(false);
		add(new Label("What function?"));

		ClickHandler funcClick = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSpecify(((Button) event.getSource()).getText());
			}
		};

		for (TrigFunctions function : TypeML.TrigFunctions.values()) {
			Button funcButton = new Button(function.toString(), funcClick);
			funcButton.addStyleName(CSS.MEDIUM_BUTTON);
			add(funcButton);
		}
	}

	public void reload() {
		appear();
	}

	protected void onSpecify(String function) {
		disappear();
	}
}
