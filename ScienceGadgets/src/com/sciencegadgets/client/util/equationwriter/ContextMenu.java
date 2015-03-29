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