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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.EquationWrapper;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SelectionPanel;
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

	protected SelectionPanel establishedSelection = new SelectionPanel();
	protected FlowPanel specPanel = new FlowPanel();
	
	public QuantityPrompt(EquationNode equationNode, boolean clearDisplays,
			boolean mustCheckUnits) {
		super();
		this.node = equationNode;
		this.mustCheckUnits = mustCheckUnits;

		establishedSelection.addStyleName(CSS.ESTABLISHED_QUANTITY_SELECTION);
		specPanel.addStyleName(CSS.QUANTITY_PROMPT_SPEC);
		add(establishedSelection);
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
			// TODO only allow units of PlaneAngle for trig arguments
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

			String symbol = extractSymbol();

			if (mustCheckUnits
					&& !spec.getUnitMap().isConvertableTo(node.getUnitMap()) && !"0".equals(symbol)) {
				Window.alert("Units must match:\n" + spec.getUnitMap());
				return;
			}

			if (symbol == null) {
				return;
			}
			setNode(symbol);

			// TODO allow selection of angle unit for trig arguments
			if (TypeSGET.Trig.equals(node.getParentType())) {
				node.getXMLNode().setAttribute(
						MathAttribute.Unit.getAttributeName(),
						UnitAttribute.ANGLE_ATTRIBUTE);
			} else {
				node.getXMLNode().setAttribute(
						MathAttribute.Unit.getAttributeName(),
						spec.getDataUnit().toString());
			}

			disappear();
			algebraActivity.reloadEquationPanel(null, null, true, node.getId());
		}
	}

	protected abstract void setNode(String symbol);

	protected abstract String extractSymbol();
}
