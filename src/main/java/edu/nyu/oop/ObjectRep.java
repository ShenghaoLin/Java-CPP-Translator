/**
 * ObjectRep class that represents the data layout and vtable of an object
 * every ObjectRep has its name, pointer to its parent, data layout declared
 * as classRep and vtable layout declared as vtable
 * the class ClassRep is used to represent the data layout of an object
 * it contains array lists of fields, constructors and methods
 * the class VTable is used to represent the vtable layut of an object
 * it contains array lists of fields and vmethods
 * upon initialization of this class, vptr, vtable, is_a and class constructor
 * are added to releveant representations automatically to ease some work on
 * Phase 2
 *
 * @author Goktug Saatcioglu
 * @author Shenghao Lin
 *
 * @version 1.0
 */
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
        ArrayList<Parameter> temp = new ArrayList<Parameter>();
        temp.add(new Parameter(this.name, "__this"));
        classRep.constructors.add(new Constructor("public", "init", temp));
        // class name
        classRep.methods.add(new Method("public", true, "Class", "__class", new ArrayList<Parameter>()));

        this.vtable = new VTable();
        // __is_a
        vtable.fields.add(new Field("public", false, "Class", "__is_a", ""));
        vtable.methods.add(new VMethod("public", false, "__is_a", "(__" + this.name + "::__class())"));
    }
}