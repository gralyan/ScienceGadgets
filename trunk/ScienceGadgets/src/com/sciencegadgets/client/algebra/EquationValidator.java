package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.TrigFunctions;
import com.sciencegadgets.shared.UnitMap;

public class EquationValidator {
	
	private static final String ANGLE ="PlaneAngle";

	/**
	 * Validates the structure of the node, looks at:<br/>
	 * · proper number of children<br/>
	 * · numbers can be parsed<br/>
	 * · no sums within sums<br/>
	 * · no terms within terms<br/>
	 * · log must have base attribute<br/>
	 * · trig must have function attribute<br/>
	 * 
	 * @return True if well formed<br/>
	 *         False if invalid
	 */
	public boolean validateMathNode(MathNode node) {

		TypeML type = node.getType();
		boolean isInEditMode = node.getTree().isInEditMode();

		boolean valid = true;

		int childCount = node.getChildCount();
		boolean isWrongChildren = false;

		// Confirm the correct number of children
		switch (type.childRequirement()) {
		case EQUATION:
			if (childCount != 3)
				isWrongChildren = true;
			break;
		case TERMINAL:
			if (childCount != 1
					|| node.getMLNode().getChild(0).getNodeType() != Node.TEXT_NODE) {
				isWrongChildren = true;
			}
			break;
		case UNARY:
			if (childCount != 1)
				isWrongChildren = true;
			break;
		case BINARY:
			if (childCount != 2)
				isWrongChildren = true;
			break;
		case SEQUENCE:
			if (childCount < 3) {
				isWrongChildren = true;
			}
			break;
		}

		if (isWrongChildren) {
			String errorMerrage = "Wrong number of children, type: " + type
					+ " can't have (" + childCount + ") children: "
					+ toString();
			JSNICalls.error(errorMerrage);
			valid = false;
			// Damage control
			Window.alert("Error, see log");
			return false;
		}

		switch (type) {
		case Number:
			// Confirm that the symbol is a number in solve mode
			if (!isInEditMode) {
				try {
					Double.parseDouble(node.getSymbol());
				} catch (NumberFormatException e) {
					JSNICalls.warn("The number node " + toString()
							+ " must have a number");
					valid = false;
					// Damage control
					node.setSymbol("1");
				}
			}
			break;
		case Sum:
		case Term:// Confirm that there are < 3 children
			if (type.equals(node.getParentType())) {
				if (TypeML.Term.equals(type)) {
					JSNICalls.error("There shouldn't be a term in a term"
							+ node.getParent().toString());
					valid = false;
				} else if (TypeML.Sum.equals(type)) {
					JSNICalls.error("There shouldn't be a sum in a sum: "
							+ node.getParent().toString());
					valid = false;
				}
			}
			break;
		case Equation:
			node.getTree().checkSideForm();
			break;
		case Log:// Confirm the base is a number
			if (!isInEditMode) {
				try {
					Double.parseDouble(node.getAttribute(MathAttribute.LogBase));
				} catch (NumberFormatException e) {
					if (!"e".equals(node.getAttribute(MathAttribute.LogBase))) {
						JSNICalls.error("The base of a log must be a number: "
								+ node.getParent().toString());
						valid = false;
						// Damage control
						node.setAttribute(MathAttribute.LogBase, "10");
					}
				}
			}
			break;
		case Trig:// Confirm the function attribute exists
			if (!isInEditMode) {
				if ("".equals(node.getAttribute(MathAttribute.Function))) {
					JSNICalls
							.error("Trig functiond must have function attribute: "
									+ node.getParent().toString());
					valid = false;
					// Damage control
					node.setAttribute(MathAttribute.Function,
							TrigFunctions.sin.toString());
				}
			}
			break;
		}
		return valid;
	}

