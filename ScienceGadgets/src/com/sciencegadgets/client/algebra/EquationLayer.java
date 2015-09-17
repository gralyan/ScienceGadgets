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
import java.util.Map.Entry;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.shared.TypeSGET;

public class EquationLayer extends SimplePanel {

	private EquationLayer parentLayer;
	private LinkedList<Wrapper> wrappers = new LinkedList<Wrapper>();
	private EquationNode eqNode;
	private EquationHTML eqHTML;
	public final String layerId;
	private boolean isExpression;

	public EquationLayer(EquationNode mathNode, EquationHTML eqHTML) {
		super();
		this.eqNode = mathNode;
		this.eqHTML = eqHTML;

		if (mathNode != null) {
			layerId = mathNode.getId();
			eqHTML.getElement().setAttribute("id",
					EquationPanel.EQ_OF_LAYER + layerId);
			replaceChildsId(eqHTML.getElement());
			eqHTML.autoFillParent = true;
			addStyleName(CSS.EQ_LAYER);
			isExpression = eqHTML.getElement().getClassName()
					.contains(CSS.EXPRESSION);
		} else {
			layerId = "pilot";
			eqHTML.pilot = true;
		}
	}

	@Override
	protected void onLoad() {
		super.onLoad();
	
		eqHTML.getElement().getStyle().setVisibility(Visibility.HIDDEN);
		add(eqHTML);
		final Style style = eqHTML.getElement().getStyle();

		Scheduler.get().scheduleDeferred(new ScheduledCommand() {

			@Override
			public void execute() {
				
				style.setTop(
						((getOffsetHeight() - eqHTML.getOffsetHeight())) / 2,
						Unit.PX);
				style.setLeft(
						((getOffsetWidth() - eqHTML.getOffsetWidth())) / 2,
						Unit.PX);

				eqHTML.getElement().getStyle().clearVisibility();
			}
		});
	}

	void setParentLayer(EquationLayer parentLayer) {
		this.parentLayer = parentLayer;
	}

	public EquationLayer getParentLayer() {
		return parentLayer;
	}

	public EquationHTML getEqHTML() {
		return eqHTML;
	}

	public LinkedList<Wrapper> getWrappers() {
		return wrappers;
	}

	public EquationNode getEquationNode() {
		return eqNode;
	}

	public void addWrapper(Wrapper wrap) {
		wrappers.add(wrap);
		wrap.setLayer(this);
	}

	public Wrapper getWapperForLayer(EquationLayer eLayer) {
		if (eLayer == null || eLayer.layerId == null
				|| "".equals(eLayer.layerId)) {
			return null;
		}
		for (Wrapper w : wrappers) {
			if (w.getNode() != null
					&& eLayer.layerId.equals(w.getNode().getId())) {
				return w;
			}
		}
		return null;
	}

	@Override
	public void setVisible(boolean visible) {
		Visibility visibility = visible ? Visibility.VISIBLE
				: Visibility.HIDDEN;
		getElement().getStyle().setVisibility(visibility);

		if (visible) {
			Widget eqPanel = getParent();
			if (isExpression) {
				eqPanel.addStyleName(CSS.CAN_ZOOM_OUT);
			} else {
				eqPanel.removeStyleName(CSS.CAN_ZOOM_OUT);
			}
		}
	}

	/**
	 * Each equation must have a different set of ID's which only differ in the
	 * prefix. The prefix is the equations placement in the list <br/>
	 * <br/>
	 * Each wrapper has a reference to its MathNode and Layer<br/>
	 * Wrapper-[equation id]-ofLayer-[MathML node id]<br/>
	 * example: Wrapper-ML1-ofLayer-ML1
	 */
	private void replaceChildsId(Element curEl) {

		// Element curEl = (Element) (parent.getChild(i));
		String oldId = curEl.getId();

		if (oldId != null) {
			if (oldId.contains(EquationPanel.OF_LAYER)) {
				curEl.setAttribute("id", oldId.split(EquationPanel.OF_LAYER)[0]
						+ EquationPanel.OF_LAYER + layerId);
			} else if (oldId.contains(EquationTree.ID_PREFIX)) {
				curEl.setAttribute("id", oldId + EquationPanel.OF_LAYER
						+ layerId);
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

	public EquationLayer clone(EquationNode node, boolean toCloneEntireEquation) {
		if (toCloneEntireEquation || TypeSGET.Equation.equals(node.getType())) {
			return new EquationLayer(node, eqHTML.clone());
		}
		for (Entry<Element, EquationNode> entry : eqHTML.displayMap.entrySet()) {
			if (node == entry.getValue()) {
				return new EquationLayer(node, new EquationHTML(entry.getKey()));
			}
		}
		JSNICalls
				.error("Can't find element for node during EquationLayer clone\nnode: "
						+ node);
		return null;
	}
}
