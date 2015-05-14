package com.tumojava.client.projectfinal.johngralyan;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.tumojava.client.projectfinal.Game;
import com.tumojava.client.ui.InteractivePanel;
import com.tumojava.client.ui.Sprite;

public class InverseTrigGame extends Game {

	int x;
	int y;

	int mouseStartX;
	int mouseStartY;

	boolean isMouseDown;
	boolean isTouching;

	Sprite arrow = new Sprite("images/final_projects/archery45/arrow.png");

	@Override
	protected void start(InteractivePanel panel) {
		arrow.setSizeScaled(10, 10);
		panel.add(arrow);
		arrow.setPositionScaled(12, 69);

		panel.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				if (isMouseDown) {
					moveArrow(event.getX(), event.getY());
				}
			}
		});
		panel.addTouchMoveHandler(new TouchMoveHandler() {
			@Override
			public void onTouchMove(TouchMoveEvent event) {
				Touch touch = event.getTargetTouches().get(0);
				if (isTouching) {
					moveArrow(touch.getClientX(), touch.getClientY());
				}
			}
		});
		panel.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				startMovingArrow(event.getX(), event.getY());
				isMouseDown = true;
			}
		});

		panel.addTouchStartHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				Touch touch = event.getTargetTouches().get(0);
				startMovingArrow(touch.getClientX(), touch.getClientY());
				isTouching = true;
			}
		});
		panel.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				isMouseDown = false;
			}
		});
		panel.addTouchEndHandler(new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				isTouching = false;
			}
		});
	}

	private void startMovingArrow(int mouseStartX, int mouseStartY) {
		this.mouseStartX = mouseStartX;
		this.mouseStartY = mouseStartY;
	}

	private void moveArrow(int mouseX, int mouseY) {
		double magnitudeX = 10 * (mouseStartX - mouseX)
				/ (double) interactiveArea.getOffsetWidth();
		GWT.log("mouseStartX: " + mouseStartX);
		GWT.log("mouseX: " + mouseX);
		GWT.log("interactiveArea.getOffsetWidth(): "
				+ interactiveArea.getOffsetWidth());
		GWT.log("magnitudeX: " + magnitudeX);
		GWT.log(" ");
		arrow.moveScaled(-1*(int) magnitudeX, (int) magnitudeX);

	}

	@Override
	public String getGameTitle() {
		return "Inverse Trig Archery";
	}

	@Override
	public String getDescription() {
		return "Shoot the target.";
	}

	@Override
	public String getEquation() {
		x = (int) (Math.random() * 20 + 5);
		y = (int) (Math.random() * 20 + 5);
		return "";
		// return
		// "<a href=\"http://sciencegadgets.org/#activity=algebrasolve&equation=%3Ce%3E%3Cv%3Ev%5Bv%5D%3Co%3E%E2%89%88%5Bo%5D%3Ct%3E%3Cn%20v%E2%89%88%220%22%20%3E0%5Bn%5D%3Co%3E%C2%B7%5Bo%5D%3Cx%3E%3Cf%3E%3Cn%20v%E2%89%88%229.8%22%20%3E9.8%5Bn%5D%3Cs%3E%3Cn%20v%E2%89%88%221%22%20%3E1%5Bn%5D%3Co%3E-%5Bo%5D%3Cn%20v%E2%89%88%220%22%20%3E0%5Bn%5D%5Bs%5D%5Bf%5D%3Cf%3E%3Cn%20v%E2%89%88%221%22%20%3E1%5Bn%5D%3Cn%20v%E2%89%88%222%22%20%3E2%5Bn%5D%5Bf%5D%5Bx%5D%5Bt%5D%5Be%5D\" style=\"text-decoration: none;\" target=\"_blank\" class=\"\"><div class=\"gwt-HTML\"><link type=\"text/css\" rel=\"stylesheet\" href=\"http://sciencegadgets.org/CSStyles/equation.css\"><div class=\"sg-Equation\"><div class=\"sg-in-equation sg-Variable\">v</div><div class=\"sg-in-equation sg-Operation\">=</div><div class=\"sg-in-equation sg-Term\"><div class=\"sg-in-term sg-Number\">x</div><div class=\"sg-in-term sg-Operation\">·</div><div class=\"sg-in-term sg-Exponential\"><div class=\"sg-in-exponential-base sg-Fraction\"><div class=\"sg-fenced\"><div class=\"sg-in-fraction-numerator sg-Number\">G</div><div class=\"sg-in-fraction-denominator sg-Sum\"><div class=\"sg-in-sum sg-Number\">y</div><div class=\"sg-in-sum sg-Operation\">-</div><div class=\"sg-in-sum sg-Number\">x</div></div></div></div><div class=\"sg-in-exponential-exponent sg-Fraction\"><div class=\"sg-fenced\"><div class=\"sg-in-fraction-numerator sg-Number\">1</div><div class=\"sg-in-fraction-denominator sg-Number\">2</div></div></div></div></div></div></div></a>";
	}

	@Override
	public String getBackgroindImageName() {
		return "images/final_projects/archery45/FinalProjectDiagram.png";
	}

}
