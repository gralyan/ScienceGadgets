package com.sciencegadgets.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;

public interface HasFitParentHTML {

	public FitParentHTML getFitParentHTML();

	public class FitParentContainer extends FlowPanel implements HasFitParentHTML{

		private final FitParentHTML fitParentHTML;
		
		public FitParentContainer(FitParentHTML fitParentHTML) {
			this.fitParentHTML = fitParentHTML;
			add(fitParentHTML);
		}
		
		@Override
		public FitParentHTML getFitParentHTML() {
			return fitParentHTML;
		}
		
		
	}
}
