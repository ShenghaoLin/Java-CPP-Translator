package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;
import java.util.Iterator;

public class Phase2 {

    public static Node runPhase2(Node n) {

        //Traverse Java AST
        Phase2Visitor visitor = new Phase2Visitor();
        visitor.traverse(n);

        //Build list of class representations (java.lang, inheritance)
        ObjectRepList unfilled = visitor.getObjectRepresentations();
        ObjectRepList filled = getFilledObjectRepList(unfilled);

        //Build C++ AST from class representations
        //return buildCppAst(filled);
        return GNode.create("some bullshit");
    }

    public static ObjectRepList getFilledObjectRepList(ObjectRepList unfilled) {

        // manually add Object, String, Class to reprepsentation list
        ObjectRepList filled = initializeRepList();
        // manutal addition is finished!

        // fill filled list using contents of unfilled, method also considers hierarchy
        filled = fill(filled, unfilled);
        // heiarchical order is determined!

        // process the v-table for each object except Object, String, Class and Main containing object
        for (ObjectRep rep : filled) {

            if (!rep.name.equals("Object") && !rep.name.equals("String") && !rep.name.equals("Class")) {

                boolean flag = true;

                // using flags and iteration to determine if main is in representation
                for (Method method : rep.classRep.methods) if (method.method_name.equals("main")) flag = false;

                // if main is not there, then process output and replace new with the old
                if (flag) {
                    ObjectRep new_rep = process(rep, rep.parent);
                    int replace_idx = filled.getIndexFromName(rep.name);
                    filled.set(replace_idx, new_rep);
                } 
            }

            // don't forget to update the parents after replacing so that logic works, "bubbling down"
            for (ObjectRep rep_sub : filled) {
                if (rep_sub.parent != null) {
                    int parent_index = filled.getIndexFromName(rep_sub.name);
                    rep_sub.parent = filled.get(parent_index);
                }
            }
        }

        //Iterate through class representations and build vtables/inherited fields
        /*
        for(ObjectRep rep : filled) {

            /* add v-table methods manually using the traversal mechanism TO-DO
            //Add class methods to the vtable
            for (Method method : rep.methods)
                rep.addVMethod(rep.getName(), method);
            

            //Skip Object, it has no parent
            if (rep.getName().equals("Object")) continue;

            //Skip class with main method
            for (Method method : rep.methods) {
                if(method.method_name.equals("main"))
                    continue;
            }

            //If no other parent, set parent to Object
            if (rep.getParentName() == null)
                rep.setParentName("Object");

            //Add parent methods to the vtable
            ObjectRep parent = filled.getFromName(rep.getParentName());
            for (Method method : parent.methods) {
                if(!rep.vtable.check(method))
                    rep.addVMethod(parent.getName(), method);
            }
            for (VMethod vmethod : parent.vtable) {
                if(!rep.vtable.check(vmethod.method))
                    rep.addVMethod(vmethod);
            }

            //Add inherited fields
            for (Field field : parent.fields)
                rep.fields.add(field);
        }
        */

        return filled;
    }

    public static ObjectRep process(ObjectRep current, ObjectRep parent) {

        // first process the methods of the class declaration
        // note: constructor processing is not required at the moment
        // fields are simply expanded out in visitor with corresponding method definitions
        // similarly update methods using relationship between an object and its parent
        // constructors are basically simple, multiple constructors currently not allowed

        ArrayList<Field> updated_fields = determineFields(parent.vtable.fields, current.classRep.methods, current, parent);

        current.vtable.fields = updated_fields;

        ArrayList<VMethod> updated_v_methods = determineVMethods();

        current_vtable.methods = updated_v_methods;

        return current;

    }

    public static ArrayList<VMethod> determineVMethods() {

    }

