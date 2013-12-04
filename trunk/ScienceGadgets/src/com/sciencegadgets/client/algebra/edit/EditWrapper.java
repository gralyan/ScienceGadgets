package com.sciencegadgets.client.algebra.edit;

import com.google.gwt.user.client.Element;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.shared.TypeML;

public class EditWrapper extends Wrapper {

	public EditWrapper(MathNode node, EquationPanel eqPanel,
			 Element element) {
		super(node, eqPanel,  element);
		
		menu = new EditMenu(this, element.getOffsetWidth()+"px");
//		menu.setVisible(false);
		
	}

	public void select() {
		TypeML parentType = node.getParentType();
		TypeML nodeType = node.getType();

		// Don't allow sums inside sums or terms in terms
		if (TypeML.Sum.equals(parentType) || TypeML.Term.equals(parentType)) {
			AlgebraActivity.changeNodeMenu.setEnable(parentType, false);
		}

		// Display editMenu if this wrapper has been set(not ???)
		if (!ChangeNodeMenu.NOT_SET.equals(node.getSymbol())) {
//			menu.setVisible(true);
			// editMenu.setFocus();
			// Disable menu options of the selected type
			AlgebraActivity.changeNodeMenu.setEnable(nodeType, false);
		}

		if (!TypeML.Operation.equals(nodeType)) {
			AlgebraActivity.changeNodeMenu.setVisible(true);
		}
		super.select();
	}

	public void unselect() {
		AlgebraActivity.changeNodeMenu.setVisible(false);

//		menu.setVisible(false);

		if (((EditMenu) menu).getResponse() != null)
			((EditMenu) menu).getResponse().removeFromParent();

		for (TypeML type : TypeML.values()) {
			AlgebraActivity.changeNodeMenu.setEnable(type, true);
		}
		super.unselect();
	}

	public EditMenu getEditMenu() {
		return (EditMenu) menu;
	}

}
