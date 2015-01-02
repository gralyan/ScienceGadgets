package com.sciencegadgets.client;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;

public class URLParameters {

	public static final String PARAMETER_DELIMETER = "parameter";
	public static final String PARAMETER_VALUE_DELIMETER = "value";
	public static final String TRUE = "true";
	public static final String False = "false";
	public static final String USER_ADMIN = "admin";

	public enum Parameter {
		activity, equation, easy, user, problemkey;
	}

	public static HashMap<Parameter, String> getParameterMap() {
		final String historyToken = History.getToken();
		HashMap<Parameter, String> paramMap = new HashMap<Parameter, String>();
		if (historyToken != null && historyToken.length() > 1) {
			for (String kvPair : historyToken.split(PARAMETER_DELIMETER)) {
				String[] kv = kvPair.split(PARAMETER_VALUE_DELIMETER, 2);
				Parameter parameter = Parameter.valueOf(kv[0]);
				if (kv.length > 1) {
					paramMap.put(parameter, URL.decodePathSegment(kv[1]));
				} else {
					paramMap.put(parameter, "");
				}
			}
		}

		return paramMap;
	}

	public static String getParameter(Parameter parameter) {
		HashMap<Parameter, String> map = getParameterMap();
		return map.get(parameter);
	}

	public static void setParameters(HashMap<Parameter, String> parameterMap,
			boolean issueEvent) {
		String historyToken = makeTolken(parameterMap);
		History.newItem(historyToken, issueEvent);
	}

	public static String makeTolken(HashMap<Parameter, String> parameterMap) {
		String historyToken = "";
		for (Entry<Parameter, String> entry : parameterMap.entrySet()) {
			// historyToken = historyToken + PARAMETER_DELIMETER +
			// entry.getKey()+PARAMETER_VALUE_DELIMETER+entry.getValue();
			historyToken = historyToken + PARAMETER_DELIMETER
					+ URL.encodePathSegment(entry.getKey() + "")
					+ PARAMETER_VALUE_DELIMETER
					+ URL.encodePathSegment(entry.getValue());
		}
		historyToken = historyToken.substring(PARAMETER_DELIMETER.length());
		return historyToken;
		// return URL.encodePathSegment(historyToken);

	}

}


