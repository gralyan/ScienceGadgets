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
package com.sciencegadgets.client.algebramanipulation.dropcontrollers;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.sciencegadgets.client.Log;
import com.sciencegadgets.client.algebramanipulation.EquationTransporter;
import com.sciencegadgets.client.algebramanipulation.MLElementWrapper;
import com.sciencegadgets.client.equationtree.MathMLBindingTree.MathMLBindingNode;

public abstract class AbstractMathDropController extends AbstractDropController {
	protected MLElementWrapper source;
	protected MLElementWrapper target;
	protected MathMLBindingNode sourceNode;
	protected MathMLBindingNode targetNode;
	public String change = "";

	public AbstractMathDropController(Widget dropTarget) {
		super(dropTarget);
		target = (MLElementWrapper) dropTarget;
		targetNode = target.getJohnNode();

	}

	@Override
	public void onDrop(DragContext context) {

		source = ((MLElementWrapper) context.draggable);
		sourceNode = source.getJohnNode();
		MathMLBindingNode sourceParent = sourceNode.getParent();

		Boolean isCorrect = askQuestion();
		if (isCorrect){
			// Actual changes. Abstract method - to be overridden
			onChange();
		}else{
			Window.alert("Fail");
		}
		

		// If the source is the last child, get rid of the parent
		try {
			if (sourceParent.getChildCount() == 1) {
				try {
					MathMLBindingNode baseParent = sourceParent.getParent();
					baseParent.add(sourceParent.getIndex(),
							sourceParent.getFirstChild());
					sourceParent.remove();
					Log.info("removed obsolete parent: "
							+ sourceParent.toString());

				} catch (NullPointerException e) {
					e.printStackTrace();
				}
			}
		} catch (NullPointerException e) {
			Log.severe("source has no parent: " + sourceNode.toString());
			e.printStackTrace();
		}

		// Clean wrappers
		for (MLElementWrapper wrap : targetNode.getTree().getWrappers()) {
			wrap.removeStyleName("selectedDropWrapper");
			wrap.getJoinedWrapper().removeStyleName("selectedDropWrapper");
		}

		// Updates
		HTML mathML = targetNode.getTree().toMathML();
		Log.info("transporting: " + mathML.getHTML());
		// AlgOutEntry.updateAlgOut(new HTML(mathML.getHTML()));
		EquationTransporter.selectEquation(mathML, changeComment());
	}

	@Override
	public void onEnter(DragContext context) {
		// Style of target when source is dragged over
		target.addStyleName("mouseOverlay");
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		target.removeStyleName("mouseOverlay");
	}

	/**
	 * Prompts the user with the question.
	 * @return True if Pass, False if Fail
	 */
	protected abstract Boolean askQuestion();
	
	/**
	 * The changes that occur in the tree. findChange should be called first.
	 */
	protected abstract void onChange();

	/**
	 * A description of the change that occurs, to be used for hints
	 * 
	 * @param sourceNode
	 * @return
	 */
	public abstract String findChange(MathMLBindingNode sourceNode);

	/**
	 * A comment for the change, to be used for the AlgOut comment
	 * 
	 * @return
	 */
	abstract String changeComment();

}
