/*   Copyright 2012 Argishti Rostomian
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sciencegadgets.client.util.equationwriter;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.VerticalPanel;

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

