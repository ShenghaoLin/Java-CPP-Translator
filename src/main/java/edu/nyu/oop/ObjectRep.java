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
		// __vptr
		classRep.fields.add(new Field("public", false, "__" + this.name + "_VT*", "__vptr", ""));
		// __vtable
		classRep.fields.add(new Field("public", true, "__" + this.name + "_VT", "__vtable", ""));
		// default constructor
		classRep.constructors.add(new Constructor("public", this.name, new ArrayList<Parameter>()));
		// class name
		classRep.methods.add(new Method("public", true, "Class", "__class", new ArrayList<Parameter>()));

		this.vtable = new VTable();
		// __is_a
		vtable.fields.add(new Field("public", false, "Class", "__is_a", ""));
		vtable.methods.add(new VMethod("public", false, "__is_a", "(__" + this.name + "::__class())"));
	}
}