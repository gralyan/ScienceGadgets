/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.algebra.transformations.specification;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.AlgebraActivity.TransformationPanel;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.Quiz;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.shared.TypeSGET;

public class SimplifyQuiz extends Quiz {

	private static AlgebraActivity simplifyActivity;
	TransformationList<TransformationButton> tButtonsBasic;
	ArrayList<TransformationProceedure> proceeduresAllPossible = new ArrayList<TransformationProceedure>();
	private Element hostXML;
	private static final String TRANSFORMATION_PACKAGE = "com.sciencegadgets.client.algebra.transformations.";

	public SimplifyQuiz(EquationNode hostNode,
			final TransformationList<TransformationButton> tButtons) {

		this.tButtonsBasic = tButtons;

		// ////////////////////////
		// Make Tree of before/after
		hostXML = hostNode.getXMLClone();
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
		EquationNode workingRight = workingTree.newNode((Element) hostXML
				.cloneNode(true));
		workingTree.getLeftSide().replace(workingLeft);
		workingTree.getRightSide().replace(workingRight);

		// ////////////////////////
		// Set Up Activity
		if (simplifyActivity == null) {
			simplifyActivity = new AlgebraActivity(workingTree, null,
					ActivityType.simplifyquiz);
		} else {
			simplifyActivity.setEquationTree(workingTree, null);
		}
		simplifyActivity.reloadEquationPanel(null, null, false, null);

		TransformationPanel tButtonPanel = simplifyActivity.new TransformationPanel(
				false);
		simplifyActivity.setDefaultUpperMidWidget(tButtonPanel);
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
						tButt.allowSkillIncrease(true);
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
	public void appear() {
		super.appear();
		//TODO in case of Skipped steps
//		findPossibleProceedures();
	}

	@Override
	public void resize() {
		super.resize();
		simplifyActivity.reloadEquationPanel(null, null, false, null);
	}

	@Override
	public void onCorrect() {
		super.onCorrect();
		disappear();

	}

	@Override
	public void onIncorrect() {
		super.onIncorrect();
		Window.alert("incorrect");
	}

	class HelpButton extends SelectionButton {
		TransformationPanel tButtonPanel;

		HelpButton(TransformationPanel tButtonPanel) {
			this.tButtonPanel = tButtonPanel;
			addStyleName(CSS.LAYOUT_ROW);
			setHTML("Hints");
		}

		@Override
		protected void onSelect() {
			// tButtonPanel.clear();

			// Show only the buttons that are not already shown on the main
			// Algebra Activity which have their requirements already met
			// HashSet<SelectionButton> autoButtonsShown = new
			// HashSet<SelectionButton>();

			ArrayList<String> exampleHtmls = new ArrayList<String>();
			for (TransformationButton tButt : tButtonsBasic) {
				if (!tButt.meetsAutoTransform()) {
					// tButt.isHelpButton = true;
					// autoButtonsShown.add(tButt);

					for (String ex : tButt.getAssociatedBadge().getSkill()
							.getExampleHTMLs()) {
						exampleHtmls.add(ex);
					}
				}
			}

			final Prompt examplePrompt = new Prompt();
			for (String exampleHTML : exampleHtmls) {
				HTML exHTML = new HTML(exampleHTML);
				exHTML.addStyleName(CSS.EXAMPLE_HTML);
				examplePrompt.add(exHTML);
			}
			examplePrompt.addOkHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent arg0) {
					examplePrompt.disappear();
				}
			});
			examplePrompt.appear();

			// tButtonPanel.addAll(autoButtonsShown);

		}

	}

	private void findPossibleProceedures() {

		// ////////////////////////
		// Find possible solutions
		EquationTree previewTree = new EquationTree(true);
		EquationNode previewLeft = previewTree.newNode((Element) hostXML
				.cloneNode(true));
		previewTree.getLeftSide().replace(previewLeft);
		TransformationProceedure noProceedure = new TransformationProceedure(
				null, previewLeft, previewLeft);
		Moderator.isInEasyMode = true;
		findPossibleProceeduresRecursion(noProceedure);
		Moderator.isInEasyMode = false;
		proceeduresAllPossible.remove(noProceedure);

		AbsolutePanel p = Moderator.scienceGadgetArea;
		for (TransformationProceedure a : proceeduresAllPossible) {
			for (TransformationButton b : a) {
				p.add(new Label(b.getClass().getName()));
			}
//			 p.add(new Label(a.getFrame().toString()));
			p.add(new HTML(a.getFrame().getHTMLString(true, true)));
			p.add(new HTML(a.getChange().getHTMLString(true, true)));
			p.add(new Label("."));

		}
	}

	// private EquationNode collectPossibleEquations(TransformationProceedure
	// baseProceedure) {
	//
	// }

	private void findPossibleProceeduresRecursion(
			TransformationProceedure baseProceedure) {

		EquationNode currentNode = baseProceedure.getChange();
		LinkedList<EquationNode> branchNodes = currentNode.getNodesByType(null);
		branchNodes.add(currentNode);

		for (EquationNode bNode : branchNodes) {
			TransformationList<TransformationButton> branches = TransformationList
					.FIND_ALL_SIMPLIFY(bNode);
			for (TransformationButton branch : branches) {
				EquationNode preview = branch.getPreview();
				if (preview == null) {
					JSNICalls.log("skipped branch: "
							+ branch.getClass().getName()
									.replace(TRANSFORMATION_PACKAGE, ""));
					continue;
				}

				EquationTree frameTree = new EquationTree(false);
				EquationNode frame = frameTree.getLeftSide();
				frame.replace(frameTree.newNode(new HTML(currentNode.toString())
						.getElement().getFirstChildElement()));
				EquationNode changing = frameTree.getNodeById(bNode.getId());
				EquationNode changed=changing.replace(frameTree.newNode(new HTML(preview.toString())
						.getElement().getFirstChildElement()));
				if(TypeSGET.Operation.equals(bNode.getType())) {
//					changed.getNextSibling().remove();
//					changed.getPrevSibling().remove();
				}
				
				TransformationProceedure newProceedure = new TransformationProceedure(
						baseProceedure, frameTree.getLeftSide(), preview);
				newProceedure.addAll(baseProceedure);
				newProceedure.add(branch);
				proceeduresAllPossible.add(newProceedure);
				findPossibleProceeduresRecursion(newProceedure);
			}
		}

	}

	class TransformationProceedure extends ArrayList<TransformationButton> {
		private static final long serialVersionUID = 1L;

//		ArrayList<TransformationProceedure> childProceedures = new ArrayList<TransformationProceedure>();
//		private TransformationProceedure parentProceedure;
		private EquationNode frame;
		private EquationNode change;

		public TransformationProceedure(
				TransformationProceedure parentProceedure, EquationNode frame,
				EquationNode change) {
			super();
//			if (parentProceedure != null) {
//				parentProceedure.childProceedures.add(this);
//			}
//			this.parentProceedure = parentProceedure;
			this.change = change;
			this.frame = frame;
		}

//		public TransformationProceedure getParentProceedure() {
//			return parentProceedure;
//		}

		public EquationNode getFrame() {
			return frame;
		}

		public EquationNode getChange() {
			return change;
		}

	}
}
