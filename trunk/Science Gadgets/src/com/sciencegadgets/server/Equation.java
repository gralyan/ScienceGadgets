package com.sciencegadgets.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.measure.quantity.Acceleration;
import javax.measure.quantity.AmountOfSubstance;
import javax.measure.quantity.Angle;
import javax.measure.quantity.AngularAcceleration;
import javax.measure.quantity.AngularVelocity;
import javax.measure.quantity.Area;
import javax.measure.quantity.CatalyticActivity;
import javax.measure.quantity.DataAmount;
import javax.measure.quantity.DataRate;
import javax.measure.quantity.Dimensionless;
import javax.measure.quantity.Duration;
import javax.measure.quantity.DynamicViscosity;
import javax.measure.quantity.ElectricCapacitance;
import javax.measure.quantity.ElectricCharge;
import javax.measure.quantity.ElectricConductance;
import javax.measure.quantity.ElectricCurrent;
import javax.measure.quantity.ElectricInductance;
import javax.measure.quantity.ElectricPotential;
import javax.measure.quantity.ElectricResistance;
import javax.measure.quantity.Energy;
import javax.measure.quantity.Force;
import javax.measure.quantity.Frequency;
import javax.measure.quantity.Illuminance;
import javax.measure.quantity.KinematicViscosity;
import javax.measure.quantity.Length;
import javax.measure.quantity.LuminousFlux;
import javax.measure.quantity.LuminousIntensity;
import javax.measure.quantity.MagneticFlux;
import javax.measure.quantity.MagneticFluxDensity;
import javax.measure.quantity.Mass;
import javax.measure.quantity.MassFlowRate;
import javax.measure.quantity.Power;
import javax.measure.quantity.Pressure;
import javax.measure.quantity.RadiationDoseEffective;
import javax.measure.quantity.RadioactiveActivity;
import javax.measure.quantity.SolidAngle;
import javax.measure.quantity.Temperature;
import javax.measure.quantity.Torque;
import javax.measure.quantity.Velocity;
import javax.measure.quantity.Volume;
import javax.measure.quantity.VolumetricDensity;
import javax.measure.quantity.VolumetricFlowRate;
import javax.measure.unit.NonSI;
import javax.measure.unit.SI;
import javax.measure.unit.Unit;


public class Equation {

    protected static String eqString;
    protected static String[] varsName;
    protected static Double[] varsValue;
    protected static Unit<?>[] varsUnit;
    protected static String findName;

    // protected static String[] givenNames;
    // protected static Double[] givenValues;

    public String toString() {
	return eqString;
    }

    protected static Unit<?> getUnitByQuantity(String varQuantity) {

	Unit<?> unit = RadiationDoseEffective.UNIT;

	if ("Acceleration".equals(varQuantity)) {
	    unit = Acceleration.UNIT;
	} else if ("AmountOfSubstance".equals(varQuantity)) {
	    unit = AmountOfSubstance.UNIT;
	} else if ("Angle".equals(varQuantity)) {
	    unit = Angle.UNIT;
	} else if ("AngularAcceleration".equals(varQuantity)) {
	    unit = AngularAcceleration.UNIT;
	} else if ("AngularVelocity".equals(varQuantity)) {
	    unit = AngularVelocity.UNIT;
	} else if ("Area".equals(varQuantity)) {
	    unit = Area.UNIT;
	} else if ("CatalyticActivity".equals(varQuantity)) {
	    unit = CatalyticActivity.UNIT;
	} else if ("DataAmount".equals(varQuantity)) {
	    unit = DataAmount.UNIT;
	} else if ("DataRate".equals(varQuantity)) {
	    unit = DataRate.UNIT;
	} else if ("Dimensionless".equals(varQuantity)) {
	    unit = Dimensionless.UNIT;
	} else if ("Duration".equals(varQuantity)) {
	    unit = Duration.UNIT;
	} else if ("DynamicViscosity".equals(varQuantity)) {
	    unit = DynamicViscosity.UNIT;
	} else if ("ElectricCapacitance".equals(varQuantity)) {
	    unit = ElectricCapacitance.UNIT;
	} else if ("ElectricCharge".equals(varQuantity)) {
	    unit = ElectricCharge.UNIT;
	} else if ("ElectricConductance".equals(varQuantity)) {
	    unit = ElectricConductance.UNIT;
	} else if ("ElectricCurrent".equals(varQuantity)) {
	    unit = ElectricCurrent.UNIT;
	} else if ("ElectricInductance".equals(varQuantity)) {
	    unit = ElectricInductance.UNIT;
	} else if ("ElectricPotential".equals(varQuantity)) {
	    unit = ElectricPotential.UNIT;
	} else if ("ElectricResistance".equals(varQuantity)) {
	    unit = ElectricResistance.UNIT;
	} else if ("Energy".equals(varQuantity)) {
	    unit = Energy.UNIT;
	} else if ("Force".equals(varQuantity)) {
	    unit = Force.UNIT;
	} else if ("Frequency".equals(varQuantity)) {
	    unit = Frequency.UNIT;
	} else if ("Illuminance".equals(varQuantity)) {
	    unit = Illuminance.UNIT;
	} else if ("KinematicViscosity".equals(varQuantity)) {
	    unit = KinematicViscosity.UNIT;
	} else if ("Length".equals(varQuantity)) {
	    unit = Length.UNIT;
	} else if ("LuminousFlux".equals(varQuantity)) {
	    unit = LuminousFlux.UNIT;
	} else if ("LuminousIntensity".equals(varQuantity)) {
	    unit = LuminousIntensity.UNIT;
	} else if ("MagneticFlux".equals(varQuantity)) {
	    unit = MagneticFlux.UNIT;
	} else if ("MagneticFluxDensity".equals(varQuantity)) {
	    unit = MagneticFluxDensity.UNIT;
	} else if ("Mass".equals(varQuantity)) {
	    unit = Mass.UNIT;
	} else if ("MassFlowRate".equals(varQuantity)) {
	    unit = MassFlowRate.UNIT;
	} else if ("Power".equals(varQuantity)) {
	    unit = Power.UNIT;
	} else if ("Pressure".equals(varQuantity)) {
	    unit = Pressure.UNIT;
	} else if ("RadioactiveActivity".equals(varQuantity)) {
	    unit = RadioactiveActivity.UNIT;
	} else if ("SolidAngle".equals(varQuantity)) {
	    unit = SolidAngle.UNIT;
	} else if ("Temperature".equals(varQuantity)) {
	    unit = Temperature.UNIT;
	} else if ("Torque".equals(varQuantity)) {
	    unit = Torque.UNIT;
	} else if ("Velocity".equals(varQuantity)) {
	    unit = Velocity.UNIT;
	} else if ("Volume".equals(varQuantity)) {
	    unit = Volume.UNIT;
	} else if ("VolumetricDensity".equals(varQuantity)) {
	    unit = VolumetricDensity.UNIT;
	} else if ("VolumetricFlowRate".equals(varQuantity)) {
	    unit = VolumetricFlowRate.UNIT;
	}

	if (unit.isCompatible(RadiationDoseEffective.UNIT)) {
	}

	return unit;

    }

    protected static List<Unit<?>> getAllUnits() {
	Set<Unit<?>> setSI = SI.getInstance().getUnits();
	Set<Unit<?>> setNonSI = NonSI.getInstance().getUnits();
	ArrayList<Unit<?>> listSI = new ArrayList<Unit<?>>(setSI);
	ArrayList<Unit<?>> listNonSI = new ArrayList<Unit<?>>(setNonSI);
	listSI.addAll(listNonSI);

	return listSI;
    }
}
