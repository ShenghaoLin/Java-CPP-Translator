package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Phase2Visitor extends Visitor {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());
    private ClassRepList classRepresentations = new ClassRepList();

    public void visitCompilationNode(GNode n) {
        visit(n);
    }

    public void visitClassDeclaration(GNode n) {
        String name = n.getString(1);
        classRepresentations.put(name, new ClassRep(name));
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
        // Get information from the tree
        super.dispatch(n);

        // Iterate through class representations and get inherited methods
        for (ClassRep classRep : classRepresentations.values()) {
            if (classRep.getName().equals("Object"))
                continue;
            if (classRep.getParentName() == null)
                classRep.setParentName("Object");
            for (String variableName : classRepresentations.get(classRep.getParentName()).getVariableNames())
                classRep.addVariableName(variableName);
            for (String methodName : classRepresentations.get(classRep.getParentName()).getMethodNames())
                classRep.addMethodName(methodName);
        }
    }

    public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
    }

    public void printClassRepresentations() {
        for (ClassRep cr : classRepresentations.values()) {
            System.out.println("Class " + cr.getName());
            System.out.println("    Inherits: " + cr.getParentName());
            System.out.println("    Variables:");
            for (String variableName : cr.getVariableNames()) {
                System.out.println("        " + variableName);
            }
            System.out.println("    Methods:");
            for (String methodName : cr.getMethodNames()) {
                System.out.println("        " + methodName);
            }
        }
    }

    public class ClassRep {
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

    public class ClassRepList extends LinkedHashMap<String, ClassRep> {
        private ClassRep current;

        public ClassRepList() {
            this.current = null;
            // Adding java.lang classes manually
            ClassRep objectRep = new ClassRep("Object");
            objectRep.addMethodName("hashCode");
            objectRep.addMethodName("equals");
            objectRep.addMethodName("getClass");
            objectRep.addMethodName("toString");
            this.put("Object", objectRep);
            ClassRep stringRep = new ClassRep("String");
            stringRep.addMethodName("length");
            stringRep.addMethodName("charAt");
            this.put("String", stringRep);
            ClassRep classRep = new ClassRep("Class");
            classRep.addMethodName("getName");
            classRep.addMethodName("getSuperclass");
            classRep.addMethodName("isInstance");
            this.put("Class", classRep);
        }

        public ClassRep put(String name, ClassRep rep) {
            this.current = rep;
            return super.put(name, rep);
        }

        public ClassRep getCurrent() { return this.current; }
    }
}
