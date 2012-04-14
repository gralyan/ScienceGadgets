package com.sciencegadgets.client.EquationTree;

import java.util.HashMap;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.AlgebraManipulation.MLElementWrapper;
import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;

public class TreeCanvas extends DrawingArea {

	private int childSpace;
	private int rowHeight;
	private AbsolutePanel panel;
	private int sideLengthLeft;
	private int sideLengthRight;
	private HashMap<JohnTree.Type, String> palette;

	// The number of members in each row
	private byte[] leftLayerCounts;
	private byte[] rightLayerCounts;

	// These counters aid in placing each member down
	private byte[] leftCounters;
	private byte[] rightCounters;

	/**
	 * Constructor of the canvas that automatically adds it to the given panel
	 * 
	 * @param panel
	 *            - panel to paint on
	 * @param jTree
	 *            - tree to paint
	 */
	public TreeCanvas(AbsolutePanel panel, JohnTree jTree) {
		this(panel.getOffsetWidth(), panel.getOffsetHeight(), jTree);
		this.panel = panel;
		draw(jTree);
	}

	private TreeCanvas(int width, int height, JohnTree jTree) {
		super(width, height);
	}

	public void draw(JohnTree jTree) {

		createPalette();

		leftLayerCounts = new byte[20];
		rightLayerCounts = new byte[20];
		leftCounters = new byte[20];
		rightCounters = new byte[20];

		// The counting in each layer will allow for maximum spacing, index 0 is
		// each side
		leftLayerCounts[0]++;
		rightLayerCounts[0]++;
		getNextLayerCounts(jTree.getLeftSide(), (byte) 1, true);
		getNextLayerCounts(jTree.getRightSide(), (byte) 1, false);

		// Resize side lengths depending on the ratio of side member density.
		// This keeps the tree from looking lopsided when there are many more
		// variables on one side
		int leftMemberCount = 0;
		int rightMemberCount = 0;

		for (int i = 0; i < leftLayerCounts.length; i++) {
			// Count all the members in this side, giving more weight to the
			// higher layers by dividing by i. (*100 to aleviate truncation,
			// only the ratio matters anyway)
			leftMemberCount += (leftLayerCounts[i] * 100 / (i + 1));
		}

		for (int i = 0; i < rightLayerCounts.length; i++) {
			rightMemberCount += rightLayerCounts[i] * 100 / (i + 1);
		}

		sideLengthLeft = (this.getWidth() * leftMemberCount)
				/ (rightMemberCount + leftMemberCount);
		sideLengthRight = this.getWidth() - sideLengthLeft;

		rowHeight = this.getHeight() / 6;

		panel.add(this);
		panel.add(jTree.getRoot().toMathML(), sideLengthLeft, 0);
		panel.add(jTree.getLeftSide().toMathML(), sideLengthLeft / 2, 0);
		panel.add(jTree.getRightSide().toMathML(),
				(sideLengthLeft + sideLengthRight / 2), 0);
		if (jTree.getLeftSide().getChildCount() > 1)
			drawChildren(jTree.getLeftSide(), (sideLengthLeft / 2), (byte) 1,
					true);
		if (jTree.getRightSide().getChildCount() > 1)
			drawChildren(jTree.getRightSide(),
					(sideLengthLeft + sideLengthRight / 2), (byte) 1, false);

	}

	/**
	 * Prepares the canvas spacing by recursively looking through the tree and
	 * counting the number of members in each layer
	 * 
	 * @param pNode
	 * @param layer
	 * @param isLeft
	 */
	private void getNextLayerCounts(JohnNode pNode, byte layer, Boolean isLeft) {
		List<JohnNode> children = pNode.getChildren();

		for (JohnNode child : children) {

			// Don't show certain nodes meant only for MathML display
			if (isHidden(child)) {
				continue;
			}

			if (isLeft) {
				leftLayerCounts[layer]++;
			} else {
				rightLayerCounts[layer]++;
			}
			if (child.getChildCount() > 0) {
				getNextLayerCounts(child, (byte) (layer + 1), isLeft);
			}
		}
	}

