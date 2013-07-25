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
package com.sciencegadgets.client.algebramanipulation;

import java.util.LinkedList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;
import com.sciencegadgets.client.equationtree.ChangeNodeMenu;
import com.sciencegadgets.client.equationtree.EquationPanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree;
import com.sciencegadgets.client.equationtree.RandomSpecification;
import com.sciencegadgets.client.equationtree.SymbolPalette;

public class Moderator implements EntryPoint {

	static EquationPanel eqPanel;
	private static AbsolutePanel eqPanelHolder = new AbsolutePanel();

	public static MathMLBindingTree jTree;
	public static LinkedList<AbstractMathDropController> dropControllers;
	public static boolean inEditMode = false;
	private int SGAWidth;
	private int SGAHeight;
	public static SymbolPalette symbolPopup;
	public static RandomSpecification randomSpec;
	public static ChangeNodeMenu changeNodeMenu;
	public static String focusLayerId;
	private Button backToBrowserButton = new Button("Back",
			new BackButtonHandler());
	private EquationBrowser browserPanel = null;
	private static AbsolutePanel scienceGadgetArea = RootPanel
			.get("scienceGadgetArea");
	private static Activity currentActivity;

	// private static DropControllAssigner dropAssigner;

	@Override
	public void onModuleLoad() {

		// Resize area when window resizes
		fitWindow();
		Window.addResizeHandler(new ResizeAreaHandler());

		switchToBrowser();

	}

	public static enum Activity {
		EquationBrowser, Algebra, Conversion;
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

		currentActivity = Activity.Algebra;
		
		scienceGadgetArea.clear();
		scienceGadgetArea.add(backToBrowserButton);

		scienceGadgetArea.add(new AlgOut());

		eqPanelHolder.setSize("inherit", (SGAHeight *3/ 4) + "px");
		eqPanelHolder.setStyleName("varName");
		scienceGadgetArea.add(eqPanelHolder, 0, SGAHeight / 8);

		if (inEditMode) {
			if (changeNodeMenu == null) {
				changeNodeMenu = new ChangeNodeMenu(scienceGadgetArea);
			}
			scienceGadgetArea.add(changeNodeMenu,0,SGAHeight*3/4);
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
//		eqPanel.getElement().getStyle().setOpacity(0);
		eqPanelHolder.add(eqPanel, 0, 0);

		if (inEditMode) {
			changeNodeMenu.setVisible(false);
		}
		// TODO uncomment
		// DropControllAssigner.assign(jTree.getWrappers(), true);
	}

	public static void onEqReady() {
		eqPanel.getElement().getStyle().setOpacity(1);
		
//		System.out.println("r "+inEditMode+" "+jTree.getMathML().getString());
	}

	public void switchToBrowser() {

		currentActivity = Activity.EquationBrowser;
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

	class BackButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent arg0) {
			switchToBrowser();
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
			case Algebra:
//				makeAgebraWorkspace(null);
				break;
			}
		}
	}

}
