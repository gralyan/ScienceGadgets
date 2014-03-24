package com.sciencegadgets.client.ui;

import java.io.Serializable;
import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class SelectionPanel extends FlowPanel {

	private ArrayList<Cell> cells = new ArrayList<Cell>();
	private Cell selectedCell = null;
	private SelectionHandler selectionHandler;
	private final Label labelAlg = new Label();

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
		labelAlg.setText(title);
		labelAlg.setStylePrimaryName(CSS.ROW_HEADER);
		super.add(labelAlg);
	}
	
	public void addSectionTitle(String title) {
		Label sectionTitle = new Label(title);
		sectionTitle.setStylePrimaryName(CSS.ROW_HEADER);
		add(sectionTitle);
	}

	public ArrayList<Cell> getCells(){
		return cells;
	}
	
	/**
	 * Removes the cell from the panel and cell list if found
	 * @param value of the cell to be removed
	 * @return - true if cell is found
	 */
	public boolean removeCell(String value) {
		for(Cell cell : getCells()) {
			if(value.equals(cell.value)) {
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
		add(labelAlg);
	}

	public void centerText() {
		addStyleName(CSS.TEXT_CENTER);
	}

	public void add(String html) {
		this.add(html, null, null);
	}

	public void add(String html, String value) {
		this.add(html, value, null);
	}
	public void add(String html, String value, Object entity) {
		Cell cell = new Cell(html, value, entity);
		cells .add(cell);
		super.add(cell);
	}
	public void insert(int index, String html, String value, Object entity) {
		Cell cell = new Cell(html, value, entity);
		cells .add(cell);
		super.insert(cell, index+1);
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

	public interface SelectionHandler {
		void onSelect(SelectionPanel.Cell selected);
	}
}
