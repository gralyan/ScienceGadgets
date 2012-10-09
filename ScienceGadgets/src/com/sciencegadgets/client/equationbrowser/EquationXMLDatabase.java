package com.sciencegadgets.client.equationbrowser;

import java.util.HashSet;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class EquationXMLDatabase {

	Document dataDoc;

	public static enum Tags {
		math, mi;
	}

	public EquationXMLDatabase(String data) {
		dataDoc = XMLParser.parse(data);
		XMLParser.removeWhitespace(dataDoc);
	}

	public String[] getAllTagged(Tags tag) {
		NodeList elements = dataDoc.getElementsByTagName(tag.toString());
		
		HashSet<String> set = new HashSet<String>();
		
		for (int i = 0; i < elements.getLength(); i++) {
			String value = elements.item(i).toString();
			set.add(value);
		}
		return set.toArray(new String[0]);
	}

}
