package com.sciencegadgets.client;

import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.sciencegadgets.client.JohnTree.JohnNode;

public class TreeCanvas extends DrawingArea {
	
	/**
	 *  Constructor of the canvas that automatically adds it to the given panel
	 * @param panel - panel to paint on
	 * @param jTree - tree to paint
	 */
	public TreeCanvas(AbsolutePanel panel, JohnTree jTree){
		this(panel.getOffsetWidth(), panel.getOffsetHeight(), jTree);
		this.panel = panel;
		draw(jTree);
	}
	private TreeCanvas(int width, int height, JohnTree jTree) {
		super(width, height);
	}
	
	private int childWidth;
	private int rowHeight;
	private AbsolutePanel panel;
	
	public void draw(JohnTree jTree) {
		// Widgets displaying nodes will be added to the panel, drawings like
		// connecting likes are added to the canvas. Both are same size, canvas
		// in panel
		//canvas = new DrawingArea(panel.getOffsetWidth(),
		//		panel.getOffsetHeight());
		//panel.add(canvas);
		//this.panel = panel;

		int center = this.getWidth() / 2;
		rowHeight = this.getHeight() / 10;
		childWidth = center / 5;

		panel.add(this);
		panel.add(jTree.getRoot().toMathML(), center, 0);
		drawChildren(jTree.getRoot(), center, (byte) 1);

	}

	private void drawChildren(JohnNode pNode, int parentX, byte layer) {

		List<JohnNode> kids = pNode.getChildren();

		// The distance from the center of the parent node to the farthest edge
		// of the child
		int offset = pNode.getChildCount() * childWidth / 2;
		int layerHeight = 2 * rowHeight * layer;

		for (JohnNode child : kids) {
			HTML childHTML = child.toMathML();
			panel.add(childHTML, (parentX - offset), layerHeight);

			int childLeft = childHTML.getAbsoluteLeft()-panel.getAbsoluteLeft();
			int childTop = childHTML.getAbsoluteTop() - panel.getAbsoluteTop();
			
			int pad = 5;
			int lineX = childLeft + childHTML.getOffsetWidth()/2 + pad;// (childWidth / 2);
			int lineY = childTop;


			Rectangle box = new Rectangle(childLeft - pad,
					childTop,
					childHTML.getOffsetWidth() + 2 * pad,
					childHTML.getOffsetHeight() * 4 / 3);
			Line line = new Line(parentX, layerHeight-rowHeight, lineX, lineY);
			this.add(line);
			this.add(box);
			if (child.getChildCount() != 0) {
				drawChildren(child, lineX, (byte)(layer+1));
			}
			offset -= (childWidth);
		}

	}
}
