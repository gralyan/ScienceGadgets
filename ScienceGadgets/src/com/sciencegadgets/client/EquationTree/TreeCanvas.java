package com.sciencegadgets.client.EquationTree;

import java.util.LinkedList;
import java.util.List;

import org.vaadin.gwtgraphics.client.DrawingArea;
import org.vaadin.gwtgraphics.client.Line;
import org.vaadin.gwtgraphics.client.shape.Rectangle;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Panel;
import com.sciencegadgets.client.EquationTree.JohnTree.JohnNode;

public class TreeCanvas extends DrawingArea {
	
	private int childWidth;
	private int rowHeight;
	private AbsolutePanel panel;
	private Boolean isLeft;

	// The number of members in each row
	private byte[] leftLayerCounts;
	private byte[] rightLayerCounts;
	
	// These counters aid in placing each member down
	private byte[]	leftCounters;
	private byte[]	rightCounters;
	private int hpad;
	
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
	
	public void draw(JohnTree jTree) {
		
		leftLayerCounts = new byte[20];
		rightLayerCounts = new byte[20];
		leftCounters = new byte[20];
		rightCounters = new byte[20];
		
		// The count in each layer will allow for maximum spacing, index 0 is each side
		leftLayerCounts[0]++;
		rightLayerCounts[0]++;
		getNextLayerCounts(jTree.getLeftSide(), (byte) 1, true);
		getNextLayerCounts(jTree.getRightSide(), (byte) 1, false);
		resizeLayers();
		
		int center = this.getWidth() / 2;
		rowHeight = this.getHeight() / 10;
		hpad = rowHeight;
		//childWidth = center / 5;

		panel.add(this);
		panel.add(jTree.getRoot().toMathML(), center, 0);
		panel.add(jTree.getLeftSide().toMathML(), center/2, rowHeight);
		panel.add(jTree.getRightSide().toMathML(), (center*3/2), rowHeight);
		
		drawChildren(jTree.getLeftSide(), (center/2), (byte) 1, true);
		drawChildren(jTree.getRightSide(), (center*3/2), (byte) 1, false);
		

	}
	public void getNextLayerCounts(JohnNode pNode, byte layer, Boolean isLeft){
		List<JohnNode> children = pNode.getChildren();
	
		for (JohnNode child : children) {
			if(isLeft){
				leftLayerCounts[layer]++;
			}else{
				rightLayerCounts[layer]++;
			
			if (child.getChildCount() != 0) {
				getNextLayerCounts(child, (byte)(layer+1), isLeft);
			}
	}}}

	private void drawChildren(JohnNode pNode, int parentX, byte layer, Boolean isLeft) {

		List<JohnNode> children = pNode.getChildren();

		//int offset = pNode.getChildCount() * childWidth / 2;
		if(isLeft){
			childWidth = (this.getWidth()-3*hpad)/leftLayerCounts[layer];
		}else{
			childWidth = (this.getWidth()-3*hpad)/rightLayerCounts[layer];
		}
		int layerHeight = rowHeight * (layer);

		for (JohnNode child : children) {
			
			HTML childHTML = child.toMathML();
			if(isLeft){
			panel.add(childHTML, (childWidth*(++leftCounters[layer])-childWidth+hpad/*parentX - offset*/), layerHeight);
			}else{
				panel.add(childHTML, ((childWidth*(++rightCounters[layer]))-childWidth +hpad+this.getWidth()/2/*parentX - offset*/), layerHeight);
			}
			int childLeft = childHTML.getAbsoluteLeft()-panel.getAbsoluteLeft();
			int childTop = childHTML.getAbsoluteTop() - panel.getAbsoluteTop();
			
			int pad = 5;
			int lineX = childLeft + childHTML.getOffsetWidth()/2 + pad;
			int lineY = childTop;

			
			Rectangle box = new Rectangle(childLeft - pad,
					childTop,
					childHTML.getOffsetWidth() + 2 * pad,
					childHTML.getOffsetHeight() * 4 / 3);
			Line line = new Line(parentX, layerHeight-rowHeight/2, lineX, lineY);
			this.add(line);
			this.add(box);
			if (child.getChildCount() != 0) {
				drawChildren(child, lineX, (byte)(layer+1), isLeft);
			}
			//offset -= (childWidth);
		}

	}
	private void resizeLayers() {
		for(int i=0 ; i<20; i++){
			System.out.println("Left "+i+": "+leftLayerCounts[i]);
		}for(int i=0 ; i<20; i++){
			System.out.println("Right "+i+": "+rightLayerCounts[i]);
		}
	}
}
