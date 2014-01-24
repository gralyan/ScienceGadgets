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
import java.math.MathContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import com.google.gwt.core.client.JavaScriptException;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.user.client.DOM;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.algebra.edit.EditWrapper;
import com.sciencegadgets.client.algebra.transformations.AlgebraicTransformations;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.TypeML.Operator;
import com.sciencegadgets.shared.UnitMap;
import com.sciencegadgets.shared.UnitUtil;

public class MathTree {

	private MathNode root;
	private LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();
	private HashMap<String, MathNode> idMap = new HashMap<String, MathNode>();
	private HashMap<String, Element> idMLMap = new HashMap<String, Element>();
	private HashMap<String, Element> idHTMLMap = new HashMap<String, Element>();
	private HashMap<Element, String> idUnitHTMLMap = new HashMap<Element, String>();
	private Element mathML;
	private EquationHTML eqHTML;
	private boolean inEditMode;
	private int idCounter = 0;
	private EquationValidator eqValidator;

	/**
	 * A tree representation of an equation.
	 * 
	 * @param mathML
	 *            - The equation element in MathML XML
	 * @param inEditMode
	 *            - true if intended for edit mode, false if for solving mode
	 * @throws TopNodesNotFoundException
	 */
	public MathTree(Element mathML, boolean inEditMode) {
		mathML = EquationRandomizer.randomizeNumbers(mathML, !inEditMode);

		this.mathML = mathML;
		this.inEditMode = inEditMode;

		bindMLtoNodes(mathML);

		reloadDisplay(true);
	}

	// public MathNode getMathNode(String id) {
	// return idMap.get(id);
	// }

	public boolean isInEditMode() {
		return inEditMode;
	}

	public Element getMathMLClone() {
		return (Element) mathML.cloneNode(true);
	}

	public EquationHTML getDisplayClone() {
		return new EquationHTML(mathML);
	}

	public Element getLeftDisplay() {
		return eqHTML.getLeft();
	}

	public Element getRightDisplay() {
		return eqHTML.getRight();
	}

	public MathNode getRoot() {
		return root;
	}

	public MathNode getLeftSide() {
		checkSideForm();
		return root.getChildAt(0);
	}

	public MathNode getRightSide() {
		checkSideForm();
		return root.getChildAt(2);
	}

	public MathNode getEquals() {
		checkSideForm();
		return root.getChildAt(1);
	}

	void checkSideForm() {
		if (root.getChildCount() != 3) {
			JSNICalls.error("root has too many children, not side=side: "
					+ getMathMLClone().getString());
		}
		if (!"=".equals(root.getChildAt(1).getSymbol())) {
			JSNICalls
					.error("<mo>=</mo> isn't the root's second child, not side=side "
							+ getMathMLClone().getString());
		}
	}

	public EquationHTML reloadDisplay(boolean hasSmallUnits) {
		for (Wrapper w : wrappers) {
			if (w instanceof EditWrapper) {
				((EditWrapper) w).onUnload();

			} else if (w instanceof AlgebaWrapper) {
				((AlgebaWrapper) w).onUnload();
			}
			w.getElement().removeFromParent();
		}
		wrappers.clear();

		eqHTML = new EquationHTML(mathML, hasSmallUnits);

		NodeList<Element> allElements = eqHTML.getElement()
				.getElementsByTagName("*");

		idHTMLMap.clear();

		for (int i = 0; i < allElements.getLength(); i++) {
			Element el = (Element) allElements.getItem(i);
			String elId = el.getAttribute("id");
			if (elId.contains(UnitUtil.UNIT_NODE_DELIMITER)) {
				String parentElId = elId.split(UnitUtil.UNIT_NODE_DELIMITER)[1];
				idUnitHTMLMap.put(el, parentElId);
			} else {
				idHTMLMap.put(elId, el);
			}
			el.removeAttribute("id");
		}
		return eqHTML;
	}

	public LinkedList<Wrapper> getWrappers() {
		return wrappers;
	}

