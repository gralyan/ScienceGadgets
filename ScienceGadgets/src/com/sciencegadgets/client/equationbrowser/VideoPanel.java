package com.sciencegadgets.client.equationbrowser;

import java.util.ArrayList;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.DOM;
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
import com.sciencegadgets.client.ui.Resizable;
import com.sciencegadgets.shared.TypeSGET;

public class VideoPanel extends FlowPanel {
	// FitParentHTML playExampleLabel = new FitParentHTML("Play Example");
	static final String cssUrl = "CSStyles/images/intro/";

	RandomEquationPanel generatePanel = new RandomEquationPanel();
	ConversionSpecification conversionSpec = new ConversionSpecification();
	public MakeEquationBrowser makeEquationBrowser = new MakeEquationBrowser();
	HTMLPanel contactUs = new HTMLPanel(
			"<div style='padding:1em;'>The tools seen here are meant to be as open and easily integratable as possible. You can add an interactive equation anywhere you can add an HTML snippet. The hyperlink or embedable iframe markup can be created using the online equation editor for your convenience. For more information on how to integrate these tools beyond the videos, demos, and <a href=\"https://github.com/gralyan/ScienceGadgets\">Git Hub</a> page, feel free to contact me directly at <a href=\"mailto:John.Gralyan@gmail.com?Subject=Science%20Gadgets%20Inquiry\" target=\"_top\">John.Gralyan@gmail.com</a></div>");

	DetailsButton activeDetailsButton = null;

	ArrayList<FlowPanel> segments = new ArrayList<FlowPanel>();
	DetailsAnimation detailsAnimation = new DetailsAnimation();

	public VideoPanel() {

		this.getElement().setId(CSS.HOME_BROWSER);

		Label title = new Label("ScienceGadgets");
		title.addStyleName(CSS.MAIN_TITLE);
		this.add(title);

		HTML headline = new HTML(
				"A set of <a style='color:royalBlue;' href=\"https://github.com/gralyan/ScienceGadgets\">Open Source</a>"
						+ " educational tools dedicated to helping redesign the next generation of learning strategies");

		headline.addStyleName(CSS.MAIN_HEADLINE);
		this.add(headline);

		addSegment(
				"Interact",
				"Get familiar with equations by experiencing the tranformations naturally",
				"equation_snapshot_icon.png",
				"equation_snapshot_icon_HOVER.png", "#A0C4FF",
				"https://www.youtube.com/embed/ZcX0GenDDrc",
				new InteractiveEquationHandler(), generatePanel, "Generate","Try interacting with a randomly generated equation. You can choose the difficulty of the equation or make it completely random");
		addSegment(
				"Make",
				"Create an equation to play with, show others, or add to your own site",
				"blank_equation_icon.png", "blank_equation_icon_HOVER.jpeg",
				"black", "https://www.youtube.com/embed/kdpSRts2oF8",
				new MakeEquationHandler(), makeEquationBrowser, "Templates","Try making an equation. You can use one of these templates or start with a blank slate");
		addSegment(
				"Convert",
				"Assistance with dimentions so you \"don't forget your units!\"",
				"conversion_snapshot_icon.png",
				"conversion_snapshot_icon_HOVER.jpeg", "#E19E2A",
				"https://www.youtube.com/embed/b3APdESppdg",
				new ConvertExampleHandler(), conversionSpec, "Convert", "Enter a quantity to convert. You can specify a value and units here");
		addSegment(
				"Integrate",
				"Embed equations directly into a website, game, LMS or anywhere else you can add basic HTML",
				"archery_snapshot_icon.jpeg",
				"archery_snapshot_icon_HOVER.jpeg", null,
				"https://www.youtube.com/embed/Gc0Aj3lYq68",
				new ArcheryGameHadler(), contactUs, "Contact","");

	}

	void addSegment(String labelStr, String descriptionStr, String imgSrc,
			String imgHoverSrc, String backColor, final String videoURL,
			ClickHandler clickHandler, Widget activityDetails,
			String detailButtonText, String detailDescriptionStr) {
		SegmentComponent descriptionArea = new SegmentComponent();

		Label label = new Label(labelStr);
		label.addStyleName(CSS.VIDEO_SEGMENT_LABEL);

		Label description = new Label(descriptionStr);

		TryActivityButton tryButton = new TryActivityButton(imgSrc, imgHoverSrc);
		tryButton.addStyleName(CSS.INTRO_BUTTON);
		tryButton.addClickHandler(clickHandler);
		if (backColor == null) {
			tryButton.getElement().getStyle()
					.setProperty("backgroundSize", "cover");
		} else {
			tryButton.getElement().getStyle().setBackgroundColor(backColor);
		}

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

		SegmentComponent introButtons = new SegmentComponent();
		introButtons.add(tryButton);
		introButtons.add(videoButton);

		descriptionArea.add(label);
		descriptionArea.add(description);

		VideoSegment videoSegment = new VideoSegment(descriptionArea,
				introButtons, detailDescriptionStr);
		this.add(videoSegment);

		if (activityDetails != null) {
			// FlowPanel detailsArea = new FlowPanel();
			// detailsArea.addStyleName(CSS.SEGMENT_DETAILS);
			// detailsArea.add(activityDetails);
			// this.add(detailsArea);

			// activityDetails.addStyleName(CSS.FILL_PARENT);
			activityDetails.getElement().getStyle().clearWidth();
			activityDetails.getElement().getStyle().clearHeight();
			activityDetails.addStyleName(CSS.SEGMENT_DETAILS);
			this.add(activityDetails);
			DetailsButton detailsButton = new DetailsButton(detailButtonText,
					activityDetails, videoSegment);
			descriptionArea.add(detailsButton);

		}
	}

