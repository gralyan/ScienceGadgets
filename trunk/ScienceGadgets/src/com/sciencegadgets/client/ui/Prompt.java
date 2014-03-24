package com.sciencegadgets.client.ui;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;

/**
 * A common method of presenting a user with a window without creating an
 * entirely new activity. Prompts are particularly useful for inquiring for a
 * specification on an action requiring user input.<br/>
 * <b>Do not launch with a {@link com.google.gwt.event.dom.client.TouchStartHandler} because some browsers
 * autoHide the Prompt immediately after appearing</b><br/>
 * Launch with {@link Prompt#appear()}<br/>
 * Remove with {@link Prompt#disappear()}<br/>
 */
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
		setAnimationEnabled(false);

		flowPanel.getElement().getStyle().setOverflowY(Overflow.AUTO);

//		getElement().getStyle().setBackgroundColor("#ADD850");
		addStyleName(CSS.PROMPT_MAIN);
		okButton.addStyleName(CSS.SMALLEST_BUTTON);

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
		super.hide();
		removeFromParent();
		Moderator.prompts.remove(this);

	}

	public void appear() {
		resize();
		super.center();
		Moderator.prompts.add(this);

	}

	public void resize() {
		flowPanel.setPixelSize(
				(int) (Window.getClientWidth() * WIDTH_FRACTION),
				(int) (Window.getClientHeight() * HEIGHT_FRACTION));
	}

	@Override
	public void hide() {
		disappear();
	}

	@Override
	public void center() {
		appear();
	}

}