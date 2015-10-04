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
package com.sciencegadgets.client.challenge;

import java.util.HashMap;

import com.googlecode.objectify.Key;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.ui.LinkPrompt;

public class LinkPrompt_Challenge extends LinkPrompt {

	private String problemID;

	public LinkPrompt_Challenge(String problemID) {
		super();
		
		this.problemID = problemID;
	}

	@Override
	public HashMap<Parameter, String> setMapParameters() {
		
		HashMap<Parameter, String> pMap = new HashMap<Parameter, String>();
		pMap.put(Parameter.problemid, problemID);
		pMap.put(Parameter.activity, ActivityType.problem.toString());

		return pMap;
	}

}
