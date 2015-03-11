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
	public void setMapParameters() {
		
		pMap = new HashMap<Parameter, String>();
		pMap.put(Parameter.problemid, problemID);
		pMap.put(Parameter.activity, ActivityType.problem.toString());

	}

}
