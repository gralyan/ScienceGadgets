package com.sciencegadgets.client;

import java.util.HashSet;
import java.util.Set;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.VetoDragException;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.HTMLTable.CellFormatter;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.AlgebraManipulation.MLElementWrapper;
import com.sciencegadgets.client.AlgebraManipulation.MathMLDropController;

public class ScienceGadgets implements EntryPoint {

	private final GreetingServiceAsync greetingService = GWT
			.create(GreetingService.class);

	EquationDatabase data = new EquationDatabase();
	private Grid eqGrid = new Grid(1, 1);
	private Grid varGrid = new Grid(1, 1);
	private Grid sumGrid = new Grid(1, 2);
	private Grid algOut = new Grid(1, 1);
	String varGridWidth = "5em";
	String columnWidth = "150em";
	private Label labelSumEq = new Label("");
	private CheckBox multiSwitch = new CheckBox("Multi-Select");
	private Set<String> selectedVars = new HashSet<String>();
	private VerticalPanel algebraPanel;
	private ListBox varBox;
	private ScrollPanel spAlg;
	private ListBox funBox;
	private TextBox coefBox;
	private VerticalPanel algDragPanel = new VerticalPanel();
	private HTML algDragHTML = new HTML();

	public void onModuleLoad() {

		// First box, Variable list
		VerticalPanel vpVar = new VerticalPanel();
		Label labelVar = new Label("Mane's box");
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
		HorizontalPanel browserPanel = new HorizontalPanel();
		browserPanel.add(vpVar);
		browserPanel.add(vpEq);
		browserPanel.add(vpSum);

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
		algebraPanel = new VerticalPanel();
		algebraPanel.setStylePrimaryName("albebraPanel");
		algOut.setWidth("40em");
		spAlg = new ScrollPanel(algOut);
		spAlg.setHeight("5em");
		algebraPanel.add(spAlg);
		// algebraPanel.add(algMenuPanel);
		// ///////////////////////////////
		// experimental
		algebraPanel.add(algDragPanel);
		// ////////////////////////////////

		RootPanel.get().add(browserPanel);
		RootPanel.get().add(algebraPanel);

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

		// make it pretty
		parseJQMath(varGrid.getElement());
		parseJQMath(eqGrid.getElement());

		// /////////////////////////////////////////
		// experimental
		// ////////////////////////////////
/*
		final Button sendButton = new Button("Send");

		RootPanel.get().add(sendButton);
		RootPanel.get().add(new EquationWriter());

		ClickHandler handler = new ClickHandler() {
			public void onClick(ClickEvent event) {
				sendNameToServer();
			}
		};

		sendButton.addClickHandler(handler);
	}

	private void sendNameToServer() {
		String textToServer = "JOHN";

		greetingService.greetServer(textToServer, new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Window.alert("FAIL");
			}

			public void onSuccess(String result) {
				Window.alert(result);
			}
		});
*/	}

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
	private void fillEqGrid(Set<String> varSet) {
		String eqHTML;
		String[] eqList = data.getEquationsByVariables(varSet);
		sumGrid.clear();

		// selectedEqElement.setClassName("");
		// selectedEqElement = null;

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
	private void fillSum(String equation) {
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
		algOut.clear(true);
		algOut.resizeRows(1);
		algOut.setWidget(0, 0, labelSumEq);
		parseJQMath(labelSumEq.getElement());

		createAlg(equation);

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
		varBox.clear();
		varBox.addItem("");
		for (int i = 0; i < sumGrid.getRowCount(); i++) {
			varBox.addItem(variables[i]);
		}

		parseJQMath(varBox.getElement());
	}

	/**
	 * Takes the equation, parses into MathML, adds JavaScript handlers
	 */
	void createAlg(String equation) {
		HTML HTMLb4JavaScript = new HTML();
		HTMLb4JavaScript.setHTML("$" + equation + "$");
		parseJQMath(HTMLb4JavaScript.getElement());

		algDragPanel.clear();
		algDragPanel.add(new Label(HTMLb4JavaScript.toString()));
		// algDragPanel.add(algDragHTML);

		// check the HTML nodes
		NodeList<Node> thirdNodeList = HTMLb4JavaScript.getElement()
				.getFirstChild().getFirstChild().getChildNodes();
		String s3 = "";
		for (int i = 0; i < thirdNodeList.getLength(); i++) {
			Node node = thirdNodeList.getItem(i);
			s3 = s3 + ": " + node.getNodeName() + " " + node.getNodeValue();
		}
		Label l3 = new Label(s3);
		algDragPanel.add(l3);

		
		AbsolutePanel dragPanel = new AbsolutePanel();
		dragPanel.add(HTMLb4JavaScript);
		
//		the Listener version
//		Element asd = HTMLb4JavaScript.getElement();
//		DOM.setEventListener(asd, new AlgDragEventListener());
//		DOM.sinkEvents(asd, Event.ONMOUSEOVER | Event.ONMOUSEOUT);
		
		//wrap mathML element in widget to add handlers
		final com.google.gwt.dom.client.Element elmnt = HTMLb4JavaScript.getElement().getFirstChildElement().getFirstChildElement().getFirstChildElement().getFirstChildElement();
		 MLElementWrapper wrapper = new MLElementWrapper(elmnt, true);
		 wrapper.setStyleName("selectedVar");
		 
		 //register handlers for wrapper
//		 HandlerRegistration handlerRegistration = wrapper.addMouseOverHandler(new MouseOverHandler() {
//			 @Override
//			 public void onMouseOver(MouseOverEvent event) {
//				 Element target = DOM.eventGetCurrentTarget(event.);
//				 Window.alert("click"+elmnt.getInnerText());				
//			 }
//		 });
		 
		 dragPanel.setStyleName("gridBox");
		 RootPanel.get().add(dragPanel);
	        Label la = new Label("dragme");
	        Label lb = new Label("000000000000");
	        dragPanel.add(la, 4, 60);
	        dragPanel.add(lb, 1, 40);
	        PickupDragController dragController = new PickupDragController(dragPanel, false);
	        DropController dropController = new MathMLDropController(lb);
			dragController.registerDropController(dropController);

//	        dragController.makeDraggable(wrapper);
	        dragController.makeDraggable(la);

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
		//		$wnd.M.parseMath($doc.body);
		$wnd.M.parseMath(element);
	}-*/;
	
	class AlgDragEventListener implements EventListener {
		
		public void onBrowserEvent(Event event) {
			Element target = DOM.eventGetCurrentTarget(event);
			
			switch (DOM.eventGetType(event)) {
			case Event.ONMOUSEOVER:
				target.setClassName("selectedVar");
				break;
			case Event.ONMOUSEOUT:
				target.setClassName("");
				break;
				
			}
		}
	}

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

				if (!el.getId().equals("selectedEq")) {
					com.google.gwt.dom.client.Element prevSel = Document.get()
							.getElementById("selectedEq");
					if (prevSel != null) {
						prevSel.setId("");
					}
					el.setId("selectedEq");
				}
				// parse HTML to get appropriate equation
				String html = clickedCell.getElement().getInnerHTML();
				int beginIndex = html.indexOf("<math alttext=\"") + 15;
				int endIndex = html.indexOf("\">", beginIndex);
				String equation = html.substring(beginIndex, endIndex);

				fillSum(equation);
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
				fillEqGrid(selectedVars);
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
				// turning multi on
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

}
