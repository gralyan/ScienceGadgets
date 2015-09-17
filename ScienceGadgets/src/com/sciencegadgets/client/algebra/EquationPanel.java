/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.algebra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.EditWrapper;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.TypeSGET;

public class EquationPanel extends AbsolutePanel {
	private HashMap<EquationNode, EquationLayer> eqLayerMap = new HashMap<EquationNode, EquationLayer>();

	private EquationTree equationTree;
	private EquationLayer rootLayer;
	private EquationLayer focusLayer;
	Wrapper selectedWrapper;
	public AutoSelectWrapper autoSelectedWrapper;
	private ArrayList<EquationNode> mergeRootNodes = new ArrayList<EquationNode>();
	private ArrayList<EquationNode> mergeFractionNodes = new ArrayList<EquationNode>();
	private ArrayList<EquationWrapper> mathWrappers = new ArrayList<EquationWrapper>();
	private AlgebraActivity algebraActivity;
	private ZoomAnimation zoomAnimation = new ZoomAnimation();

	public static final String EQ_OF_LAYER = "Equation-ofLayer-";
	public static final String EQ_LAYER = "eqLayer-";
	public static final String OF_LAYER = "-ofLayer-";

	private EquationNode rootNode;

	private EquationLayer modelEqLayer;

	// Width of equation compared to panel

	public EquationPanel(AlgebraActivity algebraActivity) {

		this.algebraActivity = algebraActivity;
		this.equationTree = algebraActivity.getEquationTree();

		setStyleName(CSS.EQ_PANEL);
		// zIndex eqPanel=1 wrapper=2 menu=3
		this.getElement().getStyle().setZIndex(1);

		if (algebraActivity.isInEditMode()) {
			addStyleName(CSS.EDIT_MODE);
		}

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
		rootNode = equationTree.getRoot();

		boolean isStacked = algebraActivity.getActivityType() == ActivityType.simplifyquiz;
		EquationHTML displayClone = equationTree.getDisplayClone(isStacked);
		modelEqLayer = new EquationLayer(null, displayClone);
		this.add(modelEqLayer);

		if (!algebraActivity.isInEditMode()) {
			findRootLayerMergingNodes(rootNode);
			findFractionMergingNodes();
		}

		draw(rootNode, null);

		modelEqLayer.removeFromParent();

		if (!algebraActivity.isInEditMode()) {
			// Seperately place into root layer, skipped in draw()
			for (EquationNode merge : mergeRootNodes) {
				placeNextEqWrappers(merge, rootLayer);
			}
			for (EquationNode merge : mergeFractionNodes) {
				placeNextEqWrappers(merge, eqLayerMap.get(merge.getParent()));
			}
			// for (EquationWrapper wrap : mathWrappers) {
			// new InterFractionTransformations(wrap.getNode());
			// }
		}

		for (EquationWrapper wrap : mathWrappers) {
			wrap.addAssociativeDragDrop();
		}
		for (EquationLayer eqLayer : eqLayerMap.values()) {
			eqLayer.getEqHTML().resizeEquation();
			eqLayer.setVisible(false);
		}

		// setFocus(algebraActivity.focusLayerId);
		setFocus("");
	}

	@Override
	protected void onUnload() {
		unselectCurrentSelection();
		super.onUnload();
	}

	public Wrapper getSelectedWrapper() {
		return selectedWrapper;
	}

