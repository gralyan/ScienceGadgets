package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.user.client.Element;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.algebra.EquationLayer;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.Type;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

public class EditWrapper extends Wrapper {

	private static ChangeNodeMenu changeNodeMenu = Moderator.changeNodeMenu;

	public EditWrapper(MathNode node, EquationPanel eqPanel,
			 Element element) {
		super(node, eqPanel,  element);
		
		menu = new EditMenu(this, element.getOffsetWidth()+"px");
//		menu.setVisible(false);
		
	}

	public void select() {
		Type parentType = node.getParentType();
		Type nodeType = node.getType();

		// Don't allow sums inside sums or terms in terms
		if (Type.Sum.equals(parentType) || Type.Term.equals(parentType)) {
			changeNodeMenu.setEnable(parentType, false);
		}

		// Display editMenu if this wrapper has been set(not ???)
		if (!ChangeNodeMenu.NOT_SET.equals(node.getSymbol())) {
//			menu.setVisible(true);
			// editMenu.setFocus();
			// Disable menu options of the selected type
			changeNodeMenu.setEnable(nodeType, false);
		}

		if (!Type.Operation.equals(nodeType)) {
			changeNodeMenu.setVisible(true);
		}
		super.select();
	}

	public void unselect() {
		changeNodeMenu.setVisible(false);

//		menu.setVisible(false);

		if (((EditMenu) menu).getResponse() != null)
			((EditMenu) menu).getResponse().removeFromParent();

		for (Type type : Type.values()) {
			changeNodeMenu.setEnable(type, true);
		}
		super.unselect();
	}

	public EditMenu getEditMenu() {
		return (EditMenu) menu;
	}

}
