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
import java.util.HashSet;

public class Diagram implements Serializable {
	private static final long serialVersionUID = 574345758101323214L;

	String imageBlobKey;
	String imageURL;
	HashSet<Measure> measurements = new HashSet<Measure>();

	public Diagram() {
	}

	public Diagram(String imageBlobKey, String imageURL) {
		this.imageBlobKey = imageBlobKey;
		this.imageURL = imageURL;
	}

	public String getImageBlobKey() {
		return imageBlobKey;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void addMeasure(String nodeID, double ratioX, double ratioY) {
		if(nodeID == null) {
			return;
		}
		for(Measure measure : measurements) {
			if(measure.nodeID.equals(nodeID)) {
				measurements.remove(measure);
				break;
			}
		}
		measurements.add(new Measure(nodeID, ratioX, ratioY));
	}

	public HashSet<Measure> getMeasurements() {
		return measurements;
	}

}

