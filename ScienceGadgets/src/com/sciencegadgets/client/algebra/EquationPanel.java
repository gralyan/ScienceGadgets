package com.sciencegadgets.client.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Duration;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.EditWrapper;
import com.sciencegadgets.client.algebra.transformations.AlgebraicTransformations;
import com.sciencegadgets.shared.TypeEquationXML;

public class EquationPanel extends AbsolutePanel {
	public HashMap<EquationNode, EquationLayer> eqLayerMap = new HashMap<EquationNode, EquationLayer>();

	private EquationTree mathTree;
	private EquationLayer rootLayer;
	private static EquationLayer focusLayer;
	public Wrapper selectedWrapper;
	private ArrayList<EquationNode> mergeRootNodes = new ArrayList<EquationNode>();
	private ArrayList<EquationNode> mergeFractionNodes = new ArrayList<EquationNode>();
	private ArrayList<EquationWrapper> mathWrappers = new ArrayList<EquationWrapper>();
	private AlgebraActivity algebraActivity;

	public static final String EQ_OF_LAYER = "Equation-ofLayer-";
	public static final String OF_LAYER = "-ofLayer-";

	private EquationNode rootNode;

	private EquationLayer modelEqLayer;

	// Width of equation compared to panel

	public EquationPanel(AlgebraActivity algebraActivity) {

		this.algebraActivity = algebraActivity;
		this.mathTree = algebraActivity.getEquationTree();

		setStyleName(CSS.EQ_PANEL);
		// zIndex eqPanel=1 wrapper=2 menu=3
		this.getElement().getStyle().setZIndex(1);

		if (Moderator.isTouch) {
			this.addDomHandler(new TouchEndHandler() {
				@Override
				public void onTouchEnd(TouchEndEvent event) {
					if (OptionsHandler.optionsPopup.isShowing()) {
						event.preventDefault();
						event.stopPropagation();
						OptionsHandler.optionsPopup.hide();
					} else {
						setFocusOut();
					}
				}
			}, TouchEndEvent.getType());
		} else {
			this.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (OptionsHandler.optionsPopup.isShowing()) {
						event.preventDefault();
						event.stopPropagation();
						OptionsHandler.optionsPopup.hide();
					} else {
						setFocusOut();
					}
				}
			}, ClickEvent.getType());
		}

	}

	@Override
	protected void onLoad() {
		super.onLoad();
		rootNode = mathTree.getRoot();
		
		modelEqLayer = new EquationLayer(null, mathTree.getDisplayClone());
		this.add(modelEqLayer);

		if (!algebraActivity.inEditMode) {
			findRootLayerMergingNodes(rootNode);
			findFractionMergingNodes();
		}
		draw(rootNode, null);

		
		modelEqLayer.removeFromParent();

		if (!algebraActivity.inEditMode) {
			// Seperately place into root layer, skipped in draw()
			for (EquationNode merge : mergeRootNodes) {
				placeNextEqWrappers(merge, rootLayer);
			}
			for (EquationNode merge : mergeFractionNodes) {
				placeNextEqWrappers(merge, eqLayerMap.get(merge.getParent()));
			}
			for (EquationWrapper wrap : mathWrappers) {
				AlgebraicTransformations.interFractionDrop_check(wrap.getNode());
			}
		}
		for (EquationWrapper wrap : mathWrappers) {
			wrap.addAssociativeDragDrop();
		}

		for (EquationLayer eqLayer : eqLayerMap.values()) {
			eqLayer.setVisible(false);
		}

		// Initialize focus to previous focus before reload
		focusLayer = setFocus(algebraActivity.focusLayerId);
		if (focusLayer == null) {
			focusLayer = eqLayerMap.get(rootNode);
		}
		focusLayer.setVisible(true);

	}

	/**
	 * Finds unnecessary layers to merge. No need for top level sums or terms,
	 * or sums and terms in fractions
	 * 
	 * @param root
	 */
	private void findRootLayerMergingNodes(EquationNode root) {
		LinkedList<EquationNode> sideEqSide = root.getChildren();

		for (EquationNode side : sideEqSide) {
			switch (side.getType()) {
			case Fraction:
				mergeRootNodes.add(side);
				for (EquationNode inFrac : side.getChildren()) {
					if (TypeEquationXML.Term.equals(inFrac.getType())) {
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

	private void findFractionMergingNodes() {
		ArrayList<EquationNode> fractions = mathTree
				.getNodesByType(TypeEquationXML.Fraction);

		for (EquationNode frac : fractions) {
			for (EquationNode child : frac.getChildren()) {
				// numerator and denominator
				if (TypeEquationXML.Term.equals(child.getType())
						&& !mergeRootNodes.contains(child)) {
					mergeFractionNodes.add(child);
				}
			}
		}
	}

	/**
	 * Replicates the equation graphic for each equation layer (node) to be
	 * displayed when this layer is in focus
	 */
	private void draw(EquationNode node, EquationLayer parentLayer) {

		EquationLayer eqLayer;
		if (!algebraActivity.inEditMode && mergeFractionNodes.contains(node)) {
			eqLayer = parentLayer;

		} else if (!algebraActivity.inEditMode && mergeRootNodes.contains(node)) {
			eqLayer = rootLayer;

		} else {
//			eqLayer = new EquationLayer(node);
			eqLayer = modelEqLayer.clone(node);

//			AbsolutePanel menuPanel = eqLayer.getContextMenuPanel();
//			menuPanel.getElement().setAttribute("id",
//					"menuLayer-" + node.getId());
//			menuPanel.addStyleName(CSS.FILL_PARENT);
//			this.add(menuPanel, 0, 0);

			eqLayer.setParentLayer(parentLayer);
			eqLayerMap.put(node, eqLayer);
			eqLayer.getElement().setAttribute("id", "eqLayer-" + node.getId());
			eqLayer.addStyleName(CSS.INTERACTIVE_EQUATION);
			this.add(eqLayer, 0, 0);

			if (parentLayer == null) {
				rootLayer = eqLayer;
			}

			placeNextEqWrappers(node, eqLayer);
		}

		for (EquationNode childNode : node.getChildren()) {

			if (childNode.hasChildElements()) {
				draw(childNode, eqLayer);
			}
		}
	}

	private void placeNextEqWrappers(EquationNode parentNode, EquationLayer eqLayer) {

		LinkedList<EquationNode> childNodes = parentNode.getChildren();
			if(parentNode.equals(mathTree.getRoot())) {
				childNodes.remove(1);
			}
			
			if (mergeRootNodes.contains(parentNode)) {
				parentNode = rootNode;
			} else if (mergeFractionNodes.contains(parentNode)) {
				parentNode = parentNode.getParent();
			}
			
			String parentId = parentNode.getId();
			
			com.google.gwt.user.client.Element layerParentNode = DOM
					.getElementById(parentId + OF_LAYER
							+ parentId);
			if(layerParentNode != null) {
				layerParentNode.addClassName(CSS.DISPLAY_WRAPPER);
			}else if(TypeEquationXML.Equation.equals(parentNode.getType())){
				com.google.gwt.user.client.Element layerEqNode = DOM
						.getElementById(EQ_OF_LAYER	+ parentId);
				layerEqNode.addClassName(CSS.DISPLAY_WRAPPER);
			}
				
		for (EquationNode node : childNodes) {
			
			if (!algebraActivity.inEditMode) {
				if (mergeRootNodes.contains(node)
						|| mergeFractionNodes.contains(node)) {
					continue;
				}
			}
			com.google.gwt.user.client.Element layerNode = DOM
					.getElementById(node.getId() + OF_LAYER
							+ parentId);

			if (algebraActivity.inEditMode) {// Edit Mode
				EditWrapper wrap = new EditWrapper(node, algebraActivity, layerNode);
				eqLayer.addWrapper(wrap);
				mathWrappers.add(wrap);
			} else {// Solver Mode
				AlgebaWrapper wrap = new AlgebaWrapper(node, algebraActivity, layerNode);
				eqLayer.addWrapper(wrap);
				mathWrappers.add(wrap);
			}

		}
	}

	public EquationLayer getFocus() {
		return focusLayer;
	}

	void setFocusOut() {
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
		if (selectedWrapper != null) {
			if (selectedWrapper instanceof EditWrapper) {
				((EditWrapper) selectedWrapper).unselect();
			} else if (selectedWrapper instanceof AlgebaWrapper) {
				((AlgebaWrapper) selectedWrapper).unselect();
			}
		}

		newFocus.setOpacity(0);
		newFocus.setVisible(true);

		Animation fade = new LayerFade(newFocus, prevFocus);
		fade.run(300, Duration.currentTimeMillis() - 100);

		focusLayer = newFocus;
		algebraActivity.focusLayerId = focusLayer.getElement().getAttribute(
				"id");

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