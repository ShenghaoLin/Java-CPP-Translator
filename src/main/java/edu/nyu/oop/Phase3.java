/**
 * Phase 3, traverses C++ AST obtained from Phase 2 and prints information
 * contained in nodes into output.h file, furhtermore achieves indentation
 * and pretty formatting, methods haven't been annotated with JavaDoc as
 * they are pretty self explanatory
 * Phase3 constructor initializes Phase3 and the printer used for printing
 * visitABC methods visit relevant nodes (e.g. ABC node will be visited)
 * headOfFile() and endOfFile() does some simple pre and post processing
 * print method flushes output of printer to file
 *
 * @author Sai Akhil
 *
 * @version 1.0
 */

package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;

import edu.nyu.oop.util.XtcProps;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.util.ArrayList;

/** Print out the information in the Inheritance AST in a concrete C++ syntax */

public class Phase3 extends Visitor {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private  Printer printer;

    private String outputLocation = XtcProps.get("output.location");

    String className;

    // To test methods
    String header;
    String endof;
    String packageDeclaration;
    ArrayList<String> classes = new ArrayList<String>();
    String datalayout;
    ArrayList<String> fieldDeclarations = new ArrayList<String>();
    ArrayList<String> constructors = new ArrayList<String>();
    String forwardDeclarations = "";
    ArrayList<String> methods = new ArrayList<String>();

