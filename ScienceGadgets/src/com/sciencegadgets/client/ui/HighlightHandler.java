package com.sciencegadgets.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.TextArea;

public class HighlightHandler implements FocusHandler {
	final TextArea code;

	public HighlightHandler(TextArea code) {
		this.code = code;
	}

	@Override
	public void onFocus(FocusEvent event) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				code.selectAll();
			}
		});
	}

}