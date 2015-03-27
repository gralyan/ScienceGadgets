package com.sciencegadgets.shared;

import java.io.Serializable;

public class Measure implements Serializable {
	private static final long serialVersionUID = -5732171957145739470L;

	String nodeID;
	double ratioX;
	double ratioY;

	public Measure() {
	}
	public Measure(String nodeID, double ratioX, double ratioY) {
		super();
		this.nodeID = nodeID;
		this.ratioX = ratioX;
		this.ratioY = ratioY;
	}

	public String getNodeID() {
		return nodeID;
	}

	public double getRatioX() {
		return ratioX;
	}

	public double getRatioY() {
		return ratioY;
	}

}