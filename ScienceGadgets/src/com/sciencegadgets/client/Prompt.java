package com.sciencegadgets.client;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class Prompt extends DialogBox {

	private static final double HEIGHT_FRACTION = 0.7;
	private static final double WIDTH_FRACTION = 0.8;
	protected final FlowPanel flowPanel = new FlowPanel();
	final Button okButton = new Button("OK");

	public Prompt() {
		this(true);
	}

	public Prompt(boolean hasOkButton) {
		super(true, false);
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(flowPanel);
		if (hasOkButton) {
			FlowPanel gap = new FlowPanel();
			gap.setHeight("10px");
			mainPanel.add(gap);
			mainPanel.add(okButton);
		}
		super.add(mainPanel);

		setGlassEnabled(true);
		setAnimationEnabled(true);

		flowPanel.getElement().getStyle().setOverflowY(Overflow.SCROLL);

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
		flowPanel.setPixelSize(
				(int) (Window.getClientWidth() * WIDTH_FRACTION),
				(int) (Window.getClientHeight() * HEIGHT_FRACTION));
	}

	@Override
	public void hide() {
		super.hide();
		Moderator.prompts.remove(this);
	}

	@Override
	public void center() {
		super.center();
		Moderator.prompts.add(this);
	}

}