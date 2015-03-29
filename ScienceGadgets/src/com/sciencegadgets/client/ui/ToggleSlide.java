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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasTouchStartHandlers;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Moderator;

public class ToggleSlide extends FlowPanel implements HasClickHandlers, HasTouchStartHandlers {
	HTML firstOption = new HTML();
	HTML secondOption = new HTML();
	HTML selectedOption = null;

	public ToggleSlide(String first, String second, boolean firstIsSelected, ClickHandler clickHandler) {
		this(first, second, firstIsSelected);
		addClickHandler(clickHandler);
	}
	public ToggleSlide(String first, String second, boolean firstIsSelected) {
		this();
		setOptions(first, second, firstIsSelected);
	}
	
	/**
	 * Be sure to set the options with {@link #setOptions(String, String, boolean)}
	 */
	public ToggleSlide() {
		
		this.addStyleName(CSS.TOGGLE_SLIDE);

		firstOption.addStyleName(CSS.TOGGLE_OPTION);
		secondOption.addStyleName(CSS.TOGGLE_OPTION);

		this.add(firstOption);
		this.add(secondOption);

		if(Moderator.isTouch) {
			addTouchStartHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					toggle();
				}
			});
		}else {
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					toggle();
				}
			});
		}
		
	}
	
	private void toggle() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			
			@Override
			public void execute() {
				if(firstOption.equals(selectedOption)){
			selectedOption = secondOption;
			secondOption.addStyleName(CSS.TOGGLE_OPTION_SELECTED);
			firstOption.removeStyleName(CSS.TOGGLE_OPTION_SELECTED);
		}else{
			selectedOption = firstOption;
			firstOption.addStyleName(CSS.TOGGLE_OPTION_SELECTED);
			secondOption.removeStyleName(CSS.TOGGLE_OPTION_SELECTED);
			
		}
			}
		});
		
		
	}
	
	public void enable(boolean enable) {
		Style style = getElement().getStyle();
		if(enable) {
			style.clearColor();
		}else {
			style.setColor("gray");
		}
	}
	
	public void setOptions(String first, String second, boolean firstIsSelected) {
		firstOption.setText(first);
		secondOption.setText(second);
		setOptions(firstIsSelected);
		
	}
	public void setOptionsHtml(String first, String second, boolean firstIsSelected) {
		firstOption.setHTML(first);
		secondOption.setHTML(second);
		setOptions(firstIsSelected);
	}
	public void setOptions(SafeHtml first, SafeHtml second, boolean firstIsSelected) {
		firstOption.setHTML(first);
		secondOption.setHTML(second);
		setOptions(firstIsSelected);
	}
	private void setOptions(boolean firstIsSelected) {

		if(firstIsSelected){
			selectedOption = firstOption;
			firstOption.addStyleName(CSS.TOGGLE_OPTION_SELECTED);
		}else{
			selectedOption = secondOption;
			secondOption.addStyleName(CSS.TOGGLE_OPTION_SELECTED);
		}
	}
	
	public boolean isFistSelected() {
		return firstOption.equals(selectedOption);
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
	@Override
	public HandlerRegistration addTouchStartHandler(TouchStartHandler handler) {
		return addDomHandler(handler, TouchStartEvent.getType());
	}
}
