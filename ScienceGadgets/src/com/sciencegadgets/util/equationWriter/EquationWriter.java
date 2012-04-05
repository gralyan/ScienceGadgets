package com.sciencegadgets.util.equationWriter;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EquationWriter extends VerticalPanel {
	
	EquationWriter panel;
	
	public EquationWriter(){
		super();
		panel = this;
		this.getElement().getStyle().setPosition(Position.ABSOLUTE);
	}
	
	int count = 0;
	Button goButton;
	String url = "integral.png";
	String x = "x.png";
	
	Symbol integral1;
	Symbol integral2;
	Symbol integral3;
	Symbol integral4;
	
	public void onLoad() {
		integral1 = new Symbol("âˆ�");
		integral2 = new Symbol("x");
		integral3 = new Symbol("âˆ�");
		integral4 = new Symbol("x");
		
		goButton = new Button("Go");
		goButton.addClickHandler(goClick);
		
		panel.add(goButton);
		panel.getElement().getStyle().setPosition(Position.ABSOLUTE);
	}
	
	public void addTo(){};
	
	ClickHandler goClick = new ClickHandler(){
		public void onClick(ClickEvent event){
			panel.add(integral1);
			integral1.setSuperScript(integral2);
			integral1.getSuperScript().setSuperScript(integral3);
			integral1.setSubScript(integral4);
			//integral2.setSuperScript(integral3);
		}
	};
}


// THE GRAVEYARD

/*
		LoadHandler x = new LoadHandler() {
		      public void onLoad(LoadEvent event) {
		    	  isLoaded=true;
		    	  System.out.println(image.getHeight() +" "+image.getWidth());
		      }};

*/

