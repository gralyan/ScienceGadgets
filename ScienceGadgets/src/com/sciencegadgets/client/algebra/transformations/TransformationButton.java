package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.FitParentHTML;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class TransformationButton extends SimplePanel implements
		HasClickHandlers {

	FitParentHTML buttonHTML;
	private TransformationList transformList;

	public TransformationButton(TransformationList context) {
		super();
		this.transformList = context;

		addStyleName(CSS.TRANSFORMATION_BUTTON + " " + CSS.LAYOUT_ROW);
	}

	public TransformationButton(String html, TransformationList context) {
		this(context);
		setHTML(html);
	}

	public void setHTML(String html) {
		clear();
		buttonHTML = new FitParentHTML(html);
		buttonHTML.percentOfParent = 85;
		add(buttonHTML);
	}

	public String getHTML() {
		if (buttonHTML == null) {
			return null;
		} else {
			return buttonHTML.getHTML();
		}
	}

	public TransformationList getTransformList() {
		return transformList;
	}

	/**
	 * @return The HTML display version of the transformation
	 */
	public MathTree getPreview() {

		if (transformList.beforeAfterTree == null) {
			MathTree mTree = transformList.beforeAfterTree = new MathTree(false);

			MathNode frame;
			if (TypeML.Operation.equals(transformList.getNode().getType())) {
				MathNode op = transformList.getNode();
				frame = mTree.getLeftSide().replace(op.getParentType(), "");

				MathNode possibleMinus = op.getPrevSibling().getPrevSibling();
				if (possibleMinus != null
						&& Operator.MINUS.getSign().equals(
								possibleMinus.getSymbol())) {
					frame.append(TypeML.Operation, Operator.MINUS.getSign());
				}

				MathNode leftClone, operationClone, rightClone;
				leftClone = mTree.NEW_NODE(op.getPrevSibling().getXMLClone());
				operationClone = mTree.NEW_NODE(op.getXMLClone());
				rightClone = mTree.NEW_NODE(op.getNextSibling().getXMLClone());

				frame.append(leftClone);
				frame.append(operationClone);
				frame.append(rightClone);

			} else {
				frame = mTree.NEW_NODE(transformList.getNode().getXMLClone());
				mTree.getLeftSide().replace(frame);
			}
		}

		MathTree mTree = transformList.beforeAfterTree;
		mTree.getRightSide().replace(mTree.getLeftSide().clone());
		MathNode previewContextNode = mTree.getRightSide();
		if (TypeML.Sum.equals(previewContextNode.getType())
				|| TypeML.Term.equals(previewContextNode.getType())) {
			int operationIndex = previewContextNode.getChildCount() == 3 ? 1
					: 2;
			previewContextNode = previewContextNode.getChildAt(operationIndex);
		}

		TransformationButton previewButton = this
				.getPreviewButton(previewContextNode);

		if (previewButton == null) {
			return null;
		}
		previewButton.fireEvent(new ClickEvent() {
		});
		mTree.reloadDisplay(true, true);
		return mTree;
	}

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}

	TransformationButton getPreviewButton(MathNode newNode) {
		return null;
	}
}
