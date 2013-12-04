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

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.UIObject;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.transformations.Rule;
import com.sciencegadgets.shared.TypeML.Operator;

public class AlgOut extends ScrollPanel {

	boolean expanded = false;
	FlowPanel algOutFlow = new FlowPanel();
	public String origionalHeightStr;
	public boolean scrolled = false;

	public AlgOut() {
		add(algOutFlow);
		algOutFlow.getElement().setId("algOut");
		getScrollableElement().setId("algOutScroll");
		getScrollableElement().getStyle().clearOverflow();

		origionalHeightStr = AlgebraActivity.upperEqArea.getElement().getStyle()
				.getHeight();

		this.sinkEvents(Event.ONCLICK);
		this.addHandler(new AlgOutClickHandler(), ClickEvent.getType());

		this.sinkEvents(Event.ONTOUCHEND);
		this.addHandler(new AlgOutTouchEndHandler(), TouchEndEvent.getType());

		this.sinkEvents(Event.ONSCROLL);
		this.addHandler(new AlgOutTouchMoveHandler(), TouchMoveEvent.getType());
	}

	public void updateAlgOut(String changeComment, Rule rule, EquationHTML eqHTML) {

		FlowPanel row = new FlowPanel();
		row.addStyleName("algOutRow");
		
		eqHTML.addStyleName("algOutEquationRow");
		row.add(eqHTML);

		Anchor changeRow = new Anchor(changeComment,true);
		if(rule != null){
		changeRow.setHref(rule.getPage());
		changeRow.setTarget("_blank");
		}
		changeRow.addStyleName("algOutChangeRow");
		row.add(changeRow);
		
		algOutFlow.add(row);

		scrollToBottom();
	}

	class AlgOutSlide extends Animation {

		private FlowPanel alg;
		private int startingHeight;
		private double dir;

		AlgOutSlide() {
			alg = AlgebraActivity.upperEqArea;
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
				scrollToBottom();
			} else {
				alg.setHeight((startingHeight * 2) + "px");
				expanded = true;
				scrollToBottom();
			}
		}
	}

	class AlgOutClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
				AlgOutSlide slide = new AlgOutSlide();
				slide.run(300);
		}
	}

	class AlgOutTouchMoveHandler implements TouchMoveHandler {
		@Override
		public void onTouchMove(TouchMoveEvent event) {
			scrolled = true;
		}
	}

	class AlgOutTouchEndHandler implements TouchEndHandler {
		@Override
		public void onTouchEnd(TouchEndEvent event) {
			((UIObject) event.getSource()).unsinkEvents(Event.ONCLICK);
			
			event.stopPropagation();
			event.preventDefault();
			
			if (!scrolled) {
				AlgOutSlide slide = new AlgOutSlide();
				slide.run(300);
			} else {
				scrolled = false;//restore field
			}
		}
	}
}


