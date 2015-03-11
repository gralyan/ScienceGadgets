package com.sciencegadgets.client;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;

public class URLParameters {

	public static final String PARAMETER_DELIMETER = "&";
	public static final String PARAMETER_VALUE_DELIMETER = "=";
	public static final String TRUE = "true";
	public static final String False = "false";
	public static final String USER_ADMIN = "admin";

	public enum Parameter {
		activity, equation, easy, user, problemid, goal;
	}

	public static String getParameter(Parameter parameter) {
		HashMap<Parameter, String> map = getParameterMap();
		return map.get(parameter);
	}

	public static HashMap<Parameter, String> getParameterMap() {
		final String historyToken = History.getToken();
		HashMap<Parameter, String> paramMap = new HashMap<Parameter, String>();
		if (historyToken != null && historyToken.length() > 1) {
			for (String kvPair : historyToken.split(PARAMETER_DELIMETER)) {
				String[] kv = kvPair.split(PARAMETER_VALUE_DELIMETER, 2);
				try {
					Parameter parameter = Parameter.valueOf(kv[0]);
					if (kv.length > 1) {
						String value = kv[1];
						// String value = URL.decodePathSegment(kv[1]);
						if (Parameter.equation.equals(parameter)
								|| Parameter.goal.equals(parameter)) {
							value = decompressEquationXML(value);
						}
						paramMap.put(parameter, value);
					} else {
						paramMap.put(parameter, "");
					}
				} catch (IllegalArgumentException e) {
				}
			}
		}

		return paramMap;
	}

	public static void addParameter(Parameter goal, String goalXML,
			boolean issueEvent) {
		HashMap<Parameter, String> map = getParameterMap();
		map.put(goal, goalXML);
		setParameters(map, issueEvent);
	}

	public static void setParameters(HashMap<Parameter, String> parameterMap,
			boolean issueEvent) {
		String historyToken = makeTolken(parameterMap, false);
		History.newItem(historyToken, issueEvent);
	}

	public static String makeTolken(HashMap<Parameter, String> parameterMap,
			boolean encode) {
		String historyToken = "";
		for (Entry<Parameter, String> entry : parameterMap.entrySet()) {
			Parameter param = entry.getKey();
			String value = entry.getValue();
			String paramStr = param + "";

			if (value == null || "".equals(value)) {
				continue;
			}

			// compress equations
			if (Parameter.equation.equals(param)
					|| Parameter.goal.equals(param)) {
				value = compressEquationXML(value);
			}

			// concat tolken
			if (encode) {
				paramStr = URL.encodePathSegment(paramStr);
				value = URL.encodePathSegment(value);
			}
			historyToken = historyToken + PARAMETER_DELIMETER + param
					+ PARAMETER_VALUE_DELIMETER + value;
		}

		// The first parameter delimeter is not important
		historyToken = historyToken.substring(PARAMETER_DELIMETER.length());

		return historyToken;

	}

	public static String decompressEquationXML(String equationXML) {
		// Add xmlns
		equationXML = equationXML.replace(TypeSGET.Equation.getTag(),
				TypeSGET.Equation.getTag() + " "
						+ "xmlns:sget=\"http://www.sciencegadgets.org/Data\"");

		// change (=) and (+)
		equationXML = equationXML.replace("\u2248", "=");
		equationXML = equationXML.replace("\u2795", "+");

		for (TypeSGET type : TypeSGET.values()) {
			equationXML = equationXML.replace("<" + type.getCompressedTag()
					+ ">", "<" + type.getTag() + ">");
			equationXML = equationXML.replace("<" + type.getCompressedTag()
					+ " ", "<" + type.getTag() + " ");
			equationXML = equationXML.replace("[" + type.getCompressedTag()
					+ "]", "</" + type.getTag() + ">");
		}
		for (MathAttribute att : MathAttribute.values()) {
			equationXML = equationXML.replace(att.getCompressedName() + "=\"",
					att.getAttributeName() + "=\"");
		}
		return equationXML;
	}

	public static String compressEquationXML(String equationXML) {
		// Remove xmlns
		equationXML = equationXML.replace(
				" xmlns:sget=\"http://www.sciencegadgets.org/Data\"", "");
		// Remove all id's
		equationXML = equationXML.replaceAll("id=\\\".*?\\\"", "");
		// Remove empty units
		equationXML = equationXML.replace(
				" " + MathAttribute.Unit.getAttributeName() + "=\"\"", "");
		// Shorten tag names
		for (TypeSGET type : TypeSGET.values()) {
			equationXML = equationXML.replace("<" + type.getTag() + ">", "<"
					+ type.getCompressedTag() + ">");
			equationXML = equationXML.replace("<" + type.getTag() + " >", "<"
					+ type.getCompressedTag() + ">");
			equationXML = equationXML.replace("<" + type.getTag() + " ", "<"
					+ type.getCompressedTag() + " ");
			equationXML = equationXML.replace("</" + type.getTag() + ">", "["
					+ type.getCompressedTag() + "]");
		}
		// Shorten attribute names
		for (MathAttribute att : MathAttribute.values()) {
			equationXML = equationXML.replace(att.getAttributeName() + "=\"",
					att.getCompressedName() + "=\"");
		}
		// change (=) and (+)
		equationXML = equationXML.replace("=", "\u2248");
		equationXML = equationXML.replace("+", "\u2795");
		return equationXML;
	}
}
