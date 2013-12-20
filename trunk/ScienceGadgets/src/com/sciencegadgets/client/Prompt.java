package com.sciencegadgets.client;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class Prompt extends DialogBox {

	private static final double HEIGHT_FRACTION = 0.7;
	private static final double WIDTH_FRACTION = 0.8;
	protected final FlowPanel flowPanel = new FlowPanel();
	final Button okButton = new Button("OK");

	public Prompt() {
		super(true, true);
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(flowPanel);
		FlowPanel gap = new FlowPanel();
		gap.setHeight("10px");
		mainPanel.add(gap);
		mainPanel.add(okButton);
		super.add(mainPanel);
		
		setGlassEnabled(true);
		setAnimationEnabled(true);
		
		getElement().getStyle().setBackgroundColor("#ADD850");
		okButton.addStyleName("smallestButton");

		addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					okButton.click();
				}
			}
		}, KeyDownEvent.getType());
	}

	/**
	 * Handler to be called when "OK" button is clicked. Also fired when "Enter"
	 * is pressed
	 * 
	 * @param handler
	 */
	public void addOkHandler(ClickHandler handler) {
		okButton.addClickHandler(handler);
	}

	@Override
	public void add(Widget w) {
		flowPanel.add(w);
	}

	public void disappear() {
		hide();
		removeFromParent();
	}

	public void appear() {
		resize();
		center();
	}
	
	public void resize() {
		flowPanel.setPixelSize((int)(Window.getClientWidth() * WIDTH_FRACTION),
				(int)(Window.getClientHeight() * HEIGHT_FRACTION));
	}

//	public class FocusOnlyClickHandler implements ClickHandler {
//		@Override
//		public void onClick(ClickEvent event) {
//			event.preventDefault();
//			event.stopPropagation();
//		}
//	}
//
//	public class FocusOnlyTouchHandler implements TouchStartHandler {
//		@Override
//		public void onTouchStart(TouchStartEvent event) {
//			event.preventDefault();
//			event.stopPropagation();
//		}
//	}
}