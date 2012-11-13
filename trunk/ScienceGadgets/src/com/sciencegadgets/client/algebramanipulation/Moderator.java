/*   Copyright 2012 John Gralyan
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.sciencegadgets.client.algebramanipulation;

import java.util.LinkedList;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.sciencegadgets.client.algebramanipulation.dropcontrollers.AbstractMathDropController;
import com.sciencegadgets.client.equationbrowser.EquationBrowser;
import com.sciencegadgets.client.equationtree.ChangeNodeMenu;
import com.sciencegadgets.client.equationtree.EquationPanel;
import com.sciencegadgets.client.equationtree.MathMLBindingTree;
import com.sciencegadgets.client.equationtree.RandomSpecification;
import com.sciencegadgets.client.equationtree.SymbolPalette;

public class Moderator implements EntryPoint {

	static EquationPanel eqPanel;
	private static ScrollPanel spTree = new ScrollPanel();

	public static MathMLBindingTree jTree;
	public static LinkedList<AbstractMathDropController> dropControllers;
	static boolean inEditMode = true;
	private Moderator moderator = this;
	private int SGAWidth;
	private int SGAHeight;
	public static SymbolPalette symbolPopup;
	public static RandomSpecification randomSpec;
	public static ChangeNodeMenu changeNodeMenu;
	private Button backToBrowserButton = new Button("Back",
			new BackButtonHandler());
	private static AbsolutePanel scienceGadgetArea = RootPanel
			.get("scienceGadgetArea");
	// private static DropControllAssigner dropAssigner;

	@Override
	public void onModuleLoad() {

		SGAHeight = Window.getClientHeight();
		SGAWidth = Window.getClientWidth();

		scienceGadgetArea.getElement().getStyle()
				.setPosition(Style.Position.RELATIVE);

		scienceGadgetArea.setSize(SGAWidth + "px", SGAHeight + "px");
		Window.scrollTo(scienceGadgetArea.getAbsoluteLeft(),
				scienceGadgetArea.getAbsoluteTop());

		EquationBrowser browserPanel = new EquationBrowser(moderator);
		scienceGadgetArea.add(browserPanel);

	}

	/**
	 * Sends the given equation to the views of it
	 * 
	 * @param mathML
	 */
	public void makeAgebraWorkspace(Element mathML) {

		scienceGadgetArea.clear();

		scienceGadgetArea.add(backToBrowserButton);

		AlgOut algOut = new AlgOut();
		scienceGadgetArea.add(algOut);

		spTree.setSize(SGAWidth + "px", (SGAHeight / 3) + "px");
		scienceGadgetArea.add(spTree);

		scienceGadgetArea.add(new Button("up", new EqSlideHandler(true)));
		scienceGadgetArea.add(new Button("down", new EqSlideHandler(false)));

		if (inEditMode) {
			if (changeNodeMenu == null) {
				changeNodeMenu = new ChangeNodeMenu(scienceGadgetArea);
			}
			scienceGadgetArea.add(changeNodeMenu, 0, SGAHeight * 9 / 10);
		}

		try {
			jTree = new MathMLBindingTree(mathML);
			reload("");

			if (inEditMode) {
				// AlgOutEntry.algOut.add(new HTML("aaaa")/*new
				// ChangeNodeMenu(eqList.selectedWrapper)*/);
			}
		} catch (com.sciencegadgets.client.TopNodesNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Updates the equation in all places when a change is made
	 * 
	 * @param mathML
	 */
	public static void reload(String changeComment) {
		// AlgOutEntry.updateAlgOut(/*jTree.getMathML()*/mathML,
		// jTree.getWrappers(),
		// changeComment);

//		if (eqList != null) {
//			Fade fade = new Fade(eqList.getElement(), true);
//			fade.run(700);
//		}
		if (eqPanel != null)
			spTree.remove(eqPanel);

		eqPanel = new EquationPanel(jTree, inEditMode);
		 spTree.add(eqPanel);

		if (inEditMode) {
			changeNodeMenu.setVisible(false);
		}
		
		// TODO uncomment
		// DropControllAssigner.assign(jTree.getWrappers(), true);
	}

	public static void onEqReady() {
		eqPanel.getElement().getStyle().setOpacity(1);
	}

	class BackButtonHandler implements ClickHandler {

		@Override
		public void onClick(ClickEvent arg0) {
			scienceGadgetArea.clear();
			if (symbolPopup != null && symbolPopup.isShowing()) {
				symbolPopup.hide();
			}
			if (randomSpec != null && randomSpec.isShowing()) {
				randomSpec.hide();
			}
			EquationBrowser browserPanel = new EquationBrowser(moderator);
			scienceGadgetArea.add(browserPanel);
		}

	}

	private class EqSlideHandler implements ClickHandler {
		boolean isUp;

		public EqSlideHandler(boolean isUp) {
			this.isUp = isUp;
		}

		@Override
		public void onClick(ClickEvent event) {
			if (isUp) {
				eqPanel.setFocusUp();
			} else {
				eqPanel.setFocusDown();
			}
		}
	}
}

class Fade extends Animation {

	public boolean isFadeOut;
	public Element element;
	public double opacity;

	/**
	 * Fading transition
	 * 
	 * @param out
	 *            - if true: fade out</br>if false: fade in
	 */
	Fade(Element element, boolean isFadeOut) {
		this.isFadeOut = isFadeOut;
		this.element = element;
	}

	@Override
	protected void onUpdate(double progress) {
		opacity = isFadeOut ? 1 - progress : progress;
		element.getStyle().setOpacity(opacity);
	}
}
