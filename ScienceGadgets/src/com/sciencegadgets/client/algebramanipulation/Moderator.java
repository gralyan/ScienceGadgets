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
package com.sciencegadgets.client.algebramanipulation;

import java.util.LinkedList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;
import com.sciencegadgets.client.equationtree.EquationList;
import com.sciencegadgets.client.equationtree.MathMLBindingTree;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Operators;

public class Moderator implements EntryPoint {

	static EquationList eqList;
	static AbsolutePanel eqListPanel = new AbsolutePanel();
	private static ScrollPanel spTree = new ScrollPanel(eqListPanel);

//	private static DropControllAssigner dropAssigner;
	public static MathMLBindingTree jTree;
	public static LinkedList<AbstractMathDropController> dropControllers;
	static boolean inEditMode = true;
	private Button backToBrowserButton = new Button("Back", new BackButtonHandler());
	private Moderator moderator = this;

	@Override
	public void onModuleLoad() {
		
		EquationBrowser browserPanel = new EquationBrowser(moderator);
		RootPanel.get("scienceGadgetArea").add(browserPanel);

	}
	
	/**
	 * Sends the given equation to the views of it
	 * 
	 * @param mathML
	 */
	public void makeAgebraWorkspace(Element mathML) {

		RootPanel.get("scienceGadgetArea").clear();
		RootPanel.get("scienceGadgetArea").add(backToBrowserButton);
		
		AlgOut algOut = new AlgOut();
		RootPanel.get("scienceGadgetArea").add(algOut);
		
		eqListPanel.setStyleName("treeCanvas");
		RootPanel.get("scienceGadgetArea").add(spTree);
		
		try {
			jTree = new MathMLBindingTree(mathML);
			reload("");
			
			if(inEditMode){
//				AlgOutEntry.algOut.add(new HTML("aaaa")/*new ChangeNodeMenu(eqList.selectedWrapper)*/);
			}
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param mathML
	 */
	public static void reload(String changeComment){
//			AlgOutEntry.updateAlgOut(/*jTree.getMathML()*/mathML, jTree.getWrappers(),
//					changeComment);
		eqListPanel.clear();
		eqList = new EquationList(eqListPanel, jTree, inEditMode);

		//TODO uncomment
//		DropControllAssigner.assign(jTree.getWrappers(), true);
		
	}
	
	class BackButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent arg0) {
			RootPanel.get("scienceGadgetArea").clear();

			EquationBrowser browserPanel = new EquationBrowser(moderator);
			RootPanel.get("scienceGadgetArea").add(browserPanel);
		}

	}

}
