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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
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
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.algebramanipulation.AlgOutEntry;
import com.sciencegadgets.client.algebramanipulation.EquationTransporter;
import com.sciencegadgets.client.equationtree.TreeEntry;
import com.sun.java.swing.plaf.windows.resources.windows;

//Uncomment to use as gadget////////////////////////////////////
//
//@com.google.gwt.gadgets.client.Gadget.ModulePrefs(//
//title = "ScienceGadgets", //
//author = "John Gralyan", //
//author_email = "john.gralyan@gmail.com")
//@com.google.gwt.gadgets.client.Gadget.UseLongManifestName(false)
//@com.google.gwt.gadgets.client.Gadget.AllowHtmlQuirksMode(false)
//public class EquationBrowserEntry extends Gadget<UserPreferences> {
public class EquationBrowserEntry implements EntryPoint {

	EquationDatabase data = new EquationDatabase();

	HorizontalPanel browserPanel = new HorizontalPanel();
	private Grid eqGrid = new Grid(1, 1);
	private Grid varGrid = new Grid(1, 1);
	private Grid sumGrid = new Grid(1, 4);
	private Grid algGrid = new Grid(1, 1);
	private CheckBox multiSwitch = new CheckBox("Multi-Select");
	private Set<String> selectedVars = new HashSet<String>();
	private RadioButton modeSelectAlg = new RadioButton("mode", "Algebra");
	private RadioButton modeSelectSci = new RadioButton("mode", "Science");
	private Button sumButton = new Button("Use");
	private Button combineEqButton = new Button("Combine");
	private HashMap<TextBox, Element> inputBinding = new HashMap<TextBox, Element>();
	public static HTML labelSumEq = new HTML("");
	
	private final DatabaseHelperAsync dataBase = GWT
			.create(DatabaseHelper.class);

	// Uncomment to use as gadget////////////////////////
	//
	// @Override
	// protected void init(UserPreferences preferences) {
	@Override
	public void onModuleLoad() {

		//TODO dont catch general exception
//		try {
			browserPanel.setStylePrimaryName("browserPanel");

			Grid modes = new Grid(1, 2);
			modes.setWidget(0, 0, modeSelectAlg);
			modes.setWidget(0, 1, modeSelectSci);
			modes.setStyleName("modes");

			RootPanel.get("scienceGadgetArea").add(modes);
			RootPanel.get("scienceGadgetArea").add(browserPanel);

			modeSelectAlg.addClickHandler(new ModeSelectHandler("algebra"));
			modeSelectSci.addClickHandler(new ModeSelectHandler("science"));

			modeSelectAlg.setValue(true, true);
//			createDatabase();
			createAlgBrowser();
			
//		} catch (Exception e) {
//			e.printStackTrace();
//			Window.alert("Please refresh the page");
//		}
	}


