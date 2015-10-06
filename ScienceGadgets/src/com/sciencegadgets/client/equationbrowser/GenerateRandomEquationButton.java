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
package com.sciencegadgets.client.equationbrowser;

import java.util.LinkedHashMap;

import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.EquationGenerator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.equationbrowser.GenerateSpec.Difficulty;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.shared.TypeSGET;

class GenerateRandomEquationButton extends SelectionButton {

	GenerateSpec generateSpec;
	Boolean hasDifficulty = false;

	public GenerateRandomEquationButton(GenerateSpec generateSpec) {
		this.generateSpec = generateSpec;
		addStyleName(CSS.MAKE_EQ_BUTTON);
		setHasDifficulty(false);
	}

	@Override
	protected void onSelect() {

		boolean mustBeWholeAnswer = true;
		boolean mustBePositives = true;

		int minAdd = 0;
		int minMultiply = 0;
		int minFraction = 0;
		int minExp = 0;

		int maxAdd = 10;
		int maxMultiply = 10;
		int maxFraction = 4;
		int maxExp = 2;

		// VS == VariableSide
		int sumsVS = 0, termsVS = 0, fracsVS = 0, expsVS = 0, //
		// OS == OtherSide
		sumsOS = 0, termsOS = 0, fracsOS = 0, expsOS = 0;

		Difficulty difficultyAdd, difficultyMult, difficultyFrac, difficultyExp;
		if (hasDifficulty) {
			difficultyAdd = generateSpec.slideAdd.getDifficulty();
			difficultyMult = generateSpec.slideMult.getDifficulty();
			difficultyFrac = generateSpec.slideFrac.getDifficulty();
			difficultyExp = generateSpec.slideExp.getDifficulty();
		} else {
			difficultyAdd = generateSpec.slideAdd.getDifficulty((int) (Math
					.random() * 3));
			difficultyMult = generateSpec.slideMult.getDifficulty((int) (Math
					.random() * 3));
			difficultyFrac = generateSpec.slideFrac.getDifficulty((int) (Math
					.random() * 3));
			difficultyExp = generateSpec.slideExp.getDifficulty((int) (Math
					.random() * 3));
		}

		if (difficultyAdd == Difficulty.NONE
				&& difficultyMult == Difficulty.NONE
				&& difficultyFrac == Difficulty.NONE
				&& difficultyExp == Difficulty.NONE) {
			difficultyAdd = Difficulty.EASY;
		}

		switch (difficultyAdd) {
		case NONE:
			break;
		case EASY:
			maxAdd = 10;
			sumsOS = 2;
			break;
		case MEDIUM:
			mustBePositives = false;
			minAdd = 10;
			maxAdd = 100;
			sumsOS = 2;
			sumsVS = 1;
			break;
		case HARD:
			minAdd = 100;
			maxAdd = 1000;
			sumsOS = 2;
			sumsVS = 2;
			break;
		}
		switch (difficultyMult) {
		case NONE:
			break;
		case EASY:
			maxMultiply = 10;
			termsOS = 1;
			break;
		case MEDIUM:
			minMultiply = 2;
			maxMultiply = 10;
			termsOS = 2;
			termsVS = 1;
			break;
		case HARD:
			minMultiply = 5;
			maxMultiply = 20;
			termsOS = 2;
			termsVS = 2;
			break;
		}
		switch (difficultyFrac) {
		case NONE:
			break;
		case EASY:
			maxFraction = 3;
			fracsOS = 1;
			break;
		case MEDIUM:
			maxFraction = 6;
			fracsOS = 2;
			break;
		case HARD:
			minFraction = 2;
			maxFraction = 10;
			fracsOS = 2;
			fracsVS = 1;
			break;
		}
		switch (difficultyExp) {
		case NONE:
			break;
		case EASY:
			maxExp = 2;
			expsOS = 1;
			break;
		case MEDIUM:
			minExp = 1;
			maxExp = 2;
			expsOS = 2;
			break;
		case HARD:
			minExp = 2;
			maxExp = 3;
			expsOS = 2;
			// expsVS = 1;
			break;
		}
		// Any subject set to easy defaults to all positives
		if (Difficulty.EASY.equals(difficultyAdd)
				|| Difficulty.EASY.equals(difficultyMult)
				|| Difficulty.EASY.equals(difficultyFrac)
				|| Difficulty.EASY.equals(difficultyExp)) {
			mustBePositives = true;
		}
		// Any subject set to hard overrides mustBePositive
		if (Difficulty.HARD.equals(difficultyAdd)
				|| Difficulty.HARD.equals(difficultyMult)
				|| Difficulty.HARD.equals(difficultyFrac)
				|| Difficulty.HARD.equals(difficultyExp)) {
			mustBePositives = false;
		}

		LinkedHashMap<TypeSGET, Integer> expressionsVariableSide = new LinkedHashMap<TypeSGET, Integer>();
		expressionsVariableSide.put(TypeSGET.Sum, sumsVS);
		expressionsVariableSide.put(TypeSGET.Term, termsVS);
		expressionsVariableSide.put(TypeSGET.Fraction, fracsVS);
		expressionsVariableSide.put(TypeSGET.Exponential, expsVS);

		LinkedHashMap<TypeSGET, Integer> expressionsOtherSide = new LinkedHashMap<TypeSGET, Integer>();
		expressionsOtherSide.put(TypeSGET.Sum, sumsOS);
		expressionsOtherSide.put(TypeSGET.Term, termsOS);
		expressionsOtherSide.put(TypeSGET.Fraction, fracsOS);
		expressionsOtherSide.put(TypeSGET.Exponential, expsOS);

		EquationTree eTree = EquationGenerator.GENERATE(
				expressionsVariableSide, expressionsOtherSide,
				mustBeWholeAnswer, mustBePositives, minAdd, minMultiply,
				minFraction, minExp, maxAdd, maxMultiply, maxFraction, maxExp);

		Moderator.isInEasyMode = true;
		Moderator
				.switchToAlgebra(eTree, ActivityType.interactiveequation, true);

		// SolverUniVariable.SOLVE(eTree);
		// Moderator.reloadEquationPanel(null, null);

	}

	public void setHasDifficulty(boolean hasDifficulty) {
		this.hasDifficulty = hasDifficulty;
		if (hasDifficulty) {
			setHTML("Generate");
			setTitle("Generate a random equation based on the difficulty specified");
		} else {
			setHTML("Completely Random");
			setTitle("Generate a completely random equation");
		}
	}

}