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
package com.sciencegadgets.client;

import com.google.gwt.dom.client.Element;

public class JSNICalls {

	private static long lastTime = 0;

	public static void TIME_ELAPSED(String placementMessage) {
		long current = System.currentTimeMillis();
		long elapsed = (current - lastTime);
		if (elapsed > 50) {
			JSNICalls.warn(placementMessage + " " + elapsed);
		}
		lastTime = current;
	}

	public static native double getElementWidth(Element elm) /*-{

		return elm.getBoundingClientRect().width;
		//		return elm.getBBox().width;
	}-*/;

	public static native double getElementHeight(Element elm) /*-{
		return elm.getBoundingClientRect().height;
		//		return elm.getBBox().height;
	}-*/;

	public static native void setAttributeNS(Element element, String nameSpace,
			String attribute, String value)/*-{
		element.setAttributeNS(nameSpace, attribute, value)
	}-*/;

	public static native Element createElementNS(final String ns,
			final String name)/*-{
		return document.createElementNS(ns, name);
	}-*/;

	public static native void log(String message) /*-{
		console.log("jg: " + message);
	}-*/;

	public static native void debug(String message) /*-{
		console.debug("jg: " + message);
	}-*/;

	public static native void warn(String message) /*-{
		console.warn("jg: " + message);
	}-*/;

	public static native void error(String message) /*-{
		console.error("jg: " + message);
	}-*/;

	public static native String elementToString(Element element) /*-{
		if (element.outerHTML) {
			return element.outerHTML;
		} else if (typeof (XMLSerializer) !== 'undefined') {
			var serializer = new XMLSerializer();
			return serializer.serializeToString(element);
		} else if (element.xml) {
			return element.xml;
		}
	}-*/;

}
