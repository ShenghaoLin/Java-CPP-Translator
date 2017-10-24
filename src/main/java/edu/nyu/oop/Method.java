package edu.nyu.oop;

import java.util.ArrayList;

public class Method {
	
	public String access_modifier;
	public boolean is_static;
	public String return_type;
	public String method_name;
	public ArrayList<Parameter> parameters;

	public Method(String access_modifier, boolean is_static, String return_type, String method_name, ArrayList<Parameter> parameters) {
		this.access_modifier = access_modifier;
		this.is_static = is_static;
		this.return_type = return_type;
		this.method_name = method_name;
		this.parameters = parameters;
	}
}