package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;

public class TransformationButton extends Button {

	public TransformationButton() {
		super();
	}

	public TransformationButton(String html) {
		super(html);
	}

	@Override
	public void setHTML(String html) {
		html = "<div style=\"display:inline-block;\">" + html + "</div>";
		super.setHTML(html);

	}

	@Override
	protected void onAttach() {
		super.onAttach();

		Element buttonElement = this.getElement();

		if ("".equals(buttonElement.getStyle().getFontSize())) {

			Element htmlElement = (Element) buttonElement.getFirstChild();

			double widthRatio = (double) buttonElement.getOffsetWidth()
					/ htmlElement.getOffsetWidth();
			double heightRatio = (double) buttonElement.getOffsetHeight()
					/ htmlElement.getOffsetHeight();
			
			double smallerRatio = (widthRatio > heightRatio) ? heightRatio
					: widthRatio;
			// *70 for looser fit, *100 for percent
			double fontPercent = smallerRatio * 70;

			buttonElement.getStyle().setFontSize((fontPercent), Unit.PCT);
			addStyleName("layoutRow");
		}
	}
}
