package com.sciencegadgets.client.EquationTree;

import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.AlgebraManipulation.EquationTransporter;
import com.sciencegadgets.client.AlgebraManipulation.MLElementWrapper;
import com.sciencegadgets.client.equationbrowser.EquationBrowserEntry;

public class JohnTree {

	private JohnTree tree = this;
	private JohnNode root;
	private JohnNode leftSide;
	private JohnNode equals;
	private JohnNode rightSide;
	private LinkedList<MLElementWrapper> wrappers;

	/**
	 * A tree representation of an equation.
	 * 
	 * @param mathML
	 *            - The equation written in MathML XML
	 * @param isParsedForMath
	 *            - If true, the tree is an abstract syntax tree that can be
	 *            manipulated as math. If false it is a tree of MathML as taken
	 *            from XML
	 */
	public JohnTree(HTML mathML, Boolean isParsedForMath) {
		new MLtoMLTree(mathML).change(this);

		if (isParsedForMath) {
			new MLTreeToMathTree().change(this);
		}

		//MathTreeToML a = new MathTreeToML(this);
		//HTML b = new HTML(a.mlBuild);
		//RootPanel.get().add(b);
		//EquationTransporter.parseJQMath(b.getElement());
	}

	public HTML toMathML(){
		MathTreeToML a = new MathTreeToML(this);
		return a.mlHTML;
	}
	
	public JohnNode getRoot() {
		return root;
	}

	public JohnNode getLeftSide() {
		return leftSide;
	}

	public JohnNode getRightSide() {
		return rightSide;
	}

	public JohnNode getEquals() {
		return equals;
	}

	public LinkedList<MLElementWrapper> getWrappers() {
		return wrappers;
	}

	public class JohnNode {
		// encapsulated dom node
		private Node domNode;
		private Type type;
		private String symbol;
		private String tag;
		private MLElementWrapper wrapper;
		private JohnNode parent;
		private List<JohnNode> children = new LinkedList<JohnNode>();
		private Boolean isHidden = false;

		public JohnNode(Node domNode, String tag, Type type, String symbol) {
			this.domNode = domNode;
			this.tag = tag;
			this.type = type;
			this.symbol = symbol;
		}

		public JohnNode(Node node) {
			domNode = node;
			tag = node.getNodeName();
			type = null;
			symbol = ((Element) node).getInnerText();
		}

		/**
		 * Adds a {@link Node} to the {@link JohnTree} by creating a new
		 * {@link JohnNode}
		 * 
		 */
		// public void add(Node node, MLElementWrapper wrap) {
		// add(new JohnNode(node, wrap));
		// }

		private void add(JohnNode johnNode) {
			children.add(johnNode);
			johnNode.parent = this;
		}

		private void add(int index, JohnNode johnNode) {
			children.add(index, johnNode);
			johnNode.parent = this;
		}

		public List<JohnNode> getChildren() {
			return children;
		}

		public JohnNode getFirstChild() {
			return children.get(0);
		}

		public JohnNode getChildAt(int index) {
			return children.get(index);
		}

		public int getChildCount() {
			return children.size();
		}

		public JohnNode getNextSibling() {
			int nextIndex = this.getParent().getChildren().indexOf(this) + 1;
			return this.getParent().getChildAt(nextIndex);
		}

		public void remove() {
			List<JohnNode> sibs = this.parent.getChildren();
			sibs.remove(sibs.indexOf(this));
		}

		public int getIndex() {
			return this.parent.getChildren().indexOf(this);
		}

		public JohnNode getParent() {
			return parent;
		}

		public Node getDomNode() {
			return domNode;
		}

		public String toString() {
			return symbol;
		}

		public void setString(String string) {
			this.symbol = string;
		}

		public Boolean isHidden() {
			return isHidden;
		}

		public void setWrapper(MLElementWrapper wrap) {
			wrapper = wrap;
		}

		public MLElementWrapper getWrapper() {
			return wrapper;
		}

		public HTML toMathML() {
			HTML mathML;
			if (type == null) {
				mathML = new HTML(tag + " " + "$" + symbol + "$");
			} else {
				mathML = new HTML(type.toString().substring(0, 2) + " " + "$"
						+ symbol + "$");
			}
			EquationBrowserEntry.parseJQMath(mathML.getElement());
			return mathML;
		}

		public Type getType() {
			return type;
		}

		public String getTag() {
			return tag;
		}

		public JohnTree getTree() {
			return tree;
		}
	}

	public static enum Type {
		Term, Series, Function, Exponent, Fraction, Variable, Number;
	}

	class MLtoMLTree extends MathMLParser {
		HashMap<Node, JohnNode> nodeMap;
		private JohnNode prevLeftNode;
		private JohnNode prevRightNode;
		private JohnNode prevSibling;
		private JohnNode curNode;
		JohnNode nLeft;
		JohnNode nEq;
		JohnNode nRight;

		public MLtoMLTree(HTML mathMLequation) {
			super(mathMLequation);
		}

