/*   Copyright 2012 John Gralyan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sciencegadgets.client.algebra;

import org.datanucleus.query.evaluator.memory.UpperFunctionEvaluator;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.sciencegadgets.client.Moderator;

public class AlgOut extends ScrollPanel {

	boolean expanded = false;
	FlowPanel algOutFlow = new FlowPanel();
	public String origionalHeightStr;

	public AlgOut() {
		add(algOutFlow);
		algOutFlow.getElement().setId("algOut");
		getScrollableElement().setId("algOutScroll");

		origionalHeightStr = Moderator.upperEqArea.getElement().getStyle()
				.getHeight();

		this.sinkEvents(Event.ONCLICK);
		this.addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				AlgOutSlide slide = new AlgOutSlide();
				slide.run(300);
			}
		}, ClickEvent.getType());
		
		this.sinkEvents(Event.ONTOUCHSTART);
		this.addHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.stopPropagation();
				event.preventDefault();
				AlgOutSlide slide = new AlgOutSlide();
				slide.run(300);
			}
		}, TouchStartEvent.getType());
	}

	public void updateAlgOut(String changeComment, EquationHTML eqHTML) {

		algOutFlow.add(eqHTML);

		HTML changeRow = new HTML(changeComment);
		changeRow.setStyleName("algOutChangeRow");
		algOutFlow.add(changeRow);

		scrollToBottom();
	}

	class AlgOutSlide extends Animation {

		private FlowPanel alg;
		private int startingHeight;
		private double dir;

		AlgOutSlide() {
			alg = Moderator.upperEqArea;
			startingHeight = alg.getOffsetHeight();

			if (expanded) {
				dir = -0.5;
			} else {
				dir = 1;
			}
		}
		@Override
		protected void onUpdate(double progress) {
			alg.setHeight((startingHeight + (dir * progress * startingHeight))
					+ "px");
		}
		@Override
		protected void onComplete() {
			super.onComplete();
			if (expanded) {
				alg.setHeight(origionalHeightStr);
				expanded = false;
			} else {
				alg.setHeight((startingHeight * 2) + "px");
				expanded = true;
			}
		}
	}
}
