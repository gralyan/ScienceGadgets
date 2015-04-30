package com.java_workshop.sciencegadgets.client;

import java.util.LinkedList;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

public class Boxes extends FlowPanel {

	public static final String PLUS_CSS = "plus";
	public static final String BOX_CSS = "box";
	
	LinkedList<FlowPanel> boxList = new LinkedList<FlowPanel>();
	private Panel parent;
	private int boxCount;

	/**
	 * Constructor
	 * 
	 * @param parent
	 *            - The panel these boxes live in
	 * @param boxCount
	 *            - How many boxes will there be
	 */
	Boxes(Panel parent, int boxCount) {
		this.parent = parent;
		this.boxCount = boxCount;
	}

	/**
	 * onLoad is used because we want to fire this method when this object is
	 * being added to the parent. This gives us the most recent width
	 */
	@Override
	protected void onLoad() {
		super.onLoad();

		// make width slightly smaller and centered for good looks
		int width = parent.getOffsetWidth() * 4 / 5;
		setWidth(width + "px");

		// create and add boxes
		FlowPanel box;
		int boxWidth = width / (boxCount + 1);

		for (int i = 0; i < boxCount; i++) {
			box = new FlowPanel();
			box.setPixelSize(boxWidth, boxWidth);
			boxList.add((FlowPanel) box);
			box.addStyleName(BOX_CSS);
			this.add(box);
		}
		
		// Pick a box to be the addition, can't be first or last
		int plus_i = (int) (Math.random() * (boxCount - 1)) + 1;
		
		Label plus = new Label("+");
		plus.addStyleName(PLUS_CSS);
		this.insert(plus, plus_i);
	}

	/**
	 * Called once the answer is given
	 * 
	 * @param isCorrect
	 *            - Whether the answer was correct
	 */
	void onAnswer(boolean isCorrect) {
		String color = isCorrect ? "lime" : "red";
		for (FlowPanel box : boxList) {
			box.getElement().getStyle().setBackgroundColor(color);
		}
	}
}
