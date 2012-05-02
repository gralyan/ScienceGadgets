package com.sciencegadgets.client.equationtree;

import java.util.HashMap;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;

public class TreeCanvas extends DrawingArea {

	private JohnTree johnTree;
	private int childSpace;
	private int rowHeight;
	private AbsolutePanel panel;
	private int sideLengthLeft;
	private int sideLengthRight;
	private HashMap<JohnTree.Type, String> palette;
	private int pad = 5;

	// The number of members in each row
	private byte[] leftLayerCounts;
	private byte[] rightLayerCounts;

	// These counters aid in placing each member down
	private byte[] leftCounters;
	private byte[] rightCounters;

	/**
	 * Constructor of the canvas that automatically adds it to the given panel
	 * @param panel - panel to paint on
	 * @param jTree - tree to paint
	 */
	public TreeCanvas(AbsolutePanel panel, JohnTree jTree) {
		this(panel.getOffsetWidth(), panel.getOffsetHeight(), jTree);
		this.panel = panel;
		this.johnTree = jTree;
		draw(jTree);
	}

	private TreeCanvas(int width, int height, JohnTree jTree) {
		super(width, height);
	}
	public void reDraw(){
		this.clear();
		panel.clear();
		draw(johnTree);
	}

	private void createPalette() {
		palette = new HashMap<JohnTree.Type, String>();
		palette.put(JohnTree.Type.Term, "#7FFFD4");
		palette.put(JohnTree.Type.Series, "#87CEFA");
		palette.put(JohnTree.Type.Function, "#FFD700");
		palette.put(JohnTree.Type.Exponent, "#E6E6FA");
		palette.put(JohnTree.Type.Fraction, "#FAEBD7");
		palette.put(JohnTree.Type.Variable, "#F0F8FF");
		palette.put(JohnTree.Type.Number, "#F0FFF0");
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

		HTML lHTML = jTree.getLeftSide().toMathML();
		HTML rHTML = jTree.getRightSide().toMathML();
		
		panel.add(this);
		
		// Add HTML widgets of top level of each side
		int[] topLayerHeights = addFirstLayer(jTree, lHTML, rHTML);

		// Recursively create the rest of the tree
		if (jTree.getLeftSide().getChildCount() > 1)
			drawChildren(jTree.getLeftSide(), (sideLengthLeft / 2),topLayerHeights[0], (byte) 1,
					true);
		if (jTree.getRightSide().getChildCount() > 1)
			drawChildren(jTree.getRightSide(),
					(sideLengthLeft + sideLengthRight / 2),topLayerHeights[1], (byte) 1, false);

	}

	private void drawChildren(JohnNode pNode, int parentX,int parentY, byte layer,
			Boolean isLeft) {

		List<JohnNode> children = pNode.getChildren();
		int layerHeight = rowHeight * (layer);

		for (JohnNode child : children) {

			// Don't show certain nodes meant only for MathML display
			if (child.isHidden()) {
				continue;
			}

			// Find the maximum width any child can have in the layer
			if (isLeft) {
				childSpace = sideLengthLeft / leftLayerCounts[layer];
			} else {
				childSpace = (sideLengthRight) / rightLayerCounts[layer];
			}
			HTML childHTML = child.toMathML();

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

			int lineX = childLeft + childWidth / 2 + pad;
			int lineY = childTop;

			Rectangle box = new Rectangle(childLeft - pad, childTop,
					childHTML.getOffsetWidth() + 2 * pad,
					childHTML.getOffsetHeight() * 4 / 3);
			box.setFillColor(palette.get(child.getType()));

			Line line = new Line(parentX, parentY
					, lineX,
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
				drawChildren(child, lineX,childTop+box.getHeight(), (byte) (layer + 1), isLeft);
			}
		}

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
			if (child.isHidden()) {
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

	private int[] addFirstLayer(JohnTree jTree, HTML lHTML, HTML rHTML) {
	
		panel.add(lHTML, sideLengthLeft / 2, 0);
		panel.add(new HTML("="), sideLengthLeft, 0);
		panel.add(rHTML, (sideLengthLeft + sideLengthRight / 2), 0);
		
		int lHeight = lHTML.getOffsetHeight();
		int lWidth = lHTML.getOffsetWidth();
		int lLeft = sideLengthLeft / 2;
		int rHeight = rHTML.getOffsetHeight();
		int rWidth = rHTML.getOffsetWidth();
		int rLeft = sideLengthLeft + sideLengthRight / 2;
	
		// Add top level wrappers
		Rectangle lbox = new Rectangle(lLeft - pad, 0,
				lWidth + 2 * pad,
				lHeight * 4 / 3);
		lbox.setFillColor(palette.get(jTree.getLeftSide().getType()));
		this.add(lbox);
		
		if (jTree.getLeftSide().getWrapper() != null) {
		MLElementWrapper lWrap = jTree.getLeftSide().getWrapper()
				.getJoinedWrapper();
		lWrap.setHeight(lbox.getHeight() + "px");
		lWrap.setWidth(lbox.getWidth() + "px");
		panel.add(lWrap, lLeft-pad, 0);
		}
		
		Rectangle rbox = new Rectangle(rLeft - pad, 0,
				rWidth + 2 * pad,
				rHeight * 4 / 3);
		rbox.setFillColor(palette.get(jTree.getRightSide().getType()));
		this.add(rbox);
		
		if (jTree.getRightSide().getWrapper() != null) {
		MLElementWrapper rWrap = jTree.getRightSide().getWrapper()
				.getJoinedWrapper();
		rWrap.setHeight(rbox.getHeight() + "px");
		rWrap.setWidth(rbox.getWidth() + "px");
		panel.add(rWrap, rLeft-pad, 0);
		}
		
		int[] a = {lbox.getHeight(),rbox.getHeight()};
		return a;
	}
}
