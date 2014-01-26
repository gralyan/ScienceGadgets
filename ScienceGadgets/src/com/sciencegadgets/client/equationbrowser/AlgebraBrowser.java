package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Equation;

public class AlgebraBrowser extends SelectionPanel {

	public AlgebraBrowser(final EquationBrowser equationBrowser) {
		super("Algebra Practice", new SelectionHandler() {

			@Override
			public void onSelect(Cell selected) {
				String mathmlStr = selected.getValue();
				if (mathmlStr != null) {
					// Element mathml = (Element)
					// XMLParser.parse(mathmlStr).getDocumentElement();
					Element mathml = (Element) (new HTML(mathmlStr)
							.getElement().getFirstChildElement());
					Moderator.makeAlgebra(mathml, equationBrowser.inEditMode);
				}
			}
		});
		getElement().setId("algBrowser");

		DataModerator.database
				.getAlgebraEquations(new AsyncCallback<Equation[]>() {
					public void onFailure(Throwable caught) {
						Window.alert("Can't find algebra equations :(");
					}

					public void onSuccess(Equation[] eqList) {
						int size = eqList.length;

						if (size == 0) {
							// Display default a=a if no other equations
							add("<div>a=a<div>",
									"<sgmt:equation><sgmt:var>a</sgmt:var><sgmt:op>=</sgmt:op><sgmt:var>a</sgmt:var></sgmt:equation>");
						} else {
							for (int i = 0; i < size; i++) {
								Equation equation = eqList[i];
								add(equation.getHtml(), equation.getMathML());
							}
						}
					}
				});
	}
}
