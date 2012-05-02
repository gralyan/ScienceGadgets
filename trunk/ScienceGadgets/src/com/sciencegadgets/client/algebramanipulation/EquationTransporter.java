package com.sciencegadgets.client.algebramanipulation;

import java.util.LinkedList;

import org.vaadin.gwtgraphics.client.DrawingArea;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
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
		// Initial AlgOut line
		HTML algOutFirstHTML = new HTML(mathML.getHTML());
		AlgOutEntry.algOut.clear(true);
		AlgOutEntry.algOut.resizeRows(1);
		AlgOutEntry.algOut.setWidget(0, 0, algOutFirstHTML);

		changeEquation(mathML);
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
			jTree = new JohnTree(mathML, true);
			tCanvas = new TreeCanvas(TreeEntry.apTree, jTree);
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
		}
		//////////////////////////////////////////////////////////
		//just to visualize the mathml
		////////////////////////////////////////////
		TreeEntry.mlTree.clear();
		try {
			mljTree = new JohnTree(mathML, false);
			mltCanvas = new TreeCanvas(TreeEntry.mlTree, mljTree);
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
		}
/////////////////////////////////////////////////////////
		// Make draggable algebra area
		AlgOutEntry.algDragPanel.clear();
		AlgOutEntry.algDragPanel.add(new AlgebraManipulator(jTree.getMathML(), jTree
				.getWrappers(), AlgOutEntry.algDragPanel));

		DropControllAssigner.assign(jTree.getWrappers(), true);
	}

	public static native void parseJQMath(Element element) /*-{
		$wnd.M.parseMath(element);
	}-*/;

}

