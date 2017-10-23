package edu.nyu.oop;

import java.util.ArrayList;


public class ObjectRep {

	private String name;
	private String parentName;

	public ArrayList<Field> fields = new ArrayList<Field>();
	public ArrayList<Constructor> constructors = new ArrayList<Constructor>();
	public ArrayList<Method> methods = new ArrayList<Method>();
	public VTable vtable = new VTable();

	public static class Field {
		public String access_modifier;
		public boolean is_static;
		public String field_type;
		public String field_name;
		public String initial;
	}

	public static class Constructor {
		public String access_modifier;
		public String constructor_name;
		public ArrayList<Parameter> parameters;
	}

	public static class Method {
		public String access_modifier;
		public boolean is_static;
		public String return_type;
		public String method_name;
		public ArrayList<Parameter> parameters;
	}

	public static class Parameter {
		public String type;
		public String name;

		public Parameter(String type, String name) {
			this.type = type;
			this.name = name;
		}
	}

	public static class VMethod {
		public String className;
		public Method method;

		public VMethod(String className, Method method) {
			this.className = className;
			this.method = method;
		}
	}

	public static class VTable extends ArrayList<VMethod> {
		public boolean check(Method newMethod) {
			for (VMethod oldMethod : this) {
				if(oldMethod.method.method_name.equals(newMethod.method_name))
					return true;
			}
			return false;
		}
	}

	public ObjectRep(String name) {
		this.name = name;
	}

	public void addField(String access_modifier, boolean is_static, String field_type, String field_name, String initial) {
		Field new_field = new Field();
		new_field.access_modifier = access_modifier;
		new_field.is_static = is_static;
		new_field.field_type = field_type;
		new_field.field_name = field_name;
		new_field.initial = initial;
		fields.add(new_field);
	}

	public void addConstructor(String access_modifier, String constructor_name, ArrayList<Parameter> parameters) {
		Constructor new_constructor = new Constructor();
		new_constructor.access_modifier = access_modifier;
		new_constructor.constructor_name = constructor_name;
		new_constructor.parameters = parameters;
		constructors.add(new_constructor);
	}

	public void addMethod(String access_modifier, boolean is_static, String return_type, String method_name, ArrayList<Parameter> parameters) {
		Method new_method = new Method();
		new_method.access_modifier = access_modifier;
		new_method.is_static = is_static;
		new_method.return_type = return_type;
		new_method.method_name = method_name;
		new_method.parameters = parameters;
		methods.add(new_method);
	}

	public void addVMethod(String className, Method method) {
		this.vtable.add(new VMethod(className, method));
	}

	public void addVMethod(VMethod vMethod) {
		this.vtable.add(vMethod);
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