package com.sciencegadgets.client.equationtree;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class EditMenu extends HTMLPanel {
	
	EditWrapper editWrapper;
	MathMLBindingNode node;
	Focusable focusable = null;

	public EditMenu(String html, EditWrapper editWrapper) {
		super(html);
		
		this.editWrapper = editWrapper;
		this.node = editWrapper.getNode();

		switch (node.getType()) {
		case Sum:
			break;
		case Term:
			break;
		case Fraction:
			break;
		case Variable:
			TextBox variableInput = new TextBox();
			variableInput.addChangeHandler(new InputChangeHandler(variableInput));
			variableInput.setText(node.getSymbol());
			focusable = variableInput;
			this.add(variableInput);
			break;
		case Number:
			DoubleBox numberInput = new DoubleBox();
			numberInput.addChangeHandler(new InputChangeHandler(numberInput));
			numberInput.setText(node.getSymbol());
			focusable = numberInput;
			this.add(numberInput);
			break;
		case Exponential:
			break;
		}
	}
	
	public void setFocus(){
		if(focusable != null){
			focusable.setFocus(true);
		}
	}
	
	private class InputChangeHandler implements ChangeHandler{

		FocusWidget input;
		public InputChangeHandler(FocusWidget input) {
			this.input = input;
		}

		@Override
		public void onChange(ChangeEvent event) {
			String extraction = null;
			
			if(input instanceof DoubleBox){
				extraction = extractNumber();
			}else if(input instanceof TextBox){
				extraction = extractVariable();
			}
			
			if(extraction != null){
				node.setSymbol(extraction);
				HTML mathML = new HTML();
				mathML.getElement().appendChild(node.getTree().getMathML());
				
				Moderator.reload("");
			}
		}
		
		private String extractNumber(){
			Double inputValue = ((DoubleBox)input).getValue();
			String inputString = null;
			
			if(inputValue == null){
				Window.alert("The input must be a number\nchange this to a variable node if you want a variable");
			}else{
				inputString = inputValue+"";
			}
			return inputString;
		}
		
		private String extractVariable(){
			String inputString = ((TextBox) input).getText();
			
			if(inputString == null){
				Window.alert("That's some crazy input");
			}
			return inputString;
		}
	}

}
