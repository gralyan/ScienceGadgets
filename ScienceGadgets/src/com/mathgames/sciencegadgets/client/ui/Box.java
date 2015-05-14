package com.mathgames.sciencegadgets.client.ui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseDownHandlers;
import com.google.gwt.event.dom.client.HasMouseUpHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.mathgames.sciencegadgets.client.CSS;

public class Box extends FlowPanel implements HasClickHandlers,
		HasMouseDownHandlers, HasMouseUpHandlers, HasTouchStartHandlers,
		HasTouchEndHandlers {

	private SelectionStyle selectionStyle = SelectionStyle.UNSELECTED;

	enum SelectionStyle {
		SELECTED_POSITIVE, SELECTED_NEGATIVE, UNSELECTED;
	}

	public Box() {
		addStyleName(CSS.BOX);
	}

	public void changeStyle(SelectionStyle sStyle) {
		selectionStyle = sStyle;

		switch (sStyle) {
		case SELECTED_POSITIVE:
			addStyleName(CSS.BOX_SELECTED_POSITIVE);
			removeStyleName(CSS.BOX_SELECTED_NEGATIVE);
			break;
		case SELECTED_NEGATIVE:
			addStyleName(CSS.BOX_SELECTED_NEGATIVE);
			removeStyleName(CSS.BOX_SELECTED_POSITIVE);
			break;
		case UNSELECTED:
			removeStyleName(CSS.BOX_SELECTED_NEGATIVE);
			removeStyleName(CSS.BOX_SELECTED_POSITIVE);
			break;
		default:
			break;
		}
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return addDomHandler(handler, TouchEndEvent.getType());
	}

	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addDomHandler(handler, TouchStartEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseUpHandler(MouseUpHandler handler) {
		return addDomHandler(handler, MouseUpEvent.getType());
	}

	@Override
	public HandlerRegistration addMouseDownHandler(MouseDownHandler handler) {
		return addDomHandler(handler, MouseDownEvent.getType());
	}

}
