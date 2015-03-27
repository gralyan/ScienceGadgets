package com.sciencegadgets.shared;

import java.io.Serializable;
import java.util.HashSet;

public class Diagram implements Serializable {
	private static final long serialVersionUID = 574345758101323214L;

	String imageBlobKey;
	String imageURL;
	HashSet<Measure> measurements = new HashSet<Measure>();

	public Diagram() {
	}

	public Diagram(String imageBlobKey, String imageURL) {
		this.imageBlobKey = imageBlobKey;
		this.imageURL = imageURL;
	}

	public String getImageBlobKey() {
		return imageBlobKey;
	}

	public String getImageURL() {
		return imageURL;
	}

	public void addMeasure(String nodeID, double ratioX, double ratioY) {
		if(nodeID == null) {
			return;
		}
		for(Measure measure : measurements) {
			if(measure.nodeID.equals(nodeID)) {
				measurements.remove(measure);
				break;
			}
		}
		measurements.add(new Measure(nodeID, ratioX, ratioY));
	}

	public HashSet<Measure> getMeasurements() {
		return measurements;
	}

}

