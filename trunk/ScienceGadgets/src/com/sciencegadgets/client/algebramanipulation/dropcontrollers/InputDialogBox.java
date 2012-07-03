package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class InputDialogBox extends DialogBox {

	private TextBox textBox;
	private String[] questionAndAnswer;
	private Label feedback;
	private int failCount = 0;

	InputDialogBox(String[] questionAndAnswer) {

		this.setGlassEnabled(true);
		this.setAnimationEnabled(true);
		this.show();
		this.center();

		this.questionAndAnswer = questionAndAnswer;
		this.setText(questionAndAnswer[0]);

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(4);
		this.setWidget(dialogContents);

		// Make input TextBox
		textBox = new TextBox();
		dialogContents.setCellHorizontalAlignment(textBox,
				HasHorizontalAlignment.ALIGN_CENTER);
		textBox.addKeyPressHandler(new KeyPressHandler() {

			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
					inputEnter();
				}
			}
		});
		dialogContents.add(textBox);

		// Add a send button at the bottom of the dialog
		Button sendButton = new Button("Send", new ClickHandler() {
			public void onClick(ClickEvent event) {
				inputEnter();
			}
		});
		dialogContents.setCellHorizontalAlignment(sendButton,
				HasHorizontalAlignment.ALIGN_CENTER);
		dialogContents.add(sendButton);

		feedback = new Label("");
		dialogContents.add(feedback);

		// Set focus to input area
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			public void execute() {
				textBox.setFocus(true);
			}
		});
	}

	private void inputEnter() {
		try {
			float input = Float.parseFloat(textBox.getText());
			float actual = Float.parseFloat(questionAndAnswer[1]);
			float percentDifference = (input - actual) * 100 / actual;

			if (percentDifference < 5 && percentDifference > -5) {
				this.hide();
			} else {
				feedback.setText("Try again " + failCount++);
				textBox.setText("");
			}
		} catch (NumberFormatException e) {
			feedback.setText("Numbers only please");
			textBox.setText("");
		}
	}
}
