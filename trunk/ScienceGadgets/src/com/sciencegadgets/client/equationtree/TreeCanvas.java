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
package com.sciencegadgets.client.equationtree;

import java.util.HashMap;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.VectorObject;
import org.vaadin.gwtgraphics.client.shape.Ellipse;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class TreeCanvas extends DrawingArea {

	private MathMLBindingTree mathMLBindingTree;
	private int childSpace;
	private int rowHeight;
	private AbsolutePanel panel;
	private int sideLengthLeft;
	private int sideLengthRight;
	private HashMap<MathMLBindingTree.Type, String> palette;
	private int pad = 5;
	private int topPad = 10;
	// private String nodePicUrl =
	// "http://www.lanxuan.org/download/openclipart/calloutscloud.svg.SVG";

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
	public TreeCanvas(AbsolutePanel panel, MathMLBindingTree jTree) {
		this(panel.getParent().getOffsetWidth(), panel.getParent()
				.getOffsetHeight(), jTree);

		// Placements are relative to the AbsolutePanel
		panel.getElement().getStyle().setPosition(Position.RELATIVE);

		this.panel = panel;
		this.mathMLBindingTree = jTree;

		// Image backgroundImg = new Image(0, 0, panel.getOffsetWidth(),
		// panel.getOffsetHeight(),
		// "http://ecoartfilm.files.wordpress.com/2012/05/tree.jpg");
		// backgroundImg.setRotation(180);
		// panel.add(backgroundImg);

		draw(jTree);
	}

	private TreeCanvas(int width, int height, MathMLBindingTree jTree) {
		super(width, height);
	}

	public void reDraw() {
		this.clear();
		panel.clear();
		draw(mathMLBindingTree);
	}

	private void createPalette() {
		palette = new HashMap<MathMLBindingTree.Type, String>();
		palette.put(MathMLBindingTree.Type.Term, "#7FFFD4");
		palette.put(MathMLBindingTree.Type.Series, "#87CEFA");
		palette.put(MathMLBindingTree.Type.Function, "#FFD700");
		palette.put(MathMLBindingTree.Type.Exponent, "#E6E6FA");
		palette.put(MathMLBindingTree.Type.Fraction, "#FAEBD7");
		palette.put(MathMLBindingTree.Type.Variable, "#F0F8FF");
		palette.put(MathMLBindingTree.Type.Number, "#F0FFF0");
	}

	public void draw(MathMLBindingTree jTree) {

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

		rowHeight = panel.getParent().getOffsetHeight() / 3;

		// Set the size of the canvas
//		int layerCount = leftLayerCounts.length > rightLayerCounts.length ? leftLayerCounts.length
//				: rightLayerCounts.length;
		int layerCount = 0;
		for(int i=0 ; i<leftLayerCounts.length ; i++){
			if(leftLayerCounts[i] != 0 || rightLayerCounts[i] != 0){
				layerCount++;
			}
		}
		

		int canvasHeight = layerCount * rowHeight;
		panel.setHeight(canvasHeight + "px");
		this.setHeight(canvasHeight + "px");

		panel.add(this);

		splitSidesInMiddle();

		// Add HTML widgets of top level of each side
		int[] topLayerHeights = addFirstLayer(jTree);

		// Recursively create the rest of the tree
		if (jTree.getLeftSide().getChildCount() > 1)
			drawChildren(jTree.getLeftSide(), (sideLengthLeft / 2),
					topLayerHeights[0], (byte) 1, true);
		if (jTree.getRightSide().getChildCount() > 1)
			drawChildren(jTree.getRightSide(),
					(sideLengthLeft + sideLengthRight / 2), topLayerHeights[1],
					(byte) 1, false);

	}

	private void drawChildren(MathMLBindingNode pNode, int parentX,
			int parentY, byte layer, Boolean isLeft) {

		List<MathMLBindingNode> children = pNode.getChildren();
		int layerHeight = rowHeight * (layer);

		for (MathMLBindingNode child : children) {

			// Don't show certain nodes meant only for MathML display
			if (child.isHidden()) {
				Log.info("Skip drawing isHidden: " + child);
				continue;
			} else {

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
			// Add gravity towards parent node
			placement = (placement + parentX) / 2;

			placement += childSpace / 4;// padding
			if (childWidth > childSpace) {
				// If the child is too big and going to overflow pull it back
				placement -= childWidth - childSpace;
			}
			panel.add(childHTML, placement, layerHeight);
			
			// Operation symbols only, no wrapper or line
			if("mo".equals(child.getTag())){
				continue;
			}

			int childLeft = childHTML.getAbsoluteLeft()
					- panel.getAbsoluteLeft();
			int childTop = childHTML.getAbsoluteTop() - panel.getAbsoluteTop();

			int lineX = childLeft + childWidth / 2 + pad;
//			int lineY = childTop;

			int boxLeft = childLeft - pad;
			int boxTop = childTop;
			int boxWidth = childHTML.getOffsetWidth() + 2 * pad;
			int boxHeight = childHTML.getOffsetHeight() * 4 / 3;

			VectorObject nodeShape = createNodeShape(child.getType(), boxLeft,
					boxTop, boxWidth, boxHeight);
			this.add(nodeShape);

			Line connectingLine = new Line(parentX, parentY, lineX,
					(nodeShape.getAbsoluteTop() - panel.getAbsoluteTop()));
			this.add(connectingLine);

			// Image nodePic = new Image(boxLeft, boxTop-boxHeight, boxWidth,
			// boxHeight*2, nodePicUrl);
			// this.add(nodePic);

			if (child.getWrapper() != null) {
				MLElementWrapper wrap = child.getWrapper().getJoinedWrapper();
				wrap.setHeight(boxHeight+6*pad + "px");
				wrap.setWidth(boxWidth + 6*pad + "px");
				panel.add(wrap, childLeft - 3*pad, childTop-2*pad);
				// Hint under drop target
				panel.add(wrap.getDropDescriptor(), childLeft - pad, childTop
						+ boxHeight);
			}

			if (child.getChildCount() > 0) {
				drawChildren(child, lineX, childTop + boxHeight,
						(byte) (layer + 1), isLeft);
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
	private void getNextLayerCounts(MathMLBindingNode pNode, byte layer,
			Boolean isLeft) {
		List<MathMLBindingNode> children = pNode.getChildren();

		for (MathMLBindingNode child : children) {

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

	private int[] addFirstLayer(MathMLBindingTree jTree) {

		HTML lHTML = jTree.getLeftSide().toMathML();
		HTML rHTML = jTree.getRightSide().toMathML();

		panel.add(lHTML, sideLengthLeft / 2, topPad);
		// panel.add(new HTML("="), sideLengthLeft, topPad);
		panel.add(rHTML, (sideLengthLeft + sideLengthRight / 2), topPad);

		int lHeight = lHTML.getOffsetHeight();
		int lWidth = lHTML.getOffsetWidth();
		int lLeft = sideLengthLeft / 2;
		int rHeight = rHTML.getOffsetHeight();
		int rWidth = rHTML.getOffsetWidth();
		int rLeft = sideLengthLeft + sideLengthRight / 2;

		// Left - Add top level wrappers
		int lboxLeft = lLeft - pad;
		int lboxTop = topPad;
		int lboxWidth = lWidth + 2 * pad;
		int lboxHeight = lHeight * 4 / 3;

		VectorObject lNodeShape = createNodeShape(
				jTree.getLeftSide().getType(), lboxLeft, lboxTop, lboxWidth,
				lboxHeight);
		this.add(lNodeShape);

		if (jTree.getLeftSide().getWrapper() != null) {
			MLElementWrapper lWrap = jTree.getLeftSide().getWrapper()
					.getJoinedWrapper();
			lWrap.setHeight(lboxHeight+4*pad + "px");
			lWrap.setWidth(lboxWidth+4*pad + "px");
			panel.add(lWrap, lLeft - 3*pad, topPad-2*pad);
			panel.add(lWrap.getDropDescriptor(), lLeft - pad, topPad
					+ lboxHeight);
		}

		// Right - Add top level wrappers
		int rboxLeft = rLeft - pad;
		int rboxTop = topPad;
		int rboxWidth = rWidth + 2 * pad;
		int rboxHeight = rHeight * 4 / 3;

		VectorObject rNodeShape = createNodeShape(jTree.getRightSide()
				.getType(), rboxLeft, rboxTop, rboxWidth, rboxHeight);
		this.add(rNodeShape);

		if (jTree.getRightSide().getWrapper() != null) {
			MLElementWrapper rWrap = jTree.getRightSide().getWrapper()
					.getJoinedWrapper();
			rWrap.setHeight(rboxHeight+4*pad + "px");
			rWrap.setWidth(rboxWidth+4*pad + "px");
			panel.add(rWrap, rLeft - 3*pad, topPad-2*pad);
			panel.add(rWrap.getDropDescriptor(), rLeft - pad, topPad
					+ rboxHeight);
		}

		int[] a = { lboxHeight + topPad, rboxHeight + topPad };
		return a;
	}

	private void splitSidesInMiddle() {

		Rectangle equalsTop = new Rectangle(sideLengthLeft - topPad, topPad,
				topPad * 2, topPad / 2);
		Rectangle equalsBottom = new Rectangle(equalsTop.getX(),
				equalsTop.getY() + topPad, equalsTop.getWidth(),
				equalsTop.getHeight());

		equalsTop.setFillColor("black");
		equalsBottom.setFillColor("black");

		Line verticalLine = new Line(sideLengthLeft, equalsBottom.getY()
				+ topPad * 2, sideLengthLeft, this.getHeight());

		Group split = new Group();
		split.add(equalsTop);
		split.add(equalsBottom);
		split.add(verticalLine);

		this.add(split);

	}

	private Group createNodeShape(Type type, int x, int y, int width, int height) {
		Group shape = new Group();

		switch (type) {
		case Term:

			int skew = height / 2;
			int spline = height * 5 / 16;// NURB length for Bézier curves

			Path front = new Path(x, y);// Box Front, starts at top left
			front.curveRelativelyTo(-spline, 0, -spline, height, 0, height);// down
			front.lineRelativelyTo(width, 0);// right
			front.curveRelativelyTo(spline, 0, spline, -height, 0, -height);// up
			front.close();
			front.setFillColor("yellow");

			Path top = new Path(x, y);// Box Top, starts bottom left corner
			top.lineRelativelyTo(skew, -skew);// up to the right
			top.lineRelativelyTo(width, 0);// right
			top.lineRelativelyTo(-skew, skew);// down left
			top.close();
			top.setFillColor("blue");

			Path side = new Path(x + width, y + height);// Side, starts lowest
			side.lineRelativelyTo(skew, -skew);// up, right
			side.curveRelativelyTo(spline, -spline, spline, -height, 0, -height);// up
			side.lineRelativelyTo(-skew, skew);// down,left
			side.close();
			side.setFillColor("red");

			shape.add(side);
			shape.add(front);// front added after side to overlap curve
			shape.add(top);
			break;

		case Series:

			Rectangle rectangle = new Rectangle(x, y - pad, width, height);
			rectangle.setFillColor("yellow");
			shape.add(rectangle);

			int tickCount = rectangle.getWidth() / pad;
			for (int i = 0; i < tickCount; i++) {
				int xPos = x + (pad * i);
				int tickHeight;
				if (i % 8 == 0) {
					tickHeight = pad * 3 / 2;
				} else if (i % 4 == 0) {
					tickHeight = pad;
				} else {
					tickHeight = pad * 2 / 3;
				}
				Line tick = new Line(xPos, y - pad, xPos, y - pad + tickHeight);
				shape.add(tick);
			}
			break;

		case Number:

			Ellipse ellipseNum = new Ellipse(x + width / 2, y + height / 2,
					width * 2 / 3, height * 2 / 3);
			ellipseNum.setFillColor("lime");

			shape.add(ellipseNum);
			break;

		case Variable:

			// Starts at top left inner corner
			Path star = new Path(x+pad, y);// ( 0, 50),
			star.lineRelativelyTo(5, -15);
			star.lineRelativelyTo(5, 15);// ( 10, 20),
			star.lineRelativelyTo(15, 0);// ( 40, 20),
			star.lineRelativelyTo(-10, 10);// ( 20, 0),
			star.lineRelativelyTo(5, 15);// ( 30, -30),
			star.lineRelativelyTo(-15, -10);// ( 0, -10),
			star.lineRelativelyTo(-15, 10);// (-30, -30),
			star.lineRelativelyTo(5, -15);// (-20, 0),
			star.lineRelativelyTo(-10, -10);// (-40, 20),
			// star.lineRelativelyTo(30, 0);// (-10, 20),
			star.close();
//			// Starts at top left inner corner
//			Path star = new Path(x, y);// ( 0, 50),
//			star.lineRelativelyTo(10, -30);
//			star.lineRelativelyTo(10, 30);// ( 10, 20),
//			star.lineRelativelyTo(30, 0);// ( 40, 20),
//			star.lineRelativelyTo(-20, 20);// ( 20, 0),
//			star.lineRelativelyTo(10, 30);// ( 30, -30),
//			star.lineRelativelyTo(-30, -20);// ( 0, -10),
//			star.lineRelativelyTo(-30, 20);// (-30, -30),
//			star.lineRelativelyTo(10, -30);// (-20, 0),
//			star.lineRelativelyTo(-20, -20);// (-40, 20),
//			// star.lineRelativelyTo(30, 0);// (-10, 20),
//			star.close();

			star.setFillColor("red");

			shape.add(star);
			break;

		case Fraction:

			Ellipse pieTop = new Ellipse(x + width / 2, y + height / 2, width,
					height / 2);
			pieTop.setFillColor("orange");

			Path piePan = new Path(x - width / 2, y + height / 2);
			piePan.lineRelativelyTo(width / 2, height);
			piePan.lineRelativelyTo(width, 0);
			piePan.lineRelativelyTo(width / 2, -height);
			piePan.close();
			piePan.setFillColor("silver");

			shape.add(piePan);
			shape.add(pieTop);
			break;

		case Exponent:

			// TODO
			Rectangle exp = new Rectangle(x, y + height, width, height);
			shape.add(exp);
			break;

		case Function:
			// TODO
			Rectangle fun = new Rectangle(x + width, y + height, width, height);
			shape.add(fun);
			break;

		}

		return shape;
	}
}
