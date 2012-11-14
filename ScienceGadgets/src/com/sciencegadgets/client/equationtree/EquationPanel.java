package com.sciencegadgets.client.equationtree;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Duration;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebramanipulation.Fade;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class EquationPanel extends AbsolutePanel {
	AbsolutePanel mainPanel = this;
	// public LinkedList<EquationLayer> eqLayers = new
	// LinkedList<EquationLayer>();
	HashMap<MathMLBindingNode, EquationLayer> eqLayerMap = new HashMap<MathMLBindingNode, EquationLayer>();

	private MathMLBindingTree mathMLBindingTree;
	private Timer timer;
	// private LinkedList<LinkedList<MathMLBindingNode>> nodeLayers = new
	// LinkedList<LinkedList<MathMLBindingNode>>();
	private HTML pilot = new HTML();
	private boolean inEditMode;
	private double eqWidth = 0;
	private double eqHeight = 0;
	private static EquationLayer focusLayer = Moderator.focusLayer;
	public static HTML selectedWrapper;
	// Width of equation compared to panel
	private static final double EQUATION_FRACTION = 0.75;

	public EquationPanel(final MathMLBindingTree jTree, Boolean inEditMode) {

		this.mathMLBindingTree = jTree;
		this.inEditMode = inEditMode;

		// fillNextNodeLayer(mathMLBindingTree.getLeftSide(), 0);
		// fillNextNodeLayer(mathMLBindingTree.getRightSide(), 0);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		this.setPixelSize(getParent().getOffsetWidth(), getParent()
				.getOffsetHeight());

		// Pilot equation used to transform to mathJax
		Element pilotEl = pilot.getElement();
		pilotEl.appendChild(mathMLBindingTree.getMathML());
		pilotEl.setAttribute("id", "pilotMathJax");
		this.add(pilot);
		JSNICalls.parseMathJax(pilotEl);

		// Wait for mathjax to format first
		timer = new Timer() {
			public void run() {
				tryToDraw();
			}
		};
		timer.scheduleRepeating(50);
	}

	private void tryToDraw() {
		if (this.getElement().getElementsByTagName("g").getLength() > 0) {
			timer.cancel();
			MathMLBindingNode root = mathMLBindingTree.getRoot();

			draw(root, null);

			pilot.removeFromParent();
			placeNextEqWrappers(root);

			for (EquationLayer eqLayer : eqLayerMap.values()) {
				eqLayer.setVisible(false);
			}

			try{
				focusLayer = setFocus(focusLayer.getElement()
						.getAttribute("id"));
			}catch(NullPointerException e){
				focusLayer = eqLayerMap.get(root);
			}
			focusLayer.setVisible(true);

			Moderator.onEqReady();
		}
	}

	public void draw(MathMLBindingNode node, EquationLayer parentLayer) {

		// for (int i = 1; i < nodeLayers.size(); i++) {

		Node pilotClone = pilot.getElement().cloneNode(true);
		replaceChildsId(pilotClone, node.getId());
		HTML eq = new HTML();

		// Transfer the children (side)(=)(side) from pilotClone
		NodeList<Node> children = pilotClone.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			eq.getElement().appendChild(children.getItem(j));
		}

		EquationLayer eqLayer = new EquationLayer();
		eqLayer.setParentLayer(parentLayer);
		eqLayerMap.put(node, eqLayer);
		eqLayer.getElement().setAttribute("id", "eqLayer-" + node.getId());
		eqLayer.setSize(mainPanel.getOffsetWidth() + "px",
				mainPanel.getOffsetHeight() + "px");

		eqLayer.eqPanel.add(eq);

		mainPanel.add(eqLayer, 0, 0);

		for (MathMLBindingNode childNode : node.getChildren()) {
			if (childNode.getType().hasChildren())
				draw(childNode, eqLayer);
		}

	}

	public EquationLayer getFocus() {
		return focusLayer;
	}

	public void setFocusOut() {
		EquationLayer parentLayer = focusLayer.getParentLayer();
		if (parentLayer != null)
			setFocus(parentLayer);
	}

	public EquationLayer setFocus(String layerId) {
		if (layerId != null) {
			for (EquationLayer eqLayer : eqLayerMap.values()) {
				if (layerId.equals(eqLayer.getElement().getAttribute("id"))) {
					setFocus(eqLayer);
					return eqLayer;
				}
			}
		}
		return null;
	}

	public void setFocus(EquationLayer eqLayer) {
		try {
			final EquationLayer prevFocus = focusLayer;
			final EquationLayer newFocus = eqLayer;

			newFocus.setOpacity(0);
			newFocus.setVisible(true);

			Animation fade = new Animation() {
				@Override
				protected void onUpdate(double progress) {
					newFocus.setOpacity(progress);
					prevFocus.setOpacity(1 - progress);
				}

				@Override
				protected void onComplete() {
					super.onComplete();
					prevFocus.setVisible(false);
				}
			};

			fade.run(300, Duration.currentTimeMillis() - 100);

			focusLayer = eqLayer;

		} catch (IndexOutOfBoundsException e) {
		}
	}

	// /////////////////////////////////////////////////////////////
	// Details
	// ////////////////////////////////////////////////////////////

	// private void fillNextNodeLayer(MathMLBindingNode parent, int layer) {
	// LinkedList<MathMLBindingNode> children = parent.getChildren();
	//
	// if (nodeLayers.size() < layer + 1) {
	// nodeLayers.add(new LinkedList<MathMLBindingNode>());
	// }
	//
	// nodeLayers.get(layer).addAll(children);
	//
	// for (int i = 0; i < children.size(); i++) {
	// MathMLBindingNode curChild = children.get(i);
	//
	// if (curChild.getChildCount() > 0) {
	// fillNextNodeLayer(curChild, layer + 1);
	// }
	// }
	// }

	private void placeNextEqWrappers(MathMLBindingNode parentNode) {
		LinkedList<MathMLBindingNode> childNodes = parentNode.getChildren();
		EquationLayer eqLayer = eqLayerMap.get(parentNode);

		childLoop: for (MathMLBindingNode node : childNodes) {
			com.google.gwt.user.client.Element svg, parentSvg, prevSibSvg = null, nextSibSvg = null;
			String prefixIdSvg = parentNode.getId() + "svg";

			svg = DOM.getElementById(prefixIdSvg + node.getId());
			parentSvg = DOM.getElementById(prefixIdSvg + parentNode.getId());
			try {
				prevSibSvg = DOM.getElementById(prefixIdSvg
						+ node.getPrevSibling().getId());
			} catch (IndexOutOfBoundsException e) {
			}
			try {
				nextSibSvg = DOM.getElementById(prefixIdSvg
						+ node.getNextSibling().getId());
			} catch (IndexOutOfBoundsException e) {
			}

			// Only the focused svg in the current layer
			setColor(svg, "black");

			int top = 0, left = 0;
			double height = 0;
			String heightStr = null, widthStr = null;

			// Top layer of equation is different
			if ("math".equalsIgnoreCase(parentNode.getTag())) {
				if ("=".equalsIgnoreCase(node.getSymbol()))
					continue childLoop;
				top = svg.getAbsoluteTop();
				left = svg.getAbsoluteLeft();
				height = JSNICalls.getElementHeight(svg);
				heightStr = height + "px";
				widthStr = JSNICalls.getElementWidth(svg) + "px";
			} else {
				// Wrapper size is based on its parent type and size
				switch (parentNode.getType()) {
				case Term:
				case Sum:
					if (Type.Operation.equals(node.getType())) {
						widthStr = JSNICalls.getElementWidth(svg) + "px";
						left = svg.getAbsoluteLeft();
					} else {
						// Fill from previous to next operator if exists
						left = prevSibSvg != null ? prevSibSvg
								.getAbsoluteLeft()
								+ (int) JSNICalls.getElementWidth(prevSibSvg)
								: svg.getAbsoluteLeft();
						double right = nextSibSvg != null ? nextSibSvg
								.getAbsoluteLeft() : svg.getAbsoluteLeft()
								+ (int) JSNICalls.getElementWidth(svg);

						widthStr = (right - left) + "px";
					}
					top = parentSvg.getAbsoluteTop();
					height = JSNICalls.getElementHeight(parentSvg);
					heightStr = height + "px";
					break;
				case Fraction:
					top = svg.getAbsoluteTop();
					height = JSNICalls.getElementHeight(svg);
					heightStr = height + "px";
					left = parentSvg.getAbsoluteLeft();
					widthStr = JSNICalls.getElementWidth(parentSvg) + "px";
					break;
				case Exponential:
					top = svg.getAbsoluteTop();
					left = svg.getAbsoluteLeft();
					height = JSNICalls.getElementHeight(svg);
					heightStr = height + "px";
					widthStr = JSNICalls.getElementWidth(svg) + "px";
					break;
				}
			}

			Widget wrap;
			if (inEditMode) {// Edit Mode////////////////////////////
				wrap = new EditWrapper(node, this, eqLayerMap.get(node),
						widthStr, heightStr);
				EditMenu editMenu = ((EditWrapper) wrap).getEditMenu();

				eqLayer.wrapPanel.add(editMenu,
						left - mainPanel.getAbsoluteLeft(),
						top - mainPanel.getAbsoluteTop() + (int) height);

			} else {// Solver Mode////////////////////////////////////
				wrap = node.getWrapper();
				((MLElementWrapper) wrap).setSelectedWrapper(selectedWrapper);
				wrap.setHeight(heightStr);
				wrap.setWidth(widthStr);
			}
			eqLayer.wrapPanel.add(wrap, left - mainPanel.getAbsoluteLeft(), top
					- mainPanel.getAbsoluteTop());

			// // Parent background image
			// WrapperBackground pWrapBack = new WrapperBackground(
			// node.getParent(),//
			// JSNICalls.getElementWidth(parentSvg) + "px",
			// JSNICalls.getElementHeight(parentSvg) + "px");
			// eqLayer.parentBackPanel.add(pWrapBack, //
			// parentSvg.getAbsoluteLeft() - mainPanel.getAbsoluteLeft(),//
			// parentSvg.getAbsoluteTop() - mainPanel.getAbsoluteTop());

			// background image
			WrapperBackground wrapBack = new WrapperBackground(node, widthStr,
					heightStr);
			eqLayer.backPanel.add(wrapBack, left - mainPanel.getAbsoluteLeft(),
					top - mainPanel.getAbsoluteTop());

			if (node.getType().hasChildren()) {
				placeNextEqWrappers(node);
			}
		}
	}

	/**
	 * Each equation must have a different set of ID's which only differ in the
	 * prefix. The prefix is the equations placement in the list
	 * 
	 * @param parent
	 * @param eqRow
	 */
	private void replaceChildsId(Node parent, String layerId) {
		NodeList<Node> children = parent.getChildNodes();

		for (int i = 0; i < children.getLength(); i++) {
			Element curEl = ((Element) children.getItem(i));
			String oldId = curEl.getAttribute("id");
			String newId = null;

			// Each equation in the list will have a different prefix for id's
			// [equation #]-svg[MathML node id] example 1-svg0
			if (oldId.contains("svg")) {
				String curClass = curEl.getAttribute("class");
				if ("mjx-svg-math".equalsIgnoreCase(curClass)) {
					resizeEquations(curEl);
				}
				newId = oldId.replaceFirst("svg", layerId + "svg");

				// Each equation will have a different MathJax frame id
				// MathJax-Element-[equation #]-Frame
			} else if (oldId.equals("MathJax-Element-1-Frame")) {
				newId = "MathJax-Element-" + (layerId) + "-Frame";

				// gray out the rest of the equation
				Element svgEl = curEl.getFirstChildElement()
						.getFirstChildElement();
				setColor(svgEl, "gray");

			} else if (oldId.equals("MathJax-Element-1")) {
				newId = "MathJax-Element-" + (layerId);
			}

			if (newId != null)
				curEl.setAttribute("id", newId);

			if (!children.getItem(i).getNodeName().equalsIgnoreCase("script"))
				replaceChildsId(children.getItem(i), layerId);
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

	private void setColor(Element element, String color) {
		element.setAttribute("fill", color);
		element.setAttribute("stroke", color);

	}
}