package com.sciencegadgets.client.ui;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.Moderator;

public abstract class SelectionButton extends SimplePanel implements
		HasClickHandlers, HasTouchEndHandlers {

	FitParentHTML buttonHTML;
	boolean isEnabled = true;

	protected SelectionButton() {
		addStyleName(CSS.TRANSFORMATION_BUTTON);

		if (Moderator.isTouch) {
			addTouchEndHandler(new TouchEndHandler() {
				@Override
				public void onTouchEnd(TouchEndEvent event) {
					if (isEnabled) {
						onSelect();
					}
				}
			});
		} else {
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (isEnabled) {
						onSelect();
					}
				}
			});
		}

	}

	protected SelectionButton(String html) {
		this();
		setHTML(html);
	}

	@Override
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return addDomHandler(handler, TouchEndEvent.getType());
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	abstract protected void onSelect();

	public void resize() {
		buttonHTML.resize();
	}

	public void setHTML(String html) {
		clear();
		if (buttonHTML == null) {
			buttonHTML = new FitParentHTML(html);
		} else {
			buttonHTML.setHTML(html);
		}
		buttonHTML.percentOfParent = 85;
		add(buttonHTML);
	}

	public String getHTML() {
		if (buttonHTML == null) {
			return null;
		} else {
			return buttonHTML.getHTML();
		}
	}

	public void setEnabled(boolean enable) {
		this.isEnabled = enable;
		Style style = getElement().getStyle();
		if (enable) {
			addStyleName(CSS.TRANSFORMATION_BUTTON);
			style.clearColor();
		} else {
			removeStyleName(CSS.TRANSFORMATION_BUTTON);
			style.setColor("gray");
		}
	}

}
