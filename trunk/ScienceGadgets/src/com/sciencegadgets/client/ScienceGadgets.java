/*   Copyright 2012 John Gralyan
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
package com.sciencegadgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;

public class ScienceGadgets implements EntryPoint {

	private final DatabaseHelperAsync greetingService = GWT
			.create(DatabaseHelper.class);

	

	public void onModuleLoad() {
		
		try {
			// /////////////////////////////////////////
			// experimenting with RPC calls
			// ////////////////////////////////

			final Button sendButton = new Button("Send");

			RootPanel.get("scienceGadgetArea").add(sendButton);

			ClickHandler handler = new ClickHandler() {
				public void onClick(ClickEvent event) {
//					sendToServer("a+b");
				}
			};

			sendButton.addClickHandler(handler);
			
		} catch (Exception e) {
			e.printStackTrace();
			Window.alert("Please refresh Page");
		}
	}

	/**
	 * Async call to server to parse the equation using Symja
	 * 
	 * @param textToServer
	 */
//	private void sendToServer(String textToServer) {
//
//		greetingService.greetServer(textToServer, new AsyncCallback<String[]>() {
//			public void onFailure(Throwable caught) {
//				Window.alert("Math parseing FAIL :(");
//			}
//
//			public void onSuccess(String[] result) {
//				Window.alert(result[0]+"-"+result[1]+"-"+result[2]+"-");
//			}
//		});
//	}

}
