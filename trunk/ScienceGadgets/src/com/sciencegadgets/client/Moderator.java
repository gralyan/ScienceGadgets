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

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.client.conversion.ConversionActivity;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;

public class Moderator implements EntryPoint {

	private static int historyCount = 0;
	public static MathTree mathTree = null;
	private int SGAWidth;
	private static int SGAHeight;
	public static RandomSpecPanel randomSpec = null;
	public static AbsolutePanel scienceGadgetArea = RootPanel
			.get("scienceGadgetArea");
	private EquationBrowser browserPanel = null;
	private HandlerRegistration detectTouchReg;
	public static boolean isTouch = false;

	private static ConversionActivity conversionActivity;
	private static AlgebraActivity algebraActivity;

	private static Activity currentActivity = null;
	public static LinkedList<Prompt> prompts = new LinkedList<Prompt>();

	@Override
	public void onModuleLoad() {
		// // Resize area when window resizes
		fitWindow();
		Window.addResizeHandler(new ResizeAreaHandler());

		History.addValueChangeHandler(new HistoryChange<String>());

		detectTouch();

		switchToBrowser();

		// Blobs
		// scienceGadgetArea.add(new UploadButton());

		// try {
		// TestBot_Addition.deployTestBot();
		// } catch (TopNodesNotFoundException e) {
		// e.printStackTrace();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

	public enum Activity {
		browser, algebra, random_spec, insert_symbol, conversion, ;
	}

	public static void setActivity(Activity activity) {
		if (!activity.equals(currentActivity)) {
			currentActivity = activity;
			History.newItem(activity.toString() + "_" + historyCount++);
		}
	}

	public static void switchToAlgebra(Element mathML) {
		setActivity(Activity.algebra);
		scienceGadgetArea.clear();

		algebraActivity = new AlgebraActivity();
		scienceGadgetArea.add(algebraActivity);

		if (mathML != null) {
			mathTree = new MathTree(mathML, AlgebraActivity.inEditMode);
			AlgebraActivity.reloadEquationPanel(null, null);
		}
	}

	public static void reloadAlgebraActivity() {
		setActivity(Activity.algebra);

		AlgebraActivity.eqPanelHolder.clear();

		scienceGadgetArea.clear();
		scienceGadgetArea.add(algebraActivity);
		AlgebraActivity.reloadEquationPanel(null, null);
	}

	public static void switchToConversion(MathNode node) {
		setActivity(Activity.conversion);
		scienceGadgetArea.clear();

		if (conversionActivity == null) {
			conversionActivity = new ConversionActivity();
			conversionActivity.getElement().setAttribute("id",
					"conversionActivity");

		}
		conversionActivity.load(node);
		scienceGadgetArea.add(conversionActivity);
	}

	public void switchToBrowser() {
		setActivity(Activity.browser);
		scienceGadgetArea.clear();

		if (browserPanel == null) {
			browserPanel = new EquationBrowser();
		}
		scienceGadgetArea.add(browserPanel);
	}

	class ResizeAreaHandler implements ResizeHandler {
		Timer resizeTimer = new Timer() {
			@Override
			public void run() {
				fitWindow();
				if (Activity.algebra.equals(currentActivity)) {
					AlgebraActivity.reloadEquationPanel(null, null);
				}
				for (Prompt prompt : prompts) {
					prompt.resize();
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
		SGAWidth = Window.getClientWidth();

		// Fill up the window
		scienceGadgetArea.setSize(SGAWidth + "px", SGAHeight + "px");
		// Window.scrollTo(0, scienceGadgetArea.getAbsoluteTop());

	}

	private void detectTouch() {
		// Touch handler in main panel that is only used to detect touch once
		TouchStartHandler detectTouch = new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				isTouch = true;
				// scienceGadgetArea.unsinkEvents(Event.ONTOUCHSTART);
				removeDetectTouch();
			}
		};
		detectTouchReg = scienceGadgetArea.addDomHandler(detectTouch,
				TouchStartEvent.getType());

		//
	}

	void removeDetectTouch() {
		if (detectTouchReg != null) {
			detectTouchReg.removeHandler();
		}
	}

	class HistoryChange<String> implements ValueChangeHandler<String> {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
			Activity newActivity;
			newActivity = Activity
					.valueOf(((java.lang.String) event.getValue()).split("_")[0]);

			if (AlgebraActivity.eqPanel != null) {
				AlgebraActivity.eqPanel.removeFromParent();
			}

			switch (newActivity) {
			case browser:
				// switchToBrowser();
				if (browserPanel != null) {
					scienceGadgetArea.clear();
					scienceGadgetArea.add(browserPanel);
				}
				break;
			case algebra:
				if (algebraActivity != null) {
					scienceGadgetArea.clear();
					scienceGadgetArea.add(algebraActivity);
					AlgebraActivity.reloadEquationPanel(null, null);
				}
				break;
			case insert_symbol:
				break;
			case random_spec:
				break;
			case conversion:
				if (conversionActivity != null) {
					scienceGadgetArea.clear();
					scienceGadgetArea.add(conversionActivity);
				}
				break;

			}
		}
	}

}
