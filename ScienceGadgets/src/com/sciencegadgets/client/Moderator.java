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
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.client.algebra.transformations.Rule;
import com.sciencegadgets.client.conversion.ConversionActivity;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Prompt;

public class Moderator implements EntryPoint {

	private int SGAWidth;
	private static int SGAHeight;
	public static RandomSpecPanel randomSpec = null;
	public static AbsolutePanel scienceGadgetArea = RootPanel
			.get(CSS.SCIENCE_GADGET_AREA);
	private HandlerRegistration detectTouchReg;
	public static boolean isTouch = false;

	private static ActivityType currentActivityType = null;
	private static AlgebraActivity algebraActivity;
	private static EquationBrowser equationBrowser;
	private static ConversionActivity conversionActivity;

	public static final LinkedList<Prompt> prompts = new LinkedList<Prompt>();
	public static boolean isInEasyMode = false;

	@Override
	public void onModuleLoad() {

		HistoryChange historyChange = new HistoryChange();
		History.addValueChangeHandler(historyChange);

		// // Resize area when window resizes
		fitWindow();
		Window.addResizeHandler(new ResizeAreaHandler());

		detectTouch();

		History.fireCurrentHistoryState();

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
		browser, algebrasolve, algebraedit, conversion;
	}

	public static void setActivity(ActivityType activityType, Widget activity) {
		if (activityType.equals(currentActivityType)) {
			return;
		}
		scienceGadgetArea.clear();
		scienceGadgetArea.add(activity);
		currentActivityType = activityType;
	}

	public static void switchToAlgebra(Element equationXML, boolean inEditMode) {
		switchToAlgebra(equationXML, inEditMode, true);
	}
	public static void switchToAlgebra(Element equationXML, boolean inEditMode, boolean updateHistory) {
		try {
			if (algebraActivity == null || algebraActivity.inEditMode != inEditMode) {
				algebraActivity = new AlgebraActivity(equationXML, inEditMode);
			} else {
				algebraActivity.reCreateEquationTree(equationXML, inEditMode);
			}
			algebraActivity.reloadEquationPanel(null, null, updateHistory);
			ActivityType type = inEditMode ? ActivityType.algebraedit
					: ActivityType.algebrasolve;
			setActivity(type, algebraActivity);
		} catch (Exception e) {
			e.printStackTrace();
			JSNICalls.error(e.toString());
			JSNICalls.error(e.getCause().toString());
			JSNICalls.error(e.getMessage());
			switchToBrowser();
		}
	}

	public static void switchToConversion(EquationNode node) {

		if (conversionActivity == null) {
			conversionActivity = new ConversionActivity();
			conversionActivity.getElement().setAttribute("id",
					CSS.CONVERSION_ACTIVITY);
		}
		conversionActivity.load(node);

		setActivity(ActivityType.conversion, conversionActivity);
	}

	public static void switchToBrowser() {
		if (equationBrowser == null) {
			equationBrowser = new EquationBrowser();
		}
		setActivity(ActivityType.browser, equationBrowser);
	}

	public static AlgebraActivity getCurrentAlgebraActivity() {
		return algebraActivity;
	}

	public static EquationTree getCurrentEquationTree() {
		return algebraActivity.getEquationTree();
	}

	public static void reloadEquationPanel(String changeComment, Rule rule) {
		algebraActivity.reloadEquationPanel(changeComment, rule);
	}

	class ResizeAreaHandler implements ResizeHandler {
		Timer resizeTimer = new Timer() {
			@Override
			public void run() {
				fitWindow();
				if (ActivityType.algebraedit.equals(currentActivityType)
						|| ActivityType.algebrasolve
								.equals(currentActivityType)) {
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
	}

	void removeDetectTouch() {
		if (detectTouchReg != null) {
			detectTouchReg.removeHandler();
		}
	}

	class HistoryChange implements ValueChangeHandler<String> {

		@Override
		public void onValueChange(ValueChangeEvent<String> event) {
//			String token = event.getValue();

			HashMap<Parameter, String> parameterMap = URLParameters
					.getParameterMap();
			String activityParameter = parameterMap.get(Parameter.activity);
			try {
				ActivityType activityType = ActivityType
						.valueOf(activityParameter);
				switch (activityType) {
				case algebraedit:
				case algebrasolve:
					String equationString = parameterMap
							.get(Parameter.equation);
					Element equationXML = new HTML(equationString).getElement()
							.getFirstChildElement();
					switchToAlgebra(equationXML,
							ActivityType.algebraedit.equals(activityType), false);
					break;
				default:
					throw new IllegalArgumentException();
				}
			} catch (NullPointerException | IllegalArgumentException e) {
				switchToBrowser();
				HashMap<Parameter, String> pMap = new HashMap<Parameter, String>();
				pMap.put(Parameter.activity, ActivityType.browser.toString());
				URLParameters.setParameters(pMap, false);
			}

		}
	}

}

// String[] testNumbers = { ".00000010"//
// , ".000001230"//
// , ".00001230"//
// , ".0001230"//
// , ".001230"//
// , ".01230"//
// , ".1230"//
// , "1.23"//
// , "12.3"//
// , "123.0"//
// , "1230.0"//
// , "12300.0"//
// , "123000.0"//
// , "1230000.0"//
// , "12300000.0"//
// , ".0000001234567890"//
// , ".000001234567890"//
// , ".00001234567890"//
// , ".0001234567890"//
// , ".001234567890"//
// , ".01234567890"//
// , ".1234567890"//
// , "1.234567890"//
// , "12.34567890"//
// , "123.4567890"//
// , "1234.567890"//
// , "12345.67890"//
// , "123456.7890"//
// , "1234567.890"//
// , "12345678.90"//
// , "123456789.0"//
// , "1234567890.0"//
// , "12345678900.0"//
// };
// for (String symbol : testNumbers) {
// BigDecimal value = new BigDecimal(symbol);
//
// // Rounded display value stored as inner test
// String displayValue;
// if (value.compareTo(new BigDecimal("1000")) < 0
// && value.remainder(new BigDecimal(".01")).compareTo(
// new BigDecimal(0)) == 0) {
// displayValue = value.stripTrailingZeros().toPlainString();
// }else {
// displayValue = "#";
// }
// // xmlNode.setInnerText(displayValue);
//
// // Full value stored as attribute
// String fullValue = value.stripTrailingZeros().toString();
// // setAttribute(MathAttribute.Value, fullValue);
//
// System.out.println("D " + displayValue + "\tF " + fullValue);
// }
// // for (String symbol : testNumbers) {
// // BigDecimal value = new BigDecimal(symbol);
// // BigDecimal roundedValue = value.round(new MathContext(3));
// // String elipses = value.equals(roundedValue) ? "" : "...";
// // // Rounded display value stored as inner test
// // String displayValue = roundedValue.toPlainString()//
// // .stripTrailingZeros()
// // + elipses;
// // // xmlNode.setInnerText(displayValue);
// // // Full value stored as attribute
// // String fullValue = value.stripTrailingZeros().toString();
// // // setAttribute(MathAttribute.Value, fullValue);
// //
// // System.out.println("D " + displayValue + "\tF " + fullValue);
// // }