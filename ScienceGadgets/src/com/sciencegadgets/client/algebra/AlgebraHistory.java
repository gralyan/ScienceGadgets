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
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.Rule;

public class AlgebraHistory extends FlowPanel {

	private AlgebraActivity algebraActivity;

	boolean expanded = false;
	public String origionalHeightStr;
	public boolean scrolled = false;
	FlowPanel firstRow = new FlowPanel();
	public static final String UP_ARROW = "\u2191";
	boolean wasTouchMoved = false;

	public AlgebraHistory(AlgebraActivity algebraActivity) {
		this.algebraActivity = algebraActivity;
		addStyleName(CSS.ALG_OUT);

		origionalHeightStr = algebraActivity.upperEqArea.getElement()
				.getStyle().getHeight();

		if (Moderator.isTouch) {
			this.addDomHandler(new AlgOutTouchMove(), TouchMoveEvent.getType());
			this.addDomHandler(new AlgOutTouchEnd(), TouchEndEvent.getType());
		} else {
			this.addDomHandler(new AlgOutClickHandler(), ClickEvent.getType());
		}

		Label firstRowEq = new Label("Solve");
		firstRowEq.addStyleName(CSS.ALG_OUT_EQ_ROW);
		firstRow.add(firstRowEq);

		Label firstRowRule = new Label();
		firstRowRule.addStyleName(CSS.ALG_OUT_RULE_ROW);
		firstRow.add(firstRowRule);

		firstRow.addStyleName(CSS.ALG_OUT_ROW);

		add(firstRow);
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		firstRow.setSize("100%", getOffsetHeight() + "px");
	}

	public void updateAlgebraHistory(String changeComment, Rule rule, EquationTree mathTree) {

		changeComment = changeComment.replace("lineThrough", "");
		
		add(new AlgebraHistoryRow(changeComment, rule, mathTree));

		if (changeComment.contains(BothSidesTransformations.BOTH_SIDES)) {
			add(new AlgebraHistoryRow(changeComment.replace(
					BothSidesTransformations.BOTH_SIDES, UP_ARROW)));
		}

	}

	void scrollToBottom() {
		getElement()
				.setScrollTop(
						getElement().getScrollHeight()
								- getElement().getClientHeight());
	}

	class AlgebraHistoryRow extends FlowPanel {
		private FlowPanel eqSide = new FlowPanel();
		private Anchor ruleSide = new Anchor();

		// Change row
		AlgebraHistoryRow(String changeComment) {
			this(new HTML("<div>" + changeComment
					+ "</div><div></div><div>" + changeComment + "</div>"));

			addStyleName(CSS.ALG_OUT_CHANGE_ROW);

		}

		// Equation row
		AlgebraHistoryRow(String changeComment, Rule rule, EquationTree mathTree) {
			this(mathTree.getDisplay());

			ruleSide.setHTML(changeComment);

			if (rule != null) {
				ruleSide.setHref(rule.getPage());
				ruleSide.setTarget("_blank");
			}
		}

		private AlgebraHistoryRow(HTML root) {
			addStyleName(CSS.ALG_OUT_ROW);

			eqSide.addStyleName(CSS.ALG_OUT_EQ_ROW);
			add(eqSide);

			eqSide.add(root);

			Element rootEl = root.getElement();
			rootEl.addClassName(CSS.FILL_PARENT);
			
			Element rightCase = DOM.createDiv();
			Element leftCase = DOM.createDiv();
			rightCase.appendChild(rootEl.getChild(2));
			leftCase.appendChild(rootEl.getChild(0));
			rootEl.appendChild(rightCase);
			rootEl.insertFirst(leftCase);
			(leftCase).addClassName(CSS.ALG_OUT_EQ_LEFT);
			((Element) rootEl.getChild(1)).addClassName(CSS.ALG_OUT_EQ_EQUALS);
			(rightCase).addClassName(CSS.ALG_OUT_EQ_RIGHT);

			ruleSide.addStyleName(CSS.ALG_OUT_RULE_ROW);
			add(ruleSide);

		}
	}

	class AlgOutSlide extends Animation {

		private FlowPanel alg = algebraActivity.upperEqArea;;
		int heightDiff = 0;
		private int direction;
		private int startingHeight;
		private int maxHeight;

		@Override
		public void run(int duration) {
			startingHeight = getOffsetHeight();

			// Don't go past the screen
			int scrollHeight = getElement().getScrollHeight();
			int screenHeight = Moderator.scienceGadgetArea.getOffsetHeight();
			maxHeight = scrollHeight > screenHeight ? screenHeight
					: scrollHeight;

			int diff = maxHeight - startingHeight;

			if (expanded) {

				direction = -1;

			} else if (diff < 3) {
				// don't do anything if there is nothing hidden to expand
				return;
			} else {// not expanded but should be

				heightDiff = diff;
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
				alg.setHeight(maxHeight + "px");
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

	class AlgOutTouchMove implements TouchMoveHandler {

		@Override
		public void onTouchMove(TouchMoveEvent event) {
			wasTouchMoved = true;
		}

	}

	class AlgOutTouchEnd implements TouchEndHandler {
		AlgOutSlide slide = new AlgOutSlide();

		@Override
		public void onTouchEnd(TouchEndEvent event) {
			if (!wasTouchMoved) {
				slide.run(300);
			}
			wasTouchMoved = false;
		}

	}
}
