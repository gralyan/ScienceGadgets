package com.sciencegadgets.client.equationtree;

import java.util.LinkedList;
import java.util.Stack;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class EquationList {
	AbsolutePanel mainPanel;
	AbsolutePanel wrapPanel = new AbsolutePanel();
	AbsolutePanel eqPanel = new AbsolutePanel();
	AbsolutePanel backPanel = new AbsolutePanel();
//	LinkedList<AbsolutePanel> eqStack = new LinkedList<AbsolutePanel>();
//	LinkedList<AbsolutePanel> wrapPanels = new LinkedList<AbsolutePanel>();
//	LinkedList<AbsolutePanel> eqPanels = new LinkedList<AbsolutePanel>();
//	LinkedList<AbsolutePanel> backPanels = new LinkedList<AbsolutePanel>();

	private Grid eqGrid = new Grid(0, 1);
	private MathMLBindingTree mathMLBindingTree;
	private Timer timer;
	private LinkedList<LinkedList<MathMLBindingNode>> nodeLayers = new LinkedList<LinkedList<MathMLBindingNode>>();
	private HTML pilot = new HTML();
	private boolean inEditMode;
	private double eqWidth = 0;
	private double eqHeight = 0;
	public static HTML selectedWrapper;

	// Width of equation compared to panel
	private static final double EQUATION_FRACTION = 0.75;

	public EquationList(AbsolutePanel panel, final MathMLBindingTree jTree,
			Boolean inEditMode) {

		this.mathMLBindingTree = jTree;
		this.mainPanel = panel;
		this.inEditMode = inEditMode;

		wrapPanel.getElement().getStyle().setZIndex(3);
		eqPanel.getElement().getStyle().setZIndex(2);
		backPanel.getElement().getStyle().setZIndex(1);

		String panelWidth = mainPanel.getOffsetWidth() + "px";
		wrapPanel.setWidth(panelWidth);
		eqPanel.setWidth(panelWidth);
		backPanel.setWidth(panelWidth);

		panel.add(backPanel);
		panel.add(eqPanel, 0, 0);
		panel.add(wrapPanel, 0, 0);

		eqGrid.setWidth(panel.getOffsetWidth() + "px");
		eqGrid.setStyleName("textCenter");
		eqPanel.add(eqGrid);
		
		fillNextNodeLayer(mathMLBindingTree.getLeftSide(), 0);
		fillNextNodeLayer(mathMLBindingTree.getRightSide(), 0);

		// Pilot equation used to transform to mathJax
		Element pilotEl = pilot.getElement();
		pilotEl.appendChild(mathMLBindingTree.getMathML());
		pilotEl.setAttribute("id", "pilotMathJax");
		panel.add(pilot);
		JSNICalls.parseMathJax("pilotMathJax");

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

			Node nextEq = pilot.getElement().cloneNode(true);
			replaceChildsId(nextEq, i);
			HTML eq = new HTML();

			NodeList<Node> children = nextEq.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				eq.getElement().appendChild(children.getItem(j));
			}
			int rowCount = eqGrid.getRowCount() + 1;
			eqGrid.resizeRows(rowCount);
			eqGrid.setWidget(rowCount - 1, 0, eq);
		}
		pilot.removeFromParent();
		placeNextEqWrappers(0);

		// Only show one equation at a time in the scroll panel
		Moderator.eqHeight = eqGrid.getOffsetHeight() / eqGrid.getRowCount();
		mainPanel.getParent().setHeight(Moderator.eqHeight + "px");
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
			MathMLBindingNode curChild = children.get(i);

			if (curChild.getChildCount() > 0) {
				fillNextNodeLayer(curChild, layer + 1);
			}
		}
	}

	private void placeNextEqWrappers(int layer) {
		LinkedList<MathMLBindingNode> nodes = nodeLayers.get(layer);

		int panelHeight = eqGrid.getOffsetHeight();
		mainPanel.setHeight(panelHeight + "px");
		eqPanel.setHeight(panelHeight + "px");
		wrapPanel.setHeight(panelHeight + "px");
		backPanel.setHeight(panelHeight + "px");

		for (MathMLBindingNode node : nodes) {
			//TODO overlay the parents wrapper behind the backround as wrll, maybe give the backWrappers id's and re-call them
			String bareId = node.getId();

			com.google.gwt.user.client.Element svg = DOM.getElementById(layer
					+ 1 + "-svg" + bareId);
			svg.setAttribute("fill", "black");
			svg.setAttribute("stroke", "black");

			int left = 0, top = 0;
			double width = 0, height = 0;
			String widthStr = null, heightStr = null;

			// String parentBareId = node.getParent().getId();
			// com.google.gwt.user.client.Element parentSvg =
			// (com.google.gwt.user.client.Element) DOM
			// .getElementById(layer + 1 + "-svg" + parentBareId);

			top = svg.getAbsoluteTop();
			left = svg.getAbsoluteLeft();

			height = JSNICalls.getElementHeight(svg);
			heightStr = height + "px";
			width = JSNICalls.getElementWidth(svg);
			widthStr = width + "px";

			Widget wrap;
			if (inEditMode) {// Edit Mode
				wrap = new EditWrapper(node, this, widthStr, heightStr);
				HTMLPanel editMenu = ((EditWrapper) wrap).getEditMenu();
				wrapPanel.add(editMenu, left - mainPanel.getAbsoluteLeft(), top
						- mainPanel.getAbsoluteTop() + (int) height);

			} else {// Solver Mode
				wrap = node.getWrapper();
				((MLElementWrapper) wrap).setSelectedWrapper(selectedWrapper);
				wrap.setHeight(heightStr);
				wrap.setWidth(widthStr);
			}

			wrapPanel.add(wrap, left - mainPanel.getAbsoluteLeft(), top
					- mainPanel.getAbsoluteTop());

			// background images
			WrapperBackground wrapBackground = new WrapperBackground(node,
					widthStr, heightStr);
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
			// [equation #]-svg[MathML node id] example 1-svg0
			if (oldId.contains("svg")) {
				String curClass = curEl.getAttribute("class");
				if ("mjx-svg-math".equalsIgnoreCase(curClass)) {
					resizeEquations(curEl);
				}
				String newId = oldId.replaceFirst("svg", eqRow + "-svg");
				curEl.setAttribute("id", newId);

				// Each equation will have a different MathJax frame id
				// MathJax-Element-[equation #]-Frame
			} else if (oldId.equals("MathJax-Element-1-Frame")) {
				String newId = "MathJax-Element-" + (eqRow + 1) + "-Frame";
				curEl.setAttribute("id", newId);

				// gray out the rest of the equation
				Element svgEl = curEl.getFirstChildElement()
						.getFirstChildElement();
				svgEl.setAttribute("fill", "gray");
				svgEl.setAttribute("stroke", "gray");

			} else if (oldId.equals("MathJax-Element-1")) {
				String newId = "MathJax-Element-" + (eqRow + 1);
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

		Style style = el.getStyle();

		if (eqWidth == 0) {
			String oldWidthString = style.getWidth();
			String oldHeightString = style.getHeight();

			oldWidthString = oldWidthString.replaceAll("[a-zA-Z ]", "");
			oldHeightString = oldHeightString.replaceAll("[a-zA-Z ]", "");

			double oldWidth = Double.parseDouble(oldWidthString);
			double oldHeight = Double.parseDouble(oldHeightString);

			double newWidth = mainPanel.getOffsetWidth() * EQUATION_FRACTION;
			double newHeight = oldHeight * (newWidth / oldWidth);

			eqWidth = newWidth;
			eqHeight = newHeight;

			style.setWidth(newWidth, Unit.PX);
			style.setHeight(newHeight, Unit.PX);
		} else {
			style.setWidth(eqWidth, Unit.PX);
			style.setHeight(eqHeight, Unit.PX);
		}
	}
}