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
package com.admin.server;

import com.admin.client.AdminService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.sciencegadgets.client.entities.Equation;




@SuppressWarnings("serial")
public class AminServiceImpl extends RemoteServiceServlet implements
			AdminService {
	
	static { 
		ObjectifyService.register( Equation.class );
	}

	@Override
	public String saveEquation(String mathML) throws IllegalArgumentException {
		Equation e = new Equation(mathML, null, null);
		ObjectifyService.ofy().save().entity(e).now();
		return e.getMathML();
	}


		
}
