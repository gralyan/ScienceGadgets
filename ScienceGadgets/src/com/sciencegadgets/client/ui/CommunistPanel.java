package com.sciencegadgets.client.ui;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Automatically gives even spacing to all the children and fills this panel
 * completely
 * 
 * 
 */
public class CommunistPanel extends FlowPanel {

	boolean isHorizontal = false;

	public CommunistPanel() {
		this(true);
	}

	public CommunistPanel(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	public void addAll(Widget[] widgets) {
		for (Widget widget : widgets) {
			addComrade(widget);
		}
		redistribute();
	}

	public void addAll(Iterable<? extends Widget> widgets) {
		for (Widget widget : widgets) {
			addComrade(widget);
		}
		redistribute();
	}

	/**
	 * When adding iteratively, consider using the addAll methods for
	 * performance
	 */
	@Override
	public void add(Widget widget) {
		addComrade(widget);
		redistribute();
	}

	private void addComrade(Widget widget) {
		Widget container = widget;
		if (widget instanceof FitParentHTML) {
			container = new FlowPanel();
			((FlowPanel)container).add(widget);
		}
		if (isHorizontal) {
			container.addStyleName(CSS.LAYOUT_ROW);
		}
		super.add(container);
	}

	public void clear() {
		int count = getWidgetCount();
		for (int i = 0; i < count; i++) {
			getWidget(0).removeFromParent();
		}
	}

	protected void redistribute() {
		int count = this.getWidgetCount();
		if (count > 0) {
			int portion = 100 / count;
			for (int i = 0; i < count; i++) {
				if (isHorizontal) {
					getWidget(i).setWidth(portion + "%");
					getWidget(i).setHeight("100%");
				} else {
					getWidget(i).setHeight(portion + "%");
					getWidget(i).setWidth("100%");
				}
			}
		}
	}

}
