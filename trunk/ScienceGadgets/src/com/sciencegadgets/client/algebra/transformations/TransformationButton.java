package com.sciencegadgets.client.algebra.transformations;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.FitParentHTML;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;

public class TransformationButton extends SimplePanel implements
		HasClickHandlers {

	FitParentHTML buttonHTML;
	private MathNode node;

	public TransformationButton() {
		super();
		addStyleName(CSS.TRANSFORMATION_BUTTON + " " + CSS.LAYOUT_ROW);
	}

	public TransformationButton(String html) {
		this();
		setHTML(html);
	}

	public void setHTML(String html) {
		clear();
		buttonHTML = new FitParentHTML(html);
		buttonHTML.percentOfParent = 85;
		add(buttonHTML);
	}

	public String getHTML() {
		return buttonHTML.getHTML();
	}
	
	/**
	 * @return The HTML display version of the transformation
	 */
	public String getPreview() {
		Element savedNode = node.getParent().getXMLNode();
		System.out.println("savedNode "+savedNode.getString());
		MathTree mTree = new MathTree(false);
		mTree.getLeftSide().replace(mTree.NEW_NODE(savedNode));
		System.out.println("mTree "+mTree.getRoot());
		
		
//		TransformationList transorms = new TransformationList(mTree.getNodeById(node.getId()));
//		System.out.println(" ");
//		System.out.println("node "+node);
//		System.out.println("html "+this.getHTML());
//		System.out.println("this "+this.getClass().getName());
//		for(TransformationButton tButton :transorms) {
//			System.out.println("tbutt "+tButton.getClass().getName());
//			if(tButton.getClass().getName().equals(this.getClass().getName())) {
//				return "PREVIEW";
////				tButton.fireEvent(new ClickEvent() {});
//			}
//		}
		
		return "prev";
	}
	

	@Override
	public HandlerRegistration addClickHandler(ClickHandler handler) {
		return addDomHandler(handler, ClickEvent.getType());
	}
}
