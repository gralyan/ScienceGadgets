package com.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.Moderator;
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
	private boolean showsEmptyRows = false;
	private boolean showsEmptyColumns = false;

	TestBot_Transformations() {
//		 this.showsEmptyRows = true;
//		 this.showsEmptyColumns = true;
		
		Moderator.isInEasyMode = true;
	}

	public void deploy() {
		testAddition_scenario(false, false);
		testAddition_scenario(false, true);
		testAddition_scenario(true, false);
		testAddition_scenario(true, true);
		testMultiplication_scenario();
		testExponential_scenario();
		testLog_scenario();
		testTrig_scenario();
	}

	public void testAddition_scenario(boolean isMinusBeforeLeft, boolean isMinus) {

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<MathNode> branches = new ArrayList<MathNode>();

		// Binary Branch cases
		Operator sign = isMinus ? Operator.MINUS : Operator.PLUS;

		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(), sign, false));

		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(), sign, true));

		// Special cases
		ArrayList<NodeCase> specialCases = new ArrayList<NodeCase>();
		specialCases.add(new NodeCase(TypeML.Variable, "a"));
		specialCases.add(new NodeCase(TypeML.Number, "-1"));
		specialCases.add(new NodeCase(TypeML.Number, "0"));
		specialCases.add(new NodeCase(TypeML.Number, "1"));

		branches.addAll(getSpecialBinaryCases(mTree.getLeftSide(), specialCases, Operator.PLUS));

		if (isMinusBeforeLeft) {
			for (MathNode child : branches) {
				child.getParent().addBefore(0, TypeML.Operation,
						Operator.MINUS.getSign());
			}
		}

		// ~~~~~~~~~~~~~~~~~~~~Test Cases~~~~~~~~~~~~~~~~~~~~

		HashMap<String, Integer> tButtonMap = new HashMap<String, Integer>();
		tButtonMap.put("AddZeroButton", 1);
		tButtonMap.put("AddNumbersButton", 2);
		tButtonMap.put("FactorLikeTermsButton", 3);
		tButtonMap.put("FactorBaseButton", 4);
		tButtonMap.put("FactorWithTermChildButton", 5);
		tButtonMap.put("ToCommonDenominatorButton", 6);
		tButtonMap.put("AddFractionsButton", 7);
		tButtonMap.put("AddLogsButton", 8);
		tButtonMap.put("AddSimilarButton", 9);

		FlexTable table = makeTable(tButtonMap, "Add");

		for (int i = 0; i < branches.size(); i++) {
			MathNode node = branches.get(i);
			AdditionTransformations transformList = new AdditionTransformations(
					node.getPrevSibling());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testMultiplication_scenario() {

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<MathNode> branches = new ArrayList<MathNode>();
		
		// Binary Branch cases
		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(),
				Operator.getMultiply(), false));
		
		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(),
				Operator.getMultiply(), true));

		// Special cases
		ArrayList<NodeCase> specialCases = new ArrayList<NodeCase>();
		specialCases.add(new NodeCase(TypeML.Variable, "a"));
		specialCases.add(new NodeCase(TypeML.Number, "-1"));
		specialCases.add(new NodeCase(TypeML.Number, "0"));
		specialCases.add(new NodeCase(TypeML.Number, "1"));

		branches.addAll(getSpecialBinaryCases(mTree.getLeftSide(), specialCases, Operator.getMultiply()));

		// ~~~~~~~~~~~~~~~~~~~~Test Cases~~~~~~~~~~~~~~~~~~~~

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

		for (int i = 0; i < branches.size(); i++) {
			MathNode node = branches.get(i);
			MultiplyTransformations transformList = new MultiplyTransformations(
					node.getPrevSibling());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testExponential_scenario() {

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<MathNode> branches = new ArrayList<MathNode>();

		// Binary Branch cases
		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(), null, false));
		
		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(), null, true));

		// Special cases
		ArrayList<NodeCase> specialCases = new ArrayList<NodeCase>();
		specialCases.add(new NodeCase(TypeML.Variable, "a"));
		specialCases.add(new NodeCase(TypeML.Number, "-1"));
		specialCases.add(new NodeCase(TypeML.Number, "0"));
		specialCases.add(new NodeCase(TypeML.Number, "1"));
		specialCases.add(new NodeCase(TypeML.Number, "2"));
		specialCases.add(new NodeCase(TypeML.Number, "3"));

		branches.addAll(getSpecialBinaryCases(mTree.getLeftSide(), specialCases, null));
		
		// ~~~~~~~~~~~~~~~~~~~~Test Cases~~~~~~~~~~~~~~~~~~~~

		HashMap<String, Integer> tButtonMap = new HashMap<String, Integer>();
		tButtonMap.put("ExponentialEvaluateButton", 1);
		tButtonMap.put("ExponentialExpandButton", 2);
		tButtonMap.put("ExponentialExponentiateButton", 3);
		tButtonMap.put("ExponentialPropagateButton", 4);
		tButtonMap.put("ExponentialFlipButton", 5);
		tButtonMap.put("ZeroBaseButton", 6);

		FlexTable table = makeTable(tButtonMap, "Exponential");

		for (int i = 0; i < branches.size(); i++) {
			MathNode node = branches.get(i);
			ExponentialTransformations transformList = new ExponentialTransformations(
					node.getParent());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testLog_scenario() {

		MathNode leftSide = mTree.getLeftSide().replace(TypeML.Log, "2");

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<MathNode> branches = new ArrayList<MathNode>();

		// Branch cases
		branches.addAll(getArgumentBranches(leftSide, true, false));

		// Special cases

		// ~~~~~~~~~~~~~~~~~~~~Test Cases~~~~~~~~~~~~~~~~~~~~

		HashMap<String, Integer> tButtonMap = new HashMap<String, Integer>();
		tButtonMap.put("LogEvaluateButton", 1);
		tButtonMap.put("LogProductButton", 2);
		tButtonMap.put("LogQuotientButton", 3);
		tButtonMap.put("LogPowerButton", 4);
		tButtonMap.put("LogOneButton", 5);
		tButtonMap.put("LogSameBaseAsArgumentButton", 5);
		tButtonMap.put("LogChangeBaseButton", 6);

		FlexTable table = makeTable(tButtonMap, "Log");

		for (int i = 0; i < branches.size(); i++) {
			MathNode node = branches.get(i);
			LogarithmicTransformations transformList = new LogarithmicTransformations(
					node.getParent());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testTrig_scenario() {

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<MathNode> branches = new ArrayList<MathNode>();

		// Function cases
		ArrayList<NodeCase> cases = new ArrayList<NodeCase>();
		for (TrigFunctions func : TrigFunctions.values()) {
			cases.add(new NodeCase(TypeML.Trig, func.toString()));
		}

		branches.addAll(makeCases(cases, mTree.getLeftSide(), true, false));

		// Branch cases
		// Special cases

		// ~~~~~~~~~~~~~~~~~~~~Test Cases~~~~~~~~~~~~~~~~~~~~
		HashMap<String, Integer> tButtonMap = new HashMap<String, Integer>();
		tButtonMap.put("TrigDefineButton", 1);
		tButtonMap.put("TrigReciprocalButton", 2);

		FlexTable table = makeTable(tButtonMap, "Trig");

		for (int i = 0; i < branches.size(); i++) {
			MathNode node = branches.get(i);
			TrigTransformations transformList = new TrigTransformations(node);

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	private FlexTable makeTable(HashMap<String, Integer> tButtonMap,
			String title) {

		FlexTable table = new FlexTable();
		RootPanel.get().add(table);

		// First row of table
		table.setText(0, 0, title);
		table.getRowFormatter().getElement(0).getStyle().setHeight(70, Unit.PX);
		if(showsEmptyColumns) {
		for (Entry<String, Integer> tButtonEntry : tButtonMap.entrySet()) {
			table.setText(0, tButtonEntry.getValue(), tButtonEntry.getKey());
		}
		}

		if(!showsEmptyColumns) {
			return null;
		}
		return table;
	}

	private void fillRow(TransformationList transformList,
			HashMap<String, Integer> tButtonMap, FlexTable table, MathNode node) {

		if (!showsEmptyRows && transformList.isEmpty()) {
			return;
		}
		
		if(table == null) {
			table = new FlexTable();
			RootPanel.get().add(table);
		}

		int row = table.getRowCount() + 1;

		MathTree tree = node.getTree();
		HTML testCase = new HTML();
		tree.reloadDisplay(true);
		testCase.setHTML(tree.getLeftDisplay());
		table.setWidget(row, 0, testCase);

		for (TransformationButton tButton : transformList) {
			String tButtonName = tButton.getClass().getName().replace(buttonPrefix, "");
			int column = tButtonMap.get(tButtonName);

			HTML testCaseTransformation = new HTML();
			tree.reloadDisplay(true);
			testCaseTransformation.setHTML(tree.getLeftDisplay());
			table.setWidget(row, 0, testCaseTransformation);

			MathTree previewEq = tButton.getPreview();
			if (previewEq == null) {
				table.setText(row, column, "error");
			} else {
				HTML preview = new HTML(previewEq.getRightDisplay());
				if(showsEmptyColumns) {
					table.setWidget(row, column, preview);
				}else {
					int cellCount = table.getCellCount(row);
					table.setText(row, cellCount, tButtonName);
					table.getCellFormatter().getElement(row, cellCount).getStyle().setBackgroundColor("yellow");
					table.setWidget(row, cellCount+1, preview);
				}
			}
		}

		table.getRowFormatter().getElement(row).getStyle()
				.setBackgroundColor("lightBlue");
	}

	ArrayList<MathNode> makeCases(ArrayList<NodeCase> caseList,
			MathNode toReplace, boolean isIncomplete, boolean containsAllTypes) {
		MathTree mTree = toReplace.getTree();
		String toReplaceId = toReplace.getId();
		ArrayList<MathNode> branches = new ArrayList<MathNode>();

		for (NodeCase branchSpec : caseList) {
			MathTree mTreeBranch = mTree.clone();
			MathNode toReplaceBranch = mTreeBranch.getNodeById(toReplaceId);
			MathNode branch = toReplaceBranch.replace(branchSpec.type,
					branchSpec.symbol);
			if (isIncomplete) {
				completeBranch(branch, containsAllTypes);
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
			boolean isIncomplete, boolean containsAllTypes) {

		ArrayList<NodeCase> branchList = new ArrayList<NodeCase>();

		switch (parent.getType()) {
		case Sum:
			branchList.addAll(branchAllBut(TypeML.Sum));
			break;
		case Term:
			branchList.addAll(branchAllBut(TypeML.Term));
			break;
		case Fraction:
		case Exponential:
		case Trig:
		case Log:
			branchList.addAll(branchAllBut(null));
			break;
		}

		MathNode toReplace = parent.append(TypeML.Variable, "case");
		return makeCases(branchList, toReplace, isIncomplete, containsAllTypes);
	}

	private ArrayList<MathNode> getBinaryArgumentBranches(MathNode toReplace,
			Operator operator, boolean containsAllTypes) {
		
		if(operator == null) {
			toReplace = toReplace.replace(TypeML.Exponential, "");
		}else if(operator.equals(Operator.PLUS) || operator.equals(Operator.MINUS)) {
			toReplace = toReplace.replace(TypeML.Sum, "");
		}else if(operator.equals(Operator.getMultiply())){
			toReplace = toReplace.replace(TypeML.Term, "");
		}

		ArrayList<MathNode> firstBranches = getArgumentBranches(toReplace, true,
				containsAllTypes);

		ArrayList<MathNode> binaryBranches = new ArrayList<MathNode>();
		for (MathNode branch : firstBranches) {
			MathNode branchParent = branch.getParent();
			if (operator != null) {
				branchParent.append(TypeML.Operation, operator.getSign());
			}
			ArrayList<MathNode> rightBranches = getArgumentBranches(
					branchParent, true, containsAllTypes);
			binaryBranches.addAll(rightBranches);
		}
		return binaryBranches;
	}

	ArrayList<MathNode> getSpecialBinaryCases(MathNode toReplace, ArrayList<NodeCase> specialCases, Operator operator) {

		if(operator == null) {
			toReplace = toReplace.replace(TypeML.Exponential, "");
		}else if(operator.equals(Operator.PLUS) || operator.equals(Operator.MINUS)) {
			toReplace = toReplace.replace(TypeML.Sum, "");
		}else if(operator.equals(Operator.getMultiply())){
			toReplace = toReplace.replace(TypeML.Term, "");
		}
		
		MathNode leftDummy = toReplace.append(TypeML.Variable,
				"toReplace");

		ArrayList<MathNode> leftCases = makeCases(specialCases, leftDummy,
				false, false);
		ArrayList<MathNode> binaryCases = new ArrayList<MathNode>();
		for (MathNode leftCase : leftCases) {
			MathNode caseParent = leftCase.getParent();
			if(operator != null) {
			caseParent.append(TypeML.Operation, operator.getSign());
			}
			MathNode rightDummy = caseParent.append(TypeML.Variable,
					"toReplace");
			ArrayList<MathNode> functionBranches = makeCases(specialCases,
					rightDummy, false, false);
			binaryCases.addAll(functionBranches);
		}
		return binaryCases;
	}

	/**
	 * Add terminal nodes to the branch to complete it
	 * 
	 * @param branch
	 */
	void completeBranch(MathNode branch, boolean containsAllTypes) {
		switch (branch.getType()) {
		case Number:
			branch.setSymbol("5");
			break;
		case Variable:
			branch.setSymbol("a");
			break;
		case Sum:
			if (containsAllTypes) {
				ArrayList<NodeCase> nodeCases = branchAllBut(branch.getType());
				for (int i = 0; i < nodeCases.size(); i++) {
					NodeCase nCase = nodeCases.get(i);
					if (i != 0) {
						branch.append(TypeML.Operation, Operator.PLUS.getSign());
					}
					completeBranch(branch.append(nCase.type, nCase.symbol),
							false);
				}
			} else {
				branch.append(TypeML.Variable, "a");
				branch.append(TypeML.Operation, Operator.PLUS.getSign());
				branch.append(TypeML.Variable, "a");
			}
			break;
		case Term:
			String multiply = Operator.getMultiply().getSign();
			if (containsAllTypes) {
				ArrayList<NodeCase> nodeCases = branchAllBut(branch.getType());
				for (int i = 0; i < nodeCases.size(); i++) {
					NodeCase nCase = nodeCases.get(i);
					if (i != 0) {
						branch.append(TypeML.Operation, Operator.getMultiply()
								.getSign());
					}
					completeBranch(branch.append(nCase.type, nCase.symbol),
							false);
				}
			} else {
				branch.append(TypeML.Variable, "a");
				branch.append(TypeML.Operation, multiply);
				branch.append(TypeML.Variable, "a");
			}
			break;
		case Fraction:
			if (containsAllTypes) {
				completeBranch(branch.append(TypeML.Term, ""), true);
				completeBranch(branch.append(TypeML.Term, ""), true);
			} else {
				branch.append(TypeML.Variable, "a");
				branch.append(TypeML.Variable, "a");
			}
			break;
		case Exponential:
			if (containsAllTypes) {
				completeBranch(branch.append(TypeML.Term, ""), true);
				branch.append(TypeML.Variable, "a");
			} else {
				branch.append(TypeML.Variable, "a");
				branch.append(TypeML.Number, "2");
			}
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
			}
			break;
		}
	}

	ArrayList<NodeCase> branchAllBut(TypeML excludedType) {

		ArrayList<NodeCase> branchMap = new ArrayList<NodeCase>();

		branchMap.add(new NodeCase(TypeML.Number, "9"));
		branchMap.add(new NodeCase(TypeML.Variable, "x"));

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
