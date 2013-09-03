package com.sciencegadgets.server;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import com.google.common.collect.Lists;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.LoadType;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.entities.Equation;

@SuppressWarnings("serial")
public class DatabaseHelperImpl extends RemoteServiceServlet implements
		DatabaseHelper {
	
	static { 
		ObjectifyService.register( Equation.class );
	}

	@Override
	public String saveEquation(String name) throws IllegalArgumentException {
		Equation e = new Equation();
		e.setXML(name);
		
		ObjectifyService.ofy().save().entity(e).now();

		return e.getXML();
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

	@Override
	public String[] getAlgebraEquations() throws IllegalArgumentException {
		
		List<Equation> eqList = ObjectifyService.ofy().load()
				.type(Equation.class).list();
//		.type(Equation.class).limit(20).list();
		
		int listSize = eqList.size();
		String[] eqArray = new String[listSize];
		for (int i = 0; i < listSize; i++) {
			eqArray[i] = eqList.get(i).getXML();
		}
		
		return eqArray;
	}

	/*
	 * XML version
	 * 
	 * public String[] getEquationsByVariables(String[] vars) throws
	 * IllegalArgumentException { Document doc = getDoc(Files.Equations);
	 * NodeList nList = doc.getElementsByTagName("mi");
	 * 
	 * HashSet<String> eqs = new HashSet<String>(); LinkedList<Node> eqHits =
	 * new LinkedList<Node>();
	 * 
	 * // Check every "mi" tag in the equations database to see if it equals //
	 * any of the given variables for (int i = 0; i < nList.getLength(); i++) {
	 * Node nNode = nList.item(i);
	 * 
	 * if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	 * 
	 * for (String var : vars) { if (var.equals(nNode.getTextContent())) {
	 * 
	 * Node curParent = nNode.getParentNode();
	 * 
	 * // Climb the tree looking for the math tag while
	 * (!curParent.getNodeName().equals("math")) { curParent =
	 * curParent.getParentNode(); } eqHits.add(curParent); } } } } //Add only if
	 * equation has all the variables for (Node eqHit : eqHits) { if
	 * (Collections.frequency(eqHits, eqHit) == vars.length) {
	 * eqs.add(nodeToString(eqHit)); } } return eqs.toArray(new String[0]); }
	 * 
	 * @Override public String[] getAlgebraEquations() throws
	 * IllegalArgumentException { Document doc = getDoc(Files.AlgebraEquations);
	 * NodeList eqNodes = doc.getElementsByTagName("math");
	 * 
	 * String[] eqStrings = new String[eqNodes.getLength()]; for (int i = 0; i <
	 * eqNodes.getLength(); i++) { eqStrings[i] = nodeToString(eqNodes.item(i));
	 * } return eqStrings; }
	 * 
	 * public String[][] getVariables() throws IllegalArgumentException {
	 * 
	 * Document doc = getDoc(Files.Variables); NodeList nList =
	 * doc.getElementsByTagName("variable"); String[][] vars = new
	 * String[2][nList.getLength()];
	 * 
	 * for (int i = 0; i < nList.getLength(); i++) { Node nNode = nList.item(i);
	 * 
	 * if (nNode.getNodeType() == Node.ELEMENT_NODE) { vars[0][i] =
	 * getTagValue("symbol", (Element) nNode); vars[1][i] = getTagValue("name",
	 * (Element) nNode); } }
	 * 
	 * return vars; }
	 * 
	 * private static String getTagValue(String sTag, Element eElement) {
	 * NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
	 * .getChildNodes();
	 * 
	 * Node nValue = (Node) nlList.item(0);
	 * 
	 * return nValue.getNodeValue(); }
	 * 
	 * private Document getDoc(Files file) { Document doc = null;
	 * 
	 * try { File fXmlFile = new File("Data/" + file.toString() + ".xml");
	 * DocumentBuilderFactory dbFactory = DocumentBuilderFactory .newInstance();
	 * DocumentBuilder dBuilder = dbFactory.newDocumentBuilder(); doc =
	 * dBuilder.parse(fXmlFile); doc.getDocumentElement().normalize();
	 * 
	 * } catch (SAXException e) { e.printStackTrace(); } catch (IOException e) {
	 * e.printStackTrace(); } catch (ParserConfigurationException e) {
	 * e.printStackTrace(); } return doc; }
	 * 
	 * private String nodeToString(Node node) {
	 * System.setProperty("javax.xml.transform.TransformerFactory",
	 * "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
	 * 
	 * StringWriter sw = new StringWriter(); try { Transformer t =
	 * TransformerFactory.newInstance().newTransformer();
	 * t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	 * t.transform(new DOMSource(node), new StreamResult(sw)); } catch
	 * (TransformerException te) {
	 * System.out.println("nodeToString Transformer Exception"); } String xml =
	 * sw.toString(); xml = xml.replaceAll(">\\s+<", "><"); return xml; }
	 * 
	 * public static enum Files { Variables, Equations, AlgebraEquations; }
	 */
}