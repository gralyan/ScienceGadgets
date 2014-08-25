package com.sciencegadgets.client.ui;

import com.sciencegadgets.client.Moderator;

public abstract class Quiz extends Prompt {

	public void onCorrect() {
		Moderator.SOUNDS.RESPONSE_GOOD.play();
	}
	public void onIncorrect() {
		Moderator.SOUNDS.RESPONSE_BAD.play();
	}
}