		public void change(JohnTree jTree) {
			jTree.root = new JohnNode(null, null, null, null);
			jTree.leftSide = nLeft;
			jTree.equals = nEq;
			jTree.rightSide = nRight;
			jTree.root.add(jTree.leftSide);
			jTree.root.add(jTree.equals);
			jTree.root.add(jTree.rightSide);

		}

		@Override
		protected void onRootsFound(Node nodeLeft, Node nodeEquals,
				Node nodeRight) {

			nodeMap = new HashMap<Node, JohnNode>();
			nEq = new JohnNode(nodeEquals);
			nLeft = new JohnNode(nodeLeft);
			nRight = new JohnNode(nodeRight);

			prevLeftNode = nLeft;
			prevRightNode = nRight;
		}

		@Override
		protected void onVisitNode(Node currentNode, Boolean isLeft,
				int indexOfChildren) {
			// Must be separated to allow the TreeCanvas to allocate space
			// proportional to member count ratio of sides of the equation

			curNode = new JohnNode(currentNode);

			if (isLeft) {
				if (indexOfChildren == 0) {
					prevLeftNode.add(curNode);
				} else {
					prevSibling.getParent().add(curNode);
				}
				prevLeftNode = curNode;

			} else {

				if (indexOfChildren == 0) {
					prevRightNode.add(curNode);
				} else {
					prevSibling.getParent().add(curNode);
				}
				prevRightNode = curNode;
			}
			nodeMap.put(currentNode, curNode);

		}

		@Override
		protected void onGoingToNextChild(Node currentNode) {
			prevSibling = nodeMap.get(currentNode);

		}
	}

	class MLTreeToMathTree {

		JohnNode mathRoot;
		private LinkedList<JohnNode> nestedMrows = new LinkedList<JohnNode>();
		private LinkedList<JohnNode> negatives = new LinkedList<JohnNode>();
		private MLElementWrapper wrap;

		public void change(JohnTree jTree) {
			mathRoot = jTree.getRoot();
			wrappers = new LinkedList<MLElementWrapper>();
			commenseRevolution(mathRoot);
			rearrangeNestedMrows();
			rearrangeNegatives();
			wrapChildren(mathRoot);
		}

		private void commenseRevolution(JohnNode jNode) {
			List<JohnNode> kids = jNode.getChildren();
			if (kids == null) {
				return;
			}
			for (JohnNode kid : kids) {
				assignSimpleTypes(jNode, kid);
				assignComplexChildMrow(jNode, kid);
				findNestedMrows(jNode, kid);
				kid.isHidden = checkIsHidden(kid);

				if (kid.getChildCount() > 0) {
					commenseRevolution(kid);
				}
			}
		}

		/**
		 * Assigns the MathML tags to JohnNode {@link Type}. This method is
		 * designed to take care of the simple cases, the more complex cases are
		 * assigned in assignComplexChildMrow
		 * 
		 * @param jNode
		 * @param kid
		 */
		private void assignSimpleTypes(JohnNode jNode, JohnNode kid) {
			if ("mi".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Variable;
			} else if ("mn".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Number;
			} else if ("msub".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Variable;
			} else if ("msup".equalsIgnoreCase(kid.getTag())
					|| "msubsup".equalsIgnoreCase(kid.getTag())
					|| "msqrt".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Exponent;
			} else if ("mfrac".equalsIgnoreCase(kid.getTag())) {
				kid.type = Type.Fraction;
			}
		}

		/**
		 * Converts all mrow tags to either {@link Type.Term} or
		 * {@link Type.Series}. Also takes case of special cases such as delta
		 * and functions
		 * 
		 * @param jNode
		 * @return
		 */
		private void assignComplexChildMrow(JohnNode jNode, JohnNode kid) {
			if ("mrow".equalsIgnoreCase(kid.getTag())) {
				// Default to term until + or - found in children
				kid.type = Type.Term;

				for (JohnNode baby : kid.getChildren()) {

					// Check children for +/- => series
					if ("mo".equalsIgnoreCase(baby.getTag())) {
						if ("−".equals(baby.toString())
								|| "+".equals(baby.toString())) {
							// A "-" at the beginning doesn't make it a
							// series
							if (baby.getIndex() > 0) {
								kid.type = Type.Series;
							}
							// Negate the next node because we don't want minus
							if ("−".equals(baby.toString())) {
								negatives.add(baby);
							}
						}
						// For Δ: Δa should be treated as one variable
					} else if ("Δ".equals(baby.toString())) {

						kid.type = Type.Variable;
						kid.children = new LinkedList<JohnNode>();
					} else if ("cos".equals(baby.toString())
							|| "sin".equals(baby.toString())
							|| "tan".equals(baby.toString())
							|| "sec".equals(baby.toString())
							|| "csc".equals(baby.toString())
							|| "cot".equals(baby.toString())
							|| "sinh".equals(baby.toString())
							|| "cosh".equals(baby.toString())
							|| "tanh".equals(baby.toString())
							|| "log".equals(jNode.toString())
							|| "ln".equals(jNode.toString())) {
						kid.type = Type.Function;
					}
				}
			}
		}

