package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.KeyPadNumerical;
import com.sciencegadgets.client.SymbolDisplay;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.algebra.MathTree;

public class LogBaseSpecification extends Prompt {
	public final String[] bases = { "2", "10", MathTree.E };
	private SymbolDisplay symbolDisplay;
	KeyPadNumerical keyPad = new KeyPadNumerical(true);

	public LogBaseSpecification() {
		super();
		symbolDisplay = keyPad.getSymbolDisplay();
		add(new Label("What base?"));
		add(symbolDisplay);
		
		ClickHandler baseClick = new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				onSpecify(((Button) event.getSource()).getText());
			}
		};

		for (String base : bases) {
			Button baseButton = new Button(base, baseClick);
			baseButton.addStyleName(CSS.MEDIUM_BUTTON);
			add(baseButton);
		}
		add(keyPad);
		
		addOkHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if (!"".equals(symbolDisplay.getText())) {
					onSpecify(symbolDisplay.getText());
				} else {
					disappear();
				}
			}
		});
	}
	public void reload() {
		symbolDisplay.setText("");
		appear();
	}
	public void onSpecify(String base) {
		disappear();
	}
	
}