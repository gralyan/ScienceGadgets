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
package com.sciencegadgets.client.algebra.edit;

import java.util.ArrayList;
import java.util.LinkedList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.sciencegadgets.client.DatabaseHelperAsync;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.UploadButton;
import com.sciencegadgets.client.algebra.EquationTree;
import com.sciencegadgets.client.algebra.EquationTree.EquationNode;
import com.sciencegadgets.client.algebra.edit.ProblemSpecification.VariableIdPanel;
import com.sciencegadgets.client.algebra.edit.ProblemSpecification.VariableIdPanel.VarIDButton;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.entities.users.Badge;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Prompt;
import com.sciencegadgets.client.ui.SelectionButton;
import com.sciencegadgets.client.ui.SelectionPanel;
import com.sciencegadgets.client.ui.SelectionPanel.Cell;
import com.sciencegadgets.client.ui.SelectionPanel.SelectionHandler;
import com.sciencegadgets.shared.Diagram;
import com.sciencegadgets.shared.TypeSGET;

public class ProblemSpecification extends Prompt {

	private DatabaseHelperAsync dataBase = DataModerator.database;

	private EquationTree eTree;

	public static final String VARIABLE_START = "-[";
	public static final String VARIABLE_END = "]-";

	protected FlowPanel topPanel = new FlowPanel();
	protected FlowPanel leftPanel = new FlowPanel();
	protected FlowPanel rightPanel = new FlowPanel();

	protected SelectionButton newProblemButton;
	protected SelectionButton addToProblemButton;

	protected Label titleLabel = new Label("Title");
	protected TextBox titleInput = new TextBox();
	protected Label descriptionLabel = new Label("Description");
	protected TextBox descriptionInput = new TextBox();

	protected VariableIdPanel varIdPanel = new VariableIdPanel();

	protected ImageContainer imageContainer = new ImageContainer(this);

	protected Badge requiredBadge = null;
	protected Problem problemSelected = null;

	protected final SelectionPanel problemSelection = new SelectionPanel(
			"Problems");
	protected SelectionPanel badgeSelection = new SelectionPanel(
			"RequiredBadge");

	protected Diagram diagram;

	ProblemSpecification(SaveButtonHandler saveButtonHandler) {

		descriptionInput.setStyleName(CSS.INSERT_VAR_DESCRIPTION);
		descriptionInput.addFocusHandler(new FocusHandler() {
			@Override
			public void onFocus(FocusEvent arg0) {
				varIdPanel.setInsertDescriptionMode();
			}
		});

		newProblemButton = new SelectionButton("New Problem") {

			@Override
			protected void onSelect() {
				refresh();
				newProblemButton.setEnabled(false);

				if (!badgeSelection.hasSelectionHandler()) {
					badgeSelection.addSelectionHandler(new SelectionHandler() {
						@Override
						public void onSelect(Cell selected) {
							requiredBadge = (Badge) selected.getEntity();
						}
					});
					for (Badge badge : Badge.values()) {
						badgeSelection.add(badge.toString(), badge.name(),
								badge);
					}
				}
				leftPanel.add(titleLabel);
				leftPanel.add(titleInput);
				titleInput.setEnabled(true);
				leftPanel.add(descriptionLabel);
				leftPanel.add(descriptionInput);
				leftPanel.add(varIdPanel);
				leftPanel.add(new ImageUploadButton(ProblemSpecification.this));
				rightPanel.add(badgeSelection);
			}
		};

		addToProblemButton = new SelectionButton("Add to Problem") {

			@Override
			protected void onSelect() {
				refresh();
				addToProblemButton.setEnabled(false);

				if (!problemSelection.hasSelectionHandler()) {
					problemSelection
							.addSelectionHandler(new ProblemSelectionHandler());
					dataBase.getProblemsByBadges(null,
							new AsyncCallback<ArrayList<Problem>>() {

								@Override
								public void onFailure(Throwable arg0) {
								}

								@Override
								public void onSuccess(
										ArrayList<Problem> problems) {
									for (Problem problem : problems) {
										problemSelection.add(
												problem.getTitle(), null,
												problem);
									}
								}
							});
				}
				leftPanel.add(titleLabel);
				leftPanel.add(titleInput);
				titleInput.setEnabled(false);
				leftPanel.add(descriptionLabel);
				leftPanel.add(descriptionInput);
				leftPanel.add(varIdPanel);
				rightPanel.add(problemSelection);
			}
		};

		topPanel.setSize("100%", "10%");
		leftPanel.setSize("70%", "90%");
		rightPanel.setSize("30%", "90%");
		leftPanel.getElement().getStyle().setOverflowY(Overflow.AUTO);
		leftPanel.addStyleName(CSS.LAYOUT_ROW);
		rightPanel.addStyleName(CSS.LAYOUT_ROW);
		add(topPanel);
		add(leftPanel);
		add(rightPanel);

		newProblemButton.addStyleName(CSS.LAYOUT_ROW);
		newProblemButton.getElement().getStyle().setMarginLeft(5, Unit.PX);
		topPanel.add(newProblemButton);

		addToProblemButton.addStyleName(CSS.LAYOUT_ROW);
		topPanel.add(addToProblemButton);

	}

