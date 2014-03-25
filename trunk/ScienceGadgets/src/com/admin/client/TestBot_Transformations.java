package com.admin.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.transformations.AdditionTransformations;
import com.sciencegadgets.client.algebra.transformations.ExponentialTransformations;
import com.sciencegadgets.client.algebra.transformations.LogarithmicTransformations;
import com.sciencegadgets.client.algebra.transformations.MultiplyTransformations;
import com.sciencegadgets.client.algebra.transformations.TransformationButton;
import com.sciencegadgets.client.algebra.transformations.TransformationList;
import com.sciencegadgets.client.algebra.transformations.TrigTransformations;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TrigFunctions;
import com.sciencegadgets.shared.TypeEquationXML;
import com.sciencegadgets.shared.TypeEquationXML.Operator;

public class TestBot_Transformations {

	private static final String buttonPrefix = "com.sciencegadgets.client.algebra.transformations.";
	private EquationTree mTree = new EquationTree(false);
	private boolean showsEmptyRows = false;
	private boolean showsEmptyColumns = false;

	TestBot_Transformations() {
		// this.showsEmptyRows = true;
		// this.showsEmptyColumns = true;
		
		Moderator.isInEasyMode = true;
	}

	public void deploy() {
//		testAddition_scenario(false, false);
//		 testAddition_scenario(false, true);
//		 testAddition_scenario(true, false);
//		 testAddition_scenario(true, true);
		 testMultiplication_scenario();
//		 testExponential_scenario();
//		 testLog_scenario();
//		 testTrig_scenario();
	}

