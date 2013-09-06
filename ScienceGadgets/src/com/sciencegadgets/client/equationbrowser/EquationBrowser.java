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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.AlgOut;
import com.sciencegadgets.client.algebra.EquationHTML;

//Uncomment to use as gadget////////////////////////////////////
//
public class EquationBrowser extends FlowPanel {

	EquationDatabase data = new EquationDatabase();

	FlowPanel browserPanel = new FlowPanel();
	private Grid eqGrid = new Grid(1, 1);
	private Grid varGrid = new Grid(1, 1);
	private Grid sumGrid = new Grid(1, 4);
	private Grid algGrid = new Grid(1, 1);
	private Element[] algElements;
	private CheckBox multiSwitch = new CheckBox("Multi-Select");
	private Set<String> selectedVars = new HashSet<String>();
	private RadioButton modeAlg = new RadioButton("mode", "Algebra");
	private RadioButton modeSci = new RadioButton("mode", "Science");
	private RadioButton modeEdit = new RadioButton("mode2", "Edit");
	private RadioButton modeSolve = new RadioButton("mode2", "Solve");
	private Button sumButton = new Button("Use");
	private Button combineEqButton = new Button("Combine");
	private HashMap<TextBox, Element> inputBinding = new HashMap<TextBox, Element>();
	public static HTML labelSumEq = new HTML("");
	private Moderator moderator;

	private final DatabaseHelperAsync dataBase = GWT
			.create(DatabaseHelper.class);

	public EquationBrowser(Moderator moderator) {

		this.moderator = moderator;
		this.setStyleName("equationBrowser");

		//TODO Science Mode
//		Grid modes = new Grid(1, 2);
//		modes.setWidget(0, 0, modeAlg);
//		modes.setWidget(0, 1, modeSci);
//		modes.setStyleName("modes");
//		this.add(modes);

		Grid modes2 = new Grid(1, 2);
		modes2.setWidget(0, 0, modeSolve);
		modes2.setWidget(0, 1, modeEdit);
		modes2.setStyleName("modes");
		this.add(modes2);

		browserPanel.setStyleName("browserPanel");
		this.add(browserPanel);

		modeAlg.addClickHandler(new ModeSelectHandler(Mode.algebra));
		modeSci.addClickHandler(new ModeSelectHandler(Mode.science));
		modeEdit.addClickHandler(new ModeSelectHandler(Mode.edit));
		modeSolve.addClickHandler(new ModeSelectHandler(Mode.solve));

		modeAlg.setValue(true, true);
		modeSolve.setValue(true, true);
		createAlgBrowser();
	}

	private void createSciBrowser() {

		// 111111111111111111111111
		// First box, Variable list
		VerticalPanel vpVar = new VerticalPanel();
		Label labelVar = new Label("Variables");
		varGrid.setStyleName("selection");
		varGrid.getColumnFormatter().setWidth(0, "5em");
		ScrollPanel spVar = new ScrollPanel(varGrid);
		varGrid.addClickHandler(new VarClickHandler(varGrid));
		vpVar.add(labelVar);
		vpVar.add(spVar);
		vpVar.add(multiSwitch);
		multiSwitch.addClickHandler(new MultiSwitchClickHandler());
		fillVarList();

		// 22222222222222222222222222
		// Second box, Equation list
		VerticalPanel vpEq = new VerticalPanel();
		Label labelEq = new Label("Equations");
		ScrollPanel spEq = new ScrollPanel(eqGrid);
		eqGrid.addClickHandler(new EqClickHandler(eqGrid));
		eqGrid.setStyleName("selection");
		eqGrid.setWidth("17em");
		combineEqButton.setVisible(false);
		vpEq.add(labelEq);
		vpEq.add(spEq);
		vpEq.add(combineEqButton);

		// 3333333333333333333333333333
		// Third box, equation summary
		VerticalPanel vpSum = new VerticalPanel();
		Label labelSum = new Label("Summary");
		sumButton.setVisible(false);
		ScrollPanel spSum = new ScrollPanel(sumGrid);
		sumGrid.setWidth("19em");

		HorizontalPanel eqAndButtonPanel = new HorizontalPanel();
		eqAndButtonPanel.setWidth("19em");
		eqAndButtonPanel.add(labelSumEq);
		eqAndButtonPanel.add(sumButton);
		eqAndButtonPanel.setCellHorizontalAlignment(sumButton,
				HasHorizontalAlignment.ALIGN_RIGHT);
		sumButton.addClickHandler(new UseEquation());

		vpSum.add(labelSum);
		vpSum.add(eqAndButtonPanel);
		vpSum.add(spSum);

		// Assemble browserPanel
		// //////////////////////
		browserPanel.add(vpVar);
		browserPanel.add(vpEq);
		browserPanel.add(vpSum);

		// Add styles
		// ///////////
		spVar.setStylePrimaryName("scrollPanel");
		spEq.setStylePrimaryName("scrollPanel");
		spSum.setStylePrimaryName("scrollPanel");

		labelVar.setStylePrimaryName("rowHeader");
		labelEq.setStylePrimaryName("rowHeader");
		labelSum.setStylePrimaryName("rowHeader");

		vpVar.setStylePrimaryName("gridBox");
		vpEq.setStylePrimaryName("gridBox");
		vpSum.setStylePrimaryName("gridBox");

	}

