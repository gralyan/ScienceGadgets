package com.sciencegadgets.client.algebramanipulation;

import java.util.LinkedList;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.TopNodesNotFoundException;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.equationtree.DropControllAssigner;
import com.sciencegadgets.client.equationtree.JohnTree;
import com.sciencegadgets.client.equationtree.TreeCanvas;
import com.sciencegadgets.client.equationtree.TreeEntry;

public class EquationTransporter {

	public static TreeCanvas tCanvas;
	private static DropControllAssigner dropAssigner;
	public static LinkedList<AbstractMathDropController> dropControllers;
	private static JohnTree jTree;

	public static TreeCanvas mltCanvas;
	private static JohnTree mljTree;

	/**
	 * Only works in firefox
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

		try {
			// Initial AlgOut line
			AlgOutEntry.algOut.clear(true);
			AlgOutEntry.algOut.resizeRows(0);

			Log.info("----Initial tree, making----");
			jTree = new JohnTree(mathML, true);
			mathML = jTree.toMathML();
			Log.info("----Initial tree, made----");

			selectEquation(mathML, "firstComment");

		} catch (TopNodesNotFoundException e) {
			Window.alert("Either this browser does not support this function, or the equation is not in the form (side)=(side)");
			e.printStackTrace();
			Log.severe("TopNodesNotFound: the MathML must be standard and well formed");
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
		TreeEntry.apTree.clear();
		try {
			Log.info("____New Tree, making____");
			jTree = new JohnTree(mathML, true);
			tCanvas = new TreeCanvas(TreeEntry.apTree, jTree);
			Log.info("====New Tree, made====");
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
			Log.severe("NEW TREE FAIL");
		}

		// ////////////////////////////////////////////////////////
		// just to visualize the mathml, the box must be uncommented in
		// TreeEntry also
		// //////////////////////////////////////////
		// TreeEntry.mlTree.clear();
		// try {
		// mljTree = new JohnTree(new HTML(mathML.getHTML()), false);
		// mltCanvas = new TreeCanvas(TreeEntry.mlTree, mljTree);
		// } catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
		// e.printStackTrace();
		// }
		// ///////////////////////////////////////////////////////

		AlgOutEntry.updateAlgOut(jTree.getMathML(), jTree.getWrappers(),
				changeComment);

		DropControllAssigner.assign(jTree.getWrappers(), true);
	}

	public static native void parseJQMath(Element element) /*-{
															$wnd.M.parseMath(element);
															}-*/;

}
