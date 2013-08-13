/*   Copyright 2012 John Gralyan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sciencegadgets.client;

import java.util.LinkedList;

import com.admin.client.AppEngineData;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.algebra.AlgOut;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathMLBindingTree;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.algebra.edit.RandomSpecification;
import com.sciencegadgets.client.algebra.edit.SymbolPalette;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;

public class Moderator implements EntryPoint {

	static EquationPanel eqPanel;
	private static AbsolutePanel eqPanelHolder = new AbsolutePanel();

	public static MathMLBindingTree jTree;
//	public static LinkedList<AbstractMathDropController> dropControllers;
	public static boolean inEditMode = false;
	private int SGAWidth;
	private int SGAHeight;
	public static SymbolPalette symbolPopup;
	public static RandomSpecification randomSpec;
	public static ChangeNodeMenu changeNodeMenu;
	public static String focusLayerId;
	private Button saveEquationButton = new Button("Save Equation",
			new SaveButtonHandler());
	private EquationBrowser browserPanel = null;
	private static AbsolutePanel scienceGadgetArea = RootPanel
			.get("scienceGadgetArea");
	private static Activity currentActivity;

	private final DatabaseHelperAsync dataBase = GWT
			.create(DatabaseHelper.class);

	// private static DropControllAssigner dropAssigner;

	@Override
	public void onModuleLoad() {

		// Resize area when window resizes
		fitWindow();
		Window.addResizeHandler(new ResizeAreaHandler());

		switchToBrowser();
		
		History.addValueChangeHandler(new HistoryChange<String>());

	}


	public static enum Activity {
		equation_browser, algebra, random_spec, conversion;
	}

	/**
	 * Creates the view of the equation
	 * @param mathML - the equation as a string
	 */
	public void makeAlgebraWorkspace(String mathMl){
		HTML html = new HTML(mathMl);
		makeAlgebraWorkspace(html.getElement().getFirstChildElement());
	}
	/**
	 * Creates the view of the equation
	 * @param mathML - the equation as an element
	 */
	public void makeAlgebraWorkspace(Element mathML) {

		currentActivity = Activity.algebra;
		History.newItem("algebra");
		
		scienceGadgetArea.clear();
		if(inEditMode){
			scienceGadgetArea.add(saveEquationButton);
		}else{
			scienceGadgetArea.add(new AlgOut());
		}
		eqPanelHolder.setSize("inherit", (SGAHeight *3/ 4) + "px");
		eqPanelHolder.setStyleName("varName");
		scienceGadgetArea.add(eqPanelHolder, 0, SGAHeight / 8);

		if (inEditMode) {
			if (changeNodeMenu == null) {
				changeNodeMenu = new ChangeNodeMenu(scienceGadgetArea);
			}
			scienceGadgetArea.add(changeNodeMenu,0,SGAHeight*7/8);
		}

		try {
			if (mathML != null)// fitWindow calls this method with mathML==null
				jTree = new MathMLBindingTree(mathML, inEditMode);
			reloadEquationPanel("");

			if (inEditMode) {
				// AlgOutEntry.algOut.add(new HTML("aaaa")/*new
				// ChangeNodeMenu(eqList.selectedWrapper)*/);
			}
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param mathML
	 */
	public static void reloadEquationPanel(String changeComment) {
		// AlgOutEntry.updateAlgOut(/*jTree.getMathML()*/mathML,
		// jTree.getWrappers(),
		// changeComment);
		if (eqPanel != null) {
			eqPanelHolder.remove(eqPanel);
		}
		eqPanel = new EquationPanel(jTree, inEditMode);
		//TODO uncomment for live
		eqPanel.getElement().getStyle().setOpacity(0);
		eqPanelHolder.add(eqPanel, 0, 0);

		if (inEditMode) {
			changeNodeMenu.setVisible(false);
		}
		History.newItem("algebra");
		// TODO uncomment
		// DropControllAssigner.assign(jTree.getWrappers(), true);
	}

	public static void onEqReady() {
		eqPanel.getElement().getStyle().setOpacity(1);
		
//		System.out.println("r "+inEditMode+" "+jTree.getMathML().getString());
	}

	public void switchToBrowser() {

		currentActivity = Activity.equation_browser;
		scienceGadgetArea.clear();

		eqPanelHolder.clear();
		focusLayerId = null;

		if (symbolPopup != null && symbolPopup.isShowing()) {
			symbolPopup.hide();
		}
		if (randomSpec != null && randomSpec.isShowing()) {
			randomSpec.hide();
		}
		if (browserPanel == null) {
			browserPanel = new EquationBrowser(this);
		}
		scienceGadgetArea.add(browserPanel);
	}

	class SaveButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent arg0) {
			try{
				String equation = jTree.getMathML().getString();
				if(equation.contains(ChangeNodeMenu.NOT_SET)){
					Window.alert("All new entities ("+ChangeNodeMenu.NOT_SET+") must be set or removed before saving");
					return;
				}
				
				dataBase.saveEquation(equation, new AsyncCallback<String>() {
					
					@Override
					public void onSuccess(String result) {
						JSNICalls.consoleLog("Saved: "+result);
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Save failed");
						JSNICalls.consoleError("Save Failed: " +caught.getCause().toString());
					}
				});
			}catch(Exception e){
				Window.alert("Could not save equation, see log");
				JSNICalls.consoleLog(e.toString());
			}
		}
	}

	class ResizeAreaHandler implements ResizeHandler {
		Timer resizeTimer = new Timer() {
			@Override
			public void run() {
//				fitWindow();
				SGAHeight = Window.getClientHeight();
				
				scienceGadgetArea.setHeight(SGAHeight + "px");
				Window.scrollTo(scienceGadgetArea.getAbsoluteLeft(),
						scienceGadgetArea.getAbsoluteTop());
				
			}
		};

		@Override
		public void onResize(ResizeEvent event) {
			resizeTimer.schedule(250);
		}
	}

	private void fitWindow() {
		SGAHeight = Window.getClientHeight();
		SGAWidth = Window.getClientWidth();

		// Take up the window
		scienceGadgetArea.setSize(SGAWidth + "px", SGAHeight + "px");
		Window.scrollTo(scienceGadgetArea.getAbsoluteLeft(),
				scienceGadgetArea.getAbsoluteTop());

		if (currentActivity != null) {
			switch (currentActivity) {
			case algebra:
//				makeAgebraWorkspace(null);
				break;
			}
		}
	}

	class HistoryChange<String> implements ValueChangeHandler<String> {
		
         @Override
         public void onValueChange(ValueChangeEvent<String> event) {
        	 String change = event.getValue();
        	 if("".equals(change)){
        		 switchToBrowser();
        	 }else if("algebra".equals(change)){
        			if (symbolPopup != null && symbolPopup.isShowing()) {
        				symbolPopup.hide();
        			}
        			if (randomSpec != null && randomSpec.isShowing()) {
        				randomSpec.hide();
        			}
        	 }
         }
	      
		
	}

}
