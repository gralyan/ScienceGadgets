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

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.client.ui.FitParentHTML;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitHTML;

public class EquationWrapper extends Wrapper {
	protected EquationPanel eqPanel;
	protected AlgebraActivity algebraActivity;
//	protected Label value;
//	private Style valueStyle;

	public EquationWrapper(EquationNode node, AlgebraActivity algebraActivity,
			Element element) {
		super(node, algebraActivity.getEquationPanel(), element);
		this.eqPanel = algebraActivity.getEquationPanel();
		this.algebraActivity = algebraActivity;

		this.addStyleName(CSS.DISPLAY_WRAPPER);

//		String valueStr = node.getAttribute(MathAttribute.Value);
//		if (!"".equals(valueStr)
//				&& !valueStr.equals(node.getXMLNode().getInnerText())) {
//			value = new Label(valueStr);
//			valueStyle = value.getElement().getStyle();
//			value.addStyleName(CSS.NUMBER_VALUE);
//			valueStyle.setWidth(this.getOffsetWidth(), Unit.PX);
//			eqPanel.add(value,
//					this.getAbsoluteLeft() - eqPanel.getAbsoluteLeft(),
//					this.getAbsoluteTop() - eqPanel.getAbsoluteTop());
//		}
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
		FitParentHTML typeLabel = new FitParentHTML(type.name());
		detailsList.add(typeLabel);

		switch (type) {
		case Number:
			String fullValue = node.getAttribute(MathAttribute.Value);
			if("".equals(fullValue)) {
				fullValue = node.getAttribute(MathAttribute.Randomness);
			}
			FitParentHTML valueHTML = new FitParentHTML(fullValue);
			detailsList.add(valueHTML);
			valueHTML.getElement().getStyle().setWidth(100, Unit.PCT);
			valueHTML.getElement().getStyle().setProperty("overflowWrap", "break-word");

			UnitAttribute unit = node.getUnitAttribute();
			if (!"".equals(unit)) {
				FitParentHTML unitHTML = new FitParentHTML();
				unitHTML.getElement().appendChild(UnitHTML.create(unit));
				detailsList.add(unitHTML);
			}

			break;
		case Variable:
			UnitAttribute qKind = node.getUnitAttribute();
			if (!"".equals(qKind)) {
				FitParentHTML qKindHTML = new FitParentHTML();
				qKindHTML.getElement().appendChild(UnitHTML.create(qKind));
				detailsList.add(qKindHTML);
			}
			break;
		default:
			break;
		}

		CommunistPanel detailsPanel = new CommunistPanel();
		detailsPanel.addStyleName(CSS.SELECTION_DETAILS);
		algebraActivity.upperMidEqArea.clear();
		algebraActivity.upperMidEqArea.add(detailsPanel);
		detailsPanel.addAll(detailsList);

		Widget typeParent = typeLabel.getParent();
		if (typeParent != null) {
			typeParent.addStyleName(type.toString());
			typeParent.addStyleName(CSS.PARENT_WRAPPER);
		}
	}

	@Override
	public void select() {

		if (this.equals(eqPanel.selectedWrapper)) {
			// If this was already selected, focus in on it

			if (node.hasChildElements()
					&& (dragController == null || !dragController.isDragging())) {
				// Moderator.SOUNDS.WRAPPER_ZOOM_IN.play();
				unselect();
				eqPanel.setFocus(node);
			}

		} else {
			// If there is another selection, unselect it, select new

			if (eqPanel.selectedWrapper != null) {
				eqPanel.selectedWrapper.unselect();
			}

			super.select();

			fillSelectionDetails();

//			if (valueStyle != null) {
//				valueStyle.clearWidth();
//				valueStyle.setBackgroundColor("white");
//			}

			eqPanel.selectedWrapper = this;
		}
	}

	@Override
	public void unselect() {
		super.unselect();

		eqPanel.selectedWrapper = null;

//		if (valueStyle != null) {
//			valueStyle.setWidth(this.getOffsetWidth(), Unit.PX);
//			valueStyle.clearBackgroundColor();
//		}

		algebraActivity.revertUpperMidAreaToDefault();
	}
}
