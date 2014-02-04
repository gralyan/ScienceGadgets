package com.sciencegadgets.client.algebra.transformations;

import java.util.Collection;
import java.util.LinkedList;

import com.google.gwt.user.client.ui.Button;

public class TransformationList extends LinkedList<TransformationButton> {

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
