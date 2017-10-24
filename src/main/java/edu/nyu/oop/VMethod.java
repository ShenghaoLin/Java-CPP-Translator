package edu.nyu.oop;

import java.util.ArrayList;

public class VMethod {
	
	public String access_modifier;
	public boolean is_static;
	public String method_name;
	public String initial;

	public VMethod(String access_modifier, boolean is_static, String method_name, String initial) {
		this.access_modifier = access_modifier;
		this.is_static = is_static;
		this.method_name = method_name;
		this.initial = initial;
	}
}