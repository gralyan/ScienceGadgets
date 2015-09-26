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
package com.sciencegadgets.client.ui;

public class CSS {
	
//////////////////////////////////////////////
//equation.css
//////////////////////////////////////////////
	
public static final String INTERACTIVE_EQUATION = "sg-interactiveEquation";
public static final String DISPLAY_WRAPPER = "sg-displayWrapper";
public static final String EDIT_MODE = "sg-editMode";
public static final String PARENT_WRAPPER = "sg-parentWrapper";

/*---------------Wrappers---------------*/
public static final String EQUATION = "sg-Equation";
public static final String NUMBER = "sg-Number";
public static final String VARIABLE = "sg-Variable";
public static final String OPERATION = "sg-Operation";
public static final String SUM = "sg-Sum";
public static final String TERM = "sg-Term";
public static final String FRACTION = "sg-Fraction";
public static final String EXPONENTIAL = "sg-Exponential";
public static final String TRIG = "sg-Trig";
public static final String LOG = "sg-Log";

/*---------------EquationHTML (display)---------------*/
public static final String EXPRESSION = "sg-Expression";
public static final String IN_EQUATION = "sg-in-equation";
public static final String IN_SUM = "sg-in-sum";
public static final String IN_TERM = "sg-in-term";
public static final String IN_EXPONENTIAL_BASE = "sg-in-exponential-base";
public static final String IN_EXPONENTIAL_EXPONENT = "sg-in-exponential-exponent";
public static final String IN_FRACTION_NUMERATOR = "sg-in-fraction-numerator";
public static final String FRACTION_LINE = "sg-fraction-line";
public static final String IN_FRACTION_DENOMINATOR = "sg-in-fraction-denominator";
public static final String IN_LOG = "sg-in-log";
public static final String IN_TRIG = "sg-in-trig";
public static final String IN_LOG_BASE = "sg-in-log-base";

public static final String PLUS = "sg-plus";
public static final String MINUS = "sg-minus";
public static final String FENCED = "sg-fenced";
public static final String FUNCTION_NAME = "sg-functionName";
public static final String UNIT = "sg-unit";
public static final String SUBSCRIPT = "sg-subscript";
public static final String NUMBER_VALUE = "sg-numberValue";

//////////////////////////////////////////////
//ScienceGadgets.css
//////////////////////////////////////////////

public static final String SCIENCE_GADGET_AREA = "sg-scienceGadgetArea";
public static final String WELCOME_PAGE_AREA = "sg-welcomePageArea";
public static final String BLOG_AREA = "sg-blogArea";
public static final String TEXT_CENTER = "sg-textCenter";
public static final String FILL_PARENT = "sg-fillParent";
public static final String FIT_PARENT_HTML = "sg-fitParentHTML";
public static final String BORDER_RADIUS_SMALL = "sg-borderRadiusSmall";
/*---------------Browsers---------------*/
public static final String MAIN_TITLE = "sg-mainTitle";
public static final String EQUATION_BROWSER = "sg-equationBrowser";
public static final String MODES = "sg-modes";
public static final String ROW_HEADER = "sg-rowHeader";
public static final String ROW_SUB_HEADER = "sg-rowSubHeader";
public static final String ALG_BROWSER = "sg-algBrowser";
public static final String ALG_BROWSER_PANEL = "sg-algBrowserPanel";
public static final String EQ_GENERATOR_PANEL = "sg-eqGeneratorPanel";
public static final String CHALLENGE_GENERATOR_PANEL = "sg-challengeBrowserPanel";
public static final String EQUATION_SOLVE_BUTTON = "sg-equationSolveButton";
public static final String SCI_BROWSER = "sg-sciBrowser";
public static final String SCI_BROWSER_VAR = "sg-sciBrowseVar";
public static final String SCI_BROWSER_EQ = "sg-sciBrowseEq";
public static final String SCI_BROWSER_SUM = "sg-sciBrowseSum";
public static final String DIAGRAM_MEASURE = "sg-diagramMeasure";
public static final String ACTIVITY_SELECTION_PANEL = "sg-activitySelectionPanel";
public static final String ACTIVITY_DETAILS_PANEL = "sg-activityDetailsPanel";
public static final String CONVERSION_SPECIFICATION = "sg-conversionSpec";
/*---------------ConversionActivity---------------*/
public static final String CONVERSION_ACTIVITY = "sg-conversionActivity";
/*---------------SelectionPanel---------------*/
public static final String SELECTION_PANEL = "sg-selectionPanel";
public static final String SELECTION_PANEL_CELL = "sg-selectionPanelCell";
public static final String SELECTED_CELL = "sg-selectedCell";
public static final String HIGHLIGHTED_CELL = "sg-highlightedCell";
public static final String SEARCH_BOX = "sg-searchBox";
public static final String UNIT_SELECTION = "sg-unitSelection";
/*---------------AlgOut---------------*/
public static final String ALG_OUT = "sg-algOut";
public static final String ALG_OUT_ROW = "sg-algOutRow";
public static final String ALG_OUT_EQ_ROW = "sg-algOutEqRow";
public static final String ALG_OUT_EQ_LEFT = "sg-algOutEqLeft";
public static final String ALG_OUT_EQ_EQUALS = "sg-algOutEqEquals";
public static final String ALG_OUT_EQ_RIGHT = "sg-algOutEqRight";
public static final String ALG_OUT_CHANGE_ROW = "sg-algOutChangeRow";
public static final String ALG_OUT_RULE_ROW = "sg-algOutRuleRow";
/*---------------changes---------------*/
public static final String HIGHLIGHT = "sg-highlight";
public static final String LINE_THROUGH = "sg-lineThrough";
/*---------------wrappers---------------*/
public static final String SELECTED_WRAPPER = "sg-selectedWrapper";
public static final String SELECTED_DROP_WRAPPER = "sg-selectedDropWrapper";
public static final String SELECTION_DETAILS = "sg-selectionDetails";
public static final String SELECTION_DETAILS_Value = "sg-selectionDetailValue";
public static final String CONVERSION_WRAPPER = "sg-conversionWrapper";
public static final String DROP_ENTER_RESPONSE = "sg-dropEnterResponse";
public static final String EQ_PANEL = "sg-eqPanel";
public static final String EQ_LAYER = "sg-eqLayer";
public static final String CAN_ZOOM_OUT = "sg-canZoomOut";
public static final String LAYOUT_ROW = "sg-layoutRow";
/*---------------popups---------------*/
//public static final String DIALOG_BOX = "gwt-DialogBox";
//public static final String POPUP_PANEL_GLASS = "gwt-PopupPanelGlass";
public static final String OPTIONS_POPUP = "sg-optionsPopup";
/*---------------toggle---------------*/
public static final String TOGGLE_SLIDE = "sg-toggleSlide";
public static final String TOGGLE_OPTION = "sg-toggleOption";
public static final String TOGGLE_OPTION_SELECTED = "sg-toggleOptionSelected";
/*---------------prompts---------------*/
public static final String NUMBER_DISPLAY = "sg-numberDisplay";
public static final String PROMPT_MAIN = "sg-promptMain";
public static final String KEY_PAD_NUMERICAL = "sg-keyPadNumerical";
public static final String DOUBLE_FONT_SIZE = "sg-doubleFontSize";
public static final String INVALID_INPUT = "sg-invalidInput";
public static final String EXAMPLE_HTML = "sg-exampleHtml";
public static final String ESTABLISHED_QUANTITY_AREA = "sg-establishedQuantityArea";
public static final String ESTABLISHED_QUANTITY_SELECTION = "sg-establishedQuantitySelection";
public static final String QUANTITY_PROMPT_SPEC = "sg-quantityPromptSpec";
public static final String SUBSTITUTION_CELL = "sg-substitutionCell";
public static final String LINK_PROMPT_CODE_DEFAULT = "sg-linkPromptCodeDefault";



//////////////////////////////////////////////
//buttons.css
//////////////////////////////////////////////

public static final String TRANSFORMATION_BUTTON = "sg-transformationButton";
public static final String SAVE_EQUATION_BUTTON = "sg-saveEquationButton";
public static final String CHANGE_NODE_BUTTON = "sg-changeNodeButton";
public static final String BOTH_SIDES_BUTTON = "sg-bothSidesButton";
public static final String TAB = "sg-tab";
public static final String SMALLEST_BUTTON = "sg-smallestButton";
public static final String MEDIUM_BUTTON = "sg-mediumButton";
public static final String BOTH_SIDES_BUTTON_SELECTED = "sg-bothSidesButtonSelected";
public static final String BOTH_SIDES_PANEL = "sg-bothSidesPanel";
public static final String SIMPLIFY_PANEL = "sg-simplifyPanel";
public static final String INSERT_VAR_DESCRIPTION = "sg-insertVarDescription";
public static final String INSERT_VAR_IMAGE = "sg-insertVarImage";
public static final String INSERT_VAR_IMAGE_ACTIVE = "sg-insertVarImageActive";
public static final String INSERT_VAR_SOLVE = "sg-insertVarSolve";
public static final String INSERT_VAR_SOLVE_ACTIVE = "sg-insertVarSolveActive";
public static final String GENERATE_BUTTON = "sg-generateButton";
public static final String ACTIVITY_SELECTION_BUTTON = "sg-activitySelectionButton";
public static final String ACTIVITY_SELECTION_BUTTON_SELECTED = "sg-activitySelectionButtonSelected";
public static final String MAKE_EQ_BUTTON = "sg-makeEqButton";
public static final String CREATE_GOAL_BUTTON = "sg-createGoalButton";
public static final String OK_PROMPT_BUTTON = "sg-okPromptButton";
public static final String INSERT_EQUATION_OPTION = "sg-insertEquationOption";
public static final String EQUATION_SELECTION = "sg-equationSelection";

}