	public MathNode getNodeById(String id) throws NoSuchElementException {
		MathNode node = idMap.get(id);
		if (node == null) {
			JSNICalls.error("Can't get node by id: " + id + "\n"
					+ getMathMLClone().getString());
			throw new NoSuchElementException("Can't get node by id: " + id);
		}
		return node;
	}

	public MathNode NEW_NODE(Element mlNode) {
		return new MathNode(mlNode);
	}

	public MathNode NEW_NODE(TypeML type, String symbol) {
		return new MathNode(type, symbol);
	}

	private String createId() {
		return "ML" + idCounter++;
	}

	public void validateTree() {
		
		if(eqValidator == null) {
			eqValidator = new EquationValidator();
		}
 
		for (MathNode node : idMap.values()) {
			eqValidator.validateMathNode(node);
		}
		
		if(!inEditMode) {
			eqValidator.validateQuantityKinds(this);
		}
		
		if (idMap.size() != idMLMap.size()) {
			JSNICalls
					.error("The binding maps must have the same size: idMap.size()="
							+ idMap.size()
							+ " idMLMap.size()="
							+ idMLMap.size());
		}
	}

	private void AddToMaps(MathNode node) {
		String id = node.getId();
		if (id == "") {
			id = createId();
		}
		// add node to binding map
		if (!idMap.containsKey(id)) {
			idMap.put(id, node);
			idMLMap.put(id, node.getMLNode());
		}
	}

	/**
	 * Gets all nodes by specified type, use null for all nodes
	 * 
	 * @param type
	 */
	public ArrayList<MathNode> getNodesByType(TypeML type) {
		ArrayList<MathNode> nodes = new ArrayList<MathNode>();
		String tag = "*";
		if (type != null) {
			tag = type.getTag();
		}
		NodeList<Element> elements = root.getMLNode().getElementsByTagName(tag);
		for (int i = 0; i < elements.getLength(); i++) {
			String id = elements.getItem(i).getAttribute("id");
			nodes.add(idMap.get(id));
		}
		return nodes;
	}

	// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Node Class
	// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	public class MathNode {
		private Element mlNode;
		private Wrapper wrapper;

		/**
		 * Wrap existing MathML node
		 */
		public MathNode(Element mlNode) {

			if ("".equals(mlNode.getAttribute("id"))) {
				mlNode.setAttribute("id", createId());
			}

			this.mlNode = mlNode;
		}

		/**
		 * Creates a new MathML DOM node which should be added into the MathML
		 * </br>get the DOM node with:
		 * <p>
		 * getMlNode()
		 * </p>
		 * 
		 * @param type
		 *            - The {@link TypeML}
		 * @param symbol
		 *            - inner text
		 */
		public MathNode(TypeML type, String symbol) {

			String tag = type.getTag();

			com.google.gwt.user.client.Element newNode = DOM.createElement(tag);
			newNode.setAttribute("id", createId());

			this.mlNode = newNode;

			if (!"".equals(symbol)) {
				setSymbol(symbol);
//				newNode.setInnerText(symbol);
			}

		}

		/**
		 * Returns a copy of this node
		 */
		public MathNode clone() {
			Element newEl = (Element) mlNode.cloneNode(true);
			newEl.removeAttribute("id");
			MathNode top = new MathNode(newEl);
			AddToMaps(top);

			NodeList<Element> descendants = newEl.getElementsByTagName("*");
			for (int i = 0; i < descendants.getLength(); i++) {
				Element descendantEl = descendants.getItem(i);
				descendantEl.removeAttribute("id");
				AddToMaps(new MathNode(descendantEl));
			}

			return top;
		}

