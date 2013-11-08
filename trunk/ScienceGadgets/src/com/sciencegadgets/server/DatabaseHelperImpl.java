package com.sciencegadgets.server;

import java.io.File;
import java.io.IOException;
//import java.io.StringWriter;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Unit;

@SuppressWarnings("serial")
public class DatabaseHelperImpl extends RemoteServiceServlet implements
		DatabaseHelper {

	static {
		ObjectifyService.register(Equation.class);
	}

	@Override
	public String saveEquation(String name) throws IllegalArgumentException {
		Equation e = new Equation();
		e.setXML(name);

		ObjectifyService.ofy().save().entity(e).now();

		return e.getXML();
	}

	@Override
	public String[] getUnits() {
		return null;

	}

	@Override
	public String[] saveUnit(Unit unit) {
		return null;

	}

	@Override
	public String[] getAlgebraEquations() throws IllegalArgumentException {

		List<Equation> eqList = ObjectifyService.ofy().load()
				.type(Equation.class).list();
		// .type(Equation.class).limit(20).list();

		int listSize = eqList.size();
		String[] eqArray = new String[listSize];
		for (int i = 0; i < listSize; i++) {
			eqArray[i] = eqList.get(i).getXML();
		}

		return eqArray;
	}

	@Override
	public String[] getUnitsFromOwl() throws IllegalArgumentException {
		Document doc = getDoc("Data/qudt-1.0/unit.owl");
		NodeList eqNodes = doc.getElementsByTagName("*");

		String[] eqStrings = new String[eqNodes.getLength()];
		System.out.println("listLength: " + eqNodes.getLength());
		for (int i = 0; i < eqNodes.getLength(); i++) {
			Element node = (Element) eqNodes.item(i);
			if (node.getNodeName().endsWith("Unit")) {
				System.out.println("getNodeName: " + node.getNodeName());

				System.out.println("rdfs:label: "
						+ extractProperty(node, "rdfs:label"));
				System.out.println("qudt:abbreviation: "
						+ extractProperty(node, "qudt:abbreviation"));
				System.out.println("qudt:symbol: "
						+ extractProperty(node, "qudt:symbol"));
				System.out.println("qudt:conversionOffset: "
						+ extractProperty(node, "qudt:conversionOffset"));
				System.out.println("qudt:conversionMultiplier: "
						+ extractProperty(node, "qudt:conversionMultiplier"));
				
				NodeList a = node.getElementsByTagName("qudt:quantityKind");
				String qKind = null;
				if (a.getLength() > 0) {
					qKind = ((Element) a.item(0)).getAttribute("rdf:resource")
							.replaceFirst(
									"http://data.nasa.gov/qudt/owl/quantity#",
									"");
				}
				System.out.println("qudt:quantityKind: " + qKind);
				System.out.println("");
			}
		}
		return eqStrings;
	}

	private String extractProperty(Element node, String property) {
		NodeList a = node.getElementsByTagName(property);
		if (a.getLength() > 0) {
			return ((Element) a.item(0)).getTextContent();
		} else {
			return null;
		}
	}

	private Document getDoc(String path) {
		Document doc = null;

		try {
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	@Override
	public String[][] getVariables() throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getEquationsByVariables(String[] name)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	// XML version

	// public String[] getEquationsByVariables(String[] vars)
	// throws IllegalArgumentException {
	// Document doc = getDoc(Files.Equations);
	// NodeList nList = doc.getElementsByTagName("mi");
	//
	// HashSet<String> eqs = new HashSet<String>();
	// LinkedList<Node> eqHits = new LinkedList<Node>();
	//
	// // Check every "mi" tag in the equations database to see if it equals
	// // any of the given variables
	// for (int i = 0; i < nList.getLength(); i++) {
	// Node nNode = nList.item(i);
	//
	// if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	//
	// for (String var : vars) {
	// if (var.equals(nNode.getTextContent())) {
	//
	// Node curParent = nNode.getParentNode();
	//
	// // Climb the tree looking for the math tag
	// while (!curParent.getNodeName().equals("math")) {
	// curParent = curParent.getParentNode();
	// }
	// eqHits.add(curParent);
	// }
	// }
	// }
	// }
	// // Add only if equation has all the variables
	// for (Node eqHit : eqHits) {
	// if (Collections.frequency(eqHits, eqHit) == vars.length) {
	// eqs.add(nodeToString(eqHit));
	// }
	// }
	// return eqs.toArray(new String[0]);
	// }

	// public String[][] getVariables() throws IllegalArgumentException {
	//
	// Document doc = getDoc(Files.Variables);
	// NodeList nList = doc.getElementsByTagName("variable");
	// String[][] vars = new String[2][nList.getLength()];
	//
	// for (int i = 0; i < nList.getLength(); i++) {
	// Node nNode = nList.item(i);
	//
	// if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	// vars[0][i] = getTagValue("symbol", (Element) nNode);
	// vars[1][i] = getTagValue("name", (Element) nNode);
	// }
	// }
	//
	// return vars;
	// }
	//
	// private static String getTagValue(String sTag, Element eElement) {
	// NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
	// .getChildNodes();
	//
	// Node nValue = (Node) nlList.item(0);
	//
	// return nValue.getNodeValue();
	// }

	// private Document getDoc(Files file) {
	// return getDoc("Data/" + file.toString() + ".xml");
	// }

	//
	// private String nodeToString(Node node) {
	// System.setProperty("javax.xml.transform.TransformerFactory",
	// "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
	//
	// StringWriter sw = new StringWriter();
	// try {
	// Transformer t = TransformerFactory.newInstance().newTransformer();
	// t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	// t.transform(new DOMSource(node), new StreamResult(sw));
	// } catch (TransformerException te) {
	// System.out.println("nodeToString Transformer Exception");
	// }
	// String xml = sw.toString();
	// xml = xml.replaceAll(">\\s+<", "><");
	// return xml;
	// }
	//
	// public static enum Files {
	// Variables, Equations, AlgebraEquations;
	// }

}