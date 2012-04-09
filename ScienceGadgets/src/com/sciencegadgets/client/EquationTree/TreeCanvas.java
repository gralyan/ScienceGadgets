package com.sciencegadgets.client.EquationTree;

import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.AlgebraManipulation.MLElementWrapper;
import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;

public class TreeCanvas extends DrawingArea {

	private int childSpace;
	private int rowHeight;
	private AbsolutePanel panel;
	private int sideLengthLeft;
	private int sideLengthRight;

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
		// resizeLayers();

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
		drawChildren(jTree.getLeftSide(), (sideLengthLeft / 2), (byte) 1, true);
		drawChildren(jTree.getRightSide(),
				(sideLengthLeft + sideLengthRight / 2), (byte) 1, false);

	}

	public void getNextLayerCounts(JohnNode pNode, byte layer, Boolean isLeft) {
		List<JohnNode> children = pNode.getChildren();

		for (JohnNode child : children) {
			if (isLeft) {
				leftLayerCounts[layer]++;
			} else {
				rightLayerCounts[layer]++;
			}
			if (child.getChildCount() > 1) {
				getNextLayerCounts(child, (byte) (layer + 1), isLeft);
			}
		}
	}

	private void drawChildren(JohnNode pNode, int parentX, byte layer,
			Boolean isLeft) {

		List<JohnNode> children = pNode.getChildren();
		int layerHeight = rowHeight * (layer);

		for (JohnNode child : children) {

			// Find the maximum width any child can have in the layer
			if (isLeft) {
				childSpace = sideLengthLeft / leftLayerCounts[layer];
			} else {
				childSpace = (sideLengthRight) / rightLayerCounts[layer];
			}
			HTML childHTML = child.toMathML();
			childHTML.getHTML();

			int childWidth = childHTML.getOffsetWidth();
			int childHeight = childHTML.getOffsetHeight();

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
			if (child.getChildCount() > 1) {
				drawChildren(child, lineX, (byte) (layer + 1), isLeft);
			}
		}

	}

}
