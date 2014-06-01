package com.sciencegadgets.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.Moderator;

public abstract class SelectionButton extends SimplePanel implements HasClickHandlers,
		HasTouchEndHandlers {

	FitParentHTML buttonHTML;
	
	protected SelectionButton(){
		
		addStyleName(CSS.TRANSFORMATION_BUTTON);
		
		if (Moderator.isTouch) {
			addTouchEndHandler(new TouchEndHandler() {
				@Override
				public void onTouchEnd(TouchEndEvent event) {
					onSelect();
				}
			});
		} else {
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					onSelect();
				}
			});
		}
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
		buttonHTML = new FitParentHTML(html);
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

}
