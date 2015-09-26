package com.sciencegadgets.client;

import java.math.BigDecimal;
import java.util.HashMap;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Moderator.ActivityType;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.ConstantRandomizer;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.ui.ColorPalette;
import com.sciencegadgets.shared.dimensions.UnitAttribute;

class HistoryChange implements ValueChangeHandler<String> {

	@Override
	public void onValueChange(ValueChangeEvent<String> event) {
		// String token = event.getValue();

		HashMap<Parameter, String> parameterMap = URLParameters
				.getParameterMap();
		
		String easyParameter = parameterMap.get(Parameter.easy);
		if (URLParameters.TRUE.equalsIgnoreCase(easyParameter)) {
			Moderator.isInEasyMode = true;
		}

		String activityParameter = parameterMap.get(Parameter.activity);
		
		String palette = parameterMap.get(Parameter.themecolor);
		ColorPalette.SET_PALETTE(palette);

		// welcomePageArea.setVisible(false);
		// scienceGadgetArea.setVisible(true);

		try {
			ActivityType activityType = ActivityType
					.valueOf(activityParameter);
			switch (activityType) {
			case interactiveequation:
				//TODO
				parameterMap = ConstantRandomizer.insertRandomProvided(parameterMap);
				// fall through
			case editequation:
			case editsolvegoal:
			case editcreategoal:
				String equationString = parameterMap
				.get(Parameter.equation);
				Element equationXML = new HTML(equationString).getElement()
						.getFirstChildElement();
				Moderator.switchToAlgebra(equationXML,// null,
						activityType, true);
				break;
			case problem:
				String idStr = parameterMap.get(Parameter.problemid);
				try {
					long id = Long.parseLong(idStr);
					DataModerator.database.getProblem(id,
							new AsyncCallback<Problem>() {
								@Override
								public void onSuccess(Problem problem) {
									Moderator.switchToProblem(problem);
								}

								@Override
								public void onFailure(Throwable arg0) {
									Window.alert("Challenge Not Found");
								}
							});
				} catch (NumberFormatException e) {
					JSNICalls
							.error("ID for problem must be of type 'long'");
				}
				break;
			case conversion:
				String initialValue = parameterMap
						.get(Parameter.conversionvalue);
				try{
					new BigDecimal(initialValue);
				}catch (NumberFormatException e) {
					break;
				}
				UnitAttribute unitAttribute = new UnitAttribute(
						parameterMap.get(Parameter.unitattribute));
				Moderator.switchToConversion(initialValue, unitAttribute, false, false);
				break;
			case browser:
				Moderator.switchToBrowser();
			default:
				throw new IllegalArgumentException();
			}

		} catch (NullPointerException | IllegalArgumentException e) {
			Moderator.switchToBrowser();
			// HashMap<Parameter, String> pMap = new HashMap<Parameter,
			// String>();
			// pMap.put(Parameter.activity,
			// ActivityType.browser.toString());
			// URLParameters.setParameters(pMap, false);

			// Window.Location.replace("/blog/index.html");

			// scienceGadgetArea.setVisible(false);
			// welcomePageArea.setVisible(true);

			// currentActivityType = ActivityType.blog;
		}
	}
}