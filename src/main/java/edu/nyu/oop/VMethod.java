package edu.nyu.oop;

import java.util.ArrayList;

public class VMethod {
	
	public String accessModifier;
	public boolean isStatic;
	public String name;
	public String initial;

	public VMethod(String accessModifier, boolean isStatic, String name, String initial) {
		this.accessModifier = accessModifier;
		this.isStatic = isStatic;
		this.name = name;
		this.initial = initial;
	}
}