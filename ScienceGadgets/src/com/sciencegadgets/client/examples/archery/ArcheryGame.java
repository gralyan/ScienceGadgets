package com.sciencegadgets.client.examples.archery;

import java.util.LinkedList;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Touch;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.event.dom.client.TouchCancelEvent;
import com.google.gwt.event.dom.client.TouchCancelHandler;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchEndHandler;
import com.google.gwt.event.dom.client.TouchMoveEvent;
import com.google.gwt.event.dom.client.TouchMoveHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class ArcheryGame implements EntryPoint {

	static final RootPanel mainPanel = RootPanel.get("mainPanel");
	FlowPanel[] lives = new FlowPanel[3];
	Arrow arrow;
	static private Label distanceLabel;
	static private Label heightLabel;
	static private Label angleLabel;
	static private VelocityInputBox input;
	static Image hint = new Image();
	static final TouchPanel touchPanel = new TouchPanel();

	static final LinkedList<HandlerRegistration> touchHandlers = new LinkedList<HandlerRegistration>();

	@Override
	public void onModuleLoad() {

		Image pom = new Image("pom.svg");
		pom.addStyleName("pom");
		mainPanel.add(pom);

		// Add touch panel
		touchPanel.setSize("100%", "100%");
		mainPanel.add(touchPanel);

		int distanceValueCenti = (int) (Math.random() * 2000 + 1000);
		double distanceValue = distanceValueCenti / 100.0;
		distanceLabel = new Label(distanceValue + "m");
		distanceLabel.addStyleName("positionInfo");
		distanceLabel.getElement().getStyle().setLeft(80, Unit.PCT);
		distanceLabel.getElement().getStyle().setTop(73, Unit.PCT);
		
		// Height is always 20% to 60% of Distance, believable for drawing
		int heightValueCenti = (int) (distanceValue
				* (Math.random() * 0.40 + 0.20) * 100);
		double heightValue = heightValueCenti / 100.0;
		heightLabel = new Label(heightValue + "m");
		heightLabel.addStyleName("positionInfo");
		heightLabel.getElement().getStyle().setLeft(89, Unit.PCT);
		heightLabel.getElement().getStyle().setTop(55, Unit.PCT);
		
		int angleValue = (int)(Math.random()*20+30);
		angleLabel = new Label(angleValue+"\u00B0");
		angleLabel.addStyleName("positionInfo");
		angleLabel.getElement().getStyle().setLeft(23, Unit.PCT);
		angleLabel.getElement().getStyle().setTop(70, Unit.PCT);
		
		// Add arrow
		arrow = new Arrow(lives, distanceValue, heightValue);
		arrow.addStyleName("arrow");
		mainPanel.add(arrow);
		
		// Add input
		input = arrow.getInput();
		input.addStyleName("input");

		// Add lives
		for (int i = 0; i < 3; i++) {
			FlowPanel life = new FlowPanel();
			life.addStyleName("arrow life");
			mainPanel.add(life);
			life.getElement().getStyle().setLeft(3 * i - 3, Unit.PCT);
			lives[i] = life;
		}

		// Add pull handlers

		touchHandlers.add(touchPanel
				.addMouseDownHandler(new MouseDownHandler() {
					@Override
					public void onMouseDown(MouseDownEvent event) {
						event.preventDefault();
						arrow.initializePull(event.getX(), event.getY());
					}
				}));
		touchHandlers.add(touchPanel
				.addMouseMoveHandler(new MouseMoveHandler() {
					@Override
					public void onMouseMove(MouseMoveEvent event) {
						arrow.updatePull(event.getX(), event.getY());
					}
				}));
		touchHandlers.add(touchPanel.addMouseUpHandler(new MouseUpHandler() {
			@Override
			public void onMouseUp(MouseUpEvent event) {
				arrow.fire(event.getX(), event.getY());
			}
		}));
		touchHandlers.add(touchPanel.addMouseOutHandler(new MouseOutHandler() {
			@Override
			public void onMouseOut(MouseOutEvent event) {
				arrow.reload();
			}
		}));
		touchHandlers.add(touchPanel
				.addTouchStartHandler(new TouchStartHandler() {
					@Override
					public void onTouchStart(TouchStartEvent event) {
						event.preventDefault();
						Touch touch = event.getTouches().get(0);
						arrow.initializePull(touch.getClientX(),
								touch.getClientY());
					}
				}));
		touchHandlers.add(touchPanel
				.addTouchMoveHandler(new TouchMoveHandler() {
					@Override
					public void onTouchMove(TouchMoveEvent event) {
						event.preventDefault();
						Touch touch = event.getTouches().get(0);
						arrow.updatePull(touch.getClientX(), touch.getClientY());
					}
				}));
		touchHandlers.add(touchPanel.addTouchEndHandler(new TouchEndHandler() {
			@Override
			public void onTouchEnd(TouchEndEvent event) {
				Touch touch = event.getTouches().get(0);
				arrow.fire(touch.getClientX(), touch.getClientY());
			}

		}));
		touchHandlers.add(touchPanel
				.addTouchCancelHandler(new TouchCancelHandler() {
					@Override
					public void onTouchCancel(TouchCancelEvent event) {
						arrow.reload();
					}
				}));
	}

	static void addHelpButton() {
		for(HandlerRegistration h : touchHandlers) {
			h.removeHandler();
		}
		touchPanel.removeFromParent();
		
		final Image lastArrowAlert = new Image("last_arrow_alert.svg");
		mainPanel.add(lastArrowAlert);
		lastArrowAlert.addStyleName("lastArrowAlert");
		
		final Button help = new Button("Help!");
		help.addStyleName("helpButton helpButtonArea");
		
		help.addClickHandler(new ClickHandler() {
			int state = 0;
			@Override
			public void onClick(ClickEvent event) {
				switch (state) {
				case 0:
					help.removeStyleName("helpButtonArea");
					help.addStyleName("nextButtonArea");
					help.setText("Next");
					lastArrowAlert.removeFromParent();
					hint.addStyleName("hint hintArea");
					mainPanel.add(hint);
					hint.setUrl("hint-v0.svg");
					break;
				case 1:
					hint.setUrl("hint-x.svg");
					break;
				case 2:
					hint.setUrl("hint-y.svg");
					break;
				case 3:
					hint.removeFromParent();
					HTML iframe = new HTML("<iframe name=\"InteractiveEquation\" src=\"http://localhost:8888/ScienceGadgets.html#activity=interactiveequation&themecolor=A0C4FF&system=%3Ce%3E%3Cv%20%20u%E2%89%88%22Velocity%5E1%22%3Evox%5Bv%5D%3Co%3E%E2%89%88%5Bo%5D%3Ct%3E%3Cv%20%20u%E2%89%88%22Velocity%5E1%22%3Evo%5Bv%5D%3Co%3E%C2%B7%5Bo%5D%3Cr%20%20f%E2%89%88%22cos%22%3E%3Cv%20%20u%E2%89%88%22Angle%5E1%22%3E%CE%B8%5Bv%5D%5Br%5D%5Bt%5D%5Be%5Dsoe%3Ce%3E%3Cv%20%20u%E2%89%88%22Velocity%5E1%22%3Evoy%5Bv%5D%3Co%3E%E2%89%88%5Bo%5D%3Ct%3E%3Cv%20%20u%E2%89%88%22Velocity%5E1%22%3Evo%5Bv%5D%3Co%3E%C2%B7%5Bo%5D%3Cr%20%20f%E2%89%88%22sin%22%3E%3Cv%20%20u%E2%89%88%22Angle%5E1%22%3E%CE%B8%5Bv%5D%5Br%5D%5Bt%5D%5Be%5Dsoe%3Ce%3E%3Cv%20%20u%E2%89%88%22Length%5E1%22%3E%CE%94x%5Bv%5D%3Co%3E%E2%89%88%5Bo%5D%3Ct%3E%3Cv%20%20u%E2%89%88%22Velocity%5E1%22%3Evox%5Bv%5D%3Co%3E%C2%B7%5Bo%5D%3Cv%20%20u%E2%89%88%22Time%5E1%22%3Et%5Bv%5D%5Bt%5D%5Be%5D&equation=%3Ce%3E%3Cv%20%20u%E2%89%88%22Length%5E1%22%3E%CE%94y%5Bv%5D%3Co%3E%E2%89%88%5Bo%5D%3Cs%3E%3Ct%3E%3Cv%20%20u%E2%89%88%22Velocity%5E1%22%3Evoy%5Bv%5D%3Co%3E%C2%B7%5Bo%5D%3Cv%20%20u%E2%89%88%22Time%5E1%22%3Et%5Bv%5D%5Bt%5D%3Co%3E%E2%9E%95%5Bo%5D%3Ct%3E%3Cf%3E%3Cn%20%20v%E2%89%88%221%22%3E1%5Bn%5D%3Cn%20%20v%E2%89%88%222%22%3E2%5Bn%5D%5Bf%5D%3Co%3E%C2%B7%5Bo%5D%3Cn%20%20v%E2%89%88%229.80665%22%20u%E2%89%88%22Length_m%5E1*Time_s%5E-2%22%3Egn%5Bn%5D%3Co%3E%C2%B7%5Bo%5D%3Cx%3E%3Cv%20%20u%E2%89%88%22Time%5E1%22%3Et%5Bv%5D%3Cn%20%20v%E2%89%88%222%22%3E2%5Bn%5D%5Bx%5D%5Bt%5D%5Bs%5D%5Be%5D\" style=\"width: 100%; height: 100%;\"></iframe>");
					iframe.addStyleName("hintArea");
					mainPanel.add(iframe);

					help.removeFromParent();
//					help.setHTML("Solve for <b>v<sub>0</sub></b>");
//					help.getElement().getStyle().setBackgroundColor("transparent");
//					help.getElement().getStyle().setProperty("boxShadow", "none");
//					HTML solveHTML = new HTML("Solve for <b>v<sub>0</sub></b>");
//					solveHTML.addStyleName("helpButtonArea");
//					mainPanel.add(solveHTML);
					break;
				default:
					break;
				}
				state++;
			}
		});
		
		FlowPanel grid = new FlowPanel();
		grid.addStyleName("grid");
		mainPanel.add(help);
		mainPanel.add(grid);
		mainPanel.add(angleLabel);
		mainPanel.add(distanceLabel);
		mainPanel.add(heightLabel);
		mainPanel.add(input);

	}
}
//HTML link = new HTML("<a target=\"_blank\" href=\"http://sciencegadgets.org/#activity=algebrasolve&equation=%3Ce%3E%3Cv%3E%CE%94y%5Bv%5D%3Co%3E%E2%89%88%5Bo%5D%3Cs%3E%3Cv%3E%CE%94x%5Bv%5D%3Co%3E%E2%9E%95%5Bo%5D%3Ct%3E%3Cv%3Ea%5Bv%5D%3Co%3E%C2%B7%5Bo%5D%3Cf%3E%3Cx%3E%3Cv%3E%CE%94x%5Bv%5D%3Cn%20%20v%E2%89%88%222%22%3E2%5Bn%5D%5Bx%5D%3Cx%3E%3Cv%3Ev%5Bv%5D%3Cn%20%20v%E2%89%88%222%22%3E2%5Bn%5D%5Bx%5D%5Bf%5D%5Bt%5D%5Bs%5D%5Be%5D\" style=\"text-decoration: none;\"><div class=\"gwt-HTML\"><link type=\"text/css\" rel=\"stylesheet\" href=\"http://sciencegadgets.org/CSStyles/equation.css\"><div class=\"sg-Equation\"><div class=\"sg-in-equation sg-Variable\">Δy<div class=\"sg-subscript\"></div></div><div class=\"sg-in-equation sg-Operation\">=</div><div class=\"sg-in-equation sg-Sum\"><div class=\"sg-in-sum sg-Variable\">Δx<div class=\"sg-subscript\"></div></div><div class=\"sg-in-sum sg-Operation\">+</div><div class=\"sg-in-sum sg-Term\"><div class=\"sg-in-term sg-Variable\">a</div><div class=\"sg-in-term sg-Operation\">&middot;</div><div class=\"sg-in-term sg-Fraction\"><div class=\"sg-in-fraction-numerator sg-Exponential\"><div class=\"sg-in-exponential-base sg-Variable\">Δx<div class=\"sg-subscript\"></div></div><div class=\"sg-in-exponential-exponent sg-Number\">2</div></div><div class=\"sg-in-fraction-denominator sg-Exponential\"><div class=\"sg-in-exponential-base sg-Variable\">v</div><div class=\"sg-in-exponential-exponent sg-Number\">2</div></div></div></div></div></div></div></a>");

