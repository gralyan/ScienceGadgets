package com.tumojava.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.FlowPanel;

public class Arrow extends FlowPanel {

	// Arrow position constraints in %
	private int leftStart = 12;
	private int topStart = 69;
	private int leftMaxPull = -4;
	private int topMaxPull = 6;

	// Used as the max pull size
	double arrowHeightPixels;

	Style arrowStyle = getElement().getStyle();

	// Should only be used when isPulling is true
	int clickedX;
	int clickedY;

	boolean isPulling = false;
	private ShootAnimation shootAnimation = new ShootAnimation();
	private double pullRatio;
	private double leftPulled;
	private double topPulled;
	
	FlowPanel[] lives;
	public int shotsFired = 0;
	
	private final AnswerInputBox input = new AnswerInputBox() {
		@Override
		void onInput() {
		}
	};

	public Arrow(FlowPanel[] lives) {
		this.lives = lives;
		reload();
	}

	void initializePull(int x, int y) {
		reload();
		isPulling = true;
		clickedX = x;
		clickedY = y;

		// Check each time pull initiated for current size
		arrowHeightPixels = getOffsetHeight();
	}

	void updatePull(int x, int y) {
		if (!isPulling) {
			return;
		}

		int pullX = clickedX - x;
		int pullY = clickedY - y;
		int pull = pullX > pullY ? pullX : pullY;

		pullRatio = pull / arrowHeightPixels;

		leftPulled = leftStart + pullRatio * leftMaxPull;
		topPulled = topStart + pullRatio * topMaxPull;
		arrowStyle.setLeft(leftPulled, Unit.PCT);
		arrowStyle.setTop(topPulled, Unit.PCT);
		
		input.inputBox.setText(pullRatio+"");
	}

	void fire(int x, int y) {
		updatePull(x, y);
		isPulling = false;

		shootAnimation.run(1000);
	}

	void reload() {
		shootAnimation.cancel();
		
		arrowStyle.setLeft(leftStart, Unit.PCT);
		arrowStyle.setTop(topStart, Unit.PCT);
		arrowStyle.setProperty("transform", "rotate(0deg)");
		isPulling = false;
		
	}
	
	AnswerInputBox getInput() {
		return input;
	}

	class ShootAnimation extends Animation {

		private double velocityX;
		private double velocityY0;
		private double acceleration = 100;
		private double time = 0;
		double y = topPulled;
		double x = leftPulled;

		@Override
		protected void onStart() {
			super.onStart();
			
			if(pullRatio > 1.75 && pullRatio < 1.97){
				pullRatio = 1.75;
			}
			
			pullRatio*=.5;
			velocityX = pullRatio * (100 - leftPulled);
			velocityY0 = -1.5 * pullRatio * topStart;
		}

		@Override
		protected void onUpdate(double progress) {

			time = progress * pullRatio*2;
			
			y = topPulled + 
					time * velocityY0 + 
					0.5 * acceleration* time * time;
			x = leftPulled + velocityX * time;

			arrowStyle.setLeft(x, Unit.PCT);
			arrowStyle.setTop(y, Unit.PCT);
			arrowStyle.setProperty("transform", "rotate(" + progress * 90
					+ "deg)");

		}

		@Override
		protected void onComplete() {
			super.onComplete();
			reload();

			input.inputBox.setText("");
			lives[2-shotsFired].removeFromParent();
			if (shotsFired++ == 2) {
				ProjectFinal.addHelpButton();
			}
		}

	}
	


}
