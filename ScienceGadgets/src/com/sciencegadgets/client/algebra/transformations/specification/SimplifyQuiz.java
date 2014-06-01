package com.sciencegadgets.client.algebra.transformations.specification;

import java.util.HashSet;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.AlgebraActivity.TransformationPanel;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Quiz;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.shared.TypeSGET;

public class SimplifyQuiz extends Quiz {

	private static AlgebraActivity simplifyActivity;
	TransformationList<TransformationButton> tButtons;

	public SimplifyQuiz(EquationNode hostNode,
			final TransformationList<TransformationButton> tButtons) {

		this.tButtons = tButtons;

		// Make Tree
		Element hostXML = hostNode.getXMLClone();
		if (TypeSGET.Operation.equals(hostNode.getType())) {
			Element hostParentOperation = (Element) hostNode.getParent()
					.getXMLNode().cloneNode(false);
			hostParentOperation.removeAttribute("id");
			Element hostLeft = hostNode.getPrevSibling().getXMLClone();
			Element hostRight = hostNode.getNextSibling().getXMLClone();
			hostParentOperation.appendChild(hostLeft);
			hostParentOperation.appendChild(hostXML);
			hostParentOperation.appendChild(hostRight);
			hostXML = hostParentOperation;
		}

		EquationTree workingTree = new EquationTree(true);
		EquationNode workingLeft = workingTree.newNode((Element) hostXML
				.cloneNode(true));
		workingLeft.hasWrapper = false;
		EquationNode workingRight = workingTree.newNode(hostXML);
		workingTree.getLeftSide().replace(workingLeft);
		workingTree.getRightSide().replace(workingRight);

		// Set Up Activity
		if (simplifyActivity == null) {
			simplifyActivity = new AlgebraActivity(workingTree, true, true);
		} else {
			simplifyActivity.setEquationTree(workingTree);
		}
		simplifyActivity.reloadEquationPanel(null, null, false);

		simplifyActivity.upperMidEqArea.clear();
		TransformationPanel tButtonPanel = simplifyActivity.new TransformationPanel();
		simplifyActivity.upperMidEqArea.add(tButtonPanel);
		tButtonPanel.add(new HelpButton(tButtonPanel));

		this.add(simplifyActivity);

		// OK Handler
		addOkHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				EquationNode response = simplifyActivity.getEquationTree()
						.getRightSide();
				response.getTree().validateMaps();
				for (TransformationButton tButt : tButtons) {
					EquationNode possibleCorrectResponse = tButt.getPreview();

					if (response.isLike(possibleCorrectResponse)) {
						onCorrect();
						tButt.transform();
						// AlgebraActivity hostActivity =
						// ((EquationWrapper)hostNode.getWrapper()).getAlgebraActivity();
						// EquationNode replacement =
						// hostNode.getTree().newNode(response.getXMLClone());
						// hostNode.replace(replacement);
						// hostActivity.reloadEquationPanel(changeComment,
						// skillsIncrease, updateHistory)
						return;
					}
				}
				onIncorrect();

			}
		});
	}

	@Override
	public void resize() {
		super.resize();
		simplifyActivity.reloadEquationPanel(null, null, false);
	}

	@Override
	public void onCorrect() {
		disappear();

	}

	@Override
	public void onIncorrect() {
		Window.alert("incorrect");
	}

	class HelpButton extends SelectionButton {
		TransformationPanel tButtonPanel;

		HelpButton(TransformationPanel tButtonPanel) {
			this.tButtonPanel = tButtonPanel;
			addStyleName(CSS.LAYOUT_ROW);
			setHTML("Help!!! :(");
		}

		@Override
		protected void onSelect() {
			tButtonPanel.clear();

			// Show only the buttons that are not already shown on the main
			// Algebra Activity which have their requirements already met
			HashSet<SelectionButton> autoButtonsShown = new HashSet<SelectionButton>();
			for (TransformationButton tButt : tButtons) {
				if (!tButt.meetsAutoTransform()) {
					autoButtonsShown.add(tButt);
				}
			}
			tButtonPanel.addAll(autoButtonsShown);

		}

	}
}
