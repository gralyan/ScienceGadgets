package com.sciencegadgets.client;

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

	public CommunistPanel(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	public void addAll(Widget[] widgets) {
		for (Widget widget : widgets) {
			add(widget);
		}
		redistribute();
	}

	public void addAll(Iterable<? extends Widget> widgets) {
		for (Widget widget : widgets) {
			add(widget);
		}
		redistribute();
	}

	@Override
	public void add(Widget widget) {
		super.add(widget);
		redistribute();
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
