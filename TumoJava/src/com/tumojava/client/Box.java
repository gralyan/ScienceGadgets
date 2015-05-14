package com.tumojava.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;

class Box extends FlowPanel implements HasClickHandlers {

	public static final String BOX_CSS = "box";
	public static final String BOXS_CSS = "boxS";
	boolean isSelected = false;

	Box(final Counter counter) {

		addStyleName(BOX_CSS);

		addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (!isSelected) {
					addStyleName(BOXS_CSS);
					counter.up();
					isSelected = true;
				}
			}
		});
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

} 
