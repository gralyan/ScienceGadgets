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

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.equationtree.DropControllAssigner;
import com.sciencegadgets.client.equationtree.EquationList;
import com.sciencegadgets.client.equationtree.MathMLBindingTree;
import com.sciencegadgets.client.equationtree.TreeCanvas;
import com.sciencegadgets.client.equationtree.TreeEntry;

public class EquationTransporter {

	public static TreeCanvas tCanvas;
	private static DropControllAssigner dropAssigner;
	public static LinkedList<AbstractMathDropController> dropControllers;
	private static MathMLBindingTree jTree;
	private static AbsolutePanel treePanel;

	/**
	 * Sends the given equation to the views of it
	 * 
	 * @param mathML
	 */
	public static void transport(Element mathML) {

		RootPanel.get("scienceGadgetArea").clear();
		
		new AlgOutEntry().onModuleLoad();
		
		TreeEntry treeEntry = new TreeEntry();
		treePanel = TreeEntry.apTree;
		treeEntry.onModuleLoad();

		try {
			// Initial AlgOut line
			AlgOutEntry.algOut.clear(true);
			AlgOutEntry.algOut.resizeRows(0);

			selectEquation(mathML, "firstComment");

		} catch (JavaScriptException e) {
			Log.severe("Input must be in form (side)=(side)");
		}
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param mathML
	 */
	public static void selectEquation(Element mathML, String changeComment) {
		

//		Log.info("EqTrans: " + mathML.getHTML());

		// Make equation tree and draw it to the canvas
		treePanel.clear();
		try {
			jTree = new MathMLBindingTree(mathML);
//			AlgOutEntry.updateAlgOut(/*jTree.getMathML()*/mathML, jTree.getWrappers(),
//					changeComment);
			new EquationList(treePanel, jTree, true);
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
		}

		//TODO uncomment
//		DropControllAssigner.assign(jTree.getWrappers(), true);
	}
}
