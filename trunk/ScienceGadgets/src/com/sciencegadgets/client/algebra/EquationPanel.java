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
import com.sciencegadgets.client.algebra.MathMLBindingTree.Type;
import com.sciencegadgets.client.algebra.edit.EditWrapper;

public class EquationPanel extends AbsolutePanel {
	private HashMap<MathMLBindingNode, EquationLayer> eqLayerMap = new HashMap<MathMLBindingNode, EquationLayer>();

	MathMLBindingTree mathMLBindingTree;
	// private HTML pilot = new HTML();
	private boolean inEditMode;
	private double newFontSize = 0;
	private EquationLayer rootLayer;
	private static EquationLayer focusLayer;
	public static Wrapper selectedWrapper;
	// Width of equation compared to panel
	private static final double EQUATION_FRACTION = 0.8;

	public EquationPanel(MathMLBindingTree mathTree, boolean inEditMode) {

		this.mathMLBindingTree = mathTree;
		this.inEditMode = inEditMode;

		setStyleName("eqPanel");
		// zIndex eqPanel=1 wrapper=2 menu=3
		this.getElement().getStyle().setZIndex(1);

		this.sinkEvents(Event.ONCLICK);
		this.addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
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
		MathMLBindingNode root = mathMLBindingTree.getRoot();

		draw(root, null);

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

	}

	/**
	 * Replicates the equation graphic for each equation layer (node) to be
	 * displayed when this layer is in focus
	 */
	public void draw(MathMLBindingNode node, EquationLayer parentLayer) {

		EquationLayer eqLayer = new EquationLayer();

		Element rootClone = mathMLBindingTree.getEqHTMLClone();
		eqLayer.getElement().appendChild(rootClone);

		AbsolutePanel menuPanel = eqLayer.getContextMenuPanel();
		menuPanel.getElement().setAttribute("id", "menuLayer-" + node.getId());
		menuPanel.addStyleName("fillParent");
		this.add(menuPanel, 0, 0);

		eqLayer.setParentLayer(parentLayer);
		eqLayerMap.put(node, eqLayer);
		eqLayer.getElement().setAttribute("id", "eqLayer-" + node.getId());
		eqLayer.addStyleName("fillParent");
		this.add(eqLayer, 0, 0);

		replaceChildsId(rootClone, node.getId());
		resizeEquation(rootClone);
		matchChildHeights(rootClone);

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

		for (MathMLBindingNode node : childNodes) {

			com.google.gwt.user.client.Element layerNode = DOM
					.getElementById(node.getId() + "-ofLayer-"
							+ parentNode.getId());

			Wrapper wrap;
			VerticalPanel menu = null;
			if (inEditMode) {// Edit Mode////////////////////////////
				wrap = new EditWrapper(node, this, eqLayerMap.get(node),
						layerNode);
				menu = ((EditWrapper) wrap).getEditMenu();

			} else {// Solver Mode////////////////////////////////////
				wrap = new MLElementWrapper(node, this, eqLayerMap.get(node),
						layerNode);
				menu = ((MLElementWrapper) wrap).getContextMenu();
			}

			eqLayer.addWrapper(wrap);

			eqLayer.ContextMenuPanel.add(
					menu,
					wrap.getAbsoluteLeft() - this.getAbsoluteLeft(),
					wrap.getAbsoluteTop() - this.getAbsoluteTop()
							+ wrap.getOffsetHeight());

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
	private void replaceChildsId(Element curEl, String layerId) {

		// Element curEl = (Element) (parent.getChild(i));
		String oldId = curEl.getId();

		// Each wrapper has a reference to its MathNode and Layer
		// Wrapper-[equation id]-ofLayer-[MathML node id]
		// example: Wrapper-ML1-ofLayer-ML1
		if (oldId != null) {
			if (oldId.contains("ML")) {
				curEl.setAttribute("id", oldId + "-ofLayer-" + layerId);
			} else if (oldId.contains("Root")) {
				curEl.setAttribute("id", "Root-ofLayer-" + layerId);
			}
		}

		if (curEl.getChildCount() > 0) {
			for (int i = 0; i < curEl.getChildCount(); i++) {
				if (Node.ELEMENT_NODE == curEl.getChild(i).getNodeType()) {
					replaceChildsId((Element) curEl.getChild(i), layerId);
				}
			}
		}
	}

	/**
	 * Resizes the equation to fill the panel
	 * 
	 * @param el
	 */
	private void resizeEquation(Element el) {

		double widthRatio = (double) this.getOffsetWidth()
				/ el.getOffsetWidth();
		double heightRatio = (double) this.getOffsetHeight()
				/ el.getOffsetHeight();

		double smallerRatio = (widthRatio > heightRatio) ? heightRatio
				: widthRatio;

		el.getStyle().setFontSize((smallerRatio * 100), Unit.PCT);
	}

	/**
	 * Matches the heights of all the children of an {@link Type.Equation},
	 * {@link Type.Term} or {@link Type.Sum} by:<br/>
	 * 1.Lifting centers to the tallest denominator using padding-bottom<br/>
	 * 2.Matching tops to tallest height with padding-top<br/>
	 * <b>Note:</b> All children of these nodes are initially aligned at their
	 * baseline
	 */
	private void matchChildHeights(Element curEl) {

		if (curEl.getChildCount() > 0) {
			for (int i = 0; i < curEl.getChildCount(); i++) {
				if (Node.ELEMENT_NODE == curEl.getChild(i).getNodeType()) {
					matchChildHeights((Element) curEl.getChild(i));
				}
			}
		}
		if (curEl.getClassName().contains(Type.Equation.toString())
				|| curEl.getClassName().contains(Type.Term.toString())
				|| curEl.getClassName().contains(Type.Sum.toString())) {

			NodeList<Node> children = curEl.getChildNodes();

			int tallestDenom = 0;
			int shortestChild = 999999999;
			for (int i = 0; i < children.getLength(); i++) {
				Element child = ((Element) children.getItem(i));
				// Find the tallest denominator to match centers
				if (child.getClassName().contains(Type.Fraction.toString())) {
					int denomHeight = ((Element) child.getChild(1))
							.getOffsetHeight();
					if (denomHeight > tallestDenom) {
						tallestDenom = denomHeight;
					}
				} else {
					// Find the tallest non-fraction to center lift
					int childHeight = child.getOffsetHeight();
					if (childHeight < shortestChild) {
						shortestChild = childHeight;
					}
				}

			}

			// Lift every child to the center of the tallest denominator
			if (tallestDenom != 0) {
				for (int i = 0; i < children.getLength(); i++) {
					Element child = ((Element) children.getItem(i));
					int lift = tallestDenom;
					if (child.getClassName().contains(Type.Fraction.toString())) {
						lift -= (((Element) child.getChild(1))
								.getOffsetHeight());
					} else {
						lift -= (shortestChild / 2);
					}
					child.getStyle().setBottom(lift, Unit.PX);
					child.getStyle().setPaddingBottom(lift, Unit.PX);
				}
			}

			// Find highest top to match heights to
			int highestTop = 999999999;
			for (int i = 0; i < children.getLength(); i++) {
				Element child = ((Element) children.getItem(i));
				int childTop = child.getAbsoluteTop();
				if (childTop < highestTop) {
					highestTop = childTop;
				}
			}

			// Set all children padding to match height
			for (int i = 0; i < children.getLength(); i++) {
				Element child = ((Element) children.getItem(i));
				int childTop = child.getAbsoluteTop();
				child.getStyle()
						.setPaddingTop((childTop - highestTop), Unit.PX);

			}
		}
	}
}