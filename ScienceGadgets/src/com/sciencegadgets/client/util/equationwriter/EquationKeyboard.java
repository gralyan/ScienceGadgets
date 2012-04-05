package com.sciencegadgets.client.util.equationwriter;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EquationKeyboard extends VerticalPanel{
	
	FlexTable flexTable = new FlexTable();
	Widget source;
	
	
	public EquationKeyboard(Widget w){
		super();
		source = w;
	}
	
	public void onLoad() {
		this.add(flexTable);
		flexTable.setWidget(0,0,new KeyboardButton("âˆ�"));
	}
	
	
	private class KeyboardButton extends Symbol{

		public KeyboardButton(String u) {
			super(u);
		}
		
		public void setOnClick(){
			invokeContextMenu = new ClickHandler(){
				public void onClick(ClickEvent event){
					event.stopPropagation();
					Widget w = (Widget) event.getSource();
					w.getElement().getStyle().setBackgroundColor("blue");
					addToEquationWriter();
				}

				private void addToEquationWriter() {
					// TODO Auto-generated method stub
					
				}
			};
		}
	}
}