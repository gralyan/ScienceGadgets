package com.sciencegadgets.client.equationtree;

import java.util.LinkedList;
import java.util.List;

import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationtree.JohnTree.JohnNode;
import com.sciencegadgets.client.equationtree.JohnTree.Type;

public class DropControllAssigner {

	public DropControllAssigner(LinkedList<MLElementWrapper> wrappers,
			Boolean hasJoiner) {
		assign(wrappers, hasJoiner);
	}

	public static void assign(LinkedList<MLElementWrapper> wrappers,
			Boolean hasJoiner) {

		JohnNode jNode;

		for (MLElementWrapper wrap : wrappers) {
			jNode = wrap.getJohnNode();

			switch (jNode.getType()) {
			case Number:
			case Variable: 
				
				List<JohnNode> siblings = jNode.getParent().getChildren();
				DropType dropType = null;

				switch (jNode.getParent().getType()) {
				case Series:
					dropType = DropType.Add;
					break;
				case Term:
					dropType = DropType.Multiply;
					break;
				}

				for (JohnNode sib : siblings) {
					if (Type.Number.equals(sib.getType()) && !jNode.equals(sib)) {

						wrap.addDropTarget(sib.getWrapper(), dropType);
						if (hasJoiner) {
							wrap.getJoinedWrapper().addDropTarget(
									sib.getWrapper().getJoinedWrapper(),
									dropType);
						}
					}
				}
				break;

			}
		}
	}

	public static enum DropType {
		Add, Multiply;
	}
}
