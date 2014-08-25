package com.sciencegadgets.client.ui;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;

public class SolvedPrompt extends Prompt {
	
	public SolvedPrompt(){
		
		add(new Label("Congradulations!!! You solved it"));
		
		CommunistPanel currentOptions = new CommunistPanel(true);
		currentOptions.add(new Button("Similar"));
		currentOptions.add(new Button("Harder"));
		add(currentOptions);
	}
	
	@Override
	public void appear() {
		super.appear();
		Moderator.SOUNDS.RESPONSE_SUCCESS.play();
	}
}
