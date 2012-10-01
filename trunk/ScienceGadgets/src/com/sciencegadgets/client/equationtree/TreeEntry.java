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
package com.sciencegadgets.client.equationtree;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

public class TreeEntry implements EntryPoint {

	public static final AbsolutePanel mlTree = new AbsolutePanel();
	public static AbsolutePanel apTree = new AbsolutePanel();
	public static ScrollPanel spTree = new ScrollPanel(apTree);
	
	public void onModuleLoad() {
		
		//TODO don't catch general exception
//		try {

		// Placements are relative to the AbsolutePanel
//		apTree.getElement().getStyle().setPosition(Position.RELATIVE);
			spTree.setStyleName("treeCanvas");
			RootPanel.get("scienceGadgetArea").add(spTree);
			
			////////////////////////////
			//Just to visualize ml
			//////////////////////////
//		mlTree.setStyleName("apTree");
//		RootPanel.get("scienceGadgetArea").add(mlTree);
			
//		} catch (Exception e) {
//			e.printStackTrace();
//			Window.alert("Please refresh this page");
//		}
	}
}
