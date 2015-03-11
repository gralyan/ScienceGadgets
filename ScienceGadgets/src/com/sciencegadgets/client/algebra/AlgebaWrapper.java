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
