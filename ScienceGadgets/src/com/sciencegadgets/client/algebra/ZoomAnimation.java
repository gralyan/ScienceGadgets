package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.sciencegadgets.shared.TypeSGET;

/**
 * Animation for zooming into and out of sub-expressions within an equation.
 * Only the sub-expression (child) is animated, the parent only changes in
 * visibility during the animation.
 *
 */
class ZoomAnimation extends Animation {

	EquationLayer parent;
	EquationLayer child;
	double parentSize;
	double childSize;
	Style childStyle;
	int childX;
	int childY;
	int dx;
	int dy;
	boolean isZoomIn;
	LinkedList<Style> opStyles = new LinkedList<Style>();

	public void setSpecs(EquationLayer parent, EquationLayer child, int dx,
			int dy, boolean isZoomIn) {
		this.parent = parent;
		this.child = child;
		this.dx = dx;
		this.dy = dy;
		this.isZoomIn = isZoomIn;

		EquationHTML childHTML = child.getEqHTML();
		this.childX = childHTML.getAbsoluteLeft() - child.getAbsoluteLeft();
		this.childY = childHTML.getAbsoluteTop() - child.getAbsoluteTop();
		this.childStyle = childHTML.getElement().getStyle();

		String parentSizeString = parent.getEqHTML().getElement().getStyle()
				.getFontSize().replace("%", "");
		String childSizeString = childStyle.getFontSize().replace("%", "");
		try {
			parentSize = Double.parseDouble(parentSizeString);
			childSize = Double.parseDouble(childSizeString);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			return;
		}

		for (Wrapper wrap : child.getWrappers()) {
			if (TypeSGET.Operation.equals(wrap.getNode().getType())) {
				opStyles.add(wrap.getElement().getStyle());
			}
		}

		if (isZoomIn) {
			parent.setVisible(false);
			child.setVisible(true);
		}
	}

	@Override
	protected void onUpdate(double progress) {
		double prog = isZoomIn ? 1 - progress : progress;
		double progInv = 1 - prog;

		double newSize = ((parentSize - childSize) * prog) + childSize;
		childStyle.setFontSize(newSize, Unit.PCT);

		childStyle.setLeft(childX + (prog * dx), Unit.PX);
		childStyle.setTop(childY + (prog * dy), Unit.PX);

		for (Style opStyle : opStyles) {
			opStyle.setProperty("padding", "0 " + progInv + "em 0 " + progInv
					+ "em");
		}
	}

	@Override
	protected void onComplete() {
		super.onComplete();

		childStyle.setFontSize(childSize, Unit.PCT);

		childStyle.setLeft(childX, Unit.PX);
		childStyle.setTop(childY, Unit.PX);

		for (Style opStyle : opStyles) {
			opStyle.clearPadding();
		}

		if (!isZoomIn) {
			parent.setVisible(true);
			child.setVisible(false);
		}

	}

}