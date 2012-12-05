package com.sciencegadgets.client.equationtree;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public class Backgrounds {

	private static final String NAMESPACE_SVG = "http://www.w3.org/2000/svg";
	private static final String NAMESPACE_XLINK = "http://www.w3.org/1999/xlink";
	private CoordinateConverter coordinateConverter;

	public Backgrounds(Element defsContainer, CoordinateConverter coordinateConverter) {

		this.coordinateConverter = coordinateConverter;
		
		//create definition library id: SG_SVG_defs
		Element defsSvg = createSvgElement("svg");
		Element defs = createSvgElement("defs");
		setSVGAttributeOf(defs, "id", "SG_SVG_defs");
		defsSvg.appendChild(defs);
		defsContainer.appendChild(defsSvg);

//		Element path = createSvgElement("path");
//		path.setAttribute("id", "xxxx");
//		path.setAttribute("stroke-width", "10");
//		path.setAttribute("d",  
//				"M56 237T56 250T70 270H369V420L370 570Q380 583 389 583Q402 583 409 568V270H707Q722 262 722 250T707 230H409V-68Q401 -82 391 -82H389H387Q375 -82 369 -68V230H70Q56 237 56 250Z"
//				);
//		defs.appendChild(path);
		
		
//		Element image = createSvgElement("image");
//		image.setAttribute("id", "xxxx");
//		image.setAttribute("width", "100");
//		image.setAttribute("height", "100");
//		setSVGAttributeOf(image, "xlink:href", "images/images.jpeg");
//		defs.appendChild(image);
		
		Element rect = createSvgElement("rect");
		rect.setAttribute("id", "xxxx");
		rect.setAttribute("width", "100");
		rect.setAttribute("height", "100");
		rect.setAttribute("fill", "red");
		defs.appendChild(rect);
		
		
	}

	public void addBackground(Element mainG, double widthGlobal, double heightGlobal) {
//		double widthSVG = coordinateConverter.XsvgPerGlobal*widthGlobal;
//		double heightSVG = coordinateConverter.YsvgPerGlobal*heightGlobal;
//
//		double scaleX = widthSVG/100;
//		double scaleY = heightSVG/100;
//		
//		System.out.println("heightGlobal"+heightGlobal);
//		System.out.println("scaleY"+scaleY);
//		System.out.println("");

		Element backUse = createSvgElement("use");
		setSVGAttributeOf(backUse, "href", "#xxxx");
//		backUse.setAttribute("transform", "scale("+scaleX+" "+1+")");
		backUse.setAttribute("width", mainG.getFirstChildElement().getOffsetWidth()+"");
		backUse.setAttribute("height", mainG.getFirstChildElement().getOffsetHeight()+"");
//		backUse.setAttribute("width", widthSVG+"");
//		backUse.setAttribute("height", heightSVG+"");
		
//		Element backG = createSvgElement("g");
//		backG.setAttribute("transform", "scale("+scaleX+" "+scaleY+")");
//		backG.appendChild(backUse);

//		mainG.appendChild(backG);
		mainG.appendChild(backUse);
	}

	public void setSVGAttributeOf(Element element, String attribute,
			String value) {
		JSNICalls.setAttributeNS(element, NAMESPACE_XLINK, attribute, value);
	}

	public Element createSvgElement(String name) {
		return JSNICalls.createElementNS(NAMESPACE_SVG, name);
	}

}
