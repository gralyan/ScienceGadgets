package com.sciencegadgets.client.algebra.transformations;

import java.util.Collection;
import java.util.LinkedList;

import com.google.gwt.user.client.ui.Button;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TypeML;

public class TransformationList extends LinkedList<TransformationButton> {

	public TransformationList() {
		super();
	}
	public TransformationList(MathNode node) {
		this();

		this.addAll(new BothSidesTransformations(node));

		switch (node.getType()) {
		case Exponential:
			this.add(AlgebraicTransformations
					.unravelExpLog_check(node));
			this.addAll(new ExponentialTransformations(node));
			break;
		case Operation:
			this.addAll(AlgebraicTransformations.operation(node));
			break;
		case Number:
			this.add(AlgebraicTransformations
					.separateNegative_check(node));
			this.add(AlgebraicTransformations
					.factorizeNumbers_check(node));
			this.add(AlgebraicTransformations
					.unitConversion_check(node));
			break;
		case Variable:
			this.add(AlgebraicTransformations
					.separateNegative_check(node));
			this.addAll(AlgebraicTransformations
					.isolatedVariable_check(node));
			break;
		case Log:
			this.add(AlgebraicTransformations
					.unravelLogExp_check(node));
			this.addAll(new LogarithmicTransformations(node));
			break;
		case Trig:
			this.add(AlgebraicTransformations
					.inverseTrig_check(node));
			this.addAll(new TrigTransformations(node));
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
			this.add(AlgebraicTransformations
					.denominatorFlip_check(node));
		}
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -3043410062241803505L;

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
