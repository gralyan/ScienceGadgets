package com.tumojava.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.tumojava.client.projectfinal.StartScreen;
import com.tumojava.client.projectfinal.johngralyan.InverseTrigGame;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class TumoJava implements EntryPoint {

	@Override
	public void onModuleLoad() {
		RootPanel.get().add(new StartScreen());
	}
}
