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
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.edit.EditWrapper;

public class EquationPanel extends AbsolutePanel {
	public HashMap<MathNode, EquationLayer> eqLayerMap = new HashMap<MathNode, EquationLayer>();

	MathTree mathMLBindingTree;
	private boolean inEditMode;
	private EquationLayer rootLayer;
	private static EquationLayer focusLayer;
	public static Wrapper selectedWrapper;
	private LinkedList<MathNode> mergeRootNodes = new LinkedList<MathNode>();

	private MathNode rootNode;

	// Width of equation compared to panel

	public EquationPanel(MathTree mathTree, boolean inEditMode) {

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

	}

	@Override
	protected void onLoad() {
		super.onLoad();
		rootNode = mathMLBindingTree.getRoot();

		if (!inEditMode) {
			findRootLayerMergingNodes(rootNode);
		}
		draw(rootNode, null);

		if (!inEditMode) {
			for (MathNode merge : mergeRootNodes) {
				placeNextEqWrappers(merge, rootLayer);
			}
		}

		for (EquationLayer eqLayer : eqLayerMap.values()) {
			eqLayer.setVisible(false);
		}

		// Initialize focus to previous focus before reload
		focusLayer = setFocus(Moderator.focusLayerId);
		if (focusLayer == null) {
			focusLayer = eqLayerMap.get(rootNode);
		}
		focusLayer.setVisible(true);

	}

	private void findRootLayerMergingNodes(MathNode root) {
		LinkedList<MathNode> sideEqSide = root.getChildren();

		for (MathNode side : sideEqSide) {
			switch (side.getType()) {
			case Fraction:
				mergeRootNodes.add(side);
				for (MathNode inFrac : side.getChildren()) {
					if (Type.Term.equals(inFrac.getType())) {
						mergeRootNodes.add(inFrac);
					}
				}
				break;
			case Term:
			case Sum:
				mergeRootNodes.add(side);
				break;
			}
		}
	}

	/**
	 * Replicates the equation graphic for each equation layer (node) to be
	 * displayed when this layer is in focus
	 */
	private void draw(MathNode node, EquationLayer parentLayer) {

		EquationLayer eqLayer;
		if (!mergeRootNodes.contains(node) || inEditMode) {
			eqLayer = new EquationLayer(node);

			AbsolutePanel menuPanel = eqLayer.getContextMenuPanel();
			menuPanel.getElement().setAttribute("id",
					"menuLayer-" + node.getId());
			menuPanel.addStyleName("fillParent");
			this.add(menuPanel, 0, 0);

			eqLayer.setParentLayer(parentLayer);
			eqLayerMap.put(node, eqLayer);
			eqLayer.getElement().setAttribute("id", "eqLayer-" + node.getId());
			eqLayer.addStyleName("fillParent");
			this.add(eqLayer, 0, 0);

			if (parentLayer == null) {
				rootLayer = eqLayer;
			}

			placeNextEqWrappers(node, eqLayer);

		} else {
			eqLayer = rootLayer;
		}
		for (MathNode childNode : node.getChildren()) {

			if (childNode.getType().hasChildren()) {
				draw(childNode, eqLayer);
			}
		}
	}

	private void placeNextEqWrappers(MathNode parentNode, EquationLayer eqLayer) {

		LinkedList<MathNode> childNodes = parentNode.getChildren();
		// EquationLayer eqLayer = eqLayerMap.get(parentNode);

		for (MathNode node : childNodes) {
			if(!inEditMode){
			if (mergeRootNodes.contains(node)) {
				continue;
			}
			if (mergeRootNodes.contains(parentNode)) {
				parentNode = rootNode;
			}}
			com.google.gwt.user.client.Element layerNode = DOM
					.getElementById(node.getId() + "-ofLayer-"
							+ parentNode.getId());

			Wrapper wrap;
			// VerticalPanel menu = null;
			if (inEditMode) {// Edit Mode////////////////////////////
				wrap = new EditWrapper(node, this, layerNode);
				// menu = ((EditWrapper) wrap).getEditMenu();

			} else {// Solver Mode////////////////////////////////////
				wrap = new MathWrapper(node, this, layerNode);
				// menu = ((MLElementWrapper) wrap).getContextMenu();
			}

			eqLayer.addWrapper(wrap);

			// eqLayer.ContextMenuPanel.add(
			// menu,
			// wrap.getAbsoluteLeft() - this.getAbsoluteLeft(),
			// wrap.getAbsoluteTop() - this.getAbsoluteTop()
			// + wrap.getOffsetHeight());

		}
	}

	public EquationLayer getFocus() {
		return focusLayer;
	}

	void setFocusOut() {
		if (inEditMode)
			Moderator.changeNodeMenu.setVisible(false);

		EquationLayer parentLayer = focusLayer.getParentLayer();
		if (parentLayer != null)
			setFocus(parentLayer);
	}

	EquationLayer setFocus(String layerId) {
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

	void setFocus(final EquationLayer newFocus) {
		final EquationLayer prevFocus = focusLayer;
		// if (selectedWrapper != null) {
		// selectedWrapper.unselect(inEditMode);
		// }

		newFocus.setOpacity(0);
		newFocus.setVisible(true);

		Animation fade = new LayerFade(newFocus, prevFocus);
		fade.run(300, Duration.currentTimeMillis() - 100);

		focusLayer = newFocus;
		Moderator.focusLayerId = focusLayer.getElement().getAttribute("id");

	}

	class LayerFade extends Animation {
		EquationLayer newFocus, prevFocus;

		LayerFade(EquationLayer newFocus, EquationLayer prevFocus) {
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

}