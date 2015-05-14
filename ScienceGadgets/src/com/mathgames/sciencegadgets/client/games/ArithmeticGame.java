package com.mathgames.sciencegadgets.client.games;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.mathgames.sciencegadgets.client.Game;
import com.mathgames.sciencegadgets.client.ui.SymbolicEquation;

public abstract class ArithmeticGame extends FlowPanel {

	private static ArithmeticGameUiBinder uiBinder = GWT
			.create(ArithmeticGameUiBinder.class);

	interface ArithmeticGameUiBinder extends UiBinder<FlowPanel, ArithmeticGame> {
	}


	@UiField
	FlowPanel backButtonArea;
	@UiField
	FlowPanel titleArea;
	@UiField
	FlowPanel nextArea;
	@UiField
	FlowPanel symbolicMathArea;
	@UiField
	FlowPanel visualMathArea;

	int max = 10;
	int min = 2;

	protected Game game;
	protected HTML symbolicEquation;

	public ArithmeticGame(Game game) {
		add(uiBinder.createAndBindUi(this));
		
		setSize("100vw", "100vh");

		this.game = game;
		titleArea.clear();;
		titleArea.add(new Label(game.toString()));

		symbolicMathArea.add(makeEquationWithIDS());
		
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				refresh();
			}
		});
	}
	
	protected void onSolve() {

		Button nextButton = new Button("Next", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				nextArea.clear();
				refresh();
			}
		});
		nextArea.add(nextButton);
	}

	private void refresh() {
		refreshEquation();
		refreshVisuals(visualMathArea);
	}

	int getRandom() {
		return (int) (Math.random() * max + min);
	}
	
	abstract HTML makeEquationWithIDS();

	abstract void refreshEquation();

	abstract void refreshVisuals(FlowPanel visualArea);
}
