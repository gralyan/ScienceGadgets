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
import com.google.gwt.thirdparty.javascript.rhino.jstype.AllType;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.CommunistPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.transformations.AlgebraicTransformations;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.ExponentialTransformations;
import com.sciencegadgets.client.algebra.transformations.LogarithmicTransformations;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.TrigTransformations;
import com.sciencegadgets.shared.TypeML;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class AlgebaWrapper extends EquationWrapper {

	private CommunistPanel algTransformMenu;

	/**
	 * Wrapper for symbols which allow for user interaction
	 * 
	 * <p>
	 * <b>Note - this widget can only be draggable if it's attached to an
	 * {@link AbsolutePanel}</b>
	 * </p>
	 */
	public AlgebaWrapper(MathNode node, AlgebraActivity algebraActivity,
			Element element) {
		super(node, algebraActivity, element);

	}

	/**
	 * Highlights the selected wrapper and joiner as well as all the drop
	 * targets associated with the selected
	 * 
	 */
	public void select() {
		super.select();

		if (this.equals(EquationPanel.selectedWrapper)) {

			if (algTransformMenu == null) {

				algTransformMenu = new CommunistPanel(true);
				algTransformMenu.addStyleName(CSS.LAYOUT_ROW);
				algTransformMenu.setSize("100%", "100%");

				TransformationList transorms = new TransformationList();

				transorms.addAll(new BothSidesTransformations(node));

				switch (node.getType()) {
				case Exponential:
					transorms.add(AlgebraicTransformations
							.unravelExpLog_check(node));
					transorms.addAll(new ExponentialTransformations(node));
					break;
				case Operation:
					transorms.addAll(AlgebraicTransformations.operation(node));
					break;
				case Number:
					transorms.add(AlgebraicTransformations
							.separateNegative_check(node));
					transorms.add(AlgebraicTransformations
							.factorizeNumbers_check(node));
					transorms.add(AlgebraicTransformations
							.unitConversion_check(node));
					break;
				case Variable:
					transorms.add(AlgebraicTransformations
							.separateNegative_check(node));
					transorms.addAll(AlgebraicTransformations
							.isolatedVariable_check(node));
					break;
				case Log:
					transorms.add(AlgebraicTransformations
							.unravelLogExp_check(node));
					transorms.addAll(new LogarithmicTransformations(node));
					break;
				case Trig:
					transorms.add(AlgebraicTransformations
							.inverseTrig_check(node));
					transorms.addAll(new TrigTransformations(node));
					break;
				case Fraction:
				case Sum:
				case Term:
					// These wrappers shouldn't have transformations because
					// they are merged into the top layer if direct child of the
					// root
					break;
				}

				if (TypeML.Fraction.equals(node.getParentType())
						&& node.getIndex() == 1) {
					transorms.add(AlgebraicTransformations
							.denominatorFlip_check(node));
				}

				algTransformMenu.addAll(transorms);
			}
			Moderator.getCurrentAlgebraActivity().lowerEqArea.clear();
			Moderator.getCurrentAlgebraActivity().lowerEqArea
					.add(algTransformMenu);
		}
	}

	public void unselect() {
		super.unselect();
		Moderator.getCurrentAlgebraActivity().lowerEqArea.clear();
	}

}
