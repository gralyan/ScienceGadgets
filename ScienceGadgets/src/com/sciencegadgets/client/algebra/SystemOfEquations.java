package com.sciencegadgets.client.algebra;

import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.shared.TypeSGET;

public class SystemOfEquations {

	private EquationTree currentTree = null;
	private LinkedList<EquationTree> nonCurrentlist = new LinkedList<EquationTree>();

	public SystemOfEquations(EquationTree currentTree) {
		this.currentTree = currentTree;
	}
	
	public LinkedList<EquationTree> getNonCurrentList() {
		return nonCurrentlist;
	}
	
	public EquationTree getCurrentTree() {
		return currentTree;
	}
	
	void setCurrentTree(EquationTree eqTree) {
		currentTree = eqTree;
	}
	
	void newCurrentTree() {
		nonCurrentlist.add(currentTree);
		currentTree = new EquationTree(TypeSGET.Variable,
				TypeSGET.NOT_SET, TypeSGET.Variable, TypeSGET.NOT_SET, true);
	}
	
	public Boolean moveToWorkingTree(EquationTree eqTree) {
		if(nonCurrentlist.contains(eqTree)) {
			nonCurrentlist.add(currentTree);
			currentTree = eqTree;
			nonCurrentlist.remove(eqTree);
			return true;
		}
		return false;
	}

	void adoptURLParam(String sysParam) {
		String[] sysArray = sysParam
				.split(URLParameters.SYSTEM_OF_EQUATIONS_DELIMITER);
		for (String eqStr : sysArray) {
			Element eqXML = new HTML(eqStr).getElement().getFirstChildElement();
			EquationTree eqTree = new EquationTree(eqXML, currentTree.isInEditMode());
			nonCurrentlist.add(eqTree);
		}
	}
	
	public String getURLParam() {
	
		String URLParam = "";
			String del = URLParameters.SYSTEM_OF_EQUATIONS_DELIMITER;
			for(EquationTree eqTree : nonCurrentlist) {
				URLParam = URLParam + del + eqTree.getEquationXMLString();
			}
			URLParam = URLParam.substring(del.length());
		return URLParam;
	}

	public void add(EquationTree eqTree){
		nonCurrentlist.add(eqTree);
	}
	
	public void remove(EquationTree eqTree) {
		nonCurrentlist.remove(eqTree);
	}
	
	public Boolean hasMultipleEquations() {
		return !nonCurrentlist.isEmpty();
	}

}
