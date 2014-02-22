package com.sciencegadgets.client.algebra.transformations;

import java.util.Collection;
import java.util.LinkedList;

import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TypeML;

public class TransformationList extends LinkedList<TransformationButton> {
	private static final long serialVersionUID = -3043410062241803505L;
	private MathNode contextNode;
	MathTree beforeAfterTree = null;
	boolean reloadAlgebraActivity = true;

	public TransformationList(MathNode contextNode) {
		super();
		this.contextNode = contextNode;
	}

	public MathNode getNode() {
		return contextNode;
	}

	public static TransformationList[] FIND_ALL(MathNode node) {
		TransformationList typeSpecific = new TransformationList(node);
		TransformationList toBothSides = new TransformationList(node);
		TransformationList general = new TransformationList(node);
		TransformationList[] lists = { typeSpecific, toBothSides, general };

		toBothSides = new BothSidesTransformations(node);

		switch (node.getType()) {
		case Exponential:
			typeSpecific.addAll(new ExponentialTransformations(node));
			break;
		case Operation:
			typeSpecific.addAll(AlgebraicTransformations.operation(node));
			break;
		case Number:
			general.add(AlgebraicTransformations.separateNegative_check(node,
					general));
			typeSpecific.addAll(new NumberTransformations(node));
			break;
		case Variable:
			general.add(AlgebraicTransformations.separateNegative_check(node,
					general));
			typeSpecific.addAll(new VariableTransformations(node));
			break;
		case Log:
			typeSpecific.addAll(new LogarithmicTransformations(node));
			break;
		case Trig:
			typeSpecific.addAll(new TrigTransformations(node));
			break;
		case Fraction:
		case Sum:
		case Term:
			// *Note - These wrappers shouldn't have transformations because
			// they would be merged into the top layer if direct children of the
			// root
			break;
		}

		if (TypeML.Fraction.equals(node.getParentType())
				&& node.getIndex() == 1) {
			general.add(AlgebraicTransformations.denominatorFlip_check(node,
					general));
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
