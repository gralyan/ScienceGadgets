package com.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable.RowFormatter;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.transformations.AdditionTransformations;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class TestBot_Transformations {
	
	static final String buttonPrefix = "com.sciencegadgets.client.algebra.transformations.";

	TestBot_Transformations() {
	}

	public void testAddition(MathTree mTree) {

	}

	public void testAddition_scenario() {

		MathTree mTree = new MathTree(TypeML.Number, "1", TypeML.Number, "1",
				false);

		MathNode leftSide = mTree.getLeftSide();

		leftSide = leftSide.replace(TypeML.Sum, "");

		ArrayList<MathNode> firstBranches = addBranches(leftSide, true, false);

		ArrayList<MathNode> binaryBranches = new ArrayList<MathNode>();
		for (MathNode branch : firstBranches) {
			MathNode branchParent = branch.getParent();
			MathNode operation = branchParent.append(TypeML.Operation,
					Operator.PLUS.getSign());
			ArrayList<MathNode> rightBranches = addBranches(branchParent, true,
					false);
			binaryBranches.addAll(rightBranches);
		}
		
		HashMap<String, Integer> tButtonMap =  new HashMap<String, Integer>();
		tButtonMap.put("AddZeroButton", 1);
		tButtonMap.put("AddNumbersButton", 2);
		tButtonMap.put("FactorLikeTermsButton", 3);
		tButtonMap.put("FactorBaseButton", 4);
		tButtonMap.put("FactorWithTermChildButton", 5);
		tButtonMap.put("AddFractionsButton", 6);
		tButtonMap.put("AddLogsButton", 7);
		tButtonMap.put("AddSimilarButton", 8);
		
		FlexTable table = new FlexTable();
		RowFormatter rowFormat = table.getRowFormatter();
		RootPanel.get().add(table);

		//First row of table
		table.setText(0, 0, "Addition");
		for(Entry<String, Integer> tButtonEntry : tButtonMap.entrySet()) {
			table.setText(0, tButtonEntry.getValue(), tButtonEntry.getKey());
			
		}

		for (int i = 0 ; i<binaryBranches.size(); i++) {
			MathNode node = binaryBranches.get(i);
			int row = i+1;
			
			MathTree tree = node.getTree();
			HTML testCase = new HTML();
			tree.reloadDisplay(true);
			testCase.setHTML(tree.getLeftDisplay());
			table.setWidget(row, 0, testCase);
			
			AdditionTransformations addT = new AdditionTransformations(node.getPrevSibling().getPrevSibling(),
					node.getPrevSibling(), node, true);
			
			for(TransformationButton tButton : addT) {
				String tButtonName = tButton.getClass().getName();
				int column = tButtonMap.get(tButtonName.replace(buttonPrefix, ""));
				
				HTML testCaseTransformation = new HTML();
				tree.reloadDisplay(true);
				testCaseTransformation.setHTML(tree.getLeftDisplay());
				table.setWidget(row, 0, testCaseTransformation);

//				tButton.setNode(node.getPrevSibling());
//				tButton.getPreview();
				table.setText(row, column, "1");
			}

			rowFormat.getElement(row).getStyle().setBackgroundColor("lightBlue");
		}
	}

	/**
	 * Creates a new MathTree for each variation branch
	 * 
	 * @return - A list of the varied nodes in their own MathTree
	 */
	private ArrayList<MathNode> addBranches(MathNode parent,
			boolean isTerminal, boolean isTestedForSimilarities) {

		ArrayList<NodeCase> branchList = new ArrayList<NodeCase>();

		switch (parent.getType()) {
		case Sum:
			branchList
					.addAll(branchAllBut(TypeML.Sum, isTestedForSimilarities));
			break;
		case Term:
			branchList
					.addAll(branchAllBut(TypeML.Term, isTestedForSimilarities));
			break;
		case Fraction:
		case Exponential:
		case Trig:
		case Log:
			branchList.addAll(branchAllBut(null, isTestedForSimilarities));
			break;
		}

		MathTree mTree = parent.getTree();
		String parentId = parent.getId();
		ArrayList<MathNode> branches = new ArrayList<MathNode>();

		for (NodeCase branchSpec : branchList) {
			MathTree mTreeBranch = mTree.clone();
			MathNode parentBranch = mTreeBranch.getNodeById(parentId);
			MathNode branch = parentBranch.append(branchSpec.type,
					branchSpec.symbol);
			if (isTerminal) {
				completeBranch(branch);
			}
			branches.add(branch);
		}

		return branches;
	}

	/**
	 * Add terminal nodes to the branch to complete it
	 * 
	 * @param branch
	 */
	void completeBranch(MathNode branch) {
		switch (branch.getType()) {
		case Number:
			branch.setSymbol("5");
			break;
		case Variable:
			branch.setSymbol("x");
			break;
		case Sum:
			branch.append(TypeML.Variable, "sumArg1");
			branch.append(TypeML.Operation, Operator.PLUS.getSign());
			branch.append(TypeML.Variable, "sumArg2");
			break;
		case Term:
			branch.append(TypeML.Variable, "termArg1");
			branch.append(TypeML.Operation, Operator.getMultiply().getSign());
			branch.append(TypeML.Variable, "termArg2");
			break;
		case Fraction:
			branch.append(TypeML.Variable, "num");
			branch.append(TypeML.Variable, "den");
			break;
		case Exponential:
			branch.append(TypeML.Variable, "base");
			branch.append(TypeML.Variable, "exp");
			break;
		case Trig:
			branch.append(TypeML.Variable, "trigArg");
			branch.setAttribute(MathAttribute.Function,
					TrigFunctions.sin.toString());
			break;
		case Log:
			branch.append(TypeML.Variable, "logArg");
			branch.setAttribute(MathAttribute.LogBase, "10");
			break;

		}
	}

	ArrayList<NodeCase> branchAllBut(TypeML excludedType,
			boolean isTestedForSimilarities) {

		ArrayList<NodeCase> branchMap = new ArrayList<NodeCase>();

		if (isTestedForSimilarities) {
			branchMap.add(new NodeCase(TypeML.Number, "-1"));
			branchMap.add(new NodeCase(TypeML.Number, "0"));
			branchMap.add(new NodeCase(TypeML.Number, "1"));

			branchMap.add(new NodeCase(TypeML.Number, "-5"));
			branchMap.add(new NodeCase(TypeML.Number, "7"));

			branchMap.add(new NodeCase(TypeML.Variable, "a"));
			branchMap.add(new NodeCase(TypeML.Variable, "b"));
		} else {
			branchMap.add(new NodeCase(TypeML.Number, "9"));
			branchMap.add(new NodeCase(TypeML.Variable, "x"));
		}

		if (!TypeML.Sum.equals(excludedType))
			branchMap.add(new NodeCase(TypeML.Sum, ""));
		if (!TypeML.Term.equals(excludedType))
			branchMap.add(new NodeCase(TypeML.Term, ""));
		if (!TypeML.Fraction.equals(excludedType))
			branchMap.add(new NodeCase(TypeML.Fraction, ""));
		if (!TypeML.Exponential.equals(excludedType))
			branchMap.add(new NodeCase(TypeML.Exponential, ""));
		if (!TypeML.Trig.equals(excludedType))
			branchMap.add(new NodeCase(TypeML.Trig, ""));
		if (!TypeML.Log.equals(excludedType))
			branchMap.add(new NodeCase(TypeML.Log, ""));

		return branchMap;
	}

	class NodeCase {
		TypeML type;
		String symbol;

		public NodeCase(TypeML type, String symbol) {
			super();
			this.type = type;
			this.symbol = symbol;
		}

	}
}
