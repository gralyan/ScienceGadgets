package com.sciencegadgets.client.algebra;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasTouchEndHandlers;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;

class AutoSelectWrapper extends HTML implements HasClickHandlers,
		HasTouchEndHandlers {
	private EquationNode node;

	public AutoSelectWrapper(final EquationPanel eqPanel,
			final EquationNode node, Element element) {
		super(element);
		this.node = node;
		onAttach();

		// zIndex eqPanel=1 wrapper=2 menu=3
		this.getElement().getStyle().setZIndex(2);

		if (Moderator.isTouch) {
			addTouchStartHandler(new TouchStartHandler() {
				@Override
				public void onTouchStart(TouchStartEvent event) {
					eqPanel.autoSelectedWrapper = AutoSelectWrapper.this;
				}
			});
		} else {
			addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					eqPanel.autoSelectedWrapper = AutoSelectWrapper.this;
				}
			});
		}
	}

	public Wrapper getWrapper() {
		return node.getWrapper();
	}
}