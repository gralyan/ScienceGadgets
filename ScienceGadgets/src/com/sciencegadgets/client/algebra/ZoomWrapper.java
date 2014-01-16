package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.UnitUtil;

public class ZoomWrapper extends Wrapper {
	protected EquationPanel eqPanel;
	protected AlgebraActivity algebraActivity;

	public ZoomWrapper(MathNode node, AlgebraActivity algebraActivity,
			Element element) {
		super(node, algebraActivity.eqPanel, element);
		this.eqPanel = algebraActivity.eqPanel;
		this.algebraActivity = algebraActivity;
	}

	public EquationLayer getEqLayer() {
		return eqPanel.eqLayerMap.get(node);
	}

	public EquationPanel getEqPanel() {
		return eqPanel;
	}
	private void fillSelectionDetails() {
		FlowPanel details = algebraActivity.selectionDetails;
		
		TypeML type = node.getType();
		Label typeLabel = new Label(type.toString());
		typeLabel.addStyleName(type.toString());
		typeLabel.addStyleName("displayWrapper");
		details.add(typeLabel);
		
			switch (type) {
			case Number:
				String fullValue = node.getAttribute(MathAttribute.Value);
				HTML quantity = new HTML(fullValue);

				String unit = node.getAttribute(MathAttribute.Unit);
				if(!"".equals(unit)) {
					quantity.getElement().appendChild(UnitUtil.element_From_attribute(unit, null , false));
				}
				
				details.add(quantity);
				
				break;
			case Variable:
				String qKind = node.getAttribute(MathAttribute.Unit);
				if(!"".equals(qKind)) {
					details.add(new Label(qKind));
				}
				break;
			}
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

			fillSelectionDetails();

			super.select();
		}
	}

	@Override
	public void unselect() {
		super.unselect();
		algebraActivity.selectionDetails.clear();
	}
}
