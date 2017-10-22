package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;

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

        //Adding java.lang classes manually
        ObjectRep objectRep = new ObjectRep("Object");
        //TODO: MANUALLY BUILD OBJECT HERE
        filled.add(objectRep);
        ObjectRep stringRep = new ObjectRep("String");
        //TODO: MANUALLY BUILD STRING HERE
        filled.add(stringRep);
        ObjectRep classRep = new ObjectRep("Class");
        //TODO: MANUALLY BUILD CLASS HERE
        filled.add(classRep);

        //Add classes from unfilled
        int finished = 0;
        while (finished != unfilled.size()) {
            for (ObjectRep rep : unfilled) {
                if (!filled.contains(rep)) {
                    //Don't add until parent class is already in filled, makes sure classes are in descending inheritance order
                    if (rep.getParentName() == null | filled.getFromName(rep.getParentName()) != null) {
                        filled.add(rep);
                        finished += 1;
                    }
                }
            }
        }

        //Iterate through class representations and get inherited methods
        for(ObjectRep rep : filled) {
            //Skip Object, it has no parent
            if (rep.getName().equals("Object"))
                continue;

            //Skip class with main method
            boolean main = false;
            for (edu.nyu.oop.ObjectRep.Method method : rep.methods) {
                if(method.method_name.equals("main"))
                    continue;
            }

            //If no other parent, set parent to Object
            if (rep.getParentName() == null)
                rep.setParentName("Object");

            //TODO: INHERITANCE
            ObjectRep parent = filled.getFromName(rep.getParentName());
            //for (String methodName : parent.getMethodNames()) {
            //    if (!rep.getMethodNames().contains(methodName))
            //        rep.addMethodName(methodName);
            //}
            //for (String variableName : parent.getVariableNames()) {
            //    if (!rep.getVariableNames().contains(variableName))
            //        rep.addVariableName(variableName);
            //}
        }

        return filled;
    }

    public static Node buildCppAst(ObjectRepList ObjectRepList) {
        Node root = GNode.create("CompilationUnit");

        //STILL NEED TO GET REAL PACKAGE INFO
        Node packageDeclaration = GNode.create("PackageDeclaration");
        Node packageName = GNode.create("?????");
        packageDeclaration.add(packageName);
        root.add(packageDeclaration);

        for (ObjectRep rep : ObjectRepList)
            root.add(buildClassNode(rep));

        return root;
    }

    public static Node buildClassNode(ObjectRep rep) {
        //Root
        Node root = GNode.create("ClassDeclaration");

        //Name
        Node className = GNode.create("Class Name", rep.getName());
        root.add(className);

        //Constructors
        //TODO: Build constructor node

        //Methods
        for(edu.nyu.oop.ObjectRep.Method method : rep.methods) {
            Node methodDeclaration = GNode.create("MethodDeclaration");

            Node accessModifier = GNode.create("Access Modifier", method.access_modifier);
            methodDeclaration.add(accessModifier);

            Node isStatic = GNode.create("Is Static", method.is_static);
            methodDeclaration.add(isStatic);

            Node name = GNode.create("Method Name", method.method_name);
            methodDeclaration.add(name);

            Node returnType = GNode.create("Return Type", method.return_type);
            methodDeclaration.add(returnType);

            Node parameterTypes = GNode.create("Parameter Types", method.parameter_types);
            methodDeclaration.add(parameterTypes);

            Node parameterNames = GNode.create("Parameter Names", method.parameter_names);
            methodDeclaration.add(parameterNames);

            root.add(methodDeclaration);
        }

        //Fields
        for(ObjectRep.Field field : rep.fields) {
            Node fieldDeclaration = GNode.create("Field");

            Node accessModifier = GNode.create("Access Modifier", field.access_modifier);
            fieldDeclaration.add(accessModifier);

            Node isStatic = GNode.create("Is Static", field.is_static);
            fieldDeclaration.add(isStatic);

            Node fieldType = GNode.create("Field Type", field.field_type);
            fieldDeclaration.add(fieldType);

            Node fieldName = GNode.create("Field Name", field.field_name);
            fieldDeclaration.add(fieldName);

            Node isInitialized = GNode.create("Is Initialized", field.is_initialized);
            fieldDeclaration.add(isInitialized);

            Node initial = GNode.create("Initial", field.initial);
            fieldDeclaration.add(initial);

            root.add(fieldDeclaration);
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
            //TODO: Visit constructor and call objectRepresentations.getCurrent().addConstructor()
            visit(n);
        }

        public void visitMethodDeclaration(GNode n) {
            //TODO: SEE NOT SURES
            String accessModifier = n.getNode(0).getNode(0).getString(0);
            boolean isStatic = false; //NOT SURE
            String returnType = ""; //NOT SURE
            String methodName = n.getString(3);
            ArrayList<String> parameterTypes = new ArrayList<String>(); //NOT SURE
            ArrayList<String> parameterNames = new ArrayList<String>(); //NOT SURE
            objectRepresentations.getCurrent().addMethod(accessModifier, isStatic, returnType, methodName, parameterTypes, parameterNames);
            visit(n);
        }

        public void visitFieldDeclaration(GNode n) {
            //TODO: SEE NOT SURES
            String accessModifier = ""; //NOT SURE
            boolean isStatic = false;
            String fieldType = n.getNode(1).getStringProperty("QualifiedIdentifier");
            String fieldName = n.getNode(2).getNode(0).getString(0);
            boolean isInitialized = false; //NOT SURE
            String initial = ""; //NOT SURE
            objectRepresentations.getCurrent().addField(accessModifier, isStatic, fieldType, fieldName, isInitialized, initial);
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
