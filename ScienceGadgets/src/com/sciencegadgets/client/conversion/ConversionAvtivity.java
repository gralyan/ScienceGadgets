package com.sciencegadgets.client.conversion;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.UnitSelection;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.EquationLayer;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.entities.DataModerator;

public class ConversionAvtivity extends AbsolutePanel {
	
	private MathTree mTree = null;
	private EquationHTML eqHTML = null;
	MathNode node = null;
	
	public void reload(MathNode node){
		this.clear();
		
		this.node = node;
		
		Node nodeCloneLeft = node.getMLNode().cloneNode(true);
		Node nodeCloneRight = node.getMLNode().cloneNode(true);
		Element equationNode = (new HTML("<math><mo>=</mo></math>")).getElement().getFirstChildElement();
		equationNode.insertFirst(nodeCloneLeft);
		equationNode.appendChild(nodeCloneRight);
		
		mTree = new MathTree(equationNode, false);
		
	}
	@Override
	protected void onLoad() {
		super.onLoad();
		eqHTML = mTree.getHTMLAlgOut();
		eqHTML.autoFillParent = true;
		
		String quantityKind = UnitUtil.getQuantityKind(node.getUnitAttribute());
		UnitSelection unitSelect = new UnitSelection(quantityKind);
		Element unitSelectElement = unitSelect.getElement();
		unitSelectElement.setAttribute("id", "conversionUnitSelect");
		mTree.getLeftSide().getHTMLAlgOut().appendChild(unitSelect.getElement());
		
		this.add(eqHTML);
		
	}
}
