package com.admin.server;

import com.admin.client.AdminService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.sciencegadgets.client.entities.Equation;




@SuppressWarnings("serial")
public class AminServiceImpl extends RemoteServiceServlet implements
			AdminService {
	
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


		
}
