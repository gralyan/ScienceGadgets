package com.sciencegadgets.client.conversion;

import java.util.LinkedList;

import com.allen_sauer.gwt.dnd.client.DragContext;
import com.allen_sauer.gwt.dnd.client.drop.AbstractDropController;
import com.sciencegadgets.client.CSS;
import com.sciencegadgets.client.algebra.Wrapper;
import com.sciencegadgets.client.algebra.MathTree.MathNode;
import com.sciencegadgets.client.conversion.ConversionActivity.UnitDisplay;
import com.sciencegadgets.shared.MathAttribute;
import com.sciencegadgets.shared.TypeML;
import com.sciencegadgets.shared.UnitUtil;

public class UnitCancelDropController extends AbstractDropController {

	private ConversionActivity conversionActivity;
	private ConversionWrapper targetWrapper;
	private String unitName;
	private int combinedExp;
	private String unitSymbol;

	public UnitCancelDropController(ConversionWrapper targetWrapper,
			String targetUnitAttribute, String dragUnitAttribute,
			String unitname) {
		super(targetWrapper);
		conversionActivity = targetWrapper.getConversionActivity();
		this.targetWrapper = targetWrapper;

		this.unitName = unitname;
		System.out.println(unitname);
		this.unitSymbol = UnitUtil.getSymbol(unitName);
		int dragExp = Integer.parseInt(UnitUtil.getExponent(dragUnitAttribute));
		int targetExp = Integer.parseInt(UnitUtil
				.getExponent(targetUnitAttribute));
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
					+ UnitUtil.EXP_DELIMITER + combinedExp);
			
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
