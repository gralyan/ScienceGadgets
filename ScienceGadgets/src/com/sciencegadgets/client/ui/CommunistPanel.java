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

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Automatically gives even spacing to all the children and fills this panel
 * completely
 * 
 * 
 */
public class CommunistPanel extends FlowPanel {

	boolean isHorizontal = false;
	ArrayList<Double> sizes = new ArrayList<Double>(); 

	public CommunistPanel() {
		this(true);
	}

	public CommunistPanel(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	public void addAll(Widget[] widgets) {
		for (Widget widget : widgets) {
			addComrade(widget);
		}
		redistribute();
	}

	public void addAll(Iterable<? extends Widget> widgets) {
		for (Widget widget : widgets) {
			addComrade(widget);
		}
		redistribute();
	}

	/**
	 * When adding iteratively, consider using the an addAll method, especially
	 * when adding a FitParentHTML
	 */
	@Override
	public void add(Widget widget) {
		addComrade(widget);
		redistribute();
	}

	private void addComrade(Widget widget) {
		Widget container = widget;
		if(widget instanceof FitParentHTML) {
			FitParentHTML w = (FitParentHTML) widget;
			container = new HasFitParentHTML.FitParentContainer(w);
			w.setPanel(this);
		}else if (widget instanceof HasFitParentHTML) {
			HasFitParentHTML w = (HasFitParentHTML) widget;
			FitParentHTML fit = w.getFitParentHTML();
			fit.setPanel(this);
		}
		if (isHorizontal) {
			container.addStyleName(CSS.LAYOUT_ROW);
		}
		super.add(container);
	}

	public void clear() {
		int count = getWidgetCount();
		for (int i = 0; i < count; i++) {
			getWidget(0).removeFromParent();
		}
	}

	protected void redistribute() {
		int count = this.getWidgetCount();
		if (count <= 0) {
			return;
		}
		int portion = 100 / count;
		for (int i = 0; i < count; i++) {
			Widget member = getWidget(i);
			if (isHorizontal) {
				member.setWidth(portion + "%");
				member.setHeight("100%");
			} else {
				member.setHeight(portion + "%");
				member.setWidth("100%");
			}
		}
	}

}
