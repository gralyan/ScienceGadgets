package com.admin.client;

import com.admin.client.AdminService;
import com.admin.client.AdminServiceAsync;
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
