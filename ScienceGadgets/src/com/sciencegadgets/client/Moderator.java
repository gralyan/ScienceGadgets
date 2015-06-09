/*******************************************************************************
 *     This file is part of ScienceGadgets, a collection of educational tools
 *     Copyright (C) 2012-2015 by John Gralyan
 *
 *     ScienceGadgets is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as
 *     published by the Free Software Foundation, either version 3 of
 *     the License, or (at your option) any later version.
 *
 *     ScienceGadgets is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *     
 *     Contact us at info@sciencegadgets.org
 *******************************************************************************/
package com.sciencegadgets.client;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
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
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.URLParameters.Parameter;
import com.sciencegadgets.client.algebra.AlgebraActivity;
import com.sciencegadgets.client.algebra.ConstantRandomizer;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.RandomSpecPanel;
import com.sciencegadgets.client.algebra.transformations.Skill;
import com.sciencegadgets.client.challenge.ProblemDetails;
import com.sciencegadgets.client.conversion.ConversionActivity;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.entities.users.Student;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Resizable;
import com.sciencegadgets.shared.dimensions.UnitAttribute;

public class Moderator implements EntryPoint {

	private int SGAWidth;
	private static int SGAHeight;
	public static RandomSpecPanel randomSpec = null;
	public static final AbsolutePanel scienceGadgetArea = RootPanel
			.get(CSS.SCIENCE_GADGET_AREA);
	// private static final AbsolutePanel welcomePageArea = RootPanel
	// .get(CSS.WELCOME_PAGE_AREA);
	private HandlerRegistration detectTouchReg;
	public static boolean isTouch = false;

	private static ActivityType currentActivityType = null;
	private static AlgebraActivity algebraActivity;
	public static EquationBrowser equationBrowser;
	private static ConversionActivity conversionActivity;
	private static ProblemDetails problemActivity;

	public static final LinkedList<Resizable> resizables = new LinkedList<Resizable>();
	private static Student student = new Student("guest");
	public static boolean isInEasyMode = true;
	public static final HashMap<Skill, Integer> skillsIncreasedCollection = new HashMap<Skill, Integer>();
	public static final LinkedList<Badge> newBadgeCollection = new LinkedList<Badge>();

	// public final static Sounds SOUNDS = new Sounds();

	@Override
	public void onModuleLoad() {

		HistoryChange historyChange = new HistoryChange();
		History.addValueChangeHandler(historyChange);

		// // Resize area when window resizes
		fitWindow();
		Window.addResizeHandler(new ResizeAreaHandler());

		detectTouch();

		History.fireCurrentHistoryState();
		//
		// // Blobs
		// // scienceGadgetArea.add(new UploadButton());
		//
		// // try {
		// // TestBot_Addition.deployTestBot();
		// // } catch (TopNodesNotFoundException e) {
		// // e.printStackTrace();
		// // } catch (Exception e) {
		// // e.printStackTrace();
		// // }

	}

	public static Student getStudent() {
		return student;
	}

	public enum ActivityType {
		browser, problem, algebrasolve, algebraedit, algebrasolvegoal, algebracreategoal, algebrasimplifyquiz, conversion;

		public boolean isInEditMode() {
			switch (this) {
			case algebraedit:
			case algebrasimplifyquiz:
			case algebracreategoal:
				return true;
			case algebrasolve:
			case algebrasolvegoal:
				return false;
			default:
				return false;
			}
		}
	}

	public static void setActivity(ActivityType activityType, Widget activity) {

		if (activityType.equals(currentActivityType)) {
			return;
		}
		scienceGadgetArea.clear();
		scienceGadgetArea.add(activity);
		currentActivityType = activityType;
	}

	public static void switchToAlgebra(Element equationXML,
			boolean sameEquation, ActivityType activityType,
			boolean updateHistory) {
		Equation eq = sameEquation && algebraActivity != null ? algebraActivity
				.getEquation() : null;
		switchToAlgebra(equationXML, eq, activityType, updateHistory);
	}

	public static void switchToAlgebra(Element equationXML, Equation equation,
			ActivityType activityType, boolean updateHistory) {
		EquationTree eTree = new EquationTree(equationXML,
				activityType.isInEditMode());
		switchToAlgebra(eTree, equation, activityType, updateHistory);
	}

