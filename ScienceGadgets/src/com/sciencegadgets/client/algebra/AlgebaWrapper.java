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
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.AlgebraicTransformations;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.ExponentialTransformations;
import com.sciencegadgets.client.algebra.transformations.LogarithmicTransformations;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.TrigTransformations;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.shared.TypeEquationXML;

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
	public AlgebaWrapper(EquationNode node, AlgebraActivity algebraActivity,
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

		if (this.equals(eqPanel.selectedWrapper)) {

//			if (algTransformMenu == null) {
//
//				algTransformMenu = new CommunistPanel(true);
//				algTransformMenu.addStyleName(CSS.LAYOUT_ROW);
//				algTransformMenu.setSize("100%", "100%");
//
//				TransformationList[] transorms = TransformationList.FIND_ALL(node);
//				algTransformMenu.addAll(transorms[0]);
//				algTransformMenu.addAll(transorms[1]);
//				algTransformMenu.addAll(transorms[2]);
//			}
			
//			AlgebraActivity algActivity = Moderator.getCurrentAlgebraActivity();
//			TransformationList[] transorms = TransformationList.FIND_ALL(node);
//			algTransformMenu.addAll(transorms[0]);
//			algTransformMenu.addAll(transorms[1]);
//			algTransformMenu.addAll(transorms[2]);
//			
//			algActivity.lowerEqArea.clear();
//			algActivity.lowerEqArea.add(algTransformMenu);
			
			Moderator.getCurrentAlgebraActivity().fillTransformLists(node);
		}
	}

	public void unselect() {
		super.unselect();
		Moderator.getCurrentAlgebraActivity().lowerEqArea.clear();
	}

}
