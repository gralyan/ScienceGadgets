package com.sciencegadgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class NavigationPanelEntry implements EntryPoint {

	private ScrollPanel scrollInTabAbout = new ScrollPanel();
	private ScrollPanel scrollInTabGetIt = new ScrollPanel();
	private ScrollPanel scrollInTabRoadMap = new ScrollPanel();
	private ScrollPanel scrollInTabContact = new ScrollPanel();

	@Override
	public void onModuleLoad() {

		scrollInTabAbout.setWidth("60em");
		scrollInTabGetIt.setWidth("60em");
		scrollInTabRoadMap.setWidth("60em");
		scrollInTabContact.setWidth("60em");

		scrollInTabAbout.setHeight("20em");
		scrollInTabGetIt.setHeight("20em");
		scrollInTabRoadMap.setHeight("20em");
		scrollInTabContact.setHeight("20em");

		TabPanel tp = new TabPanel();
		tp.add(makeAbout(), "About");
		tp.add(makeGetIt(), "Get it");
		tp.add(makeRoadmap(), "Roadmap");
		tp.add(makeContacts(), "Contact");

		// Show the 'bar' tab initially.
		tp.selectTab(0);

		// Add it to the root panel.
		RootPanel.get("navigationArea").add(tp);
	}

	ScrollPanel makeAbout() {
		HTML html = new HTML(//
				"<p>This collection of tools is meant to be used by educational websites as an interactive learning resource. "
						+ "The goal is to provide the right tools for students to focus on the particular skills in question. "
						+ "It should be pieced together and configured appropriately for a specific purpose (e.g. subject, learning level etc).</p>"
						+

						"<p>The collection currently consists of an equation browser, algebra manipulating area, "
						+ "and a graphical representation of the equation as a tree of its components. " 
						+ "Algebra has been the main focus because it is the core prerequisire most students struggle with. "
						+ "The components may be hovered over by mouse (or clicked on in touchscreen devices) "
						+ "in order to display all the possible changes it can make on the equation as hints. "
						+ "Students can explore all these possibilities in their own direction without being force fed one algorithm of solving particular equations. "
						+ "This freedom to play around with an equation would give students the opportunity to direct their own learning while keeping the proper rules of algebra in tact.</p>"
						+

						"<p>In the full featured example, as long as the program doesn't run into a glitch, it would be impossible to get an answer wrong. "
						+ "A struggling student would just take more steps to solve it, providing a chance to collect assessment data in the background "
						+ "while avoiding the disappointment of getting a wrong answer or the frustration of being \"stuck\" on a problem. "
						+ "Possible uses of the data gathered would be grade determination or a reward system "
						+ "whereby mastery of a subject would allow the privilege to skip the mastered step.</p>");
		scrollInTabAbout.add(html);
		return scrollInTabAbout;
	}

	ScrollPanel makeGetIt() {
		VerticalPanel vp = new VerticalPanel();
		Image image = new Image("http://upload.wikimedia.org/wikipedia/commons/c/cd/ASF-logo.svg");
		HTML html = new HTML(//
				"<img href=\"http://upload.wikimedia.org/wikipedia/commons/c/cd/ASF-logo.svg\"></img>" +
				"<p>Science Gadgets is an open source project under an Apache 2.0 liscence. "
						+ "<p>You can find the <a href=\"http://code.google.com/p/sciencegadget/\">source code here</a></p>"
						+ "<p>Once it has matured, I will post a proper way to add the components and configurations so anyone can easily use it on their own site.</p>");
		
		vp.add(image);
		vp.add(html);
		scrollInTabGetIt.add(vp);
		return scrollInTabGetIt;
	}

	ScrollPanel makeRoadmap() {
		VerticalPanel roadmapPanel = new VerticalPanel();
		roadmapPanel
				.add(new HTML(
						"<p>I wrote an Eclipse plug-in that would make a mind map of the source code. "
								+ "It's much easier to navigate, share, brainstorm, experiment and visualize all the code and connections.</p>"
								+ "<p>If you're interested in getting the plug-in please feel free to contact me.</p>"
								+ "<p>You can download the actual map to explore in Freeplane, or view a snapshot image below.</p>"
								+ "<p><a href=\"ScienceGadgets_Roadmap_6-20-2012.jpg\" target=\"_blank\">RoadMap Image</a></p>"
								+ "<p><b><a href=\"ScienceGadgets_6-20-2012.mm\" target=\"_blank\">Download Map</a></b></p>"));

		scrollInTabRoadMap.add(roadmapPanel);
		return scrollInTabRoadMap;
	}

	ScrollPanel makeContacts() {
		Grid grid = new Grid(2,2);
		grid.getCellFormatter().setStyleName(0,0, CSS.ROW_HEADER);
		grid.getCellFormatter().setStyleName(1,0, CSS.ROW_HEADER);
		grid.setWidget(0, 0, new Label("E-mail"));
		grid.setWidget(0, 1, new Label("john.gralyan@gmail.com"));
		grid.setWidget(1, 0, new Label("LinkedIn"));
		grid.setWidget(1, 1, new HTML("<a href=\"http://www.linkedin.com/pub/john-gralyan/52/488/b98\">John Gralyan</a>"));
		
		scrollInTabContact.add(grid);
		return scrollInTabContact;
	}
}
