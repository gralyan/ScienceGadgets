package com.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.transformations.AdditionTransformations;
import com.sciencegadgets.client.algebra.transformations.ExponentialTransformations;
import com.sciencegadgets.client.algebra.transformations.LogarithmicTransformations;
import com.sciencegadgets.client.algebra.transformations.MultiplyTransformations;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.TrigTransformations;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;

public class TestBot_Transformations {

	private static final String buttonPrefix = "com.sciencegadgets.client.algebra.transformations.";
	private MathTree mTree = new MathTree(false);
	private boolean showsEmptyRows;

	TestBot_Transformations() {

	}

	public void deploy(boolean showsEmptyRows) {
		this.showsEmptyRows = showsEmptyRows;
		JSNICalls.log("test initiated");
		 testAddition_scenario(false, false);
		 testAddition_scenario(false, true);
		 testAddition_scenario(true, false);
		 testAddition_scenario(true, true);
		 testMultiplication_scenario();
		 testExponential_scenario();
		 testLog_scenario();
		testTrig_scenario();
		JSNICalls.log("test terminated");

	}

	public void testAddition_scenario(boolean isMinusBeforeLeft, boolean isMinus) {

		MathNode leftSide = mTree.getLeftSide().replace(TypeML.Sum, "");

		//Binary Branch cases
		Operator sign = isMinus ? Operator.MINUS : Operator.PLUS;
		ArrayList<MathNode> binaryBranches = getBinaryArgumentBranches(leftSide, sign);
		if (isMinusBeforeLeft) {
			for(MathNode child :binaryBranches) {
				child.getParent().addBefore(0, TypeML.Operation,
					Operator.MINUS.getSign());
			}
		}
		
		//Special cases
		

		HashMap<String, Integer> tButtonMap = new HashMap<String, Integer>();
		tButtonMap.put("AddZeroButton", 1);
		tButtonMap.put("AddNumbersButton", 2);
		tButtonMap.put("FactorLikeTermsButton", 3);
		tButtonMap.put("FactorBaseButton", 4);
		tButtonMap.put("FactorWithTermChildButton", 5);
		tButtonMap.put("AddFractionsButton", 6);
		tButtonMap.put("AddLogsButton", 7);
		tButtonMap.put("AddSimilarButton", 8);

		FlexTable table = makeTable(tButtonMap, "Add");

		for (int i = 0; i < binaryBranches.size(); i++) {
			MathNode node = binaryBranches.get(i);
			AdditionTransformations transformList = new AdditionTransformations(
					node.getPrevSibling());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testMultiplication_scenario() {

		MathNode leftSide = mTree.getLeftSide().replace(TypeML.Term, "");

		//Binary Branch cases
		ArrayList<MathNode> binaryBranches = getBinaryArgumentBranches(leftSide, Operator.getMultiply());

		//Special cases
		
		HashMap<String, Integer> tButtonMap = new HashMap<String, Integer>();
		tButtonMap.put("MultiplyZeroButton", 1);
		tButtonMap.put("MultiplyOneButton", 2);
		tButtonMap.put("MultiplyNegOneButton", 3);
		tButtonMap.put("MultiplyNumbersButton", 4);
		tButtonMap.put("MultiplyDistributionButton", 5);
		tButtonMap.put("MultiplyCombineExponentsButton", 6);
		tButtonMap.put("MultiplyCombineBasesButton", 7);
		tButtonMap.put("MultiplyWithFractionButton", 8);
		tButtonMap.put("MultiplyFractionsButton", 9);
		tButtonMap.put("MultiplyLogRuleButton", 10);

		FlexTable table = makeTable(tButtonMap, "Multiplication");

		for (int i = 0; i < binaryBranches.size(); i++) {
			MathNode node = binaryBranches.get(i);
			MultiplyTransformations transformList = new MultiplyTransformations(
					node.getPrevSibling());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testExponential_scenario() {
		
		MathNode leftSide = mTree.getLeftSide().replace(TypeML.Exponential, "");
		
		//Binary Branch cases
		ArrayList<MathNode> binaryBranches = getBinaryArgumentBranches(leftSide, null);

		//Special cases
		
		HashMap<String, Integer> tButtonMap = new HashMap<String, Integer>();
		tButtonMap.put("ExponentialEvaluateButton", 1);
		tButtonMap.put("ExponentialExpandButton", 2);
		tButtonMap.put("ExponentialExponentiateButton", 3);
		tButtonMap.put("ExponentialPropagateButton", 4);
		tButtonMap.put("ExponentialFlipButton", 5);

		FlexTable table = makeTable(tButtonMap, "Exponential");

		for (int i = 0; i < binaryBranches.size(); i++) {
			MathNode node = binaryBranches.get(i);
			ExponentialTransformations transformList = new ExponentialTransformations(
					node.getParent());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testLog_scenario() {
		
		MathNode leftSide = mTree.getLeftSide().replace(TypeML.Log, "2");

		//Branch cases
		ArrayList<MathNode> argBranches = getArgumentBranches(leftSide, true, false);
		
		//Special cases

		HashMap<String, Integer> tButtonMap = new HashMap<String, Integer>();
		tButtonMap.put("LogEvaluateButton", 1);
		tButtonMap.put("LogProductButton", 2);
		tButtonMap.put("LogQuotientButton", 3);
		tButtonMap.put("LogPowerButton", 4);
		tButtonMap.put("LogOneButton", 5);
		tButtonMap.put("LogSameBaseAsArgumentButton", 5);
		tButtonMap.put("LogChangeBaseButton", 6);
		
		FlexTable table = makeTable(tButtonMap, "Log");

		for (int i = 0; i < argBranches.size(); i++) {
			MathNode node = argBranches.get(i);
			LogarithmicTransformations transformList = new LogarithmicTransformations(
					node.getParent());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testTrig_scenario() {

		//Branch cases
		
		// Function cases
		MathNode leftSide = mTree.getLeftSide()
				.replace(TypeML.Variable, "case");

		ArrayList<NodeCase> cases = new ArrayList<NodeCase>();
		for (TrigFunctions func : TrigFunctions.values()) {
			cases.add(new NodeCase(TypeML.Trig, func.toString()));
		}

		ArrayList<MathNode> functionBranches = makeCases(cases, leftSide, true);

		//Special cases

		HashMap<String, Integer> tButtonMap = new HashMap<String, Integer>();
		tButtonMap.put("TrigDefineButton", 1);
		tButtonMap.put("TrigReciprocalButton", 2);

		FlexTable table = makeTable(tButtonMap, "Trig");

		for (int i = 0; i < functionBranches.size(); i++) {
			MathNode node = functionBranches.get(i);
			TrigTransformations transformList = new TrigTransformations(node);

			fillRow(transformList, tButtonMap, table, node);
		}
	}
	
	private FlexTable makeTable(HashMap<String, Integer> tButtonMap, String title) {

		FlexTable table = new FlexTable();
		RootPanel.get().add(table);

		// First row of table
		table.setText(0, 0, title);
		table.getRowFormatter().getElement(0).getStyle().setHeight(70, Unit.PX);
		for (Entry<String, Integer> tButtonEntry : tButtonMap.entrySet()) {
			table.setText(0, tButtonEntry.getValue(), tButtonEntry.getKey());

		}
		
		return table;
	}

	private void fillRow(TransformationList transformList,
			HashMap<String, Integer> tButtonMap, FlexTable table, MathNode node) {

		if (!showsEmptyRows && transformList.isEmpty()) {
			return;
		}

		int row = table.getRowCount() + 1;

		MathTree tree = node.getTree();
		HTML testCase = new HTML();
		tree.reloadDisplay(true);
		testCase.setHTML(tree.getLeftDisplay());
		table.setWidget(row, 0, testCase);

		for (TransformationButton tButton : transformList) {
			String tButtonName = tButton.getClass().getName();
			int column = tButtonMap.get(tButtonName.replace(buttonPrefix, ""));

			HTML testCaseTransformation = new HTML();
			tree.reloadDisplay(true);
			testCaseTransformation.setHTML(tree.getLeftDisplay());
			table.setWidget(row, 0, testCaseTransformation);

			MathTree previewEq = tButton.getPreview();
			if (previewEq == null) {
				table.setText(row, column, "error");
			} else {
				HTML preview = new HTML(previewEq.getRightDisplay());
				table.setWidget(row, column, preview);
			}
		}

		table.getRowFormatter().getElement(row).getStyle()
				.setBackgroundColor("lightBlue");
	}

	ArrayList<MathNode> makeCases(ArrayList<NodeCase> caseList,
			MathNode toReplace, boolean isTerminal) {
		MathTree mTree = toReplace.getTree();
		String parentId = toReplace.getId();
		ArrayList<MathNode> branches = new ArrayList<MathNode>();

		for (NodeCase branchSpec : caseList) {
			MathTree mTreeBranch = mTree.clone();
			MathNode parentBranch = mTreeBranch.getNodeById(parentId);
			MathNode branch = parentBranch.replace(branchSpec.type,
					branchSpec.symbol);
			if (isTerminal) {
				completeBranch(branch);
			}
			branches.add(branch);
		}
		return branches;
	}

	/**
	 * Creates a new MathTree for each variation branch
	 * 
	 * @return - A list of the varied nodes in their own MathTree
	 */
	private ArrayList<MathNode> getArgumentBranches(MathNode parent,
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

		return makeCases(branchList, parent.append(TypeML.Variable, "case"),
				isTerminal);
	}

	private ArrayList<MathNode> getBinaryArgumentBranches(MathNode parent, Operator operator){
		
		ArrayList<MathNode> firstBranches = getArgumentBranches(parent, true, false);

		ArrayList<MathNode> binaryBranches = new ArrayList<MathNode>();
		for (MathNode branch : firstBranches) {
			MathNode branchParent = branch.getParent();
			if(operator != null) {
			branchParent.append(TypeML.Operation, operator
					.getSign());
			}
			ArrayList<MathNode> rightBranches = getArgumentBranches(branchParent, true,
					false);
			binaryBranches.addAll(rightBranches);
		}
		return binaryBranches;
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
			branch.setSymbol("a");
			break;
		case Sum:
			branch.append(TypeML.Variable, "a");
			branch.append(TypeML.Operation, Operator.PLUS.getSign());
			branch.append(TypeML.Variable, "a");
			break;
		case Term:
			branch.append(TypeML.Variable, "a");
			branch.append(TypeML.Operation, Operator.getMultiply().getSign());
			branch.append(TypeML.Variable, "a");
			break;
		case Fraction:
			branch.append(TypeML.Variable, "a");
			branch.append(TypeML.Variable, "a");
			break;
		case Exponential:
			branch.append(TypeML.Variable, "a");
			branch.append(TypeML.Variable, "a");
			break;
		case Trig:
			branch.append(TypeML.Variable, "a");
			if ("".equals(branch.getAttribute(MathAttribute.Function))) {
				branch.setAttribute(MathAttribute.Function,
						TrigFunctions.sin.toString());
			}
			break;
		case Log:
			branch.append(TypeML.Variable, "a");
			if ("".equals(branch.getAttribute(MathAttribute.LogBase))) {
			branch.setAttribute(MathAttribute.LogBase, "10");
			}			break;

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
