/**
 * 
 */
package com.sciencegadgets.client.equationbrowser;

import java.io.StringReader;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.xml.sax.InputSource;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.client.ui.SelectionButton;

public class ProblemDetails extends Composite {

	private static ProblemDetailsUiBinder uiBinder = GWT
			.create(ProblemDetailsUiBinder.class);

	interface ProblemDetailsUiBinder extends UiBinder<Widget, ProblemDetails> {
	}

	public ProblemDetails() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	Label problemTitle;
	@UiField
	CommunistPanel tabsPanel;
	@UiField
	SimplePanel detailContainer;

	public ProblemDetails(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));

	}

	public void clear() {
		problemTitle.setText("");
		tabsPanel.clear();
		detailContainer.clear();

	}

	void loadProblem(Problem problem) {

		clear();

		problemTitle.setText(problem.getTitle());

		HashSet<Equation> equations = problem.getEquations();

		FlowPanel summaryPanel = new FlowPanel();
		tabsPanel.add(new ProblemDetailTab("Summary", summaryPanel));
		HTML description = new HTML(problem.getDescription());
		description.addStyleName(CSS.PROBLEM_DETAILS_DESCRIPTION);
		summaryPanel.add(description);
		FlowPanel varPanel = new FlowPanel();
		FlowPanel eqPanel = new FlowPanel();
		varPanel.addStyleName(CSS.PROBLEM_DETAILS_VARIABLE_PANEL);
		eqPanel.addStyleName(CSS.PROBLEM_DETAILS_EQUATION_PANEL);
		summaryPanel.add(varPanel);
		summaryPanel.add(eqPanel);

		if (equations != null) {
			for (Equation eq : equations) {
				try {
					if (eq.isSolved()) {
						VariableConvertButton convertButton = new VariableConvertButton(
								eq);
						varPanel.add(convertButton);
					} else {
						EquationSolveButton eqSolveButton = new EquationSolveButton(
								eq);
						eqPanel.add(eqSolveButton);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		detailContainer.add(summaryPanel);
	}

	class ProblemDetailTab extends SelectionButton {
		Panel detailPanel;

		ProblemDetailTab(String name, Panel detailPanel) {
			super(name);
			this.detailPanel = detailPanel;
		}

		@Override
		protected void onSelect() {
			detailContainer.clear();
			detailContainer.add(detailPanel);
		}
	}
}

class VariableConvertButton extends EquationSolveButton {
	EquationNode node;

	VariableConvertButton(Equation equation) {
		super(equation);
	}

	@Override
	protected void onSelect() {
		node = (new EquationTree(new HTML(equationXMLStr).getElement().getFirstChildElement(), false)).getRightSide();
		Moderator.switchToConversion(node, equation);
	}
}

class EquationSolveButton extends SelectionButton {
	String equationXMLStr;
	Equation equation;

	EquationSolveButton(Equation equation) {
		setHTML(equation.getHtml());
		addStyleName(CSS.EQUATION_SOLVE_BUTTON);
	}
	
//	@Override
//	protected void onAttach() {
//		super.onAttach();
//		setHTML(equation.getHtml());
//	}

	@Override
	protected void onSelect() {
		Element mathml = new HTML(equationXMLStr).getElement()
				.getFirstChildElement();
		Moderator.switchToAlgebra(mathml, false);
	}

}
