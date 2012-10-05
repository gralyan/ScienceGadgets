package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class EquationXMLDatabase {

	Document dataDoc;

	public static enum Tags {
		algebra_equation;
	}

	public EquationXMLDatabase(String data) {
		dataDoc = XMLParser.parse(data);
		XMLParser.removeWhitespace(dataDoc);
	}

	public String[] getAll(Tags tag) {
		NodeList elements = dataDoc.getElementsByTagName(tag.toString());
		int tagCount = elements.getLength();
		String[] list = new String[tagCount];
		
		for (int i = 0; i < elements.getLength(); i++) {
			String value = elements.item(i).getFirstChild().toString();
			list[i] = value;
		}
		return list;
	}

}
