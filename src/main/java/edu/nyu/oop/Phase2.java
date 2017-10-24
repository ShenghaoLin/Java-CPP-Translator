package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import edu.nyu.oop.ObjectRep.Field;
import edu.nyu.oop.ObjectRep.Constructor;
import edu.nyu.oop.ObjectRep.Method;
import edu.nyu.oop.ObjectRep.Parameter;
import edu.nyu.oop.ObjectRep.VTable;
import edu.nyu.oop.ObjectRep.VMethod;

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
        return buildCppAst(filled);
    }

    public static ObjectRepList getFilledObjectRepList(ObjectRepList unfilled) {

        ObjectRepList filled = new ObjectRepList();

        // Adding java.lang structure manually for Object
        ObjectRep objectRep = new ObjectRep("Object");
        ArrayList<Parameter> forHashCode = new ArrayListM<Parameter>();
        forHashCode.add(new Parameter("Object", "o"));
        Method hashCode = new Method("public", true, "int32_t", "hashCode", forHashCode);
        objectRep.classRep.methods.add(hashCode);
        ArrayList<Parameter> forEquals = new ArrayList<Parameter>();
        forEquals.add(new Parameter("Object", "o"));
        forEquals.add(new Parameter("Object", "o"));
        Method equals = new Method("public", true, "bool", "equals", forEquals);
        objectRep.classRep.methods.add(equals);
        ArrayList<Parameters> forGetClass = new ArrayList<Parameter>();
        forGetClass.add(new Parameter("Object", "o"));
        Method getClass = new Method("public", true, "Class", "getClass", forGetClass);
        objectRep.classRep.methods.add(getClass);
        ArrayList<Parameters> forToString = new ArrayList<Parameter>();
        forToString.add(new Parameter("Object", "o"));
        Method toString = new Method("public", true, "String", "toString", forToString);
        objectRep.classRep.methods.add(toString);
        // class representation for Object filled, now do V-Table filling
        Field v_hashCode = new Field("public", false, "int32_t", "(*hashCode)(Object)");
        objectRep.vtable.fields.add(v_hashCode);
        Field v_equals = new Field("public", false, "bool", "(*equals)(Object, Object)");
        objectRep.vtable.fields.add(v_equals);
        Field v_getClass = new Field("public", false, "Class", "(*getClass)(Object)");
        objectRep.vtable.fields.add(v_getClass);
        Field v_toString = new Field("public", false, "String", "(*toString)(Object)");
        objectRep.vtable.fields.add(v_toString);
        Method v_method_hashCode = new Method("public", false, "", "hashCode", "(__&Object::__hashCode)");
        objectRep.vtable.methods.add(v_method_hashCode);
        Method v_method_equals = new Method("public", false, "", "equals", "(__&Object::__equals)");
        objectRep.vtable.methods.add(v_method_equals);
        Method v_method_getClass = new Method("public", false, "", "getClass", "(__&Object::__getClass)");
        objectRep.vtable.methods.add(v_method_getClass);
        Method v_method_toString = new Method("public", false, "", "toString", "(__&Object::__toString)");
        objectRep.vtable.methods.add(v_method_toString);
        // object is created!
        filled.add(objectRep);

        // Adding java.lang structure manually for String
        ObjectRep classRep = new ObjectRep("Class");
        Field name = new Field("public", false, "String", "name", "");
        classRep.classRep.fields.add(name);
        Field parent = new Field("public", false, "Class", "parent", "");
        classRep.classRep.fields.add(parent);
        ArrayList<Parameter> forClassConstructor = new ArrayList<Parameter>();
        forClassConstructor.add(new Parameter("String", "name"));
        forClassConstructor.add(new Parameter("Class", "parent"));
        Constructor classConstructor = new Constructor("public", "__" + this.name, forClassConstructor);
        classRep.classRep.constructors.remove(0);
        classRep.classRep.constructors.add(classConstructor);
        ArrayList<Parameter> forToStringClass = new ArrayList<Parameter>();
        forToStringClass.add(new Parameter("Class", "c"));
        Method toStringClass = new Method("public", true, "String", "toString", forToStringClass);
        classRep.classRep.add(toStringClass);
        ArrayList<Parameter> forGetNameClass = new ArrayList<Parameter>();
        forGetNameClass.add(new Parameter("Class", "c"));
        Method getNameClass = new Method("public", true, "String", "getName", forGetNameClass);
        classRep.classRep.add(getNameClass);
        ArrayList<Parameter> forGetSuperClass = new ArrayList<Parameter>();
        forGetSuperClass.add(new Parameter("Class", "c"));
        Method getSuperClass = new Method("public", true, "Class", "getSuperclass", forGetSuperClass);
        classRep.classRep.add(getSuperClass);
        ArrayList<Parameter> forIsInstanceClass = new ArrayList<Parameter>();
        forIsInstanceClass.add(new Parameter("Class", "c"));
        forIsInstanceClass.add(new Parameter("Object", "o"));
        Method isInstanceClass = new Method("public", true, "bool", "isInstance", forIsInstanceClass);
        classRep.classRep.add(isInstanceClass);
        // class representation for Class filled, now do V-Table filling
        v_hashCode = new Field("public", false, "int32_t", "(*hashCode)(Class)");
        classRep.vtable.fields.add(v_hashCode);
        v_equals = new Field("public", false, "bool", "(*equals)(Class, Object)");
        classRep.vtable.fields.add(v_equals);
        v_getClass = new Field("public", false, "Class", "(*getClass)(Class)");
        classRep.vtable.fields.add(v_getClass);
        v_toString = new Field("public", false, "String", "(*toString)(Class)");
        classRep.vtable.fields.add(v_toString);
        v_getName = new Field("public", false, "String", "(*getName)(Class)");
        classRep.vtable.fields.add(v_getName);
        v_getSuperClass = new Field("public", false, "String", "(*getSuperclass)(Class)");
        classRep.vtable.fields.add(v_getSuperClass);
        v_isInstance = new Field("public", false, "String", "(*isInstance)(Class)");
        classRep.vtable.fields.add(v_isInstance);
        v_method_hashCode = new Method("public", false, "", "hashCode((int32_t(*)(Class))", "__&Object::__hashCode)");
        classRep.vtable.methods.add(v_method_hashCode);
        v_method_equals = new Method("public", false, "", "equals((bool(*)(Class,Object))", "__&Object::__equals)");
        classRep.vtable.methods.add(v_method_equals);
        v_method_getClass = new Method("public", false, "", "getClass((Class(*)(Class))", "__&Object::__getClass)");
        classRep.vtable.methods.add(v_method_getClass);
        v_method_toString = new Method("public", false, "", "toString", "__&Class::__toString)");
        classRep.vtable.methods.add(v_method_toString);
        Method v_method_getName = new Method("public", false, "", "getName", "__&Class::__getName)");
        classRep.vtable.methods.add(v_method_getName);
        Method v_method_getSuperClass = new Method("public", false, "", "getSuperclass", "(__&Class::__getSuperClass)");
        classRep.vtable.methods.add(v_method_getSuperClass);
        Method v_method_isInstance = new Method("public", false, "", "isInstance", "(__&Class::__isInstance)");
        classRep.vtable.methods.add(v_method_isInstance);





        
        objectRep.addMethod("Public", true, "int", "hashCode", new ArrayList<Parameter>());
        ArrayList<Parameter> equalsParameters = new ArrayList<Parameter>();
        equalsParameters.add(new Parameter("Object", "o"));
        objectRep.addMethod("Public", true, "boolean", "equals", equalsParameters);
        objectRep.addMethod("Public", true, "Class", "getClass", new ArrayList<Parameter>());
        objectRep.addMethod("Public", true, "String", "toString", new ArrayList<Parameter>());
        filled.add(objectRep);
        ObjectRep stringRep = new ObjectRep("String");
        stringRep.addMethod("Public", true, "int", "length", new ArrayList<Parameter>());
        ArrayList<Parameter> charAtParameters = new ArrayList<Parameter>();
        charAtParameters.add(new Parameter("int", "i"));
        stringRep.addMethod("Public", true, "char", "charAt", charAtParameters);
        filled.add(stringRep);
        ObjectRep classRep = new ObjectRep("Class");
        classRep.addMethod("Public", true, "String", "getName", new ArrayList<Parameter>());
        classRep.addMethod("Public", true, "Class", "getSuperclass", new ArrayList<Parameter>());
        ArrayList<Parameter> isInstanceParameters = new ArrayList<Parameter>();
        isInstanceParameters.add(new Parameter("Object", "o"));
        classRep.addMethod("Public", true, "boolean", "isInstance", isInstanceParameters);
        filled.add(classRep);

        //Add classes from unfilled
        int finished = 0;
        while (finished != unfilled.size()) {
            for (ObjectRep rep : unfilled) {
                if (!filled.contains(rep)) {
                    //Don't add until parent class is already in filled, makes sure classes are in descending inheritance order, needed for inheritance
                    if (rep.getParentName() == null | filled.getFromName(rep.getParentName()) != null) {
                        filled.add(rep);
                        finished += 1;
                    }
                }
            }
        }

        //Iterate through class representations and build vtables/inherited fields
        for(ObjectRep rep : filled) {
            //Add class methods to the vtable
            for (Method method : rep.methods)
                rep.addVMethod(rep.getName(), method);

            //Skip Object, it has no parent
            if (rep.getName().equals("Object"))
                continue;

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

        return filled;
    }

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

    public static class Phase2Visitor extends Visitor {
        private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        private ObjectRepList objectRepresentations = new ObjectRepList();

        public void visitCompilationNode(GNode n) {
            visit(n);
        }

        public void visitClassDeclaration(GNode n) {
            objectRepresentations.add(new ObjectRep(n.getString(1)));
            visit(n);
        }

        public void visitExtension(GNode n) {
            objectRepresentations.getCurrent().setParentName(n.getNode(0).getNode(0).getString(0));
            visit(n);
        }

        public void visitConstructorDeclaration(GNode n) {
            //Modifiers
            String accessModifier = "";
            boolean isStatic = false;
            Iterator modifierIter = n.getNode(0).iterator();
            while(modifierIter.hasNext()) {
                Node modifierNode = (Node) modifierIter.next();
                if(modifierNode.getString(0).equals("static"))
                    isStatic = true;
                else
                    accessModifier = modifierNode.getString(0);
            }

            //Name
            String name = n.getString(2);

            //Parameters
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();
            Iterator parametersIter = n.getNode(4).iterator();
            while(parametersIter.hasNext()) {
                Node parameterNode = (Node) parametersIter.next();
                String parameterType = parameterNode.getNode(1).getNode(0).getString(0);
                String parameterName = parameterNode.getString(3);
                parameters.add(new Parameter(parameterType, parameterName));
            }

            visit(n);
        }

        public void visitMethodDeclaration(GNode n) {
            //Modifiers
            String accessModifier = "";
            boolean isStatic = false;
            Iterator modifierIter = n.getNode(0).iterator();
            while(modifierIter.hasNext()) {
                Node modifierNode = (Node) modifierIter.next();
                if(modifierNode.getString(0).equals("static"))
                    isStatic = true;
                else
                    accessModifier = modifierNode.getString(0);
            }

            //Return type
            String returnType;
            Node returnNode = n.getNode(2);
            if(returnNode.getName().equals("VoidType"))
                returnType = "void";
            else
                returnType = returnNode.getNode(0).getString(0);

            //Method name
            String methodName = n.getString(3);

            //Parameters
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();
            Iterator parametersIter = n.getNode(4).iterator();
            while(parametersIter.hasNext()) {
                Node parameterNode = (Node) parametersIter.next();
                String parameterType = parameterNode.getNode(1).getNode(0).getString(0);
                String parameterName = parameterNode.getString(3);
                parameters.add(new Parameter(parameterType, parameterName));
            }

            objectRepresentations.getCurrent().addMethod(accessModifier, isStatic, returnType, methodName, parameters);
            visit(n);
        }

        public void visitFieldDeclaration(GNode n) {
            //Modifiers
            Iterator modifierIter = n.getNode(0).iterator();
            boolean isStatic = false;
            String accessModifier = "";
            while(modifierIter.hasNext()) {
                Node modifierNode = (Node) modifierIter.next();
                if(modifierNode.getString(0).equals("static"))
                    isStatic = true;
                else
                    accessModifier = modifierNode.getString(0);
            }

            //Type
            String fieldType = n.getNode(1).getNode(0).getString(0);

            //Name
            String fieldName = n.getNode(2).getNode(0).getString(0);

            //Initialization
            String initial = null;
            Node initialNode = n.getNode(2).getNode(0).getNode(2);
            if(initialNode != null)
                initial = initialNode.getString(0);

            objectRepresentations.getCurrent().addField(accessModifier, isStatic, fieldType, fieldName, initial);
            visit(n);
        }

        public void traverse(Node n) {
            super.dispatch(n);
        }

        public void visit(Node n) {
            for (Object o : n) if (o instanceof Node) dispatch((Node) o);
        }

        public ObjectRepList getObjectRepresentations() {
            return objectRepresentations;
        }
    }

    public static class ObjectRepList extends ArrayList<ObjectRep> {
        public ObjectRep getCurrent() { return this.get(this.size()-1); }

        public ObjectRep getFromName(String name) {
            for(ObjectRep rep : this) {
                if(rep.getName().equals(name))
                    return rep;
            }
            return null;
        }
    }
}
