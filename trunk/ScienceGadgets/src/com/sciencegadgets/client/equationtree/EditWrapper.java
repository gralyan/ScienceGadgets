package com.sciencegadgets.client.equationtree;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.algebramanipulation.Moderator;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.Type;

public class EditWrapper extends HTML {

	MathMLBindingNode node;
	static EditWrapper selectedWrapper;
	private EditWrapper thisWrapper = this;
	EditMenu editMenu;
	EquationPanel eqList;
	private static ChangeNodeMenu changeNodeMenu = Moderator.changeNodeMenu;

	public EditWrapper(MathMLBindingNode node, EquationPanel eqList,
			String width, String height) {

		setWidth(width);
		setHeight(height);

		this.node = node;
		this.eqList = eqList;
		this.selectedWrapper = (EditWrapper) eqList.selectedWrapper;

		editMenu = new EditMenu(this, width);
		editMenu.setVisible(false);

		addClickHandler(new EditWrapperClickHandler());
		addMouseOverHandler(new EditWrapperMouseOverHandler());
		addMouseOutHandler(new EditWrapperMouseOutHandler());
		addTouchStartHandler(new EditWrapperTouchHandler());

	}

	void select(Boolean select) {
		Type parentType = node.getParent().getType();
		Type nodeType = node.getType();

		if (select) {

			if (selectedWrapper != null) {
				if(this.equals(selectedWrapper)){
					Window.alert("double");
				}else{
					((EditWrapper) selectedWrapper).select(false);
				}
			}

			selectedWrapper = this;
			this.getElement().setAttribute("id", "selectedWrapper");

			// Don't allow sums inside sums or terms in terms
			if (Type.Sum.equals(parentType) || Type.Term.equals(parentType)) {
				changeNodeMenu.setEnable(parentType, false);
			}

			// Display editMenu if this wrapper has been set(not ???)
			if (!ChangeNodeMenu.NOT_SET.equals(node.getSymbol())) {
				editMenu.setVisible(true);
				editMenu.setFocus();
				// Disable menu options of the selected type
				changeNodeMenu.setEnable(nodeType, false);
			}

			if (!Type.Operation.equals(nodeType)) {
				changeNodeMenu.setVisible(true);
			}

		} else {// unselect

			changeNodeMenu.setVisible(false);

			editMenu.setVisible(false);

			selectedWrapper = null;
			this.getElement().removeAttribute("id");

			if (editMenu.getDisplay() != null)
				editMenu.getDisplay().removeFromParent();

			for (Type type : Type.values()) {
				changeNodeMenu.setEnable(type, true);
			}
		}
	}

	EditMenu getEditMenu() {
		return editMenu;
	}

	MathMLBindingNode getNode() {
		return node;
	}

	// /////////////////////////////////////////////////////////////////////
	// Inner Classes
	// ////////////////////////////////////////////////////////////////////
	private class EditWrapperClickHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent event) {
			select(true);
		}
	}

	private class EditWrapperTouchHandler implements TouchStartHandler {

		@Override
		public void onTouchStart(TouchStartEvent arg0) {
			select(true);
		}
	}

	private class EditWrapperMouseOverHandler implements MouseOverHandler {
		@Override
		public void onMouseOver(MouseOverEvent event) {
			thisWrapper.setStyleName("hoveredWrapper", true);// setAttribute("id",
																// "hoveredWrapper");
		}
	}

	private class EditWrapperMouseOutHandler implements MouseOutHandler {
		@Override
		public void onMouseOut(MouseOutEvent event) {
			thisWrapper.setStyleName("hoveredWrapper", false);
			// thisWrapper.getElement().removeAttribute("id");
		}
	}
}