	public void testAddition_scenario(boolean isMinusBeforeLeft, boolean isMinus) {

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<EquationNode> branches = new ArrayList<EquationNode>();

		// Binary Branch cases
		Operator sign = isMinus ? Operator.MINUS : Operator.PLUS;

		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(), sign,
				false));

		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(), sign,
				true));

		// Special cases
		ArrayList<NodeCase> specialCases = new ArrayList<NodeCase>();
		specialCases.add(new NodeCase(TypeEquationXML.Variable, "a"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "-1"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1"));

		branches.addAll(getSpecialBinaryCases(mTree.getLeftSide(),
				specialCases, Operator.PLUS));

		if (isMinusBeforeLeft) {
			for (EquationNode child : branches) {
				child.getParent().addFirst(TypeEquationXML.Operation,
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
		tButtonMap.put("FactorButton", 10);
		tButtonMap.put("CombineLikeTermsButton", 11);

		FlexTable table = makeTable(tButtonMap, "Add");

		for (int i = 0; i < branches.size(); i++) {
			EquationNode node = branches.get(i);
			AdditionTransformations transformList = new AdditionTransformations(
					node.getPrevSibling());

//			LinkedList<TransformationButton> exclude = new LinkedList<TransformationButton>();
//			for (TransformationButton a : transformList) {
//				if (!a.getClass().getName().contains("FactorButton")
//						&& !a.getClass().getName()
//								.contains("CombineLikeTermsButton")) {
//					exclude.add(a);
//				}
//			}
//			for (TransformationButton ex : exclude) {
//				transformList.remove(ex);
//			}

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testMultiplication_scenario() {

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<EquationNode> branches = new ArrayList<EquationNode>();

		// Binary Branch cases
//		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(),
//				Operator.getMultiply(), false));
//
//		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(),
//				Operator.getMultiply(), true));

		// Special cases
		ArrayList<NodeCase> specialCases = new ArrayList<NodeCase>();
//		specialCases.add(new NodeCase(TypeML.Variable, "a"));
//		specialCases.add(new NodeCase(TypeML.Number, "-1"));
//		specialCases.add(new NodeCase(TypeML.Number, "0"));
//		specialCases.add(new NodeCase(TypeML.Number, "1"));
//		
//		branches.addAll(getSpecialBinaryCases(mTree.getLeftSide(),
//				specialCases, Operator.getMultiply()));
		
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".00000010"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".0000010"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".000010"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".00010"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".0010"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".010"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".10"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "10.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "100.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1000.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "10000.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "100000.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1000000.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "10000000.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".0000001234567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".000001234567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".00001234567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".0001234567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".001234567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".01234567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, ".1234567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1.234567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "12.34567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "123.4567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1234.567890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "12345.67890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "123456.7890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1234567.890"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "12345678.90"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "123456789.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1234567890.0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "12345678900.0"));
		
		branches.addAll(getSpecialBinaryCases(mTree.getLeftSide(),
		specialCases, Operator.getMultiply()));

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
			EquationNode node = branches.get(i);
			MultiplyTransformations transformList = new MultiplyTransformations(
					node.getPrevSibling());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testExponential_scenario() {

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<EquationNode> branches = new ArrayList<EquationNode>();

		// Binary Branch cases
		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(), null,
				false));

		branches.addAll(getBinaryArgumentBranches(mTree.getLeftSide(), null,
				true));

		// Special cases
		ArrayList<NodeCase> specialCases = new ArrayList<NodeCase>();
		specialCases.add(new NodeCase(TypeEquationXML.Variable, "a"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "-1"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "0"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "1"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "2"));
		specialCases.add(new NodeCase(TypeEquationXML.Number, "3"));

		branches.addAll(getSpecialBinaryCases(mTree.getLeftSide(),
				specialCases, null));

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
			EquationNode node = branches.get(i);
			ExponentialTransformations transformList = new ExponentialTransformations(
					node.getParent());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testLog_scenario() {

		EquationNode leftSide = mTree.getLeftSide().replace(TypeEquationXML.Log, "2");

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<EquationNode> branches = new ArrayList<EquationNode>();

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
			EquationNode node = branches.get(i);
			LogarithmicTransformations transformList = new LogarithmicTransformations(
					node.getParent());

			fillRow(transformList, tButtonMap, table, node);
		}
	}

	public void testTrig_scenario() {

		// ~~~~~~~~~~~~~~~~~~~~Collect Cases~~~~~~~~~~~~~~~~~~~~
		ArrayList<EquationNode> branches = new ArrayList<EquationNode>();

		// Function cases
		ArrayList<NodeCase> cases = new ArrayList<NodeCase>();
		for (TrigFunctions func : TrigFunctions.values()) {
			cases.add(new NodeCase(TypeEquationXML.Trig, func.toString()));
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
			EquationNode node = branches.get(i);
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
		if (showsEmptyColumns) {
			for (Entry<String, Integer> tButtonEntry : tButtonMap.entrySet()) {
				table.setText(0, tButtonEntry.getValue(), tButtonEntry.getKey());
			}
		}

		if (!showsEmptyColumns) {
			return null;
		}
		return table;
	}

	private void fillRow(TransformationList<? extends TransformationButton> transformList,
			HashMap<String, Integer> tButtonMap, FlexTable table, EquationNode node) {

		if (!showsEmptyRows && transformList.isEmpty()) {
			return;
		}

		if (table == null) {
			table = new FlexTable();
			RootPanel.get().add(table);
		}

		int row = table.getRowCount() + 1;

		EquationTree tree = node.getTree();
		HTML testCase = new HTML();
		tree.reloadDisplay(true, true);
		testCase.setHTML(tree.getLeftDisplay());
		table.setWidget(row, 0, testCase);

//		for (TransformationButton tButton : transformList) {
//			String tButtonName = tButton.getClass().getName()
//					.replace(buttonPrefix, "");
//			int column = tButtonMap.get(tButtonName);
//
//			HTML testCaseTransformation = new HTML();
//			tree.reloadDisplay(true);
//			testCaseTransformation.setHTML(tree.getLeftDisplay());
//			table.setWidget(row, 0, testCaseTransformation);
//
//			MathTree previewEq = tButton.getPreview();
//			HTML preview = null;
//			if (previewEq != null) {
//				preview = new HTML(previewEq.getRightDisplay());
//			} else {
//				preview = new HTML(tButton.getHTML());
//			}
//			
//			if (showsEmptyColumns) {
//				table.setWidget(row, column, preview);
//			} else {
//				int cellCount = table.getCellCount(row);
//				table.setText(row, cellCount, tButtonName);
//				table.getCellFormatter().getElement(row, cellCount).getStyle()
//						.setBackgroundColor("yellow");
//				table.setWidget(row, cellCount + 1, preview);
//				table.getCellFormatter().getElement(row, cellCount + 1)
//						.getStyle().setBackgroundColor("orange");
//
//			}
//		}
//		if (!showsEmptyColumns) {
//			TransformationButton oneOfTheButtons = transformList.get(0);
//			oneOfTheButtons.fireEvent(new ClickEvent() {
//			});
//			EquationHTML actualTree = oneOfTheButtons.getTransformList()
//					.getNode().getTree().getDisplayClone();
//			table.setWidget(row + 1, 1, actualTree);
//
//			table.getRowFormatter().getElement(row).getStyle()
//					.setBackgroundColor("lightBlue");
//		}
	}

	ArrayList<EquationNode> makeCases(ArrayList<NodeCase> caseList,
			EquationNode toReplace, boolean isIncomplete, boolean containsAllTypes) {
		EquationTree mTree = toReplace.getTree();
		String toReplaceId = toReplace.getId();
		ArrayList<EquationNode> branches = new ArrayList<EquationNode>();

		for (NodeCase branchSpec : caseList) {
			EquationTree mTreeBranch = mTree.clone();
			EquationNode toReplaceBranch = mTreeBranch.getNodeById(toReplaceId);
			EquationNode branch = toReplaceBranch.replace(branchSpec.type,
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
	private ArrayList<EquationNode> getArgumentBranches(EquationNode parent,
			boolean isIncomplete, boolean containsAllTypes) {

		ArrayList<NodeCase> branchList = new ArrayList<NodeCase>();

		switch (parent.getType()) {
		case Sum:
			branchList.addAll(branchAllBut(TypeEquationXML.Sum));
			break;
		case Term:
			branchList.addAll(branchAllBut(TypeEquationXML.Term));
			break;
		case Fraction:
		case Exponential:
		case Trig:
		case Log:
			branchList.addAll(branchAllBut(null));
			break;
		}

		EquationNode toReplace = parent.append(TypeEquationXML.Variable, "case");
		return makeCases(branchList, toReplace, isIncomplete, containsAllTypes);
	}

	private ArrayList<EquationNode> getBinaryArgumentBranches(EquationNode toReplace,
			Operator operator, boolean containsAllTypes) {

		if (operator == null) {
			toReplace = toReplace.replace(TypeEquationXML.Exponential, "");
		} else if (operator.equals(Operator.PLUS)
				|| operator.equals(Operator.MINUS)) {
			toReplace = toReplace.replace(TypeEquationXML.Sum, "");
		} else if (operator.equals(Operator.getMultiply())) {
			toReplace = toReplace.replace(TypeEquationXML.Term, "");
		}

		ArrayList<EquationNode> firstBranches = getArgumentBranches(toReplace,
				true, containsAllTypes);

		ArrayList<EquationNode> binaryBranches = new ArrayList<EquationNode>();
		for (EquationNode branch : firstBranches) {
			EquationNode branchParent = branch.getParent();
			if (operator != null) {
				branchParent.append(TypeEquationXML.Operation, operator.getSign());
			}
			ArrayList<EquationNode> rightBranches = getArgumentBranches(
					branchParent, true, containsAllTypes);
			for (EquationNode rightBranch : rightBranches) {
				EquationNode parent = rightBranch.getParent();
//				parent.addBefore(0, TypeML.Operation, operator.getSign());
//				parent.addBefore(0, TypeML.Variable, "[");
//				parent.append(TypeML.Operation, operator.getSign());
//				parent.append(TypeML.Variable, "]");
			}
			binaryBranches.addAll(rightBranches);

		}
		return binaryBranches;
	}

	ArrayList<EquationNode> getSpecialBinaryCases(EquationNode toReplace,
			ArrayList<NodeCase> specialCases, Operator operator) {

		if (operator == null) {
			toReplace = toReplace.replace(TypeEquationXML.Exponential, "");
		} else if (operator.equals(Operator.PLUS)
				|| operator.equals(Operator.MINUS)) {
			toReplace = toReplace.replace(TypeEquationXML.Sum, "");
		} else if (operator.equals(Operator.getMultiply())) {
			toReplace = toReplace.replace(TypeEquationXML.Term, "");
		}

		EquationNode leftDummy = toReplace.append(TypeEquationXML.Variable, "toReplace");

		ArrayList<EquationNode> leftCases = makeCases(specialCases, leftDummy,
				false, false);
		ArrayList<EquationNode> binaryBranches = new ArrayList<EquationNode>();
		for (EquationNode leftCase : leftCases) {
			EquationNode caseParent = leftCase.getParent();
			if (operator != null) {
				caseParent.append(TypeEquationXML.Operation, operator.getSign());
			}
			EquationNode rightDummy = caseParent.append(TypeEquationXML.Variable,
					"toReplace");
			ArrayList<EquationNode> rightBranches = makeCases(specialCases,
					rightDummy, false, false);
			for (EquationNode rightBranch : rightBranches) {
				EquationNode parent = rightBranch.getParent();
//				parent.addBefore(0, TypeML.Operation, operator.getSign());
//				parent.addBefore(0, TypeML.Variable, "[");
//				parent.append(TypeML.Operation, operator.getSign());
//				parent.append(TypeML.Variable, "]");
			}
			binaryBranches.addAll(rightBranches);
		}
		return binaryBranches;
	}

	/**
	 * Add terminal nodes to the branch to complete it
	 * 
	 * @param branch
	 */
	void completeBranch(EquationNode branch, boolean containsAllTypes) {
		switch (branch.getType()) {
		case Number:
			branch.setSymbol(Math.random()*10 +"");
			break;
		case Variable:
			branch.setSymbol("a");
			break;
		case Sum:
			if (containsAllTypes) {
				// ArrayList<NodeCase> nodeCases =
				// branchAllBut(branch.getType());
				ArrayList<NodeCase> nodeCases = new ArrayList<NodeCase>();
				nodeCases.add(new NodeCase(TypeEquationXML.Exponential, ""));
				nodeCases.add(new NodeCase(TypeEquationXML.Fraction, ""));
				nodeCases.add(new NodeCase(TypeEquationXML.Term, ""));

				for (int i = 0; i < nodeCases.size(); i++) {
					NodeCase nCase = nodeCases.get(i);
					if (i != 0) {
						branch.append(TypeEquationXML.Operation, Operator.PLUS.getSign());
					}
					completeBranch(branch.append(nCase.type, nCase.symbol),
							false);
				}
			} else {
//				branch.append(TypeML.Variable, "{");
//				branch.append(TypeML.Operation, Operator.PLUS.getSign());
				branch.append(TypeEquationXML.Variable, "a");
				branch.append(TypeEquationXML.Operation, Operator.PLUS.getSign());
				branch.append(TypeEquationXML.Variable, "a");
//				branch.append(TypeML.Operation, Operator.PLUS.getSign());
//				branch.append(TypeML.Variable, "}");
			}
			break;
		case Term:
			String multiply = Operator.getMultiply().getSign();
			if (containsAllTypes) {
				// ArrayList<NodeCase> nodeCases =
				// branchAllBut(branch.getType());
				ArrayList<NodeCase> nodeCases = new ArrayList<NodeCase>();
				nodeCases.add(new NodeCase(TypeEquationXML.Number, Math.random()*10 +""));
				nodeCases.add(new NodeCase(TypeEquationXML.Exponential, ""));
				nodeCases.add(new NodeCase(TypeEquationXML.Fraction, ""));
				nodeCases.add(new NodeCase(TypeEquationXML.Sum, ""));
				for (int i = 0; i < nodeCases.size(); i++) {
					NodeCase nCase = nodeCases.get(i);
					if (i != 0) {
						branch.append(TypeEquationXML.Operation, Operator.getMultiply()
								.getSign());
					}
					completeBranch(branch.append(nCase.type, nCase.symbol),
							false);
				}
			} else {
//				branch.append(TypeML.Variable, "{");
//				branch.append(TypeML.Operation, multiply);
//				branch.append(TypeML.Variable, "a");
				branch.append(TypeEquationXML.Number, Math.random()*10 +"");
				branch.append(TypeEquationXML.Operation, multiply);
				branch.append(TypeEquationXML.Variable, "a");
				branch.append(TypeEquationXML.Operation, multiply);
				branch.append(TypeEquationXML.Variable, "a");
//				branch.append(TypeML.Operation, multiply);
//				branch.append(TypeML.Variable, "}");
			}
			break;
		case Fraction:
			if (containsAllTypes) {
				completeBranch(branch.append(TypeEquationXML.Term, ""), true);
				completeBranch(branch.append(TypeEquationXML.Term, ""), true);
			} else {
				branch.append(TypeEquationXML.Variable, "a");
				branch.append(TypeEquationXML.Variable, "a");
			}
			break;
		case Exponential:
			// if (containsAllTypes) {
			// completeBranch(branch.append(TypeML.Term, ""), true);
			// branch.append(TypeML.Variable, "a");
			// } else {
			branch.append(TypeEquationXML.Variable, "a");
			branch.append(TypeEquationXML.Number, "2");
			// }
			break;
		case Trig:
			branch.append(TypeEquationXML.Variable, "a");
			if ("".equals(branch.getAttribute(MathAttribute.Function))) {
				branch.setAttribute(MathAttribute.Function,
						TrigFunctions.sin.toString());
			}
			break;
		case Log:
			branch.append(TypeEquationXML.Variable, "a");
			if ("".equals(branch.getAttribute(MathAttribute.LogBase))) {
				branch.setAttribute(MathAttribute.LogBase, "10");
			}
			break;
		}
	}

	ArrayList<NodeCase> branchAllBut(TypeEquationXML excludedType) {

		ArrayList<NodeCase> branchMap = new ArrayList<NodeCase>();

		branchMap.add(new NodeCase(TypeEquationXML.Number, "9"));
		branchMap.add(new NodeCase(TypeEquationXML.Variable, "x"));

		if (!TypeEquationXML.Sum.equals(excludedType))
			branchMap.add(new NodeCase(TypeEquationXML.Sum, ""));
		if (!TypeEquationXML.Term.equals(excludedType))
			branchMap.add(new NodeCase(TypeEquationXML.Term, ""));
		if (!TypeEquationXML.Fraction.equals(excludedType))
			branchMap.add(new NodeCase(TypeEquationXML.Fraction, ""));
		if (!TypeEquationXML.Exponential.equals(excludedType))
			branchMap.add(new NodeCase(TypeEquationXML.Exponential, ""));
		if (!TypeEquationXML.Trig.equals(excludedType))
			branchMap.add(new NodeCase(TypeEquationXML.Trig, ""));
		if (!TypeEquationXML.Log.equals(excludedType))
			branchMap.add(new NodeCase(TypeEquationXML.Log, ""));

		return branchMap;
	}

	class NodeCase {
		TypeEquationXML type;
		String symbol;

		public NodeCase(TypeEquationXML type, String symbol) {
			super();
			this.type = type;
			this.symbol = symbol;
		}

	}
}
