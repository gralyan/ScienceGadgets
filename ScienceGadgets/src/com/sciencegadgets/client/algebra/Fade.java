package com.sciencegadgets.client.algebra;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;

public class Fade extends Animation {

	public boolean isFadeOut;
	public Element element;
	public double opacity;

	/**
	 * Fading transition
	 * 
	 * @param out
	 *            - if true: fade out</br>if false: fade in
	 */
	public Fade(Element element, boolean isFadeOut) {
		this.isFadeOut = isFadeOut;
		this.element = element;
	}

	@Override
	protected void onUpdate(double progress) {
		opacity = isFadeOut ? 1 - progress : progress;
		element.getStyle().setOpacity(opacity);
	}
}