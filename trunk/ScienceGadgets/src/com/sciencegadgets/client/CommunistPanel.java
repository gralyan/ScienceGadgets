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
	
	public void setHorizontal(boolean isHorizontal) {
		
	}

	public void addAll(Widget[] widgets) {
		for (Widget widget : widgets) {
			addToContainer(widget);
		}
		redistribute();
	}

	public void addAll(Iterable<Widget> widgets) {
		for (Widget widget : widgets) {
			addToContainer(widget);
		}
		redistribute();
	}

	@Override
	public void add(Widget widget) {
		addToContainer(widget);
		redistribute();
	}
	
	private void addToContainer(Widget widget){
		widget.setHeight("100%");
		widget.setWidth("100%");
		FlowPanel container = new FlowPanel();
		super.add(container);
		container.add(widget);
		if (isHorizontal) {
			container.addStyleName("layoutRow");
		}
	}

	private void redistribute() {
		int count = this.getWidgetCount();
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
