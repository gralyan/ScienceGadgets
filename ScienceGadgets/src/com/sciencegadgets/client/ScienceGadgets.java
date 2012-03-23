package com.sciencegadgets.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ScienceGadgets implements EntryPoint {

	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	
	//test123
	EquationDatabase data = new EquationDatabase();
	private Grid eqGrid;
	private Grid varGrid;
	private Grid sumGrid;
	public com.google.gwt.dom.client.Element selectedEqElement;
	String varGridWidth = "5em";
	String columnWidth = "150em";
	private Label labelVar = new Label("Variables");
	private Label labelEq = new Label("Equations");
	private Label labelSum = new Label("Summary");
	private Label labelSumEq = new Label("");
	private CheckBox multiSwitch = new CheckBox("Multi-Select");
	private List<?> listAllUnits;
	private AsyncCallback<String> a;
	private String b;
	
	public void onModuleLoad() {
		

		// First box, Variable list
		varGrid = new Grid(1, 1);
		fillVarList();
		varGrid.addClickHandler(new VarClickHandler(varGrid));

		// Second box, Equation list
		eqGrid = new Grid(1, 1);
		eqGrid.setWidth("10em");
		eqGrid.addClickHandler(new EqClickHandler(eqGrid));

		// Third box, Variable Descriptions
		sumGrid = new Grid(1, 2);

		// Assemble browserPanel
		HorizontalPanel browserPanel = new HorizontalPanel();

		VerticalPanel vpVar = new VerticalPanel();
		ScrollPanel spVar = new ScrollPanel(varGrid);
		vpVar.add(labelVar);
		vpVar.add(spVar);
		vpVar.add(multiSwitch);

		VerticalPanel vpEq = new VerticalPanel();
		ScrollPanel spEq = new ScrollPanel(eqGrid);
		vpEq.add(labelEq);
		vpEq.add(spEq);

		VerticalPanel vpSum = new VerticalPanel();
		ScrollPanel spSum = new ScrollPanel(sumGrid);
		vpSum.add(labelSum);
		vpSum.add(labelSumEq);
		vpSum.add(spSum);

		// Widget styles
		vpVar.setStylePrimaryName("gridBox");
		vpEq.setStylePrimaryName("gridBox");
		vpSum.setStylePrimaryName("gridBox");

		spVar.setStylePrimaryName("sp");
		spEq.setStylePrimaryName("sp");
		spSum.setStylePrimaryName("sp");

		labelVar.setStylePrimaryName("rowHeader");
		labelEq.setStylePrimaryName("rowHeader");
		labelSum.setStylePrimaryName("rowHeader");

		// Add the widgets to the root panel.
		browserPanel.add(vpVar);
		browserPanel.add(vpEq);
		browserPanel.add(vpSum);

		RootPanel.get().add(browserPanel);

		parseJQMath(varGrid.getElement());
		parseJQMath(eqGrid.getElement());

		multiSwitch.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				CellFormatter elm = varGrid.getCellFormatter();
				for (int i = 0; i < varGrid.getRowCount(); i++) {
					elm.getElement(i, 0).setClassName("");
				}
			}
		});
		
		
		
		final Button sendButton = new Button("Send");

		RootPanel.get().add(sendButton);
		
		ClickHandler handler = new ClickHandler() 
		{
			public void onClick(ClickEvent event) 
			{
				sendNameToServer();
			}
		};
		
		sendButton.addClickHandler(handler);
	}
	
	private void sendNameToServer() {
		String textToServer = "JOHN";

		greetingService.greetServer(textToServer,
				new AsyncCallback<String>() {
					public void onFailure(Throwable caught) {
						Window.alert("FAIL");
					}

					public void onSuccess(String result) {
						Window.alert(result);
					}
				});
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
			varHTML = "<span style=\"cursor:pointer;\">$" + vars[i]
					+ "$ "+ desc[i]+"</span>";
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
	private void fillEqGrid(Set<String> varSet) {
		String eqHTML;
		String[] eqList = data.getEquationsByVariables(varSet);
		sumGrid.clear();

		selectedEqElement.setClassName("");
		selectedEqElement = null;

		eqGrid.resizeRows(eqList.length);

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
	 * Finds the variables with descriptions of the given equation to fill the
	 * variable description (varDesc) box
	 * 
	 * @param equation
	 */
	private void fillVarDesc(String equation) {
		String[] variables;
		try {
			variables = data.getVariablesByEquation(equation);
		} catch (ElementNotFoundExeption e) {
			e.printStackTrace();
			return;
		}
		int row = 0;
		String varHTML, descHTML;

		sumGrid.clear(true);
		sumGrid.resizeRows(variables.length);

		labelSumEq.setText("$" + equation + "$");
		parseJQMath(labelSumEq.getElement());

		for (String var : variables) {
			varHTML = "<span class=\"var\">$" + var + "$ </span>";
			try {
				descHTML = "<span class=\"varName\">$"
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
		
	
	}

	/**
	 * JavaScript method in jqMath that would parse the given element and
	 * display all equations in standard mathematical symbols. This will parse
	 * every string surrounded by $'s
	 * <p>
	 * ex. $ K=1/2mν^2 $
	 * </p>
	 * 
	 * @param element
	 *            - the web element to parse as math
	 */
	static native void parseJQMath(Element element) /*-{
													//		$wnd.M.parseMath($doc.body);
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
				Element el = clickedCell.getElement();

				if (!el.getClassName().equals("selected")) {
					el.setClassName("selected");
					selectedEqElement.setClassName("");
					selectedEqElement = el;
				}
				// parse HTML to get appropriate equation
				String html = clickedCell.getElement().getInnerHTML();
				int beginIndex = html.indexOf("<math alttext=\"") + 15;
				int endIndex = html.indexOf("\">", beginIndex);
				String equation = html.substring(beginIndex, endIndex);

				fillVarDesc(equation);
			}
		}
	}

	/**
	 * The multi-selection handler for variable list
	 */
	class VarClickHandler extends EqClickHandler implements ClickHandler {
		private Set<String> selectedVars = new HashSet<String>();

		public VarClickHandler(HTMLTable table) {
			super(table);
		}

		@Override
		public void onClick(ClickEvent event) {
			Cell clickedCell = table.getCellForEvent(event);

			if (clickedCell != null) {
				Element clicked = clickedCell.getElement();
				String varName = clicked.getInnerText().split(" ")[0];

				if (multiSwitch.getValue()) {
					Document.get().getElementById("selected")
							.setClassName("selected");
					Document.get().getElementById("selected").setId("");
					if (clicked.getClassName().equals("selected")) {
						clicked.setClassName("");
						selectedVars.remove(varName);
					} else {
						clicked.setClassName("selected");
						selectedVars.add(varName);
					}
				} else {
					if (clicked.getClassName().equals("selected")) {
						clicked.setClassName("");
						selectedVars.remove(varName);
					} else {
						Document.get().getElementById("selected").setId("");
						selectedVars.clear();
						clicked.setId("selected");
						selectedVars.add(varName);
					}
				}
				sumGrid.clear(true);
				labelSumEq.setText("");
				fillEqGrid(selectedVars);
			}
		}
	}
}



