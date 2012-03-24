package com.sciencegadgets.client;

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
		integral1 = new Symbol(url);
		integral2 = new Symbol(x);
		integral3 = new Symbol(url);
		integral4 = new Symbol(x);
		
		goButton = new Button("Go");
		goButton.addClickHandler(goClick);
		
		panel.add(goButton);
		panel.getElement().getStyle().setPosition(Position.ABSOLUTE);
	}
	
	ClickHandler goClick = new ClickHandler(){
		public void onClick(ClickEvent event){
			panel.add(integral1);
			integral1.setSuperScript(integral2);
			integral1.getSuperScript().setSuperScript(integral3);
			integral1.setSubScript(integral4);
			//integral2.setSuperScript(integral3);
		}
	};
	
	
	public class Symbol extends HorizontalPanel{
		Image image;
		boolean isLoaded = false;
		VerticalPanel left = new VerticalPanel();
		VerticalPanel right = new VerticalPanel();
		HorizontalPanel top = new HorizontalPanel();
		HorizontalPanel bottom = new HorizontalPanel();
		
		public Symbol(String url){
			image = new Image(url);
			right.add(top);
			right.add(bottom);
			left.add(image);
			this.add(left);
			this.add(right);
			
			this.getElement().getStyle().setPosition(Position.ABSOLUTE);
			
			addDomHandler(invokeContextMenu, ClickEvent.getType());
		}
		
		public void onLoad(){
			this.getElement().getStyle().setPosition(Position.ABSOLUTE);
		}
		
		private void setSize(int w, int h){
			image.setPixelSize(w, h);
		}
		
		private int getHeight(){
			return image.getHeight();
		}
		
		private int getWidth(){
			return image.getWidth();
		}
		
		public Symbol getSuperScript(){
			return (Symbol)top;
		}

		public void setSuperScript(Symbol s){
			int h = this.getHeight();
			int w = this.getWidth();
			s.setSize(w/4, h/4);
			bottom.removeFromParent();
			top.removeFromParent();
			top = (HorizontalPanel)s;
			right.add(top);
			right.add(bottom);
			
		}
		
		public void setSubScript(Symbol s){
			int h = this.getHeight();
			int w = this.getWidth();
			s.setSize(w/4, h/4);
			bottom.removeFromParent();
			top.removeFromParent();
			bottom = (HorizontalPanel)s;
			right.add(top);
			right.add(bottom);
			bottom.getElement().getStyle().setBottom(0, Unit.PX);
			bottom.getElement().getStyle().setPosition(Position.ABSOLUTE);
		}
		
		ClickHandler invokeContextMenu = new ClickHandler(){
			public void onClick(ClickEvent event){
				event.stopPropagation();
				Widget w = (Widget) event.getSource();
				w.getElement().getStyle().setBackgroundColor("red");
				new contextMenu(event);
			}
		};
	}

	public class contextMenu extends PopupPanel{
		
		VerticalPanel content = new VerticalPanel();
		Widget clickSource;
		
		public contextMenu(ClickEvent event){
			super(true);
			content.add(new Label("SOLVE FOR THIS"));
			content.add(new Label("EXPAND THIS"));
			content.add(new Label("PLUG IN A VALUE"));
			content.add(new Label("MMM...Dounts"));
			
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
	}
}


// THE GRAVEYARD

/*
		LoadHandler x = new LoadHandler() {
		      public void onLoad(LoadEvent event) {
		    	  isLoaded=true;
		    	  System.out.println(image.getHeight() +" "+image.getWidth());
		      }};

*/

