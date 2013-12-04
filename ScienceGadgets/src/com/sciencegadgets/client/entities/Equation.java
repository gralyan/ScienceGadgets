package com.sciencegadgets.client.entities;

import java.io.Serializable;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Entity
public class Equation implements Serializable {

	private static final long serialVersionUID = 8904253167706595238L;

	@Id
	Long id;

	@Index
	List<Key<QuantityKind>> quantityKinds;

	String mathML;
	String html;

	public Equation() {
	}

	public Equation(String mathML, String html, List<Key<QuantityKind>> quantityKinds) {
		this.mathML = mathML;
		this.html = html;
this.quantityKinds = quantityKinds;
	}

	public String getMathML() {
		return mathML;
	}

	public String getHtml() {
		return html;
	}
	
	public List<Key<QuantityKind>> getQuantityKinds() {
		return quantityKinds;
	}
}
