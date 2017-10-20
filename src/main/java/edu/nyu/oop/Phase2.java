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
        ClassRepList unfilled = visitor.getClassRepresentations();
        ClassRepList filled = getFilledClassRepList(unfilled);
        //Build C++ AST from class representations
        return buildCppAst(filled);
    }

    public static ClassRepList getFilledClassRepList(ClassRepList unfilled) {
        ClassRepList filled = new ClassRepList();

        //Adding java.lang classes manually
        ClassRep objectRep = new ClassRep("Object");
        objectRep.addMethodName("hashCode");
        objectRep.addMethodName("equals");
        objectRep.addMethodName("getClass");
        objectRep.addMethodName("toString");
        filled.add(objectRep);
        ClassRep stringRep = new ClassRep("String");
        stringRep.addMethodName("length");
        stringRep.addMethodName("charAt");
        filled.add(stringRep);
        ClassRep classRep = new ClassRep("Class");
        classRep.addMethodName("getName");
        classRep.addMethodName("getSuperclass");
        classRep.addMethodName("isInstance");
        filled.add(classRep);

        //Add classes from unfilled
        int finished = 0;
        while (finished != unfilled.size()) {
            for (ClassRep cr : unfilled) {
                if (!filled.contains(cr)) {
                    //Don't add until parent class is already in filled, makes sure classes are in descending inheritance order
                    if (cr.getParentName() == null | filled.getFromName(cr.getParentName()) != null) {
                        filled.add(cr);
                        finished += 1;
                    }
                }
            }
        }

        //Iterate through class representations and get inherited methods
        for(ClassRep cr : filled) {
            //Skip Object, it has no parent
            if (cr.getName().equals("Object"))
                continue;

            //Skip class with main method
            if (cr.getMethodNames().contains("main"))
                continue;

            //If no other parent, set parent to Object
            if (cr.getParentName() == null)
                cr.setParentName("Object");

            //Inherit un-overridden methods and variables
            ClassRep parent = filled.getFromName(cr.getParentName());
            for (String methodName : parent.getMethodNames()) {
                if (!cr.getMethodNames().contains(methodName))
                    cr.addMethodName(methodName);
            }
            for (String variableName : parent.getVariableNames()) {
                if (!cr.getVariableNames().contains(variableName))
                    cr.addVariableName(variableName);
            }
        }

        return filled;
    }

    public static Node buildCppAst(ClassRepList classRepList) {
        Node root = GNode.create("CompilationUnit");

        //STILL NEED TO REAL PACKAGE INFO
        Node packageDeclaration = GNode.create("PackageDeclaration");
        Node packageName = GNode.create("?????");
        packageDeclaration.add(packageName);
        root.add(packageDeclaration);

        for (ClassRep cr : classRepList)
            root.add(buildClassNode(cr));

        return root;
    }

    public static Node buildClassNode(ClassRep classRep) {
        //Create root
        Node root = GNode.create("ClassDeclaration");

        //Name
        Node className = GNode.create(classRep.getName());
        root.add(className);

        //Methods
        for(String method : classRep.getMethodNames()) {
            Node methodDeclaration = GNode.create("MethodDeclaration");
            Node methodName = GNode.create(method);
            methodDeclaration.add(methodName);
            root.add(methodDeclaration);
        }

        //Variables
        for(String variable : classRep.getVariableNames()) {
            Node declarator = GNode.create("Declarator");
            Node variableName = GNode.create(variable);
            declarator.add(variableName);
            root.add(declarator);
        }

        return root;
    }

    public static class Phase2Visitor extends Visitor {
        private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
        private ClassRepList classRepresentations = new ClassRepList();

        public void visitCompilationNode(GNode n) {
            visit(n);
        }

        public void visitClassDeclaration(GNode n) {
            classRepresentations.add(new ClassRep(n.getString(1)));
            visit(n);
        }

        public void visitExtension(GNode n) {
            classRepresentations.getCurrent().setParentName(n.getNode(0).getNode(0).getString(0));
            visit(n);
        }

        public void visitMethodDeclaration(GNode n) {
            classRepresentations.getCurrent().addMethodName(n.getString(3));
            visit(n);
        }

        public void visitDeclarator(GNode n) {
            classRepresentations.getCurrent().addVariableName(n.getString(0));
            visit(n);
        }

        public void traverse(Node n) {
            super.dispatch(n);
        }

        public void visit(Node n) {
            for (Object o : n) if (o instanceof Node) dispatch((Node) o);
        }

        public ClassRepList getClassRepresentations() {
            return classRepresentations;
        }
    }

    public static class ClassRep {
        private String name;
        private String parentName; // Name of the most immediately inherited class
        private ArrayList<String> variableNames; // List of all variable names
        private ArrayList<String> methodNames; // List of all method names

        public ClassRep(String name){
            this.name = name;
            this.parentName = null;
            this.variableNames = new ArrayList<String>();
            this.methodNames = new ArrayList<String>();
        }

        public String getName() { return this.name; }

        public void setName(String name) { this.name = name; }

        public String getParentName() { return this.parentName; }

        public void setParentName(String parentName){
            this.parentName = parentName;
        }

        public ArrayList<String> getVariableNames() {
            return variableNames;
        }

        public void addVariableName(String variableName) {
            this.variableNames.add(variableName);
        }

        public ArrayList<String> getMethodNames() { return this.methodNames; }

        public void addMethodName(String methodName) {
            // PREVENTS OVERLOADED METHODS FOR NOW -- WILL HAVE TO CHANGE THIS LATER -- METHOD REP CLASS?
            if (!this.methodNames.contains(methodName))
                this.methodNames.add(methodName);
        }

    }

    public static class ClassRepList extends ArrayList<ClassRep> {
        public ClassRep getCurrent() { return this.get(this.size()-1); }

        public ClassRep getFromName(String name) {
            for(ClassRep cr : this) {
                if(cr.getName().equals(name))
                    return cr;
            }
            return null;
        }
    }
}
