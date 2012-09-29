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
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.equationtree.DropControllAssigner;
import com.sciencegadgets.client.equationtree.MathMLBindingTree;
import com.sciencegadgets.client.equationtree.TreeCanvas;
import com.sciencegadgets.client.equationtree.TreeEntry;

public class EquationTransporter {

	public static TreeCanvas tCanvas;
	private static DropControllAssigner dropAssigner;
	public static LinkedList<AbstractMathDropController> dropControllers;
	private static MathMLBindingTree jTree;

	public static TreeCanvas mltCanvas;
	private static MathMLBindingTree mljTree;
	private static AbsolutePanel treePanel;

	/**
	 * Only works in firefox, parses jqMath to MathML
	 * 
	 * @param equation
	 *            - the equation as a jqmath String
	 * @return - the Math ML as a String
	 */
	public static String transport(String jqMath) {

		// jqMath to MathML to transport
		HTML html = new HTML();
		html.setHTML("$" + jqMath + "$");
		parseJQMath(html.getElement());

		transport(html);
		return html.getHTML();
	}

	/**
	 * Sends the given equation to the views of it
	 * 
	 * @param mathML
	 */
	public static void transport(HTML mathML) {

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
	public static void selectEquation(HTML mathML, String changeComment) {
		

		Log.info("EqTrans: " + mathML.getHTML());

		// Make equation tree and draw it to the canvas
		treePanel.clear();
		try {
			jTree = new MathMLBindingTree(mathML);
			//TODO uncomment
			AlgOutEntry.updateAlgOut(jTree.getMathML(), jTree.getWrappers(),
					changeComment);
			tCanvas = new TreeCanvas(treePanel, jTree);
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
		}

		DropControllAssigner.assign(jTree.getWrappers(), true);
	}

	public static native void parseJQMath(Element element) /*-{
															$wnd.M.parseMath(element);
															}-*/;


}
