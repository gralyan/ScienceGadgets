package com.sciencegadgets.client.algebra.edit;

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
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.ToggleSlide;

public class LinkPrompt extends Prompt {

	private static LinkPromptUiBinder uiBinder = GWT
			.create(LinkPromptUiBinder.class);

	interface LinkPromptUiBinder extends UiBinder<FlowPanel, LinkPrompt> {
	}

	@UiField
	FlowPanel params;
	@UiField
	Label linkCode;
	@UiField
	Anchor linkDisplay;
	@UiField
	Label iframeCode;
	@UiField
	FlowPanel iFrameContainer;
	
	IFrameElement iframeDisplay = Document.get().createIFrameElement();

	final ToggleSlide easyToggle = new ToggleSlide("Normal", "Easy", true,
			new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					ToggleSlide toggle = (ToggleSlide) event.getSource();
					if (toggle.isFistSelected()) {
						pMap.put(Parameter.easy, URLParameters.TRUE);
					} else {
						pMap.remove(Parameter.easy);
					}

					updateLinks();
				}
			});

	HashMap<Parameter, String> pMap;
	EquationHTML html;
	AlgebraActivity algebraActivity;

	public LinkPrompt(AlgebraActivity algebraActivity) {
		this.algebraActivity = algebraActivity;
		add(uiBinder.createAndBindUi(this));

		params.add(easyToggle);
		linkDisplay.setTarget("_blank");
		linkDisplay.removeStyleName("gwt-Anchor");
		linkDisplay.getElement().getStyle().setTextDecoration(TextDecoration.NONE);
		
		iFrameContainer.getElement().appendChild(iframeDisplay);
	}

	void updateLinks() {
		
		//<link type="text/css" rel="stylesheet" href="equation.css">
		String url = "http://sciencegadgets.org/#"
				+ URL.encodePathSegment(URLParameters.makeTolken(pMap));
		
		
		html = algebraActivity.getEquationTree()
				.getDisplay();
//		linkDisplay.setHTML(html.getHTML());
		
		Element styleLink = new HTML("<link type=\"text/css\" rel=\"stylesheet\" href=\"http://sciencegadgets.org/CSStyles/equation.css\"></link>").getElement();
		styleLink.appendChild(html.getElement());
		linkDisplay.getElement().appendChild(styleLink);
		
		linkDisplay.setHref(url);
		linkCode.setText(JSNICalls.elementToString(linkDisplay.getElement()));
		iframeDisplay.setSrc(url);
		iframeDisplay.setName("Interactive Equation");
		iframeDisplay.getStyle().setWidth(100, Unit.PCT);
		iframeDisplay.getStyle().setHeight(100, Unit.PCT);
		
		iframeCode.setText(JSNICalls.elementToString(iframeDisplay));
	}

	@Override
	public void appear() {
		String eqString = URLParameters.getParameter(Parameter.equation);

		pMap = URLParameters.getParameterMap();
		pMap.put(Parameter.equation, eqString);
		pMap.put(Parameter.activity, ActivityType.algebrasolve.toString());

		updateLinks();

		super.appear();
	};
}
