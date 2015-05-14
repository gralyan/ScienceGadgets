package com.mathgames.sciencegadgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.RootPanel;
import com.mathgames.sciencegadgets.client.games.Addition1;
import com.mathgames.sciencegadgets.client.games.ArithmeticGame;

public class MathGames implements EntryPoint {

	private static final RootPanel mainPanel = RootPanel.get();

	@Override
	public void onModuleLoad() {

		StartScreen startScreen = new StartScreen();
		mainPanel.add(startScreen);
	}

	public static void switchToGame(Game game) {
		GWT.log(1.2+"");
		mainPanel.clear();
		GWT.log(2+"");
		ArithmeticGame aGame;
		switch (game) {
		case Addition_1:
			GWT.log(3+"");
			aGame = new Addition1();
			GWT.log(4+"");
			break;

		default:
			aGame = new Addition1();
			break;
		}
		mainPanel.add(aGame);

	}
}
