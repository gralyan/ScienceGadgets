package com.sciencegadgets.server;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.entities.QuantityKind;
import com.sciencegadgets.client.entities.Unit;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.shared.Diagram;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.UnitName;

@SuppressWarnings("serial")
public class DatabaseHelperImpl extends RemoteServiceServlet implements
		DatabaseHelper {

	static {
		ObjectifyService.register(Equation.class);
		ObjectifyService.register(Unit.class);
		ObjectifyService.register(QuantityKind.class);
		ObjectifyService.register(Problem.class);
	}
	
	public Problem getProblem(long id) {
		Key<Problem> problemKey = Key.create(Problem.class, id);
		return ObjectifyService.ofy().load().key(problemKey).now();
	}
	
	@Override
	public String saveProblem(Problem problem) {
		Key<Problem> problemKey = ObjectifyService.ofy().save().entity(problem).now();
		return problemKey.getString();
	}

	@Override
	public String getUnitQuantityKindName(String unitName) {
		String parent = new UnitName(unitName).getQuantityKind();
		Key<QuantityKind> parentKey = Key.create(QuantityKind.class, parent);
		Unit unit = ObjectifyService.ofy().load().type(Unit.class).parent(parentKey)
				.id(unitName).now();
		return unit.getQuantityKindName();
	}

	@Override
	public String newProblem(String title, String description,
			Badge requiredBadge,
			Diagram diagram, Equation equation, String toSolveID) {

		Problem problem = new Problem(title, description, requiredBadge,
				equation, diagram, toSolveID);
		Key<Problem> problemKey = ObjectifyService.ofy().save().entity(problem).now();
		return problemKey.getString();
	}

	@Override
	public Equation saveEquation(String mathXML, String html) {

		try {

			ArrayList<Key<QuantityKind>> quantityKinds = new ArrayList<Key<QuantityKind>>();
			
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
					.parse(new InputSource(new StringReader(mathXML)));
			
			NodeList variables = doc.getElementsByTagName(TypeSGET.Variable.getTag());
			
			if(variables == null) {
				return null;
			}

			for (int i = 0; i < variables.getLength(); i++) {
				
				Element var = (Element) variables.item(i);
				String quantityKind = var.getAttribute(MathAttribute.Unit
						.getAttributeName());
				if (quantityKind != null && !"".equals(quantityKind)) {
					Key<QuantityKind> qKey = Key.create(QuantityKind.class,
							quantityKind);
					quantityKinds.add(qKey);
				}
			}
			
			Equation eq = new Equation(mathXML, html, quantityKinds);
			ObjectifyService.ofy().save().entity(eq).now();
			return eq;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Equation[] getAlgebraEquations() throws IllegalArgumentException {
		List<Equation> eqList = ObjectifyService.ofy().load()
				.type(Equation.class).list();
		return eqList.toArray(new Equation[eqList.size()]);
	}

	@Override
	public LinkedList<Unit> getUnitsAll()
			throws IllegalArgumentException {
		List<Unit> list = ObjectifyService.ofy().load().type(Unit.class).list();
		LinkedList<Unit> linkedList = new LinkedList<Unit>();
		for(Unit u : list) {
			u.removeQuantityKindKey();
			linkedList.add(u);
		}
		return linkedList;
	}

	@Override
	public LinkedList<Unit> getUnitsByQuantity(String quantityKind)
			throws IllegalArgumentException {
		List<Unit> list = ObjectifyService.ofy().load().type(Unit.class)
				.ancestor(Key.create(QuantityKind.class, quantityKind)).list();
		LinkedList<Unit> linkedList = new LinkedList<Unit>();
		for(Unit u : list) {
			u.removeQuantityKindKey();
			linkedList.add(u);
		}
		return linkedList;
	}

	@Override
	public Equation[] getEquationsWithQuantities(ArrayList<String> quantityKinds)
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
	public ArrayList<Problem> getProblemsByBadge(Badge badge) {
		List<Problem> probs = ObjectifyService.ofy().load().type(Problem.class)
				.filter("requiredBadge =", badge).list();
		ArrayList<Problem> problems = new ArrayList<Problem>();
		problems.addAll(probs);
		return problems;
	}

	@Override
	public ArrayList<Problem> getProblemsByBadges(HashSet<Badge> badges) {
		List<Problem> probs;
		if(badges == null) {
			probs = ObjectifyService.ofy().load().type(Problem.class)
					.list();
		}else {
		 probs = ObjectifyService.ofy().load().type(Problem.class)
				.filter("requiredBadge in", badges).list();
		}
		ArrayList<Problem> problems = new ArrayList<Problem>();
		problems.addAll(probs);
		return problems;
	}

	@Override
	public LinkedList<String> getQuantityKinds() {
		List<QuantityKind> list = ObjectifyService.ofy().load()
				.type(QuantityKind.class)// .order("id")
				.list();

		LinkedList<String> linkedList = new LinkedList<String>();
		for (QuantityKind qKind : list) {
			linkedList.add(qKind.getId());
		}
		return linkedList;
	}

	@Override
	public String getBlobURL() {
		return BlobstoreUtil.getUrl();
	}

	@Override
	public void reCreateUnits() {
		new OwlMiner().recreateUnitsFromOwl();
	}

}