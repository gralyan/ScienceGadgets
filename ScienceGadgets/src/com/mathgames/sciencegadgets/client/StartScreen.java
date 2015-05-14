package com.mathgames.sciencegadgets.client;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class StartScreen extends FlowPanel {

	StartScreen() {

		addStyleName(CSS.FILL_PARENT);

		Label title = new Label("MathGames");
		title.addStyleName(CSS.TITLE);
		add(title);

		FlowPanel iconArea = new FlowPanel();
		iconArea.setSize("100vh", "80vh");
		add(iconArea);

		for (Game game : Game.values()) {
			GameIcon gameIcon = new GameIcon(game);
			iconArea.add(gameIcon);

//			addStyleName(CSS.GAME_ICON);
		}
	}

	class GameIcon extends Label {
		public final Game game;

		GameIcon(final Game game) {
			this.game = game;
			addStyleName(CSS.GAME_ICON_LEVEL);
			setText(game.toString());

			addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					MathGames.switchToGame(game);
				}
			});
		}
	}
}
