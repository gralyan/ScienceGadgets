package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.UnitAttribute;
import com.sciencegadgets.shared.UnitHTML;

public class EquationWrapper extends Wrapper {
	protected EquationPanel eqPanel;
	protected AlgebraActivity algebraActivity;
	protected Label value;
	private Style valueStyle;

	public EquationWrapper(EquationNode node, AlgebraActivity algebraActivity,
			Element element) {
		super(node, algebraActivity.eqPanel, element);
		this.eqPanel = algebraActivity.eqPanel;
		this.algebraActivity = algebraActivity;

		this.addStyleName(CSS.DISPLAY_WRAPPER);

		String valueStr = node.getAttribute(MathAttribute.Value);
		if (!"".equals(valueStr)
				&& !valueStr.equals(node.getXMLNode().getInnerText())) {
			value = new Label(valueStr);
			valueStyle = value.getElement().getStyle();
			value.addStyleName(CSS.NUMBER_VALUE);
			valueStyle.setWidth(this.getOffsetWidth(), Unit.PX);
			eqPanel.add(value,
					this.getAbsoluteLeft() - eqPanel.getAbsoluteLeft(),
					this.getAbsoluteTop() - eqPanel.getAbsoluteTop());
		}
	}

	public EquationPanel getEqPanel() {
		return eqPanel;
	}

	private void fillSelectionDetails() {
		FlowPanel details = algebraActivity.selectionDetails;

		TypeEquationXML type = node.getType();
		Label typeLabel = new Label(type.name());
		typeLabel.addStyleName(type.toString());
		typeLabel.addStyleName(CSS.DISPLAY_WRAPPER);
		details.add(typeLabel);

		switch (type) {
		case Number:
			String fullValue = node.getAttribute(MathAttribute.Value);
			HTML quantity = new HTML(fullValue);

			UnitAttribute unit = node.getUnitAttribute();
			if (!"".equals(unit)) {
				quantity.getElement().appendChild(UnitHTML.create(unit));
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

			if (valueStyle != null) {
				valueStyle.clearWidth();
				valueStyle.setBackgroundColor("white");
			}

			eqPanel.selectedWrapper = this;

			super.select();
		}
	}

	@Override
	public void unselect() {
		super.unselect();

		eqPanel.selectedWrapper = null;

		if (valueStyle != null) {
			valueStyle.setWidth(this.getOffsetWidth(), Unit.PX);
			valueStyle.clearBackgroundColor();
		}

		algebraActivity.selectionDetails.clear();
	}
}
