package com.sciencegadgets.client.conversion;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

class ConversionWrapper extends Wrapper {

	ConversionAvtivity conversionAvtivity = null;
	private boolean cancelled;

	ConversionWrapper(MathNode node, AbsolutePanel panel, Element element) {
		super(node, panel, element);
		conversionAvtivity = ((ConversionAvtivity) parentPanel);
	}

	public void cancel() {
		getElement().getStyle()
		.setTextDecoration(TextDecoration.LINE_THROUGH);
		cancelled = true;
	}

	@Override
	public void select() {
		if (this.equals(ConversionAvtivity.selectedWrapper) || cancelled) {
			return;
		}

		conversionAvtivity.fillUnitSelection(node.getUnitAttribute());

		if (ConversionAvtivity.selectedWrapper != null) {
			ConversionAvtivity.selectedWrapper.unselect();
		}
		ConversionAvtivity.selectedWrapper = this;
		super.select();
	}

	@Override
	public void unselect() {
		super.unselect();
		conversionAvtivity.unitSelection.unitBox.clear();
		ConversionAvtivity.selectedWrapper = null;
	}

}