		/**
		 * Adds a node between this node and its parent, encasing this branch of
		 * the tree in a new node. <br/>
		 * 
		 * @param type
		 *            - the type of the new node
		 * @return - encasing node
		 */
		public MathNode encase(TypeML type) {
			// Don't encase sum in sum or term in term
			boolean sumOrTerm = TypeML.Sum.equals(type)
					|| TypeML.Term.equals(type);
			if (getType().equals(type) && sumOrTerm) {
				return this;
			} else if (getParentType().equals(type) && sumOrTerm) {
				return getParent();
			} else {
				MathNode encasing = new MathNode(type, "");
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

			// Propagate leading minus sign or remove leading plus
			if (getChildCount() != 0) {
				MathNode possibleMinus = getChildAt(0);
				if (TypeML.Operation.equals(possibleMinus.getType())) {
					if (Operator.MINUS.getSign().equals(
							possibleMinus.getSymbol())) {
						if (getChildCount() > 1) {
							AlgebraicTransformations
									.propagateNegative(getChildAt(1));
							possibleMinus.remove();
						} else {
							possibleMinus.remove();
							MathNode parent = this.getParent();
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
				this.replace(TypeML.Number, "0");
				break;
			case 1:
				TypeML childType = getFirstChild().getType();
				if (childType.equals(getParentType())
						&& (TypeML.Sum.equals(childType) || TypeML.Term
								.equals(childType))) {
					LinkedList<MathNode> grandChildren = this.getFirstChild()
							.getChildren();
					for (int i = grandChildren.size(); i > 0; i--) {
						getParent().addBefore(this.getIndex(),
								grandChildren.get(i - 1));
					}
					this.getFirstChild().remove();
					this.remove();
				} else {
					getParent()
							.addBefore(this.getIndex(), this.getFirstChild());
					this.remove();
				}
				break;
			case 2:// Should only be sums with a negative in front
				JSNICalls
						.error("There should not be two children in a Sum or Term: "
								+ toString());
				break;
			}
		}

		public void replace(MathNode replacement) {
			this.getParent().addBefore(this.getIndex(), replacement);
			this.remove();
		}

		public MathNode replace(TypeML type, String symbol) {
			MathNode replacement = new MathNode(type, symbol);
			replace(replacement);
			return replacement;
		}

		private void add(int index, MathNode node, boolean after)
				throws IllegalArgumentException {

			// Don't add sum to sum or term to term, just add it's children
			if (getType().equals(node.getType())
					&& (TypeML.Sum.equals(node.getType()) || TypeML.Term
							.equals(node.getType()))) {
				LinkedList<MathNode> children = node.getChildren();
				for (int i = children.size(); i > 0; i--) {
					addBefore(index, children.get(i - 1));
				}

				node.remove();

			} else {
				Element elementNode = node.getMLNode();

				// Add node to DOM tree
				if (index < 0 || index >= mlNode.getChildCount()) {
					mlNode.appendChild(elementNode);
				} else if (after) {
					Node referenceChild = mlNode.getChild(index);
					mlNode.insertAfter(elementNode, referenceChild);
				} else {
					Node referenceChild = mlNode.getChild(index);
					mlNode.insertBefore(elementNode, referenceChild);
				}

				AddToMaps(node);
			}
		}

		public void addAfter(int index, MathNode node) {
			add(index, node, true);
		}

		public MathNode addAfter(int index, TypeML type, String symbol) {
			MathNode newNode = new MathNode(type, symbol);
			this.addAfter(index, newNode);
			return newNode;
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
		 * @param node
		 *            - the node to be added
		 */
		public void addBefore(int index, MathNode node) {
			add(index, node, false);
		}

		/**
		 * Creates a node to add as a child at the specified index.
		 * 
		 * @return - The newly create child
		 * @throws Exception
		 */
		public MathNode addBefore(int index, TypeML type, String symbol) {
			MathNode newNode = new MathNode(type, symbol);
			this.addBefore(index, newNode);
			return newNode;
		}

		public MathNode append(MathNode newNode) {
			addBefore(-1, newNode);
			return newNode;
		}

		public MathNode append(TypeML type, String symbol) {
			return addBefore(-1, type, symbol);
		}

		public LinkedList<MathNode> getChildren() {
			NodeList<Node> childrenNodesList = getMLNode().getChildNodes();
			LinkedList<MathNode> childrenNodes = new LinkedList<MathNode>();

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

		public MathNode getChildAt(int index) {
			Node node = getMLNode().getChildNodes().getItem(index);
			String id = ((Element) node).getAttribute("id");
			return getNodeById(id);
		}

		public MathNode getFirstChild() {
			return getChildAt(0);
		}

		public int getChildCount() {
			return mlNode.getChildCount();
		}

		/**
		 * @return <b>Next sibling</b> or <b>null</b> if none exists
		 */
		public MathNode getNextSibling() {
			return getSibling(1);
		}

		/**
		 * @return <b>Previous sibling</b> or <b>null</b> if none exists
		 */
		public MathNode getPrevSibling() {
			return getSibling(-1);
		}

		/**
		 * This method gets the sibling at a position relative the this node
		 * 
		 * @param indexesAway
		 *            - the number of indexes away from this sibling positive
		 *            for siblings to the right, negative for siblings to the
		 *            left
		 *            <p>
		 *            ex:</br>-1 for previous </br>1 for next
		 *            </p>
		 */
		private MathNode getSibling(int indexesAway) {
			MathNode parent = this.getParent();
			int siblingIndex = getIndex() + indexesAway;

			try {
				MathNode sibling = parent.getChildAt(siblingIndex);
				return sibling;
			} catch (JavaScriptException e) {
				return null;
			}
		}

		public void remove() {
			removeChildren();

			String id = getId();
			idMap.remove(id);
			idMLMap.remove(id);
			mlNode.removeFromParent();
			wrappers.remove(wrapper);
		}

		private void removeChildren() {
			LinkedList<MathNode> children = getChildren();

			for (MathNode child : children) {
				String id = child.getId();
				idMap.remove(id);
				idMLMap.remove(id);
				child.removeChildren();
			}
		}

		public int getIndex() {
			return this.getParent().getChildren().indexOf(this);
		}

		public MathNode getParent() {
			if ("math".equalsIgnoreCase(getTag())) {
				throw new NoSuchElementException(
						"Can't get the parent of a math tag because it's the root:\n"
								+ toString());
			}
			Element parentElement = getMLNode().getParentElement();
			String parentId = parentElement.getAttribute("id");
			MathNode parentNode = getNodeById(parentId);
			return parentNode;
		}

		public Element getMLNode() {
			return mlNode;
		}

		public String toString() {
			if (mlNode.getString() != null) {
				return mlNode.getString();
			} else {
				return JSNICalls.elementToString(mlNode);
			}
		}

		/**
		 * Inserts the symbol into the node. If the node is a number, the symbol
		 * will be saved as the node's value attribute and the displaying symbol
		 * may be shortened for formatting purposes
		 * 
		 * @param symbol
		 */
		public void setSymbol(String symbol) {
			
			if (TypeML.Number.equals(getType()) && !ChangeNodeMenu.NOT_SET.equals(symbol)) {
				setAttribute(MathAttribute.Value, symbol);
				String roundedValue = new BigDecimal(symbol).round(
						new MathContext(4)).toString();
				String tilda = symbol.equals(roundedValue) ? "" : "~";
				mlNode.setInnerText(tilda + roundedValue);
			} else {
				mlNode.setInnerText(symbol);
			}
		}

		public String getSymbol() {
			if (TypeML.Number.equals(getType())) {
				return getAttribute(MathAttribute.Value);
			} else {
				return mlNode.getInnerText();
			}
		}

		// public ArrayList<Element> getSimilarHTMLFromAllLayers() {
		// String id = getMLNode().getAttribute("id");
		// ArrayList<Element> similar = new ArrayList<Element>();
		// for (int i = 0; i < wrappers.size(); i++) {
		// Element possible = Document.get().getElementById(
		// id + EquationPanel.OF_LAYER + "ML" + i);
		// if (possible != null) {
		// similar.add(possible);
		// }
		// }
		// return similar;
		// }

		public Wrapper wrap(Wrapper wrap) {
			wrapper = wrap;
			wrappers.add(wrapper);
			return wrapper;
		}

		public Wrapper getWrapper() {
			return wrapper;
		}

		/**
		 * @return Tag of MathML DOM node in <b>Lower Case</b>
		 */
		public String getTag() {
			return mlNode.getTagName().toLowerCase();
		}

		public MathTree getTree() {
			return MathTree.this;
		}

		public String getId() {
			return getMLNode().getAttribute("id");
		}

		public String getUnitAttribute() {
			return mlNode.getAttribute(MathAttribute.Unit.getName());
		}

		public String getAttribute(MathAttribute attribute) {
			return mlNode.getAttribute(attribute.getName());
		}

		public UnitMap getUnitMap() {
			return new UnitMap(this);
		}

		public void setAttribute(MathAttribute attribute, String value) {
			mlNode.setAttribute(attribute.getName(), value);
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

		public TypeML.Operator getOperation() {
			if ("mo".equalsIgnoreCase(getTag())) {
				String symbol = getSymbol();

				for (TypeML.Operator op : TypeML.Operator.values()) {
					if (op.getSign().equalsIgnoreCase(symbol)
							|| op.getHTML().equalsIgnoreCase(symbol)) {
						return op;
					}
				}
			}
			// throw new
			// InvalidParameterException("Can't getOperation for this node: "+toString());
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

		public TypeML getType() {
			return TypeML.getType(getTag());
		}

		public TypeML getParentType() {
			Element parentEl = getMLNode().getParentElement();
			if (parentEl == null) {
				return null;
			}
			String parentTag = parentEl.getTagName();
			return TypeML.getType(parentTag);
		}

		public Element getHTMLClone() {
			Element html = (Element) getHTML().cloneNode(true);

			html.removeAttribute("class");
			html.getStyle().setDisplay(Display.INLINE_BLOCK);
			return html;
		}

		public String getHTMLString() {
			return getHTMLClone().getString();
		}

		/**
		 * This should only be done after
		 * {@link MathTree#reloadDisplay(boolean)}
		 * 
		 * @return The current HTML element associated with this node
		 */
		public Element getHTML() {
			Element el = idHTMLMap.get(getId());
			if (el == null) {
				JSNICalls.warn("No HTML for node: " + toString());
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
			getHTML().addClassName("highlight");
		}

		public void lineThrough() {
			getHTML().addClassName("lineThrough");
		}

		public boolean isLike(MathNode another) {

			if (!getType().equals(another.getType())) {
				return false;
			}

			// breaks not needed, returns at each step
			switch (getType()) {
			case Term:
				// fall through
			case Sum:
				LinkedList<MathNode> assignedOtherChildren = new LinkedList<MathNode>();
				a: for (MathNode child : getChildren()) {
					b: for (MathNode otherChild : another.getChildren()) {
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
				if (!UnitUtil.compareUnits(this, another)) {
					return false;
				}
				// fall through
			case Variable:
				if (getSymbol().equals(another.getSymbol())) {
					return true;
				} else {
					return false;
				}
			default:
				return false;
			}
		}


	}

	private void bindMLtoNodes(Node mathMLequation) {
		Element rootNode = (Element) mathMLequation;
		root = new MathNode(rootNode);

		addRecursively(rootNode);

		validateTree();
	}

	private void addRecursively(Element mathMLNode) {
		String id = createId();

		mathMLNode.setAttribute("id", id);

		idMLMap.put(id, mathMLNode);
		idMap.put(id, new MathNode(mathMLNode));

		NodeList<Node> mathMLChildren = mathMLNode.getChildNodes();
		for (int i = 0; i < mathMLChildren.getLength(); i++) {
			Element currentNode = (Element) mathMLChildren.getItem(i);

			// Nodes with no children are either inner text or formatting only
			if (currentNode.getChildCount() > 0) {
				addRecursively((Element) currentNode);
			}
		}
	}

}