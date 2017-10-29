package edu.nyu.oop;

import java.util.ArrayList;

public class Method {
	
	public String accessModifier;
	public boolean isStatic;
	public String returnType;
	public String name;
	public ArrayList<Parameter> parameters;

	public Method(String accessModifier, boolean isStatic, String returnType, String name, ArrayList<Parameter> parameters) {
		this.accessModifier = accessModifier;
		this.isStatic = isStatic;
		this.returnType = returnType;
		this.name = name;
		this.parameters = parameters;
	}
}