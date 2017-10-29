package edu.nyu.oop;

import java.util.ArrayList;

public class Constructor {
	
	public String accessModifier;
	public String name;
	public ArrayList<Parameter> parameters;

	public Constructor(String accessModifier, String name, ArrayList<Parameter> parameters) {
		this.accessModifier = accessModifier;
		this.name = name;
		this.parameters = parameters;
	}
}