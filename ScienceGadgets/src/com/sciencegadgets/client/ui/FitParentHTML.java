package com.sciencegadgets.client.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Moderator;

public class FitParentHTML extends HTML implements Resizable {

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
	protected void onLoad() {
		super.onLoad();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				resize();
			}
		});
		Moderator.resizables.add(this);
	}
	
	@Override
	protected void onDetach() {
		super.onDetach();
		Moderator.resizables.remove(this);
	}

	@Override
	public void resize() {
		Element htmlElement = this.getElement();

		htmlElement.getStyle().clearFontSize();
		
		if(this.getParent() == null) {
		}

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
