package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
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
	protected Label value = new Label();

	public EquationWrapper(MathNode node, AlgebraActivity algebraActivity,
			Element element) {
		super(node, algebraActivity.eqPanel, element);
		this.eqPanel = algebraActivity.eqPanel;
		this.algebraActivity = algebraActivity;

		this.addStyleName(CSS.DISPLAY_WRAPPER);
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
			if (!"".equals(unit)) {
				quantity.getElement().appendChild(
						UnitUtil.element_From_attribute(unit));
			}

			details.add(quantity);

			break;
		case Variable:
			String qKind = node.getAttribute(MathAttribute.Unit);
			if (!"".equals(qKind)) {
				details.add(new Label(qKind));
			}
			break;
		}
	}

	@Override
	public void select() {

		if (this.equals(eqPanel.selectedWrapper)) {

			// If this was already selected, focus in on it
			if (node.hasChildElements()
					&& (dragController == null || !dragController.isDragging())) {
				unselect();
				eqPanel.setFocus(eqPanel.eqLayerMap.get(node));
			}
		} else {

			// If there is another selection, unselect it
			if (eqPanel.selectedWrapper != null) {
				eqPanel.selectedWrapper.unselect();
			}

			fillSelectionDetails();

			String valueStr = node.getAttribute(MathAttribute.Value);
			if (!"".equals(valueStr) && !valueStr.equals(node.getXMLNode().getInnerText())) {
				value.setText(valueStr);
				Style valueStyle = value.getElement().getStyle();
				valueStyle.setBackgroundColor("white");
				valueStyle.setZIndex(3);
				eqPanel.add(value, this.getAbsoluteLeft()-eqPanel.getAbsoluteLeft(), this.getAbsoluteTop()-eqPanel.getAbsoluteTop());
			}

			eqPanel.selectedWrapper = this;

			super.select();
		}
	}

	@Override
	public void unselect() {
		super.unselect();

		eqPanel.selectedWrapper = null;
		
		value.removeFromParent();

		algebraActivity.selectionDetails.clear();
	}
}
