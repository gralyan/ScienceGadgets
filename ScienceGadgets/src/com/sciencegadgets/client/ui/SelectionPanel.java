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

import java.util.ArrayList;

import org.apache.commons.logging.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

public class SelectionPanel extends FlowPanel {

	private ArrayList<Cell> cells = new ArrayList<Cell>();
	private Cell selectedCell = null;
	private SelectionHandler selectionHandler;
	private final Label titleLabel = new Label();

	public SelectionPanel() {
		super();
		setStylePrimaryName(CSS.SELECTION_PANEL);
	}

	public SelectionPanel(String title) {
		this();
		setTitle(title);
	}

	public SelectionPanel(String title, SelectionHandler selectionHandler) {
		this(title);
		addSelectionHandler(selectionHandler);
	}

	public void setTitle(String title) {
		titleLabel.setText(title);
		titleLabel.setStylePrimaryName(CSS.ROW_HEADER);
		super.add(titleLabel);
	}

	public void addSectionTitle(String title) {
		Label sectionTitle = new Label(title);
		sectionTitle.setStylePrimaryName(CSS.ROW_SUB_HEADER);
		add(sectionTitle);
	}

	public ArrayList<Cell> getCells() {
		return cells;
	}

	/**
	 * Removes the cell from the panel and cell list if found
	 * 
	 * @param value
	 *            of the cell to be removed
	 * @return - true if cell is found
	 */
	public boolean removeCell(String value) {
		for (Cell cell : getCells()) {
			if (value.equals(cell.value)) {
				cells.remove(cell);
				cell.removeFromParent();
				return true;
			}
		}
		return false;
	}

	@Override
	public void clear() {
		super.clear();
		cells.clear();
		add(titleLabel);
	}

	public void centerText() {
		addStyleName(CSS.TEXT_CENTER);
	}

	public Cell add(String html) {
		return this.add(html, null, null);
	}

	public Cell add(String html, String value) {
		return this.add(html, value, null);
	}

	public Cell add(String html, String value, Object entity) {
		Cell cell = new Cell(html, value, entity);
		cells.add(cell);
		super.add(cell);
		return cell;
	}

	public Cell insert(int index, String html, String value, Object entity) {
		Cell cell = new Cell(html, value, entity);
		cells.add(cell);
		super.insert(cell, index + 1);
		return cell;
	}
	
	public void advanceCell(Cell toAdvance) {
		toAdvance.addStyleName(CSS.HIGHLIGHTED_CELL);
		if (cells.contains(toAdvance)) {
			cells.remove(toAdvance);
			cells.add(0, toAdvance);
		}
	}

	public String getSelectedText() {
		if (selectedCell != null) {
			return selectedCell.getText();
		} else {
			return null;
		}
	}

	public Element getSelectedElement() {
		if (selectedCell != null) {
			return selectedCell.getElement().getFirstChildElement();
		} else {
			return null;
		}
	}

	public String getSelectedValue() {
		if (selectedCell != null) {
			return selectedCell.getValue();
		} else {
			return null;
		}
	}

	public Cell getSelection() {
		return selectedCell;
	}

	public void clearSelection() {
		if (selectedCell != null) {
			selectedCell.removeStyleName(CSS.SELECTED_CELL);
			selectedCell = null;
		}
	}

	@Override
	protected void onDetach() {
		clearSelection();
		super.onDetach();
	}

	public void search(String searchQ) {
		if (searchQ != null) {
			searchQ = searchQ.toLowerCase();
		}

		super.clear();
		add(titleLabel);
		

		if (searchQ == null || "".equals(searchQ)) {
			for (Cell c : cells) {
				super.add(c);
			}
		} else {
			for (Cell c : cells) {
				if (c.getHTML().toLowerCase().contains(searchQ)) {
					super.add(c);
				}
			}
		}
	}

	public SearchBox makeSearchBox() {
		return new SearchBox();
	}

	public class Cell extends HTML {
		private String value;
		private Object entity;

		Cell(String html, final String value, Object entity) {
			super(html);
			this.value = value;
			this.entity = entity;

			addStyleName(CSS.SELECTION_PANEL_CELL);

			this.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					select((Cell) event.getSource());
				}
			});
		}

		private void select(Cell selection) {
			advanceCell(this);
			
			if (selectedCell != null) {
				selectedCell.removeStyleName(CSS.SELECTED_CELL);
			}

			selectedCell = selection;
			selectedCell.addStyleName(CSS.SELECTED_CELL);
			if (selectionHandler != null) {
				selectionHandler.onSelect(selectedCell);
			}
		}

		public String getValue() {
			return value;
		}

		public Object getEntity() {
			return entity;
		}
	}

	public void addSelectionHandler(SelectionHandler handler) {
		selectionHandler = handler;
	}

	public boolean hasSelectionHandler() {
		return selectionHandler != null;
	}

	public interface SelectionHandler {
		void onSelect(SelectionPanel.Cell selected);
	}

	public class SearchBox extends TextBox {
		private static final String SEARCH_DEFAULT_TEXT = "Search";
		private String previousSearch = "";
		SelectionPanel browser = null;

		SearchBox() {

			setText(SEARCH_DEFAULT_TEXT);
			getElement().getStyle().setColor("gray");

			addFocusHandler(new FocusHandler() {
				@Override
				public void onFocus(FocusEvent event) {
					if (SEARCH_DEFAULT_TEXT.equals(getText())) {
						setText("");
						getElement().getStyle().setColor("black");
					}
				}
			});

			addKeyUpHandler(new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					String searchQ = getText();
					search(searchQ);
				}
			});

			addValueChangeHandler(new ValueChangeHandler<String>() {
				@Override
				public void onValueChange(ValueChangeEvent<String> event) {
					String searchQ = event.getValue();
					search(searchQ);
				}
			});

		}

		public void search(String searchQ) {
			if (previousSearch.equals(searchQ)) {
				return;
			}
			previousSearch = searchQ;
			if (browser != null) {
				browser.clearSelection();
			}
			if (!SEARCH_DEFAULT_TEXT.equals(searchQ)) {
				SelectionPanel.this.search(searchQ);
			}

		}

		@Override
		protected void onDetach() {
			setText(SEARCH_DEFAULT_TEXT);
			getElement().getStyle().setColor("gray");
			super.onDetach();
		}

		public void setBrowser(SelectionPanel browser) {
			this.browser = browser;
		}
	}
}
