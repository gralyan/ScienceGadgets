package com.sciencegadgets.client.algebra.transformations.specification;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.core.java.util.Collections;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.AlgebraActivity.TransformationPanel;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.ui.Quiz;
import com.sciencegadgets.shared.TypeSGET;

public class SimplifyQuiz extends Quiz {

	private static AlgebraActivity simplifyActivity;
	TransformationList<TransformationButton> tButtons;

	public SimplifyQuiz(final EquationNode hostNode,
			final TransformationList<TransformationButton> tButtons) {

		this.tButtons = tButtons;

		// Make Tree
		// final EquationNode hostNode = tButtons.getFirst().getTransformList()
		// .getNode();
		Element hostXML = hostNode.getXMLClone();
		if (TypeSGET.Operation.equals(hostNode.getType())) {
			Element hostParentOperation = (Element) hostNode.getParent()
					.getXMLNode().cloneNode(false);
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
		for (TransformationButton tButt : tButtons) {
			if (tButt.meetsAutoTransform()) {
				tButtonPanel.add(tButt);
			}
		}
		simplifyActivity.upperMidEqArea.add(tButtonPanel);

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
						tButt.transform();
//						EquationNode replacement = hostNode.getTree().newNode(response.getXMLClone());
//						JSNICalls.log("replace "+replacement);
//						JSNICalls.log("hostNode.getTree() "+hostNode.getTree());
//						hostNode.replace(replacement);
//						JSNICalls.log("hostNode.getTree() "+hostNode.getTree());
						onCorrect();
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

}
