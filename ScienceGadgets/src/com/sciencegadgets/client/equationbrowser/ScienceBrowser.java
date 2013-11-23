package com.sciencegadgets.client.equationbrowser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.SelectionPanel.Cell;
import com.sciencegadgets.client.entities.DataModerator;

public class ScienceBrowser extends FlowPanel {

	private SelectionPanel varSelection = null;
	private SelectionPanel eqSelection = null;
	public Grid sumGrid = new Grid(1, 4);
	private Button combineEqButton = new Button("Combine");
	private CheckBox multiSwitch = new CheckBox("Multi-Select");
	private Set<String> selectedVars = new HashSet<String>();
	private HashMap<TextBox, Element> inputBinding = new HashMap<TextBox, Element>();
	private Button sumButton = new Button("Use");
	public static HTML labelSumEq = new HTML("");
	
	public ScienceBrowser() {
		super();
		
		// (1) First box, Variable list
		varSelection = new SelectionPanel("Variables",new SelectionHandler() {
			
			@Override
			public void onSelect(Cell selected) {
				DataModerator.fill_UnitsByQuantity(selected.getValue(), eqSelection);
			}
		});
		varSelection.addStyleName("sciSelectionBox");
		DataModerator.fill_Quantities(varSelection);
//		varSelection.add(multiSwitch);
//		multiSwitch.addClickHandler(new MultiSwitchClickHandler());

		// (2) Second box, Equation list
		eqSelection = new SelectionPanel("Equations",new SelectionHandler() {
			
			@Override
			public void onSelect(Cell selected) {
			}
		});
		eqSelection.addStyleName("algSelectionBox");
		eqSelection.addStyleName("sciSelectionBox");
//		eqGrid.addClickHandler(new EqClickHandler(eqGrid));
//		combineEqButton.setVisible(false);
//		vpEq.add(combineEqButton);

		// (3) Third box, equation summary
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
		this.add(varSelection);
		this.add(eqSelection);
		this.add(vpSum);
	}

	/**
	 * The multi-selection handler for variable list
	 */
//	class VarClickHandler implements ClickHandler {
//		Grid table;
//
//		public VarClickHandler(HTMLTable table) {
//			this.table = (Grid) table;
//		}
//
//		public void onClick(ClickEvent event) {
//			Cell clickedCell = table.getCellForEvent(event);
//
//			if (clickedCell != null) {
//
//				// The variable symbol and name should be in diferent columns
//				// but how do you get a cell element in a table?
//				// if(clickedCell.getCellIndex() != 0){
//				// table.getHTML(clickedCell.getRowIndex(), 0;)
//				// }
//
//				Element clicked = clickedCell.getElement();
//				String varName = clicked.getInnerText().split(" ")[0];
//
//				// Multi select
//				if (multiSwitch.getValue()) {
//
//					if (clicked.getClassName().equals("selectedVar")) {
//						clicked.setClassName("");
//						selectedVars.remove(varName);
//					} else {
//						clicked.setClassName("selectedVar");
//						selectedVars.add(varName);
//					}
//					// Single select
//				} else {
//					if (clicked.getId().equals("selectedVar")) {
//					} else {
//						com.google.gwt.dom.client.Element prevSelect = Document
//								.get().getElementById("selectedVar");
//						if (prevSelect != null) {
//							prevSelect.setId("");
//						}
//						selectedVars.clear();
//						clicked.setId("selectedVar");
//						selectedVars.add(varName);
//					}
//				}
//				sumGrid.clear(true);
//				labelSumEq.setText("");
//				sumButton.setVisible(false);
//				// AlgOut.algOut.clear(true);
//				com.google.gwt.dom.client.Element prevSel = Document.get()
//						.getElementById("selectedEq");
//				// onVarSelect(selectedVars);
////				onVarSelect(selectedVars.toArray(new String[0]));
//
//				if (prevSel != null) {
//					prevSel.setId("");
//				}
//			}
//		}
//	}


	/**
	 * fills the summary box
	 */
	void fillSummary(Element mathml) {

		labelSumEq.getElement().appendChild(mathml);
		sumButton.setVisible(true);

		// fill variable summary
//		NodeList<com.google.gwt.dom.client.Element> varNodes = labelSumEq
//				.getElement().getElementsByTagName("mi");
//
//		sumGrid.clear(true);
//		sumGrid.resizeRows(varNodes.getLength());
//
//		inputBinding.clear();
//
//		for (int i = 0; i < varNodes.getLength(); i++) {
//			Label varLabel = new Label(varNodes.getItem(i).getInnerText());
//
//			TextBox valueInput = new TextBox();
//			inputBinding.put(valueInput, (Element) varNodes.getItem(i));
//			valueInput.setWidth("4em");
//
//			Button findButton = new Button("Find");
//			findButton.addClickHandler(new FindClickHandler(valueInput));
//
//			sumGrid.setWidget(i, 0, varLabel);
//			// sumGrid.setHTML(i, 1, descHTML);
//			sumGrid.setWidget(i, 2, valueInput);
//			sumGrid.setWidget(i, 3, findButton);
//		}

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
//				eqGrid.clear(true);
//				CellFormatter elm = varGrid.getCellFormatter();
//				for (int i = 0; i < varGrid.getRowCount(); i++) {
//					elm.getElement(i, 0).setClassName("");
//				}
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
			Moderator.makeAlgebraWorkspace(labelSumEq.getElement()
					.getFirstChildElement());
		}

	}
}
