package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.user.client.Element;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationLayer;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathMLBindingTree;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.MathMLBindingTree.MathMLBindingNode;
import com.sciencegadgets.client.algebra.MathMLBindingTree.Type;

public class EditWrapper extends Wrapper {

	EditMenu editMenu;
	private static ChangeNodeMenu changeNodeMenu = Moderator.changeNodeMenu;

//	public EditWrapper(MathMLBindingNode node, EquationPanel eqPanel,
//			EquationLayer eqLayer, String width, String height) {
//		super(node, eqPanel, eqLayer, width, height);
//
//		editMenu = new EditMenu(this, width);
//		editMenu.setVisible(false);
//
//	}
	public EditWrapper(MathMLBindingNode node, EquationPanel eqPanel,
			EquationLayer eqLayer, Element element) {
		super(node, eqPanel, eqLayer, element);
		
		editMenu = new EditMenu(this, element.getOffsetWidth()+"px");
		editMenu.setVisible(false);
		
	}

	public void select() {
		super.select();
		Type parentType = null;
		if (!"math".equalsIgnoreCase(node.getParent().getTag())) {
			parentType = node.getParent().getType();
		}
		Type nodeType = node.getType();

		// Don't allow sums inside sums or terms in terms
		if (Type.Sum.equals(parentType) || Type.Term.equals(parentType)) {
			changeNodeMenu.setEnable(parentType, false);
		}

		// Display editMenu if this wrapper has been set(not ???)
		if (!ChangeNodeMenu.NOT_SET.equals(node.getSymbol())) {
			editMenu.setVisible(true);
			// editMenu.setFocus();
			// Disable menu options of the selected type
			changeNodeMenu.setEnable(nodeType, false);
		}

		if (!Type.Operation.equals(nodeType)) {
			changeNodeMenu.setVisible(true);
		}
	}

	public void unselect() {
		super.unselect();
		changeNodeMenu.setVisible(false);

		editMenu.setVisible(false);

		if (editMenu.getResponse() != null)
			editMenu.getResponse().removeFromParent();

		for (Type type : Type.values()) {
			changeNodeMenu.setEnable(type, true);
		}
	}

	public EditMenu getEditMenu() {
		return editMenu;
	}

}
