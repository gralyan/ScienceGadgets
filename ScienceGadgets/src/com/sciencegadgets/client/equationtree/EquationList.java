package com.sciencegadgets.client.equationtree;

import java.util.LinkedList;

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
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class EquationList {
	AbsolutePanel mainPanel;
	AbsolutePanel wrapPanel = new AbsolutePanel();
	AbsolutePanel eqPanel = new AbsolutePanel();
	AbsolutePanel backPanel = new AbsolutePanel();

	private Grid eqGrid = new Grid(0, 1);
	private MathMLBindingTree mathMLBindingTree;
	private Timer timer;
	private LinkedList<LinkedList<MathMLBindingNode>> nodeLayers = new LinkedList<LinkedList<MathMLBindingNode>>();
	private HTML pilot = new HTML();
	private boolean inEditMode;
	public static HTML selectedWrapper;
	
	//Width of equation compared to panel
	private static final double EQUATION_FRACTION = 0.75;

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
		panel.add(backPanel);
		panel.add(eqPanel, 0, 0);
		panel.add(wrapPanel,0,0);

		if(inEditMode){
			ChangeNodeMenu changeNodeMenu = new ChangeNodeMenu(this);
			changeNodeMenu.getElement().getStyle().setZIndex(3);
			panel.add(changeNodeMenu,0,0);
		}
		
		eqGrid.setWidth(panel.getOffsetWidth() + "px");
		eqGrid.setStyleName("textCenter");
		eqPanel.add(eqGrid);

		fillNextNodeLayer(mathMLBindingTree.getLeftSide(), 0);
		fillNextNodeLayer(mathMLBindingTree.getRightSide(), 0);
		
		//Pilot equation used to transform to mathJax
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
			for(int j=0 ; j<children.getLength() ; j++){
				eq.getElement().appendChild(children.getItem(j));
			}
			int rowCount = eqGrid.getRowCount() + 2;
			eqGrid.resizeRows(rowCount);
			eqGrid.setWidget(rowCount - 2, 0, eq);
		}
		pilot.removeFromParent();
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
			svg.setAttribute("fill", "black");
			svg.setAttribute("stroke", "black");

			int left = 0, top = 0;
			double width = 0, height=0;
			String widthStr=null, heightStr=null;

			// Even out the heights of all children in a sum or term
			if ("mfenced".equalsIgnoreCase(node.getParent().getTag())) {
				String parentBareId = node.getParent().getId();
				com.google.gwt.user.client.Element parentSvg = (com.google.gwt.user.client.Element) DOM
						.getElementById(layer + 1 + "-svg" + parentBareId);

				height = JSNICalls.getElementHeight(parentSvg);
				heightStr = height+"px";
				top = parentSvg.getAbsoluteTop();

			} else {
				height = JSNICalls.getElementHeight(svg);
				heightStr = height+"px";
				top = svg.getAbsoluteTop();
				System.out.println(top);
			}

			left = svg.getAbsoluteLeft();
			width = JSNICalls.getElementWidth(svg);
			widthStr = width+"px";

			Widget wrap;
			if (inEditMode) {//Edit Mode
				wrap = new EditWrapper(node, selectedWrapper, widthStr, heightStr);
				HTMLPanel editMenu = ((EditWrapper) wrap).getEditMenu();
				wrapPanel.add(editMenu, left - mainPanel.getAbsoluteLeft(), top
						- mainPanel.getAbsoluteTop() + (int)height);
				
			} else {//Solver Mode
				wrap = node.getWrapper();
				((MLElementWrapper) wrap).setSelectedWrapper(selectedWrapper);
				wrap.setHeight(heightStr);
				wrap.setWidth(widthStr);
			}
			wrapPanel.add(wrap, left - mainPanel.getAbsoluteLeft(), top
					- mainPanel.getAbsoluteTop());

			// background images
			WrapperBackground wrapBackground = new WrapperBackground(widthStr,
					heightStr);
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
				if (oldId.equals("svg0")) {
					resizeEquations(curEl);
				}
				String newId = oldId.replaceFirst("svg", eqRow + "-svg");
				curEl.setAttribute("id", newId);

				// Each equation will have a different MathJax frame id
				// MathJax-Element-[equation #]-Frame
			} else if (oldId.equals("MathJax-Element-1-Frame")) {
				String newId = "MathJax-Element-" + (eqRow + 1) + "-Frame";
				curEl.setAttribute("id", newId);
				
//				TODO gray out the rest of the equation
				Element svgEl = curEl.getFirstChildElement().getFirstChildElement();
				svgEl.setAttribute("fill", "gray");
				svgEl.setAttribute("stroke", "gray");
				
			}else if (oldId.equals("MathJax-Element-1")) {
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
		
		String oldWidthString = style.getWidth();
		String oldHeightString = style.getHeight();
		
		oldWidthString = oldWidthString.replaceAll("[a-zA-Z ]", "");
		oldHeightString = oldHeightString.replaceAll("[a-zA-Z ]", "");
		
		double oldWidth = Double.parseDouble(oldWidthString);
		double oldHeight = Double.parseDouble(oldHeightString);
		
		double newWidth = mainPanel.getOffsetWidth() * EQUATION_FRACTION;
		double newHeight = oldHeight * (newWidth / oldWidth);
		
		style.setWidth(newWidth, Unit.PX);
		style.setHeight(newHeight, Unit.PX);
	}
}