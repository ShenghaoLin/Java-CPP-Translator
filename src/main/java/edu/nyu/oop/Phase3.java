package edu.nyu.oop;

import java.io.*;
import java.util.*;
import xtc.tree.*;
import edu.nyu.oop.util.XtcProps;
import org.slf4j.Logger;



/** Print out the information in the Inheritance AST in a concrete C++ syntax */

public class Phase3 extends Visitor {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private  Printer printer;

    private String outputLocation = XtcProps.get("output.location");

    String className;

    public Phase3() {
        Writer w = null;
        try {
            FileOutputStream fos = new FileOutputStream(outputLocation + "/output.h");
            OutputStreamWriter ows = new OutputStreamWriter(fos, "utf-8");
            w = new BufferedWriter(ows);
            this.printer = new Printer(w);
        } catch (Exception e) {
            throw new RuntimeException("Output location not found. Create the /output directory.");
        }

        printer.register(this);
    }

    public void print(GNode ast) {
        headOfFile();
        dispatch(ast);
        printer.flush();
    }

    private void headOfFile() {
        printer.pln("#pragma once");
        printer.pln();
        printer.pln("#include \"java_lang.h\"");
        printer.pln();
        printer.pln("using namespace java::lang;");
        printer.pln();
    }


    static String packageName = null;

    public void visitPackageDeclaration(GNode node){
        
        /*

        for(int i =0; i < n.getNode(0).size(); i++){
            packageName = n.getNode(0).getString(i); //null if no package
            if (packageName != null){
                printer.pln("namespace " + packageName + " {");
                printer.incr().indent();
            }
        }
        printer.pln();
        visit(n);
        for (int i=0; i<n.getNode(0).size(); i++){
            printer.decr().indent().pln("}"); //i times
        }
        */
        printer.pln("namespace " + node.getString(0) + " {");
        printer.pln();
        visit(node);
    }

    public void visitForwardDeclarations(GNode node) {

        for (int i = 0; i < node.size(); i++){
            printer.indent().pln("struct __" + node.getString(i) + ";");
            printer.indent().pln("struct __" + node.getString(i) + "_VT;");
            printer.pln();
        }
        for (int j = 0; j < node.size(); j++){
            printer.pln("typedef __" + node.getString(j) + "* " + node.getString(j) + ";");
        }
        printer.pln();
        visit(node);
    }

    public void visitClassDeclaration(GNode node) { 

        className = node.getNode(0).getString(0);
        printer.pln("struct __" + className + " {");
        printer.pln();
        visit(node);
    }

    public void visitFieldDeclarations(GNode node) {

        for (int i = 0; i < node.size(); i++) {
            if (node.getNode(i).getNode(0).getString(0).equals("false")) {
                printer.indent().pln("" + node.getNode(i).getNode(1).getString(0) + " " + node.getNode(i).getNode(2).getString(0) + " " + node.getNode(i).getNode(3).getString(0) + ";");
            }
            else {
                printer.indent().pln("static " + node.getNode(i).getNode(1).getString(0) + " " + node.getNode(i).getNode(2).getString(0) + " " + node.getNode(i).getNode(3).getString(0) + ";");
            }
            
        }
        printer.pln();
        visit(node);
    }

    public void visitConstructorDeclarations(GNode node) {

        for (int i = 0; i < node.size(); i++) {
            printer.indent().p(" __" + node.getNode(i).getNode(0).getString(0) + "(");
            if (node.getNode(i).getNode(1).size() > 0) {
                int lastIndex = node.getNode(i).getNode(1).size() - 1;
                for (int j = 0; j < node.getNode(i).size(); i++) {
                    if (j == lastIndex) printer.indent().p(node.getNode(i).getNode(1).getNode(j).getNode(0).getString(0) + " " + node.getNode(i).getNode(1).getNode(j).getNode(1).getString(0));
                    else printer.indent().p(node.getNode(i).getNode(1).getNode(j).getNode(0).getString(0) + " " + node.getNode(i).getNode(1).getNode(j).getNode(1).getString(0) + ", ");
                }
            }
            printer.pln(");");
        }
        printer.pln();
        visit(node);
    }

    public void visitMethodDeclarations(GNode node) {

        for(int i = 0; i < node.size(); i++){
            if (node.getNode(i).getNode(0).getString(0).equals("false")) {
                printer.indent().p("" + node.getNode(i).getNode(1).getString(0) + " " + node.getNode(i).getNode(2).getString(0) + "(");
            }
            else {
                printer.indent().p("static " + node.getNode(i).getNode(1).getString(0) + " " + node.getNode(i).getNode(2).getString(0) + "(");
            }
            if (node.getNode(i).getNode(3).size() > 0) {
                for (int j = 0; j < node.getNode(i).getNode(3).size(); j++) {
                    if (j == 0 && node.size() == 1) printer.p(node.getNode(i).getNode(3).getNode(j).getNode(0).getString(0));
                    else if (j == node.getNode(i).getNode(3).size() - 1) printer.p(node.getNode(i).getNode(3).getNode(j).getNode(0).getString(0) + " " + node.getNode(i).getNode(3).getNode(j).getNode(1).getString(0));
                    else printer.indent().p(node.getNode(i).getNode(3).getNode(j).getNode(0).getString(0) + " " + node.getNode(i).getNode(3).getNode(j).getNode(1).getString(0) + ", ");
                }
            }
            printer.pln(");");
        }
        printer.pln();
        printer.pln("};");
        printer.pln();
        visit(node);
    }

    public void visitVTableLayout(GNode node) {
        printer.pln("struct __" + className + "_VT {");
        printer.pln();
        visit(node);
        printer.pln("};");
        printer.pln("};");
    }

    public void visitVFields(GNode node){
        for(int i = 0; i < node.size(); i++) {
            if (i == 0) {
                printer.pln(node.getNode(i).getNode(0).getString(0) + " " + node.getNode(i).getNode(1).getString(0) + ";");
                printer.pln();
            }
            else printer.pln(node.getNode(i).getNode(0).getString(0) + " (" + node.getNode(i).getNode(1).getString(0) + ")(" + node.getNode(i).getNode(2).getString(0) + ");");
        }
        printer.pln();
        visit(node);
    }

    public void visitVMethods(GNode node){
        printer.pln("__" + className + "_VT()");
        for (int i = 0; i < node.size(); i++) {
            if (i == 0) printer.pln(":" + node.getNode(i).getNode(0).getString(0) + node.getNode(i).getNode(1).getString(0) + ",");
            else if (i == node.size() - 1) {
                printer.pln(node.getNode(i).getNode(0).getString(0) + node.getNode(i).getNode(1).getString(0));
                printer.pln("{");
                printer.pln("}");
            }
            else printer.pln(node.getNode(i).getNode(0).getString(0) + node.getNode(i).getNode(1).getString(0) + ",");
        }
        
        visit(node);

    }

    public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
    }
}