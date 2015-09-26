package com.sciencegadgets.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.user.client.ui.TextArea;

public class HighlightHandler implements FocusHandler {
	
	public static final String DEFAULT_TEXT = "Get Code";
	final TextArea codeArea;
	String codeText;

	public HighlightHandler(TextArea codeArea) {
		this.codeArea = codeArea;
		codeArea.addStyleName(CSS.LINK_PROMPT_CODE_DEFAULT);
		codeArea.addStyleName(CSS.BORDER_RADIUS_SMALL);
	}

	@Override
	public void onFocus(FocusEvent event) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				codeArea.getElement().getStyle().clearTextAlign();
				codeArea.setText(codeText);
				codeArea.selectAll();
			}
		});
	}
	
	public void revert(String codeText) {
		if(codeText != null || !"".equals(codeText)) {
			this.codeText = codeText;
		}
		codeArea.getElement().getStyle().setTextAlign(TextAlign.CENTER);
		codeArea.setText(DEFAULT_TEXT);
	}

}