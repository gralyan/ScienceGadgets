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

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;

public class FitParentHTML extends HTML implements Resizable, HasFitParentHTML {

	public static final int DEFAULT_PERCENT_OF_PARENT = 95;
	CommunistPanel commPanel = null;

	/**
	 * Percent of the parent to fill. 100 would match the parent.
	 */
	public int percentOfParent = DEFAULT_PERCENT_OF_PARENT;

	public FitParentHTML() {
		this("", DEFAULT_PERCENT_OF_PARENT);
	}

	public FitParentHTML(String html) {
		this(html, DEFAULT_PERCENT_OF_PARENT);
	}

	public FitParentHTML(int percentOfParent) {
		this("", percentOfParent);
	}

	public FitParentHTML(String html, int percentOfParent) {
		super(html);
		this.percentOfParent = percentOfParent;
		addStyleName(CSS.FIT_PARENT_HTML);
	}

	void setPanel(CommunistPanel commPanel) {
		this.commPanel = commPanel;
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		GWT.log("1");
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				GWT.log("2");
				Style style = getElement().getStyle();
				style.setVisibility(Visibility.HIDDEN);
				resize();
				style.clearVisibility();
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
		
		if(this.getParent() == null) {
			GWT.log("-----------");
			return;
		}
		if (commPanel == null) {
			GWT.log("//////////");
			resizeThis(getFontPercent());
			return;
		}

		commPanel.sizes.add(getFontPercent());

		if (commPanel.sizes.size() == commPanel.getWidgetCount()) {
			double smallest = commPanel.sizes.get(0);
			for (double size : commPanel.sizes) {
				smallest = Math.min(size, smallest);
			}
			int count = commPanel.getWidgetCount();
			GWT.log("count "+count);
			for (int i = 0; i < count; i++) {
				Widget member = commPanel.getWidget(i);
				GWT.log("0 ");
				if (member instanceof HasFitParentHTML) {
					FitParentHTML fit = ((HasFitParentHTML) member)
							.getFitParentHTML();
					GWT.log("00 ");
					if (fit != null) {
						GWT.log("000 ");
						fit.resizeThis(smallest);
					}

				}
			}
			commPanel.sizes.clear();
		}

	}

	private double getFontPercent() {

		if (this.getParent() == null) {
			return 100;
		}

		Element htmlElement = this.getElement();
		Style htmlStyle = htmlElement.getStyle();

		htmlStyle.clearPadding();
		htmlStyle.clearFontSize();

		Element parentElement = this.getParent().getElement();

		double widthRatio = (double) parentElement.getOffsetWidth()
				/ (double) htmlElement.getOffsetWidth();
		double heightRatio = (double) parentElement.getOffsetHeight()
				/ (double) htmlElement.getOffsetHeight();

		double smallerRatio = (widthRatio > heightRatio) ? heightRatio
				: widthRatio;

		// *percentOfParent for looser fit, *100 for percent
		return smallerRatio * percentOfParent;

	}

	private void resizeThis(double fontPercent) {
		Widget parent = this.getParent();
		if(parent == null) {
			return;
		}
		Element parentElement = parent.getElement();
		Element htmlElement = this.getElement();
		Style htmlStyle = htmlElement.getStyle();

		htmlStyle.setFontSize((fontPercent), Unit.PCT);

		// Center vertically
		double extraBottom = (double) parentElement.getOffsetHeight()
				- (double) htmlElement.getOffsetHeight();
		htmlStyle.setPaddingTop(extraBottom / 2, Unit.PX);

	}

	@Override
	public FitParentHTML getFitParentHTML() {
		return this;
	}
}
