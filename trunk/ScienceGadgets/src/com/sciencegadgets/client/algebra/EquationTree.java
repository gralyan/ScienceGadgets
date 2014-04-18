/*   Copyright 2012 John Gralyan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sciencegadgets.client.algebra;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.algebra.edit.EditWrapper;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.client.algebra.transformations.AlgebraicTransformations;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.TypeSGET.ChildRequirement;
import com.sciencegadgets.shared.TypeSGET.Operator;
import com.sciencegadgets.shared.dimensions.CommonConstants;
import com.sciencegadgets.shared.dimensions.UnitAttribute;
import com.sciencegadgets.shared.dimensions.UnitHTML;
import com.sciencegadgets.shared.dimensions.UnitMap;

public class EquationTree {

	private EquationNode root;
	private LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();
	private HashMap<String, EquationNode> idMap = new HashMap<String, EquationNode>();
	private HashMap<String, Element> idMLMap = new HashMap<String, Element>();
	private HashMap<String, Element> idHTMLMap = new HashMap<String, Element>();
	private HashMap<Element, String> idUnitHTMLMap = new HashMap<Element, String>();
	private Element equationXML;
	private EquationHTML eqHTML;
	private boolean inEditMode;
	private int idCounter = 0;
	private EquationValidator eqValidator;

	/**
	 * A tree representation of an equation.
	 * 
	 * @param equationXML
	 *            - The equation element in XML
	 * @param inEditMode
	 *            - true if intended for edit mode, false if for solving mode
	 * @throws TopNodesNotFoundException
	 */
	public EquationTree(Element equationXML, boolean inEditMode) {
		this.equationXML = equationXML;
		this.inEditMode = inEditMode;

		bindXMLtoNodes(equationXML);

		EquationRandomizer.randomizeNumbers(this, !inEditMode);

		// reloadDisplay(true);
	}

	public EquationTree(TypeSGET leftType, String leftSymbol,
			TypeSGET rightType, String righSymbol, boolean inEditMode) {
		this(newDummyElement(), inEditMode);
		getLeftSide().replace(leftType, leftSymbol);
		getRightSide().replace(rightType, righSymbol);
	}

	public EquationTree(boolean inEditMode) {
		this(newDummyElement(), inEditMode);
	}

	private static Element newDummyElement() {
		Element eq = DOM.createElement(TypeSGET.Operation.getTag());
		eq.setInnerText("=");
		eq.setAttribute("id", "dummyNodeEquals");

		Element dummyLeft = DOM.createElement(TypeSGET.Variable.getTag());
		dummyLeft.setInnerText("a");
		dummyLeft.setAttribute("id", "dummyNodeLeft");

		Element dummyRight = DOM.createElement(TypeSGET.Variable.getTag());
		dummyRight.setInnerText("a");
		dummyRight.setAttribute("id", "dummyNodeRight");

		Element root = DOM.createElement(TypeSGET.Equation.getTag());
		root.appendChild(dummyLeft);
		root.appendChild(eq);
		root.appendChild(dummyRight);
		return root;
	}

	public EquationTree clone() {
		return new EquationTree(getEquationXMLClone(), isInEditMode());
	}

	public boolean isInEditMode() {
		return inEditMode;
	}

	public Element getEquationXMLClone() {
		return (Element) equationXML.cloneNode(true);
	}

	public String getEquationXMLString() {
		String equationString = JSNICalls.elementToString(equationXML);
		equationString = equationString.replace(
				" xmlns=\"http://www.w3.org/1998/Math/MathML\"", "").replace(
				" xmlns=\"http://www.w3.org/1999/xhtml\"", "");
		return equationString;
	}

	public EquationNode getRoot() {
		return root;
	}

	public EquationNode getLeftSide() {
		checkSideForm();
		return root.getChildAt(0);
	}

	public EquationNode getRightSide() {
		checkSideForm();
		return root.getChildAt(2);
	}

	public EquationNode getEquals() {
		checkSideForm();
		return root.getChildAt(1);
	}

	void checkSideForm() {
		if (root.getChildCount() != 3) {
			String errorMessage = "There must be 3 parts to an equation, not side=side: "
					+ getEquationXMLClone().getString();
			throw new IllegalStateException(errorMessage, new Throwable(
					errorMessage));
		}
		if (!"=".equals(root.getChildAt(1).getSymbol())) {
			String errorMessage = "[=] isn't the root's second child, not side=side "
					+ getEquationXMLClone().getString();
			throw new IllegalStateException(errorMessage, new Throwable(
					errorMessage));
		}
	}

	public String getLeftDisplay() {
		return JSNICalls.elementToString(eqHTML.getLeft());
	}

	public String getRightDisplay() {
		return JSNICalls.elementToString(eqHTML.getRight());
	}

	public EquationHTML getDisplayClone() {
		return new EquationHTML(this);
	}

	public EquationHTML getDisplay() {
		return eqHTML;
	}

	public void setDisplay(EquationHTML equationHTML) {
		for (Wrapper w : wrappers) {
			if (w instanceof EditWrapper) {
				((EditWrapper) w).onUnload();

			} else if (w instanceof AlgebaWrapper) {
				((AlgebaWrapper) w).onUnload();
			}
			w.getElement().removeFromParent();
		}
		wrappers.clear();

		idHTMLMap.clear();

		this.eqHTML = equationHTML;

		NodeList<Element> allElements = eqHTML.getElement()
				.getElementsByTagName("*");

		for (int i = 0; i < allElements.getLength(); i++) {
			Element el = (Element) allElements.getItem(i);
			String elId = el.getAttribute("id");
			if (elId.contains(UnitHTML.UNIT_NODE_DELIMITER)) {
				String parentElId = elId.split(UnitHTML.UNIT_NODE_DELIMITER)[1];
				idUnitHTMLMap.put(el, parentElId);
			} else {
				idHTMLMap.put(elId, el);
			}
			el.removeAttribute("id");
		}
	}

	public EquationHTML reloadDisplay(boolean hasSmallUnits,
			boolean hasSubscripts) {

		EquationHTML equationHTML = new EquationHTML(this, hasSmallUnits,
				hasSubscripts);
		equationHTML.pilot = true;
		setDisplay(equationHTML);
		return equationHTML;
	}

	public LinkedList<Wrapper> getWrappers() {
		return wrappers;
	}

	public EquationNode getNodeById(String id) throws NoSuchElementException {

		EquationNode node = idMap.get(id);
		if (node == null) {
			JSNICalls.error("Can't get node by id: " + id + "\n"
					+ getEquationXMLClone().getString());
			throw new NoSuchElementException("Can't get node by id: " + id);
		}
		return node;
	}

	public EquationNode newNode(Element xmlNode) {
		EquationNode newNode = new EquationNode(xmlNode);
		AddToMaps(newNode);

		NodeList<Element> descendants = xmlNode.getElementsByTagName("*");
		for (int i = 0; i < descendants.getLength(); i++) {
			Element descendantEl = descendants.getItem(i);
			AddToMaps(new EquationNode(descendantEl));
		}

		return newNode;
	}

	public EquationNode newNode(TypeSGET type, String symbol) {
		EquationNode newNode = new EquationNode(type, symbol);
		AddToMaps(newNode);
		return newNode;
	}

	public void validateTree() throws IllegalStateException {

		if (eqValidator == null) {
			eqValidator = new EquationValidator();
		}

		for (EquationNode node : idMap.values()) {
			eqValidator.validateEquationNode(node);
		}

		if (!inEditMode) {
			eqValidator.validateQuantityKinds(this);
		}

		if (idMap.size() != idMLMap.size()) {
			String errorMessage = "The binding maps must have the same size: idMap.size()="
					+ idMap.size() + " idMLMap.size()=" + idMLMap.size();
			throw new IllegalStateException(errorMessage, new Throwable(
					errorMessage));
		}
	}

	private void AddToMaps(EquationNode node) {
		String id = node.getId();
		id = node.createId(id);

		node.xmlNode.setAttribute("id", id);
		idMap.put(id, node);
		idMLMap.put(id, node.getXMLNode());
	}

	/**
	 * Gets all nodes by specified type, use null for all nodes
	 * 
	 * @param type
	 */
	public LinkedList<EquationNode> getNodesByType(TypeSGET type) {
		return getNodesByType(type, root);
	}

	/**
	 * Gets all nodes in the parent by specified type, use null for all nodes
	 * 
	 * @param type
	 */
	public LinkedList<EquationNode> getNodesByType(TypeSGET type,
			EquationNode parent) {
		LinkedList<EquationNode> nodes = new LinkedList<EquationNode>();
		String tag = "*";
		if (type != null) {
			tag = type.getTag();
		}
		NodeList<Element> elements = parent.getXMLNode().getElementsByTagName(
				tag);
		for (int i = 0; i < elements.getLength(); i++) {
			String id = elements.getItem(i).getAttribute("id");
			nodes.add(idMap.get(id));
		}
		return nodes;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Node Class
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public class EquationNode {
		private Element xmlNode;
		private Wrapper wrapper;

		/**
		 * Wrap existing equation XML node
		 */
		public EquationNode(Element xmlNode) {

			String id = xmlNode.getAttribute("id");
			id = createId(id);
			xmlNode.setAttribute("id", id);

			this.xmlNode = xmlNode;
		}

		/**
		 * Creates a new equation XML DOM node which should be added into the
		 * EquationNode </br>get the DOM node with:
		 * <p>
		 * getMlNode()
		 * </p>
		 * 
		 * @param type
		 *            - The {@link TypeSGET}
		 * @param symbol
		 *            - inner text
		 */
		public EquationNode(TypeSGET type, String symbol) {

			String tag = type.getTag();

			Element newNode = DOM.createElement(tag);
			newNode.setAttribute("id", createId(""));

			this.xmlNode = newNode;

			setSymbol(symbol);

		}

		/**
		 * Returns a copy of this node
		 */
		public EquationNode clone() {
			Element newEl = (Element) xmlNode.cloneNode(true);
			newEl.removeAttribute("id");
			EquationNode top = new EquationNode(newEl);
			AddToMaps(top);

			NodeList<Element> descendants = newEl.getElementsByTagName("*");
			for (int i = 0; i < descendants.getLength(); i++) {
				Element descendantEl = descendants.getItem(i);
				descendantEl.removeAttribute("id");
				AddToMaps(new EquationNode(descendantEl));
			}

			return top;
		}

		private String createId(String prevId) {
			if (prevId != null && !"".equals(prevId)) {
				if (!idMap.containsKey(prevId)
						|| this.equals(idMap.get(prevId))) {
					return prevId;
				}
			}
			String id = "ML" + idCounter++;
			while (idMap.containsKey(id)) {
				id = "ML" + idCounter++;
			}
			return id;
		}

		/**
		 * Adds a node between this node and its parent, encasing this branch of
		 * the tree in a new node. <br/>
		 * 
		 * @param type
		 *            - the type of the new node
		 * @return - encasing node
		 */
		public EquationNode encase(TypeSGET type) {
			// Don't encase sum in sum or term in term
			boolean sumOrTerm = TypeSGET.Sum.equals(type)
					|| TypeSGET.Term.equals(type);
			if (getType().equals(type) && sumOrTerm) {
				return this;
			} else if (getParentType().equals(type) && sumOrTerm) {
				return getParent();
			} else {
				EquationNode encasing = new EquationNode(type, "");
				this.getParent().addBefore(this.getIndex(), encasing);
				encasing.append(this);

				return encasing;
			}
		}

		/**
		 * <b>This method must always be called after removing children from a
		 * sum or term</b> <br/>
		 * Moves child into parent and removes this node if there is only one
		 * child. <br/>
		 * If the child and parent are the same type, the grandchildren are
		 * moved and both this and the child are removed. <br/>
		 */
		public void decase() {

			// This method is only useful for terms and sums
			switch (getType()) {
			case Term:
			case Sum:
				break;
			default:
				return;
			}

			// Propagate leading minus sign or remove leading plus
			if (getChildCount() != 0) {
				EquationNode possibleMinus = getChildAt(0);
				if (TypeSGET.Operation.equals(possibleMinus.getType())) {
					if (Operator.MINUS.getSign().equals(
							possibleMinus.getSymbol())) {
						if (getChildCount() > 1) {
							AlgebraicTransformations
									.propagateNegative(getChildAt(1));
							possibleMinus.remove();
						} else {
							possibleMinus.remove();
							EquationNode parent = this.getParent();
							this.remove();
							parent.decase();
							JSNICalls.error("Operation with no siblings: "
									+ toString());
							return;
						}
					} else {
						possibleMinus.remove();
						decase();
						return;
					}
				}
			}

			switch (this.getChildCount()) {
			case 0:
				this.replace(TypeSGET.Number, "0");
				break;
			case 1:
				getParent().addBefore(this.getIndex(), this.getFirstChild());
				this.remove();
				break;
			case 2:// Should only be sums with a negative in front
				JSNICalls
						.error("There should not be two children in a Sum or Term: "
								+ toString());
				break;
			}
		}

		public EquationNode replace(EquationNode replacement) {
			this.getParent().addBefore(this.getIndex(), replacement);
			this.remove();
			return replacement;
		}

		public EquationNode replace(TypeSGET type, String symbol) {
			EquationNode replacement = new EquationNode(type, symbol);
			replace(replacement);
			return replacement;
		}

		/**
		 * Adds a child at the specified index.</br></br>If the node is already
		 * in the tree, it is just repositioned. There is no need to remove it
		 * first</br></br> Children of this node must reflect it's type</br>
		 * <em>Requirements:</em></br> <b>Variable and Number</b> must have
		 * <b>exactly 0</b> children</br> <b>Term and Sum</b> must have <b>at
		 * least 2</b> children</br> <b>Fraction and Exponential</b> must have
		 * <b>exactly 2</b> children</br></br> The order should be:</br> <b>Term
		 * and Sum</b> - same as the order seen</br> <b>Fraction</b> - numerator
		 * then denominator</br> <b>Exponent</b> - base then exponent
		 * 
		 * @param index
		 *            - the placement of siblings
		 * @param after
		 *            - True if the node is to be added after the node at the
		 *            specified index (to next index).<br/>
		 *            False if the node is to be added before the node at the
		 *            specified index (to this index)
		 * @param node
		 *            - the node to be added
		 */
		private void add(int index, EquationNode node, boolean after)
				throws IllegalArgumentException {

			boolean indexOutOfRange = index < 0 || index >= getChildCount();

			// Don't add sum to sum or term to term, just add it's children
			if (getType().equals(node.getType())
					&& (TypeSGET.Sum.equals(node.getType()) || TypeSGET.Term
							.equals(node.getType()))) {
				LinkedList<EquationNode> children = node.getChildren();
				if (indexOutOfRange) {
					for (int i = 0; i < children.size(); i++) {
						append(children.get(i));
					}
				} else {
					for (int i = children.size(); i > 0; i--) {
						addBefore(index, children.get(i - 1));
					}
				}

				node.remove();

			} else {
				Element elementNode = node.getXMLNode();

				// Add node to DOM tree
				if (indexOutOfRange) {
					xmlNode.appendChild(elementNode);
				} else if (after) {
					Node referenceChild = xmlNode.getChild(index);
					xmlNode.insertAfter(elementNode, referenceChild);
				} else {
					Node referenceChild = xmlNode.getChild(index);
					xmlNode.insertBefore(elementNode, referenceChild);
				}

				AddToMaps(node);
			}
		}

		public void addAfter(int index, EquationNode node) {
			add(index, node, true);
		}

		public EquationNode addAfter(int index, TypeSGET type, String symbol) {
			EquationNode newNode = new EquationNode(type, symbol);
			this.addAfter(index, newNode);
			return newNode;
		}

		public void addBefore(int index, EquationNode node) {
			add(index, node, false);
		}

		public EquationNode addBefore(int index, TypeSGET type, String symbol) {
			EquationNode newNode = new EquationNode(type, symbol);
			this.addBefore(index, newNode);
			return newNode;
		}

		public EquationNode append(EquationNode newNode) {
			addBefore(-1, newNode);
			return newNode;
		}

		public EquationNode append(TypeSGET type, String symbol) {
			return addBefore(-1, type, symbol);
		}

		public EquationNode addFirst(EquationNode newNode) {
			addBefore(0, newNode);
			return newNode;
		}

		public EquationNode addFirst(TypeSGET type, String symbol) {
			return addBefore(0, type, symbol);
		}

		public LinkedList<EquationNode> getNodesByType(TypeSGET type) {
			return EquationTree.this.getNodesByType(type, this);
		}

		public LinkedList<EquationNode> getChildren() {
			NodeList<Node> childrenNodesList = getXMLNode().getChildNodes();
			LinkedList<EquationNode> childrenNodes = new LinkedList<EquationNode>();

			for (int i = 0; i < childrenNodesList.getLength(); i++) {
				Node curNode = childrenNodesList.getItem(i);

				if (curNode.getNodeType() == Node.ELEMENT_NODE) {
					Element childElement = ((Element) curNode);
					String childId = childElement.getAttribute("id");
					childrenNodes.add(getNodeById(childId));
				}
			}
			return childrenNodes;
		}

		public EquationNode getChildAt(int index) {

			if (index < 0
					|| index > getChildCount() - 1
					|| ChildRequirement.TERMINAL.equals(getType()
							.childRequirement())) {
				return null;
			}

			Node node = getXMLNode().getChildNodes().getItem(index);
			if (node == null) {
				String response = "No children at position: " + index + " in: "
						+ toString();
				throw new IllegalArgumentException(response);
			}
			String id = ((Element) node).getAttribute("id");
			return getNodeById(id);
		}

		public EquationNode getFirstChild() {
			return getChildAt(0);
		}

		public int getChildCount() {
			// Terminal nodes have text nodes in XML
			if (ChildRequirement.TERMINAL.equals(getType().childRequirement())) {
				return 0;
			}
			return xmlNode.getChildCount();
		}

		/**
		 * @return <b>Next sibling</b> or <b>null</b> if none exists
		 */
		public EquationNode getNextSibling() {
			return getSibling(1);
		}

		/**
		 * @return <b>Previous sibling</b> or <b>null</b> if none exists
		 */
		public EquationNode getPrevSibling() {
			return getSibling(-1);
		}

		/**
		 * This method gets the sibling at a position relative the this node
		 * 
		 * @param indexesAway
		 *            - the number of indexes away from this sibling positive
		 *            for siblings to the right, negative for siblings to the
		 *            left <br/>
		 *            ex:<br/>
		 *            -1 for previous </br>1 for next
		 */
		private EquationNode getSibling(int indexesAway) {
			EquationNode parent = this.getParent();
			int siblingIndex = getIndex() + indexesAway;

			try {
				EquationNode sibling = parent.getChildAt(siblingIndex);
				return sibling;
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

		public void remove() {
			removeChildren();

			String id = getId();
			idMap.remove(id);
			idMLMap.remove(id);
			xmlNode.removeFromParent();

			if (wrapper instanceof EditWrapper) {
				((EditWrapper) wrapper).onUnload();

			} else if (wrapper instanceof AlgebaWrapper) {
				((AlgebaWrapper) wrapper).onUnload();
			}
			wrappers.remove(wrapper);
		}

		private void removeChildren() {
			LinkedList<EquationNode> children = getChildren();

			for (EquationNode child : children) {
				String id = child.getId();
				idMap.remove(id);
				idMLMap.remove(id);
				child.removeChildren();
			}
		}

		public int getIndex() {
			return this.getParent().getChildren().indexOf(this);
		}

		public EquationNode getParent() {
			if (TypeSGET.Equation.getTag().equalsIgnoreCase(getTag())) {
				throw new NoSuchElementException(
						"Can't get the parent of an equation tag because it's the root:\n"
								+ toString());
			}
			Element parentElement = getXMLNode().getParentElement();
			String parentId = parentElement.getAttribute("id");
			EquationNode parentNode = getNodeById(parentId);
			return parentNode;
		}

		public Element getXMLNode() {
			return xmlNode;
		}

		public Element getXMLClone() {
			return (Element) xmlNode.cloneNode(true);
		}

		public String toString() {
			if (xmlNode.getString() != null) {
				return xmlNode.getString();
			} else {
				return JSNICalls.elementToString(xmlNode);
			}
		}

		/**
		 * Depending on the node type:<br/>
		 * <b>Variable and Operation</b> - Inserts the symbol into the node.<br/>
		 * <b>Number</b> - The symbol will be saved as the node's value
		 * attribute and the displaying symbol may be shortened for formatting
		 * purposes<br/>
		 * <b>Trig</b> - Symbol is the function stored as an attribute
		 * <b>Log</b> -Symbol is the log base stored as an attribute
		 * 
		 * @param symbol
		 */
		public void setSymbol(String symbol) {

			switch (getType()) {
			case Number:
				if (ChangeNodeMenu.NOT_SET.equals(symbol)
						|| RandomSpecPanel.RANDOM_SYMBOL.equals(symbol)) {
					xmlNode.setInnerText(symbol);
					setAttribute(MathAttribute.Value, null);
				} else {
					BigDecimal value = new BigDecimal(symbol);

					// Rounded display value stored as inner text
					String displayValue;
					if (value.compareTo(new BigDecimal("1000")) < 0
							&& value.remainder(new BigDecimal(".01"))
									.compareTo(new BigDecimal(0)) == 0) {
						displayValue = value.stripTrailingZeros()
								.toPlainString();
					} else {
						displayValue = "#";
					}
					xmlNode.setInnerText(displayValue);

					// Full value stored as attribute
					String fullValue = value.stripTrailingZeros().toString();
					setAttribute(MathAttribute.Value, fullValue);
					break;
				}
			case Variable:
			case Operation:
				xmlNode.setInnerText(symbol);
				break;
			case Trig:
				setAttribute(MathAttribute.Function, symbol);
				break;
			case Log:
				setAttribute(MathAttribute.LogBase, symbol);
				break;
			}
		}

		public void setConstant(CommonConstants symbol) {
			// Display
			xmlNode.setInnerText(symbol.getSymbol());
			// Value
			setAttribute(MathAttribute.Value, symbol.getValue());
			// Unit
			setAttribute(MathAttribute.Unit, symbol.getUnitMap()
					.getUnitAttribute().toString());
		}

		public String getSymbol() {

			switch (getType()) {
			case Number:
				JSNICalls.TIME_ELAPSED("getSymbol1");
				String valueAttr = getAttribute(MathAttribute.Value);
				JSNICalls.TIME_ELAPSED("getSymbol2");
				try {
					return new BigDecimal(valueAttr).toString();
				} catch (NumberFormatException e) {
				}
				JSNICalls.TIME_ELAPSED("getSymbol3");
				// if (innerText.matches(".*\\d.*")) {
				// try {
				// return new BigDecimal(valueAttr).toString();
				// } catch (NumberFormatException e) {
				// }
				// JSNICalls.TIME_ELAPSED("getSymbol3");
				// } else {
				// try {
				// return CommonConstants.valueOf(valueAttr).getValue();
				// } catch (IllegalArgumentException e) {
				// }
				// JSNICalls.TIME_ELAPSED("getSymbol4");
				// }
				JSNICalls.TIME_ELAPSED("getSymbol5");
			case Variable:
			case Operation:
				return xmlNode.getInnerText();
			case Trig:
				return getAttribute(MathAttribute.Function);
			case Log:
				return getAttribute(MathAttribute.LogBase);
			}
			return "";
		}

		public Wrapper wrap(Wrapper wrap) {
			wrapper = wrap;
			wrappers.add(wrapper);
			return wrapper;
		}

		public Wrapper getWrapper() {
			return wrapper;
		}

		/**
		 * @return Tag of XML DOM node in <b>Lower Case</b>
		 */
		public String getTag() {
			return xmlNode.getTagName().toLowerCase();
		}

		public EquationTree getTree() {
			return EquationTree.this;
		}

		public String getId() {
			return getXMLNode().getAttribute("id");
		}

		public UnitAttribute getUnitAttribute() {
			return new UnitAttribute(xmlNode.getAttribute(MathAttribute.Unit
					.getAttributeName()));
		}

		public String getAttribute(MathAttribute attribute) {
			return xmlNode.getAttribute(attribute.getAttributeName());
		}

		public UnitMap getUnitMap() {
			return new UnitMap(this);
		}

		public void setAttribute(MathAttribute attribute, String value) {
			if (value == null || "".equals(value)) {
				xmlNode.removeAttribute(attribute.getAttributeName());
			} else {
				xmlNode.setAttribute(attribute.getAttributeName(), value);
			}
		}

		public boolean isLeftSide() {
			if (this.equals(root.getChildAt(0)))
				return true;
			else
				return false;
		}

		public boolean isRightSide() {
			if (this.equals(root.getChildAt(2)))
				return true;
			else
				return false;
		}

		public TypeSGET.Operator getOperation() {
			if (TypeSGET.Operation.equals(getType())) {
				String symbol = getSymbol();

				for (TypeSGET.Operator op : TypeSGET.Operator.values()) {
					if (op.getSign().equalsIgnoreCase(symbol)
							|| op.getHTML().equalsIgnoreCase(symbol)) {
						return op;
					}
				}
			}
			return null;
		}

		public boolean hasChildElements() {
			switch (getType().childRequirement()) {
			case TERMINAL:
				return false;
			case EQUATION:
			case SEQUENCE:
			case BINARY:
			case UNARY:
				if (getChildCount() > 0) {
					return true;
				}
			}
			return false;
		}

		public TypeSGET getType() {
			return TypeSGET.getType(getTag());
		}

		public TypeSGET getParentType() {
			Element parentEl = getXMLNode().getParentElement();
			if (parentEl == null) {
				return null;
			}
			String parentTag = parentEl.getTagName();
			return TypeSGET.getType(parentTag);
		}

		public Element getHTMLClone(boolean hasSmallUnits, boolean hasSubscripts) {
			Element html = (Element) getHTML(hasSmallUnits, hasSubscripts)
					.cloneNode(true);

			html.removeAttribute("class");
			html.getStyle().setDisplay(Display.INLINE_BLOCK);
			return html;
		}

		public String getHTMLString(boolean hasSmallUnits, boolean hasSubscripts) {
			return getHTMLClone(hasSmallUnits, hasSubscripts).getString();
		}

		/**
		 * This should only be done after
		 * {@link EquationTree#reloadDisplay(boolean)}
		 * 
		 * @return The current HTML element associated with this node
		 */
		public Element getHTML(boolean hasSmallUnits, boolean hasSubscripts) {
			Element el = idHTMLMap.get(getId());
			if (el == null) {
				EquationTree.this.reloadDisplay(hasSmallUnits, hasSubscripts);
				Element el2 = idHTMLMap.get(getId());
				return (Element) el2;
			}
			return (Element) el;
		}

		public Element[] getHTMLofUnits() {
			LinkedList<Element> units = new LinkedList<Element>();
			for (Entry<Element, String> entry : idUnitHTMLMap.entrySet()) {
				if (getId().equals(entry.getValue())) {
					units.add(entry.getKey());
				}
			}
			return units.toArray(new Element[units.size()]);
		}

		public void highlight() {
			getHTML(true, true).addClassName(CSS.HIGHLIGHT);
		}

		public void lineThrough() {
			getHTML(true, true).addClassName(CSS.LINE_THROUGH);
		}

		public boolean isLike(EquationNode another) {

			if (!getType().equals(another.getType())) {
				return false;
			}

			JSNICalls.TIME_ELAPSED("like1 ");

			// breaks not needed, returns at each step
			switch (getType()) {
			case Term:
				// fall through
			case Sum:
				LinkedList<EquationNode> assignedOtherChildren = new LinkedList<EquationNode>();
				a: for (EquationNode child : getChildren()) {
					b: for (EquationNode otherChild : another.getChildren()) {
						if (assignedOtherChildren.contains(otherChild)) {
							continue b;
						}
						if (child.isLike(otherChild)) {
							assignedOtherChildren.add(otherChild);
							continue a;
						}
					}
				}
				if (assignedOtherChildren.size() == getChildCount()
						&& assignedOtherChildren.size() == another
								.getChildCount()) {
					return true;
				} else {
					return false;
				}
			case Exponential:
				// fall through
			case Fraction:
				if (getChildAt(0).isLike(another.getChildAt(0))
						&& getChildAt(1).isLike(another.getChildAt(1))) {
					return true;
				} else {
					return false;
				}
			case Operation:
				if (Operator.PLUS.equals(getSymbol())
						|| Operator.MINUS.equals(getSymbol())) {
					if (!getSymbol().equals(another.getSymbol())) {
						return false;
					}
				} else {
					return true;
				}
			case Number:
				JSNICalls.TIME_ELAPSED("like2 ");
				if (!this.getUnitAttribute().equals(another.getUnitAttribute())
						&& !new UnitMap(this).equals(new UnitMap(another))) {
					return false;
				}
				JSNICalls.TIME_ELAPSED("like3 ");
				// fall through
			case Variable:
				JSNICalls.TIME_ELAPSED("like4 ");
				if (getSymbol().equals(another.getSymbol())) {
					JSNICalls.TIME_ELAPSED("like5 ");
					return true;
				} else {
					JSNICalls.TIME_ELAPSED("like5 ");
					return false;
				}
			case Log:
				if (getAttribute(MathAttribute.LogBase).equals(
						another.getAttribute(MathAttribute.LogBase))
						&& getChildAt(0).isLike(another.getChildAt(0))) {
					return true;
				} else {
					return false;
				}
			case Trig:
				if (getAttribute(MathAttribute.Function).equals(
						another.getAttribute(MathAttribute.Function))
						&& getChildAt(0).isLike(another.getChildAt(0))) {
					return true;
				} else {
					return false;
				}
			default:
				return false;
			}
		}

	}

	private void bindXMLtoNodes(Node equationXMLNode) {
		Element rootNode = (Element) equationXMLNode;

		root = bindXMLtoNodeRecursive(rootNode);

		try {
			validateTree();
		} catch (IllegalStateException e) {
			String message = e.getMessage();
			if (message == null) {
				Window.alert("Oops, an error occured, please refresh the page");
			} else {
				Window.alert(message);
			}
			JSNICalls.error(e.getCause().toString());
			return;
		}
	}

	private EquationNode bindXMLtoNodeRecursive(Element equationXMLNode) {

		EquationNode eqNode = new EquationNode(equationXMLNode);
		String id = eqNode.getId();
		idMap.put(id, eqNode);
		idMLMap.put(id, equationXMLNode);

		NodeList<Node> equationXMLNodeChildren = equationXMLNode
				.getChildNodes();
		for (int i = 0; i < equationXMLNodeChildren.getLength(); i++) {
			Element currentNode = (Element) equationXMLNodeChildren.getItem(i);

			if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
				bindXMLtoNodeRecursive((Element) currentNode);
			}
		}
		return eqNode;
	}

}