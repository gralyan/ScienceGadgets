/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.CSS;

public class EquationLayer extends SimplePanel {
	
	LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();
	EquationLayer parentLayer;
	AbsolutePanel ContextMenuPanel = new AbsolutePanel();
	EquationNode mathNode;
	EquationHTML eqHTML;
	private String layerId;
	private Element focusElement;

	public EquationLayer(EquationNode mathNode, EquationHTML eqHTML) {
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
//		addFocusElement();
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
		wrap.setLayer(this);
	}

//	public void addFocusElement() {
//		if(wrappers.isEmpty()) {
//			return;
//		}
//		
//		this.focusElement = wrappers.get(0).getElement().getParentElement();
//		if(focusElement != null && isAttached()) {
//			eqHTML.setFocus(focusElement);
//			int left = focusElement.getAbsoluteLeft()-20;
//			getElement().getStyle().setLeft(-1*left, Unit.PX);
//		}else {
//			GWT.log("isAttached() "+isAttached());
//			GWT.log("focusElement "+focusElement);
//		}
//			
//	}
	
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
			}else if (oldId.contains(EquationTree.ID_PREFIX)) {
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

	public EquationLayer clone(EquationNode node) {
		return new EquationLayer(node, eqHTML.clone());
	}

}