    public static ArrayList<Field> determineFields(ArrayList<Field> parent_fields, ArrayList<Method> current_methods, ObjectRep current, ObjectRep parent) {


        ArrayList<Field> updated_fields = new ArrayList<Field>();

        // determine method declarations dependent on parent declarations (overwritten or not)
        for (Field parent_field : parent_fields) {
            // boolean to check if something was updated
            boolean not_updated = true;
            // loop over current methods to determine what has been overwritten and what hasn't, this will ensure preservation of order too
            for (Method current_method : current_methods) {
                // if the field is class don't process anything, preprocessing for this is already achieved in ObjectRep initialization
                if (parent_field.field_name.equals("Class")) {
                    not_updated = false;
                }
                // if method is overwritten by child, need extra processing
                else if (parent_field.field_name.replaceFirst("*","").equals(current_method.method_name)) {
                    // process parameters correctly into field declaration
                    String parameters = "";
                    ArrayList<Parameter> params = current_method.parameters;
                    for (Parameter param : params) {
                        if (params.indexOf(param) == params.size() - 1) parameters += param.type;
                        else parameters += param.type + ",";
                    }
                    updated_fields.add(new Field(current_method.access_modifier, true, current_method.return_type, "*"+current_method.method_name, parameters));
                    not_updated = false;
                }
            }
            // if method wasn't overwritten, modify its args and simply add to updated_fields list
            if (not_updated) updated_fields.add(new Field(parent_field.access_modifier, parent_field.is_static, parent_field.field_type, parent_field.field_name, parent_field.initial.replaceFirst(parent.name, current.name)));
        }

        // dump rest of methods in current_methods into updated_fields and set precedent for order + preserve order
        for (Method current_method : current_methods) {
            // loop over updated fields to ensure if something was added in, if so ignore, else dump
            for (Field updated_field : updated_fields) {
                // if not already declared, declare it now
                if (!current_method.method_name.equals(updated_field.field_name.replaceFirst("*",""))) {
                    // process parameters correctly into field declaration
                    String parameters = "";
                    ArrayList<Parameter> params = current_method.parameters;
                    for (Parameter param : params) {
                        if (params.indexOf(param) == params.size() - 1) parameters += param.type;
                        else parameters += param.type + ",";
                    }
                    updated_fields.add(new Field(current_method.access_modifier, true, current_method.return_type, "*"+current_method.method_name, parameters));
                }
            }
        }

        return updated_fields;
    }

    public static ObjectRepList fill(ObjectRepList filled, ObjectRepList unfilled) {

        //Add classes from unfilled, keep doing this until filled has same size as unfilled
        while (filled.size() < unfilled.size() + 3) {

            for (ObjectRep rep : unfilled) {

                // if no parent set parent to ObjectRep, which will be at filled.get(0)
                if (rep.parent == null) {
                    rep.parent = filled.get(0);
                }

                //Don't add until parent class is already in filled, makes sure classes are in inheritance order, i.e. if a class iherits a class it should be further down the list
                if (filled.getFromName(rep.parent.name) != null) {
                    int parent_idx = filled.getIndexFromName(rep.parent.name);
                    rep.parent = filled.get(parent_idx);
                    filled.add(rep);
                }
            }
        }

        return filled;
    }

