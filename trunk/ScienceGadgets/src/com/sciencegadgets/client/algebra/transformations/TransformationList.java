package com.sciencegadgets.client.algebra.transformations;

import java.util.Collection;
import java.util.LinkedList;

import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.shared.TypeEquationXML;

public class TransformationList extends LinkedList<TransformationButton> {
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

	public static TransformationList[] FIND_ALL(EquationNode node) {
		TransformationList simplify = new TransformationList(node);
		TransformationList toBothSides = new BothSidesTransformations(node);

		switch (node.getType()) {
		case Exponential:
			simplify.addAll(new ExponentialTransformations(node));
			break;
		case Operation:
			simplify.addAll(AlgebraicTransformations.operation(node));
			break;
		case Number:
			simplify.add(AlgebraicTransformations.separateNegative_check(node,
					simplify));
			simplify.addAll(new NumberTransformations(node));
			break;
		case Variable:
			simplify.add(AlgebraicTransformations.separateNegative_check(node,
					simplify));
			simplify.addAll(new VariableTransformations(node));
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

		if (TypeEquationXML.Fraction.equals(node.getParentType())
				&& node.getIndex() == 1) {
			simplify.add(AlgebraicTransformations.denominatorFlip_check(node,
					simplify));
		}

		TransformationList[] lists = { simplify, toBothSides};
		
		// Name as preview
		for(TransformationList list : lists) {
			for(TransformationButton button : list) {
				String buttonHTML = button.getHTML();
				if(buttonHTML == null || "".equals(buttonHTML)) {
					EquationTree previewEq = button.getPreview();
					String preview = null;
					if (previewEq != null) {
						preview = previewEq.getRightDisplay();
					} else {
						preview = "no label";
					}
					button.setHTML(preview);
				}
			}
		}

		return lists;
	}

	@Override
	public boolean add(TransformationButton tButt) {
		if (tButt != null) {
			super.add(tButt);
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends TransformationButton> c) {
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
