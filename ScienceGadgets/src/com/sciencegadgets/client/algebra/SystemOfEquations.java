package com.sciencegadgets.client.algebra;

import java.util.HashMap;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.URLParameters;
import com.sciencegadgets.shared.TypeSGET;

public class SystemOfEquations {
	
	private EquationTree workingTree;
	private HashMap<EquationTree, SystemEquationInfo> system = new HashMap<EquationTree, SystemEquationInfo>();
//	private EquationTree currentTree = null;
//	private LinkedList<EquationTree> nonWorkingTrees = new LinkedList<EquationTree>();
//	private LinkedList<EquationTree> archivedTrees = new LinkedList<EquationTree>();

	public SystemOfEquations(EquationTree workingTree) {
		this.workingTree = workingTree;
		system.put(workingTree, new SystemEquationInfo(false));
	}
	
	public EquationTree getWorkingTree() {
		return workingTree;
	}
	
	public HashMap<EquationTree, SystemEquationInfo> getNonWorkingTrees() {
		HashMap<EquationTree, SystemEquationInfo> nonWorking = new HashMap<EquationTree, SystemEquationInfo>(system);
		nonWorking.remove(workingTree);
		return nonWorking;
	}
	
	void setWorkingTree(EquationTree eTree) {
		workingTree = eTree;
	}
	
	void newWorkingTree() {
		workingTree = new EquationTree(TypeSGET.Variable,
				TypeSGET.NOT_SET, TypeSGET.Variable, TypeSGET.NOT_SET, true);
		system.put(workingTree, new SystemEquationInfo(false));
	}
	
	public Boolean moveToWorkingTree(EquationTree eqTree) {
		if(system.containsKey(eqTree)) {
			workingTree = eqTree;
			return true;
		}
		return false;
	}

	void adoptURLParam(String sysParam) {
		String[] sysArray = sysParam
				.split(URLParameters.SYSTEM_OF_EQUATIONS_DELIMITER);
		for (String eqStr : sysArray) {
			Element eqXML = new HTML(eqStr).getElement().getFirstChildElement();
			EquationTree eqTree = new EquationTree(eqXML, workingTree.isInEditMode());
			system.put(eqTree, new SystemEquationInfo(false));
		}
	}
	
	public String getURLParam() {
	
		String URLParam = "";
			String del = URLParameters.SYSTEM_OF_EQUATIONS_DELIMITER;
			for(EquationTree eqTree : getNonWorkingTrees().keySet()) {
				URLParam = URLParam + del + eqTree.getEquationXMLString();
			}
			URLParam = URLParam.substring(del.length());
		return URLParam;
	}
	
	public SystemEquationInfo getInfo(EquationTree eTree) {
		return system.get(eTree);
	}
	
	public Boolean hasMultipleEquations() {
		return system.size() > 1;
	}
	
	public void archive(EquationTree eTree) {
		SystemEquationInfo sysEqInfo = new SystemEquationInfo(true);
		system.put(eTree, sysEqInfo);
	}

	public class SystemEquationInfo{
		private boolean isArchived = false;
		AlgebraHistory history;
		public SystemEquationInfo(Boolean isArchived) {
			this.isArchived = isArchived;
		}
		public void setHistory(AlgebraHistory history){
			this.history = history;
		}
		public Boolean isArchived() {
			return isArchived;
		}
	}
}
