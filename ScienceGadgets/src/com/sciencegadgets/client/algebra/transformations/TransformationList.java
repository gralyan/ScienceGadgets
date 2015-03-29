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
package com.sciencegadgets.client.algebra.transformations;

import java.util.Collection;
import java.util.LinkedList;

import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.shared.TypeSGET;

public class TransformationList<E extends TransformationButton> extends LinkedList<E> {
	private static final long serialVersionUID = -3043410062241803505L;
	private EquationNode contextNode;
	EquationTree beforeAfterTree = null;
	public boolean reloadAlgebraActivity = true;

	public TransformationList(EquationNode contextNode) {
		super();
		this.contextNode = contextNode;
	}

	public EquationNode getNode() {
		return contextNode;
	}
	
	public void setNode(EquationNode node){
		contextNode = node;
	}

	public static BothSidesTransformations FIND_ALL_BOTHSIDES(EquationNode node) {
		BothSidesTransformations toBothSides = new BothSidesTransformations(node);
		if(!Moderator.getCurrentAlgebraActivity().inProgramaticTransformMode) {
		toBothSides.setPreviewLabels();
		}
		return toBothSides;
	}

	public static TransformationList<TransformationButton> FIND_ALL_SIMPLIFY(EquationNode node) {
		TransformationList<TransformationButton> simplify = new TransformationList<TransformationButton>(node);

		simplify.addAll(new InterFractionTransformations(node));

		if (TypeSGET.Fraction.equals(node.getParentType())
				&& node.getIndex() == 1) {
			simplify.add(new FractionTransformations(node.getParent()).denominatorFlip_check());
		}
		
		switch (node.getType()) {
		case Exponential:
			simplify.addAll(new ExponentialTransformations(node));
			break;
		case Operation:
			simplify.addAll(AlgebraicTransformations.operation(node));
			break;
		case Number:
			simplify.addAll(new NumberTransformations(node));
			simplify.add(AlgebraicTransformations.separateNegative_check(node,
					simplify));
			break;
		case Variable:
			simplify.addAll(new VariableTransformations(node));
			simplify.add(AlgebraicTransformations.separateNegative_check(node,
					simplify));
			break;
		case Log:
			simplify.addAll(new LogarithmicTransformations(node));
			break;
		case Trig:
			simplify.addAll(new TrigTransformations(node));
			break;
		case Fraction:
		case Sum:
		case Term:
			// *Note - These wrappers shouldn't have transformations because
			// they would be merged into the top layer if direct children of the
			// root
			break;
		}

		if(!Moderator.getCurrentAlgebraActivity().inProgramaticTransformMode) {
		simplify.setPreviewLabels();
		}
		return simplify;
	}

	void setPreviewLabels() {
		// Name as preview
		for (TransformationButton button : this) {
			String buttonHTML = button.getHTML();
			if (buttonHTML == null || "".equals(buttonHTML)) {
				EquationNode previewEq = button.getPreview();
				String preview = null;
				if (previewEq != null) {
					previewEq.getTree().reloadDisplay(true, true);
					preview = previewEq.getHTMLString(true, true);
				} else {
					preview = "no label";
				}
				button.setHTML(preview);
			}
		}
	}

	@Override
	public boolean add(E tButt) {
		if (tButt != null) {
			super.add(tButt);
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (c != null) {
			for (TransformationButton b : c) {
				if (b == null) {
					remove(b);
				}
			}
			return super.addAll(c);
		}
		return false;
	}

}
