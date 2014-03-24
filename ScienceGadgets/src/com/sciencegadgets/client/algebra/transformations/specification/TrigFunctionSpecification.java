package com.sciencegadgets.client.algebra.transformations.specification;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.shared.TrigFunctions;

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

		for (TrigFunctions function : TrigFunctions.values()) {
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
