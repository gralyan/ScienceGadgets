package com.sciencegadgets.client.ui.sliders;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Image;
import com.kiouri.sliderbar.client.view.SliderBarHorizontal;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.ui.Resizable;

public class SlideBarIncremental extends SliderBarHorizontal implements Resizable{

	ImagesSliderBar images = GWT.create(ImagesSliderBar.class);
	
	String width;
	
	@UiConstructor
	public SlideBarIncremental(int maxValue, String width) {
		
		this.width = width;
		
		setLessWidget(new Image(images.less()));
		setScaleWidget(new Image(images.scale()), 6);
		setMoreWidget(new Image(images.more()));
		setDragWidget(new Image(images.drag()));
		this.setMaxValue(maxValue);
		this.setNotSelectedInFocus();
		this.drawMarks("black", 10);
	}
	

	@Override
	protected void onLoad() {
		super.onLoad();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				resize();
			}
		});
		Moderator.resizables.add(this);
	}
	@Override
	protected void onDetach() {
		super.onDetach();
		Moderator.resizables.remove(this);
	}
	@Override
	public void resize() {
		this.setWidth(width);
	}
	
	interface ImagesSliderBar extends ClientBundle {

		@Source("left.png")
		ImageResource less();

		@Source("line.png")
		ImageResource scale();
		
		@Source("right.png")
		ImageResource more();
		
		@Source("drag.png")
		ImageResource drag();
	}

}
