package com.sciencegadgets.util.equationWriter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ContextMenu extends PopupPanel{
	
	VerticalPanel content = new VerticalPanel();
	Widget clickSource;
	
	public ContextMenu(ClickEvent event){
		super(true);

		setOptions();
		add(content);
		
		clickSource = (Widget) event.getSource();
		
		setPopupPosition(event.getClientX(), event.getClientY());
		show();
	}
	
	@Override
	public void hide(boolean autoClosed){
		clickSource.getElement().getStyle().clearBackgroundColor();
		super.hide(autoClosed);
	}
	
	private void setOptions(){
		content.add(new Label("SOLVE FOR THIS"));
		content.add(new Label("EXPAND THIS"));
		content.add(new Label("PLUG IN A VALUE"));
		content.add(new Label("MMM...Dounts"));
	}
}