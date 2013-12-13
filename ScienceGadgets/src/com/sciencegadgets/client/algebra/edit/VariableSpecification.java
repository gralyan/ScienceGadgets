package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.Moderator.Activity;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

public class VariableSpecification extends Prompt {

	interface VariableSpecUiBinder extends UiBinder<FlowPanel, VariableSpecification> {
	}
	private static VariableSpecUiBinder variableSpecUiBinder = GWT
			.create(VariableSpecUiBinder.class);
	
	@UiField
	Grid symbolPalette;
	@UiField
	TextBox symbolInput;
	@UiField
	FlowPanel unitSelectionHolder;
	
	private MathNode node;
//	private Grid grid = new Grid(4, 12);
//	private Button useButton = new Button("Select Symbol", new UseHandler());

	private final String VARIABLE_INSERT = "Special Symbols";

	private String[][] greekLetters = { { "α", "Α", "alpha" },
			{ "β", "Β", "beta" }, { "γ", "Γ", "gamma" }, { "δ", "Δ", "delta" },
			{ "ε", "Ε", "epsilon" }, { "ζ", "Ζ", "zeta" }, { "η", "Η", "eta" },
			{ "θ", "Θ", "theta" }, { "ι", "Ι", "iota" }, { "κ", "Κ", "kappa" },
			{ "λ", "Λ", "lambda" }, { "μ", "Μ", "mu" }, { "ν", "Ν", "nu" },
			{ "ξ", "Ξ", "xi" }, { "ο", "Ο", "omicron" }, { "π", "Π", "pi" },
			{ "ρ", "Ρ", "rho" }, { "σ", "Σ", "sigma" }, { "τ", "Τ", "tau" },
			{ "υ", "Υ", "upsilon" }, { "φ", "Φ", "phi" }, { "χ", "Χ", "chi" },
			{ "ψ", "Ψ", "psi" }, { "ω", "Ω", "omega" } };
	public Cell selectedSymbol;

	public VariableSpecification(EditMenu editMenu) {
		super();

		add(variableSpecUiBinder.createAndBindUi(this));
		
		this.node = editMenu.node;

		//1st Component - symbol
//		this.symbolInput = new TextBox();
		symbolInput.addClickHandler(new FocusOnlyClickHandler());
		symbolInput.addTouchStartHandler(new FocusOnlyTouchHandler());
		symbolInput.setText(node.getSymbol());
		editMenu.focusable = symbolInput;
//		add(symbolInput);
		
		//Symbols
		symbolPalette.resize(4, 12);
		for (int j = 0; j < 12; j++) {
			symbolPalette.setHTML(0, j, greekLetters[j][0]);
			symbolPalette.setHTML(1, j, greekLetters[j + 12][0]);
			symbolPalette.setHTML(2, j, greekLetters[j][1]);
			symbolPalette.setHTML(3, j, greekLetters[j + 12][1]);
		}
		symbolPalette.addClickHandler(new SymbolClickHandler());
		
		//2nd Component - QuantityKind
		UnitSelection quantityBox = new UnitSelection(true, false);
		quantityBox.setSize("100%", "100%");
		unitSelectionHolder.add(quantityBox);
		
		//3rd Component - Use button
		addOkHandler(new EditSpecHandler(
				editMenu, symbolInput, quantityBox.quantityBox));
		
//		this.getStyleElement().getStyle().setBackgroundColor("#ADD850");
	}
	

	private class SymbolClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			Cell selectedCell = symbolPalette.getCellForEvent(event);

			if (selectedCell != null) {

//				useButton.setEnabled(true);
//				useButton.setHTML(selectedCell.getElement().getInnerText());
				symbolInput.setText(selectedCell.getElement().getInnerText());
				
				if(selectedSymbol != null){
					selectedSymbol.getElement().removeClassName("selectedVar");
				}
				selectedSymbol = selectedCell;
				selectedSymbol.getElement().addClassName("selectedVar");
				
			}
		}
	}

//	private class UseHandler implements ClickHandler {
//
//		@Override
//		public void onClick(ClickEvent event) {
//			palette.hide();
//			String symbol = useButton.getElement().getInnerText();
//			symbolInput.setText(symbol);
////			node.setSymbol(symbol);
////			Moderator.reloadEquationPanel(null, null);
//			Moderator.setActivity(Activity.algebra);
//		}
//
//	}
}
