package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.FitParentHTML;

public class VideoPanel extends FlowPanel {
	// FitParentHTML playExampleLabel = new FitParentHTML("Play Example");
	static final String cssUrl = "CSStyles/images/intro/";

	public VideoPanel() {
		this.addStyleName(CSS.VIDEO_PANEL + " " + CSS.BORDER_RADIUS_SMALL);

		// playExampleLabel
		// .addStyleName(CSS.PLAY_EXAMPLE + " " + CSS.INTRO_BUTTON);

		addSegment("Example Game", "archery_snapshot_hover.jpeg",
				"https://www.youtube.com/embed/wNYcqj8A1Tg",
				new ArcheryGameHadler());
		addSegment("Make Equation", "archery_snapshot_hover.jpeg",
				"https://www.youtube.com/embed/wNYcqj8A1Tg",
				new MakeEquationHandler());
	}

	void addSegment(String labelStr, String imgSrc, final String videoURL,
			ClickHandler clickHandler) {
		FlowPanel videoSegment = new FlowPanel();
		videoSegment.addStyleName(CSS.VIDEO_SEGMENT);
		Label label = new Label(labelStr);
		Image playButton = new Image(cssUrl + imgSrc);
		playButton.addClickHandler(clickHandler);
		Image videoButton = new Image(cssUrl + "playSign.png");
		videoButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.open(videoURL, "_blank", "");
			}
		});
		label.addStyleName(CSS.VIDEO_SEGMENT_LABEL);
		playButton.addStyleName(CSS.INTRO_BUTTON);
		videoButton.addStyleName(CSS.INTRO_BUTTON);

		add(label);
		videoSegment.add(playButton);
		videoSegment.add(videoButton);
		add(videoSegment);
	}
	
	class ArcheryGameHadler implements ClickHandler{
		@Override
		public void onClick(ClickEvent event) {
			Window.open("www.sciencegadgets.org/examples/archery/archery.html", "_blank", "");
		}
	}
	class MakeEquationHandler implements ClickHandler{
		@Override
		public void onClick(ClickEvent event) {
		}
	}
}
