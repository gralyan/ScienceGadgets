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
package com.sciencegadgets.client.algebra;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Panel;
import com.sciencegadgets.client.Moderator;

public class AlgOut extends FlowPanel{

	boolean expanded = false;


	public AlgOut() {
			setStyleName("algOut");
			
			this.sinkEvents(Event.ONCLICK);
			this.addHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
				AlgOutSlide slide = new AlgOutSlide();
				slide.run(300);
				}
			}, ClickEvent.getType());
	}


	public void updateAlgOut(Element change, EquationHTML eqHTML) {
		
		getElement().appendChild(change.cloneNode(true));
		add(eqHTML);
	}
	
	class AlgOutSlide extends Animation{
		private Panel shrinker;
		private Panel grower;
		private int growerHeight;
		private int shrinkerHeight;
		private String growerHeightStyle;
		private String shrinkerHeightStyle;
		
		AlgOutSlide(){
			if(expanded){
				grower = Moderator.eqPanelHolder;
				shrinker = Moderator.upperEqArea;
				Moderator.eqPanelHolder.getElement().getStyle().setDisplay(Display.BLOCK);
			}else{
				grower = Moderator.upperEqArea;
				shrinker = Moderator.eqPanelHolder;
			}
			growerHeight = grower.getOffsetHeight();
			shrinkerHeight = shrinker.getOffsetHeight();
			
			growerHeightStyle = grower.getElement().getStyle().getHeight();
			shrinkerHeightStyle = shrinker.getElement().getStyle().getHeight();
		}
		@Override
		protected void onUpdate(double progress) {
			double diff = ((shrinkerHeight-growerHeight)*progress);
			grower.setHeight((growerHeight+diff)+"px");
			shrinker.setHeight((shrinkerHeight-diff)+"px");
		}
		@Override
		protected void onComplete() {
			super.onComplete();
			grower.setHeight(shrinkerHeightStyle);
			shrinker.setHeight(growerHeightStyle);
			if(expanded){
				expanded = false;
			}else{
				Moderator.eqPanelHolder.getElement().getStyle().setDisplay(Display.NONE);
				expanded = true;
			}
		}
		
		
	}

}