	public AlgebraActivity getAlgebraActivity() {
		return algebraActivity;
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
					if (TypeSGET.Term.equals(inFrac.getType())) {
						mergeRootNodes.add(inFrac);
					}
				}
				break;
			case Term:
			case Sum:
				mergeRootNodes.add(side);
				break;
			default:
				break;
			}
		}
	}

	private void findFractionMergingNodes() {
		LinkedList<EquationNode> fractions = equationTree
				.getNodesByType(TypeSGET.Fraction);

		for (EquationNode frac : fractions) {
			for (EquationNode child : frac.getChildren()) {
				// numerator and denominator
				if (TypeSGET.Term.equals(child.getType())
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
		if (!algebraActivity.isInEditMode()
				&& mergeFractionNodes.contains(node)) {
			eqLayer = parentLayer;

		} else if (!algebraActivity.isInEditMode()
				&& mergeRootNodes.contains(node)) {
			eqLayer = rootLayer;

		} else {
			eqLayer = modelEqLayer.clone(node, algebraActivity.isInEditMode());

			eqLayer.setParentLayer(parentLayer);
			eqLayerMap.put(node, eqLayer);
			eqLayer.getElement().setAttribute("id", EQ_LAYER + node.getId());

			if (parentLayer == null) {
				rootLayer = eqLayer;
			}

			this.add(eqLayer, 0, 0);
			placeNextEqWrappers(node, eqLayer);
		}

		for (EquationNode childNode : node.getChildren()) {

			if (childNode.hasChildElements()) {
				draw(childNode, eqLayer);
			}
		}
	}

	private void placeNextEqWrappers(EquationNode parentNode,
			EquationLayer eqLayer) {

		LinkedList<EquationNode> childNodes = parentNode.getChildren();

		if (parentNode.equals(equationTree.getRoot())) {
			childNodes.remove(1);// "=" sign
		}

		if (mergeRootNodes.contains(parentNode)) {
			parentNode = rootNode;
		} else if (mergeFractionNodes.contains(parentNode)) {
			parentNode = parentNode.getParent();
		}

		// Decorate parent expression of layer
		String parentId = parentNode.getId();

		Element layerParentNode = DOM.getElementById(EQ_OF_LAYER + parentId);
		if (algebraActivity.isInEditMode()
				&& !TypeSGET.Equation.equals(parentNode.getType())) {
			layerParentNode = DOM
					.getElementById(parentId + OF_LAYER + parentId);
		}
		if (layerParentNode != null) {
			String typeName = parentNode.getType().toString();
			layerParentNode.addClassName(typeName);
			layerParentNode.addClassName(CSS.PARENT_WRAPPER);
		} else {
			JSNICalls.error("Can't find parent expression"
					+ " while placing wrappers " + parentNode);
		}

		for (EquationNode node : childNodes) {

			if (!algebraActivity.isInEditMode()) {
				if (mergeRootNodes.contains(node)
						|| mergeFractionNodes.contains(node)) {
					continue;
				}
			} else if (!node.hasWrapper) {
				continue;
			}

			// Collect selectable children for autoselect
			LinkedList<EquationNode> selectableChildren = node.getChildren();
			if (!algebraActivity.isInEditMode()
					&& TypeSGET.Fraction.equals(node.getType())
					&& selectableChildren.size() == 2) {
				EquationNode numerator = selectableChildren.get(0);
				EquationNode denominator = selectableChildren.get(1);
				if (TypeSGET.Term.equals(numerator.getType())) {
					for (EquationNode fracTermChild : numerator.getChildren()) {
						selectableChildren.add(fracTermChild);
					}
					selectableChildren.remove(numerator);
				}
				if (TypeSGET.Term.equals(denominator.getType())) {
					for (EquationNode fracTermChild : denominator.getChildren()) {
						selectableChildren.add(fracTermChild);
					}
					selectableChildren.remove(denominator);
				}
			}
			// Wrap selectable children for autoselect
			for (EquationNode subChild : selectableChildren) {
				Element autoSelectLayerNode = DOM.getElementById(subChild
						.getId() + OF_LAYER + parentId);
				new AutoSelectWrapper(this, subChild, autoSelectLayerNode);
			}

			// Wrap children expressions of layer
			Element layerNode = DOM.getElementById(node.getId() + OF_LAYER
					+ parentId);

			if (algebraActivity.isInEditMode()) {// Edit Mode
				EditWrapper wrap = new EditWrapper(node, algebraActivity,
						layerNode);
				eqLayer.addWrapper(wrap);
				mathWrappers.add(wrap);
			} else {// Solver Mode
				AlgebaWrapper wrap = new AlgebaWrapper(node, algebraActivity,
						layerNode);
				eqLayer.addWrapper(wrap);
				mathWrappers.add(wrap);
			}
		}
	}

	public EquationLayer getFocus() {
		return focusLayer;
	}

	void setFocusOut() {
		Wrapper autoSelect = null;

		EquationLayer parentLayer = focusLayer.getParentLayer();
		if (parentLayer == null) {
			unselectCurrentSelection();
			algebraActivity.revertUpperMidAreaToDefault();
			return;
		}

		autoSelect = parentLayer.getWapperForLayer(focusLayer);

		// Moderator.SOUNDS.WRAPPER_ZOOM_OUT.play();

		if (autoSelect != null
				&& parentLayer.getWrappers().contains(autoSelect)) {
			autoSelect.select();
		}
		setFocus(parentLayer);
	}

	void setFocus(EquationNode node) {
		setFocus(eqLayerMap.get(node));
	}

	public EquationLayer setFocus(String layerId) {
		if (layerId != null && !"".equals(layerId)) {
			for (EquationLayer eqLayer : eqLayerMap.values()) {
				if (layerId.equals(eqLayer.getElement().getAttribute("id"))) {
					setFocus(eqLayer);
					return eqLayer;
				}
			}
		}
		// If no layer found, focus on root
		setFocus(eqLayerMap.get(rootNode));
		return null;
	}

	void setFocus(final EquationLayer newFocus) {
		EquationLayer prevFocus = focusLayer;
		Wrapper prevSelection = selectedWrapper;

		if (!algebraActivity.isInEditMode() && newFocus != null
				&& prevFocus != null) {
			if (zoomAnimation.isRunning()) {
				zoomAnimation.cancel();
			}
			if (prevFocus.equals(newFocus.getParentLayer())) {
				int dx = prevSelection.getAbsoluteLeft()
						- newFocus.getEqHTML().getAbsoluteLeft();
				int dy = prevSelection.getAbsoluteTop()
						- newFocus.getEqHTML().getAbsoluteTop();
				zoomAnimation.setSpecs(prevFocus, newFocus, dx, dy, true);
				zoomAnimation.run(500);
			} else if (newFocus.equals(prevFocus.getParentLayer())) {
				Element prevEl = DOM.getElementById(prevFocus.layerId
						+ OF_LAYER + newFocus.layerId);
				int dx = prevEl.getAbsoluteLeft()
						- prevFocus.getEqHTML().getAbsoluteLeft();
				int dy = prevEl.getAbsoluteTop()
						- prevFocus.getEqHTML().getAbsoluteTop();
				zoomAnimation.setSpecs(newFocus, prevFocus, dx, dy, false);
				zoomAnimation.run(500);
			}
		} else {
			if (prevFocus != null) {
				prevFocus.setVisible(false);
			}
			newFocus.setVisible(true);
		}

		focusLayer = newFocus;

		if (selectedWrapper != null
				&& !newFocus.getWrappers().contains(selectedWrapper)) {
			selectedWrapper.unselect();
		}

		if (autoSelectedWrapper != null) {
			Wrapper wrapperToSelect = autoSelectedWrapper.getWrapper();
			autoSelectedWrapper = null;
			if (focusLayer.getWrappers().contains(wrapperToSelect)) {
				wrapperToSelect.select();
			}
		}

		if (!algebraActivity.isInEditMode()) {
			Scheduler.get().scheduleIncremental(
					new PrepareWrappersInLayer(newFocus.getWrappers()));
		}
	}

	public Wrapper unselectCurrentSelection() {
		Wrapper prevSelected = selectedWrapper;
		if (selectedWrapper != null) {
			selectedWrapper.unselect();
		}
		return prevSelected;
	}

	private class PrepareWrappersInLayer implements RepeatingCommand {
		LinkedList<Wrapper> wrappers;
		int wrapperCount = 0;
		int currentIndex = 0;

		PrepareWrappersInLayer(LinkedList<Wrapper> wrappers) {
			this.wrappers = wrappers;
			this.wrapperCount = wrappers.size();
		}

		@Override
		public boolean execute() {
			if (currentIndex >= wrapperCount) {
				return false;
			}
			((AlgebaWrapper) wrappers.get(currentIndex)).attachButtons();
			currentIndex++;
			return true;
		}

	}

	public void zoomToAndSelect(String nodeIdToSelect) {

		if (nodeIdToSelect == null) {
			return;
		} else if ("".equals(nodeIdToSelect)
				|| !equationTree.containsId(nodeIdToSelect)) {
			JSNICalls.warn("Can't get nodeToSelect: " + nodeIdToSelect);
			return;
		}

		zoomToAndSelect(equationTree.getNodeById(nodeIdToSelect));

	}

	public void zoomToAndSelect(EquationNode nodeToSelect) {
		Wrapper wrap = nodeToSelect.getWrapper();
		if (wrap == null) {
			JSNICalls.warn("Can't zoomToAndSelect wrapper: \n" + wrap);
			return;
		}
		EquationLayer eqLayer = wrap.getLayer();
		if (eqLayer == null) {
			JSNICalls.warn("Can't zoomToAndSelect layer: \n" + eqLayer);
			return;
		}
		if (getChildren().contains(eqLayer)) {
			setFocus(eqLayer);
			wrap.select();
		} else {
			JSNICalls
					.warn("Layer to zoom to is not in panel, can't autoselect: \n"
							+ eqLayer);
		}
	}
}
