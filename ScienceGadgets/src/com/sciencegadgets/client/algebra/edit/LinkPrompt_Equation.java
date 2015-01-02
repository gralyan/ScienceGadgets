package com.sciencegadgets.client.algebra.edit;

import java.util.HashMap;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.ui.LinkPrompt;
import com.sciencegadgets.client.ui.ToggleSlide;

public class LinkPrompt_Equation extends LinkPrompt {

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

	EquationHTML html;
	AlgebraActivity algebraActivity;

	public LinkPrompt_Equation(AlgebraActivity algebraActivity) {
		super();
		this.algebraActivity = algebraActivity;
//		params.add(easyToggle);
	}

	public void setMapParameters() {
		String eqString = URLParameters.getParameter(Parameter.equation);

		pMap = new HashMap<Parameter, String>();
		pMap.put(Parameter.equation, eqString);
		pMap.put(Parameter.activity, ActivityType.algebrasolve.toString());
		
	}
	protected void updateLinks() {
		
		//Replaces link text with equation HTML
		
		html = algebraActivity.getEquationTree()
				.getDisplay();
//		linkDisplay.setHTML(html.getHTML());
		
		Element styleLink = new HTML("<link type=\"text/css\" rel=\"stylesheet\" href=\"http://sciencegadgets.org/CSStyles/equation.css\"></link>").getElement();
		styleLink.appendChild(html.getElement());
		Element linkEl = linkDisplay.getElement();
		linkEl.removeAllChildren();
		linkEl.appendChild(styleLink);
		
		iframeDisplay.setName("Interactive Equation");
		
		super.updateLinks();
	}

}