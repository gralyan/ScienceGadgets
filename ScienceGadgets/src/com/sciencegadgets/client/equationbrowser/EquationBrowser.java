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

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RadioButton;
import com.sciencegadgets.client.algebra.AlgebraActivity;

//Uncomment to use as gadget////////////////////////////////////
//
public class EquationBrowser extends FlowPanel {

	ScienceBrowser scienceBrowser = new ScienceBrowser();
	AlgebraBrowser algebraBrowser = new AlgebraBrowser();
	private RadioButton modeAlg = new RadioButton("mode", "Algebra");
	private RadioButton modeSci = new RadioButton("mode", "Science");
	private RadioButton modeEdit = new RadioButton("mode2", "Edit");
	private RadioButton modeSolve = new RadioButton("mode2", "Solve");
	EquationBrowser equationBrowser = this;

	public EquationBrowser() {

		this.getElement().setId("equationBrowser");

		 Grid modes = new Grid(1, 2);
		 modes.setWidget(0, 0, modeAlg);
		 modes.setWidget(0, 1, modeSci);
		 modes.setStyleName("modes");
		 this.add(modes);

		Grid modes2 = new Grid(1, 2);
		modes2.setWidget(0, 0, modeSolve);
		modes2.setWidget(0, 1, modeEdit);
		modes2.setStyleName("modes");
		this.add(modes2);

		modeAlg.addClickHandler(new ModeSelectHandler(Mode.algebra));
		modeSci.addClickHandler(new ModeSelectHandler(Mode.science));
		modeEdit.addClickHandler(new ModeSelectHandler(Mode.edit));
		modeSolve.addClickHandler(new ModeSelectHandler(Mode.solve));

		modeAlg.setValue(true, true);
		modeSolve.setValue(true, true);
		
		this.add(algebraBrowser);
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
				AlgebraActivity.inEditMode = true;
				break;
			case solve:
				AlgebraActivity.inEditMode = false;
				break;

			}
		}
	}

//	/**
//	 * Single selection handler for equation list
//	 */
//	class EqClickHandler implements ClickHandler {
//		HTMLTable table;
//
//		public EqClickHandler(HTMLTable table) {
//			this.table = table;
//		}
//
//		public void onClick(ClickEvent event) {
//			Cell clickedCell = table.getCellForEvent(event);
//			if (clickedCell != null) {
//				Element clickedEl = clickedCell.getElement();
//
//				if (!clickedEl.getId().equals("selectedEq")) {
//					com.google.gwt.dom.client.Element prevSel = Document.get()
//							.getElementById("selectedEq");
//					if (prevSel != null) {
//						prevSel.removeAttribute("id");
//					}
//					clickedEl.setId("selectedEq");
//				}
//
//				// For Algebra practice mode
//					// For Science Mode
//				} else if (scienceBrowser.eqGrid.equals(table) && modeSci.getValue()) {
//					fillSummary(mathml);
//				}
//			}
//		}
//	}
}
