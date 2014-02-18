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

import java.util.HashMap;
import java.util.LinkedHashMap;
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
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.MathTree;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.client.algebra.transformations.Rule;
import com.sciencegadgets.client.conversion.ConversionActivity;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;

public class Moderator implements EntryPoint {

	private static int historyCount = 0;
	private int SGAWidth;
	private static int SGAHeight;
	public static RandomSpecPanel randomSpec = null;
	public static AbsolutePanel scienceGadgetArea = RootPanel
			.get(CSS.SCIENCE_GADGET_AREA);
	private HandlerRegistration detectTouchReg;
	public static boolean isTouch = false;

	private static Widget currentActivity = null;
	private static AlgebraActivity currentAlgebraActivity;

	private static final HashMap<String, Widget> algebraActivityMap = new HashMap<String, Widget>();

	private static ActivityType currentActivityType = null;
	public static final LinkedList<Prompt> prompts = new LinkedList<Prompt>();
	public static boolean isInEasyMode = false;
	public static final String HISTORY_DELIMITER = "_";

	@Override
	public void onModuleLoad() {
//		History.addValueChangeHandler(new HistoryChange<String>());
//
//		// // Resize area when window resizes
//		fitWindow();
//		Window.addResizeHandler(new ResizeAreaHandler());
//
//		detectTouch();
//
//		switchToBrowser();

		
		
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

	public enum ActivityType {
		browser, algebra, conversion;
	}

	public static void setActivity(ActivityType activityType, Widget activity) {
		// if (!activity.equals(currentActivity)) {
		currentActivityType = activityType;
		String tolken = activityType.toString() + HISTORY_DELIMITER
				+ historyCount++;
		algebraActivityMap.put(tolken, activity);
		History.newItem(tolken);
		// }
	}

	public static AlgebraActivity getCurrentAlgebraActivity() {
		return currentAlgebraActivity;
	}

	public static MathTree getCurrentMathTree() {
		return currentAlgebraActivity.getMathTree();
	}

	public static void reloadEquationPanel(String changeComment, Rule rule) {
		currentAlgebraActivity.reloadEquationPanel(changeComment, rule);
	}

	public static void makeAlgebra(Element mathML, boolean inEditMode) {
		currentAlgebraActivity = new AlgebraActivity(mathML, inEditMode);
		currentActivity = currentAlgebraActivity;
		
		switchToAlgebra();
	}
	public static void switchToAlgebra() {

		scienceGadgetArea.clear();
		scienceGadgetArea.add(currentAlgebraActivity);

//		reloadEquationPanel(null, null);

		setActivity(ActivityType.algebra, currentAlgebraActivity);
	}

	public static void switchToConversion(MathNode node) {

		ConversionActivity conversionActivity = new ConversionActivity();
		currentActivity = conversionActivity;
		currentActivity.getElement().setAttribute("id", CSS.CONVERSION_ACTIVITY);

		conversionActivity.load(node);

		scienceGadgetArea.clear();
		scienceGadgetArea.add(currentActivity);

		setActivity(ActivityType.conversion, currentActivity);
	}

	public void switchToBrowser() {

		currentActivity = new EquationBrowser();

		scienceGadgetArea.clear();
		scienceGadgetArea.add(currentActivity);

		setActivity(ActivityType.browser, currentActivity);
	}

	class ResizeAreaHandler implements ResizeHandler {
		Timer resizeTimer = new Timer() {
			@Override
			public void run() {
				fitWindow();
				if (ActivityType.algebra.equals(currentActivityType)) {
					reloadEquationPanel(null, null);
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
			java.lang.String token = ((java.lang.String) event.getValue());

			if ("".equals(token)) {
				return;
			}

			Widget activity = algebraActivityMap.get(token);

			if (activity != null) {
				if (currentActivity == activity) {
				} else {
					scienceGadgetArea.clear();
					scienceGadgetArea.add(activity);
					currentActivity = activity;

				}
			}
			if (currentActivity instanceof AlgebraActivity) {
				reloadEquationPanel(null, null);
			}
			
		}
	}

}
