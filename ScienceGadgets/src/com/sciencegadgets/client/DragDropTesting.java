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
package com.sciencegadgets.client;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;

public class DragDropTesting implements EntryPoint {

	private PickupDragController dragC1, dragC2, dragC3, dragC4;
	private HTML box1, box2, box3, box4;

	public void onModuleLoad() {
		AbsolutePanel panel = new AbsolutePanel();
		panel.setWidth("400px");
		panel.setHeight("400px");
		RootPanel.get().add(panel);

		box1 = new HTML("<span>1</span>");
		box2 = new HTML("<span>2</span>");
		box3 = new HTML("<span>3</span>");
		box4 = new HTML("<span>4</span>");

		box1.setSize("100px", "100px");
		box2.setSize("100px", "100px");
		box3.setSize("100px", "100px");
		box4.setSize("100px", "100px");

		box1.setStyleName("var");
		box2.setStyleName("var");
		box3.setStyleName("var");
		box4.setStyleName("var");

		panel.add(box1, 0, 0);
		panel.add(box2, 150, 0);
		panel.add(box3, 0, 150);
		panel.add(box4, 150, 150);

		dragC1 = new PickupDragController((AbsolutePanel) panel, true);
		dragC1.makeDraggable(box1);

		dragC2 = new PickupDragController((AbsolutePanel) panel, true);
		dragC2.makeDraggable(box2);

		dragC3 = new PickupDragController((AbsolutePanel) panel, true);
		dragC3.makeDraggable(box3);

		dragC4 = new PickupDragController((AbsolutePanel) panel, true);
		dragC4.makeDraggable(box4);

		box1.addDomHandler(new ElementOverHandler(), MouseOverEvent.getType());
		box2.addDomHandler(new ElementOverHandler(), MouseOverEvent.getType());
		box3.addDomHandler(new ElementOverHandler(), MouseOverEvent.getType());
		box4.addDomHandler(new ElementOverHandler(), MouseOverEvent.getType());

		box1.addDomHandler(new ElementOutHandler(), MouseOutEvent.getType());
		box2.addDomHandler(new ElementOutHandler(), MouseOutEvent.getType());
		box3.addDomHandler(new ElementOutHandler(), MouseOutEvent.getType());
		box4.addDomHandler(new ElementOutHandler(), MouseOutEvent.getType());

		registerAll();

	}

	class ElementOverHandler implements MouseOverHandler {
		public void onMouseOver(MouseOverEvent event) {
			((HTML) event.getSource()).getElement().setId(CSS.SELECTED_WRAPPER);
		}
	}

	class ElementOutHandler implements MouseOutHandler {
		public void onMouseOut(MouseOutEvent event) {
			((HTML) event.getSource()).getElement().removeAttribute("id");
		}
	}

	class ElementDrop extends AbstractDropController {

		public ElementDrop(Widget dropTarget) {
			super(dropTarget);
		}

		@Override
		public void onDrop(DragContext context) {
			super.onDrop(context);
			// Window.alert("dropped");
			dragC1.unregisterDropControllers();
			dragC2.unregisterDropControllers();
			dragC3.unregisterDropControllers();
			dragC4.unregisterDropControllers();
			context.draggable.removeFromParent();
			registerAll();
		}

		@Override
		public void onEnter(DragContext context) {
			super.onEnter(context);
		}

	}

	public void registerAll() {
		if (box1.isAttached()) {
			Window.alert("1");
			dragC2.registerDropController(new ElementDrop(box1));
			dragC3.registerDropController(new ElementDrop(box1));
			dragC4.registerDropController(new ElementDrop(box1));
		}
		if (box2.isAttached()) {
			Window.alert("2");
			dragC1.registerDropController(new ElementDrop(box2));
			dragC3.registerDropController(new ElementDrop(box2));
			dragC4.registerDropController(new ElementDrop(box2));
		}
		if (box3.isAttached()) {
			dragC1.registerDropController(new ElementDrop(box3));
			dragC2.registerDropController(new ElementDrop(box3));
			dragC4.registerDropController(new ElementDrop(box3));
		}
		if (box4.isAttached()) {
			dragC1.registerDropController(new ElementDrop(box4));
			dragC2.registerDropController(new ElementDrop(box4));
			dragC3.registerDropController(new ElementDrop(box4));
		}
	}
}
