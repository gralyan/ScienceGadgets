package com.sciencegadgets.server;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.QuantityKind;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;

@SuppressWarnings("serial")
public class DatabaseHelperImpl extends RemoteServiceServlet implements
		DatabaseHelper {

	static {
		ObjectifyService.register(Equation.class);
		ObjectifyService.register(Unit.class);
		ObjectifyService.register(QuantityKind.class);
	}

	@Override
	public String saveEquation(String mathML, String html)
			throws IllegalArgumentException {
		ArrayList<Key<QuantityKind>> quantityKinds = new ArrayList<Key<QuantityKind>>();
		try {
			Document doc = DocumentBuilderFactory.newInstance()
					.newDocumentBuilder()
					.parse(new InputSource(new StringReader(mathML)));
			NodeList variables = doc.getElementsByTagName(TypeML.Variable
					.getTag());

			for (int i = 0; i < variables.getLength(); i++) {
				Element var = (Element) variables.item(i);
				String quantityKind = var.getAttribute(MathAttribute.Unit
						.getName());
				if (quantityKind != null && !"".equals(quantityKind)) {
					Key<QuantityKind> qKey = Key.create(QuantityKind.class,
							quantityKind);
					quantityKinds.add(qKey);
				}
			}

			Equation e = new Equation(mathML, html, quantityKinds);
			ObjectifyService.ofy().save().entity(e).now();
			return e.getMathML();

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public Equation[] getAlgebraEquations() throws IllegalArgumentException {
		List<Equation> eqList = ObjectifyService.ofy().load()
				.type(Equation.class).list();
		// .type(Equation.class).limit(20).list();
		return eqList.toArray(new Equation[eqList.size()]);
	}

	@Override
	public Unit[] getUnitsByQuantity(String quantityKind)
			throws IllegalArgumentException {
		List<Unit> list = ObjectifyService.ofy().load().type(Unit.class)
				.ancestor(Key.create(QuantityKind.class, quantityKind)).list();
		return list.toArray(new Unit[list.size()]);
	}

	@Override
	public Equation[] getEquationsWithQuantities(List<String> quantityKinds)
			throws IllegalArgumentException {
		ArrayList<Key<QuantityKind>> qkindKeys = new ArrayList<Key<QuantityKind>>();
		for (String quantityKind : quantityKinds) {
			qkindKeys.add(Key.create(QuantityKind.class, quantityKind));
		}
		List<Equation> list = ObjectifyService.ofy().load()
				.type(Equation.class).filter("quantityKinds in", qkindKeys)
				.list();
		return list.toArray(new Equation[list.size()]);
	}

	@Override
	public Set<String> getQuantityKinds() {
		List<QuantityKind> list = ObjectifyService.ofy().load()
				.type(QuantityKind.class).order("id").list();

		Set<String> set = new LinkedHashSet<String>();
		for (QuantityKind qKind : list) {
			set.add(qKind.getId());
		}
		return set;
	}

	@Override
	public String getBlobURL() {
		return BlobstoreUtil.getUrl();
	}

}