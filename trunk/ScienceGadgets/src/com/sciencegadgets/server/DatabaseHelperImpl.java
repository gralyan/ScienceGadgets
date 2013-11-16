package com.sciencegadgets.server;

import java.io.File;
import java.io.IOException;
//import java.io.StringWriter;
//import java.util.Collections;
//import java.util.HashSet;
//import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.transform.OutputKeys;
//import javax.xml.transform.Transformer;
//import javax.xml.transform.TransformerException;
//import javax.xml.transform.TransformerFactory;
//import javax.xml.transform.dom.DOMSource;
//import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.ObjectifyService;
import com.sciencegadgets.client.DatabaseHelper;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.QuantityKind;
import com.sciencegadgets.client.entities.Unit;

@SuppressWarnings("serial")
public class DatabaseHelperImpl extends RemoteServiceServlet implements
		DatabaseHelper {

	TreeMap<String, String> map = new TreeMap<String, String>();
	HashSet<String> set = new HashSet<String>();

	static {
		ObjectifyService.register(Equation.class);
		ObjectifyService.register(Unit.class);
		ObjectifyService.register(QuantityKind.class);
	}

	@Override
	public String saveEquation(String name) throws IllegalArgumentException {
		Equation e = new Equation();
		e.setXML(name);

		ObjectifyService.ofy().save().entity(e).now();

		return e.getXML();
	}

	@Override
	public String[] getUnits() {
		return null;

	}

	@Override
	public String[] saveUnit(Unit unit) {
		return null;

	}

	@Override
	public String[] getAlgebraEquations() throws IllegalArgumentException {

		List<Equation> eqList = ObjectifyService.ofy().load()
				.type(Equation.class).list();
		// .type(Equation.class).limit(20).list();

		int listSize = eqList.size();
		String[] eqArray = new String[listSize];
		for (int i = 0; i < listSize; i++) {
			eqArray[i] = eqList.get(i).getXML();
		}

		return eqArray;
	}

	public String[][] getVariables() throws IllegalArgumentException {
		Document doc = getDoc("Data/qudt-1.0/dimension.owl");
		NodeList dimensionNodes = doc.getElementsByTagName("qudt:Dimension");

		// aa.add("Inductance");
		// aa.add("ElectricCurrentPerUnitLength");
		// aa.add("EnergyPerElectricCharge");
		// aa.add("Capacitance");
		// aa.add("MagneticField");
		// aa.add("ElectricConductivity");
		// aa.add("ElectricChargePerArea");
		// aa.add("ElectricField");
		// aa.add("Permeability");
		// aa.add("ElectricCharge");
		// aa.add("Resistance");
		// aa.add("ElectricCurrentDensity");
		// aa.add("ElectricCurrent");
		// aa.add("Permittivity");
		// aa.add("MagneticFlux");

		for (int i = 0; i < dimensionNodes.getLength(); i++) {
			Element dimension = (Element) dimensionNodes.item(i);
			NodeList baseVectors = dimension
					.getElementsByTagName("qudt:dimensionVector");
			String U = "0", L = "0", M = "0", T = "0", I = "0", Θ = "0", N = "0", J = "0";
			for (int j = 0; j < baseVectors.getLength(); j++) {
				Element base = (Element) baseVectors.item(j);
				String baseAttribute = base.getAttribute("rdf:resource");
				if (baseAttribute.equals("")) {
					baseAttribute = ((Element) base.getElementsByTagName(
							"qudt:DimensionVector").item(0)).getAttribute(
							"rdf:about").replace(
							"http://data.nasa.gov/qudt/owl/dimension#Vector_",
							"");
				} else {
					baseAttribute = baseAttribute.replaceFirst(
							"http://data.nasa.gov/qudt/owl/dimension#Vector_",
							"");
				}
				String baseType = "" + baseAttribute.charAt(0);
				String baseValue = baseAttribute.replace(baseType, "");

				// dimension notation 0,0,0,0,0,0,0,0
				if (baseType.equals("U")) {
					U = baseValue;
				} else if (baseType.equals("L")) {
					L = baseValue;
				} else if (baseType.equals("M")) {
					M = baseValue;
				} else if (baseType.equals("T")) {
					T = baseValue;
				} else if (baseType.equals("I")) {
					I = baseValue;
				} else if (baseType.equals("Θ")) {
					Θ = baseValue;
				} else if (baseType.equals("N")) {
					N = baseValue;
				} else if (baseType.equals("J")) {
					J = baseValue;
				}
			}
			String dim = U + "," + L + "," + M + "," + T + "," + I + "," + Θ
					+ "," + N + "," + J;

			String quantity = ((Element) dimension.getElementsByTagName(
					"qudt:referenceQuantity").item(0)).getAttribute(
					"rdf:resource").replace(
					"http://data.nasa.gov/qudt/owl/quantity#", "");

			map.put(quantity, dim);

			// map.put(quantity + dim, dim);
		}
		map.put("Prefix", "1,0,0,0,0,0,0,0");
		map.put("PlaneAngle", "1,0,0,0,0,0,0,0");
		map.put("SolidAngle", "2,0,0,0,0,0,0,0");
		map.remove("AreaPerTime");
		map.put("KinematicViscosity", "0,2,0,-1,0,0,0,0");
		map.remove("EnergyPerTemperature");
		map.put("HeatCapacity", "0,2,1,-2,0,-1,0,0");
		map.put("Radioactivity", "0,0,0,-1,0,0,0,0");
		map.put("EnergyPerArea", "0,0,1,-2,0,0,0,0");
		map.put("InformationEntropy", "1,0,0,0,0,0,0,0");
		map.remove("ElectricCurrentPerUnitLength");
		map.put("AuxillaryMagneticField", "0,-1,0,0,1,0,0,0");

		// for (String s : map.keySet()) {
		// String v = map.get(s);
		// System.out.println(s.replaceAll("[0-9]", "").replace(",", "")
		// .replace("-", "").replace(".", "")
		// + " " + v);
		// }
		getUnitsFromOwl();
		return null;
	}

	@Override
	public String[] getUnitsFromOwl() throws IllegalArgumentException {
		Document doc = getDoc("Data/qudt-1.0/unit.owl");
		NodeList eqNodes = doc.getElementsByTagName("*");

		int codeCount = 1;
		for (int i = 0; i < eqNodes.getLength(); i++) {
			Element node = (Element) eqNodes.item(i);
			if (!node.getNodeName().endsWith("Unit")
					|| node.getNodeName().startsWith("qudt:system")
					|| node.getNodeName().startsWith("qudt:Currency")) {
				continue;
			}

			String qKind = null;

			// if (symbol.contains("-") || symbol.contains(" ")
			// || symbol.contains("^") || symbol.contains("/")) {
			// continue;
			// }
			String label = extractProperty(node, "rdfs:label");
			if (label.equals("Millimeter of Mercury")) {
			} else if (label.equals("Yotta") || label.equals("Zetta")
					|| label.equals("Exa") || label.equals("Peta")
					|| label.equals("Tera") || label.equals("Giga")
					|| label.equals("Mega") || label.equals("Kilo")
					|| label.equals("Hecto") || label.equals("Deca")
					|| label.equals("Deci") || label.equals("Centi")
					|| label.equals("Milli") || label.equals("Micro")
					|| label.equals("Nano") || label.equals("Pico")
					|| label.equals("Femto")
					|| label.equals("Atto")
					|| label.equals("Zepto")
					|| label.equals("Yocto")
					// binary prefix
					|| label.equals("Kibi") || label.equals("Mebi")
					|| label.equals("Gibi") || label.equals("Tebi")
					|| label.equals("Pebi") || label.equals("Exbi")
					|| label.equals("Zebi") || label.equals("Yobi")) {
				qKind = "Prefix";
			} else if (label.startsWith("Yotta") || label.startsWith("Zetta")
					|| label.startsWith("Exa") || label.startsWith("Peta")
					|| label.startsWith("Tera") || label.startsWith("Giga")
					|| label.startsWith("Mega") || label.startsWith("Kilo")
					|| label.startsWith("Hecto") || label.startsWith("Deca")
					|| label.startsWith("Deci") || label.startsWith("Centi")
					|| label.startsWith("Milli") || label.startsWith("Micro")
					|| label.startsWith("Nano") || label.startsWith("Pico")
					|| label.startsWith("Femto")
					|| label.startsWith("Atto")
					|| label.startsWith("Zepto")
					|| label.startsWith("Yocto")
					// binary prefix
					|| label.startsWith("Kibi") || label.startsWith("Mebi")
					|| label.startsWith("Gibi") || label.startsWith("Tebi")
					|| label.startsWith("Pebi") || label.startsWith("Exbi")
					|| label.startsWith("Zebi") || label.startsWith("Yobi")) {
				continue;
			}

			String code = extractProperty(node, "qudt:code");
			if (code == null) {
				code = "a" + codeCount++;
			}

			String offset = extractProperty(node, "qudt:conversionOffset");
			if (offset == null || offset.equals("0.0") || offset.equals("0")) {
			} else {
			}

			String multiplier = extractProperty(node,
					"qudt:conversionMultiplier");
			if (multiplier == null) {
				continue;
			}

			if (qKind == null) {
				NodeList a = node.getElementsByTagName("qudt:quantityKind");
				if (a.getLength() > 0) {
					qKind = ((Element) a.item(0)).getAttribute("rdf:resource")
							.replaceFirst(
									"http://data.nasa.gov/qudt/owl/quantity#",
									"");
					if (qKind.equals("LiquidVolume")
							|| qKind.equals("DryVolume")) {
						qKind = "Volume";
					} else if (qKind.equals("HeatFlowRate")) {
						qKind = "Power";
					} else if (qKind.equals("MolecularMass")) {
						qKind = "Mass";
					} else if (qKind.equals("AreaPerTime")) {
						qKind = "HeatCapacity";
					} else if (qKind.equals("EnergyPerTemperature")) {
						qKind = "KinematicViscosity";
					} else if (qKind.equals("ThermalDiffusivity")) {
						qKind = "AreaThermalExpansion";
					} else if (qKind.equals("Activity")) {
						qKind = "Radioactivity";
					} else if (qKind.equals("Exposure")) {
						qKind = "ElectricChargePerMass";
					} else if (qKind.equals("AbsorbedDose")
							|| qKind.equals("DoseEquivalent")) {
						qKind = "SpecificEnergy";
					} else if (qKind.equals("ThermalEnergy")) {
						qKind = "EnergyAndWork";
					} else if (qKind.equals("MassAmountOfSubstance")) {
						continue;
					} else if (qKind.equals("ForcePerElectricCharge")) {
						continue;
					}
				} else {
					continue;
				}
			}

			String systemUnit = node.getParentNode().getParentNode()
					.getNodeName();
			if (systemUnit.equals("qudt:SystemOfUnits")) {
				systemUnit = ((Element) node.getParentNode().getParentNode())
						.getAttribute("rdf:about")
						.replace(
								"http://data.nasa.gov/qudt/owl/unit#SystemOfUnits_",
								"");
			}

			String symbol = extractProperty(node, "qudt:symbol");
			String symbolFixed = symbol;
			if (symbol == null) {
				continue;

				// close enough
			} else if (label.equals("US Survey Foot")) {
				continue;
			} else if (label.equals("Mile US Statute")) {
				continue;
			} else if (label.equals("Biot")
					&& systemUnit.equals("qudt:NotUsedWithSIUnit")) {
				continue;
			} else if (label.equals("Metric Ton")
					&& systemUnit.equals("qudt:NotUsedWithSIUnit")) {
				continue;

			} else if (label.equals("Cord")) {
				symbolFixed = "Cord";
			} else if (symbol.equals("therm (US)")) {
				symbolFixed = "therm(US)";
			} else if (symbol.equals("therm (EC)")) {
				symbolFixed = "therm(EC)";
			} else if (symbol.equals("(K^2)m/W")) {
				symbolFixed = "K*m^2*W^-1";
			} else if (symbol.equals("ft-lbf(ft^2-s)")) {
				symbolFixed = "ft^-1*lbf*s^-1)";
			} else if (label.equals("Ounce Troy")) {
				symbolFixed = "oz(Troy)";
			} else if (label.equals("Ounce Mass")) {
				symbolFixed = "oz(Mass)";
			} else if (label.equals("US Liquid Ounce")) {
				symbolFixed = "oz(fl,US)";
			} else if (label.equals("Imperial Ounce")) {
				symbolFixed = "oz(fl,UK)";
			} else if (label.equals("Pound Troy")) {
				symbolFixed = "lb(Troy)";
			} else if (label.equals("Pound Mass")) {
				symbolFixed = "lb";
			} else if (label.equals("Faraday")) {
				symbolFixed = "Faraday";
			} else if (label.equals("Day")) {
				symbolFixed = "day";
			} else if (label.equals("")) {
				symbolFixed = "";
			} else if (label.equals("")) {
				symbolFixed = "";
			} else if (label.equals("")) {
				symbolFixed = "";

				//
			} else {
				symbolFixed = symbolFixed.replace("^-", "expNeg")
						.replace("-", "*").replace(" / ", "/")
						.replace(" ", "*").replace("(", "").replace(")", "");
				symbolFixed = symbolFixed.replace("expNeg", "^-");

				String[] symbolParts = symbolFixed.split("/");

				symbolFixed = symbolParts[0];
				if (symbolParts.length > 1) {
					String[] denominator = symbolParts[1].split("\\*");
					for (String den : denominator) {
						// System.out.println("");
						// System.out.println("before "+den);
						if (den.contains("^")) {
							den = den.replace("^", "^-");
						} else {
							den = den + "^-1";
						}
						// System.out.println("after "+den);
						symbolFixed = symbolFixed + "*" + den;
						// System.out.println("runin "+symbolFixed);
					}
				}
			}

			// TODO
			if (map.keySet().contains(symbolFixed)) {
				System.out.println(symbolFixed + " " + label + " "
						+ map.get(symbolFixed));
			} else {

				map.put(symbolFixed, label);
			}

			// map.put(qKind, "used");
			// System.out.println("");
			// System.out.print(qKind);
			// System.out.print(",");
			// System.out.print(multiplier);
			// System.out.print(",");
			// System.out.print(extractProperty(node, "rdfs:label"));
			// System.out.print(",");
			// System.out.print((node.getParentNode().getNodeName()
			// .replaceFirst("system", "") + "").replaceFirst("Unit", "")
			// .replaceFirst("qudt", ""));
			// System.out.print(",");
			// System.out.print(systemUnit);
			// System.out.print(",");
			// System.out.print(symbol);
			// System.out.print(",");
			// System.out.print(symbolFixed);
			// System.out.print(",");
			// System.out.print(code);
			// System.out.print(",");
			// System.out.print(offset);

			// + extractProperty(node, "rdfs:label"));
			// + extractProperty(node, "qudt:symbol"));
			// + extractProperty(node, "qudt:code"));
			// + extractProperty(node, "qudt:description"));
			// + extractProperty(node, "qudt:conversionOffset"));
			// + extractProperty(node, "qudt:conversionMultiplier"));

			// Unit unit = new Unit();
			// QuantityKind quantityKind =
			// ObjectifyService.ofy().load().type(QuantityKind.class).id(qKind).now();
			// ObjectifyService.ofy().save().entity(unit);

		}

		// for(String s: map.keySet()){
		// if("used".equals(map.get(s))){
		// ObjectifyService.ofy().save().entity(new QuantityKind(s)).now();
		// QuantityKind qk =
		// ObjectifyService.ofy().load().type(QuantityKind.class).id(s).now();
		// System.out.println(qk.getId());
		// }
		// }

		int c = 0;
		// HashMap<String, Integer> kindCounts = new HashMap<String, Integer>();
		// for (String s : qKinds.keySet()) {
		// // count
		// System.out.println(c++ + "," + s.replaceAll("[0-9]", "") + ","
		// + qKinds.get(s).replace("qudt:", ""));
		// }
		// return eqStrings;
		return null;
	}

	private String extractProperty(Element node, String property) {
		NodeList a = node.getElementsByTagName(property);
		if (a.getLength() > 0) {
			return ((Element) a.item(0)).getTextContent();
		} else {
			return null;
		}
	}

	private Document getDoc(String path) {
		Document doc = null;

		try {
			File fXmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return doc;
	}

	@Override
	public String[] getEquationsByVariables(String[] name)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	// XML version

	// public String[] getEquationsByVariables(String[] vars)
	// throws IllegalArgumentException {
	// Document doc = getDoc(Files.Equations);
	// NodeList nList = doc.getElementsByTagName("mi");
	//
	// HashSet<String> eqs = new HashSet<String>();
	// LinkedList<Node> eqHits = new LinkedList<Node>();
	//
	// // Check every "mi" tag in the equations database to see if it equals
	// // any of the given variables
	// for (int i = 0; i < nList.getLength(); i++) {
	// Node nNode = nList.item(i);
	//
	// if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	//
	// for (String var : vars) {
	// if (var.equals(nNode.getTextContent())) {
	//
	// Node curParent = nNode.getParentNode();
	//
	// // Climb the tree looking for the math tag
	// while (!curParent.getNodeName().equals("math")) {
	// curParent = curParent.getParentNode();
	// }
	// eqHits.add(curParent);
	// }
	// }
	// }
	// }
	// // Add only if equation has all the variables
	// for (Node eqHit : eqHits) {
	// if (Collections.frequency(eqHits, eqHit) == vars.length) {
	// eqs.add(nodeToString(eqHit));
	// }
	// }
	// return eqs.toArray(new String[0]);
	// }

	// public String[][] getVariables() throws IllegalArgumentException {
	//
	// Document doc = getDoc(Files.Variables);
	// NodeList nList = doc.getElementsByTagName("variable");
	// String[][] vars = new String[2][nList.getLength()];
	//
	// for (int i = 0; i < nList.getLength(); i++) {
	// Node nNode = nList.item(i);
	//
	// if (nNode.getNodeType() == Node.ELEMENT_NODE) {
	// vars[0][i] = getTagValue("symbol", (Element) nNode);
	// vars[1][i] = getTagValue("name", (Element) nNode);
	// }
	// }
	//
	// return vars;
	// }
	//
	// private static String getTagValue(String sTag, Element eElement) {
	// NodeList nlList = eElement.getElementsByTagName(sTag).item(0)
	// .getChildNodes();
	//
	// Node nValue = (Node) nlList.item(0);
	//
	// return nValue.getNodeValue();
	// }

	// private Document getDoc(Files file) {
	// return getDoc("Data/" + file.toString() + ".xml");
	// }

	//
	// private String nodeToString(Node node) {
	// System.setProperty("javax.xml.transform.TransformerFactory",
	// "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
	//
	// StringWriter sw = new StringWriter();
	// try {
	// Transformer t = TransformerFactory.newInstance().newTransformer();
	// t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
	// t.transform(new DOMSource(node), new StreamResult(sw));
	// } catch (TransformerException te) {
	// System.out.println("nodeToString Transformer Exception");
	// }
	// String xml = sw.toString();
	// xml = xml.replaceAll(">\\s+<", "><");
	// return xml;
	// }
	//
	// public static enum Files {
	// Variables, Equations, AlgebraEquations;
	// }

}