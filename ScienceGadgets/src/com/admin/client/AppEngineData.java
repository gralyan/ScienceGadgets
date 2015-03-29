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

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AppEngineData {

	private final AdminServiceAsync adminService = GWT
			.create(AdminService.class);
	
	public void saveEquation(String newEquation){
		
		newEquation = newEquation.toLowerCase()
				.replaceAll("\r|\n|\r\n|\t| {2,}", "")
				.replaceAll("> <", "><");

		adminService.saveEquation(newEquation,
				new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						Window.alert("Saved as: " + result);
					}
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("FAILED save!!!");
					}
				});
	}
	public void doNot(){
		Window.alert("did");
	}
}
