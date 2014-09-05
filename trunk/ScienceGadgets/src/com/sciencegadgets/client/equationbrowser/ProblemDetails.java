/**
 * 
 */
package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.client.ui.FitParentHTML;
import com.sciencegadgets.client.ui.SelectionButton;

public class ProblemDetails extends Composite {

	private static ProblemDetailsUiBinder uiBinder = GWT
			.create(ProblemDetailsUiBinder.class);

	interface ProblemDetailsUiBinder extends UiBinder<Widget, ProblemDetails> {
	}

	@UiField
	SimplePanel problemTitleContainer;
	@UiField
	CommunistPanel tabsPanel;
	@UiField
	SimplePanel detailContainer;

	private ProblemSummayPanel summaryPanel = new ProblemSummayPanel();
	
	public ProblemDetails() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void clear() {
		problemTitleContainer.clear();
		tabsPanel.clear();
		detailContainer.clear();

	}

	public void loadProblem(Problem problem) {

		clear();

		problemTitleContainer.add(new FitParentHTML(problem.getTitle()));

		tabsPanel.add(new ProblemDetailTab("Summary", summaryPanel));
		
		summaryPanel.loadProblem(problem);

		detailContainer.add(summaryPanel);
	}
	
	public ProblemSummayPanel getSummaryPanel() {
		return summaryPanel;
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
