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