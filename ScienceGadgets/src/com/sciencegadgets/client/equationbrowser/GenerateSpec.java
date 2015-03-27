package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.kiouri.sliderbar.client.event.BarValueChangedEvent;
import com.kiouri.sliderbar.client.event.BarValueChangedHandler;
import com.sciencegadgets.client.JSNICalls;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.sliders.SlideBarIncremental;
import com.sciencegadgets.shared.TypeSGET;

public class GenerateSpec extends Composite {

	private static GenerateSpecUiBinder uiBinder = GWT
			.create(GenerateSpecUiBinder.class);

	interface GenerateSpecUiBinder extends UiBinder<Widget, GenerateSpec> {
	}

	@UiField
	FlowPanel generateButtonContainer;
	@UiField
	FlowPanel slideContainerAdd;
	@UiField
	FlowPanel slideContainerMult;
	@UiField
	FlowPanel slideContainerFrac;
	@UiField
	FlowPanel slideContainerExp;

	@UiField
	Label subjectAdd;
	@UiField
	Label subjectMult;
	@UiField
	Label subjectFrac;
	@UiField
	Label subjectExp;
	
	private Button generateButton;
	SlideBarDifficulty slideAdd;
	SlideBarDifficulty slideMult;
	SlideBarDifficulty slideFrac;
	SlideBarDifficulty slideExp;
	SlideBarDifficulty[] allSliders = new SlideBarDifficulty[4];

	enum Difficulty {
		NONE, EASY, MEDIUM, HARD;
	}

	public GenerateSpec() {
		initWidget(uiBinder.createAndBindUi(this));

		slideAdd = new SlideBarDifficulty(subjectAdd);
		slideMult = new SlideBarDifficulty(subjectMult);
		slideFrac = new SlideBarDifficulty(subjectFrac);
		slideExp = new SlideBarDifficulty(subjectExp);
		allSliders[0] = slideAdd;
		allSliders[1] = slideMult;
		allSliders[2] = slideFrac;
		allSliders[3] = slideExp;
		slideContainerAdd.add(slideAdd);
		slideContainerMult.add(slideMult);
		slideContainerFrac.add(slideFrac);
		slideContainerExp.add(slideExp);

		generateButton = new Button("Generate",
				new GenerateEquationHandler(this));
		generateButton.getElement().setId(CSS.GENERATE_BUTTON);
		generateButtonContainer.add(generateButton);
		generateButton.setEnabled(false);
	}
	
	class SlideBarDifficulty extends SlideBarIncremental {
		Label subject;
		protected Difficulty difficulty = Difficulty.NONE;
		SlideBarDifficulty(final Label subject) {
			super(3, "100%");
			this.subject = subject;
			
			addBarValueChangedHandler(new BarValueChangedHandler() {
				@Override
				public void onBarValueChanged(BarValueChangedEvent event) {
					String backColor = "white";
					String foregroundColor = "black";
					difficulty = getDifficulty(event.getValue());
					switch (difficulty) {
					case NONE:
						break;
					case EASY:
						backColor = "lightGray";
						break;
					case MEDIUM:
						backColor = "gray";
						break;
					case HARD:
						backColor = "black";
						foregroundColor = "white";
						break;
					default:
						break;
					}
					subject.getElement().getStyle().setBackgroundColor(backColor);
					subject.getElement().getStyle().setColor(foregroundColor);
					
					boolean hasDifficulty = false;
					for(SlideBarDifficulty slide : allSliders) {
						if(!slide.difficulty.equals(Difficulty.NONE)) {
							hasDifficulty=true;
							break;
						}
					}
					generateButton.setEnabled(hasDifficulty);
				}
			});
		}

		public Difficulty getDifficulty() {
			return getDifficulty(getValue());
		}
		public Difficulty getDifficulty(int value) {
			
			switch (value) {
			case 0:
				return Difficulty.NONE;
			case 1:
				return Difficulty.EASY;
			case 2:
				return Difficulty.MEDIUM;
			case 3:
				return Difficulty.HARD;
			default:
				return Difficulty.NONE;
			}
		}
	}

}
