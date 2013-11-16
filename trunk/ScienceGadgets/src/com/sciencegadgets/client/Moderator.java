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
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationPanel;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.edit.RandomSpecification;
import com.sciencegadgets.client.algebra.edit.SymbolPalette;
import com.sciencegadgets.client.algebra.transformations.Rule;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;

public class Moderator implements EntryPoint {

	// public static EquationPanel eqPanel = null;

	public static MathTree mathTree = null;
	// public static LinkedList<AbstractMathDropController> dropControllers;
	private int SGAWidth;
	private static int SGAHeight;
	public static SymbolPalette symbolPopup;
	public static RandomSpecification randomSpec = null;
	public static AbsolutePanel scienceGadgetArea = RootPanel
			.get("scienceGadgetArea");
	private EquationBrowser browserPanel = null;
	private static Activity currentActivity = null;
	public static boolean isTouch = false;
	
	private final DatabaseHelperAsync dataBase = GWT
			.create(DatabaseHelper.class);
	@Override
	public void onModuleLoad() {
//
//		// Resize area when window resizes
//		fitWindow();
//		Window.addResizeHandler(new ResizeAreaHandler());
//
//		History.addValueChangeHandler(new HistoryChange<String>());
//		
//		detectTouch();
//
//		switchToBrowser();
//		
//		JSNICalls.jowl();
		
//			dataBase.getVariables(new AsyncCallback<String[][]>() {
		dataBase.getUnitsFromOwl(new AsyncCallback<String[]>() {
			@Override
			public void onSuccess(String[] result) {
//			public void onSuccess(String[][] result) {
			}
			
			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Save failed "
						+ caught.getCause().toString());
				JSNICalls.error("Save Failed: "
						+ caught.getCause().toString());
			}
		});
		
		// try {
		// TestBot_Addition.deployTestBot();
		// } catch (TopNodesNotFoundException e) {
		// e.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

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
		setActivity(Activity.algebra);

		scienceGadgetArea.clear();

		scienceGadgetArea.add(new AlgebraActivity());

		try {
			if (mathML != null) {
				mathTree = new MathTree(mathML, AlgebraActivity.inEditMode);
				reloadEquationPanel(null, null);
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
	public static void reloadEquationPanel(String changeComment, Rule rule) {
		if (changeComment != null) {
			AlgebraActivity.algOut.updateAlgOut(changeComment, rule,
					mathTree.getHTMLAlgOut());
		}
		if (!AlgebraActivity.inEditMode) {
			AlgebraActivity.selectedMenu.clear();
		}
		AlgebraActivity.contextMenuArea.clear();
		AlgebraActivity.eqPanelHolder.clear();

		mathTree.validateTree();
		mathTree.reloadEqHTML();
		AlgebraActivity.eqPanel = new EquationPanel(mathTree);
		AlgebraActivity.eqPanelHolder.add(AlgebraActivity.eqPanel);

		if (AlgebraActivity.inEditMode) {
			AlgebraActivity.changeNodeMenu.setVisible(false);
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
					reloadEquationPanel(null, null);
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

	private void detectTouch() {
		scienceGadgetArea.sinkEvents(Event.ONTOUCHSTART);
		scienceGadgetArea.addHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				isTouch = true;
				scienceGadgetArea.unsinkEvents(Event.ONTOUCHSTART);
			}
		}, TouchStartEvent.getType());
//		
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

				AlgebraActivity.eqPanelHolder.clear();

				AlgebraActivity.focusLayerId = null;

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
