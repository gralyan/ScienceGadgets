package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.UnitUtil;

public class EquationWrapper extends Wrapper {
	protected EquationPanel eqPanel;
	protected AlgebraActivity algebraActivity;

	public EquationWrapper(MathNode node, AlgebraActivity algebraActivity,
			Element element) {
		super(node, algebraActivity.eqPanel, element);
		this.eqPanel = algebraActivity.eqPanel;
		this.algebraActivity = algebraActivity;
		
//		element.getParentElement().addClassName(CSS.DISPLAY_WRAPPER);
//		this.addStyleName(CSS.DISPLAY_WRAPPER);
		
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
		Label typeLabel = new Label(type.name());
		typeLabel.addStyleName(type.toString());
		typeLabel.addStyleName(CSS.DISPLAY_WRAPPER);
		details.add(typeLabel);
		
			switch (type) {
			case Number:
				String fullValue = node.getAttribute(MathAttribute.Value);
				HTML quantity = new HTML(fullValue);

				String unit = node.getAttribute(MathAttribute.Unit);
				if(!"".equals(unit)) {
					quantity.getElement().appendChild(UnitUtil.element_From_attribute(unit));
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
			
			this.addStyleName(CSS.DISPLAY_WRAPPER);
			if(TypeML.Operation.equals(node.getType())) {
				node.getPrevSibling().getWrapper().addStyleName(CSS.DISPLAY_WRAPPER);
				node.getNextSibling().getWrapper().addStyleName(CSS.DISPLAY_WRAPPER);
			}

			fillSelectionDetails();

			super.select();
		}
	}

	@Override
	public void unselect() {
		super.unselect();
		algebraActivity.selectionDetails.clear();

		this.removeStyleName(CSS.DISPLAY_WRAPPER);
		if(TypeML.Operation.equals(node.getType())) {
			node.getPrevSibling().getWrapper().removeStyleName(CSS.DISPLAY_WRAPPER);
			node.getNextSibling().getWrapper().removeStyleName(CSS.DISPLAY_WRAPPER);
		}
	}
}