    public static ObjectRepList initializeRepList() {

        ObjectRepList filled = new ObjectRepList();

        // Adding java.lang structure manually for Object
        ObjectRep objectRep = new ObjectRep("Object");
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        params.add(new Parameter("Object", "o"));
        Method hashCode = new Method("public", true, "int32_t", "hashCode", params);
        objectRep.classRep.methods.add(hashCode);
        params.add(new Parameter("Object", "o"));
        Method equals = new Method("public", true, "bool", "equals", params);
        objectRep.classRep.methods.add(equals);
        params.remove(1);
        Method getClass = new Method("public", true, "Class", "getClass", params);
        objectRep.classRep.methods.add(getClass);
        Method toString = new Method("public", true, "String", "toString", params);
        objectRep.classRep.methods.add(toString);
        // class representation for Object filled, now do V-Table filling
        Field v_hashCode = new Field("public", false, "int32_t", "*hashCode", "Object");
        objectRep.vtable.fields.add(v_hashCode);
        Field v_equals = new Field("public", false, "bool", "*equals", "Object, Object");
        objectRep.vtable.fields.add(v_equals);
        Field v_getClass = new Field("public", false, "Class", "*getClass", "Object");
        objectRep.vtable.fields.add(v_getClass);
        Field v_toString = new Field("public", false, "String", "*toString", "Object");
        objectRep.vtable.fields.add(v_toString);
        VMethod v_method_hashCode = new VMethod("public", false, "hashCode", "&__Object::__hashCode");
        objectRep.vtable.methods.add(v_method_hashCode);
        VMethod v_method_equals = new VMethod("public", false, "equals", "&__Object::__equals");
        objectRep.vtable.methods.add(v_method_equals);
        VMethod v_method_getClass = new VMethod("public", false, "getClass", "&__Object::__getClass");
        objectRep.vtable.methods.add(v_method_getClass);
        VMethod v_method_toString = new VMethod("public", false, "toString", "&__Object::__toString");
        objectRep.vtable.methods.add(v_method_toString);
        // set parent to null
        objectRep.parent = null;
        // object is created!
        filled.add(objectRep);

        // Adding java.lang structure manually for String
        ObjectRep stringRep = new ObjectRep("String");
        Field data = new Field("public", false, "std::string", "data", "");
        stringRep.classRep.fields.add(data);
        params = new ArrayList<Parameter>();
        params.add(new Parameter("std::string", "data"));
        Constructor stringConstructor = new Constructor("public", "__String", params);
        stringRep.classRep.constructors.remove(0);
        stringRep.classRep.constructors.add(stringConstructor);
        params = new ArrayList<Parameter>();
        params.add(new Parameter("String", "str"));
        hashCode = new Method("public", true, "int32_t", "hashCode", params);
        stringRep.classRep.methods.add(hashCode);
        params.add(new Parameter("Object", "o"));
        equals = new Method("public", true, "bool", "equals", params);
        stringRep.classRep.methods.add(equals);
        params.remove(1);
        toString = new Method("public", true, "int32_t", "length", params);
        stringRep.classRep.methods.add(toString);
        params.add(new Parameter("int32_t", "int_length"));
        Method charAt = new Method("public", true, "char", "charAt", params);
        stringRep.classRep.methods.add(charAt);
        // class representation for String filled, now do V-Table filling
        v_hashCode = new Field("public", false, "int32_t", "*hashCode", "String");
        stringRep.vtable.fields.add(v_hashCode);
        v_equals = new Field("public", false, "bool", "*equals", "String, Object");
        stringRep.vtable.fields.add(v_equals);
        v_getClass = new Field("public", false, "Class", "*getClass", "String");
        stringRep.vtable.fields.add(v_getClass);
        v_toString = new Field("public", false, "String", "*toString", "String");
        stringRep.vtable.fields.add(v_toString);
        Field v_length = new Field("public", false, "int32_t", "*length", "String");
        stringRep.vtable.fields.add(v_length);
        Field v_charAt = new Field("public", false, "char", "*charAt", "String");
        stringRep.vtable.fields.add(v_charAt);
        v_method_hashCode = new VMethod("public", false, "hashCode", "&__String::__class()");
        stringRep.vtable.methods.add(v_method_hashCode);
        v_method_equals = new VMethod("public", false, "equals", "&__String::equals");
        stringRep.vtable.methods.add(v_method_equals);
        v_method_getClass = new VMethod("public", false, "getClass", "(Class(*)(String) &__Object::getClass");
        stringRep.vtable.methods.add(v_method_getClass);
        v_method_toString = new VMethod("public", false, "toString", "&__toString::toString");
        stringRep.vtable.methods.add(v_method_toString);
        VMethod v_method_charAt = new VMethod("public", false, "charAt", "&__String::charAt");
        stringRep.vtable.methods.add(v_method_charAt);
        // set parent to Object
        stringRep.parent = objectRep;
        // String is created!
        filled.add(stringRep);

        // Adding java.lang structure manually for Class
        ObjectRep classRep = new ObjectRep("Class");
        Field name = new Field("public", false, "String", "name", "");
        classRep.classRep.fields.add(name);
        Field parent = new Field("public", false, "Class", "parent", "");
        classRep.classRep.fields.add(parent);
        params = new ArrayList<Parameter>();
        params.add(new Parameter("String", "name"));
        params.add(new Parameter("Class", "parent"));
        Constructor classConstructor = new Constructor("public", "__Class", params);
        classRep.classRep.constructors.remove(0);
        classRep.classRep.constructors.add(classConstructor);
        params = new ArrayList<Parameter>();
        params.add(new Parameter("Class", "c"));
        toString = new Method("public", true, "String", "toString", params);
        classRep.classRep.methods.add(toString);
        Method getName = new Method("public", true, "String", "getName", params);
        classRep.classRep.methods.add(getName);
        Method getSuperClass = new Method("public", true, "Class", "getSuperclass", params);
        classRep.classRep.methods.add(getSuperClass);
        params.add(new Parameter("Object", "o"));
        Method isInstance = new Method("public", true, "bool", "isInstance", params);
        classRep.classRep.methods.add(isInstance);
        // class representation for Class filled, now do V-Table filling
        v_hashCode = new Field("public", false, "int32_t", "*hashCode", "Class");
        classRep.vtable.fields.add(v_hashCode);
        v_equals = new Field("public", false, "bool", "*equals", "Class, Object");
        classRep.vtable.fields.add(v_equals);
        v_getClass = new Field("public", false, "Class", "*getClass", "Class");
        classRep.vtable.fields.add(v_getClass);
        v_toString = new Field("public", false, "String", "*toString", "Class");
        classRep.vtable.fields.add(v_toString);
        Field v_getName = new Field("public", false, "String", "*getName", "Class");
        classRep.vtable.fields.add(v_getName);
        Field v_getSuperClass = new Field("public", false, "String", "*getSuperclass", "Class");
        classRep.vtable.fields.add(v_getSuperClass);
        Field v_isInstance = new Field("public", false, "String", "*isInstance", "Class");
        classRep.vtable.fields.add(v_isInstance);
        v_method_hashCode = new VMethod("public", false, "hashCode", "(int32_t(*)(Class) &__Object::hashCode");
        classRep.vtable.methods.add(v_method_hashCode);
        v_method_equals = new VMethod("public", false, "equals", "(bool(*)(Class,Object) &__Object::equals");
        classRep.vtable.methods.add(v_method_equals);
        v_method_getClass = new VMethod("public", false, "getClass", "(Class(*)(Class) &__Object::getClass");
        classRep.vtable.methods.add(v_method_getClass);
        v_method_toString = new VMethod("public", false, "toString", "&__Class::toString");
        classRep.vtable.methods.add(v_method_toString);
        VMethod v_method_getName = new VMethod("public", false, "getName", "&__Class::getName");
        classRep.vtable.methods.add(v_method_getName);
        VMethod v_method_getSuperClass = new VMethod("public", false, "getSuperclass", "&__Class::getSuperclass");
        classRep.vtable.methods.add(v_method_getSuperClass);
        VMethod v_method_isInstance = new VMethod("public", false, "isInstance", "&__Class::isInstance");
        classRep.vtable.methods.add(v_method_isInstance);
        // set parent to object
        classRep.parent = objectRep;
        // class is created!
        filled.add(classRep);

        return filled;
    }

