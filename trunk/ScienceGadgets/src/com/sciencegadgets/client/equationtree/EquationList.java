package com.sciencegadgets.client.equationtree;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class EquationList {
	private MathMLBindingTree mathMLBindingTree;
	private AbsolutePanel panel;
	private Grid eqList = new Grid(0, 1);
	private Timer timer;
	private LinkedList<LinkedList<MathMLBindingNode>> nodeLayers = new LinkedList<LinkedList<MathMLBindingNode>>();

	public EquationList(AbsolutePanel panel, final MathMLBindingTree jTree) {

		this.panel = panel;
		this.mathMLBindingTree = jTree;

		panel.add(eqList);
		eqList.setWidth(panel.getOffsetWidth() / 3 + "px");
		eqList.setStyleName("algOutGrid");
		
		fillNextNodeLayer(mathMLBindingTree.getRoot(), 0);

		// Wait for mathjax to format first
		timer = new Timer() {
			public void run() {
				checkIfWeCanDraw();
			}
		};
		timer.scheduleRepeating(100);
	}

	private void checkIfWeCanDraw() {
		String eqId = "svg" + mathMLBindingTree.getEquals().getId();
		Element eqEl = DOM.getElementById(eqId);
		if (eqEl != null) {
			timer.cancel();
			draw(mathMLBindingTree);
		}
	}

	public void draw(MathMLBindingTree jTree) {

		for (int i = 1; i < 4; i++) {
			Node nextEq = mathMLBindingTree.getMathML().getElement()
					.getFirstChild().cloneNode(true);

			replaceChildsId(nextEq, i);
			HTML eq = new HTML();
			eq.getElement().appendChild(nextEq);

			int rowCount = eqList.getRowCount() + 1;
			eqList.resizeRows(rowCount);
			eqList.setWidget(rowCount - 1, 0, eq);
		}
		
		placeNextEqWrappers(0);
	}
	
	
	///////////////////////////////////////////////////////////////
	// Details
	//////////////////////////////////////////////////////////////
	
	private void fillNextNodeLayer(MathMLBindingNode parent, int layer){
		LinkedList<MathMLBindingNode> children = parent.getChildren();

		if(nodeLayers.size()<layer+1){
			nodeLayers.add(new LinkedList<MathMLBindingNode>());
		}
		
		nodeLayers.get(layer).addAll(children);
		
		for(int i=0 ; i<children.size() ; i++){
			MathMLBindingNode curChild = children.get(i);
			
			if(curChild.getChildCount()>1){
				fillNextNodeLayer(curChild, layer + 1);
			}
		}
	}
	
	private void placeNextEqWrappers(int layer){
		LinkedList<MathMLBindingNode> nodes = nodeLayers.get(layer);
		
		for(MathMLBindingNode node : nodes){
			String bareId = node.getId();
			System.out.println(bareId);
			
			HTML h = new HTML("wrapper");
			h.setStyleName("var");
			
			Element el = DOM.getElementById(layer+1+"-svg"+bareId);
//			panel.add(h, 0, 0);
			panel.add(h, el.getAbsoluteLeft()-panel.getAbsoluteLeft(), el.getAbsoluteTop()-panel.getAbsoluteTop());
		}
		if(nodeLayers.size()>layer+1){
		placeNextEqWrappers(layer+1);
	}}

	private void replaceChildsId(Node parent, int eqRow) {
		NodeList<Node> children = parent.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Element curEl = ((Element) children.getItem(i));
			String oldId = curEl.getAttribute("id");

			// Each equation in the list will have a different prefix for id's
			// [equation #]-svg... example 1-svg0
			if (oldId.contains("svg")) {
				if (oldId.equals("svg0")) {
					resizeEquations(curEl);
				}
				String newId = oldId.replaceFirst("svg", eqRow + "-svg");
				curEl.setAttribute("id", newId);

				// Each equation will have a different MathJax frame id
				// MathJax-Element-[equation #]-Frame
			} else if (oldId.contains("MathJax-Element")) {
				String newId = "MathJax-Element-" + (eqRow + 1) + "-Frame";
				curEl.setAttribute("id", newId);
			}

			if (!children.getItem(i).getNodeName().equalsIgnoreCase("script")) {
				replaceChildsId(children.getItem(i), eqRow);
			}
		}
	}

	private void resizeEquations(Element el) {
		String widthAnchor = "-widthAnchor-";
		String heightAnchor = "-heightAnchor-";
		double width = 0;
		double height = 0;

		String entireStyle = el.getAttribute("style");
		entireStyle = entireStyle.replaceAll(" ", "");
		String[] styles = entireStyle.split(";");

		// get old width and height
		for (int i = 0; i < styles.length; i++) {
			if (styles[i].startsWith("width")) {
				styles[i] = styles[i].replaceFirst("width:", "").replaceFirst(
						"ex", "");
				width = Double.parseDouble(styles[i]);
				styles[i] = "width: " + widthAnchor + "px";
			} else if (styles[i].startsWith("height")) {
				styles[i] = styles[i].replaceFirst("height:", "").replaceFirst(
						"ex", "");
				height = Double.parseDouble(styles[i]);
				styles[i] = "height: " + heightAnchor + "px";
			}
		}

		// replace width and height
		String newStyle = "";
		for (String style : styles) {
			newStyle = newStyle + "; " + style;
		}
		newStyle = newStyle.replaceFirst("; ", "");

		double newWidth = eqList.getOffsetWidth();
		double newHeight = height * (newWidth / width);

		newStyle = newStyle.replaceFirst(widthAnchor, "" + (newWidth));
		newStyle = newStyle.replaceFirst(heightAnchor, "" + (newHeight));

		el.setAttribute("style", newStyle);

	}
}