package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.FitParentHTML;

public class TransformationButton extends SimplePanel implements
		HasClickHandlers {

	FitParentHTML buttonHTML;

	public TransformationButton() {
		super();
		addStyleName(CSS.TRANSFORMATION_BUTTON + " " + CSS.LAYOUT_ROW);
	}

	public TransformationButton(String html) {
		this();
		setHTML(html);
	}

	public void setHTML(String html) {
		clear();
		buttonHTML = new FitParentHTML(html);
		buttonHTML.percentOfParent = 85;
		add(buttonHTML);
	}

	public String getHTML() {
		return buttonHTML.getHTML();
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
}