    public Phase3() {

        Writer w = null;
        try {
            FileOutputStream fos = new FileOutputStream(outputLocation + "output.h");
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
        endOfFile();
        printer.flush();
    }

    private void headOfFile() {
        header = "#pragma once\n\n#include <stdint.h>\n#include <string>";
        printer.pln("#pragma once");
        printer.pln();
        printer.pln("#include <stdint.h>");
        printer.pln("#include <string>");
        printer.pln().pln();
    }

    private void endOfFile() {
        endof = "}\n}";
        printer.pln("}");
        printer.pln("}");
    }

    //String packageName = null;
    public void visitPackageDeclaration(GNode node){
        packageDeclaration = "namespace " + node.getString(0) + "{\nnamespace javalang {";
        printer.pln("namespace " + node.getString(0) + "{");
        printer.pln("namespace javalang {");
        printer.pln();
        visit(node);
    }

    public void visitForwardDeclarations(GNode node){
        printer.incr();
        for (int i = 0; i < node.size(); i++) {
            printer.indent().pln("struct __" + node.getString(i) + ";");
            printer.indent().pln("struct __" + node.getString(i) + "_VT;");
            printer.pln();
            forwardDeclarations+="struct __" + node.getString(i) + ";\nstruct __"+ node.getString(i) + "_VT;\n";
        }
        printer.pln();
        for (int i = 0; i < node.size(); i++) {
            printer.indent().pln("typedef __" + node.getString(i) + "* " + node.getString(i)+";");
            forwardDeclarations+="typedef __" + node.getString(i) + "* " + node.getString(i)+";\n";
        }
        printer.pln().pln();
        visit(node);
    }

    public void visitClassDeclaration(GNode node){
        className = node.getString(0);
        classes.add(className);
        visit(node);
    }

    public void visitDataLayout(GNode node){
        datalayout = "struct __" + className+ " {";
        printer.indent().pln("struct __" + className+ " {");
        printer.pln();
        visit(node);
        printer.decr().indent().pln("};");
        printer.pln();

    }

    String modifier2;
    String VTName;
    public void visitFieldDeclarations(GNode node){
        printer.incr();
        for (int i = 0; i < node.size(); i++) {
            VTName = "__" + className + "_VT";
            if(node.getNode(i).getNode(0).getString(0).equals("true")) modifier2 = "static ";
            else modifier2 = "";
            if (node.getNode(i).getNode(1).getString(0).equals(VTName) && node.getNode(i).getNode(2).getString(0).equals("__vtable")) continue;
            printer.indent().pln(modifier2 + node.getNode(i).getNode(1).getString(0) + " " + node.getNode(i).getNode(2).getString(0) + ";");
            fieldDeclarations.add(modifier2 + node.getNode(i).getNode(1).getString(0) + " " + node.getNode(i).getNode(2).getString(0) + ";");
        }
        printer.pln();
        visit(node);
    }


    public void visitConstructorDeclarations(GNode node){
        for(int i = 0; i < node.size(); i++) {
            printer.indent().p("static __" + node.getNode(i).getNode(0).getString(0) + "(");
            printParameters((GNode)node.getNode(i).getNode(1));
            printer.pln(");");
            constructors.add("static __" + node.getNode(i).getNode(0).getString(0) + "("+getParameters((GNode)node.getNode(i).getNode(1))+");");
        }
        printer.pln();
        visit(node);
    }

    String modifier;
    public void visitMethodDeclarations(GNode node){
        for(int i = 0; i < node.size(); i++) {
            if(node.getNode(i).getNode(0).getString(0).equals("true")) modifier = "static ";
            else modifier = "";
            if (i != 0) {
                printer.indent().p(modifier + node.getNode(i).getNode(1).getString(0) + " " + node.getNode(i).getNode(2).getString(0) + "(");
                printParameters((GNode)node.getNode(i).getNode(3));
                printer.pln(");");
                methods.add(modifier + node.getNode(i).getNode(1).getString(0) + " " + node.getNode(i).getNode(2).getString(0) + "("+getParameters((GNode)node.getNode(i).getNode(3))+")");
            }
        }
        printer.pln();
        printer.indent().pln("static Class __class();");
        printer.pln();
        visit(node);
    }

    public void visitVFieldDec(GNode node){
        printer.indent().pln("static __" + className + "_VT __vtable;");
        visit(node);
    }

    public void visitVTableLayout(GNode node){
        printer.indent().pln("struct __" + node.getString(0) + "_VT {");
        printer.pln();
        printer.incr().indent().pln("Class __is_a;");
        printer.pln();
        visit(node);
        printer.decr().indent().pln("{");
        printer.indent().pln("}");
        printer.decr().indent().pln("};");
        printer.pln();
    }

    public void visitVFields(GNode node){
        for(int i = 1; i < node.size(); i++){
            printer.indent().pln(node.getNode(i).getNode(0).getString(0) + " (" + node.getNode(i).getNode(1).getString(0) +
                    ")(" + node.getNode(i).getNode(2).getString(0) + ");");
        }
        printer.pln();
    }


    public void visitVMethods(GNode node){
        printer.indent().pln("__" + className + "_VT()");
        printer.indent().pln(": __is_a(__" + className + "::__class()),");
        printer.incr();
        for(int i = 0; i < node.size(); i++) {
            if (!node.getNode(i).getNode(0).getString(0).equals("__is_a") && !node.getNode(i).getNode(1).getString(0).equals("(__" + className + "::__class())")) {
                printer.indent().pln(node.getNode(i).getNode(0).getString(0) + node.getNode(i).getNode(1).getString(0) + ",");
            }
        }
        visit(node);
    }

    public void printParameters(GNode node){
        for (int i = 0; i < node.size(); i++) {
            printer.p(node.getNode(i).getNode(0).getString(0) + " " + node.getNode(i).getNode(1).getString(0));
            if (i != (node.size()-1)) printer.p(", ");
        }
    }

    public String getParameters(GNode node){
        String ret = "";
        for(int i = 0; i < node.size(); i++){
            ret += node.getNode(i).getNode(0).getString(0) + " " + node.getNode(i).getNode(1).getString(0);
            if (i != (node.size()-1)) ret+=", ";
        }
        return ret;
    }

    public void visit(Node node) {
        for (Object o : node) if (o instanceof Node) dispatch((Node) o);
    }

}
