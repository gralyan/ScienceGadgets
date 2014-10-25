package com.sciencegadgets.client.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;

@Entity
public class Equation implements Serializable {

	private static final long serialVersionUID = 8904253167706595238L;

	@Id
	Long id;

	@Index
	List<Key<QuantityKind>> quantityKinds;

	String mathML;
	String html;

	public Equation() {
	}

	public Equation(String mathML, String html,
			List<Key<QuantityKind>> quantityKinds) {
		this.mathML = mathML;
		this.html = html;
		this.quantityKinds = quantityKinds;

	}

	public static Document PARSE_DOCUMENT(String mathXML) throws Exception {
		return XMLParser.parse(mathXML);
	}

	public NodeList getVariables() throws Exception {
		return FIND_VARIABLES(mathML);
	}

	public static NodeList FIND_VARIABLES(String mathXML) throws Exception {
		return PARSE_DOCUMENT(mathXML).getElementsByTagName(
				TypeSGET.Variable.getTag());
	}
	
	public boolean isSolved() {
		try {
			return getVarIdIfSolved() != null;
		}catch (Exception e) {
			return false;
		}
	}
/**
 * Checks if the equation is solved, (in the form [variable = number]). 
 * @return variable id - If solved, the variable ID is returned. If not solved, null is returned.
 * @throws Exception
 */
	public String getVarIdIfSolved() throws Exception {
		Document doc = PARSE_DOCUMENT(mathML);
		Node eqRootNode = doc.getFirstChild();
		NodeList elements = eqRootNode.getChildNodes();

		ArrayList<String> tagsRequired = new ArrayList<String>();
		tagsRequired.add(TypeSGET.Operation.getTag());
		tagsRequired.add(TypeSGET.Variable.getTag());
		tagsRequired.add(TypeSGET.Number.getTag());

		for (int i = 0; i < elements.getLength(); i++) {
			Element el = (Element) elements.item(i);
			boolean containedTag = tagsRequired.remove(el.getTagName());
			if (!containedTag) {
				return null;
			}
		}

		if (TypeSGET.Number.getTag().equals(elements.item(0).getNodeName())) {
			eqRootNode.appendChild(elements.item(1));
			eqRootNode.appendChild(elements.item(0));
			mathML = eqRootNode.toString();
			reCreateHTML();
		}
		
		Element var = (Element)elements.item(0);
		return var.getAttribute(MathAttribute.ID.getAttributeName());
	}
	
	public void reCreateHTML() {
		html = JSNICalls.elementToString(new EquationTree(new HTML(mathML).getElement().getFirstChildElement(), false).getDisplayClone()
				.getElement());
	}
	
	public void reCreate(EquationTree eTree) {
		mathML = eTree.getEquationXMLString();
		html = JSNICalls.elementToString(eTree.getDisplayClone().getElement());
	}

	public void setMathML(String mathML) {
		this.mathML = mathML;
	}
	public String getMathML() {
		return mathML;
	}

	public String getHtml() {
		return html;
	}

	public List<Key<QuantityKind>> getQuantityKinds() {
		return quantityKinds;
	}
}
