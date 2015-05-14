package com.tumojava.client.projectfinal;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.tumojava.client.CSS;
import com.tumojava.client.ui.InteractivePanel;

public abstract class Game extends Composite {

	private static GameUiBinder uiBinder = GWT.create(GameUiBinder.class);

	interface GameUiBinder extends UiBinder<Widget, Game> {
	}

	@UiField
	Button backButton;
	@UiField
	Label title;
	@UiField
	FlowPanel refreshButtonArea;
	@UiField
	Label descriptionArea;
	@UiField
	HTML equationArea;
	@UiField
	protected InteractivePanel interactiveArea;

	final Button refreshButton = new Button("\u21BB", new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			refreshButton.removeFromParent();
			start(interactiveArea);
		}
	});

	public Game() {
		initWidget(uiBinder.createAndBindUi(this));

		title.setText(getGameTitle());
		descriptionArea.setText(getDescription());
		equationArea.setHTML(getEquation());

		refreshButton.addStyleName(CSS.FILL_PARENT);

		interactiveArea.getElement().getStyle().setBackgroundImage(
				"url('" + getBackgroindImageName() + "')");

	}

	@UiHandler("backButton")
	void onClick(ClickEvent e) {
		Window.alert("backButton!");
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		start(interactiveArea);
	}

	protected abstract void start(InteractivePanel panel);

	public abstract String getGameTitle();

	public abstract String getDescription();

	public abstract String getEquation();

	public abstract String getBackgroindImageName();
	

	protected void onAnswer(boolean isCorrect) {
		if (isCorrect) {
			refreshButtonArea.add(refreshButton);
		}
	}

}
