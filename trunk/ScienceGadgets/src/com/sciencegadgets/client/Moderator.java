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
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Node;
import com.google.gwt.dom.client.NodeList;
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
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.sciencegadgets.client.algebra.AlgOut;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathMLBindingTree;
import com.sciencegadgets.client.algebra.edit.ChangeNodeMenu;
import com.sciencegadgets.client.algebra.edit.RandomSpecification;
import com.sciencegadgets.client.algebra.edit.SymbolPalette;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;

public class Moderator implements EntryPoint {

	static EquationPanel eqPanel = null;
	private static SimplePanel eqPanelHolder = new SimplePanel();

	public static MathMLBindingTree jTree;
	// public static LinkedList<AbstractMathDropController> dropControllers;
	public static boolean inEditMode = false;
	private int SGAWidth;
	private static int SGAHeight;
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

	public enum Activity {
		equation_browser, algebra, random_spec, insert_symbol, conversion;
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
	 *            - the equation as a string
	 */
	public void makeAlgebraWorkspace(String mathMl) {
		HTML html = new HTML(mathMl);
		makeAlgebraWorkspace(html.getElement().getFirstChildElement());
	}

	/**
	 * Creates the view of the equation
	 * 
	 * @param mathML
	 *            - the equation as an element
	 */
	public void makeAlgebraWorkspace(Element mathML) {
		setActivity(Activity.algebra);
		
		scienceGadgetArea.clear();
		
		//Upper Area - 15%
		FlowPanel upperArea = new FlowPanel();
		upperArea.setSize("100%", "15%");		
		
		if (inEditMode) {
			saveEquationButton.setSize("100%", "100%");
			upperArea.add(saveEquationButton);
		} else {
			AlgOut algOut = new AlgOut();
			algOut.setSize("100%", "100%");
			upperArea.add(algOut);
		}
		scienceGadgetArea.add(upperArea);

		//Equation Area - 70%
		eqPanelHolder.setSize("100%", "70%");
		scienceGadgetArea.add(eqPanelHolder);

		//Lower Area - 15%
		FlowPanel lowerArea = new FlowPanel();
		lowerArea.setSize("100%", "15%");
		
		if (inEditMode) {
			if (changeNodeMenu == null) {
				changeNodeMenu = new ChangeNodeMenu();
			}
			lowerArea.add(changeNodeMenu);
		}
		scienceGadgetArea.add(lowerArea);

		try {
			if (mathML != null) {// fitWindow calls this method with
									// mathML==null
				jTree = new MathMLBindingTree(mathML, inEditMode);
				reloadEquationPanel("");
			}

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

		JSNICalls.consoleLog("Loading: " + jTree.getMathMLClone().getString());
		// AlgOutEntry.updateAlgOut(/*jTree.getMathML()*/mathML,
		// jTree.getWrappers(),
		// changeComment);
		eqPanelHolder.clear();
		jTree.reloadEqHTML();
		eqPanel = new EquationPanel(jTree, inEditMode);
		eqPanelHolder.add(eqPanel);

		if (inEditMode) {
			changeNodeMenu.setVisible(false);
		}
		// TODO uncomment
		// DropControllAssigner.assign(jTree.getWrappers(), true);
	}

	public void switchToBrowser() {
		setActivity(Activity.equation_browser);

		eqPanelHolder.clear();
		scienceGadgetArea.clear();

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
			try {
				String equation = jTree.getMathMLClone().getString();
				if (equation.contains(ChangeNodeMenu.NOT_SET)) {
					Window.alert("All new entities (" + ChangeNodeMenu.NOT_SET
							+ ") must be set or removed before saving");
					return;
				}

				dataBase.saveEquation(equation, new AsyncCallback<String>() {

					@Override
					public void onSuccess(String result) {
						JSNICalls.consoleLog("Saved: " + result);
					}

					@Override
					public void onFailure(Throwable caught) {
						Window.alert("Save failed");
						JSNICalls.consoleError("Save Failed: "
								+ caught.getCause().toString());
					}
				});
			} catch (Exception e) {
				Window.alert("Could not save equation, see log");
				JSNICalls.consoleLog(e.toString());
			}
		}
	}

	class ResizeAreaHandler implements ResizeHandler {
		Timer resizeTimer = new Timer() {
			@Override
			public void run() {
				 fitWindow();
			}
		};

		@Override
		public void onResize(ResizeEvent event) {
			resizeTimer.schedule(250);
		}
	}

	private void fitWindow() {
		SGAHeight = Window.getClientHeight();
		SGAWidth = Window.getClientWidth()*97/100;

		// Take up the window
		scienceGadgetArea.setSize(SGAWidth + "px", SGAHeight + "px");
		Window.scrollTo(0,
				scienceGadgetArea.getAbsoluteTop());

	}

	class HistoryChange<String> implements ValueChangeHandler<String> {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			
			switch (Activity.valueOf((java.lang.String) event.getValue())) {
			case equation_browser:
				switchToBrowser();
				break;
			case algebra:
				if (symbolPopup != null && symbolPopup.isShowing()) {
					symbolPopup.hide();
				}
				if (randomSpec != null && randomSpec.isShowing()) {
					randomSpec.hide();
				}
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
