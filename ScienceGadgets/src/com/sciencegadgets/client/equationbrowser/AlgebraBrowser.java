package com.sciencegadgets.client.equationbrowser;

import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.Moderator;
import com.sciencegadgets.client.SelectionPanel;
import com.sciencegadgets.client.entities.DataModerator;
import com.sciencegadgets.client.entities.Equation;
import com.sciencegadgets.shared.TypeEquationXML;

public class AlgebraBrowser extends SelectionPanel {

	public AlgebraBrowser(final EquationBrowser equationBrowser) {
		super("Algebra Practice", new SelectionHandler() {

			@Override
			public void onSelect(Cell selected) {
				String mathmlStr = selected.getValue();
				if (mathmlStr != null) {
					// Element mathml = (Element)
					// XMLParser.parse(mathmlStr).getDocumentElement();
					Element mathml = new HTML(mathmlStr)
							.getElement().getFirstChildElement();
					Moderator.switchToAlgebra(mathml, equationBrowser.inEditMode);
				}
			}
		});
		getElement().setId(CSS.ALG_BROWSER);

		DataModerator.database
				.getAlgebraEquations(new AsyncCallback<Equation[]>() {
					public void onFailure(Throwable caught) {
						Window.alert("Can't find algebra equations :(");
					}

					public void onSuccess(Equation[] eqList) {
						int size = eqList.length;

						if (size == 0) {
							// Display default a=a if no other equations
							String eq = TypeEquationXML.Equation.getTag();
							String var = TypeEquationXML.Variable.getTag();
							String op = TypeEquationXML.Operation.getTag();
							add("<div>a=a<div>",
									"<"+eq+"><"+var+">a</"+var+"><"+op+">=</"+op+"><"+var+">a</"+var+"></"+eq+">");
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