	class SegmentComponent extends FlowPanel implements Resizable {
		
		public SegmentComponent() {
			super();
			addStyleName(CSS.INTRO_SEGMENT_COMPONENT);
		}
		@Override
		protected void onAttach() {
			Moderator.resizables.add(this);
			resize();
			super.onAttach();
		}

		@Override
		protected void onDetach() {
			Moderator.resizables.remove(this);
			super.onDetach();
		}

		@Override
		public void resize() {
			String minStyle = CSS.INTRO_SEGMENT_COMPONENT_BLOCK;
			if (Window.getClientWidth() < 500) {
				addStyleName(minStyle);
			} else {
				removeStyleName(minStyle);
			}
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
					style.setBackgroundImage("url('" + cssUrl + imgHoverSrc
							+ "')");
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

	class VideoSegment extends FlowPanel {
		SegmentComponent descriptionArea;
		SegmentComponent buttonsArea;
		SegmentComponent detailsDescriptionArea;

		public VideoSegment(SegmentComponent descriptionArea,
				SegmentComponent buttonsArea, String detailDescriptionStr) {
			this.descriptionArea = descriptionArea;
			this.buttonsArea = buttonsArea;
			add(descriptionArea);
			add(buttonsArea);
			
			detailsDescriptionArea = new SegmentComponent();
			detailsDescriptionArea.add(new Label(detailDescriptionStr));
			detailsDescriptionArea.setVisible(false);
			add(detailsDescriptionArea);

			addStyleName(CSS.VIDEO_SEGMENT);
			segments.add(this);
		}

		void switchMode(boolean toDetails) {
			buttonsArea.setVisible(!toDetails);
			detailsDescriptionArea.setVisible(toDetails);
			String blockStyle = CSS.INTRO_SEGMENT_COMPONENT_BLOCK;
			if (toDetails) {
//				descriptionArea.setWidth("98%");
//				descriptionArea.addStyleName(blockStyle);
			} else {
//				descriptionArea.resize();
//				descriptionArea.getElement().getStyle().clearWidth();
			}
		}
	}

	class DetailsButton extends Label {
		Style detailsAreaStyle;
		String defaultText = "";
		VideoSegment segmentParent;

		DetailsButton(String title, Widget detailsArea,
				VideoSegment segmentParent) {
			super(title);
			this.segmentParent = segmentParent;
			defaultText = title;
			this.detailsAreaStyle = detailsArea.getElement().getStyle();
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
			segmentParent.switchMode(true);
			setText("Go Back");
			detailsAreaStyle.setPosition(Style.Position.RELATIVE);
			detailsAreaStyle.setVisibility(Style.Visibility.VISIBLE);
			activeDetailsButton = this;
			setFocus(true);
		}

		void unselect() {
			segmentParent.switchMode(false);
			setText(defaultText);
			setFocus(false);
		}

		void setFocus(boolean isFocus) {
			detailsAnimation.init(segmentParent, isFocus, detailsAreaStyle);
			detailsAnimation.run(500);
		}
	}

	class DetailsAnimation extends Animation {
		VideoSegment segmentParent;
		private boolean toDetails;
		Style detailsAreaStyle;
		private int height;
		ArrayList<Style> otherSegmentStyles = new ArrayList<Style>();

		void init(VideoSegment segmentParent, boolean toDetails,
				Style detailsAreaStyle) {
			this.segmentParent = segmentParent;
			this.toDetails = toDetails;
			this.detailsAreaStyle = detailsAreaStyle;
		}

		@Override
		protected void onStart() {
			if (toDetails) {
				otherSegmentStyles.clear();
				height = segmentParent.getOffsetHeight();
				for (FlowPanel otherSegment : segments) {
					if (otherSegment != segmentParent) {
						Style otherSegmentStyle = otherSegment.getElement().getStyle();
						otherSegmentStyles.add(otherSegmentStyle);
						otherSegmentStyle.setHeight(height, Unit.PX);
						otherSegmentStyle.setVisibility(Visibility.HIDDEN);
						otherSegmentStyle.setMargin(0, Unit.PX);
					}
				}
			}
			super.onStart();
		}

		@Override
		protected void onUpdate(double progress) {
			double prog = toDetails ? 1 - progress : progress;
			double revProg = 1 - prog;
			for (Style otherSegment : otherSegmentStyles) {
					otherSegment.setHeight(height * prog, Unit.PX);
			}
			detailsAreaStyle.setProperty("height", revProg * 60 + "vh");

		}

		@Override
		protected void onComplete() {
			super.onComplete();
			if (!toDetails) {
				for (Style otherSegment : otherSegmentStyles) {
						otherSegment.clearHeight();
						otherSegment.clearVisibility();
						otherSegment.clearMargin();
				}

				detailsAreaStyle.clearVisibility();
				detailsAreaStyle.clearPosition();
			}
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
