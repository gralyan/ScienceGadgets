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
package com.sciencegadgets.client.equationbrowser;

import java.util.LinkedHashMap;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RadioButton;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationGenerator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.SolverUniVariable;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.TypeSGET;

public class EquationBrowser extends FlowPanel {

	ScienceBrowser scienceBrowser = new ScienceBrowser(this);
	AlgebraBrowser algebraBrowser = new AlgebraBrowser(this);
	private RadioButton modeAlg = new RadioButton("mode", "Algebra");
	private RadioButton modeSci = new RadioButton("mode", "Science");
	private RadioButton modeEdit = new RadioButton("mode2", "Edit");
	private RadioButton modeSolve = new RadioButton("mode2", "Solve");
	EquationBrowser equationBrowser = this;
	public boolean inEditMode;

	public EquationBrowser() {

		this.getElement().setId(CSS.EQUATION_BROWSER);

		// Grid modes = new Grid(1, 2);
		// modes.setWidget(0, 0, modeAlg);
		// modes.setWidget(0, 1, modeSci);
		// modes.setStyleName(CSS.MODES);
		// this.add(modes);

		Grid modes2 = new Grid(1, 2);
		modes2.setWidget(0, 0, modeSolve);
		modes2.setWidget(0, 1, modeEdit);
		modes2.setStyleName(CSS.MODES);
		this.add(modes2);

		modeAlg.addClickHandler(new ModeSelectHandler(Mode.algebra));
		modeSci.addClickHandler(new ModeSelectHandler(Mode.science));
		modeEdit.addClickHandler(new ModeSelectHandler(Mode.edit));
		modeSolve.addClickHandler(new ModeSelectHandler(Mode.solve));

		modeAlg.setValue(true, true);
		modeSolve.setValue(true, true);
		this.add(algebraBrowser);
		this.add(new Button("generate", new GenerateEquationHandler()));
	}

	private enum Mode {
		science, algebra, edit, solve;
	}

	class ModeSelectHandler implements ClickHandler {
		Mode mode;

		private ModeSelectHandler(Mode mode) {
			this.mode = mode;
		}

		public void onClick(ClickEvent event) {
			// AlgOut.algOut.clear(true);
			// AlgOut.algOut.resizeRows(0);

			switch (mode) {
			case algebra:
				scienceBrowser.removeFromParent();
				equationBrowser.add(algebraBrowser);
				break;
			case science:
				scienceBrowser.sumGrid.clear(true);
				algebraBrowser.removeFromParent();
				equationBrowser.add(scienceBrowser);
				break;
			case edit:
				inEditMode = true;
				break;
			case solve:
				inEditMode = false;
				break;

			}
		}
	}

	class GenerateEquationHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {

			LinkedHashMap<TypeSGET, Integer> expressionsVariableSide = new LinkedHashMap<TypeSGET, Integer>();
			expressionsVariableSide.put(TypeSGET.Sum, 3);
			expressionsVariableSide.put(TypeSGET.Term, 3);
			expressionsVariableSide.put(TypeSGET.Fraction, 1);
			expressionsVariableSide.put(TypeSGET.Exponential, 1);

			LinkedHashMap<TypeSGET, Integer> expressionsOtherSide = new LinkedHashMap<TypeSGET, Integer>();
			expressionsOtherSide.put(TypeSGET.Sum, 3);
			expressionsOtherSide.put(TypeSGET.Term, 3);
			expressionsOtherSide.put(TypeSGET.Fraction, 1);
			expressionsOtherSide.put(TypeSGET.Exponential, 1);

			boolean mustBeWholeAnswer = true;
			boolean mustBePositives = false;
			int maxAdd = 10;
			int maxMultiply = 10;
			int maxFraction = 4;
			int maxExp = 4;

			EquationTree eTree = EquationGenerator.GENERATE(expressionsVariableSide,
					expressionsOtherSide, mustBeWholeAnswer, mustBePositives,
					maxAdd, maxMultiply, maxFraction, maxExp);
			Moderator.switchToAlgebra(eTree, false);
//			SolverUniVariable.SOLVE(eTree);
//			Moderator.reloadEquationPanel(null, null);
		}

	}
}
