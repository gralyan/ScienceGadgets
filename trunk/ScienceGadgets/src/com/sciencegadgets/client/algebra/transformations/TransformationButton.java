package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.CSS;

public class TransformationButton extends HTML {

	public TransformationButton() {
		super();
		addStyleName(CSS.TRANSFORMATION_BUTTON);
	}

	public TransformationButton(String html) {
		super(html);
		addStyleName(CSS.TRANSFORMATION_BUTTON);
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
					/(double) htmlElement.getOffsetWidth();
			double heightRatio = (double) buttonElement.getOffsetHeight()
					/(double) htmlElement.getOffsetHeight();

			
			double smallerRatio = (widthRatio > heightRatio) ? heightRatio
					: widthRatio;
			// *85 for looser fit, *100 for percent
			double fontPercent = smallerRatio * 85;

			buttonElement.getStyle().setFontSize((fontPercent), Unit.PCT);
			addStyleName(CSS.LAYOUT_ROW);
		}
	}
}
