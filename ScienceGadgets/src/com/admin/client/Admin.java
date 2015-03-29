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
package com.admin.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.TextBox;

public class Admin implements EntryPoint {

	TextBox inputBox = new TextBox();
//	AppEngineData data = new AppEngineData();


	@Override
	public void onModuleLoad() {
		
		TestBot_Transformations testBot = new TestBot_Transformations();
		
		testBot.deploy();
		
//		Button saveButton = new Button("Save");
//		RootPanel.get().add(inputBox);
//		RootPanel.get().add(saveButton);
//
//		saveButton.addClickHandler(new ClickHandler() {
//
//			@Override
//			public void onClick(ClickEvent event) {
//				data.saveEquation(inputBox.getText());
//			}
//		});

	}

}
