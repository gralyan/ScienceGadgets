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
package com.sciencegadgets.client.algebra;

import java.util.HashMap;
import java.util.LinkedList;

import org.apache.james.mime4j.io.MaxLineLimitException;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.WidgetCollection;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.ui.CSS;

public class AlgebraHistory extends FlowPanel {

	boolean expanded = false;
	public boolean scrolled = false;
	boolean wasTouchMoved = false;
	public boolean isSolved = false;
	private AlgebraHistory that = this;

	public AlgebraHistory() {
		addStyleName(CSS.ALG_OUT);

		if (Moderator.isTouch) {
			this.addDomHandler(new AlgOutTouchMove(), TouchMoveEvent.getType());
			this.addDomHandler(new AlgOutTouchEnd(), TouchEndEvent.getType());
		} else {
			this.addDomHandler(new AlgOutClickHandler(), ClickEvent.getType());
		}

		String goalStr = URLParameters.getParameter(Parameter.goal);
		if (goalStr != null && !"".equals(goalStr)) {
			Label firstRowEq = new HTML("Simplify to: " + goalStr);

			FlowPanel firstRow = new FlowPanel();
			firstRowEq.addStyleName(CSS.ALG_OUT_EQ_ROW);
			firstRow.add(firstRowEq);

			Label firstRowRule = new Label();
			firstRowRule.addStyleName(CSS.ALG_OUT_RULE_ROW);
			firstRow.add(firstRowRule);

			firstRow.addStyleName(CSS.ALG_OUT_ROW);

			add(firstRow);
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		// if (isSolved) {
		// firstRow.getElement().getStyle().clearHeight();
		// } else {
		// firstRow.setHeight(getOffsetHeight() + "px");
		// }

		reset();
	}

	public void updateAlgebraHistory(String changeComment, Skill skill,
			EquationTree mathTree) {

		changeComment = changeComment.replace("lineThrough", "");

		add(new AlgebraHistoryRow(changeComment, skill, mathTree));

		if (changeComment.contains(BothSidesTransformations.UP_ARROW)) {
			add(new AlgebraHistoryRow(changeComment));
		}
		reset();
	}

	public void solvedUpdate(EquationTree mathTree, String evaluation) {
		AlgebraHistoryRow lastRow = new AlgebraHistoryRow(mathTree.getDisplay());
		lastRow.eqSide.getElement().getStyle().setFontSize(300, Unit.PCT);
		lastRow.ruleSide.removeFromParent();
		add(lastRow);

		Label evaluatedBox = new Label();
		JSNICalls.log("ev " + evaluation);
		evaluatedBox.addStyleName(CSS.ALG_OUT_RULE_ROW);
		// evaluatedBox.addFocusHandler(new HighlightHandler(evaluatedBox));
		lastRow.add(evaluatedBox);
		evaluatedBox.setText(evaluation);
	}

	void reset() {
		expanded = false;

		setHeight("100%");

		// Minimize overflow by fitting equation into row
		LinkedList<AlgebraHistoryRow> rows = getRows();
		if (isAttached() && !rows.isEmpty()) {
			AlgebraHistoryRow lastRow = rows.getLast();
			
			int leftWidthMin = 0, rightWidthMin = 0;
			for (AlgebraHistoryRow row : rows) {
				leftWidthMin = Math.max(leftWidthMin,
						row.leftCaseContent.getOffsetWidth());
				rightWidthMin = Math.max(rightWidthMin,
						row.rightCaseContent.getOffsetWidth());
			}
			double maxWidth = lastRow.leftCase.getParentElement()
					.getOffsetWidth() * 0.9;
			double combinedWidthMin = leftWidthMin + rightWidthMin;
			double leftWidth = lastRow.leftCaseContent.getOffsetWidth();
			double rightWidth = lastRow.rightCaseContent.getOffsetWidth();
			if(combinedWidthMin < maxWidth) {// all rows fit
				// Weigh to center
				double exess = maxWidth - leftWidthMin - rightWidthMin;
				leftWidth = leftWidthMin + (exess/2);
				rightWidth = rightWidthMin + (exess/2);
			}else if(leftWidthMin <= rightWidthMin) {
				rightWidth = maxWidth - leftWidth;
			}else if(leftWidthMin > rightWidthMin) {
				leftWidth = maxWidth - rightWidth;
			}

			double combinedWidth = leftWidth + rightWidth;
			for (AlgebraHistoryRow row : rows) {
				Style leftStyle = row.leftCase.getStyle();
				Style rightStyle = row.rightCase.getStyle();
				leftStyle
						.setWidth((leftWidth / (combinedWidth)) * 90, Unit.PCT);
				rightStyle.setWidth((rightWidth / (combinedWidth)) * 90,
						Unit.PCT);
			}
		}

		scrollToBottom();
	}

	void scrollToBottom() {
		getElement()
				.setScrollTop(
						getElement().getScrollHeight()
								- getElement().getClientHeight());
	}

	LinkedList<AlgebraHistoryRow> getRows() {
		LinkedList<AlgebraHistoryRow> rows = new LinkedList<AlgebraHistoryRow>();
		for (Widget row : getChildren()) {
			if (row instanceof AlgebraHistoryRow && !((AlgebraHistoryRow) row).isChangeRow) {
				rows.add((AlgebraHistoryRow) row);
			}
		}
		return rows;
	}

	class AlgebraHistoryRow extends FlowPanel {
		private FlowPanel eqSide = new FlowPanel();
		private Anchor ruleSide = new Anchor();
		boolean isChangeRow = false;
		Element rightCase;
		Element leftCase;
		Element rightCaseContent;
		Element leftCaseContent;

		// Change row
		AlgebraHistoryRow(String changeComment) {
			this(new HTML("<div>" + changeComment + "</div><div></div><div>"
					+ changeComment + "</div>"));

			isChangeRow = true;

			addStyleName(CSS.ALG_OUT_CHANGE_ROW);

		}

		// Equation row
		AlgebraHistoryRow(String changeComment, Skill rule,
				EquationTree mathTree) {
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

			rightCase = DOM.createDiv();
			leftCase = DOM.createDiv();
			rightCaseContent = (Element) rootEl.getChild(2);
			leftCaseContent = (Element) rootEl.getChild(0);
			rightCase.appendChild(rightCaseContent);
			leftCase.appendChild(leftCaseContent);
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

		private AlgebraHistory alg = that;
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
				return;
			} else {
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
				expanded = false;
				reset();
			} else {
				alg.setHeight(maxHeight + "px");
				expanded = true;
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
