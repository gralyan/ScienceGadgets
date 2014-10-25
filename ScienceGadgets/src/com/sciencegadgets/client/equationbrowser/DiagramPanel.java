package com.sciencegadgets.client.equationbrowser;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Label;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.entities.Problem;
import com.sciencegadgets.client.ui.CSS;
import com.sciencegadgets.client.ui.Resizable;
import com.sciencegadgets.shared.Diagram;
import com.sciencegadgets.shared.Measure;

public class DiagramPanel extends AbsolutePanel implements Resizable {
		
		HashMap<Measure, Label> measures = new HashMap<Measure, Label>();

		DiagramPanel() {
			setSize("100%", "100%");
			getElement().getStyle().setProperty("backgroundSize", "100% 100%");
			

		}
		
		public void loadDiagram(Diagram diagram, Problem problem, HashMap<String, String> randomMap) {
			clear();
			measures.clear();
			
			getElement().getStyle().setBackgroundImage("url('" + diagram.getImageURL() + "')");

			for (Measure m : diagram.getMeasurements()) {
				Label measureLabel = new Label(randomMap.get(m.getNodeID()));
				measureLabel.addStyleName(CSS.DIAGRAM_MEASURE);
				measures.put(m, measureLabel);
			}

			resize();
		}

		@Override
		public void resize() {
			for (Entry<Measure, Label> entry : this.measures.entrySet()) {
				Measure m = entry.getKey();
				int x = (int) (m.getRatioX() * getOffsetWidth());
				int y = (int) (m.getRatioY() * getOffsetHeight());
				add(entry.getValue(), x, y);
			}
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
	}
