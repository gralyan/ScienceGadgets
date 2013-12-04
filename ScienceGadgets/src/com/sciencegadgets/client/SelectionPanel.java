package com.sciencegadgets.client;

import java.util.ArrayList;

import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;

public class SelectionPanel extends FlowPanel {

	ArrayList<Cell> cells = new ArrayList<Cell>();
	private Cell selectedCell = null;
	private SelectionHandler selectionHandler;
	private final Label labelAlg = new Label();

	public SelectionPanel(String title) {
		super();
		setStylePrimaryName("selectionPanel");

		labelAlg.setText(title);
		labelAlg.setStylePrimaryName("rowHeader");
		add(labelAlg);
	}

	public SelectionPanel(String title, SelectionHandler selectionHandler) {
		this(title);
		addSelectionHandler(selectionHandler);
	}

	@Override
	public void clear() {
		super.clear();
		add(labelAlg);
	}

	public void centerText() {
		addStyleName("textCenter");
	}

	public void add(String html) {
		this.add(html, null);
	}

	public void add(String html, String value) {
		super.add(new Cell(html, value));
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
		public String value;
		public String html;

		Cell(String html, final String value) {
			super(html);
			this.html = html;
			this.value = value;
			this.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					if (selectedCell != null) {
						selectedCell.removeStyleName("selectedCell");
					}

					selectedCell = (Cell) event.getSource();
					selectedCell.addStyleName("selectedCell");
					if (selectionHandler != null) {
						selectionHandler.onSelect(selectedCell);
					}
				}
			});
		}

		public String getValue() {
			return value;
		}
	}

	public void addSelectionHandler(SelectionHandler handler) {
		selectionHandler = handler;
	}

	public interface SelectionHandler {
		void onSelect(SelectionPanel.Cell selected);
	}
}
