package edu.nyu.oop;

import java.util.ArrayList;

public class ObjectRep {

	public String name;
	public ObjectRep parent;

	public ClassRep classRep;
	public VTable vtable;

	public static class ClassRep {
		public ArrayList<Field> fields = new ArrayList<Field>();
		public ArrayList<Constructor> constructors = new ArrayList<Constructor>();
		public ArrayList<Method> methods = new ArrayList<Method>();
	}

	public static class VTable {
		public ArrayList<Field> fields = new ArrayList<Field>();
		public ArrayList<VMethod> methods = new ArrayList<VMethod>();
	}

	public ObjectRep(String name) {

		this.name = name;
		this.parent = null;

		this.classRep = new ClassRep();
		Field __vptr = new Field("public", false, "__" + this.name + "_VT*", "__vptr", "");
		classRep.fields.add(__vptr);
		Field __vtable = new Field("public", true, "__" + this.name + "_VT", "__vtable", "");
		classRep.fields.add(__vtable);
		Constructor constructor = new Constructor("public", "__" + this.name + "()", new ArrayList<Parameter>());
		classRep.constructors.add(constructor);
		Method class_name = new Method("public", true, "Class", "__class()", new ArrayList<Parameter>());
		classRep.methods.add(class_name);

		this.vtable = new VTable();
		Field __isa_field = new Field("public", false, "Class", "__is_a", "");
		vtable.fields.add(__isa_field);
		VMethod __isa_method = new VMethod("public", false, "__is_a", "(__" + this.name + "::__class())");
		vtable.methods.add(__isa_method);
	}
}