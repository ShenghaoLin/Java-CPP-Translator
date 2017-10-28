package edu.nyu.oop;

public class Field {

	public String access_modifier;
	public boolean is_static;
	public String field_type;
	public String field_name;
	public String initial;
	public String inherited_from;

	public Field(String access_modifier, boolean is_static, String field_type, String field_name, String initial) {
		this.access_modifier = access_modifier;
		this.is_static = is_static;
		this.field_type = field_type;
		this.field_name = field_name;
		this.initial = initial;
		this.inherited_from = "";
	}
}