		/**
		 * Propagate a negation down the first of every child
		 * 
		 * @param node
		 */
/*		private void negatePropagate(JohnNode node) {
			node.symbol = "-" + node.symbol;
			if (node.getChildCount() > 0) {
				negatePropagate(node.getChildAt(0));
			}
		}
*/
		/**
		 * Finds all instances where there is a series inside a series or a term
		 * inside a term. These will be compiled into one node to make the tree
		 * more mathematically sound by conveying the associative property. Due
		 * to {@link ConcurrentModificationException} problems, the actual
		 * re-arrangement is done in another step: {@link rearrangeNestedMrows}
		 * 
		 * @param parent
		 * @param nestMrow
		 */
		private void findNestedMrows(JohnNode parent, JohnNode kid) {
			if (((Type.Series).equals(kid.getType())
			/**/&& (Type.Series).equals(parent.getType()))
			/**/|| ((Type.Term).equals(kid.getType())
			/**/&& (Type.Term).equals(parent.getType()))) {
				nestedMrows.add(kid);
			}
		}

		/**
		 * Second part of {@link findNestedMrows}, compiles all nested
		 * {@link Type.Series} and {@link Type.Term} to one node
		 */
		private void rearrangeNestedMrows() {
			if (nestedMrows == null) {
				return;
			}
			for (JohnNode nest : nestedMrows) {
				List<JohnNode> kids = nest.getChildren();
				for (JohnNode kid : kids) {
					nest.getParent().add(nest.getIndex(), kid);
				}
				nest.remove();
			}
		}

		/**
		 * This method allows the "invisible negative one" to be displayed
		 * explicitly and maneuvered accordingly. It makes an encasing sentinel
		 * term for negative terms in a series.
		 */
		private void rearrangeNegatives() {
			for (JohnNode neg : negatives) {

				JohnNode neg1 = new JohnNode(neg.getDomNode(), "mn", Type.Number, "-1");

				if (neg.getParent().type == Type.Series) {
					JohnNode negArg = neg.getNextSibling();

					JohnNode encasingTerm = new JohnNode(negArg.getDomNode(), "mrow",
							Type.Term, "-" + negArg.toString());
					neg.getParent().add(negArg.getIndex(), encasingTerm);

					encasingTerm.add(neg1);
					negArg.remove();
					encasingTerm.add(negArg);
				} else {
					neg.getParent().add(neg.getIndex() + 1, neg1);
				}
			}
		}

		private Boolean checkIsHidden(JohnNode jNode) {

			if ("(".equals(jNode.toString()) || ")".equals(jNode.toString())) {
				// No need to show parentheses
				return true;
			} else if ("mo".equals(jNode.getTag())) {
				return true;
			} else if ("cos".equals(jNode.toString())
					|| "sin".equals(jNode.toString())
					|| "tan".equals(jNode.toString())
					|| "sec".equals(jNode.toString())
					|| "csc".equals(jNode.toString())
					|| "cot".equals(jNode.toString())
					|| "log".equals(jNode.toString())
					|| "ln".equals(jNode.toString())
			// Don't show function name as child, parent will be function
			// Changes here must also be made to
			// MLTreeToMathTree.assignComplexChildMrow
			) {
				return true;
			} else if ("msub".equals(jNode.getParent().getTag())
			// Don't show subscripts because it's really one variable
					|| ("msubsup".equals(jNode.getParent().getTag()) && jNode
							.getIndex() == 1)) {
				return true;
			}

			return false;

		}

		private void wrapChildren(JohnNode jNode) {
			List<JohnNode> children = jNode.getChildren();

			for (JohnNode child : children) {
				if (!child.isHidden()) {
					wrap = new MLElementWrapper(child, true, true);
					child.setWrapper(wrap);
					wrappers.add(wrap);
				}
				if(child.getChildCount() > 0){
					wrapChildren(child);
			}
				
			}
		}
	}

	class MathTreeToML {
		//String mlBuild = "<math>";
		HTML mlHTML = new HTML("<math></math>");

		MathTreeToML(JohnTree tree) {
			Element firstNode = mlHTML.getElement().getFirstChildElement();
			addChild(tree.getRoot(), firstNode);
			//mlBuild = mlBuild + "</math>";
			//System.out.println(mlBuild);
			System.out.println(mlHTML.getHTML());
			
		}

		private void addChild(JohnNode from, Node to) {
			List<JohnNode> children = from.getChildren();
			
			for (JohnNode child : children) {
				
				Node childTo = to.appendChild(child.getDomNode().cloneNode(false));
				try {
					child.getWrapper().setElementWrapped((Element)childTo);
				} catch (NullPointerException e) {
					//System.out.println("child: " + child.tag+" "+child.toString());
				}
				
				if ("mi".equals(child.getTag()) | "mn".equals(child.getTag())
						| "mo".equals(child.getTag())) {

					if (child.toString() != "-1") {
						((Element)childTo).setInnerText(child.toString());
					}
				}
				if (child.getChildCount() > 0) {
					addChild(child, childTo);
				}
			}
		}
	}
}