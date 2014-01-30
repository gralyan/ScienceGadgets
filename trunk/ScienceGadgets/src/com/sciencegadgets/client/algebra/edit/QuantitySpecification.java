package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.shared.UnitMap;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.SymbolDisplay;
import com.sciencegadgets.client.ToggleSlide;
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
	SymbolDisplay symbolDisplay;
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
	UnitMap unitMap = new UnitMap();
	private String dataUnit = "";
	private Element unitHTML;

	public QuantitySpecification(MathNode mathNode) {
		super();
		this.node = mathNode;

		add(uiBinder.createAndBindUi(this));

		if (Moderator.isTouch) {
			// Unit Display Touch - clear
			unitDisplay.addDomHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					dataUnit = "";
					unitMap.clear();
					if (unitHTML != null) {
						unitHTML.removeFromParent();
						unitHTML = null;
					}
				}
			}, TouchStartEvent.getType());

			// Symbol Display on Touch - clear
			symbolDisplay.addTouchStartHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					symbolDisplay.setText("");
				}
			});
		} else {
			// Unit Display Click - clear
			unitDisplay.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					dataUnit = "";
					unitMap.clear();
					if (unitHTML != null) {
						unitHTML.removeFromParent();
						unitHTML = null;
					}
				}
			}, ClickEvent.getType());

			// Symbol Display on Click - clear
			symbolDisplay.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					symbolDisplay.setText("");
				}
			});
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
		if (unitHTML != null) {
			unitHTML.removeFromParent();
			unitHTML = null;
		}
		unitMap.clear();

		// Symbol Display
		String oldSymbol = node.getSymbol();
		if (ChangeNodeMenu.NOT_SET.equals(oldSymbol)) {
			symbolDisplay.setText("");

		} else {
			symbolDisplay.setText(oldSymbol);

			// Unit Display
			unitHTML = UnitUtil.element_From_MathNode(node, null, false);
			unitHTML.addClassName("fillParent");
			unitDisplay.getElement().appendChild(unitHTML);

			dataUnit = node.getUnitAttribute();
			unitMap = new UnitMap(node);
		}

	}

	class UnitSelectionHandler implements SelectionHandler {

		@Override
		public void onSelect(
				com.sciencegadgets.client.SelectionPanel.Cell selected) {

			String name = selected.getValue();

			int direction = isReciprocal ? -1 : 1;
			unitMap.put(name, direction);

			dataUnit = unitMap.getUnitAttribute();
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
			Moderator.reloadEquationPanel(null, null);
		}
	}

	abstract void setSymbol(String symbol);

	abstract String extractSymbol();
}
