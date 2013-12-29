package com.sciencegadgets.client.algebra.edit;

import java.util.LinkedHashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.ToggleSlide;
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
	private boolean isReciprocal = false;

	// Stores the unit name and associated exponent
	LinkedHashMap<String, Integer> unitMap = new LinkedHashMap<String, Integer>();
	private String dataUnit = "";
	private Element unitHTML;

	public QuantitySpecification(MathNode mathNode) {
		super();
		this.node = mathNode;

		add(uiBinder.createAndBindUi(this));

		if(Moderator.isTouch) {
			//Unit Display Touch
			unitDisplay.addDomHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					dataUnit = "";
					unitHTML.removeFromParent();
					unitHTML = null;
					unitMap.clear();
				}
			}, TouchStartEvent.getType());
		}else {
			
			//Unit Display Click
			unitDisplay.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					dataUnit = "";
					unitHTML.removeFromParent();
					unitHTML = null;
					unitMap.clear();
				}
			}, ClickEvent.getType());
		}

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


		reload(mathNode);
	}
	
	public void reload(MathNode mathNode) {

		node = mathNode; 

		dataUnit = "";
		if(unitHTML != null) {
			unitHTML.removeFromParent();
			unitHTML = null;
		}
		unitMap.clear();
		
		// Symbol Display
		String oldSymbol = node.getSymbol();
		if (ChangeNodeMenu.NOT_SET.equals(oldSymbol)) {
			symbolDisplay.setText("");
			
		}else {
			symbolDisplay.setText(oldSymbol);

			// Unit Display
			unitHTML = UnitUtil.element_From_MathNode(node, null, false);
			unitHTML.addClassName("fillParent");
			unitDisplay.getElement().appendChild(unitHTML);
			
			dataUnit = node.getUnitAttribute();
			unitMap = UnitUtil.getUnitMap(node);
		}

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
			unitHTML = UnitUtil.element_From_attribute(dataUnit, null, false);
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

			if (dataUnit == null || "".equals(dataUnit)) {
				node.getMLNode().removeAttribute(MathAttribute.Unit.getName());
			} else {
				node.getMLNode().setAttribute(MathAttribute.Unit.getName(),
						dataUnit);
			}
			disappear();
			AlgebraActivity.reloadEquationPanel(null, null);
		}
	}

	abstract void setSymbol(String symbol);

	abstract String extractSymbol();
}