    /*
    public static Node buildCppAst(ObjectRepList ObjectRepList) {
        Node root = GNode.create("CompilationUnit");

        //TODO: STILL NEED TO GET REAL PACKAGE INFO
        Node packageDeclaration = GNode.create("PackageDeclaration");
        Node packageName = GNode.create("?????");
        packageDeclaration.add(packageName);
        root.add(packageDeclaration);

        for (ObjectRep rep : ObjectRepList)
            root.add(buildClassNode(rep));

        return root;
    }

    public static Node buildClassNode(ObjectRep rep) {
        Node name = GNode.create("ClassName", rep.getName());
        Node vtablePointer = GNode.create("VTablePointer");
        Node fields = GNode.create("FieldDeclarations");
        for(Field field : rep.fields) {
            fields.add(buildFieldNode(field));
        }
        Node constructors = GNode.create("ConstructorDeclarations");
        for(Constructor constructor : rep.constructors) {
            constructors.add(buildConstructorNode(constructor));
        }
        Node methods = GNode.create("MethodDeclarations");
        for(Method method : rep.methods) {
            methods.add(buildMethodNode(method));
        }
        Node vtable = buildVTableNode(rep.vtable);
        return GNode.create("ClassDeclaration", name, vtablePointer, fields, constructors, methods, vtable);
    }

    public static Node buildFieldNode(Field field) {
        Node accessModifier = GNode.create("AccessModifier", field.access_modifier);
        Node isStatic = GNode.create("IsStatic", String.valueOf(field.is_static));
        Node type = GNode.create("FieldType", field.field_type);
        Node name = GNode.create("FieldName", field.field_name);
        Node initial = GNode.create("Initial", field.initial);
        return GNode.create("Field", accessModifier, isStatic, type, name, initial);
    }

    public static Node buildConstructorNode(Constructor constructor) {
        Node accessModifier = GNode.create("AccessModifier", constructor.access_modifier);
        Node name = GNode.create("ConstructorName", constructor.constructor_name);
        Node parameters = GNode.create("Parameters");
        for(Parameter p : constructor.parameters) {
            Node parameterType = GNode.create("ParameterType", p.type);
            Node parameterName = GNode.create("ParameterName", p.name);
            Node parameter = GNode.create("Parameter", parameterType, parameterName);
            parameters.add(parameter);
        }
        return GNode.create("ConstructorDeclaration", accessModifier, name, parameters);
    }

    public static Node buildMethodNode(Method method) {
        Node accessModifier = GNode.create("AccessModifier", method.access_modifier);
        Node isStatic = GNode.create("IsStatic", String.valueOf(method.is_static));
        Node returnType = GNode.create("ReturnType", method.return_type);
        Node name = GNode.create("MethodName", method.method_name);
        Node parameters = GNode.create("Parameters");
        for(Parameter p : method.parameters) {
            Node parameterType = GNode.create("ParameterType", p.type);
            Node parameterName = GNode.create("ParameterName", p.name);
            Node parameter = GNode.create("Parameter", parameterType, parameterName);
            parameters.add(parameter);
        }
        return GNode.create("MethodDeclaration", accessModifier, isStatic, returnType, name, parameters);
    }

    public static Node buildVTableNode(VTable vtable) {
        Node root = GNode.create("VTable");
        for(VMethod vmethod : vtable) {
            Node className = GNode.create("ClassName", vmethod.className);
            Node methodName = GNode.create("MethodName", vmethod.method.method_name);
            root.add(GNode.create("VMethod", className, methodName));
        }
        return root;
    }
    */

