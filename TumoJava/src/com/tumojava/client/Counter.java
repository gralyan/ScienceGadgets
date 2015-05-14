package com.tumojava.client;

import com.google.gwt.user.client.ui.Label;

class Counter extends Label {

	private int count = 0;
	
	Counter(){
		updateText();
	}

	void up(){
		count++;
		updateText();
	}
	
	private void updateText() {
		setText(count+"");
	}
	
}