	public boolean validateQuantityKinds(MathTree mathTree) {
		try {
			getQuantityKind(mathTree.getRoot());
			return true;
		} catch (IllegalStateException e) {
			JSNICalls.error(e.getMessage());
			Window.alert(e.getCause().getMessage());
			return false;
		}
	}

	private UnitMap getQuantityKind(MathNode node)
			throws IllegalStateException {

		switch (node.getType()) {
		case Number:
		case Variable:
			return new UnitMap(node, true);
		case Term:
			UnitMap termMap = new UnitMap();
			for (MathNode child : node.getChildren()) {
				if (TypeML.Operation.equals(child.getType())) {
					continue;
				}
				UnitMap childMap = getQuantityKind(child);
				termMap = termMap.getMultiple(childMap);
			}
			return termMap;
		case Equation:
		case Sum:
			UnitMap sumMap = null;
			for (MathNode child : node.getChildren()) {
				if (TypeML.Operation.equals(child.getType())) {
					continue;
				}
				UnitMap childMap = getQuantityKind(child);
				if (sumMap == null) {
					sumMap = childMap;
				} else if (!sumMap.equals(childMap)) {
					throw new IllegalStateException(
							"All sums and equations must contain equivalent derived quantity kinds for all of it's children: \n"
									+ "first: "
									+ sumMap
									+ "\ndiffernent: "
									+ childMap
									+ "\nof node: "
									+ child,
							new Throwable(
									"Units must be similar in sides of the equation and in sums"));
				}
			}
			return sumMap;
		case Exponential:
			MathNode base = node.getChildAt(0);
			MathNode exp = node.getChildAt(1);
			UnitMap baseMap = getQuantityKind(base);
			UnitMap expMap = getQuantityKind(exp);

			if (expMap.size() != 0) {
				throw new IllegalStateException(
						"Exponents can't have units: \n" + "attribute: "
								+ expMap + "\nof node: " + exp, new Throwable(
								"Exponents can't have units"));
			}

			if ((baseMap.size() == 0)) {
				return baseMap;
			}

			if (TypeML.Number.equals(exp.getType())) {
				try {
					Integer expValue = Integer.parseInt(exp
							.getAttribute(MathAttribute.Value));
					baseMap = baseMap.getExponential(expValue);
					return baseMap;
				} catch (NumberFormatException e) {
					throw new IllegalStateException(
							"The base of a non-integer exponential has units: \n"
									+ baseMap + "\nof node: " + node,
							new Throwable(
									"Bases of exponentials can only have units if the exponent is a constant integer"));
				}
			} else {
				throw new IllegalStateException(
						"The base of a variable exponential has units: \n"
								+ baseMap + "\nof node: " + node,
						new Throwable(
								"Bases of exponentials can only have units if the exponent is a constant integer"));
			}
			// unreachable
		case Fraction:
			MathNode numerator = node.getChildAt(0);
			MathNode denominator = node.getChildAt(1);
			UnitMap divisionMap = getQuantityKind(numerator).getDivision(getQuantityKind(denominator));

			return divisionMap;
		case Log:
			UnitMap logArgumentMap = getQuantityKind(node
					.getChildAt(0));
			if (logArgumentMap.size() == 0) {
				return logArgumentMap;
			} else {
				throw new IllegalStateException(
						"The child of a log has units: \n" + logArgumentMap
								+ "\nof node: " + node, new Throwable(
								"The argument of a logarithm can't have units"));

			}
			// unreachable
		case Trig:
			UnitMap trigArgumentMap = getQuantityKind(node
					.getChildAt(0));
			UnitMap comparison = new UnitMap();
			comparison.put(ANGLE , 1);
			if(comparison.equals(trigArgumentMap)) {
				break;
			}else {
				throw new IllegalStateException(
						"The child of a trig function doesn't have units of "+ANGLE+": \n" + trigArgumentMap
								+ "\nof node: " + node, new Throwable(
								"The argument of a trigonometric function must have the unit type of "+ANGLE));
			}
		}
		return new UnitMap();
	}
}