    public static class Phase2Visitor extends Visitor {

        private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        private ObjectRepList objectReps = new ObjectRepList();

        public void visitCompilationNode(GNode n) { visit(n); }

        public void visitClassDeclaration(GNode n) {
            objectReps.add(new ObjectRep(n.getString(1)));
            visit(n);
        }

        public void visitExtension(GNode n) {
            objectReps.getCurrent().parent = new ObjectRep(n.getNode(0).getNode(0).getString(0));
            visit(n);
        }

        public void visitConstructorDeclaration(GNode n) {

            // modifier and static checking
            String access_modifier = "";
            boolean is_static = false;

            // iterator so we can iterate over the nodes modifier contents
            Iterator modifier_iter = n.getNode(0).iterator();

            while (modifier_iter.hasNext()) {
                Node node_sub = (Node) modifier_iter.next();
                if (node_sub.getString(0).equals("static")) is_static = true;
                else access_modifier = node_sub.getString(0);
            }

            // get name of constructor
            String constructor_name = n.getString(2);

            // get parameters of constructor
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();

            // iterator so we can iterate over the nodes parameter contents
            Iterator parameters_iter = n.getNode(4).iterator();

            while (parameters_iter.hasNext()) {
                Node node_sub = (Node) parameters_iter.next();
                String type = node_sub.getNode(1).getNode(0).getString(0);
                type = converter(type);
                String name = node_sub.getString(3);
                parameters.add(new Parameter(type, name));
            }

            // add this constructor
            Constructor constructor = new Constructor(access_modifier, constructor_name, parameters);
            objectReps.getCurrent().classRep.constructors.add(constructor);

            // keep traversing
            visit(n);
        }

