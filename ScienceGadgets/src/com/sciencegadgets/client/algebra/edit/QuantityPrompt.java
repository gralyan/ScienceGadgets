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
package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.EquationWrapper;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SearchBox;
import com.sciencegadgets.client.ui.specification.QuantitySpecification;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitMap;

public abstract class QuantityPrompt extends Prompt {

	protected EquationNode node;
	protected boolean mustCheckUnits;
	protected QuantitySpecification spec;

	protected SelectionPanel establishedSelection = new SelectionPanel(
			"Established");
	final SearchBox estSearch = establishedSelection.makeSearchBox();
	protected FlowPanel specPanel = new FlowPanel();
	HandlerRegistration keyUp;

	public QuantityPrompt(EquationNode equationNode, boolean clearDisplays,
			boolean mustCheckUnits) {
		super();
		this.node = equationNode;
		this.mustCheckUnits = mustCheckUnits;

		// Established Area
		FlowPanel est = new FlowPanel();
		est.addStyleName(CSS.ESTABLISHED_QUANTITY_AREA);
		estSearch.addStyleName(CSS.SEARCH_BOX);
		establishedSelection.addStyleName(CSS.ESTABLISHED_QUANTITY_SELECTION);
		est.add(estSearch);
		est.add(establishedSelection);
		add(est);

		specPanel.addStyleName(CSS.QUANTITY_PROMPT_SPEC);
		add(specPanel);

		// OK button
		addOkHandler(new OkHandler());

	}

	public void reload(EquationNode mathNode, boolean clearDisplays,
			boolean mustCheckUnits) {
		spec.reload(clearDisplays, canHaveUnits(mathNode));

		this.mustCheckUnits = mustCheckUnits;
		this.node = mathNode;

		// Symbol Display
		if (!clearDisplays) {
			String oldSymbol = node.getSymbol();
			spec.getSymbolDisplay().setText(oldSymbol);
			spec.setUnit(new UnitMap(node));
		}

		spec.getSymbolDisplay().removeStyleName(CSS.INVALID_INPUT);

		establishedSelection.search("");
		
		keyUp = addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				estSearch.setFocus(true);
				char keyChar = (char) event.getNativeKeyCode();
				if (Character.isUpperCase(keyChar)) {
					String searchQ = ""+Character.toLowerCase(keyChar);
					estSearch.setText(searchQ);
					estSearch.search(searchQ);
				}
				if (keyUp != null) {
					keyUp.removeHandler();
					keyUp = null;
				}
			}
		});
		// blur = onFocusOut , when focus shifts, stop sending keys to search
		addBlurHandler(new BlurHandler() {
			@Override
			public void onBlur(BlurEvent event) {
				if (keyUp != null) {
					keyUp.removeHandler();
					keyUp = null;
				}
			}
		});
	}

	protected boolean canHaveUnits(EquationNode mNode) {
		switch (mNode.getParentType()) {
		case Exponential:
			if (mNode.getIndex() == 1) {
				return false;
			}
			break;
		case Trig:
			String func = mNode.getParent()
					.getAttribute(MathAttribute.Function);
			if (func.contains(TrigFunctions.ARC)) {
				return false;
			}
			// only allowed units for trig arguments are of PlaneAngle
			break;
		case Log:
			return false;
		case Equation:
			return true;
		default:
		}
		return canHaveUnits(mNode.getParent());
	}

	private class OkHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			AlgebraActivity algebraActivity = ((EquationWrapper) node
					.getWrapper()).getAlgebraActivity();

			String oldSymbol = node.getSymbol();
			String symbol = extractSymbol();

			if (mustCheckUnits
					&& !spec.getUnitMap().isConvertableTo(node.getUnitMap())
					&& !"0".equals(symbol)) {
				Window.alert("Units must match:\n" + spec.getUnitMap()+"\n"+node.getUnitMap());
				return;
			}

			if (symbol == null) {
				return;
			}
			node.highlight();

			setNode(symbol);

				node.getXMLNode().setAttribute(
						MathAttribute.Unit.getAttributeName(),
						spec.getDataUnit().toString());

			disappear();
			algebraActivity.reloadEquationPanel(oldSymbol+" = " +symbol, null, true, node.getId());
		}
	}

	protected abstract void setNode(String symbol);

	protected abstract String extractSymbol();
}
