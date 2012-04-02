package com.sciencegadgets.client;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Document;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.sciencegadgets.client.AlgebraManipulation.EquationTree;
import com.sciencegadgets.client.AlgebraManipulation.MLElementWrapper;

public class ScienceGadgets implements EntryPoint {

	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	EquationDatabase data = new EquationDatabase();
	HorizontalPanel browserPanel = new HorizontalPanel();
	private Grid eqGrid = new Grid(1, 1);
	private Grid varGrid = new Grid(1, 1);
	private Grid sumGrid = new Grid(1, 2);
	private Grid algGrid = new Grid(1, 1);
	private Grid algOut = new Grid(1, 1);
	String varGridWidth = "5em";
	String columnWidth = "150em";
	private HTML labelSumEq = new HTML("");
	private CheckBox multiSwitch = new CheckBox("Multi-Select");
	private Set<String> selectedVars = new HashSet<String>();
	private HorizontalPanel algebraPanel = new HorizontalPanel();
	private ListBox varBox;
	private ListBox funBox;
	private TextBox coefBox;
	private AbsolutePanel algDragPanel = new AbsolutePanel();
	private HTML algDragHTML = new HTML();
	private AbsolutePanel eqTreePanel = new AbsolutePanel();
	private ScrollPanel spAlg = new ScrollPanel(algOut);
	private String selectedEquation;
	private RadioButton modeSelectAlg = new RadioButton("mode", "Algebra Mode");
	private RadioButton modeSelectSci = new RadioButton("mode", "Science Mode");

	private ScrollPanel spTree;

