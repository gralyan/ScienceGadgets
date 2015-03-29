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

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;

/**
 * This Widget is used to wrap elementary tags so mouse handlers can be attached
 * to them. Mainly used for MathML tags so equations can be manipulated
 * 
 * @author John Gralyan
 * 
 */
public class AlgebaWrapper extends EquationWrapper {
	TransformationList<TransformationButton> simplifyTransformations = null;
	BothSidesTransformations bothSidesTransformations = null;

	/**
	 * Wrapper for symbols which allow for user interaction
	 * 
	 * <p>
	 * <b>Note - this widget can only be draggable if it's attached to an
	 * {@link AbsolutePanel}</b>
	 * </p>
	 */
	public AlgebaWrapper(EquationNode node, AlgebraActivity algebraActivity,
			Element element) {
		super(node, algebraActivity, element);

	}

	public void attachButtons() {
		if (simplifyTransformations == null) {
			simplifyTransformations = TransformationList
					.FIND_ALL_SIMPLIFY(node);
		}
		if (bothSidesTransformations == null) {
			bothSidesTransformations = new BothSidesTransformations(node);
		}
	}

	public void select() {
		super.select();

		if (this.equals(eqPanel.selectedWrapper)) {
			attachButtons();
			
			eqPanel.getAlgebraActivity().fillTransformLists(
					simplifyTransformations, bothSidesTransformations);
		}
	}

	public void unselect() {
		super.unselect();

		if (bothSidesTransformations != null) {
			for (BothSidesButton button : bothSidesTransformations) {
				button.deselect();
				button.getJoinedButton().deselect();
			}
		}

		eqPanel.getAlgebraActivity().clearTransformLists();
	}

}