        public void visitMethodDeclaration(GNode n) {

            // modifier and static checking
            String access_modifier = "";
            boolean is_static = false;

            // iterator so we can iterate over modifiers
            Iterator modifier_iter = n.getNode(0).iterator();

            while (modifier_iter.hasNext()) {
                Node node_sub = (Node) modifier_iter.next();
                if (node_sub.getString(0).equals("static")) is_static = true;
                else access_modifier = node_sub.getString(0);
            }

            // determine return type by visiting return node
            String return_type;
            Node return_node = n.getNode(2);

            if (return_node.getName().equals("VoidType")) return_type = "void";
            else return_type = return_node.getNode(0).getString(0);

            return_type = converter(return_type);

            // get name of method
            String method_name = n.getString(3);

            // get parameters of method
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();

            // iterator so we can iterate over the nodes of the parameter contents
            Iterator parameters_iter = n.getNode(4).iterator();

            while (parameters_iter.hasNext()) {
                Node node_sub = (Node) parameters_iter.next();
                String type = node_sub.getNode(1).getNode(0).getString(0);
                type = converter(type);
                String name = node_sub.getString(3);
                parameters.add(new Parameter(type, name));
            }

            // add this method
            Method method = new Method(access_modifier, is_static, return_type, method_name, parameters);
            objectReps.getCurrent().classRep.methods.add(method);
            
            visit(n);
        }

        public void visitFieldDeclaration(GNode n) {

            // modifier and static checking
            String access_modifier = "";
            boolean is_static = false;

            // iterator so we can iterate over modifiers
            Iterator modifier_iter = n.getNode(0).iterator();

            while (modifier_iter.hasNext()) {
                Node node_sub = (Node) modifier_iter.next();
                if (node_sub.getString(0).equals("static")) is_static = true;
                else access_modifier = node_sub.getString(0);
            }

            // type of the field
            String field_type = n.getNode(1).getNode(0).getString(0);
            field_type = converter(field_type);

            // name of the field
            String field_name = n.getNode(2).getNode(0).getString(0);

            // check if field is initialized to anything
            String initial = "";

            Node initial_node = n.getNode(2).getNode(0).getNode(2);
            if (initial_node != null) initial = initial_node.getString(0);

            // add this field
            Field field = new Field(access_modifier, is_static, field_type, field_name, initial);
            objectReps.getCurrent().classRep.fields.add(field);

            visit(n);
        }

        public String converter(String type) {

            // converted c type
            String new_type = "";

            // determine type
            if (type.equals("long")) new_type = "int64_t";
            else if (type.equals("int")) new_type = "int32_t";
            else if (type.equals("short")) new_type = "int16_t";
            else if (type.equals("byte")) new_type = "int8_t";
            else if (type.equals("boolean")) new_type = "bool";
            else new_type = type;

            return new_type;
        }

        public void traverse(Node n) { super.dispatch(n); }

        public void visit(Node n) { for (Object o : n) if (o instanceof Node) dispatch((Node) o); }

        public ObjectRepList getObjectRepresentations() { return objectReps; }
    }

    public static class ObjectRepList extends ArrayList<ObjectRep> {

        public ObjectRep getCurrent() { return this.get(this.size()-1); }

        public ObjectRep getFromName(String name) {
            for (ObjectRep rep : this) if(rep.name.equals(name)) return rep;
            return null;
        }

        public int getIndexFromName(String name) {
            int counter = 0;
            for (ObjectRep rep : this) if (rep.name.equals(name)) return counter; else counter++;
            return -1;
        }
    }
}