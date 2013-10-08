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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.sciencegadgets.client.algebra.AlgOut;
import com.sciencegadgets.client.algebra.EquationHTML;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.OptionsHandler;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.algebra.edit.RandomSpecification;
import com.sciencegadgets.client.algebra.edit.SaveButtonHandler;
import com.sciencegadgets.client.algebra.edit.SymbolPalette;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;

public class Moderator implements EntryPoint {

	public static EquationPanel eqPanel = null;
	public static SimplePanel eqPanelHolder = new SimplePanel();

	public static MathTree mathTree = null;
	// public static LinkedList<AbstractMathDropController> dropControllers;
	public static boolean inEditMode = false;
	private int SGAWidth;
	private static int SGAHeight;
	public static SymbolPalette symbolPopup;
	public static RandomSpecification randomSpec = null;
	public static String focusLayerId = null;
	public static AbsolutePanel scienceGadgetArea = RootPanel
			.get("scienceGadgetArea");
	private static Button saveEquationButton = new Button("Save Equation",
			new SaveButtonHandler());
	private EquationBrowser browserPanel = null;
	private static Activity currentActivity = null;
	private static AlgOut algOut = null;
	public static FlowPanel upperEqArea = null;
	public static FlowPanel lowerEqArea = null;
	public static FlowPanel contextMenuArea = null;
	public static FlowPanel selectedMenu = null;
	public static ChangeNodeMenu changeNodeMenu = null;
//	private static EquationHTML prevEqHTML = null;
	public static Button optionsButton = null;
	public static boolean isInEasyMode = false;

	// private static DropControllAssigner dropAssigner;

	@Override
	public void onModuleLoad() {

//		 Resize area when window resizes
		fitWindow();
		Window.addResizeHandler(new ResizeAreaHandler());

		History.addValueChangeHandler(new HistoryChange<String>());

		switchToBrowser();

//		 try {
//		 TestBot_Addition.deployTestBot();
//		 } catch (TopNodesNotFoundException e) {
//		 e.printStackTrace();
//		 } catch (Exception e) {
//		 e.printStackTrace();
//		 }

	}

	public enum Activity {
		equation_browser, algebra, random_spec, insert_symbol, conversion, ;
	}

	public static void setActivity(Activity activity) {
		if (!activity.equals(currentActivity)) {
			currentActivity = activity;
			History.newItem(activity.toString());
		}
	}