	private void createSciBrowser() {

		// 111111111111111111111111
		// First box, Variable list
		VerticalPanel vpVar = new VerticalPanel();
		Label labelVar = new Label("Variables");			
		varGrid.setStyleName("selectable");
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
		eqGrid.setStyleName("selectable");
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
				VerticalPanel vpAlg = new VerticalPanel();
				Label labelAlg = new Label("Algebra Practice");
				ScrollPanel spAlg = new ScrollPanel(algGrid);
				algGrid.addClickHandler(new EqClickHandler(algGrid));			
				algGrid.setStyleName("selectable");
				vpAlg.add(labelAlg);
				vpAlg.add(spAlg);
				
				vpAlg.setStylePrimaryName("gridBox");
				spAlg.setStylePrimaryName("sp");
				labelAlg.setStylePrimaryName("rowHeader");
				
				// Fill panel
				algGrid.resizeRows(eqList.length);
				for (int i = 0; i < eqList.length; i++) {
					HTML cell = new HTML(eqList[i]);
					algGrid.setWidget(i, 0, cell);
				}
				
				browserPanel.add(vpAlg);
			}
		});
	}

	private void fillVarList() {

		dataBase.getVariables(new AsyncCallback<String[][]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Can't find variables :(");
			}

			public void onSuccess(String[][] result) {
				String [] varSymbols = result[0];
				String[] varNames = result[1];
				
				varGrid.resizeRows(varSymbols.length);
				
				for (int i = 0; i < varSymbols.length; i++) {
					varGrid.setHTML(i, 0, "<math><mi>"+varSymbols[i]+"</mi></math>"+" - "+varNames[i]);
				}
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
			}
		});
	}

	/**
	 * fills the summary box
	 */
	void fillSummary(String equation) {

		labelSumEq.setHTML(equation);
		sumButton.setVisible(true);

		// fill variable summary
		NodeList<com.google.gwt.dom.client.Element> varNodes = labelSumEq.getElement().getElementsByTagName("mi");
		String descHTML;

		sumGrid.clear(true);
		sumGrid.resizeRows(varNodes.getLength());

		inputBinding.clear();

		for (int i=0 ; i<varNodes.getLength() ; i++) {
			Label varLabel = new Label(varNodes.getItem(i).getInnerText());

			TextBox valueInput = new TextBox();
			inputBinding.put(valueInput, (Element) varNodes.getItem(i));
			valueInput.setWidth("4em");

			Button findButton = new Button("Find");
			findButton.addClickHandler(new FindClickHandler(valueInput));

			sumGrid.setWidget(i, 0, varLabel);
//			sumGrid.setHTML(i, 1, descHTML);
			sumGrid.setWidget(i, 2, valueInput);
			sumGrid.setWidget(i, 3, findButton);
		}

		// Fill varBox (algebra menu)
		/*
		 * scienceGadgets.varBox.clear(); scienceGadgets.varBox.addItem(""); for
		 * (int i = 0; i < sumGrid.getRowCount(); i++) {
		 * scienceGadgets.varBox.addItem(variables[i]); }
		 * parseJQMath(scienceGadgets.varBox.getElement());
		 */

	}		
	
	/**
	 * Replaces all inner text of mn tags with random numbers before sending
	 * 
	 * @param equation
	 */
	private void sendAlgebraEquation(String equation) {
		HTML randomizedEquation = new HTML(equation);
		
		NodeList<com.google.gwt.dom.client.Element> variables = randomizedEquation
				.getElement().getElementsByTagName("mn");
		
		for(int i=0 ; i<variables.getLength() ; i++){
			com.google.gwt.dom.client.Element var = variables.getItem(i);
			
			int posOrNeg = Random.nextBoolean() ? 1 : -1;
			int randomNumber = posOrNeg * ((int) (Math.random() * 10) + 1);
			
			var.setInnerText(randomNumber + "");
		}
		
		HTML eq = new HTML(randomizedEquation.toString());
		randomizedEquation.removeFromParent();
		EquationTransporter.transport(eq);
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
						prevSel.setId("");
					}
					clickedEl.setId("selectedEq");
				}

				String equation = null;

				if (table != null) {
					Widget cell = table.getWidget(clickedCell.getRowIndex(),
							clickedCell.getCellIndex());

					equation = cell.getElement().getInnerHTML();
					
					if (table.equals(algGrid)) { // For Algebra practice mode
						sendAlgebraEquation(equation);

					} else if (table.equals(eqGrid)) { // For Science Mode
						if (modeSelectSci.getValue()) {
							fillSummary(clickedEl.getFirstChildElement().getInnerHTML());
						}
					}
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
				
				//The variable symbol and name should be in diferent columns but how do you get a cell element in a table?
//				if(clickedCell.getCellIndex() != 0){
//					table.getHTML(clickedCell.getRowIndex(), 0;)
//				}
				
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
				AlgOutEntry.algOut.clear(true);
				com.google.gwt.dom.client.Element prevSel = Document.get()
						.getElementById("selectedEq");
//				onVarSelect(selectedVars);
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

	class ModeSelectHandler implements ClickHandler {
		String mode = "algebra";

		private ModeSelectHandler(String mode) {
			this.mode = mode;
		}

		public void onClick(ClickEvent event) {
			browserPanel.clear();
			AlgOutEntry.algOut.clear(true);
			AlgOutEntry.algOut.resizeRows(0);
			TreeEntry.apTree.clear();

			if ("algebra".equals(mode)) {
				createAlgBrowser();

			} else if ("science".equals(mode)) {
				createSciBrowser();
				sumGrid.clear(true);

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

//Replace known variables with given values inputed
			for (TextBox box : inputBinding.keySet()) {
				if (box.isEnabled()) {
					try {
						float value = Float.parseFloat(box.getText());
						
						Element oldElement = inputBinding.get(box);
						Element newElement = DOM.createElement("mi");
						oldElement.getParentElement().replaceChild(newElement, oldElement);
//						oldElement.setInnerText(value+"");
						newElement.setInnerText(value+"");
					} catch (NumberFormatException e) {
						Window.alert("All values should be numbers (except for unknown variable to find)");
						return;
					}
				}
			}
			EquationTransporter.transport(labelSumEq);
		}

	}
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	//Database methods
	///////////////////////////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////

	
	
//	private void createDatabase(){
//		try {
//			new RequestBuilder(RequestBuilder.GET, "Equations.xml")
//					.sendRequest("", new RequestCallback() {
//
//						@Override
//						public void onResponseReceived(Request request,
//								Response response) {
//							//Gets the Equations.xml as a string
//							String text = response.getText();
//							//Parses Equation.xml to be queried as needed
//							dataXML = new EquationXMLDatabase(text);
//							createAlgBrowser();
//						}
//
//						@Override
//						public void onError(Request request, Throwable exception) {
//							System.out.println("error");
//
//						}
//
//					});
//		} catch (RequestException e) {
//			System.out.println("couldn't get equations");
//			e.printStackTrace();
//
//		}
//	}
}