	public static void switchToAlgebra(EquationTree equationTree,
			Equation equation, ActivityType activityType, boolean updateHistory) {
		try {

			if (algebraActivity == null
					|| algebraActivity.getActivityType() != activityType) {
				algebraActivity = new AlgebraActivity(equationTree, equation,
						activityType);
			} else {
				algebraActivity.setEquationTree(equationTree, equation);
				if (ActivityType.algebrasolve == activityType) {
					algebraActivity.reCreateAlgHistory();
				}
			}

			isInEasyMode = isInEasyMode || activityType == ActivityType.algebrasolvegoal;

			setActivity(activityType, algebraActivity);
			algebraActivity.reloadEquationPanel(null, null, updateHistory, null);
		} catch (Exception e) {
			e.printStackTrace();
			JSNICalls.error(e.toString());
			JSNICalls.error(e.getCause().toString());
			JSNICalls.error(e.getMessage());
			switchToBrowser();
		}
	}

	public static void switchToConversion(EquationNode node,
			Equation variableEquation) {

		switchToConversion(node.getSymbol(), node.getUnitAttribute(), true);
		conversionActivity.setNode(node);
		conversionActivity.setVariableEquation(variableEquation);
	}

	public static void switchToConversion(String initialValue,
			UnitAttribute unitAttribute, boolean allowConvertButton) {
		switchToConversion(initialValue, unitAttribute, allowConvertButton, true);
	}
	public static void switchToConversion(String initialValue,
			UnitAttribute unitAttribute, boolean allowConvertButton, boolean updateParameters) {

		if (conversionActivity == null) {
			conversionActivity = new ConversionActivity();
			conversionActivity.getElement().setAttribute("id",
					CSS.CONVERSION_ACTIVITY);
		}
		conversionActivity
				.load(initialValue, unitAttribute, allowConvertButton);

		setActivity(ActivityType.conversion, conversionActivity);

		if(updateParameters) {
		HashMap<Parameter, String> parameterMap = new HashMap<Parameter, String>();
		parameterMap.put(Parameter.activity, ActivityType.conversion.name());
		parameterMap.put(Parameter.conversionvalue, initialValue);
		parameterMap.put(Parameter.unitattribute, unitAttribute.toString());
		URLParameters.setParameters(parameterMap, false);
		}
	}

	public static void switchBackToProblem() {
		if (problemActivity == null) {
			switchToBrowser();
			return;
		}
		boolean isProblemSolved = problemActivity.updateSolvedEquation();
		if (isProblemSolved) {
			switchToBrowser();
		} else {
			setActivity(ActivityType.problem, problemActivity);
		}
	}

	public static void switchToProblem(Problem problem) {
		if (problem == null) {
			return;
		}
		if (problemActivity == null) {
			problemActivity = new ProblemDetails();
		}
		problemActivity.loadProblem(problem);
		setActivity(ActivityType.problem, problemActivity);

		HashMap<Parameter, String> parameterMap = new HashMap<Parameter, String>();
		Long id = problem.getId();
		parameterMap.put(Parameter.activity, ActivityType.problem.name());
		parameterMap.put(Parameter.problemid, id.toString());
		URLParameters.setParameters(parameterMap, false);
	}

	public static void switchToBrowser() {
		if (equationBrowser == null) {
			equationBrowser = new EquationBrowser();
		}
		setActivity(ActivityType.browser, equationBrowser);

		HashMap<Parameter, String> parameterMap = new HashMap<Parameter, String>();
		URLParameters.setParameters(parameterMap, false);
	}

	public static AlgebraActivity getCurrentAlgebraActivity() {
		return algebraActivity;
	}

	public static EquationTree getCurrentEquationTree() {
		return algebraActivity.getEquationTree();
	}

	public static void reloadEquationPanel() {
		reloadEquationPanel(null, null);
	}

	public static void reloadEquationPanel(String changeComment, String nodeIdToSelect) {
		reloadEquationPanel(changeComment, (HashMap<Skill, Integer>) null, nodeIdToSelect);
	}

	public static void reloadEquationPanel(String changeComment, Skill skill, String nodeIdToSelect) {
		if (skill == null) {
			reloadEquationPanel(changeComment, nodeIdToSelect);
		} else {
			HashMap<Skill, Integer> skills = new HashMap<Skill, Integer>();
			skills.put(skill, 1);
			reloadEquationPanel(changeComment, skills, nodeIdToSelect);
		}
	}

	public static void reloadEquationPanel(String changeComment,
			HashMap<Skill, Integer> skills, String nodeIdToSelect) {
		algebraActivity.reloadEquationPanel(changeComment, skills, true, nodeIdToSelect);
	}