	/**
	 * Creates the view of the equation
	 * 
	 * @param mathML
	 *            - the equation as an element
	 */
	public static void makeAlgebraWorkspace(Element mathML) {
		System.out.println(mathML.getString());
		setActivity(Activity.algebra);

		scienceGadgetArea.clear();

		// Upper Area - 15%
		upperEqArea = new FlowPanel();
		upperEqArea.setSize("100%", "15%");
		scienceGadgetArea.add(upperEqArea);
		
		if(optionsButton == null){
		optionsButton = new Button("Options", new OptionsHandler());
		optionsButton.setSize("10%", "100%");
		optionsButton.addStyleName("layoutRow");
		}
		upperEqArea.add(optionsButton);
		
		
		if (inEditMode) {
			saveEquationButton.setSize("90%", "100%");
			saveEquationButton.setStyleName("saveEquationButton");
			saveEquationButton.addStyleName("layoutRow");
			upperEqArea.add(saveEquationButton);
		} else {
			algOut = new AlgOut();
			algOut.setSize("90%", "100%");
			algOut.addStyleName("layoutRow");
			upperEqArea.add(algOut);
		}

		// Equation Area - 70%
		eqPanelHolder.setSize("100%", "70%");
		scienceGadgetArea.add(eqPanelHolder);

		// Lower Area - 15%
		lowerEqArea = new FlowPanel();
		lowerEqArea.setSize("100%", "15%");
		scienceGadgetArea.add(lowerEqArea);
		// Context Menu Area
		contextMenuArea = new FlowPanel();
		contextMenuArea.addStyleName("layoutRow");
		contextMenuArea.setSize("10%", "100%");
		lowerEqArea.add(contextMenuArea);
		// SelectedMenuArea
		selectedMenu = new FlowPanel();
		selectedMenu.setSize("80%", "100%");
		selectedMenu.addStyleName("layoutRow");

		if (inEditMode) {
			if (changeNodeMenu == null) {
				changeNodeMenu = new ChangeNodeMenu();
			}
			selectedMenu.add(changeNodeMenu);
		}
		lowerEqArea.add(selectedMenu);

		try {
			if (mathML != null) {
				mathTree = new MathTree(mathML, inEditMode);
//				prevEqHTML = mathTree.getHTMLAlgOut();
				reloadEquationPanel(null);
			}
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param changeComment
	 *            - use null for simple reload, specify change to add to AlgOut
	 */
	public static void reloadEquationPanel(String changeComment) {
		if (changeComment != null) {
			algOut.updateAlgOut(changeComment, mathTree.getHTMLAlgOut());
		}
//		prevEqHTML = mathTree.getHTMLAlgOut();

		if (!inEditMode) {
			selectedMenu.clear();
		}
		contextMenuArea.clear();
		eqPanelHolder.clear();

		mathTree.validateTree();
		mathTree.reloadEqHTML();
		eqPanel = new EquationPanel(mathTree, inEditMode);
		eqPanelHolder.add(eqPanel);

		if (inEditMode) {
			changeNodeMenu.setVisible(false);
		}
	}

	public void switchToBrowser() {
		// DOM.getElementById("algebraWorkspace").getStyle().setDisplay(Display.NONE);
		scienceGadgetArea.clear();

		if (browserPanel == null) {
			browserPanel = new EquationBrowser(this);
		}
		scienceGadgetArea.add(browserPanel);
	}



	class ResizeAreaHandler implements ResizeHandler {
		Timer resizeTimer = new Timer() {
			@Override
			public void run() {
				fitWindow();
				if (Activity.algebra.equals(currentActivity)) {
					reloadEquationPanel(null);
				}
			}
		};

		@Override
		public void onResize(ResizeEvent event) {
			resizeTimer.schedule(250);
		}
	}

	private void fitWindow() {
		SGAHeight = Window.getClientHeight();
		SGAWidth = Window.getClientWidth() * 97 / 100;

		// Take up the window
		scienceGadgetArea.setSize(SGAWidth + "px", SGAHeight + "px");
		Window.scrollTo(0, scienceGadgetArea.getAbsoluteTop());

	}

	class HistoryChange<String> implements ValueChangeHandler<String> {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			Activity newActivity;
			try {
				newActivity = Activity.valueOf((java.lang.String) event
						.getValue());
			} catch (IllegalArgumentException e) {
				newActivity = Activity.equation_browser;
				currentActivity = Activity.equation_browser;
			}
			switch (newActivity) {
			case equation_browser:

				eqPanelHolder.clear();

				focusLayerId = null;

				if (symbolPopup != null && symbolPopup.isShowing()) {
					symbolPopup.hide();
				}
				if (randomSpec != null && randomSpec.isShowing()) {
					randomSpec.hide();
				}
				switchToBrowser();
				break;
			case algebra:
				if (symbolPopup != null && symbolPopup.isShowing()) {
					symbolPopup.hide();
				}
				if (randomSpec != null && randomSpec.isShowing()) {
					randomSpec.hide();
				}
				// DOM.getElementById("algebraWorkspace").getStyle().setDisplay(Display.BLOCK);
				break;
			case insert_symbol:
				if (symbolPopup != null && !symbolPopup.isShowing()) {
					symbolPopup.show();
				}
				break;
			case random_spec:
				if (randomSpec != null && !randomSpec.isShowing()) {
					randomSpec.show();
				}
				break;
			case conversion:

				break;

			}
		}
	}

}
