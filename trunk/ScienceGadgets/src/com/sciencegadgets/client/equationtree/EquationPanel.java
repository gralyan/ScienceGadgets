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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Wrapper;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class EquationPanel extends AbsolutePanel {
	private HashMap<MathMLBindingNode, EquationLayer> eqLayerMap = new HashMap<MathMLBindingNode, EquationLayer>();

	MathMLBindingTree mathMLBindingTree;
	private Timer timer = null;
	private HTML pilot = new HTML();
	private boolean inEditMode;
	private double eqWidth = 0;
	private double eqHeight = 0;
	private EquationLayer rootLayer;
	public Element svgContainer;
	private CoordinateConverter coordinateConverter;
	private static EquationLayer focusLayer;
	public static Wrapper selectedWrapper;
	private Backgrounds backgrounds;
	// Width of equation compared to panel
	private static final double EQUATION_FRACTION = 0.9;

	public EquationPanel(final MathMLBindingTree jTree, Boolean inEditMode) {

		this.mathMLBindingTree = jTree;
		this.inEditMode = inEditMode;

		this.sinkEvents(Event.ONCLICK);
		this.addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// event.stopPropagation();
				// event.preventDefault();
				setFocusOut();
			}
		}, ClickEvent.getType());

		this.sinkEvents(Event.ONTOUCHSTART);
		this.addHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				setFocusOut();
			}
		}, TouchStartEvent.getType());

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
			timer = null;
			MathMLBindingNode root = mathMLBindingTree.getRoot();

			draw(root, null);

			pilot.removeFromParent();

			this.backgrounds = new Backgrounds(
					DOM.getElementById("scienceGadgetArea"),
					coordinateConverter);

			placeNextEqWrappers(root);
			// migrateSVG();

			for (EquationLayer eqLayer : eqLayerMap.values()) {
				eqLayer.setVisible(false);
			}

			// Initialize focus
			focusLayer = setFocus(Moderator.focusLayerId);
			if (focusLayer == null) {
				focusLayer = eqLayerMap.get(root);
			}
			focusLayer.setVisible(true);

			Moderator.onEqReady();
		}
	}

	public void draw(MathMLBindingNode node, EquationLayer parentLayer) {

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
		// eqLayer.setSize(this.getOffsetWidth() + "px",
		// this.getOffsetHeight() + "px");
		eqLayer.setSize("inherit", "inherit");
		eqLayer.eqPanel.add(eq);
		this.add(eqLayer, 0, 0);

		if (parentLayer == null) {
			rootLayer = parentLayer;
		}

		for (MathMLBindingNode childNode : node.getChildren()) {
			if (childNode.getType().hasChildren()) {
				draw(childNode, eqLayer);
			}
		}
	}

	public EquationLayer getFocus() {
		return focusLayer;
	}

	public void setFocusOut() {
		if (inEditMode)
			Moderator.changeNodeMenu.setVisible(false);

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

	public void setFocus(final EquationLayer newFocus) {
		final EquationLayer prevFocus = focusLayer;

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

		focusLayer = newFocus;
		Moderator.focusLayerId = focusLayer.getElement().getAttribute("id");
	}

	private void placeNextEqWrappers(MathMLBindingNode parentNode) {
		LinkedList<MathMLBindingNode> childNodes = parentNode.getChildren();
		EquationLayer eqLayer = eqLayerMap.get(parentNode);

		com.google.gwt.user.client.Element svg, parentSvg, prevSibSvg = null, nextSibSvg = null;
		String prefixIdSvg = parentNode.getId() + "svg";
		parentSvg = DOM.getElementById(prefixIdSvg + parentNode.getId());

		childLoop: for (MathMLBindingNode node : childNodes) {

			svg = DOM.getElementById(prefixIdSvg + node.getId());
			node.setSVG(svg);

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
			double height = 0, width = 0;
			int padLeft = 0, padRight = 0;

			// Top layer of equation is different
			if ("math".equalsIgnoreCase(parentNode.getTag())) {
				if ("=".equalsIgnoreCase(node.getSymbol()))
					continue childLoop;
				top = svg.getAbsoluteTop();
				left = svg.getAbsoluteLeft();
				height = JSNICalls.getElementHeight(svg);
				width = JSNICalls.getElementWidth(svg);
			} else {
				// Wrapper size is based on its parent type and size
				switch (parentNode.getType()) {
				case Term:
				case Sum:
					if (Type.Operation.equals(node.getType())) {
						width = JSNICalls.getElementWidth(svg);
						left = svg.getAbsoluteLeft();
					} else {
						// Fill from previous to next operator if exists
						left = prevSibSvg != null ? prevSibSvg
								.getAbsoluteLeft()
								+ (int) JSNICalls.getElementWidth(prevSibSvg)
								: svg.getAbsoluteLeft();
						int right = nextSibSvg != null ? nextSibSvg
								.getAbsoluteLeft() : svg.getAbsoluteLeft()
								+ (int) JSNICalls.getElementWidth(svg);

						width = right - left;

						padLeft = svg.getAbsoluteLeft() - left;
						padRight = right - svg.getAbsoluteLeft()
								+ (int) JSNICalls.getElementWidth(svg);
					}
					top = parentSvg.getAbsoluteTop();
					height = JSNICalls.getElementHeight(parentSvg);
					break;
				case Fraction:
					top = svg.getAbsoluteTop();
					height = JSNICalls.getElementHeight(svg);
					left = parentSvg.getAbsoluteLeft();
					width = JSNICalls.getElementWidth(parentSvg);
					break;
				case Exponential:
					top = svg.getAbsoluteTop();
					left = svg.getAbsoluteLeft();
					height = JSNICalls.getElementHeight(svg);
					width = JSNICalls.getElementWidth(svg);
					break;
				}
			}

			String heightStr = height + "px", widthStr = width + "px";

			Wrapper wrap;
			VerticalPanel menu = null;
			if (inEditMode) {// Edit Mode////////////////////////////
				wrap = new EditWrapper(node, this, eqLayerMap.get(node),
						widthStr, heightStr);
				menu = ((EditWrapper) wrap).getEditMenu();

			} else {// Solver Mode////////////////////////////////////
				wrap = new MLElementWrapper(node, this, eqLayerMap.get(node),
						widthStr, heightStr);
				menu = ((MLElementWrapper) wrap).getContextMenu();
			}

			node.wrap(wrap);
			eqLayer.addWrapper(wrap);
			wrap.paddingLeft = padLeft;
			wrap.paddingRight = padRight;

			// Wrapper
			eqLayer.wrapPanel.add(wrap, left - this.getAbsoluteLeft(), top
					- this.getAbsoluteTop());

			// Wrapper Menu
			eqLayer.wrapPanel.add(menu, left - this.getAbsoluteLeft(), top
					- this.getAbsoluteTop() + (int) height);

//			System.out.println(height);
//			backgrounds.addBackground(svg, width, height);

//			 background image
			 SimplePanel wrapBack = new SimplePanel();
					 //node, widthStr, heightStr);
			 wrapBack.setSize(widthStr, heightStr);
			 wrapBack.setStyleName(node.getType().toString());
			 eqLayer.backPanel.add(wrapBack, left - this.getAbsoluteLeft(),
			 top
			 - this.getAbsoluteTop());

			if (node.getType().hasChildren()) {
				placeNextEqWrappers(node);
			}
		}
	}

	// private void migrateSVG() {
	// for (EquationLayer eqLayer : eqLayerMap.values()) {
	// for (Wrapper wrap : eqLayer.getWrappers()) {
	//
	// Element svg = (Element) svgContainer.cloneNode(false);
	//
	// // Element svg = DOM.createElement("svg");
	// // svg.setAttribute("id", svgContainer.getAttribute("id"));
	// // svg.setAttribute("class", "mjx-svg-math");
	// // svg.setAttribute("xmlns:xlink",
	// // "http://www.w3.org/1999/xlink");
	// // svg.setAttribute("style", "width: " + wrap.getOffsetWidth()
	// // + "; height: " + wrap.getOffsetHeight() + ";");
	// // svg.setAttribute("viewBox", "0 0 1000 1000");//
	// +wrap.getOffsetWidth()+" "+wrap.getOffsetHeight());
	//
	// svg.getStyle().setLeft(0, Unit.PX);
	// svg.getStyle().setTop(0, Unit.PX);
	//
	// Element svgGroup = wrap.getSVG();
	// svgGroup.setAttribute("transform", "scale(1, -1)");
	// svg.appendChild(svgGroup);
	// wrap.getElement().appendChild(svg);
	// }
	// }
	// }

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
					if (svgContainer == null) {
						svgContainer = curEl;
					}
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

			double newWidth = this.getOffsetWidth() * EQUATION_FRACTION;
			double newHeight = oldHeight * (newWidth / oldWidth);

			eqWidth = newWidth;
			eqHeight = newHeight;

			style.setWidth(newWidth, Unit.PX);
			style.setHeight(newHeight, Unit.PX);

			// Global/SVG coordinate converter
			String[] svgSizes = svgContainer.getAttribute("viewBox").split(" ");
			coordinateConverter = new CoordinateConverter(
					(Double.parseDouble(svgSizes[2])) / eqWidth,
					(Double.parseDouble(svgSizes[3])) / eqHeight, //
					this.getAbsoluteLeft()
					// gap made by centering equation
							+ (getOffsetWidth() * (1 - EQUATION_FRACTION)) / 2,
					this.getAbsoluteTop());
		} else {
			style.setWidth(eqWidth, Unit.PX);
			style.setHeight(eqHeight, Unit.PX);
		}
	}

	private void setColor(Element element, String color) {
		element.setAttribute("fill", color);
		element.setAttribute("stroke", color);
	}

	public CoordinateConverter getCoordinateConverter() {
		return coordinateConverter;
	}
}