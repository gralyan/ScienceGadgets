package com.sciencegadgets.client.ui;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;

public class FitParentHTML extends HTML {

	public static final int DEFAULT_PERCENT_OF_PARENT = 95;

	/**
	 * Percent of the parent to fill. 100 would match the parent.
	 */
	public int percentOfParent = DEFAULT_PERCENT_OF_PARENT;

	public FitParentHTML() {
		this("", DEFAULT_PERCENT_OF_PARENT);
	}

	public FitParentHTML(String html) {
		this(html, DEFAULT_PERCENT_OF_PARENT);
	}

	public FitParentHTML(int percentOfParent) {
		this("", percentOfParent);
	}

	public FitParentHTML(String html, int percentOfParent) {
		super(html);
		this.percentOfParent = percentOfParent;
		addStyleName(CSS.FIT_PARENT_HTML);
	}

	@Override
	protected void onAttach() {
		super.onAttach();

		resize();
	}

	public void resize() {
		Element htmlElement = this.getElement();

		htmlElement.getStyle().clearFontSize();

		Element parentElement = this.getParent().getElement();

		double widthRatio = (double) parentElement.getOffsetWidth()
				/ (double) htmlElement.getOffsetWidth();
		double heightRatio = (double) parentElement.getOffsetHeight()
				/ (double) htmlElement.getOffsetHeight();

		double smallerRatio = (widthRatio > heightRatio) ? heightRatio
				: widthRatio;

		// *percentOfParent for looser fit, *100 for percent
		double fontPercent = smallerRatio * percentOfParent;

		htmlElement.getStyle().setFontSize((fontPercent), Unit.PCT);
	}
}
