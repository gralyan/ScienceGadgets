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
package com.sciencegadgets.client;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.dom.client.Element;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.SystemOfEquations;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeSGET;
import com.sciencegadgets.shared.dimensions.UnitAttribute;

public class URLParameters {

	public static final String PARAMETER_DELIMETER = "&";
	public static final String PARAMETER_VALUE_DELIMETER = "=";
	public static final String TRUE = "true";
	public static final String False = "false";
	public static final String USER_ADMIN = "admin";
	public static final String RANDOM_PROVIDED_DELIMITER = "_";
	public static final String SYSTEM_OF_EQUATIONS_DELIMITER = "soe";

	public enum Parameter {
		//Always available
		activity, easy, user,
		//Only when activity==algebrasolve, algebraedit, algebrasolvegoal, algebracreategoal
		equation, 
		system,
		//Only when activity==problem
		problemid, 
		//Only when activity==algebrasolve
		goal,
		//Only when activity==conversion
		conversionvalue, unitattribute,
		//Only activity==algebraedit and up until HistoryChange of activity==algebrasolve
		randomprovided;
	}

	/**
	 * @param parameter - type of parameter 
	 * @return url parameter value, decompressed if necessary
	 */
	public static String getParameter(Parameter parameter) {
		HashMap<Parameter, String> map = getParameterMap();
		return map.get(parameter);
	}

	/**
	 * @return url parameter map, decompressed if necessary
	 */
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
						switch (parameter) {
						case equation:
						case goal:
						case system:
						value = decompressEquationXML(value);
							break;
						default:
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

	/**
	 * Creates url tolken, compressing when necessary
	 */
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
			switch (param) {
			case equation:
			case goal:
			case system:
				value = compressEquationXML(value);
				break;
			default:
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

	//
	// Equation
	//
	
	public static String decompressEquationXML(String equationXML) {
		
		// Random ? mark
		equationXML = equationXML.replace("%3F", RandomSpecPanel.RANDOM_SYMBOL);
		
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
