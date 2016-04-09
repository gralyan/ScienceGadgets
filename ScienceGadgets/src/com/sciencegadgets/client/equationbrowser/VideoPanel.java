package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.TypeSGET;

public class VideoPanel extends FlowPanel {
	// FitParentHTML playExampleLabel = new FitParentHTML("Play Example");
	static final String cssUrl = "CSStyles/images/intro/";

	FlowPanel browser = new FlowPanel();

	RandomEquationPanel generatePanel = new RandomEquationPanel();
	ConversionSpecification conversionSpec = new ConversionSpecification();
	public MakeEquationBrowser makeEquationBrowser = new MakeEquationBrowser();

	DetailsButton activeDetailsButton = null;

	public VideoPanel() {

		this.getElement().setId(CSS.HOME_BROWSER);

		Label title = new Label("ScienceGadgets");
		title.addStyleName(CSS.MAIN_TITLE);
		this.add(title);

		HTML headline = new HTML(
				"A set of <a href=\"https://github.com/gralyan/ScienceGadgets\">Open Source</a>"
						+ " educational tools dedicated to helping redesign the next generation of learning strategies");
		// Label headline = new Label("sdgh");
		headline.addStyleName(CSS.MAIN_HEADLINE);
		this.add(headline);

		browser.addStyleName(CSS.VIDEO_PANEL + " " + CSS.BORDER_RADIUS_SMALL);
		this.add(browser);

		addSegment("Integration",
				"Embed equations directly into a website, game, LMS...",
				"archery_snapshot_icon.jpeg",
				"archery_snapshot_icon_HOVER.jpeg",
				"https://www.youtube.com/embed/Gc0Aj3lYq68",
				new ArcheryGameHadler(), null, null);
		addSegment("Make", "Create an equation to share with others",
				"blank_equation_icon.png",
				"blank_equation_icon_HOVER.jpeg",
				"https://www.youtube.com/embed/kdpSRts2oF8",
				new MakeEquationHandler(), makeEquationBrowser, "Templates");
		addSegment(
				"Interact",
				"Learn algebraic rules naturally while focusing class time on realistic applications",
				"equation_snapshot_icon.png",
				"equation_snapshot_icon_HOVER.png",
				"https://www.youtube.com/embed/ZcX0GenDDrc",
				new InteractiveEquationHandler(), generatePanel, "Generate");
		addSegment(
				"Convert",
				"Assistance with dimentions so you \"don't forget your units!\"",
				"conversion_snapshot_icon.png",
				"conversion_snapshot_icon_HOVER.jpeg",
				"https://www.youtube.com/embed/b3APdESppdg",
				new ConvertExampleHandler(), conversionSpec, "Convert");
	}

