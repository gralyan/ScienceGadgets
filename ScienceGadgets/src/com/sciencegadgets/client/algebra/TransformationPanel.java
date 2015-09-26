package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.BothSidesTransformations.BothSidesButton;
import com.sciencegadgets.client.algebra.transformations.specification.SimplifyQuiz;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.CommunistPanel;
import com.sciencegadgets.client.ui.SelectionButton;

public class TransformationPanel extends FlowPanel {

	private enum TransformationPanelType {
		BOTH_SIDES, SIMPLIFY, DEFAULT;
	}

	private TransformationSubPanel bothSidesPanelLeft = null;
	private TransformationSubPanel simplifyButtonsPanel = null;
	private TransformationSubPanel bothSidesPanelRight = null;
	
	private SimplifyPromptButton simplifyPromptButton = new SimplifyPromptButton();
	public SimplifyQuiz simplifyQuiz = null;
	private AlgebraActivity algebraActivity = null;

	public TransformationPanel(AlgebraActivity algebraActivity) {
		this.algebraActivity = algebraActivity;
		addStyleName(CSS.FILL_PARENT);

		add(bothSidesPanelLeft = new TransformationSubPanel(
				TransformationPanelType.BOTH_SIDES));
		add(simplifyButtonsPanel = new TransformationSubPanel(
				TransformationPanelType.SIMPLIFY));
		add(bothSidesPanelRight = new TransformationSubPanel(
				TransformationPanelType.BOTH_SIDES));
	}
	
	public void clearTransformLists() {
		bothSidesPanelLeft.clear();
		bothSidesPanelRight.clear();
		simplifyButtonsPanel.clear();
		algebraActivity.lowerEqArea.clear();
	}

	public void fillTransformLists(
			TransformationList<TransformationButton> transSimplify,
			BothSidesTransformations transBothSides) {

		clearTransformLists();

		// Both Sides Buttons
		bothSidesPanelLeft.addAll(transBothSides);

		for (int i = transBothSides.size(); i > 0; i--) {
			BothSidesButton button = transBothSides.get(i - 1);
			bothSidesPanelRight.add(button.getJoinedButton());
		}

		// Simplify Buttons
		LinkedList<SelectionButton> buttonsShown = new LinkedList<SelectionButton>();

		for (TransformationButton tButt : transSimplify) {
			if (tButt.meetsAutoTransform()) {
				buttonsShown.add(tButt);
			}
		}
		if (transSimplify.size() > buttonsShown.size()) {
			simplifyPromptButton.setTransformationList(transSimplify);
			buttonsShown.add(simplifyPromptButton);
		}

		simplifyButtonsPanel.addAll(buttonsShown);

		algebraActivity.lowerEqArea.add(this);
	}


	public class TransformationSubPanel extends CommunistPanel {

		
		public TransformationSubPanel() {
			this(TransformationPanelType.DEFAULT);
		}
		public TransformationSubPanel(TransformationPanelType type) {
			super(true);

			switch (type) {
			case BOTH_SIDES:
				addStyleName(CSS.BOTH_SIDES_PANEL);
				break;
			case SIMPLIFY:
				addStyleName(CSS.SIMPLIFY_PANEL);
				break;
			default:
				addStyleName(CSS.FILL_PARENT);
				break;
			}
		}

		@Override
		protected void onAttach() {
			super.onAttach();
			for (Widget child : getChildren()) {
				((SelectionButton) child).resize();
			}
		}
	}

	class SimplifyPromptButton extends SelectionButton {

		TransformationList<TransformationButton> transSimplify;

		SimplifyPromptButton() {
			super("Simplify");
			addStyleName(CSS.TRANSFORMATION_BUTTON + " " + CSS.LAYOUT_ROW);
		}

		@Override
		protected void onSelect() {
			simplifyQuiz = new SimplifyQuiz(algebraActivity.getEquationPanel().selectedWrapper.node,
					transSimplify);
			simplifyQuiz.appear();
		}

		public void setTransformationList(
				TransformationList<TransformationButton> transSimplify) {
			this.transSimplify = transSimplify;
		}
	}
}