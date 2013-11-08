package com.sciencegadgets.client.algebra;

import com.google.gwt.user.client.ui.Label;

public enum ResponseNote {
	Cancel("Cancel"), Switch("Switch"), Incorrect("Incorrect");
	
	private String responseText;
	public static Label response = new Label();

	ResponseNote(String responseText){
		this.responseText = responseText;
	}
	
	public String getResponse(){
		return responseText;
	}

}
