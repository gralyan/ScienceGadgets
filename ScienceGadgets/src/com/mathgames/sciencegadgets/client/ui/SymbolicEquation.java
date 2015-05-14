package com.mathgames.sciencegadgets.client.ui;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;

public class SymbolicEquation extends Composite {

	public enum Operation {
		PLUS("+"), MINUS("-"), MULTIPLY("*"), DIVID("/");

		private String symbol;

		private Operation(String symbol) {
			this.symbol = symbol;
		}

		String getSymbol() {
			return symbol;
		}
	}

	private Symbol rightLabel = new Symbol();
	private Symbol operationLabel = new Symbol();
	private Symbol leftLabel = new Symbol();
	private Symbol equalsLabel = new Symbol();
	private Symbol totalLabel = new Symbol();

	int leftValue;
	int rightValue;
	int totalValue;

	Operation operationValue;

	Symbol[] allSymbols = { rightLabel, operationLabel, leftLabel, equalsLabel,
			totalLabel };
	
	public SymbolicEquation(Operation operationValue) {
		equalsLabel.setText(" = ");
		operationLabel.setText(" "+operationValue.getSymbol()+" ");
		this.operationValue = operationValue;
		
		FlowPanel panel = new FlowPanel();
		for(Symbol symbol : allSymbols) {
			panel.add(symbol);
		}
		initWidget(panel);
	}

	public SymbolicEquation(int leftValue, Operation operationValue, int rightValue,
			int totalValue) {
		this(operationValue);
		setSymbols(leftValue,rightValue, totalValue);
	}

	public void setSymbols(int leftValue, int rightValue,
			int totalValue) {

		boldAll(false);

		this.leftValue = leftValue;
		this.rightValue = rightValue;
		this.totalValue = totalValue;

		leftLabel.setText(leftValue + "");
		rightLabel.setText(rightValue + "");
		totalLabel.setText(totalValue + "");

	}
	
	void boldAll(boolean toBold) {
		for (Symbol symbol : allSymbols) {
			symbol.setBold(toBold);
		}
	}

	public int getLeftValue() {
		return leftValue;
	}

	public int getRightValue() {
		return rightValue;
	}

	public int getTotalValue() {
		return totalValue;
	}

	class Symbol extends Label {
		public Symbol() {
			getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		}

		public void setBold(boolean toBold) {
			if (toBold) {
				getElement().getStyle().setFontWeight(FontWeight.BOLDER);
			} else {
				getElement().getStyle().clearFontWeight();
			}
		}

	}
}
