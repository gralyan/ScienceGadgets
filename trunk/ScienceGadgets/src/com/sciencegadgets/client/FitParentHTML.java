package com.sciencegadgets.client;

import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
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
		getElement().getStyle().setDisplay(Display.INLINE_BLOCK);
	}

	@Override
	protected void onAttach() {
		super.onAttach();

		Element htmlElement = this.getElement();

		if ("".equals(htmlElement.getStyle().getFontSize())) {

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
}
