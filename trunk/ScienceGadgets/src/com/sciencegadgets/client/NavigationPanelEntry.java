package com.sciencegadgets.client;

import java.util.Arrays;
import java.util.List;

import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabPanel;

public class NavigationPanelEntry implements EntryPoint {

	private static final List<String> navigationIcons = Arrays.asList("About",
			"Get ScienceGadgets", "Roadmap");

	@Override
	public void onModuleLoad() {

		// TextCell textCell = new TextCell();
		// CellList<String> navigationList = new CellList<String>(textCell);
		//
		// navigationList.setRowCount(navigationIcons.size());
		// navigationList.setRowData(navigationIcons);
		// navigationList.setStyleName("navIcon");
		//
		// RootPanel.get("navigationArea").add(navigationList);

		TabPanel tp = new TabPanel();
		tp.add(makeAbout(), "About");
		tp.add(makeGetIt() , "Get it");
		tp.add(new Image("ScienceGadgets_Roadmap_6-20-2012.jpg"), "Roadmap");
		tp.add(new HTML("john.gralyan@gmail.com"), "Contact");

		// Show the 'bar' tab initially.
		tp.selectTab(0);

		// Add it to the root panel.
		RootPanel.get("navigationArea").add(tp);
	}

	HTML makeAbout() {
		return new HTML("");
	}

	HTML makeGetIt() {
		return new HTML(//
		"<p>Science Gadgets is an open source project under an Apache 2.0 liscence. You can find the <a href=\"source code here\"></a></p>"+ 
				"<p>Since Science Gadgets are still ver early in development, it would not be wise to link directly to the demo above. Once it has matured, I will post the HTML code and configurations that anyone can easily put on their own site.</p>");
	}

}
