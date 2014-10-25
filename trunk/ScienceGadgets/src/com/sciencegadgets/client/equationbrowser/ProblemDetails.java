/**
 * 
 */
package com.sciencegadgets.client.equationbrowser;

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
	CommunistPanel tabsPanel;
	@UiField
	SimplePanel detailContainer;

	private ProblemSummayPanel summaryPanel = new ProblemSummayPanel();
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

		summaryPanel.loadProblem(problem, randomMap);
		tabsPanel.add(new ProblemDetailTab("Summary", summaryPanel));
		if (detailContainer.getWidget() == null) {
			detailContainer.add(summaryPanel);
		}

		Abs ab = new Abs();
		tabsPanel.add(new ProblemDetailTab("Abs", ab));
	}

	public ProblemSummayPanel getSummaryPanel() {
		return summaryPanel;
	}

	public boolean updateSolvedEquation() {
		try {
			String varId = summaryPanel.currentEqButton.equation
					.getVarIdIfSolved();
			if (varId != null) {
				summaryPanel.updateSolvedEquation();
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

class Abs extends AbsolutePanel {
	Abs() {

		setSize("100%", "100%");
		getElement().getStyle().setBackgroundColor("blue");

		if (Moderator.isTouch) {
			this.addDomHandler(new TouchEndHandler() {
				@Override
				public void onTouchEnd(TouchEndEvent event) {
					Touch touch = event.getTouches().get(0);
					// TODO
					JSNICalls.log("touch");
					JSNICalls.log("client x:"+ touch.getClientX()+" y: "+ touch.getClientY());
					JSNICalls.log("page x:"+ touch.getPageX()+" y: "+ touch.getPageY());
					JSNICalls.log("relative x:"+ touch.getRelativeX(Abs.this.getElement())+" y: "+ touch.getRelativeY(Abs.this.getElement()));
					JSNICalls.log("screen x:"+ touch.getScreenX()+" y: "+ touch.getScreenY());

					add(new Label("client"), touch.getClientX(), touch.getClientY());
					add(new Label("page"), touch.getPageX(), touch.getPageY());
					add(new Label("relative"), touch.getRelativeX(Abs.this.getElement()),
							touch.getRelativeY(Abs.this.getElement()));
					add(new Label("screen"), touch.getScreenX(), touch.getScreenY());
					
//					pointSelection(touch);
				}
			}, TouchEndEvent.getType());
		}
		this.addDomHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				JSNICalls.log("click x:"+ event.getX()+" y: "+ event.getY());
				add(new Label("click"), event.getX(), event.getY());
			}
		}, ClickEvent.getType());
	}

	void pointSelection(Touch touch) {

		add(new Label("client"), touch.getClientX(), touch.getClientY());
		add(new Label("page"), touch.getPageX(), touch.getPageY());
		add(new Label("relative"), touch.getRelativeX(this.getElement()),
				touch.getRelativeY(this.getElement()));
		add(new Label("screen"), touch.getScreenX(), touch.getScreenY());
	}
}
