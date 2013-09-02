package com.sciencegadgets.client.algebra;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Duration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.algebra.edit.EditWrapper;

public class EquationPanel extends AbsolutePanel {
	private HashMap<MathMLBindingNode, EquationLayer> eqLayerMap = new HashMap<MathMLBindingNode, EquationLayer>();

	MathMLBindingTree mathMLBindingTree;
	// private HTML pilot = new HTML();
	private boolean inEditMode;
	private EquationLayer rootLayer;
	private static EquationLayer focusLayer;
	public static Wrapper selectedWrapper;
	// Width of equation compared to panel

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

//		this.sinkEvents(Event.ONTOUCHSTART);
//		this.addHandler(new TouchStartHandler() {
//			@Override
//			public void onTouchStart(TouchStartEvent event) {
//				setFocusOut();
//			}
//		}, TouchStartEvent.getType());

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

		EquationLayer eqLayer = new EquationLayer(node);

		AbsolutePanel menuPanel = eqLayer.getContextMenuPanel();
		menuPanel.getElement().setAttribute("id", "menuLayer-" + node.getId());
		menuPanel.addStyleName("fillParent");
		this.add(menuPanel, 0, 0);

		eqLayer.setParentLayer(parentLayer);
		eqLayerMap.put(node, eqLayer);
		eqLayer.getElement().setAttribute("id", "eqLayer-" + node.getId());
		eqLayer.addStyleName("fillParent");
		this.add(eqLayer, 0, 0);

//		Element rootClone = mathMLBindingTree.getEqHTMLClone();
//		eqLayer.getElement().appendChild(rootClone);
//		replaceChildsId(rootClone, node.getId());
//		resizeEquation(rootClone);
//		matchChildHeights(rootClone);

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

		Animation fade = new LayerFade(newFocus, prevFocus);
		fade.run(300, Duration.currentTimeMillis() - 100);

		focusLayer = newFocus;
		Moderator.focusLayerId = focusLayer.getElement().getAttribute("id");
	}
	class LayerFade extends Animation{
		EquationLayer newFocus, prevFocus;
		
		LayerFade(EquationLayer newFocus, EquationLayer prevFocus){
			this.newFocus = newFocus;
			this.prevFocus = prevFocus;
		}
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
	}

	private void placeNextEqWrappers(MathMLBindingNode parentNode) {

		LinkedList<MathMLBindingNode> childNodes = parentNode.getChildren();
		EquationLayer eqLayer = eqLayerMap.get(parentNode);

		for (MathMLBindingNode node : childNodes) {

			com.google.gwt.user.client.Element layerNode = DOM
					.getElementById(node.getId() + "-ofLayer-"
							+ parentNode.getId());

			Wrapper wrap;
//			VerticalPanel menu = null;
			if (inEditMode) {// Edit Mode////////////////////////////
				wrap = new EditWrapper(node, this, eqLayerMap.get(node),
						layerNode);
//				menu = ((EditWrapper) wrap).getEditMenu();

			} else {// Solver Mode////////////////////////////////////
				wrap = new MLElementWrapper(node, this, eqLayerMap.get(node),
						layerNode);
//				menu = ((MLElementWrapper) wrap).getContextMenu();
			}

			eqLayer.addWrapper(wrap);

//			eqLayer.ContextMenuPanel.add(
//					menu,
//					wrap.getAbsoluteLeft() - this.getAbsoluteLeft(),
//					wrap.getAbsoluteTop() - this.getAbsoluteTop()
//							+ wrap.getOffsetHeight());

			if (node.getType().hasChildren()) {
				placeNextEqWrappers(node);
			}
		}
	}

}