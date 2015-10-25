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
package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitHTML;

public class EquationWrapper extends Wrapper {
	protected EquationPanel eqPanel;
	protected AlgebraActivity algebraActivity;

	public EquationWrapper(EquationNode node, AlgebraActivity algebraActivity,
			Element element) {
		super(node, algebraActivity.getEquationPanel(), element);
		this.eqPanel = algebraActivity.getEquationPanel();
		this.algebraActivity = algebraActivity;

		this.addStyleName(CSS.DISPLAY_WRAPPER);

		if (TypeSGET.Equation.equals(node.getParentType())
				&& node.getIndex() == 1) {
			canSelect = false;
		}
	}

	public EquationPanel getEqPanel() {
		return eqPanel;
	}

	public AlgebraActivity getAlgebraActivity() {
		return algebraActivity;
	}

	private void fillSelectionDetails() {
		LinkedList<Widget> detailsList = new LinkedList<Widget>();

		TypeSGET type = node.getType();
		TypeSGET operationType = null;
		// FitParentHTML typeLabel = new FitParentHTML(type.getIcon());
		HTML typeLabel = new HTML(type.toString());
		detailsList.add(typeLabel);

		switch (type) {
		case Number:
			String fullValue = node.getAttribute(MathAttribute.Value);
			if ("".equals(fullValue)) {
				fullValue = node.getAttribute(MathAttribute.Randomness);
			} else {
				try {
					new BigDecimal(fullValue);
					// FitParentHTML valueHTML = new
					// FitParentHTML(fullValue);
					final TextBox valueHTML = new TextBox();
					valueHTML.setText(fullValue);
					valueHTML.addClickHandler(new ClickHandler() {
						@Override
						public void onClick(ClickEvent event) {
							valueHTML.selectAll();
						}
					});
					valueHTML.addStyleName(CSS.SELECTION_DETAILS_Value);
					valueHTML.getElement().getStyle().setProperty("textShadow", "inherit");
					detailsList.add(valueHTML);
				} catch (NumberFormatException ex) {
				}
			}

			UnitAttribute unit = node.getUnitAttribute();
			if (!"".equals(unit.toString())) {
				HTML unitHTML = new HTML();
				unitHTML.getElement().appendChild(UnitHTML.create(unit, false));
				detailsList.add(unitHTML);
			}

			break;
		case Variable:
			UnitAttribute qKind = node.getUnitAttribute();
			if (!"".equals(qKind.toString())) {
				// FitParentHTML qKindHTML = new FitParentHTML();
				HTML qKindHTML = new HTML();
				qKindHTML.getElement().appendChild(
						UnitHTML.create(qKind, false));
				detailsList.add(qKindHTML);
			}
			break;
		case Operation:
			operationType = node.getParentType();
			String[] parts = { node.getPrevSibling().getType().toString(),
					node.getSymbol(),
					node.getNextSibling().getType().toString() };

			typeLabel.setHTML("");
			Element typeEl = typeLabel.getElement();
			for (String part : parts) {
				Element partEl = new HTML(part).getElement();
				Style partSt = partEl.getStyle();
				partSt.setMargin(2, Unit.PX);
				partSt.setDisplay(Display.INLINE_BLOCK);
				partSt.setVerticalAlign(VerticalAlign.MIDDLE);
				typeEl.appendChild(partEl);
			}
		default:
			break;
		}

		FlowPanel detailsPanel = new FlowPanel();
		detailsPanel.addStyleName(CSS.SELECTION_DETAILS);
		for (Widget s : detailsList) {
			detailsPanel.add(s);
		}
		algebraActivity.detailsArea.clear();
		algebraActivity.detailsArea.add(detailsPanel);

		Widget typeParent = typeLabel.getParent();
		if (typeParent != null) {
			// typeParent.addStyleName(type.getCSSClassName());
			// typeParent.addStyleName(CSS.PARENT_WRAPPER);
			if (operationType != null) {
				typeParent.addStyleName(operationType.getCSSClassName());
			}
		}
	}

	@Override
	public void select() {
		if (!canSelect) {
			return;
		}

		if (this.equals(eqPanel.selectedWrapper)) {
			// If this was already selected, focus in on it

			if (node.hasChildElements()
					&& (dragController == null || !dragController.isDragging())) {
				// Moderator.SOUNDS.WRAPPER_ZOOM_IN.play();
				eqPanel.setFocus(node);
				// unselect();
			}

		} else {
			// If there is another selection, unselect it, select new

			if (eqPanel.selectedWrapper != null) {
				((EquationWrapper) eqPanel.selectedWrapper).unselect();
			}

			super.select();

			fillSelectionDetails();
			eqPanel.selectedWrapper = this;
		}
	}

	@Override
	public void unselect() {
		if (!canSelect) {
			return;
		}
		super.unselect();

		eqPanel.selectedWrapper = null;

		algebraActivity.detailsArea.clear();
	}
}
