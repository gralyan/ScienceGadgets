package com.sciencegadgets.client.algebra.transformations;

import java.util.LinkedList;

import com.google.gwt.user.client.ui.Button;
import com.sciencegadgets.client.algebra.AlgebraActivity;

public class Transformations {
	
	LinkedList<Button> transformations = new LinkedList<Button>();
	
	void addButtons() {
		if (AlgebraActivity.isInEasyMode && transformations.size() == 1) {
			transformations.getFirst().click();
		} else {
			for (Button transform : transformations) {
				AlgebraActivity.addTransformation(transform);
			}
		}
	}

	protected void check(Button tButt) {
		if (tButt != null) {
			transformations.add(tButt);
		}
	}
}
