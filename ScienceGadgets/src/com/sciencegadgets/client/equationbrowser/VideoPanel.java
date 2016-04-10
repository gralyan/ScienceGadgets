package com.sciencegadgets.client.equationbrowser;

import java.util.ArrayList;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
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

		addSegment("Integrate",
				"Embed equations directly into a website, game, LMS or anywhere else you can add basic HTML",
				"archery_snapshot_icon.jpeg",
				"archery_snapshot_icon_HOVER.jpeg", null,
				"https://www.youtube.com/embed/Gc0Aj3lYq68",
				new ArcheryGameHadler(), null, null);
		addSegment("Make",
				"Create an equation to play with, show others, or add to your own site",
				"blank_equation_icon.png", "blank_equation_icon_HOVER.jpeg",
				"black", "https://www.youtube.com/embed/kdpSRts2oF8",
				new MakeEquationHandler(), makeEquationBrowser, "Templates");
		addSegment(
				"Interact",
				"Get familiar with equations by experiencing the tranformations naturally",
				"equation_snapshot_icon.png",
				"equation_snapshot_icon_HOVER.png", "#A0C4FF",
				"https://www.youtube.com/embed/ZcX0GenDDrc",
				new InteractiveEquationHandler(), generatePanel, "Generate");
		addSegment(
				"Convert",
				"Assistance with dimentions so you \"don't forget your units!\"",
				"conversion_snapshot_icon.png",
				"conversion_snapshot_icon_HOVER.jpeg", "#E19E2A",
				"https://www.youtube.com/embed/b3APdESppdg",
				new ConvertExampleHandler(), conversionSpec, "Convert");

	}

	void addSegment(String labelStr, String descriptionStr, String imgSrc,
			String imgHoverSrc, String backColor, final String videoURL,
			ClickHandler clickHandler, Widget activityDetails,
			String detailButtonText) {
		SegmentComponent descriptionArea = new SegmentComponent();
		descriptionArea.addStyleName(CSS.INTRO_SEGMENT_COMPONENT);

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
		introButtons.addStyleName(CSS.INTRO_SEGMENT_COMPONENT);
		introButtons.add(tryButton);
		introButtons.add(videoButton);

		descriptionArea.add(label);
		descriptionArea.add(description);

		VideoSegment videoSegment = new VideoSegment(descriptionArea,
				introButtons);
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

			GWT.log("");
			GWT.log(labelStr);
			GWT.log("decs " + description.getAbsoluteTop() + " + "
					+ description.getOffsetHeight());
			GWT.log("butTop: " + detailsButton.getAbsoluteTop());
		}
	}

	class SegmentComponent extends FlowPanel implements Resizable {
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
			if (Window.getClientWidth() < 400) {
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

		public VideoSegment(SegmentComponent descriptionArea,
				SegmentComponent buttonsArea) {
			this.descriptionArea = descriptionArea;
			this.buttonsArea = buttonsArea;
			add(descriptionArea);
			add(buttonsArea);

			segments.add(this);
			addStyleName(CSS.VIDEO_SEGMENT);
		}

		void switchMode(boolean toDetails) {
			buttonsArea.setVisible(!toDetails);
			if (toDetails) {
				descriptionArea.setWidth("98%");
			} else {
				descriptionArea.getElement().getStyle().clearWidth();
			}
		}
	}

	class DetailsButton extends Label {
		Style detailsAreaStyle;
		Style style;
		String defaultText = "";
		VideoSegment segmentParent;

		DetailsButton(String title, Widget detailsArea,
				VideoSegment segmentParent) {
			super(title);
			this.segmentParent = segmentParent;
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
			segmentParent.switchMode(true);
			setText("Go Back");
			style.setBackgroundColor("black");
			detailsAreaStyle.setPosition(Style.Position.RELATIVE);
			detailsAreaStyle.setVisibility(Style.Visibility.VISIBLE);
			activeDetailsButton = this;
			setFocus(true);
		}

		void unselect() {
			segmentParent.switchMode(false);
			style.clearBackgroundColor();
			setText(defaultText);
			detailsAreaStyle.clearVisibility();
			detailsAreaStyle.clearPosition();
			setFocus(false);
		}

		void setFocus(boolean isFocus) {
			detailsAnimation.init(segmentParent, isFocus,detailsAreaStyle);
			 detailsAnimation.run(500);
			// for (FlowPanel otherSegment : segments) {
			// if (otherSegment != segmentParent) {
			// otherSegment.setVisible(!isFocus);
			// }
			// }
		}
	}

	class DetailsAnimation extends Animation {
		VideoSegment segmentParent;
		private boolean toDetails;
		Style detailsAreaStyle;
		private int height;
		private static final String px = "px";

		void init(VideoSegment segmentParent, boolean toDetails, Style detailsAreaStyle) {
			this.segmentParent = segmentParent;
			this.toDetails = toDetails;
			this.detailsAreaStyle = detailsAreaStyle;
		}

		@Override
		protected void onStart() {
			super.onStart();
			if (toDetails) {
				height = segmentParent.getOffsetHeight();
				for (FlowPanel otherSegment : segments) {
					if (otherSegment != segmentParent) {
						otherSegment.setHeight(height + px);
						for (int i = 0; i < otherSegment.getWidgetCount(); i++) {
							otherSegment.getWidget(i).setVisible(false);
						}
					}
				}
			}
		}

		@Override
		protected void onUpdate(double progress) {
			double prog = toDetails ? 1 - progress : progress;
			double revProg = 1-prog;
			for (FlowPanel otherSegment : segments) {
				if (otherSegment != segmentParent) {
					otherSegment.setHeight((height * prog) + px);
				}
			}
			detailsAreaStyle.setProperty("height",revProg*60+"vh");
			
		}

		@Override
		protected void onComplete() {
			super.onComplete();
			if (!toDetails) {
			for (FlowPanel otherSegment : segments) {
				if (otherSegment != segmentParent) {
					otherSegment.getElement().getStyle().clearHeight();
					for (int i = 0; i < otherSegment.getWidgetCount(); i++) {
							otherSegment.getWidget(i).setVisible(true);
					}
				}
			}
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
