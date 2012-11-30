package com.sciencegadgets.client.equationtree;

public class CoordinateConverter {
	
	double XsvgPerGlobal;
	double YsvgPerGlobal;
	double Xoffset;
	double Yoffset;
	
	public CoordinateConverter(double XsvgPerGlobal, double YsvgPerGlobal, double Xoffset, double Yoffset){
		this.XsvgPerGlobal = XsvgPerGlobal;
		this.YsvgPerGlobal = YsvgPerGlobal;
		this.Xoffset = Xoffset;
		this.Yoffset = Yoffset;
	}

	public double getXsvgPerGlobal() {
		return XsvgPerGlobal;
	}

	public double getYsvgPerGlobal() {
		return YsvgPerGlobal;
	}

	public double getXoffset() {
		return Xoffset;
	}

	public double getYoffset() {
		return Yoffset;
	}

	public double XtoSVG(double Xglobal){
		return (Xglobal - Xoffset) * XsvgPerGlobal;
	}

	public double YtoSVG(double Yglobal){
		return (Yglobal - Yoffset) * YsvgPerGlobal;
	}
	
	public double XtoGlobal(double Xsvg){
		return Xsvg / XsvgPerGlobal + Xoffset;
	}
	
	public double YtoGlobal(double Ysvg){
		return Ysvg / YsvgPerGlobal + Yoffset;
	}
	
}
