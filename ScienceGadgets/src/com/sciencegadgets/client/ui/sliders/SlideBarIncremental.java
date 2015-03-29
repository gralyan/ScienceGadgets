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
