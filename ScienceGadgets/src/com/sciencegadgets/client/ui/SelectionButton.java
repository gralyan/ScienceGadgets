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

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.Moderator;

public abstract class SelectionButton extends SimplePanel implements
		HasClickHandlers, HasTouchEndHandlers {

	FitParentHTML buttonHTML;
	boolean isEnabled = true;

	protected SelectionButton() {
		addStyleName(CSS.TRANSFORMATION_BUTTON);

		if (Moderator.isTouch) {
			addTouchEndHandler(new TouchEndHandler() {
				@Override
				public void onTouchEnd(TouchEndEvent event) {
					if (isEnabled) {
						onSelect();
						event.preventDefault();
						event.stopPropagation();
					}
				}
			});
		} else {
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (isEnabled) {
						onSelect();
						event.preventDefault();
						event.stopPropagation();
					}
				}
			});
		}

	}

	protected SelectionButton(String html) {
		this();
		setHTML(html);
	}

	@Override
	public HandlerRegistration addTouchEndHandler(TouchEndHandler handler) {
		return addDomHandler(handler, TouchEndEvent.getType());
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	abstract protected void onSelect();

	public void resize() {
		buttonHTML.resize();
	}

	public void setHTML(String html) {
		clear();
		if (buttonHTML == null) {
			buttonHTML = new FitParentHTML(html);
		} else {
			buttonHTML.setHTML(html);
		}
		buttonHTML.percentOfParent = 85;
		add(buttonHTML);
	}

	public String getHTML() {
		if (buttonHTML == null) {
			return null;
		} else {
			return buttonHTML.getHTML();
		}
	}

	public void setEnabled(boolean enable) {
		this.isEnabled = enable;
		Style style = getElement().getStyle();
		if (enable) {
			addStyleName(CSS.TRANSFORMATION_BUTTON);
			style.clearColor();
		} else {
			removeStyleName(CSS.TRANSFORMATION_BUTTON);
			style.setColor("gray");
		}
	}

}
