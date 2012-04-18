package com.sciencegadgets.client.AlgebraManipulation;

import java.util.LinkedList;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.EquationTree.DropControllAssigner;
import com.sciencegadgets.client.EquationTree.JohnTree;
import com.sciencegadgets.client.EquationTree.TreeCanvas;
import com.sciencegadgets.client.EquationTree.TreeEntry;

public class EquationTransporter {

	public static TreeCanvas tCanvas;
	private static DropControllAssigner dropAssigner;
	public static LinkedList<MathMLDropController> dropControllers;

	public static void transport(String equation) {

		HTML draggableEquation = new HTML();
		draggableEquation.setHTML("$" + equation + "$");
		parseJQMath(draggableEquation.getElement());

		transport(draggableEquation);
	}

	private static void transport(HTML mathML){
		// Initial AlgOut line
		HTML algOutFirstHTML = new HTML(mathML.getHTML());
		AlgOutEntry.algOut.clear(true);
		AlgOutEntry.algOut.resizeRows(1);
		AlgOutEntry.algOut.setWidget(0, 0, algOutFirstHTML);
		//parseJQMath(algOutFirstHTML.getElement());

		// Make equation tree
		JohnTree jTree = new JohnTree(mathML, true);
		TreeEntry.apTree.clear();
		tCanvas = new TreeCanvas(TreeEntry.apTree, jTree);
		
		// Make draggable algebra area
		AlgOutEntry.algDragPanel.add(new AlgebraManipulator(mathML,
				jTree.getWrappers(), AlgOutEntry.algDragPanel));

		dropAssigner = new DropControllAssigner(jTree.getWrappers(), true);

		/*
		 * MathTree mathTree = new MathTree(draggableEquation);
		 * RootPanel.get().add(mathTree.getTreeDrawing());
		 */
	}
	
	public static native void parseJQMath(Element element) /*-{
		$wnd.M.parseMath(element);
	}-*/;

}
