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
package com.sciencegadgets.client.entities;

import java.io.Serializable;
import java.util.HashSet;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.shared.Diagram;

@Entity
public class Problem  implements Serializable{

	private static final long serialVersionUID = 3194351441392500048L;

	@Id
	Long id;

	@Index
	Badge requiredBadge;
	
	String title;
	String description;
	HashSet<Equation> equations;
	Diagram diagram;
	String toSolveID;
	
	public Problem(){
	}
	public Problem(String title, String description, Badge requiredBadge){
		this.title=title;
		this.description=description;
		this.requiredBadge=requiredBadge;
	}
	public Problem(String title, String description, Badge requiredBadge, Equation equation, Diagram diagram, String toSolveID){
		this(title, description, requiredBadge);
		addEquation(equation);
		setDiagram(diagram);
		setToSolveID(toSolveID);
	}
	
	public void addEquation(Equation eq) {
		if(equations == null) {
			equations = new HashSet<Equation>();
		}
		
		equations.add(eq);
	}
	
	public Badge getRequiredBadge() {
		return requiredBadge;
	}
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public Diagram getDiagram() {
		return diagram;
	}
	public HashSet<Equation> getEquations() throws NullPointerException {
		return equations;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setDiagram(Diagram diagram) {
		this.diagram = diagram;
	}

	public String getToSolveID() {
		return toSolveID;
	}
	public void setToSolveID(String toSolveID) {
		this.toSolveID = toSolveID;
	}
	public Long getId() {
		return id;
	}
}