	public static void increaseSkill(Skill skill, int increase) {
		HashMap<Skill, Integer> skills = new HashMap<Skill, Integer>();
		skills.put(skill, increase);
		increaseSkills(skills);
	}

	public static void increaseSkills(HashMap<Skill, Integer> skillsIncrease) {

		if (skillsIncrease != null) {
			for (Entry<Skill, Integer> skillEntry : skillsIncrease.entrySet()) {
				HashSet<Badge> newBadges = Moderator.getStudent()
						.increaseSkill(skillEntry.getKey(),
								skillEntry.getValue());

				Integer currentSkillLevel = skillsIncreasedCollection
						.get(skillEntry.getKey());
				if (currentSkillLevel == null) {
					currentSkillLevel = 0;
				}
				skillsIncreasedCollection.put(skillEntry.getKey(),
						skillEntry.getValue() + currentSkillLevel);
				for (Badge newBadge : newBadges) {
					JSNICalls.log("newBadge " + newBadge);
					newBadgeCollection.add(newBadge);
				}
				JSNICalls.log("Skill up of " + skillEntry.getKey() + " by "
						+ skillEntry.getValue() + "\nskills: "
						+ Moderator.getStudent().getSkills() + "\nbadges "
						+ Moderator.getStudent().getBadges() + "\n");
			}
		}
	}

	class ResizeAreaHandler implements ResizeHandler {
		Timer resizeTimer = new Timer() {
			@Override
			public void run() {
				fitWindow();
				switch (currentActivityType) {
				case algebraedit:
				case algebrasolve:
					reloadEquationPanel();
					break;
				case conversion:
					conversionActivity.reloadEquation();
					break;
				default:
					break;
				}
				for (Resizable resizable : resizables) {
					resizable.resize();
				}
			}
		};

		@Override
		public void onResize(ResizeEvent event) {
			resizeTimer.schedule(250);
		}
	}

	public static boolean meetsRequirement(Badge badge) {
		if (student == null || badge == null) {
			return false;
		} else if (isInEasyMode) {
			return true;
		} else {
			return student.hasBadge(badge);
		}
	}

	public static boolean meetsRequirements(Badge... badges) {
		HashSet<Badge> badgeSet = new HashSet<Badge>();
		for (Badge badge : badges) {
			badgeSet.add(badge);
		}
		return meetsRequirements(badgeSet);
	}

	public static boolean meetsRequirements(HashSet<Badge> badges) {
		if (student == null || badges == null) {
			JSNICalls.error("NO STUDENT or BADGES: student: " + student
					+ ", badges:" + badges);
			return false;
		} else if (isInEasyMode) {
			JSNICalls.warn("isInEasyMode " + isInEasyMode);
			return true;
		} else {
			JSNICalls.warn("has: " + student.getBadges() + ", needs: " + badges
					+ ", fulfilled? " + student.hasBadges(badges));
			return student.hasBadges(badges);
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
			// String token = event.getValue();

			HashMap<Parameter, String> parameterMap = URLParameters
					.getParameterMap();
			
			String easyParameter = parameterMap.get(Parameter.easy);
			if (URLParameters.TRUE.equalsIgnoreCase(easyParameter)) {
				isInEasyMode = true;
			}

			String activityParameter = parameterMap.get(Parameter.activity);

			// welcomePageArea.setVisible(false);
			// scienceGadgetArea.setVisible(true);

			try {
				ActivityType activityType = ActivityType
						.valueOf(activityParameter);
				switch (activityType) {
				case algebrasolve:
					//TODO
					parameterMap = ConstantRandomizer.insertRandomProvided(parameterMap);
					// fall through
				case algebraedit:
				case algebrasolvegoal:
				case algebracreategoal:
					String equationString = parameterMap
					.get(Parameter.equation);
					Element equationXML = new HTML(equationString).getElement()
							.getFirstChildElement();
					switchToAlgebra(equationXML, null, activityType, true);
					break;
				case problem:
					String idStr = parameterMap.get(Parameter.problemid);
					try {
						long id = Long.parseLong(idStr);
						DataModerator.database.getProblem(id,
								new AsyncCallback<Problem>() {
									@Override
									public void onSuccess(Problem problem) {
										switchToProblem(problem);
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
					switchToConversion(initialValue, unitAttribute, false, false);
					break;
				case browser:
					switchToBrowser();
				default:
					throw new IllegalArgumentException();
				}

			} catch (NullPointerException | IllegalArgumentException e) {
				switchToBrowser();
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
}
