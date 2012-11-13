package com.sciencegadgets.client;

import com.google.gwt.dom.client.Element;


public class JSNICalls {

	public static native void parseMathJax(String areaId) /*-{
		$doc.prettify(areaId);
	}-*/;
	
	public static native void parseMathJax(Element element) /*-{
		$doc.prettify(element);
	}-*/;

	public static native double getElementWidth(Element elm) /*-{

		return elm.getBoundingClientRect().width;
		//		return elm.getBBox().width;
	}-*/;

	public static native double getElementHeight(Element elm) /*-{
		return elm.getBoundingClientRect().height;
		//		return elm.getBBox().height;
	}-*/;
}
