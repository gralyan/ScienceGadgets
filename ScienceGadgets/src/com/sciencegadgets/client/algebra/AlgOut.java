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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.transformations.Rule;

public class AlgOut extends FlowPanel {

	boolean expanded = false;
	public String origionalHeightStr;
	public boolean scrolled = false;
	FlowPanel firstRow = new FlowPanel();

	public AlgOut() {
		addStyleName("algOut");

		origionalHeightStr = AlgebraActivity.upperEqArea.getElement()
				.getStyle().getHeight();

//		this.addDomHandler(new AlgOutTouchMoveHandler(),
//				TouchMoveEvent.getType());
		// this.addDomHandler(new AlgOutTouchEndHandler(),
		// TouchEndEvent.getType());

		if(Moderator.isTouch) {
			this.addDomHandler(new AlgOutTouchStart(), TouchStartEvent.getType());
		}else {
			this.addDomHandler(new AlgOutClickHandler(), ClickEvent.getType());
		}

		Label firstRowEq = new Label("Solve");
		firstRowEq.addStyleName("algOutEquationRow");
		firstRow.add(firstRowEq);

		Label firstRowCh = new Label();
		firstRowCh.addStyleName("algOutChangeRow");
		firstRow.add(firstRowCh);

		firstRow.addStyleName("algOutRow");

		add(firstRow);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		firstRow.setSize("100%", getOffsetHeight() + "px");
	}

	public void updateAlgOut(String changeComment, Rule rule,
			EquationHTML eqHTML) {

		FlowPanel row = new FlowPanel();
		row.addStyleName("algOutRow");

		eqHTML.addStyleName("algOutEquationRow");
		row.add(eqHTML);

		Anchor changeRow = new Anchor(changeComment, true);
		if (rule != null) {
			changeRow.setHref(rule.getPage());
			changeRow.setTarget("_blank");
		}
		changeRow.addStyleName("algOutChangeRow");
		row.add(changeRow);

		add(row);

		scrollToBottom();
	}

	private void scrollToBottom() {
		getElement()
				.setScrollTop(
						getElement().getScrollHeight()
								- getElement().getClientHeight());
	}

	class AlgOutSlide extends Animation {

		private FlowPanel alg = AlgebraActivity.upperEqArea;;
		int heightDiff = 0;
		private int direction;
		private int startingHeight;

		@Override
		public void run(int duration) {
			int normalHeight = getOffsetHeight();
			heightDiff = getElement().getScrollHeight() - normalHeight;

			if (expanded) {

				startingHeight = normalHeight + heightDiff;
				direction = -1;

			} else if (heightDiff < 3) {
				// don't do anything if there is nothing hidden to expand
				return;
			} else {// not expanded but should be

				startingHeight = normalHeight;
				direction = 1;
			}
			super.run(duration);
		}

		@Override
		protected void onUpdate(double progress) {
			alg.setHeight((startingHeight + (direction * heightDiff * progress))
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
				alg.setHeight(getElement().getScrollHeight() + "px");
				expanded = true;
				scrollToBottom();
			}
		}
	}

	class AlgOutClickHandler implements ClickHandler {
		AlgOutSlide slide = new AlgOutSlide();

		@Override
		public void onClick(ClickEvent event) {
			slide.run(300);
		}
	}

	class AlgOutTouchStart implements TouchStartHandler {
		AlgOutSlide slide = new AlgOutSlide();

		@Override
		public void onTouchStart(TouchStartEvent event) {
			slide.run(300);
		}

	}
	// class AlgOutTouchMoveHandler implements TouchMoveHandler {
	// @Override
	// public void onTouchMove(TouchMoveEvent event) {
	// scrolled = true;
	// }
	// }
	//
	// class AlgOutTouchEndHandler implements TouchEndHandler {
	// AlgOutSlide slide = new AlgOutSlide();
	//
	// @Override
	// public void onTouchEnd(TouchEndEvent event) {
	// ((UIObject) event.getSource()).unsinkEvents(Event.ONCLICK);
	//
	// event.stopPropagation();
	// event.preventDefault();
	//
	// if (!scrolled) {
	// slide.run(300);
	// } else {
	// scrolled = false;// restore field
	// }
	// }
	// }
}
