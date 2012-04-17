package com.sciencegadgets.client.AlgebraManipulation;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.EquationTree.DropControllAssigner;
import com.sciencegadgets.client.EquationTree.JohnTree;
import com.sciencegadgets.client.EquationTree.TreeCanvas;
import com.sciencegadgets.client.EquationTree.TreeEntry;

public class EquationTransporter {
	
	public static TreeCanvas tCanvas;
	public static void transport(String equation){


		// Initial AlgOut line
		HTML algOutFirstHTML = new HTML("$" + equation + "$");
		AlgOutEntry.algOut.clear(true);
		AlgOutEntry.algOut.resizeRows(1);
		AlgOutEntry.algOut.setWidget(0, 0, algOutFirstHTML);
		parseJQMath(algOutFirstHTML.getElement());

		HTML draggableEquation = new HTML();
		draggableEquation.setHTML("$" + equation + "$");
		parseJQMath(draggableEquation.getElement());

		// Make the tree on canvas
		//JohnTree johnTree = new JohnTree(draggableEquation, false);
		//TreeEntry.apTree.clear();
		//TreeCanvas treeCanvas = new TreeCanvas(TreeEntry.apTree, johnTree);
		
		///////////////////////////////
		//
		// Second tree to visualize difference
		//
		////////////////////////////
		JohnTree jTree = new JohnTree(draggableEquation, true);
		TreeEntry.parsedTreePanel.clear();
		tCanvas = new TreeCanvas(TreeEntry.parsedTreePanel, jTree);

		//System.out.println(algOutFirstHTML.getHTML());

		// Make draggable algebra area
		AlgOutEntry.algDragPanel.add(new AlgebraManipulator(
				draggableEquation, jTree.getWrappers(),
				AlgOutEntry.algDragPanel));

		DropControllAssigner a = new DropControllAssigner(jTree);

		/*
		 * MathTree mathTree = new MathTree(draggableEquation);
		 * RootPanel.get().add(mathTree.getTreeDrawing());
		 */

		
	}
	public static native void parseJQMath(Element element) /*-{
	$wnd.M.parseMath(element);
}-*/;

}

