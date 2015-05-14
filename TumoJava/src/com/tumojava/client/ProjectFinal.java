package com.tumojava.client;

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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class ProjectFinal implements EntryPoint {

	static RootPanel mainPanel = RootPanel.get("mainPanel");
	FlowPanel[] lives = new FlowPanel[3];
	Arrow arrow = new Arrow(lives);
	
	static final LinkedList<HandlerRegistration> touchHandlers = new LinkedList<HandlerRegistration>();

	
	@Override
	public void onModuleLoad() {

		// Add touch panel
		
		TouchPanel touchPanel = new TouchPanel();
		touchPanel.setSize("100%", "100%");
		mainPanel.add(touchPanel);

		// Add arrow
		arrow.addStyleName("arrow");
		mainPanel.add(arrow);
		
		// Add lives
		for(int i=0 ; i<3 ; i++) {
			FlowPanel life = new FlowPanel();
			life.addStyleName("arrow life");
			mainPanel.add(life);
			life.getElement().getStyle().setLeft(3*i-3, Unit.PCT);
			lives[i] = life;
		}
		
		// Add input
		AnswerInputBox input = arrow.getInput();
		input.addStyleName("input");
		input.getElement().getStyle().setLeft(25, Unit.PCT);
		input.getElement().getStyle().setTop(85, Unit.PCT);
		mainPanel.add(input);
		input.getElement().getStyle().setWidth(15, Unit.PCT);
		input.inputBox.getElement().getStyle().setWidth(60, Unit.PCT);
		input.okButton.getElement().getStyle().setWidth(30, Unit.PCT);
		

		int distanceValue = (int) (Math.random() * 200 + 1000);
		double distanceValueShort = distanceValue / 100.0;
		Label distanceLabel = new Label(distanceValueShort + "m");
		distanceLabel.addStyleName("positionInfo");
		distanceLabel.getElement().getStyle().setLeft(60, Unit.PCT);
		distanceLabel.getElement().getStyle().setTop(73, Unit.PCT);
		mainPanel.add(distanceLabel);

		int heightValue = (int) (Math.random() * 200 + 500);
		double heightValueShort = heightValue / 100d;
		Label heightLabel = new Label(heightValueShort + "m");
		heightLabel.addStyleName("positionInfo");
		heightLabel.getElement().getStyle().setLeft(89, Unit.PCT);
		heightLabel.getElement().getStyle().setTop(55, Unit.PCT);
		mainPanel.add(heightLabel);

		// Add pull handlers

		touchHandlers.add(touchPanel.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				event.preventDefault();
				arrow.initializePull(event.getX(), event.getY());
			}
		}));
		touchHandlers.add(touchPanel.addMouseMoveHandler(new MouseMoveHandler() {
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
		touchHandlers.add(touchPanel.addTouchStartHandler(new TouchStartHandler() {
			@Override
			public void onTouchStart(TouchStartEvent event) {
				event.preventDefault();
				Touch touch = event.getTouches().get(0);
				arrow.initializePull(touch.getClientX(), touch.getClientY());
			}
		}));
		touchHandlers.add(touchPanel.addTouchMoveHandler(new TouchMoveHandler() {
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
		touchHandlers.add(touchPanel.addTouchCancelHandler(new TouchCancelHandler() {
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
		Button help = new Button("help", new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
			}
		});
		mainPanel.add(help);
	}

}
