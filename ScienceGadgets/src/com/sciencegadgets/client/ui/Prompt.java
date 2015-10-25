/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasBlurHandlers;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;

/**
 * A common method of presenting a user with a window without creating an
 * entirely new activity. Prompts are particularly useful for inquiring for a
 * specification on an action requiring user input.<br/>
 * <b>Do not launch with a
 * {@link com.google.gwt.event.dom.client.TouchStartHandler} because some
 * browsers autoHide the Prompt immediately after appearing</b><br/>
 * Launch with {@link Prompt#appear()}<br/>
 * Remove with {@link Prompt#disappear()}<br/>
 */
public class Prompt extends DialogBox implements Resizable, HasKeyUpHandlers, HasBlurHandlers {

	private static final double HEIGHT_FRACTION = 0.7;
	private static final double WIDTH_FRACTION = 0.8;
	protected final FlowPanel flowPanel = new FlowPanel();
	final Button okButton = new Button("OK");
	protected boolean okClickedOnEnterPressed = true;
	private FocusPanel focusPanel = new FocusPanel();

	public Prompt() {
		this(true);
	}

	public Prompt(boolean hasOkButton) {
		super(true, false);
		FlowPanel mainPanel = new FlowPanel();
		mainPanel.add(flowPanel);
		if (hasOkButton) {
			okButton.addStyleName(CSS.OK_PROMPT_BUTTON);
			mainPanel.add(okButton);
		}
		focusPanel.add(mainPanel);
		super.add(focusPanel);

		setGlassEnabled(true);
		setAnimationEnabled(false);

		focusPanel.getElement().getStyle().setOutlineStyle(OutlineStyle.NONE);
		flowPanel.getElement().getStyle().setOverflowY(Overflow.AUTO);

		addStyleName(CSS.PROMPT_MAIN);
		okButton.addStyleName(CSS.SMALLEST_BUTTON);

		addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				if (okClickedOnEnterPressed && event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					okButton.click();
				}
			}
		});

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
		Moderator.resizables.remove(this);
	}

	public void appear() {
		resize();
		super.center();
		Moderator.resizables.add(this);
		focusPanel.setFocus(true);
	}

	@Override
	public void hide() {
		disappear();
	}

	@Override
	public void center() {
		appear();
	}

	@Override
	public void resize() {
		flowPanel.setPixelSize(
				(int) (Window.getClientWidth() * WIDTH_FRACTION),
				(int) (Window.getClientHeight() * HEIGHT_FRACTION));
	}

	@Override
	public HandlerRegistration addKeyUpHandler(KeyUpHandler handler) {
		return focusPanel.addKeyUpHandler(handler);
	}

	@Override
	public HandlerRegistration addBlurHandler(BlurHandler handler) {
		return focusPanel.addBlurHandler(handler);
	}
	
}