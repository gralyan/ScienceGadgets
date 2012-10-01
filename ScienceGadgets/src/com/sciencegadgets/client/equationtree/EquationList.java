package com.sciencegadgets.client.equationtree;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;

public class EquationList {
	private MathMLBindingTree mathMLBindingTree;
	private AbsolutePanel panel;
	private Grid eqList = new Grid(0, 1);
	private Timer timer;

	public EquationList(AbsolutePanel panel, final MathMLBindingTree jTree) {

		this.panel = panel;
		this.mathMLBindingTree = jTree;

		panel.add(eqList);
		eqList.setWidth(panel.getOffsetWidth() + "px");
		eqList.setStyleName("algOutGrid");

		// Wait for mathjax to format first
		timer = new Timer() {
			public void run() {
				System.out.println("111CHECKINGGGGGG");
				checkIfWeCanDraw();
			}
		};
		timer.scheduleRepeating(100);
	}

	private void checkIfWeCanDraw() {
		String eqId = "svg" + mathMLBindingTree.getEquals().getId();
		Element eqEl = DOM.getElementById(eqId);
		if (eqEl != null) {
			System.out.println("111CAN DRAWWW!!!!!!");
			timer.cancel();
			draw(mathMLBindingTree);
		}
	}

	public void draw(MathMLBindingTree jTree) {

for(int i=1 ; i<4 ; i++){
		Node nextEq = mathMLBindingTree.getMathML().getElement()
				.getFirstChild().cloneNode(true);
		System.out.println("nexteqIDDD "
				+ ((Element) nextEq).getAttribute("id"));

		replaceChildsId(nextEq, i);
		HTML eq = new HTML();
		eq.getElement().appendChild(nextEq);

		int rowCount = eqList.getRowCount() + 1;
		eqList.resizeRows(rowCount);
		eqList.setWidget(rowCount - 1, 0, eq);
	}}

	private void replaceChildsId(Node parent, int eqRow) {
		NodeList<Node> children = parent.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Element curEl = ((Element) children.getItem(i));
			System.out.println("oldId " + curEl.getAttribute("id"));
			String oldId = curEl.getAttribute("id");
			
			//Each equation in the list will have a different prefix for id's
			//[equation #]-svg... example 1-svg0
			if (oldId.contains("svg")) {
				String newId = oldId.replaceFirst("svg", eqRow + "-svg");
				curEl.setAttribute("id", newId);
				System.out.println("newId " + newId);
				
				//Each equation will have a different MathJax frame id
				//MathJax-Element-[equation #]-Frame
			} else if (oldId.contains("MathJax-Element")) {
				String newId = "MathJax-Element-" + (eqRow + 1) + "-Frame";
				curEl.setAttribute("id", newId);
				System.out.println("newId " + newId);
			}
			
			if (!children.getItem(i).getNodeName().equalsIgnoreCase("script")) {
				replaceChildsId(children.getItem(i), eqRow);
			}
		}
	}
}