	private void drawChildren(JohnNode pNode, int parentX, byte layer,
			Boolean isLeft) {

		List<JohnNode> children = pNode.getChildren();
		int layerHeight = rowHeight * (layer);

		for (JohnNode child : children) {

			// Don't show certain nodes meant only for MathML display
			if (isHidden(child)) {
				continue;
			}

			// Find the maximum width any child can have in the layer
			if (isLeft) {
				childSpace = sideLengthLeft / leftLayerCounts[layer];
			} else {
				childSpace = (sideLengthRight) / rightLayerCounts[layer];
			}
			HTML childHTML = child.toMathML();
			childHTML.getHTML();

			int childWidth = childHTML.getOffsetWidth();
			// int childHeight = childHTML.getOffsetHeight();

			int placement;
			if (isLeft) {
				placement = childSpace * (leftCounters[layer]);
				leftCounters[layer]++;

			} else {
				placement = (childSpace * rightCounters[layer]);
				rightCounters[layer]++;
				// Shift to right side
				placement += sideLengthLeft;
			}
			// placement = (placement + parentX) / 2; // Add gravity towards
			// parent node
			placement += childSpace / 4;// padding
			if (childWidth >= childSpace) {
				// If the child is too big and going to overflow pull it back
				placement -= childSpace / 4;
			}
			panel.add(childHTML, placement, layerHeight);

			int childLeft = childHTML.getAbsoluteLeft()
					- panel.getAbsoluteLeft();
			int childTop = childHTML.getAbsoluteTop() - panel.getAbsoluteTop();

			int pad = 5;
			int lineX = childLeft + childWidth / 2 + pad;
			int lineY = childTop;

			Rectangle box = new Rectangle(childLeft - pad, childTop,
					childHTML.getOffsetWidth() + 2 * pad,
					childHTML.getOffsetHeight() * 4 / 3);
			//TODO
			System.out.println(palette.get(child.getType()));
			//box.setFillColor(palette.get(child.getType()));

			Line line = new Line(parentX, layerHeight - rowHeight / 2, lineX,
					lineY);
			this.add(line);
			this.add(box);

			if (child.getWrapper() != null) {
				MLElementWrapper wrap = child.getWrapper().getJoinedWrapper();
				wrap.setHeight(box.getHeight() + "px");
				wrap.setWidth(box.getWidth() + "px");
				panel.add(wrap, childLeft - pad, childTop);
			}

			if (child.getChildCount() > 0) {
				drawChildren(child, lineX, (byte) (layer + 1), isLeft);
			}
		}

	}

	/**
	 * Since the GWT graphics library doesn't look into CSS files, this method
	 * is meant to get the color names from the CSS file and add them to a
	 * palette which will be used to add the appropriate color to each box
	 */
	private void createPalette() {
		palette = new HashMap<JohnTree.Type, String>();
		SimplePanel dummyPanel = new SimplePanel();
		Element from = dummyPanel.getElement();
		String att = "color";

		for(JohnTree.Type t : JohnTree.Type.values()){
		dummyPanel.setStyleName(t.toString());
		palette.put(t, DOM.getStyleAttribute(from, att));
		}
	}

	private Boolean isHidden(JohnNode child) {
		if ("(".equals(child.toString()) || ")".equals(child.toString())) {
			// No need to show parentheses
			return true;
		} else if ("mo".equals(child.getTag())) {
			return true;
		} else if ("cos".equals(child.toString())
				|| "sin".equals(child.toString())
				|| "tan".equals(child.toString())
				|| "sec".equals(child.toString())
				|| "csc".equals(child.toString())
				|| "cot".equals(child.toString())
				|| "log".equals(child.toString())
				|| "ln".equals(child.toString())
		// Don't show function name as child, parent will be function
		// Changes here must also be made to
		// MLTreeToMathTree.assignComplexChildMrow
		) {
			return true;
		} else if ("msub".equals(child.getParent().getTag())
		// Don't show subscripts because it's really one variable
				|| ("msubsup".equals(child.getParent().getTag()) && child
						.getIndex() == 1)) {
			return true;
		}

		return false;
	}

}
