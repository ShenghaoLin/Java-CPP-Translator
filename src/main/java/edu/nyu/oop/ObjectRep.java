package edu.nyu.oop;

import java.util.ArrayList;


public class ObjectRep {

	private String name;
	private String parentName;

	public ArrayList<Field> fields = new ArrayList<Field>();
	public ArrayList<Constructor> constructors = new ArrayList<Constructor>();
	public ArrayList<Method> methods = new ArrayList<Method>();

	public static class Field {
		public String access_modifier;
		public boolean is_static;
		public String field_type;
		public String field_name;
		public boolean is_initialized;
		public String initial;
	}

	public static class Constructor {
		public String access_modifier;
		public String constructor_name;
		public ArrayList<String> parameter_types = new ArrayList<String>();
		public ArrayList<String> parameter_names = new ArrayList<String>();
	}

	public static class Method {
		public String access_modifier;
		public boolean is_static;
		public String return_type;
		public String method_name;
		public ArrayList<String> parameter_types = new ArrayList<String>();
		public ArrayList<String> parameter_names = new ArrayList<String>();
	}

	public ObjectRep(String name) {
		this.name = name;
	}

	public void addField(String access_modifier, boolean is_static, String field_type, String field_name, boolean is_initialized, String initial) {
		Field new_field = new Field();
		new_field.access_modifier = access_modifier;
		new_field.is_static = is_static;
		new_field.field_type = field_type;
		new_field.field_name = field_name;
		new_field.is_initialized = is_initialized;
		new_field.initial = initial;
		fields.add(new_field);
	}

	public void addConstructor(String access_modifier, String constructor_name, ArrayList<String> parameter_types, ArrayList<String> parameter_names) {
		Constructor new_constructor = new Constructor();
		new_constructor.access_modifier = access_modifier;
		new_constructor.constructor_name = constructor_name;
		new_constructor.parameter_types = parameter_types;
		new_constructor.parameter_names = parameter_names;
		constructors.add(new_constructor);
	}

	public void addMethod(String access_modifier, boolean is_static, String return_type, String method_name, ArrayList<String> parameter_types, ArrayList<String> parameter_names) {
		Method new_method = new Method();
		new_method.access_modifier = access_modifier;
		new_method.is_static = is_static;
		new_method.return_type = return_type;
		new_method.method_name = method_name;
		new_method.parameter_types = parameter_types;
		new_method.parameter_names = parameter_names;
		methods.add(new_method);
	}

	public Field getCurrentField() {
		return this.fields.get(this.fields.size()-1);
	}

	public Constructor getCurrentConstructor() {
		return this.constructors.get(this.constructors.size()-1);
	}

	public Method getCurrentMethod() {
		return this.methods.get(this.methods.size()-1);
	}

	public String getName() {
		return this.name;
	}

	public String getParentName() {
		return this.parentName;
	}

	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
}