	public void onModuleLoad() {
		modeSelectAlg.setSize("50em", "10em");
		modeSelectSci.setSize("50em", "10em");

		createAlgebraPanel();
		browserPanel.setStylePrimaryName("browserPanel");

		RootPanel.get().add(modeSelectAlg);
		RootPanel.get().add(modeSelectSci);
		RootPanel.get().add(browserPanel);
		RootPanel.get().add(algebraPanel);

		ModeSelectHandler modeSelectHandle = new ModeSelectHandler();
		modeSelectAlg.addClickHandler(modeSelectHandle);
		modeSelectSci.addClickHandler(modeSelectHandle);

		// /////////////////////////////////////////
		// experimental
		// ////////////////////////////////

		final Button sendButton = new Button("Send");

		RootPanel.get().add(sendButton);
		// RootPanel.get().add(new EquationWriter());

		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				string2MathML_BySymja_OnServer("a+b");
			}
		};

		sendButton.addClickHandler(handler);
	}

	private void createSciBrowser() {
		// First box, Variable list
		VerticalPanel vpVar = new VerticalPanel();
		Label labelVar = new Label("Variables");
		ScrollPanel spVar = new ScrollPanel(varGrid);
		varGrid.addClickHandler(new VarClickHandler(varGrid));
		vpVar.add(labelVar);
		vpVar.add(spVar);
		vpVar.add(multiSwitch);
		multiSwitch.addClickHandler(new MultiSwitchClickHandler());
		fillVarList();

		// Second box, Equation list
		VerticalPanel vpEq = new VerticalPanel();
		Label labelEq = new Label("Equations");
		ScrollPanel spEq = new ScrollPanel(eqGrid);
		eqGrid.addClickHandler(new EqClickHandler(eqGrid));
		vpEq.add(labelEq);
		vpEq.add(spEq);

		// Third box, equation summary
		VerticalPanel vpSum = new VerticalPanel();
		Label labelSum = new Label("Summary");
		ScrollPanel spSum = new ScrollPanel(sumGrid);
		labelSumEq.setHeight("2em");
		vpSum.add(labelSum);
		vpSum.add(labelSumEq);
		vpSum.add(spSum);

		// Assemble browserPanel
		browserPanel.add(vpVar);
		browserPanel.add(vpEq);
		browserPanel.add(vpSum);

		// Add styles
		vpVar.setStylePrimaryName("gridBox");
		vpEq.setStylePrimaryName("gridBox");
		vpSum.setStylePrimaryName("gridBox");

		spVar.setStylePrimaryName("sp");
		spEq.setStylePrimaryName("sp");
		spSum.setStylePrimaryName("sp");

		labelVar.setStylePrimaryName("rowHeader");
		labelEq.setStylePrimaryName("rowHeader");
		labelSum.setStylePrimaryName("rowHeader");

		parseJQMath(varGrid.getElement());

	}

	private void createAlgBrowser() {
		VerticalPanel vpAlg = new VerticalPanel();
		Label labelAlg = new Label("Algebra Practice");
		ScrollPanel spAlg = new ScrollPanel(algGrid);
		algGrid.addClickHandler(new EqClickHandler(algGrid));
		vpAlg.add(labelAlg);
		vpAlg.add(spAlg);

		vpAlg.setStylePrimaryName("gridBox");
		spAlg.setStylePrimaryName("sp");
		labelAlg.setStylePrimaryName("rowHeader");

		// Fill panel
		String[] algNameList = data.getAll(data.FLAG_ALGEBRA_NAME);
		algGrid.resizeRows(algNameList.length);
		for (int i = 0; i < algNameList.length; i++) {
			algGrid.setText(i, 0, algNameList[i]);
		}

		browserPanel.add(vpAlg);

	}

	private void createAlgebraPanel() {

		// Assemble algebra menu panel
		HorizontalPanel algMenuPanel = new HorizontalPanel();
		algMenuPanel.setHeight("2em");
		algMenuPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_BOTTOM);
		// function
		funBox = new ListBox();
		for (String fun : data.functions) {
			funBox.addItem(fun);
		}
		algMenuPanel.add(funBox);
		// coefficient
		coefBox = new TextBox();
		coefBox.setValue("1");
		coefBox.setWidth("2em");
		algMenuPanel.add(coefBox);
		// variable
		varBox = new ListBox();
		algMenuPanel.add(varBox);
		// to both sides button
		Button toBothSides = new Button("To Both Sides",
				new ToBothSidesHandler());
		toBothSides.setWidth("5em");
		algMenuPanel.setHeight("2em");
		algMenuPanel.add(toBothSides);

		// Assemble Algebra panel
		algebraPanel
				.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		algebraPanel.setStyleName("albebraPanel");
		VerticalPanel AlgebraVerticalPanel = new VerticalPanel();
		AlgebraVerticalPanel.add(spAlg);
		AlgebraVerticalPanel.add(algDragPanel);
		algebraPanel.add(AlgebraVerticalPanel);
		spTree = new ScrollPanel(eqTreePanel);
		algebraPanel.add(spTree);

		// Add styles
		algOut.setStyleName("algOutPanel");
		spAlg.setStyleName("algOutPanel");
		algDragPanel.setStyleName("algDragPanel");
		// eqTreePanel.setStyleName("treePanel");
		spTree.setStyleName("treePanel");

		// make it pretty
		parseJQMath(varGrid.getElement());
		parseJQMath(eqGrid.getElement());

	}

	/**
	 * Gets all the available variables and fills the list
	 * 
	 * @param varSet
	 *            - set of variables
	 */
	private void fillVarList() {
		String varHTML;
		String[] vars = data.getAll(data.FLAG_VARIABLE_LOOK);
		String[] desc = data.getAll(data.FLAG_VARIABLE_DESCRIPTION);
		varGrid.resizeRows(vars.length);

		for (int i = 0; i < vars.length; i++) {
			varHTML = "<span style=\"cursor:pointer;\">$" + vars[i] + "$ "
					+ desc[i] + "</span>";
			varGrid.setHTML(i, 0, varHTML);
		}
	}

	/**
	 * Finds all the equations with every variable in the given set, then fills
	 * the equation list
	 * 
	 * @param varSet
	 *            - set of variables
	 */
	private void onVarSelect(Set<String> varSet) {
		String eqHTML;
		String[] eqList = data.getEquationsByVariables(varSet);

		// selectedEqElement.setClassName("");
		// selectedEqElement = null;

		eqGrid.resizeRows(eqList.length);

		sumGrid.clear();
		for (int i = 0; i < eqList.length; i++) {
			eqHTML = "<span style=\"cursor:pointer;width:10em;\">$" + eqList[i]
					+ "$</span>";
			eqGrid.setHTML(i, 0, eqHTML);
			eqGrid.getCellFormatter().setAlignment(i, 0,
					HasHorizontalAlignment.ALIGN_CENTER,
					HasVerticalAlignment.ALIGN_MIDDLE);
		}
		parseJQMath(eqGrid.getElement());
	}

	/**
	 * Async call to server to parse the equation using Symja
	 * 
	 * @param textToServer
	 */
	private void string2MathML_BySymja_OnServer(String textToServer) {

		greetingService.greetServer(textToServer, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Window.alert("Math parseing FAIL :(");
			}

			public void onSuccess(String result) {
				labelSumEq.setHTML(result);
				onEqSelect(result);
			}
		});
	}

	/**
	 * Finds the variables with descriptions of the given equation to fill the
	 * variable description (varDesc) box
	 * 
	 * @param equation
	 */
	private void onEqSelect(String equation) {

		// Initial AlgOut line
		// labelSumEq.setText("$" + equation + "$");
		labelSumEq.setHTML(equation);
		HTML algOutFirstHTML = new HTML(equation);
		algOut.clear(true);
		algOut.resizeRows(1);
		algOut.setWidget(0, 0, algOutFirstHTML);
		// parseJQMath(labelSumEq.getElement());

		// make algebra manipulator
		HTML draggableEquation = new HTML();
		// draggableEquation.setHTML("$" + equation + "$");
		draggableEquation.setHTML(equation);
		// parseJQMath(draggableEquation.getElement());
		// The main mathML element is fmath in chrome, math in firefox
		Element draggableEquationElement = (Element) draggableEquation
				.getElement().getElementsByTagName("math").getItem(0);
		if (draggableEquationElement == null) {
			draggableEquationElement = (Element) draggableEquation.getElement()
					.getElementsByTagName("fmath").getItem(0);
		}
		DOM.setElementAttribute(draggableEquationElement, "mathsize", "300%");
		algDragPanel.clear();
		algDragPanel.add(draggableEquation);

		// Make EquationTree
		EquationTree eqTree = new EquationTree(draggableEquation);
		eqTreePanel.clear();
		eqTreePanel.add(eqTree);

		Iterator<TreeItem> it = eqTree.treeItemIterator();
		while (it.hasNext()) {
			TreeItem nextTreeItem = it.next();
			nextTreeItem.setState(true, false);
			parseJQMath(nextTreeItem.getElement());
		}

		// Make draggable overlays on the equation
		int algLeft = algDragPanel.getAbsoluteLeft();
		int algTop = algDragPanel.getAbsoluteTop();

		for (MLElementWrapper wrap : eqTree.wrappers) {
			try {
				int wrapLeft = wrap.getElementWrapped().getAbsoluteLeft();
				int wrapTop = wrap.getElementWrapped().getAbsoluteTop();

				int positionLeft = wrapLeft - algLeft;
				int positionTop = wrapTop - algTop;
				
				int width = (int) ((0.75) * draggableEquation.getOffsetHeight());
				int height = draggableEquation.getOffsetHeight();
				
				wrap.setWidth(width + "px");
				wrap.setHeight(height + "px");

				algDragPanel.add(wrap, positionLeft, positionTop);
			} catch (JavaScriptException e) {
			}
		}
		
		if (modeSelectSci.getValue()) {
			fillSummary();
		}
	}

	/**
	 * fills the summary box
	 */
	void fillSummary() {
		// fill variable summary
		String[] variables;
		try {
			variables = data.getVariablesByEquation(selectedEquation);
		} catch (ElementNotFoundExeption e) {
			e.printStackTrace();
			return;
		}
		int row = 0;
		String varHTML, descHTML;

		sumGrid.clear(true);
		sumGrid.resizeRows(variables.length);

		for (String var : variables) {
			varHTML = "<span class=\"var\">$" + var + "$ </span>";
			try {
				descHTML = "<span>$"
						+ data.getAttribute(data.FLAG_VARIABLE_DESCRIPTION, var)
						+ "$</span>";
			} catch (ElementNotFoundExeption e) {
				e.printStackTrace();
				descHTML = "<span></span>";
			}

			sumGrid.setHTML(row, 0, varHTML);
			sumGrid.setHTML(row, 1, descHTML);
			row++;
		}
		parseJQMath(sumGrid.getElement());

		// Fill varBox (algebra menu)
		varBox.clear();
		varBox.addItem("");
		for (int i = 0; i < sumGrid.getRowCount(); i++) {
			varBox.addItem(variables[i]);
		}
		parseJQMath(varBox.getElement());
	}

	/**
	 * JavaScript method in jqMath that would parse the given element and
	 * display all equations in standard mathematical symbols. This will parse
	 * every string surrounded by $'s
	 * <p>
	 * ex. $ K=1/2mÎ½^2 $
	 * </p>
	 * 
	 * @param element
	 *            - the web element to parse as math
	 */
	static native void parseJQMath(Element element) /*-{
		$wnd.M.parseMath(element);
	}-*/;

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
				String sendEq = null;

				// For Algebra practice mode
				if (table.equals(algGrid)) {
					try {
						equation = data.getAlgAttribute(
								data.FLAG_ALGEBRA_EQUATION,
								clickedEl.getInnerText());
						sendEq = equation;
					} catch (ElementNotFoundExeption e) {
						e.printStackTrace();
					}

					// For Science Mode
				} else if (table.equals(eqGrid)) {

					Element element = (Element) clickedEl.getElementsByTagName(
							"math").getItem(0);
					if (element == null) {
						Window.alert("Your browser may not show everything correctly. Try another browser");
						element = (Element) clickedEl.getElementsByTagName(
								"fmath").getItem(0);
					}
					equation = DOM.getElementAttribute(element, "alttext");
					try {
						sendEq = data.getAttribute(data.FLAG_EQUATION_SYMJA,
								equation);
					} catch (ElementNotFoundExeption e) {
						e.printStackTrace();
					}
				}
				selectedEquation = equation;
				string2MathML_BySymja_OnServer(sendEq);
				// onEqSelect(equation);
			}
		}
	}

	/**
	 * The multi-selection handler for variable list
	 */
	class VarClickHandler implements ClickHandler {
		HTMLTable table;

		public VarClickHandler(HTMLTable table) {
			this.table = table;
		}

		public void onClick(ClickEvent event) {
			Cell clickedCell = table.getCellForEvent(event);

			if (clickedCell != null) {
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
				algOut.clear(true);
				onVarSelect(selectedVars);
				com.google.gwt.dom.client.Element prevSel = Document.get()
						.getElementById("selectedEq");
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

	class ToBothSidesHandler implements ClickHandler {

		public void onClick(ClickEvent event) {
			int newRowCount = algOut.getRowCount() + 2;
			algOut.resizeRows(newRowCount);

			String inpFun = funBox.getItemText(funBox.getSelectedIndex());
			String inpCoef = coefBox.getText();
			String inpVar = varBox.getItemText(varBox.getSelectedIndex());

			try {
				Integer.parseInt(inpCoef);
			} catch (NumberFormatException e) {
				Window.alert("The coefficient must be a number");
				return;
			}

			algOut.setWidget(newRowCount - 2, 0, new Label(inpFun + inpCoef
					+ inpVar + "    " + inpFun + inpCoef + inpVar));
			algOut.setWidget(newRowCount - 1, 0, new Label("equation"));
			spAlg.scrollToBottom();
		}

	}

	class ModeSelectHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			RadioButton clicked = ((RadioButton) event.getSource());

			if (clicked.equals(modeSelectAlg)) {
				browserPanel.clear();
				algDragPanel.clear();
				algOut.clear(true);
				eqTreePanel.clear();
				createAlgBrowser();
			} else if (clicked.equals(modeSelectSci)) {
				browserPanel.clear();
				algDragPanel.clear();
				algOut.clear(true);
				eqTreePanel.clear();
				createSciBrowser();
			}
		}
	}
}
