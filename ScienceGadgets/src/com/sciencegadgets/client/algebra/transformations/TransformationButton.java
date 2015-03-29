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
package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.FitParentHTML;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.Operator;

public abstract class TransformationButton extends SelectionButton implements
		HasClickHandlers, HasTouchEndHandlers {

	private TransformationList<? extends TransformationButton> transformList;
	public EquationNode previewNode = null;
	protected boolean isEvaluation = false;
	protected boolean allowSkillIncrease = false;

	public TransformationButton(
			TransformationList<? extends TransformationButton> context) {
		super();
		this.transformList = context;

		addStyleName(CSS.TRANSFORMATION_BUTTON + " " + CSS.LAYOUT_ROW);
	}

	public TransformationButton(String html,
			TransformationList<? extends TransformationButton> context) {
		this(context);
		setHTML(html);
	}

	public TransformationList<? extends TransformationButton> getTransformList() {
		return transformList;
	}
	
	public EquationNode getNode() {
		return transformList.getNode();
	}

	/**
	 * @return The HTML display version of the transformation
	 */
	public EquationNode getPreview() {

		if (previewNode != null) {
			return previewNode;
		}

		if (transformList.beforeAfterTree == null) {
			EquationTree mTree = new EquationTree(false);
			EquationNode frame;

			if (TypeSGET.Operation.equals(transformList.getNode().getType())) {
				EquationNode op = transformList.getNode();
				frame = mTree.getLeftSide().replace(op.getParentType(), "");

				EquationNode possibleMinus = op.getPrevSibling()
						.getPrevSibling();
				if (possibleMinus != null
						&& Operator.MINUS.getSign().equals(
								possibleMinus.getSymbol())) {
					frame.append(TypeSGET.Operation, Operator.MINUS.getSign());
				}

				EquationNode leftClone, operationClone, rightClone;
				leftClone = mTree.newNode(op.getPrevSibling().getXMLClone());
				operationClone = mTree.newNode(op.getXMLClone());
				rightClone = mTree.newNode(op.getNextSibling().getXMLClone());

				frame.append(leftClone);
				frame.append(operationClone);
				frame.append(rightClone);

			} else {
				frame = mTree.newNode(transformList.getNode().getXMLClone());
				mTree.getLeftSide().replace(frame);
			}
			transformList.beforeAfterTree = mTree;
		}

		EquationTree previewTree = new EquationTree(
				transformList.beforeAfterTree.getEquationXMLClone(), false);
		previewTree.getRightSide().replace(previewTree.getLeftSide().clone());
		EquationNode previewContextNode = previewTree.getRightSide();
		if (TypeSGET.Sum.equals(previewContextNode.getType())
				|| TypeSGET.Term.equals(previewContextNode.getType())) {
			int operationIndex = previewContextNode.getChildCount() == 3 ? 1
					: 2;
			previewContextNode = previewContextNode.getChildAt(operationIndex);
		}

		TransformationButton previewButton = this
				.getPreviewButton(previewContextNode);

		if (previewButton == null) {
			return null;
		}
		previewButton.transform();
		previewNode = previewTree.getRightSide();
		return previewNode;
	}

	// Buttons with a preview must override this method
	TransformationButton getPreviewButton(EquationNode newNode) {
		return null;
	}

	public boolean isEvaluation() {
		return isEvaluation;
	}

	@Override
	protected void onSelect() {
		Moderator.getCurrentAlgebraActivity().getEquationPanel().unselectCurrentSelection();;
		transform();
	}

	public void allowSkillIncrease(boolean allow) {
		allowSkillIncrease = allow;
	}

	protected void onTransformationEnd(String changeComment) {

		Badge badge = getAssociatedBadge();
		Skill skills = badge == null ? null : badge.getSkill();
		
		if (allowSkillIncrease && skills != null) {
			Moderator.increaseSkill(skills, 1);
		}

		if (transformList.reloadAlgebraActivity) {
			Moderator.reloadEquationPanel(changeComment, skills);
		}
	}

	public abstract void transform();

	public boolean meetsAutoTransform() {
		return Moderator.meetsRequirement(getAssociatedBadge());
	}

	public abstract Badge getAssociatedBadge();

	public String getExampleHTML() {
		return "";
	}
}