	void refresh() {
		newProblemButton.setEnabled(true);
		addToProblemButton.setEnabled(true);

		titleInput.setText("");
		descriptionInput.setText("");
		requiredBadge = null;
		problemSelected = null;

		leftPanel.clear();
		rightPanel.clear();

		diagram = null;

		varIdPanel.refresh(eTree);
	}
	
	public void setEquationTree(EquationTree eTree) {
		this.eTree = eTree;
	}

	@Override
	public void appear() {
		refresh();
		super.appear();
	}

	class ProblemSelectionHandler implements SelectionPanel.SelectionHandler {

		@Override
		public void onSelect(Cell selected) {

			problemSelected = (Problem) selected.getEntity();
			descriptionInput.setText(problemSelected.getDescription());
			titleInput.setText(problemSelected.getTitle());
			varIdPanel.toSolve.setTitle(problemSelected.getToSolveID());
			varIdPanel.toSolve.setText(problemSelected.getToSolveID());

			// Make sure the new equation doesn't have
			// node id's that occur in any equation in
			// the selected problem

			// Collect existing id's
			ArrayList<String> ids = new ArrayList<String>();
			for (Equation eq : problemSelected.getEquations()) {
				NodeList<Element> nodes = new HTML(eq.getMathML()).getElement()
						.getElementsByTagName("*");
				for (int i = 0; i < nodes.getLength(); i++) {
					String id = nodes.getItem(i).getAttribute("id");
					if (ids.contains(id)) {
						JSNICalls
								.error("There are multiple nodes with the same id: "
										+ id
										+ " in the problem: "
										+ problemSelected.getTitle()
										+ "\n"
										+ problemSelected);
					} else {
						ids.add(id);
					}
				}
			}

			for (EquationNode node : eTree.getNodes()) {
				if (ids.contains(node.getId())) {
					node.changeId(ids);
				}
			}
		}

	}

	class VariableIdPanel extends FlowPanel {

		static final int INSERT_DESCRIPTION = 0;
		static final int INSERT_IMAGE = 1;
		static final int INSERT_SOLVE = 2;
		private int mode;
		VarIDButton activeButton = null;
		public final Button toSolve = new Button(TypeSGET.NOT_SET, new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent arg0) {
				setInsertSolveMode();
			}
		});

		public void refresh(EquationTree eTree) {
			clear();
			
			toSolve.setTitle("");
			toSolve.setText(TypeSGET.NOT_SET);

			add(new Label("Measurements: "));

			for (final EquationNode node : eTree
					.getNodesByType(TypeSGET.Variable)) {
				add(new VarIDButton(node, true));
			}
			for (final EquationNode node : eTree
					.getNodesByType(TypeSGET.Number)) {
				add(new VarIDButton(node, false));
			}
			
			add(new Label(" Variable to Solve: "));
			add(toSolve);
			
			setInsertDescriptionMode();
		}

		void setInsertSolveMode() {
			mode = INSERT_SOLVE;
			setStyleName(CSS.INSERT_VAR_SOLVE);
			clearActive();
			toSolve.addStyleName(CSS.INSERT_VAR_SOLVE_ACTIVE);
		}
		void setInsertDescriptionMode() {
				mode = INSERT_DESCRIPTION;
				setStyleName(CSS.INSERT_VAR_DESCRIPTION);
				clearActive();
				toSolve.removeStyleName(CSS.INSERT_VAR_SOLVE_ACTIVE);
		}

		/**
		 * Sets the mode, style, and clears the active button if the mode was
		 * different
		 * 
		 * @return - true if mode was switched
		 */
		boolean setInsertImageMode() {
			if (mode != INSERT_IMAGE) {
				mode = INSERT_IMAGE;
				setStyleName(CSS.INSERT_VAR_IMAGE);
				clearActive();
				toSolve.removeStyleName(CSS.INSERT_VAR_SOLVE_ACTIVE);
				return true;
			} else {
				return false;
			}
		}

		void clearActive() {
			if (activeButton != null) {
				activeButton.removeStyleName(CSS.INSERT_VAR_IMAGE_ACTIVE);
				activeButton = null;
			}

		}

		class VarIDButton extends Button {

			EquationNode node;
			Label imageLabel;
			boolean isVariable;

			VarIDButton(final EquationNode node, boolean isVariable) {
				super(node.getSymbol());
				this.isVariable = isVariable;

				this.node = node;
				imageLabel = new Label(node.getSymbol());
				imageLabel.setStyleName(CSS.INSERT_VAR_IMAGE);

				addClickHandler(new ClickHandler() {

					@Override
					public void onClick(ClickEvent arg0) {

						clearActive();

						if (mode == INSERT_DESCRIPTION) {
							insertDescription();
						} else if (mode == INSERT_IMAGE) {
							insertImageActivate();
						} else if (mode == INSERT_SOLVE) {
							if(VarIDButton.this.isVariable) {
							insertSolve();
							}else {
								toSolve.setText("Variables Only");
								toSolve.setTitle("");
							}
						}
					}
				});
			}

			private void insertDescription() {
				TextBox input = descriptionInput;
				int cursorPos = input.getCursorPos();
				String inputText = input.getText();
				String inputTextPreCursor = inputText.substring(0, cursorPos)
						.trim();
				String inputTextPostCursor = inputText.substring(cursorPos)
						.trim();
				String insertion = " "
						+ VARIABLE_START
						+ node.getId()
						+ VARIABLE_END + " ";
				input.setText(inputTextPreCursor + insertion
						+ inputTextPostCursor);
				input.setFocus(true);
				input.setCursorPos(inputTextPreCursor.length()
						+ insertion.length());
			}

			private void insertImageActivate() {
				activeButton = this;
				setStyleName(CSS.INSERT_VAR_IMAGE_ACTIVE);
			}
			private void insertSolve() {
				toSolve.setText(getText());
				toSolve.setTitle(node.getId());
			}
		}
	}
}

