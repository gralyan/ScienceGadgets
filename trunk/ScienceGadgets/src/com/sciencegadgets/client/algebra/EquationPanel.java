package com.sciencegadgets.client.algebra;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Locale;

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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.algebra.edit.EditWrapper;

public class EquationPanel extends AbsolutePanel {
	private HashMap<MathMLBindingNode, EquationLayer> eqLayerMap = new HashMap<MathMLBindingNode, EquationLayer>();

	MathMLBindingTree mathMLBindingTree;
	private Timer timer = null;
	private HTML pilot = new HTML();
	private boolean inEditMode;
	private double newFontSize = 0;
	private EquationLayer rootLayer;
	private static EquationLayer focusLayer;
	public static Wrapper selectedWrapper;
	// Width of equation compared to panel
	private static final double EQUATION_FRACTION = 0.8;

	public EquationPanel(final MathMLBindingTree jTree, Boolean inEditMode) {

		this.mathMLBindingTree = jTree;
		this.inEditMode = inEditMode;

		this.sinkEvents(Event.ONCLICK);
		this.addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				event.preventDefault();
				event.stopPropagation();
				setFocusOut();
			}
		}, ClickEvent.getType());

		this.sinkEvents(Event.ONTOUCHSTART);
		this.addHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				event.stopPropagation();
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
		NodeList<Element> list = this.getElement().getElementsByTagName("span");

		if (list.getLength() > 1) {

			timer.cancel();
			timer = null;

			MathMLBindingNode root = mathMLBindingTree.getRoot();

			NodeList<Element> nobrList = pilot.getElement()
					.getElementsByTagName("nobr");
			for (int j = 0; j < nobrList.getLength(); j++) {
				Element nobr = nobrList.getItem(j);
				Element nobrParent = nobr.getParentElement();
				NodeList<Node> nobrChildren = nobr.getChildNodes();
				for (int k = 0; k < nobrChildren.getLength(); k++) {
					nobrParent.appendChild(nobrChildren.getItem(k));
				}
				nobr.removeFromParent();
			}

			pilot.getElement().getElementsByTagName("script").getItem(0)
					.removeFromParent();

			draw(root, null);

			pilot.removeFromParent();

			// this.backgrounds = new Backgrounds(
			// DOM.getElementById("scienceGadgetArea"),
			// coordinateConverter);

			 placeNextEqWrappers(root);

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

	/**
	 * Replicates the equation graphic for each equation layer (node) to be
	 * displayed when this layer is in focus
	 */
	public void draw(MathMLBindingNode node, EquationLayer parentLayer) {

		Node pilotClone = pilot.getElement().cloneNode(true);
		replaceChildsId(pilotClone, node.getId());
		EquationLayer eqLayer = new EquationLayer();

		// Transfer the children (side)(=)(side) from pilotClone
		NodeList<Node> children = pilotClone.getChildNodes();
		for (int j = 0; j < children.getLength(); j++) {
			eqLayer.getElement().appendChild(children.getItem(j));
		}

		AbsolutePanel menuPanel = eqLayer.getContextMenuPanel();
		menuPanel.getElement().setAttribute("id", "menuLayer-" + node.getId());
		menuPanel.setSize(this.getOffsetWidth()+"px", this.getOffsetHeight()+"px");
		this.add(menuPanel,0,0);

		eqLayer.setParentLayer(parentLayer);
		eqLayerMap.put(node, eqLayer);
		eqLayer.getElement().setAttribute("id", "eqLayer-" + node.getId());
		eqLayer.setStyleName("textCenter");
		this.add(eqLayer,0,this.getOffsetHeight()/2);

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

		com.google.gwt.user.client.Element svg;
		
		childLoop: for (MathMLBindingNode node : childNodes) {

			svg = DOM.getElementById("Wrapper-" + node.getId()+"-ofLayer-"+parentNode.getId());
			node.setSVG(svg);

			Wrapper wrap;
			VerticalPanel menu = null;
			if (inEditMode) {// Edit Mode////////////////////////////
				wrap = new EditWrapper(node, this, eqLayerMap.get(node),
						svg);
				menu = ((EditWrapper) wrap).getEditMenu();

			} else {// Solver Mode////////////////////////////////////
				wrap = new MLElementWrapper(node, this, eqLayerMap.get(node),
						svg);
				menu = ((MLElementWrapper) wrap).getContextMenu();
			}
			
//			wrap.onAttach();

			eqLayer.addWrapper(wrap);

			eqLayer.ContextMenuPanel.add(menu, wrap.getAbsoluteLeft() - this.getAbsoluteLeft(), wrap.getAbsoluteTop()
					- this.getAbsoluteTop() + wrap.getOffsetHeight());

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
	 * @param layerId
	 */
	private void replaceChildsId(Node parent, String layerId) {

		for (int i = 0; i < parent.getChildCount(); i++) {
			Element curEl = ((Element) parent.getChild(i));
			if(Node.TEXT_NODE==curEl.getNodeType()){
				return;
			}
			String oldId = curEl.getId();
			String newId = null;
			
			evenPadding(curEl);
			
			if (!"".equals(oldId) && oldId != null) {

				// Each wrapper has a reference to its MathNode and Layer
				// Wrapper-[equation id]-ofLayer-[MathML node id]
				// example: Wrapper-1-ofLayer-ML1
				if (oldId.contains("ML")) {
					newId = "Wrapper-"+oldId +"-ofLayer-"+layerId;

					// Each equation will have a different MathJax frame id
					// MathJax-Element-[equation #]-Frame
				} else if (oldId.contains("MathJax-Element-") && oldId.contains("-Frame")) {
					newId = "MathJax-Element-" + (layerId) + "-Frame";

					Element fontElement = curEl.getFirstChildElement().getFirstChildElement();
					resizeEquations(fontElement);
					removeClip(fontElement.getFirstChildElement());

					// gray out the rest of the equation
					// TODO
//					 Element svgEl = curEl.getFirstChildElement().getFirstChildElement();
//					 setColor(curEl, "gray");

				} 

				if (newId != null)
					curEl.setAttribute("id", newId);
			}
			 if (curEl.getChildCount() > 0)
			replaceChildsId(curEl, layerId);
		}
	}

	/**
	 * Gives the top node of each equation a certain size
	 * 
	 * @param el
	 */
	private void resizeEquations(Element el) {
		Style style = el.getStyle();
		
		if (newFontSize == 0) {
			String oldWidthString = style.getWidth().replaceAll("[a-zA-Z ]", "");
			double oldWidth = Double.parseDouble(oldWidthString);
			double widthRatio = this.getOffsetWidth()/oldWidth;
			
			String oldHeightString = style.getHeight().replaceAll("[a-zA-Z ]", "");
			double oldHeight = Double.parseDouble(oldHeightString);
			double heightRatio = this.getOffsetHeight()/oldHeight;

			String oldFontString = style.getFontSize().replaceAll("%", "");
			double oldFontValue = Double.parseDouble(oldFontString);
		
			double smallerRatio = (widthRatio>heightRatio) ? heightRatio:widthRatio;
			
			newFontSize = smallerRatio * oldFontValue; 
		}

		style.setFontSize(newFontSize, Unit.PCT);
		style.setWidth(this.getOffsetWidth(), Unit.PX);
//		style.setHeight(this.getOffsetHeight(), Unit.PX);
	}

	private void removeClip(Element el){
		el.getStyle().setProperty("clip", "auto");
	}
	
	/**
	 * Mathjax originally only places padding on left. This method cuts the left padding in half and adds padding to the right to center the content
	 */
	private void evenPadding(Element el){//TODO
//		try{
			Style style = el.getStyle();
			String leftP = style.getPaddingLeft();
			
			if(leftP == null || "".equals(leftP)){
				return;
			}
			
			leftP = leftP.toLowerCase();
			
			String unitLetters = null;
			Unit unitProper = null;
			
			for(Unit c : Unit.values()){
				if(leftP.contains(c.getType())){
					unitLetters = c.getType();
					unitProper = c;
				}
			}
			if(unitLetters==null || unitProper==null){
				return;
			}
			
			leftP = leftP.replaceFirst(unitLetters, "");
			double newPad = Double.parseDouble(leftP);
			newPad = newPad / 2;
			style.setPaddingLeft(newPad, unitProper);
			style.setPaddingRight(newPad, unitProper);
		
//		}catch(Exception e){
//			e.printStackTrace();
//			JSNICalls.consoleLog("e.getCause.str: "+e.getCause().toString());
//			JSNICalls.consoleLog("e.str: "+e.toString());
//		}
	}

	private void setColor(Element element, String color) {
		element.setAttribute("color", color);
//		element.setAttribute("fill", color);
//		element.setAttribute("stroke", color);
	}

}