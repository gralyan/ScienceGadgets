package com.sciencegadgets.client.examples.archery;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Window;
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

	private final VelocityInputBox input;
	private final double targetVelocity;
	private final double targetPullRatioInput = 1.68;
	private final double targetPullRatioErrorInput = 0.04;
	private final double targetPullRatioSwipe = 1.86;
	private final double targetPullRatioErrorSwipe = 0.11;

	public Arrow(FlowPanel[] lives, double dx, double dy) {
		this.lives = lives;

		targetVelocity = Math.sqrt((-9.8 * dx * dx) / (dy - dx));

		GWT.log(targetVelocity + "");
		input = new VelocityInputBox("v<sub>0</sub>", "<sup>m</sup>&frasl;<sub>s</sub>") {
			@Override
			void onInput(String inputString) {
				try {
					GWT.log(targetVelocity + "");
					double inputDouble = Double.parseDouble(inputString);
					double inputError = inputDouble / targetVelocity;
					pullRatio = (inputDouble / targetVelocity)
							* targetPullRatioInput;
					shootAnimation.fire(true);
				} catch (NumberFormatException | NullPointerException e) {
					GWT.log("Non-number entered");
				}
			}
		};

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

		input.inputBox.setText(((int) (pullRatio * 100)) / 100.0 + "");
	}

	void fire(int x, int y) {
		updatePull(x, y);
		fire();
	}
	void fire() {
		isPulling = false;
		shootAnimation.fire(false);
	}

	void reload() {
		shootAnimation.cancel();

		arrowStyle.setLeft(leftStart, Unit.PCT);
		arrowStyle.setTop(topStart, Unit.PCT);
		arrowStyle.setProperty("transform", "rotate(0deg)");
		isPulling = false;

		input.setFocus(true);
	}

	VelocityInputBox getInput() {
		return input;
	}

	class ShootAnimation extends Animation {

		private double velocityX;
		private double velocityY0;
		private double acceleration = 100;
		private double time = 0;
		double y = topPulled;
		double x = leftPulled;
		double rotation;
		private boolean isInput = false;
		private boolean hit = false;

		void fire(boolean isInput) {
			this.isInput = isInput;
			run((int) (pullRatio * 500)+200);
		}

		@Override
		protected void onStart() {
			super.onStart();
			
//			GWT.log("p "+pullRatio);

			double trajectoryErrorInput = Math.abs(pullRatio - targetPullRatioInput);
			double trajectoryErrorSwipe = Math.abs(pullRatio - targetPullRatioSwipe);
			boolean hitTrajectoryInput =  trajectoryErrorInput < targetPullRatioErrorInput;
			boolean hitTrajectorySwipe =  trajectoryErrorSwipe < targetPullRatioErrorSwipe;
			hit = isInput && hitTrajectoryInput;

			if (isInput) {
				topPulled = topStart;
				leftPulled = leftStart;
			} else if (hitTrajectorySwipe) { // Always miss when touching
					pullRatio = targetPullRatioSwipe - targetPullRatioErrorSwipe;
			}

			pullRatio *= .5;
			velocityX = pullRatio * (100 - leftPulled);
			velocityY0 = -1.5 * pullRatio * topStart;
		}

		@Override
		protected void onUpdate(double progress) {
			

			time = progress * pullRatio * 2;

			y = topPulled + time * velocityY0 + 0.5 * acceleration * time
					* time;
			x = leftPulled + velocityX * time;
			rotation = progress * 90;

			 if(hit && x > 83) {
				 x = 88;
				 moveArrow();
				 cancel();
			 }
			 
			 moveArrow();
		}
		
		private void moveArrow() {
			arrowStyle.setLeft(x, Unit.PCT);
			arrowStyle.setTop(y, Unit.PCT);
			arrowStyle.setProperty("transform", "rotate(" + rotation
					+ "deg)");
		}

		@Override
		protected void onComplete() {
			input.inputBox.setText("");
			
			int toRemove = 2 - shotsFired;
			if(toRemove >= 0) {
				lives[toRemove].removeFromParent();
			}
			 if (shotsFired++ == 2) {
				 ArcheryGame.addHelpButton();
			 }

			 if(shotsFired < 4) {
				 reload();
			 }
		}

//		@Override
//		protected void onCancel() {
//			// Don't run super
//		}
	}

}