	void addSegment(String labelStr, String descriptionStr, String imgSrc,String imgHoverSrc,
			final String videoURL, ClickHandler clickHandler,
			Widget activityDetails, String detailButtonText) {
		FlowPanel videoSegment = new FlowPanel();
		videoSegment.addStyleName(CSS.VIDEO_SEGMENT);
		FlowPanel videoArea = new FlowPanel();
		videoArea.addStyleName(CSS.INTRO_VIDEO_AREA);

		Label label = new Label(labelStr);
		label.addStyleName(CSS.VIDEO_SEGMENT_LABEL);

		Label description = new Label(descriptionStr);

		TryActivityButton tryButton = new TryActivityButton(imgSrc, imgHoverSrc);
		tryButton.addStyleName(CSS.INTRO_BUTTON);
		tryButton.addClickHandler(clickHandler);

		// IFrameElement videoIFrame = Document.get()
		// .createIFrameElement();
		// videoIFrame.setFrameBorder(0);
		// videoIFrame.setSrc(videoURL);
		// videoIFrame.setAttribute("allowfullscreen", "");
		// videoIFrame.addClassName(CSS.FILL_PARENT);
		// SimplePanel videoIFramePanel = new SimplePanel();
		// videoIFramePanel.addStyleName(CSS.INTRO_BUTTON + " "
		// + CSS.INTRO_VIDEO_BUTTON);
		// videoIFramePanel.getElement().appendChild(videoIFrame);
		FocusPanel videoButton = new FocusPanel();
		videoButton.addStyleName(CSS.INTRO_BUTTON + " "
				+ CSS.INTRO_VIDEO_BUTTON);
		videoButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(videoURL, "_blank", "");
			}
		});

		videoSegment.add(videoArea);
		videoArea.add(label);
		videoArea.add(description);
		// videoSegment.add(videoIFramePanel);
		videoSegment.add(tryButton);
		videoSegment.add(videoButton);
		browser.add(videoSegment);

		if (activityDetails != null) {
			FlowPanel detailsArea = new FlowPanel();
			detailsArea.addStyleName(CSS.SEGMENT_DETAILS);
			detailsArea.add(activityDetails);
			browser.add(detailsArea);
			DetailsButton detailsButton = new DetailsButton(detailButtonText,
					detailsArea);
			videoArea.add(detailsButton);
		}
	}

	class TryActivityButton extends FocusPanel {
		Style style;

		TryActivityButton(final String imgSrc, final String imgHoverSrc) {
			style = getElement().getStyle();
			style.setBackgroundImage("url('" + cssUrl + imgSrc + "')");

			addMouseOverHandler(new MouseOverHandler() {
				@Override
				public void onMouseOver(MouseOverEvent event) {
					style.setBackgroundImage("url('" + cssUrl + imgHoverSrc + "')");
				}
			});
			
			addMouseOutHandler(new MouseOutHandler() {

				@Override
				public void onMouseOut(MouseOutEvent event) {
					style.setBackgroundImage("url('" + cssUrl + imgSrc + "')");
				}
			});
		}
	}

	class DetailsButton extends Label {
		Style detailsAreaStyle;
		Style style;
		String defaultText = "";

		DetailsButton(String title, FlowPanel detailsArea) {
			super(title);
			defaultText = title;
			this.detailsAreaStyle = detailsArea.getElement().getStyle();
			this.style = getElement().getStyle();
			addStyleName(CSS.SEGMENT_DETAILS_BUTTON);

			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					click();
				}
			});
		}

		void click() {
			if (activeDetailsButton == this) {
				unselect();
				activeDetailsButton = null;
			} else {
				if (activeDetailsButton != null) {
					activeDetailsButton.unselect();
				}
				select();
			}
		}

		void select() {
			setText("Hide");
			style.setBackgroundColor("black");
			detailsAreaStyle.setVisibility(Visibility.VISIBLE);
			activeDetailsButton = this;
		}

		void unselect() {
			style.clearBackgroundColor();
			;
			setText(defaultText);
			detailsAreaStyle.clearVisibility();
		}

	}

	class ArcheryGameHadler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			Window.open("examples/archery/archery.html", "_blank", "");
		}
	}

	class InteractiveEquationHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			GenerateRandomEquationButton gen = new GenerateRandomEquationButton(
					null);
			URLParameters.addParameter(Parameter.color, "A0C4FF", true);
			gen.onSelect();
		}
	}

	class MakeEquationHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			EquationTree blankEq = new EquationTree(TypeSGET.Variable,
					TypeSGET.NOT_SET, TypeSGET.Variable, TypeSGET.NOT_SET, true);
			Moderator.switchToAlgebra(blankEq,
					Moderator.ActivityType.editequation, true);
		}
	}

	class ConvertExampleHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			Window.open(
					"http://sciencegadgets.org/#activity=interactiveequation&equation=%3Ce%3E%3Cv%20u%E2%89%88%22Energy%5E1%22%20%3EE%5Bv%5D%3Co%3E%E2%89%88%5Bo%5D%3Cs%3E%3Cn%20v%E2%89%88%221%22%20u%E2%89%88%22Mass_kg%5E1*Length_m%5E2*Time_s%5E-2%22%20%3E1%5Bn%5D%3Co%3E%E2%9E%95%5Bo%5D%3Cn%20v%E2%89%88%221%22%20u%E2%89%88%22Energy_J%5E1%22%20%3E1%5Bn%5D%3Co%3E%E2%9E%95%5Bo%5D%3Cn%20v%E2%89%88%221%22%20u%E2%89%88%22Energy_cal%5E1%22%20%3E1%5Bn%5D%5Bs%5D%5Be%5D&color=e19e2a",
					"_blank", "");
		}
	}
}
