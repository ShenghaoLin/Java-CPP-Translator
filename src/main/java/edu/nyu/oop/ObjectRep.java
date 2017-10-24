package edu.nyu.oop;

import java.util.ArrayList;

public class ObjectRep {

	private String name;
	private String parentName;

	public ClassRep classRep;
	public VTable vtable;

	public static class ClassRep {
		public ArrayList<Field> fields = new ArrayList<Field>();
		public ArrayList<Constructor> constructors = new ArrayList<Constructor>();
		public ArrayList<Method> methods = new ArrayList<Method>();
	}

	public static class VTable {
		public ArrayList<Field> fields = new ArrayList<Field>();
		public ArrayList<Method> methods = new ArrayList<Method>();
	}

	public ObjectRep(String name) {

		this.name = name;

		this.classRep = new ClassRep();
		Field __vptr = new Field("public", false, "__" + this.name + "_VT*", "__vptr", "");
		classRep.fields.add(__vptr);
		Field __vtable = new Field("public", true, "__" + this.name + "_VT", "__vtable", "");
		classRep.fields.add(__vtable);
		Constructor constructor = new Constructor("public", "__" + this.name + "()", null);
		classRep.constructors.add(constructor);
		Method class_name = new Method("public", true, "Class", "__class()", null);
		classRep.methods.add(class_name;

		this.vtable = new VTable();
		Field __isa = new Field("public", false, "Class", "__isa", "");
		vtable.fields.add(__isa);
		Method __isa = new Method("public", false, "", "__isa", "(__" + this.name + "::__class())");
		vtable.methods.add(__isa);
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