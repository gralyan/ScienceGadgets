/*   Copyright 2012 John Gralyan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;
import java.util.LinkedList;

import com.allen_sauer.gwt.dnd.client.AbstractDragController;
import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Type.Operator;
import com.sciencegadgets.client.algebra.transformations.AlgebraicTransformations;
import com.sciencegadgets.client.algebra.transformations.AssociativeDropController;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class MathWrapper extends Wrapper {

	private BothSidesMenu bothSidesMenu;

	/**
	 * Wrapper for symbols which allow for user interaction
	 * 
	 * <p>
	 * <b>Note - this widget can only be draggable if it's attached to an
	 * {@link AbsolutePanel}</b>
	 * </p>
	 */
	public MathWrapper(MathNode node, EquationPanel eqPanel, Element element) {
		super(node, eqPanel, element);

		bothSidesMenu = new BothSidesMenu(this, element.getOffsetWidth() + "px");
		menu = new FlowPanel();
	}

	/**
	 * Highlights the selected wrapper and joiner as well as all the drop
	 * targets associated with the selected
	 * 
	 * @param wrapper
	 * @param select
	 *            - selects if true, unselects if false
	 */
	public void select() {

		AlgebraActivity.selectedMenu.clear();
		AlgebraActivity.selectedMenu.add(bothSidesMenu);

		switch (node.getType()) {
		case Equation:
		case Exponential:
		case Fraction:
		case Sum:
		case Term:
			break;
		case Operation:
			AlgebraicTransformations.operation(node);
			return;
		case Number:
			AlgebraicTransformations.factorizeNumbers_check(node);
		case Variable:
			AlgebraicTransformations.separateNegative_check(node);
			break;
		}

		super.select();

	}

	public void unselect() {
		AlgebraActivity.selectedMenu.clear();
		super.unselect();
	}

}


