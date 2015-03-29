/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.ui;

import java.math.BigDecimal;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.shared.TypeSGET.Operator;

public class SymbolDisplay extends HTML {
	KeyPadNumerical keyPad;
	String text = "";

	SymbolDisplay() {
	}

	public SymbolDisplay(KeyPadNumerical keyPad) {
		this.keyPad = keyPad;
	}

	public void setText(String text) {
		this.text = text;

		getElement().removeAllChildren();

		boolean isBlank = "".equals(text);

		if (keyPad != null) {
			keyPad.negButton.setEnabled(isBlank);
			keyPad.periodButton.setEnabled(!text.contains(keyPad.periodButton
					.getHTML()));
			keyPad.eButton.setEnabled(!isBlank
					&& !text.contains(keyPad.eButton.getHTML()));
			keyPad.expButton.setEnabled(!isBlank
					&& !text.contains(keyPad.expButton.getHTML()));
		}

		if (!isBlank) {
			try {
				new BigDecimal(text);
				setHTML(text);
			} catch (NumberFormatException e) {
				// non-numbers, characters after the first are subscripts
				// note - constants are number nodes with character text
				int substringEnd = text.startsWith(Operator.MINUS.getSign()) ? 2
						: 1;
				setHTML(text.substring(0, substringEnd));
				Element subscript = DOM.createDiv();
				subscript.addClassName(CSS.SUBSCRIPT);
				subscript.setInnerText(text.substring(substringEnd));
				getElement().appendChild(subscript);
			}
		}
	}

	public String getText() {
		return text;
	}

	void setKeyPad(KeyPadNumerical keyPad) {
		this.keyPad = keyPad;
	}
	
	public void clear() {
		setText("");
	}

}