class ImageUploadButton extends UploadButton {
	ImageUploadButton(final ProblemSpecification problemSpec) {
		super();
		addSubmitCompleteHandler(new SubmitCompleteHandler() {

			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {

				String imgurlstart = "<img src='";
				String imgurlmid = "' alt='";
				String imgurlend = "'>";

				String response = event.getResults().replace("\"", "'")
						.replace("&lt;", "<").replace("&gt;", ">");
				if (!response.contains(imgurlstart)
						|| !response.contains(imgurlmid)
						|| !response.contains(imgurlend)) {
					Window.alert("File must be an Image");
					problemSpec.leftPanel
							.add(new ImageUploadButton(problemSpec));
					ImageUploadButton.this.removeFromParent();
					return;
				}
				String imageURL = response.split(imgurlstart)[1]
						.split(imgurlmid)[0];
				String imageBlobKey = response.split(imgurlmid)[1]
						.split(imgurlend)[0];

				problemSpec.diagram = new Diagram(imageBlobKey, imageURL);

				problemSpec.imageContainer.setSize("100%", "100%");
				Style imageContStyle = problemSpec.imageContainer.getElement()
						.getStyle();
				imageContStyle.setBackgroundImage("url('" + imageURL + "')");
				imageContStyle.setProperty("backgroundSize", "100% 100%");
				removeFromParent();
				problemSpec.leftPanel.add(problemSpec.imageContainer);
				problemSpec.leftPanel.getElement().setScrollTop(
						problemSpec.varIdPanel.getElement().getAbsoluteTop());
			}
		});
	}
}

class ImageContainer extends AbsolutePanel {

	ProblemSpecification problemSpec;
	VariableIdPanel varIdPanel;

	public ImageContainer(final ProblemSpecification problemSpec) {
		super();

		this.problemSpec = problemSpec;
		this.varIdPanel = problemSpec.varIdPanel;

		if (Moderator.isTouch) {
			this.addDomHandler(new TouchEndHandler() {
				@Override
				public void onTouchEnd(TouchEndEvent event) {
					if (!varIdPanel.setInsertImageMode()) {
						Touch touch = event.getTouches().get(0);
						// TODO
						// pointSelection(touch);
					}
				}
			}, TouchEndEvent.getType());
		} else {
			this.addDomHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					if (!varIdPanel.setInsertImageMode()) {
						pointSelection(event.getX(), event.getY());
					}
				}
			}, ClickEvent.getType());
		}
	}

	void pointSelection(int x, int y) {
		VarIDButton aButton = varIdPanel.activeButton;
		if (aButton == null) {
			return;
		}
		add(aButton.imageLabel, x, y);
		String nodeID = aButton.node.getId();
		double ratioX = (double) x / (double) getOffsetWidth();
		double ratioY = (double) y / (double) getOffsetHeight();
		problemSpec.diagram.addMeasure(nodeID, ratioX, ratioY);

	}
}