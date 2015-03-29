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
package com.sciencegadgets.shared;

import java.io.Serializable;

public class Measure implements Serializable {
	private static final long serialVersionUID = -5732171957145739470L;

	String nodeID;
	double ratioX;
	double ratioY;

	public Measure() {
	}
	public Measure(String nodeID, double ratioX, double ratioY) {
		super();
		this.nodeID = nodeID;
		this.ratioX = ratioX;
		this.ratioY = ratioY;
	}

	public String getNodeID() {
		return nodeID;
	}

	public double getRatioX() {
		return ratioX;
	}

	public double getRatioY() {
		return ratioY;
	}

}