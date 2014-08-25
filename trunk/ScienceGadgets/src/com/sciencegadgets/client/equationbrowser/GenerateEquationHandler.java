package com.sciencegadgets.client.equationbrowser;

import java.util.LinkedHashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationGenerator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.shared.TypeSGET;

class GenerateEquationHandler implements ClickHandler {

	@Override
	public void onClick(ClickEvent event) {

		LinkedHashMap<TypeSGET, Integer> expressionsVariableSide = new LinkedHashMap<TypeSGET, Integer>();
		expressionsVariableSide.put(TypeSGET.Sum, 0);
		expressionsVariableSide.put(TypeSGET.Term, 0);
		expressionsVariableSide.put(TypeSGET.Fraction, 0);
		expressionsVariableSide.put(TypeSGET.Exponential, 0);

		LinkedHashMap<TypeSGET, Integer> expressionsOtherSide = new LinkedHashMap<TypeSGET, Integer>();
		expressionsOtherSide.put(TypeSGET.Sum, 1);
		expressionsOtherSide.put(TypeSGET.Term, 0);
		expressionsOtherSide.put(TypeSGET.Fraction, 0);
		expressionsOtherSide.put(TypeSGET.Exponential, 0);

		boolean mustBeWholeAnswer = true;
		boolean mustBePositives = false;
		int maxAdd = 10;
		int maxMultiply = 10;
		int maxFraction = 4;
		int maxExp = 4;

		EquationTree eTree = EquationGenerator.GENERATE(
				expressionsVariableSide, expressionsOtherSide,
				mustBeWholeAnswer, mustBePositives, maxAdd, maxMultiply,
				maxFraction, maxExp);
		Moderator.switchToAlgebra(eTree, false);
		// SolverUniVariable.SOLVE(eTree);
		// Moderator.reloadEquationPanel(null, null);

	}
}