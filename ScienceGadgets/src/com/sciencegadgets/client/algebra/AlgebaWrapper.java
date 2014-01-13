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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.transformations.AlgebraicTransformations;
import com.sciencegadgets.client.algebra.transformations.ExponentialTransformations;
import com.sciencegadgets.client.algebra.transformations.LogarithmicTransformations;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class AlgebaWrapper extends ZoomWrapper {

	private BothSidesMenu bothSidesMenu;

	/**
	 * Wrapper for symbols which allow for user interaction
	 * 
	 * <p>
	 * <b>Note - this widget can only be draggable if it's attached to an
	 * {@link AbsolutePanel}</b>
	 * </p>
	 */
	public AlgebaWrapper(MathNode node, EquationPanel eqPanel, Element element) {
		super(node, eqPanel, element);

		bothSidesMenu = new BothSidesMenu(node, element.getOffsetWidth() + "px");
//		menu = new FlowPanel();
	}

	/**
	 * Highlights the selected wrapper and joiner as well as all the drop
	 * targets associated with the selected
	 * 
	 */
	public void select() {
		super.select();

		if (this.equals(EquationPanel.selectedWrapper)) {
			AlgebraActivity.bothSidesButtonMenu.clear();
			AlgebraActivity.bothSidesButtonMenu.add(bothSidesMenu);
			
			AlgebraActivity.algTransformMenu.clear();

			switch (node.getType()) {
			case Exponential:
				AlgebraicTransformations.unravelExpLog_check(node);
				new ExponentialTransformations(node);
				break;
			case Fraction:
				AlgebraicTransformations.denominatorFlip_check(node);
				break;
			case Sum:
				break;
			case Term:
				break;
			case Operation:
				AlgebraicTransformations.operation(node);
				break;
			case Number:
				AlgebraicTransformations.separateNegative_check(node);
				AlgebraicTransformations.factorizeNumbers_check(node);
				AlgebraicTransformations.unitConversion_check(node);
				break;
			case Variable:
				AlgebraicTransformations.separateNegative_check(node);
				AlgebraicTransformations.isolatedVariable_check(node);
				break;
			case Log:
				AlgebraicTransformations.unravelLogExp_check(node);
				new LogarithmicTransformations(node);
				break;
			case Trig:
				AlgebraicTransformations.inverseTrig_check(node);
				break;
				
			}

		}
	}

	public void unselect() {
		super.unselect();
		AlgebraActivity.algTransformMenu.clear();
		AlgebraActivity.bothSidesButtonMenu.clear();
	}

}
