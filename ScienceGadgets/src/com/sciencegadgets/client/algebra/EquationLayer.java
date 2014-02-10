package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

public class EquationLayer extends SimplePanel {
	
	LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();
	EquationLayer parentLayer;
	AbsolutePanel ContextMenuPanel = new AbsolutePanel();
	MathNode mathNode;
	EquationHTML eqHTML;
	private String layerId;

	public EquationLayer(MathNode mathNode, EquationHTML eqHTML) {
		super();
		this.mathNode = mathNode;
		this.eqHTML = eqHTML;

		if(mathNode != null) {
		layerId=mathNode.getId();
		eqHTML.getElement().setAttribute("id", EquationPanel.EQ_OF_LAYER + layerId);
		replaceChildsId(eqHTML.getElement());
		addStyleName(CSS.EQ_LAYER);
		eqHTML.autoFillParent = true;
		}else {
			eqHTML.pilot = true;
		}
	}
	
	@Override
	protected void onLoad() {
		super.onLoad();
		this.add(eqHTML);
	}

	public void setOpacity(double opacity) {
		getElement().getStyle().setOpacity(opacity);
		ContextMenuPanel.getElement().getStyle().setOpacity(opacity);
	}

	void setParentLayer(EquationLayer parentLayer) {
		this.parentLayer = parentLayer;
	}

	public EquationLayer getParentLayer() {
		return parentLayer;
	}
	
	public LinkedList<Wrapper> getWrappers() {
		return wrappers;
	}
	
	public void addWrapper(Wrapper wrap) {
		wrappers.add(wrap);
	}
	
	public AbsolutePanel getContextMenuPanel(){
		return ContextMenuPanel;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		ContextMenuPanel.setVisible(visible);
	}

	/**
	 * Each equation must have a different set of ID's which only differ in the
	 * prefix. The prefix is the equations placement in the list
	 * 
	 */
	private void replaceChildsId(Element curEl) {

		// Element curEl = (Element) (parent.getChild(i));
		String oldId = curEl.getId();

		// Each wrapper has a reference to its MathNode and Layer
		// Wrapper-[equation id]-ofLayer-[MathML node id]
		// example: Wrapper-ML1-ofLayer-ML1
		if (oldId != null) {
			if(oldId.contains(EquationPanel.OF_LAYER)) {
				curEl.setAttribute("id", oldId.split(EquationPanel.OF_LAYER)[0] + EquationPanel.OF_LAYER + layerId);
			}else if (oldId.contains("ML")) {
				curEl.setAttribute("id", oldId + EquationPanel.OF_LAYER + layerId);
//			} else if (oldId.contains("Root")) {
//				curEl.setAttribute("id", "Root-ofLayer-" + layerId);
			}
		}

		if (curEl.getChildCount() > 0) {
			for (int i = 0; i < curEl.getChildCount(); i++) {
				if (Node.ELEMENT_NODE == curEl.getChild(i).getNodeType()) {
					replaceChildsId((Element) curEl.getChild(i));
				}
			}
		}
	}

	public EquationLayer clone(MathNode node) {
		return new EquationLayer(node, eqHTML.clone());
	}
}
