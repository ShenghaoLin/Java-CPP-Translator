package edu.nyu.oop;

import java.util.ArrayList;

public class Constructor {
	
	public String access_modifier;
	public String constructor_name;
	public ArrayList<Parameter> parameters;

	public Constructor(String access_modifier, String constructor_name, ArrayList<Parameter> parameters) {
		this.access_modifier = access_modifier;
		this.constructor_name = constructor_name;
		this.parameters = parameters;
	}
}