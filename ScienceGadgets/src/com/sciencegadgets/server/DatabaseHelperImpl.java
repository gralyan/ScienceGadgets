package com.sciencegadgets.server;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.QuantityKind;
import com.sciencegadgets.client.entities.Unit;

@SuppressWarnings("serial")
public class DatabaseHelperImpl extends RemoteServiceServlet implements
		DatabaseHelper {

	static {
		ObjectifyService.register(Equation.class);
		ObjectifyService.register(Unit.class);
		ObjectifyService.register(QuantityKind.class);
	}

	@Override
	public String saveEquation(String mathML, String html) throws IllegalArgumentException {
		ArrayList<Key<QuantityKind>> quantityKinds = new ArrayList<Key<QuantityKind>>();
		mathML = mathML.replace("\"", "");
		String[] parts = mathML.split("data-unit=");
		for (int i = 1; i < parts.length; i++) {
			String quantityKind = parts[i].split(" ")[0];
			System.out.println(quantityKind);
			Key<QuantityKind> qKey = Key.create(QuantityKind.class,
					quantityKind);
			quantityKinds.add(qKey);
		}
		
		Equation e = new Equation(mathML, html, quantityKinds);
		ObjectifyService.ofy().save().entity(e).now();
		return e.getMathML();
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
		for(String quantityKind : quantityKinds){
			qkindKeys.add(Key.create(QuantityKind.class, quantityKind));
		}
		List<Equation> list = ObjectifyService.ofy().load().type(Equation.class)
				.filter("quantityKinds in", qkindKeys).list();
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

}