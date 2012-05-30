package com.sciencegadgets.client.algebramanipulation;

import java.util.LinkedList;

import com.google.gwt.user.client.Element;
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

	public static void transport(String equation) {
		HTML draggableEquation = new HTML();
		draggableEquation.setHTML("$" + equation + "$");
		parseJQMath(draggableEquation.getElement());

		transport(draggableEquation);
	}

	/**
	 * Initializes an equation
	 * 
	 * @param mathML
	 */
	private static void transport(HTML mathML) {

		try {
			Log.info( "----Initial tree, making----");
			jTree= new JohnTree(mathML, true);
			mathML = jTree.toMathML();
			Log.info( "----Initial tree, made----");

			// Initial AlgOut line
			AlgOutEntry.algOut.clear(true);
			AlgOutEntry.algOut.resizeRows(0);

			changeEquation(mathML);
		} catch (TopNodesNotFoundException e) {
			e.printStackTrace();
			Log.severe( "INITIAL TREE FAIL");
		}
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param mathML
	 */
	public static void changeEquation(HTML mathML) {
		// Make equation tree and draw it to the canvas
		TreeEntry.apTree.clear();
		try {
			Log.info("____New Tree, making____");
			jTree = new JohnTree(mathML, true);
			tCanvas = new TreeCanvas(TreeEntry.apTree, jTree);
			Log.info( "====New Tree, made====");
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
			Log.severe( "NEW TREE FAIL");
		}

		// ////////////////////////////////////////////////////////
		// just to visualize the mathml
		// //////////////////////////////////////////
		TreeEntry.mlTree.clear();
		try {
			mljTree = new JohnTree(new HTML(mathML.getHTML()), false);
			mltCanvas = new TreeCanvas(TreeEntry.mlTree, mljTree);
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
		}
		// ///////////////////////////////////////////////////////

		AlgOutEntry.updateAlgOut(jTree.getMathML(), jTree.getWrappers());
		
		DropControllAssigner.assign(jTree.getWrappers(), true);
	}

	public static native void parseJQMath(Element element) /*-{
		$wnd.M.parseMath(element);
	}-*/;

}
