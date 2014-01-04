package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Element;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

public class ZoomWrapper extends Wrapper {
	protected EquationPanel eqPanel;

	public ZoomWrapper(MathNode node, EquationPanel eqPanel, Element element) {
		super(node, eqPanel, element);
		this.eqPanel = eqPanel;
	}

	public EquationLayer getEqLayer() {
		return eqPanel.eqLayerMap.get(node);
	}

	public EquationPanel getEqPanel() {
		return eqPanel;
	}
	@Override
	public void select() {

		if (this.equals(EquationPanel.selectedWrapper)) {

			// If this was already selected, focus in on it
			if (node.hasChildElements()) {
				unselect();
				eqPanel.setFocus(getEqLayer());
			}
		} else {

			// If there is another selection, unselect it
			if (EquationPanel.selectedWrapper != null) {
				EquationPanel.selectedWrapper.unselect();
			}

//			AlgebraActivity.contextMenuArea.add(menu);

			super.select();
		}
	}
	
	@Override
	public void unselect() {
		super.unselect();
	}
}
