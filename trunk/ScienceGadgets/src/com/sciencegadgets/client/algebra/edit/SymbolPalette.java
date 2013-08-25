package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.HTMLTable.Cell;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.Activity;
import com.sciencegadgets.client.algebra.MathMLBindingTree;
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;

public class SymbolPalette extends PopupPanel {

	Grid grid = new Grid(5, 13);
	Button useButton = new Button("Select Symbol", new UseHandler());
	SymbolPalette palette = this;
	MathMLBindingNode node;

	// lower, upper, name
	private String[][] greekLetters = { { "α", "Α", "alpha" },
			{ "β", "Β", "beta" }, { "γ", "Γ", "gamma" }, { "δ", "Δ", "delta" },
			{ "ε", "Ε", "epsilon" }, { "ζ", "Ζ", "zeta" }, { "η", "Η", "eta" },
			{ "θ", "Θ", "theta" }, { "ι", "Ι", "iota" }, { "κ", "Κ", "kappa" },
			{ "λ", "Λ", "lambda" }, { "μ", "Μ", "mu" }, { "ν", "Ν", "nu" },
			{ "ξ", "Ξ", "xi" }, { "ο", "Ο", "omicron" }, { "π", "Π", "pi" },
			{ "ρ", "Ρ", "rho" }, { "σ", "Σ", "sigma" }, { "τ", "Τ", "tau" },
			{ "υ", "Υ", "upsilon" }, { "φ", "Φ", "phi" }, { "χ", "Χ", "chi" },
			{ "ψ", "Ψ", "psi" }, { "ω", "Ω", "omega" } };

	public SymbolPalette(MathMLBindingNode node) {
		this.node = node;

		for (int j = 1; j < 12; j++) {
			grid.setHTML(1, j, greekLetters[j][0]);
			grid.setHTML(2, j, greekLetters[j + 12][0]);
			grid.setHTML(3, j, greekLetters[j][1]);
			grid.setHTML(4, j, greekLetters[j + 12][1]);
		}

		grid.setWidget(0, 0, useButton);
		useButton.setWidth(this.getOffsetWidth()+"px");
		useButton.setEnabled(false);
		useButton.setWidth("50px");
		grid.addClickHandler(new SymbolClickHandler());
		
		this.add(grid);
		// this.setGlassEnabled(true);
//		this.setModal(true);
	}
	
	public void setNode(MathMLBindingNode mlNode){
		this.node = mlNode;
	}

	private class SymbolClickHandler implements ClickHandler {
		@Override
		public void onClick(ClickEvent event) {
			Cell selectedCell = grid.getCellForEvent(event);

			if (selectedCell != null) {

				useButton.setEnabled(true);
				useButton.setHTML(selectedCell.getElement().getInnerText());
			}
		}
	}

	private class UseHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			palette.hide();
			String symbol = useButton.getElement().getInnerText();
			node.setSymbol(symbol);
			Moderator.reloadEquationPanel("");
			Moderator.setActivity(Activity.algebra);
		}

	}
}
