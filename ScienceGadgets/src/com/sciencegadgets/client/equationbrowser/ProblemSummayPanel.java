package com.sciencegadgets.client.equationbrowser;

import java.util.HashMap;
import java.util.HashSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.SelectionButton;

public class ProblemSummayPanel extends Composite {

	private static ProblemSummayPanelUiBinder uiBinder = GWT
			.create(ProblemSummayPanelUiBinder.class);

	interface ProblemSummayPanelUiBinder extends
			UiBinder<Widget, ProblemSummayPanel> {
	}

	@UiField
	FlowPanel varPanel;
	@UiField
	FlowPanel eqPanel;

	EquationSolveButton currentEqButton;

	public ProblemSummayPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void loadProblem(Problem problem, HashMap<String, String> randomMap) {
		currentEqButton = null;

		varPanel.clear();
		eqPanel.clear();

		HashSet<Equation> equations = problem.getEquations();
		if (equations != null) {
			for (Equation eq : equations) {
				try {
					if (eq.isSolved()) {
						varPanel.add(new EquationSolveButton(eq, true));
					} else {
						eqPanel.add(new EquationSolveButton(eq, false));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void updateSolvedEquation() {
		varPanel.add(currentEqButton);
		currentEqButton.isSolved = true;
	}

	class EquationSolveButton extends SelectionButton {
		Equation equation;
		boolean isSolved = false;

		EquationSolveButton(Equation equation, boolean isSolved) {
			this.equation = equation;
			this.isSolved = isSolved;
			// setHTML(equation.getHtml());
			addStyleName(CSS.EQUATION_SOLVE_BUTTON);
		}

		@Override
		protected void onLoad() {
			super.onLoad();
			equation.reCreateHTML();
			setHTML(equation.getHtml());
		};

		@Override
		protected void onSelect() {
			currentEqButton = this;

			String equationXMLStr = equation.getMathML();
			if (isSolved) {
				EquationNode node = (new EquationTree(new HTML(equationXMLStr)
						.getElement().getFirstChildElement(), false))
						.getRightSide();
				Moderator.switchToConversion(node, equation);
			} else {
				Element mathml = new HTML(equationXMLStr).getElement()
						.getFirstChildElement();
				Moderator.switchToAlgebra(mathml, equation, false, true);
			}
		}

	}
}
