package com.sciencegadgets.client.algebra.edit;

import java.util.HashSet;
import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.ValueBoxBase;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.ToggleSlide;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.UnitUtil;

public abstract class QuantitySpecification extends Prompt {

	interface VariableSpecUiBinder extends
			UiBinder<FlowPanel, QuantitySpecification> {
	}

	private static VariableSpecUiBinder uiBinder = GWT
			.create(VariableSpecUiBinder.class);

	@UiField
	Label symbolDisplay;
	@UiField
	FlowPanel unitDisplay;
	@UiField
	ToggleSlide symbolCaseToggle;
	@UiField
	ToggleSlide unitReciprocalToggle;

	@UiField
	FlowPanel symbolPalette;
	@UiField
	FlowPanel unitSelectionHolder;

	protected MathNode node;
	private String prevSymbol = "";
	private Element prevUnit = null;
	private Element unitHTML;
	private boolean isReciprocal = false;
	private String dataUnit = "";

	// Stores the unit name and associated exponent
	LinkedHashMap<String, Integer> prevUnitMap = new LinkedHashMap<String, Integer>();
	LinkedHashMap<String, Integer> unitMap = new LinkedHashMap<String, Integer>();

	public QuantitySpecification(EditMenu editMenu) {
		super();
		this.node = editMenu.node;

		add(uiBinder.createAndBindUi(this));

		// Symbol Display
		symbolDisplay.setText(node.getSymbol());

		// Unit Display
		unitHTML = UnitUtil.element_From_MathNode(node);
		unitHTML.removeClassName(UnitUtil.UNIT_CLASSNAME);
		unitHTML.addClassName("fillParent");
		unitDisplay.getElement().appendChild(unitHTML);

		symbolDisplay.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Switch symbol with saved previous symbol
				String intermediateSymbol = symbolDisplay.getText();
				symbolDisplay.setText(prevSymbol);
				prevSymbol = intermediateSymbol;
			}
		});
		unitDisplay.addHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				// Switch unit with saved previous unit
				Element intermediateUnit = prevUnit;
				prevUnit = unitHTML;
				unitHTML.removeFromParent();
				if (intermediateUnit != null) {
					unitHTML = intermediateUnit;
					unitDisplay.getElement().appendChild(unitHTML);
				}

				// Switch unitMap with saved previous unitMap
				LinkedHashMap<String, Integer> intermediateUnitMap = unitMap;
				unitMap = prevUnitMap;
				prevUnitMap = intermediateUnitMap;
			}
		}, ClickEvent.getType());

		unitReciprocalToggle
				.setOptionsHtml(
						"<div><div style=\"border-bottom: thin solid;\">unit</div><div>1</div></div>",
						"<div><div style=\"border-bottom: thin solid;\">1</div><div>unit</div></div>",
						!isReciprocal);
		unitReciprocalToggle.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				isReciprocal = !isReciprocal;
			}
		});


		// OK button
		addOkHandler(new OkHandler());

	}

	class UnitSelectionHandler implements SelectionHandler {

		@Override
		public void onSelect(
				com.sciencegadgets.client.SelectionPanel.Cell selected) {

			String name = selected.getValue();

			int exp = 0;
			if (unitMap.containsKey(name)) {
				Integer prevExp = unitMap.get(name);
				exp = isReciprocal ? --prevExp : ++prevExp;
			} else {
				exp = isReciprocal ? -1 : 1;
			}
			if (exp == 0) {
				unitMap.remove(name);
			} else {
				unitMap.put(name, exp);
			}
			dataUnit = "";
			for (String base : unitMap.keySet()) {
				dataUnit = dataUnit + UnitUtil.BASE_DELIMITER + base
						+ UnitUtil.EXP_DELIMITER + unitMap.get(base);
			}
			dataUnit = dataUnit.replaceFirst(UnitUtil.BASE_DELIMITER_REGEX, "");
			if (unitHTML != null) {
				unitHTML.removeFromParent();
			}
			unitHTML = UnitUtil.element_From_attribute(dataUnit);
			unitHTML.removeClassName(UnitUtil.UNIT_CLASSNAME);
			unitHTML.addClassName("fillParent");
			unitDisplay.getElement().appendChild(unitHTML);
		}
	}



	private class OkHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String symbol = extractSymbol();

			if (symbol == null) {
				return;
			}
			setSymbol(symbol);

			if (dataUnit != null) {
				node.getMLNode().setAttribute(MathAttribute.Unit.getName(),
						dataUnit);
			}else {
				node.getMLNode().removeAttribute(MathAttribute.Unit.getName());
			}
			disappear();
			AlgebraActivity.reloadEquationPanel(null, null);
		}
	}
	
	abstract void setSymbol(String symbol);
	abstract String extractSymbol();
}