	private void createAlgBrowser() {

		dataBase.getAlgebraEquations(new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Can't find algebra equations :(");
			}

			public void onSuccess(String[] eqList) {
				FlowPanel gridBox = new FlowPanel();
				Label labelAlg = new Label("Algebra Practice");
				ScrollPanel spAlg = new ScrollPanel(algGrid);
				algGrid.addClickHandler(new EqClickHandler(algGrid));
				spAlg.setStyleName("selection");
				gridBox.add(labelAlg);
				gridBox.add(spAlg);

				gridBox.setStylePrimaryName("gridBox");
//				spAlg.setStylePrimaryName("scrollPanel");
				labelAlg.setStylePrimaryName("rowHeader");

				// Fill panel
				algGrid.resizeRows(eqList.length);
				algElements = new Element[eqList.length];
				for (int i = 0; i < eqList.length; i++) {
					
					//TODO for deployment
					Element mathElement = (Element) new HTML(eqList[i]).getElement().getFirstChildElement();
					EquationHTML cell = new EquationHTML(mathElement);
					algElements[i] = mathElement;
					//TODO for development
//					HTML cell = new HTML(eqList[i]);
					
					algGrid.setWidget(i, 0, cell);
				}

				browserPanel.add(gridBox);
				
//				JSNICalls.parseMathJax(algGrid.getElement());
			}
		});
	}

	private void fillVarList() {

		dataBase.getVariables(new AsyncCallback<String[][]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Can't find variables :(");
			}

			public void onSuccess(String[][] result) {
				String[] varSymbols = result[0];
				String[] varNames = result[1];

				varGrid.resizeRows(varSymbols.length);

				for (int i = 0; i < varSymbols.length; i++) {
					varGrid.setHTML(i, 0, "<math><mi>" + varSymbols[i]
							+ "</mi></math>" + " - " + varNames[i]);
				}
//				JSNICalls.parseMathJax(varGrid.getElement());
			}
		});
	}

	/**
	 * Finds all the equations with every variable in the given set, then fills
	 * the equation list
	 * 
	 * @param varSet
	 *            - set of variables
	 */
	private void onVarSelect(String[] vars) {

		dataBase.getEquationsByVariables(vars, new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Can't find variables :(");
			}

			public void onSuccess(String[] eqList) {
				eqGrid.resizeRows(eqList.length);
				combineEqButton.setVisible(true);
				sumGrid.clear();

				for (int i = 0; i < eqList.length; i++) {
					HTML html = new HTML(eqList[i]);
					eqGrid.setWidget(i, 0, html);
					eqGrid.getCellFormatter().setAlignment(i, 0,
							HasHorizontalAlignment.ALIGN_CENTER,
							HasVerticalAlignment.ALIGN_MIDDLE);
				}
//				JSNICalls.parseMathJax(eqGrid.getElement());
			}
		});
	}

	/**
	 * fills the summary box
	 */
	void fillSummary(Element mathml) {

		labelSumEq.getElement().appendChild(mathml);
		sumButton.setVisible(true);

		// fill variable summary
		NodeList<com.google.gwt.dom.client.Element> varNodes = labelSumEq
				.getElement().getElementsByTagName("mi");

		sumGrid.clear(true);
		sumGrid.resizeRows(varNodes.getLength());

		inputBinding.clear();

		for (int i = 0; i < varNodes.getLength(); i++) {
			Label varLabel = new Label(varNodes.getItem(i).getInnerText());

			TextBox valueInput = new TextBox();
			inputBinding.put(valueInput, (Element) varNodes.getItem(i));
			valueInput.setWidth("4em");

			Button findButton = new Button("Find");
			findButton.addClickHandler(new FindClickHandler(valueInput));

			sumGrid.setWidget(i, 0, varLabel);
			// sumGrid.setHTML(i, 1, descHTML);
			sumGrid.setWidget(i, 2, valueInput);
			sumGrid.setWidget(i, 3, findButton);
		}

	}

	// //////////////////////////////////////////
	// //////////////////////////////////////////
	// Handlers
	// //////////////////////////////////////////
	// //////////////////////////////////////////

	/**
	 * Single selection handler for equation list
	 */
	class EqClickHandler implements ClickHandler {
		HTMLTable table;

		public EqClickHandler(HTMLTable table) {
			this.table = table;
		}

		public void onClick(ClickEvent event) {
			Cell clickedCell = table.getCellForEvent(event);
			if (clickedCell != null) {
				Element clickedEl = clickedCell.getElement();

				if (!clickedEl.getId().equals("selectedEq")) {
					com.google.gwt.dom.client.Element prevSel = Document.get()
							.getElementById("selectedEq");
					if (prevSel != null) {
						prevSel.removeAttribute("id");
					}
					clickedEl.setId("selectedEq");
				}
//TODO for development				
//				Element mathml = (Element) ((Element)clickedCell.getElement().getElementsByTagName("math").getItem(0)).cloneNode(true);
//TODO  for deployment
				Element mathml =(Element) algElements[clickedCell.getRowIndex()].cloneNode(true);
				
				if (algGrid.equals(table) && modeAlg.getValue()) { // For Algebra practice mode
					moderator.makeAlgebraWorkspace(mathml);

				} else if (eqGrid.equals(table) && modeSci.getValue()) { // For Science Mode
						fillSummary(mathml);
				}
			}
		}
	}

	/**
	 * The multi-selection handler for variable list
	 */
	class VarClickHandler implements ClickHandler {
		Grid table;

		public VarClickHandler(HTMLTable table) {
			this.table = (Grid) table;
		}

		public void onClick(ClickEvent event) {
			Cell clickedCell = table.getCellForEvent(event);

			if (clickedCell != null) {

				// The variable symbol and name should be in diferent columns
				// but how do you get a cell element in a table?
				// if(clickedCell.getCellIndex() != 0){
				// table.getHTML(clickedCell.getRowIndex(), 0;)
				// }

				Element clicked = clickedCell.getElement();
				String varName = clicked.getInnerText().split(" ")[0];

				// Multi select
				if (multiSwitch.getValue()) {

					if (clicked.getClassName().equals("selectedVar")) {
						clicked.setClassName("");
						selectedVars.remove(varName);
					} else {
						clicked.setClassName("selectedVar");
						selectedVars.add(varName);
					}
					// Single select
				} else {
					if (clicked.getId().equals("selectedVar")) {
					} else {
						com.google.gwt.dom.client.Element prevSelect = Document
								.get().getElementById("selectedVar");
						if (prevSelect != null) {
							prevSelect.setId("");
						}
						selectedVars.clear();
						clicked.setId("selectedVar");
						selectedVars.add(varName);
					}
				}
				sumGrid.clear(true);
				labelSumEq.setText("");
				sumButton.setVisible(false);
//				AlgOut.algOut.clear(true);
				com.google.gwt.dom.client.Element prevSel = Document.get()
						.getElementById("selectedEq");
				// onVarSelect(selectedVars);
				onVarSelect(selectedVars.toArray(new String[0]));

				if (prevSel != null) {
					prevSel.setId("");
				}
			}
		}
	}

	class MultiSwitchClickHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			if (multiSwitch.getValue()) {
				// turning Multi-Select on
				com.google.gwt.dom.client.Element el = Document.get()
						.getElementById("selectedVar");
				if (el != null) {
					el.setClassName("selectedVar");
					el.setId("");
				}
			} else {
				// turning multi off
				eqGrid.clear(true);
				CellFormatter elm = varGrid.getCellFormatter();
				for (int i = 0; i < varGrid.getRowCount(); i++) {
					elm.getElement(i, 0).setClassName("");
				}
			}
		}
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
//			AlgOut.algOut.clear(true);
//			AlgOut.algOut.resizeRows(0);

			switch (mode) {
			case algebra:
				browserPanel.clear();
				createAlgBrowser();
				break;
			case science:
				browserPanel.clear();
				createSciBrowser();
				sumGrid.clear(true);
				break;
			case edit:
				Moderator.inEditMode = true;
				break;
			case solve:
				Moderator.inEditMode = false;
				break;

			}
		}
	}

	class FindClickHandler implements ClickHandler {
		TextBox textBox;

		FindClickHandler(TextBox textBox) {
			this.textBox = textBox;
		}

		@Override
		public void onClick(ClickEvent arg0) {

			boolean unknownExists = false;

			for (TextBox box : inputBinding.keySet()) {
				if (!box.isEnabled()) {
					unknownExists = true;
				}
			}

			if (unknownExists) {
				Window.alert("There should only be one unknown variable to find");
			} else {
				textBox.setText("???");
				textBox.setEnabled(false);
			}
		}
	}

	class UseEquation implements ClickHandler {

		@Override
		public void onClick(ClickEvent arg0) {

			// Replace known variables with given values inputed
			for (TextBox box : inputBinding.keySet()) {
				if (box.isEnabled()) {
					try {
						float value = Float.parseFloat(box.getText());

						Element oldElement = inputBinding.get(box);
						Element newElement = DOM.createElement("mi");
						oldElement.getParentElement().replaceChild(newElement,
								oldElement);
						// oldElement.setInnerText(value+"");
						newElement.setInnerText(value + "");
					} catch (NumberFormatException e) {
						Window.alert("All values should be numbers (except for unknown variable to find)");
						return;
					}
				}
			}
			moderator.makeAlgebraWorkspace(labelSumEq.getElement()
					.getFirstChildElement());
		}

	}
	// //////////////////////////////////////////////////////////////////////////////
	// //////////////////////////////////////////////////////////////////////////////
	// Database methods
	// /////////////////////////////////////////////////////////////////////////////
	// /////////////////////////////////////////////////////////////////////////////

	// private void createDatabase(){
	// try {
	// new RequestBuilder(RequestBuilder.GET, "Equations.xml")
	// .sendRequest("", new RequestCallback() {
	//
	// @Override
	// public void onResponseReceived(Request request,
	// Response response) {
	// //Gets the Equations.xml as a string
	// String text = response.getText();
	// //Parses Equation.xml to be queried as needed
	// dataXML = new EquationXMLDatabase(text);
	// createAlgBrowser();
	// }
	//
	// @Override
	// public void onError(Request request, Throwable exception) {
	// System.out.println("error");
	//
	// }
	//
	// });
	// } catch (RequestException e) {
	// System.out.println("couldn't get equations");
	// e.printStackTrace();
	//
	// }
	// }
}
