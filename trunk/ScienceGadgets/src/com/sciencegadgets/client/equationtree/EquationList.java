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
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class EquationList {
	private MathMLBindingTree mathMLBindingTree;
	private AbsolutePanel mainPanel;
	private AbsolutePanel wrapPanel = new AbsolutePanel();
	private AbsolutePanel eqPanel = new AbsolutePanel();
	private AbsolutePanel backPanel = new AbsolutePanel();
	private Grid eqGrid = new Grid(1, 1);
	private Timer timer;
	private LinkedList<LinkedList<MathMLBindingNode>> nodeLayers = new LinkedList<LinkedList<MathMLBindingNode>>();
	private HTML responseNotes = new HTML();
	private boolean inEditMode;
	private static HTML selectedWrapper;

	public EquationList(AbsolutePanel panel, final MathMLBindingTree jTree, Boolean inEditMode) {

		this.mathMLBindingTree = jTree;
		this.mainPanel = panel;
		
		this.inEditMode = inEditMode;

		wrapPanel.setStyleName("treeCanvas");
		eqPanel.setStyleName("treeCanvas");
		backPanel.setStyleName("treeCanvas");

		wrapPanel.getElement().getStyle().setZIndex(3);
		eqPanel.getElement().getStyle().setZIndex(2);
		backPanel.getElement().getStyle().setZIndex(1);
		panel.add(wrapPanel);
		panel.add(eqPanel, 0, 0);
		panel.add(backPanel, 0, 0);

		// responseNotes.setSize(eqGrid.getOffsetWidth() + "px", "40px");
		eqGrid.setWidth(panel.getOffsetWidth() + "px");
		eqGrid.setStyleName("textCenter");
		responseNotes.getElement().setAttribute("id", "responseNotes");
		eqGrid.setWidget(0, 0, responseNotes);
		eqPanel.add(eqGrid);

		fillNextNodeLayer(mathMLBindingTree.getLeftSide(), 0);
		fillNextNodeLayer(mathMLBindingTree.getRightSide(), 0);
		
		//Pilot equation used to transform to mathJax
		responseNotes.getElement().appendChild(mathMLBindingTree.getMathML());
		JSNICalls.parseMathJax("responseNotes");

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

		for (int i = 1; i < nodeLayers.size(); i++) {
//			System.out.println("eq1 i-" +i);
//			Node nextEq = mathMLBindingTree.getMathML();

//			replaceChildsId(nextEq, i);
			Node nextEq = responseNotes.getElement().getFirstChild().cloneNode(true);
			replaceChildsId(nextEq, i);
			HTML eq = new HTML();
			eq.getElement().appendChild(nextEq);

			int rowCount = eqGrid.getRowCount() + 2;
			eqGrid.resizeRows(rowCount);
			eqGrid.setWidget(rowCount - 1, 0, eq);
		}
		placeNextEqWrappers(0);
	}

	// /////////////////////////////////////////////////////////////
	// Details
	// ////////////////////////////////////////////////////////////

	private void fillNextNodeLayer(MathMLBindingNode parent, int layer) {
		LinkedList<MathMLBindingNode> children = parent.getChildren();

		if (nodeLayers.size() < layer + 1) {
			nodeLayers.add(new LinkedList<MathMLBindingNode>());
		}

		nodeLayers.get(layer).addAll(children);

		for (int i = 0; i < children.size(); i++) {
			// System.out.println("for layer: "+layer+"-"+i);
			MathMLBindingNode curChild = children.get(i);

			if (curChild.getChildCount() > 0) {
				fillNextNodeLayer(curChild, layer + 1);
			}
		}
	}

	private void placeNextEqWrappers(int layer) {
		LinkedList<MathMLBindingNode> nodes = nodeLayers.get(layer);

		for (MathMLBindingNode node : nodes) {
			String bareId = node.getId();

			com.google.gwt.user.client.Element svg = DOM.getElementById(layer
					+ 1 + "-svg" + bareId);
			svg.setAttribute("style", "fill:red;stroke:red");

			String width = null, height = null;
			int left = 0, top = 0;

			// Even out the heights of all children in a sum or term
			if ("mfenced".equalsIgnoreCase(node.getParent().getTag())) {
				String parentBareId = node.getParent().getId();
				com.google.gwt.user.client.Element parentSvg = (com.google.gwt.user.client.Element) DOM
						.getElementById(layer + 1 + "-svg" + parentBareId);

				height = JSNICalls.getElementHeight(parentSvg) + "px";
				top = parentSvg.getAbsoluteTop();

			} else {
				height = JSNICalls.getElementHeight(svg) + "px";
				top = svg.getAbsoluteTop();
			}

			left = svg.getAbsoluteLeft();
			width = JSNICalls.getElementWidth(svg) + "px";

			if (inEditMode) {
				responseNotes.setHTML("<span>Edit Mode</span>");
				EditWrapper wrap = new EditWrapper(node, selectedWrapper, width, height);
				wrapPanel.add(wrap, left - mainPanel.getAbsoluteLeft(), top
						- mainPanel.getAbsoluteTop());
			} else {
				// Drag handlers
				responseNotes.setHTML("<span>Solver Mode</span>");
				MLElementWrapper wrap = node.getWrapper();
				wrap.setSelectedWrapper(selectedWrapper);
				wrap.setHeight(height);
				wrap.setWidth(width);
				wrapPanel.add(wrap, left - mainPanel.getAbsoluteLeft(), top
						- mainPanel.getAbsoluteTop());
			}
			// background images
			WrapperBackground wrapBackground = new WrapperBackground(width,
					height);
			backPanel.add(wrapBackground, left - mainPanel.getAbsoluteLeft(),
					top - mainPanel.getAbsoluteTop());

		}
		if (nodeLayers.size() > layer + 1) {
			placeNextEqWrappers(layer + 1);
		}
	}

	/**
	 * Each equation must have a different set of ID's which only differ in the
	 * prefix. The prefix is the equations placement in the list
	 * 
	 * @param parent
	 * @param eqRow
	 */
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

	/**
	 * Gives the top node of each equation a certain size
	 * 
	 * @param el
	 */
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

		// Width will always be 1/2 the panel, height is calculated from width
		double newWidth = mainPanel.getOffsetWidth() / 2;
		double newHeight = height * (newWidth / width);

		newStyle = newStyle.replaceFirst(widthAnchor, "" + (newWidth));
		newStyle = newStyle.replaceFirst(heightAnchor, "" + (newHeight));

		el.setAttribute("style", newStyle);

	}
}