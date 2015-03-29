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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
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