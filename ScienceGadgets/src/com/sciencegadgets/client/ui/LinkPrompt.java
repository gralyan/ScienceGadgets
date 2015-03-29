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

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.LinkElement;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationHTML;

public abstract class LinkPrompt extends Prompt {

	private static LinkPromptUiBinder uiBinder = GWT
			.create(LinkPromptUiBinder.class);

	interface LinkPromptUiBinder extends UiBinder<FlowPanel, LinkPrompt> {
	}

	@UiField
	protected
	FlowPanel upperArea;
	@UiField
	protected
	Label linkCode;
	@UiField
	protected
	Anchor linkDisplay;
	@UiField
	protected
	Label iframeCode;
	@UiField
	FlowPanel iFrameContainer;
	
	protected IFrameElement iframeDisplay = Document.get().createIFrameElement();

	protected HashMap<Parameter, String> pMap;

	public LinkPrompt() {
		add(uiBinder.createAndBindUi(this));

		linkDisplay.setTarget("_blank");
		linkDisplay.removeStyleName("gwt-Anchor");
		linkDisplay.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
		
		iFrameContainer.getElement().appendChild(iframeDisplay);
		
		addOkHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				disappear();
			}
		});
	}

	@Override
	public void appear() {
		

		setMapParameters();
		
		Element linkEl = linkDisplay.getElement();
		linkEl.removeAllChildren();
		linkDisplay.setHTML("link");
		
		updateLinks();

		super.appear();
	};
	
	public abstract void setMapParameters();

	protected void updateLinks() {
		
		String url = "http://sciencegadgets.org/#"
				+ URLParameters.makeTolken(pMap, true);
		linkDisplay.setHref(url);
		
		linkCode.setText(JSNICalls.elementToString(linkDisplay.getElement()).replace("&amp;", "&"));
		
		iframeDisplay.setSrc(url);
		iframeCode.setText(JSNICalls.elementToString(iframeDisplay));
		
		iframeDisplay.getStyle().setWidth(100, Unit.PCT);
		iframeDisplay.getStyle().setHeight(100, Unit.PCT);
	}
}
