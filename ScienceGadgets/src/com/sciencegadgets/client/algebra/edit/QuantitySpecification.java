package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.shared.UnitMap;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Prompt;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.SelectionPanel.Cell;
import com.sciencegadgets.client.SelectionPanel.SelectionHandler;
import com.sciencegadgets.client.SymbolDisplay;
import com.sciencegadgets.client.ToggleSlide;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.UnitAttribute;
import com.sciencegadgets.shared.UnitHTML;
import com.sciencegadgets.shared.UnitName;

public abstract class QuantitySpecification extends Prompt {

	interface VariableSpecUiBinder extends
			UiBinder<FlowPanel, QuantitySpecification> {
	}

	private static VariableSpecUiBinder uiBinder = GWT
			.create(VariableSpecUiBinder.class);

	@UiField
	SymbolDisplay symbolDisplay;
	@UiField
	ToggleSlide symbolCaseToggle;
	@UiField
	FlowPanel symbolPalette;

	@UiField
	FlowPanel unitArea;
	@UiField
	FlowPanel unitDisplay;
	@UiField
	ToggleSlide unitReciprocalToggle;
	@UiField
	FlowPanel unitPalette;

	@UiField
	SelectionPanel establishedSelection;

	FlowPanel mainPanel = uiBinder.createAndBindUi(this);

	protected EquationNode node;
	private boolean isReciprocal = false;

	// Stores the unit name and associated exponent
	protected UnitMap unitMap = new UnitMap();
	protected UnitAttribute dataUnit = new UnitAttribute("");
	protected Element unitHTML;

	public QuantitySpecification(EquationNode mathNode, boolean clearDisplays) {
		super();
		this.node = mathNode;

		add(mainPanel);

		if (Moderator.isTouch) {
			// Unit Display Touch - clear
			unitDisplay.addDomHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					dataUnit.setString("");
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
					dataUnit.setString("");
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

		reload(mathNode, clearDisplays);
	}

	public void reload(EquationNode mathNode) {
		reload(mathNode, true);
	}
	public void reload(EquationNode mathNode, boolean clearDisplays) {

		this.node = mathNode;

		dataUnit.setString("");
		if (unitHTML != null) {
			unitHTML.removeFromParent();
			unitHTML = null;
		}
		unitMap.clear();

		// Symbol Display
		String oldSymbol = node.getSymbol();
		if (clearDisplays) {
			symbolDisplay.setText("");

		} else {
			symbolDisplay.setText(oldSymbol);

			// Unit Display
			unitHTML = UnitHTML.create(node, null, false);
			unitHTML.addClassName(CSS.FILL_PARENT);
			unitDisplay.getElement().appendChild(unitHTML);

			dataUnit = node.getUnitAttribute();
			unitMap = new UnitMap(node);
		}

		if (canHaveUnits(node)) {
			unitArea.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		} else {
			unitArea.getElement().getStyle().setDisplay(Display.NONE);
		}

	}

	private boolean canHaveUnits(EquationNode mNode) {
		switch (mNode.getParentType()) {
		case Exponential:
			if (mNode.getIndex() == 1) {
				return false;
			}
			break;
		case Trig:
			String func = mNode.getParent()
					.getAttribute(MathAttribute.Function);
			if (func.contains(TrigFunctions.ARC)) {
				return false;
			}
			// TODO only allow units of PlaneAngle for trig arguments
			break;
		case Log:
			return false;
		case Equation:
			return true;
		}
		return canHaveUnits(mNode.getParent());
	}

	protected void setUnit(UnitMap map) {

		unitMap = map;
		dataUnit = unitMap.getUnitAttribute();
		if (unitHTML != null) {
			unitHTML.removeFromParent();
		}
		unitHTML = UnitHTML.create(dataUnit, null, false);
		unitHTML.addClassName(CSS.FILL_PARENT);
		unitDisplay.getElement().appendChild(unitHTML);
	}

	class UnitSelectionHandler implements SelectionHandler {

		@Override
		public void onSelect(Cell selected) {

			UnitName name = new UnitName(selected.getValue());

			int direction = isReciprocal ? -1 : 1;
			unitMap.put(name, direction);

			setUnit(unitMap);

		}
	}

	private class OkHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			String symbol = extractSymbol();

			if (symbol == null) {
				return;
			}
			setNode(symbol);

				node.getXMLNode().setAttribute(
						MathAttribute.Unit.getAttributeName(), dataUnit.toString());

				disappear();
			Moderator.reloadEquationPanel(null, null);
		}
	}

	abstract void setNode(String symbol);

	abstract String extractSymbol();
}
