package com.sciencegadgets.client;

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
import com.google.gwt.user.client.ui.Label;

public class ToggleSlide extends FlowPanel implements HasClickHandlers, HasTouchStartHandlers {
	HTML firstOption = new HTML();
	HTML secondOption = new HTML();
	HTML selectedOption = null;

	public ToggleSlide(String first, String second, boolean firstIsSelected, ClickHandler clickHangler) {
		this(first, second, firstIsSelected);
		addClickHandler(clickHangler);
	}
	public ToggleSlide(String first, String second, boolean firstIsSelected) {
		this();
		setOptions(first, second, firstIsSelected);
	}
	
	/**
	 * Be sure to set the options with {@link #setOptions(String, String, boolean)}
	 */
	public ToggleSlide() {
		
		this.addStyleName("ToggleSlide");

		firstOption.addStyleName("toggleOption");
		secondOption.addStyleName("toggleOption");

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
		if(firstOption.equals(selectedOption)){
			selectedOption = secondOption;
			secondOption.addStyleName("toggleOptionSelected");
			firstOption.removeStyleName("toggleOptionSelected");
		}else{
			selectedOption = firstOption;
			firstOption.addStyleName("toggleOptionSelected");
			secondOption.removeStyleName("toggleOptionSelected");
			
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
			firstOption.addStyleName("toggleOptionSelected");
		}else{
			selectedOption = secondOption;
			secondOption.addStyleName("toggleOptionSelected");
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
