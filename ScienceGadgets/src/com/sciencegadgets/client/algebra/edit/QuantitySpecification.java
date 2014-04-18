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
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.ui.FitParentHTML;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SymbolDisplay;
import com.sciencegadgets.client.ui.ToggleSlide;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitHTML;
import com.sciencegadgets.shared.dimensions.UnitMap;
import com.sciencegadgets.shared.dimensions.UnitName;

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
	protected boolean mustCheckUnits;

	// Stores the unit name and associated exponent
	protected UnitMap unitMap = new UnitMap();
	protected UnitAttribute dataUnit = new UnitAttribute("");
	protected FitParentHTML unitHTML = new FitParentHTML();

	public QuantitySpecification(EquationNode equationNode,
			boolean clearDisplays, boolean mustCheckUnits) {
		super();
		this.node = equationNode;
		this.mustCheckUnits = mustCheckUnits;

		add(mainPanel);

		unitDisplay.add(unitHTML);

		if (Moderator.isTouch) {
			// Unit Display Touch - clear
			unitDisplay.addDomHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					dataUnit.setString("");
					unitMap.clear();
					unitHTML.setHTML("");
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
					unitHTML.setHTML("");
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

		reload(equationNode, clearDisplays, mustCheckUnits);
	}

	public void reload(EquationNode mathNode, boolean clearDisplays,
			boolean mustCheckUnits) {

		this.node = mathNode;

		dataUnit.setString("");
		unitMap.clear();
		unitHTML.setHTML("");

		// Symbol Display
		String oldSymbol = node.getSymbol();
		if (clearDisplays) {
			symbolDisplay.setText("");
		} else {
			symbolDisplay.setText(oldSymbol);
			setUnit(new UnitMap(node));
		}

		if (canHaveUnits(node)) {
			unitArea.getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
		} else {
			unitArea.getElement().getStyle().setDisplay(Display.NONE);
		}

	}

	protected void setUnit(UnitMap map) {
		unitMap = map;
		dataUnit = unitMap.getUnitAttribute();
		Element unitElement = UnitHTML.create(dataUnit, null, false);
		String unitHTMLString = JSNICalls.elementToString(unitElement);
		unitHTML.setHTML(unitHTMLString);
		unitHTML.resize();
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

	private class OkHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			
			if(mustCheckUnits && !unitMap.isConvertableTo(node.getUnitMap())) {
				Window.alert("Units must match:\n"+unitMap);
				return;
			}
			
			String symbol = extractSymbol();

			if (symbol == null) {
				return;
			}
			setNode(symbol);

			node.getXMLNode().setAttribute(
					MathAttribute.Unit.getAttributeName(), dataUnit.toString());

			disappear();
			Moderator.reloadEquationPanel(null, (Skill[])null);
		}
	}

	protected abstract void setNode(String symbol);

	protected abstract String extractSymbol();
}
