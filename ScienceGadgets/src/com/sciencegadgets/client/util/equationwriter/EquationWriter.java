/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
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

