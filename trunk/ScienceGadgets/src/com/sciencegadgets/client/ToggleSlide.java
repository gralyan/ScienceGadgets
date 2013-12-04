package com.sciencegadgets.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class ToggleSlide extends FlowPanel implements HasClickHandlers {
	Label firstOption = new Label();
	Label secondOption = new Label();
	Label selectedOption = null;

	public ToggleSlide(String first, String second, boolean firstIsSelected, ClickHandler clickHangler) {
		this(first, second, firstIsSelected);
		addClickHandler(clickHangler);
	}
	public ToggleSlide(String first, String second, boolean firstIsSelected) {
		
		this.addStyleName("ToggleSlide");

		firstOption.setText(first);
		secondOption.setText(second);
		
		if(firstIsSelected){
			selectedOption = firstOption;
			firstOption.addStyleName("toggleOptionSelected");
		}else{
			selectedOption = secondOption;
			secondOption.addStyleName("toggleOptionSelected");
		}

		firstOption.addStyleName("toggleOption");
		secondOption.addStyleName("toggleOption");

		this.add(firstOption);
		this.add(secondOption);

		addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
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
		});
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
}
