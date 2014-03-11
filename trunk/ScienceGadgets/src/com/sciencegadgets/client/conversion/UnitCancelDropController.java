package com.sciencegadgets.client.conversion;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.UnitAttribute;
import com.sciencegadgets.shared.UnitHTML;
import com.sciencegadgets.shared.UnitMultiple;
import com.sciencegadgets.shared.UnitName;

public class UnitCancelDropController extends AbstractDropController {

	private ConversionActivity conversionActivity;
	private ConversionWrapper targetWrapper;
	private UnitName unitName;
	private int combinedExp;
	private String unitSymbol;

	public UnitCancelDropController(ConversionWrapper targetWrapper,
			UnitMultiple targetUnit, UnitMultiple dragUnit,
			UnitName unitname) {
		super(targetWrapper);
		conversionActivity = targetWrapper.getConversionActivity();
		this.targetWrapper = targetWrapper;

		this.unitName = unitname;
		this.unitSymbol = unitName.getSymbol();
		int dragExp = Integer.parseInt(dragUnit.getUnitExponent());
		int targetExp = Integer.parseInt(targetUnit.getUnitExponent());
		this.combinedExp = targetExp - dragExp;
	}

	@Override
	public void onDrop(DragContext context) {
		super.onDrop(context);

		MathNode drag = ((Wrapper) context.draggable).getNode();
		MathNode target = ((Wrapper) getDropTarget()).getNode();

		ConversionWrapper dragWraper = ((ConversionWrapper) context.draggable);
		UnitDisplay dragDisplay = dragWraper.getUnitDisplay();
		UnitDisplay targetDisplay = targetWrapper.getUnitDisplay();

		if (combinedExp == 0) {
			targetDisplay.isCanceled = true;
			dragDisplay.isCanceled = true;
			
		} else {
			MathNode nodeToChange;
			UnitDisplay displayToChange;
			
			if (combinedExp > 0) {
				nodeToChange = target;
				dragDisplay.isCanceled = true;
				displayToChange = targetDisplay;
				
			} else {
				combinedExp = Math.abs(combinedExp);
				nodeToChange = drag;
				targetDisplay.isCanceled = true;
				displayToChange = dragDisplay;
			}
			
			MathNode combinedNode = nodeToChange.replace(TypeML.Variable, unitSymbol);
			if (combinedExp > 1) {
				combinedNode = combinedNode.encase(TypeML.Exponential);
				combinedNode.append(TypeML.Number, combinedExp + "");
			}
			combinedNode.setAttribute(MathAttribute.Unit, unitName
					+ UnitAttribute.EXP_DELIMITER + combinedExp);
			
			displayToChange.wrappedNode = combinedNode;
		}

		conversionActivity.reloadEquation();
	}

	@Override
	public void onEnter(DragContext context) {
		super.onEnter(context);
		getDropTarget().addStyleName(CSS.SELECTED_DROP_WRAPPER);
	}

	@Override
	public void onLeave(DragContext context) {
		super.onLeave(context);
		getDropTarget().removeStyleName(CSS.SELECTED_DROP_WRAPPER);
	}
}
