/**
 * 
 */
package com.sciencegadgets.client.challenge;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.ConstantRandomizer;
import com.sciencegadgets.client.algebra.edit.ProblemSpecification;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.client.ui.FitParentHTML;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.shared.Diagram;

public class ProblemDetails extends Composite {

	private static ProblemDetailsUiBinder uiBinder = GWT
			.create(ProblemDetailsUiBinder.class);

	interface ProblemDetailsUiBinder extends UiBinder<Widget, ProblemDetails> {
	}

	@UiField
	SimplePanel problemTitleContainer;
	@UiField
	SimplePanel problemDescriptionContainer;
	@UiField
	SimplePanel equationsContainer;
	@UiField
	CommunistPanel tabsPanel;
	@UiField
	SimplePanel detailContainer;

	private ProblemSummayPanel equationsPanel = new ProblemSummayPanel();
	private DiagramPanel diagramPanel = new DiagramPanel();
	private Problem currentProblem;

	public ProblemDetails() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void clear() {
		problemTitleContainer.clear();
		problemDescriptionContainer.clear();
		tabsPanel.clear();
		detailContainer.clear();
		equationsContainer.clear();
		currentProblem = null;
	}

	public void loadProblem(Problem problem) {

		clear();

		currentProblem = problem;

		problemTitleContainer.add(new FitParentHTML(problem.getTitle()));

		HashMap<String, String> randomMap = new HashMap<String, String>();
		for (Equation eq : problem.getEquations()) {
			Element eqEl = new HTML(eq.getMathML()).getElement()
					.getFirstChildElement();
			randomMap.putAll(ConstantRandomizer.randomizeNumbers(eqEl, true));
			eq.setMathML(JSNICalls.elementToString(eqEl));
		}

		String description = problem.getDescription();
		for (Entry<String, String> randomEntry : randomMap.entrySet()) {
			String toReplace = ProblemSpecification.VARIABLE_START
					+ randomEntry.getKey() + ProblemSpecification.VARIABLE_END;
			description = description
					.replace(toReplace, randomEntry.getValue());
		}
		problemDescriptionContainer.add(new Label(description));

		Diagram diagram = problem.getDiagram();
		if (diagram != null) {
			diagramPanel.loadDiagram(diagram, problem, randomMap);
			tabsPanel.add(new ProblemDetailTab("Diagram", diagramPanel));
			detailContainer.add(diagramPanel);
		}

		equationsPanel.loadProblem(problem, randomMap);
//		tabsPanel.add(new ProblemDetailTab("Summary", equationsPanel));
//		if (detailContainer.getWidget() == null) {
//			detailContainer.add(equationsPanel);
//		}
		
		equationsContainer.add(equationsPanel);
	}

	public ProblemSummayPanel getSummaryPanel() {
		return equationsPanel;
	}

	public boolean updateSolvedEquation() {
		try {
			String varId = equationsPanel.currentEqButton.equation
					.getVarIdIfSolved();
			if (varId != null) {
				equationsPanel.updateSolvedEquation();
				if (varId.equals(currentProblem.getToSolveID())) {
					return true;
				}
			}
		} catch (Exception e) {
		}
		return false;
	}

	class ProblemDetailTab extends SelectionButton {
		Widget detailPanel;

		ProblemDetailTab(String name, Widget detailPanel) {
			super(name);
			this.detailPanel = detailPanel;
			addStyleName(CSS.TAB);
		}

		@Override
		protected void onSelect() {
			detailContainer.clear();
			detailContainer.add(detailPanel);
		}
	}


}
