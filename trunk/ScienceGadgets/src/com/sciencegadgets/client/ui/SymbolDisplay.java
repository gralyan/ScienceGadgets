package com.sciencegadgets.client.ui;

import com.google.gwt.user.client.ui.Label;

public class SymbolDisplay extends Label {
	KeyPadNumerical keyPad;

	SymbolDisplay() {
	}

	SymbolDisplay(KeyPadNumerical keyPad) {
		this.keyPad = keyPad;
	}

	@Override
	public void setText(String text) {
		super.setText(text);
		if (keyPad != null) {
			keyPad.negButton.setEnabled("".equals(text));
			keyPad.periodButton.setEnabled(!text.contains(keyPad.periodButton
					.getHTML()));
			keyPad.eButton.setEnabled(!"".equals(text)
					&& !text.contains(keyPad.eButton.getHTML()));
			keyPad.expButton.setEnabled(!"".equals(text)
					&& !text.contains(keyPad.expButton.getHTML()));
		}
	}

	void setKeyPad(KeyPadNumerical keyPad) {
		this.keyPad = keyPad;
	}
	
}