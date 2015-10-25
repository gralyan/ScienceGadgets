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

import com.google.common.util.concurrent.AbstractScheduledService.Scheduler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.shared.TypeSGET;

public abstract class LinkPrompt extends Prompt {

	private static LinkPromptUiBinder uiBinder = GWT
			.create(LinkPromptUiBinder.class);

	interface LinkPromptUiBinder extends UiBinder<FlowPanel, LinkPrompt> {
	}

	@UiField
	protected FlowPanel upperArea;
	@UiField
	protected TextArea urlCode;
	@UiField
	protected TextArea linkCode;
	@UiField
	protected Anchor linkDisplay;
	@UiField
	protected TextArea iframeCode;
	@UiField
	FlowPanel iFrameContainer;

	@UiField
	protected Label colorLabel;
	@UiField
	protected TextBox colorTextBox;
	@UiField
	protected FlowPanel colorInputArea;
	@UiField
	protected Label widthLabel;
	@UiField
	protected TextBox widthTextBox;
	@UiField
	protected ValueListBox<String> widthUnits;
	@UiField
	protected Label heightLabel;
	@UiField
	protected TextBox heightTextBox;
	@UiField
	protected ValueListBox<String> heightUnits;

	protected IFrameElement iframeDisplay = Document.get()
			.createIFrameElement();

	private HighlightHandler urlHandler;
	private HighlightHandler linkHandler;
	private HighlightHandler iframeHandler;
	
	protected static String initialColor;

	public LinkPrompt() {
		add(uiBinder.createAndBindUi(this));

		linkDisplay.setTarget("_blank");
		linkDisplay.removeStyleName("gwt-Anchor");
		linkDisplay.getElement().getStyle()
				.setTextDecoration(TextDecoration.NONE);

		iFrameContainer.getElement().appendChild(iframeDisplay);

		okClickedOnEnterPressed = false;
		addOkHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				disappear();
			}
		});

		urlHandler = new HighlightHandler(urlCode);
		linkHandler = new HighlightHandler(linkCode);
		iframeHandler = new HighlightHandler(iframeCode);
		urlCode.addFocusHandler(urlHandler);
		linkCode.addFocusHandler(linkHandler);
		iframeCode.addFocusHandler(iframeHandler);


	}

	public void setColor(String hexColor) {
		Style labelStyle = colorLabel.getElement().getStyle();
		if (hexColor == null || hexColor.length() != 6) {
			if ("".equals(hexColor)) {
				labelStyle.clearColor();
				labelStyle.clearFontWeight();
			} else {
				labelStyle.setColor("red");
				labelStyle.setFontWeight(FontWeight.BOLD);
			}
			HashMap<Parameter, String> pMap = setMapParameters();
			pMap.remove(Parameter.color);
			updateLinks(pMap);
			return;
		}
		try {
			Integer.valueOf(hexColor.substring(0, 2), 16);
			Integer.valueOf(hexColor.substring(2, 4), 16);
			Integer.valueOf(hexColor.substring(4, 6), 16);
		} catch (NumberFormatException e) {
			labelStyle.setColor("red");
			labelStyle.setFontWeight(FontWeight.BOLD);
			return;
		}

		labelStyle.clearColor();
		labelStyle.setFontWeight(FontWeight.BOLD);

//		HashMap<Parameter, String> algActivityMap = URLParameters
//				.getParameterMap();
//		algActivityMap.put(Parameter.themecolor, hexColor);
//		URLParameters.setParameters(algActivityMap, false);
		URLParameters.addParameter(Parameter.color, hexColor, true);
		initialColor = hexColor;

		
		HashMap<Parameter, String> pMap = setMapParameters();
		pMap.put(Parameter.color, hexColor);
		updateLinks(pMap);
	}

	@Override
	public void appear() {

		HashMap<Parameter, String> pMap = setMapParameters();

		Element linkEl = linkDisplay.getElement();
		linkEl.removeAllChildren();
		linkDisplay.setHTML("link");

		updateLinks(pMap);

		super.appear();
	};

	public abstract HashMap<Parameter, String> setMapParameters();

	protected void updateLinks(HashMap<Parameter, String> pMap) {

		String url = URLParameters.URL_TOP + "/#"
				+ URLParameters.makeTolken(pMap, true);

		urlHandler.revert(url);

		linkDisplay.setHref(url);

		linkHandler.revert(JSNICalls
				.elementToString(linkDisplay.getElement())
				.replace("&amp;", "&")
				.replace(TypeSGET.Operator.DOT.getSign(),
						TypeSGET.Operator.DOT.getHTML()));

		iframeDisplay.setSrc(url);
		updateIframeCode();

	}

	protected void updateIframeCode() {

		Style iframeStyle = iframeDisplay.getStyle();

		if (isValidSpec(widthLabel, widthTextBox, widthUnits)) {
			double value = Double.parseDouble(widthTextBox.getValue());
			String unit = widthUnits.getValue();
			iframeStyle.setWidth(value, Unit.valueOf(unit));
		} else {
			iframeStyle.setWidth(100, Unit.PCT);
		}
		if (isValidSpec(heightLabel, heightTextBox, heightUnits)) {
			double value = Double.parseDouble(heightTextBox.getValue());
			String unit = heightUnits.getValue();
			iframeStyle.setHeight(value, Unit.valueOf(unit));
		} else {
			iframeStyle.setHeight(100, Unit.PCT);
		}
		String iframeCode = JSNICalls.elementToString(iframeDisplay).replace(
				"&amp;", "&");
		iframeHandler.revert(iframeCode);
	}

	boolean isValidSpec(Label label, TextBox textBox,
			ValueListBox<String> listBox) {
		Style labelStyle = label.getElement().getStyle();

		if ("".equals(textBox.getValue()) && listBox.getValue() == null) {
			labelStyle.clearColor();
			labelStyle.clearFontWeight();
			return false;
		}

		boolean isValid;
		try {
			Double value = Double.parseDouble(textBox.getValue());
			if (value > 0 && listBox.getValue() != null) {
				isValid = true;
			} else {
				isValid = false;
			}
		} catch (NumberFormatException e) {
			isValid = false;
		}

		if (isValid) {
			labelStyle.clearColor();
			labelStyle.setFontWeight(FontWeight.BOLD);
		} else {
			labelStyle.setColor("red");
			labelStyle.setFontWeight(FontWeight.BOLD);
		}
		return isValid;
	}

}
