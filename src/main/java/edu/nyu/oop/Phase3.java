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
        printer.flush();
    }



    private void headOfFile() {
        printer.pln("#pragma once");
        printer.pln();
        printer.pln("#include <stdint.h>");
        printer.pln("#include <string>");
        printer.pln().pln();
    }


    //String packageName = null;

    public void visitPackageDeclaration(GNode n){

        printer.pln("namespace " + n.getString(0)+ "{");
        visit(n);


    }


    public void visitForwardDeclarations(GNode n){
        printer.incr();
        for (int i=0; i<n.size(); i++){
            printer.indent().pln("struct __"+n.getString(i)+";");
            printer.indent().pln("struct __"+n.getString(i)+"_VT;");
            printer.pln();
        }
        printer.pln();
        for (int i=0; i<n.size(); i++){
            printer.indent().pln("typedef __"+n.getString(i)+"* "+n.getString(i)+";");
        }
        printer.pln().pln();
        visit(n);

    }


    public void visitClassDeclaration(GNode n){
        className = n.getString(0);
        visit(n);
    }



    public void visitDataLayout(GNode n){

        printer.indent().pln("struct __" + className+ " {");
        printer.pln();
        visit(n);
        printer.decr().indent().pln("};");
        printer.pln().pln().pln();

    }

    String modifier2;
    String VT_name;
    public void visitFieldDeclarations(GNode n){

        printer.incr();
        for (int x = 0; x<n.size(); x++){
            VT_name = "__"+className+"_VT";
            if(n.getNode(x).getNode(0).getString(0).equals("true")){

                modifier2 = "static ";

            }
            else {
                modifier2 = "";
            }
            if(n.getNode(x).getNode(1).getString(0).equals(VT_name) && n.getNode(x).getNode(2).getString(0).equals("__vtable")){
                continue;
            }
            printer.indent().pln(modifier2+n.getNode(x).getNode(1).getString(0)+" "+n.getNode(x).getNode(2).getString(0)+";");


        }
        printer.pln();
        visit(n);


    }


    public void visitConstructorDeclarations(GNode n){

        for(int x = 0; x<n.size(); x++ ){
            printer.indent().p("__" + className + "(");
            printParameters((GNode)n.getNode(x).getNode(1)); //NOT SURE
            printer.pln(");");

        }
        printer.pln();
        visit(n);


    }


    String modifier;
    public void visitMethodDeclarations(GNode n){
        for(int x = 0; x<n.size(); x++){
            if(n.getNode(x).getNode(0).getString(0).equals("true")){
                modifier = "static ";
            }
            else {
                modifier = "";
            }

            if(n.getNode(x).getNode(1).getString(0).equals("Class") && n.getNode(x).getNode(2).getString(0).equals("__class()")){
                continue;
            }

            printer.indent().p(modifier+n.getNode(x).getNode(1).getString(0)+" "+n.getNode(x).getNode(2).getString(0)+"(");
            printParameters((GNode)n.getNode(x).getNode(3)); //NOT SURE
            printer.pln(");");

        }
        printer.pln().pln();
        printer.indent().pln("static Class __class();");
        printer.pln().pln();
        visit(n);

    }



    public void visitVFieldDec(GNode n){
        printer.indent().pln("static __"+className+"_VT __vtable;");
        printer.pln();
        visit(n);
    }


    public void visitVTableLayout(GNode n){
        printer.indent().pln("struct __"+n.getString(0)+"_VT {");
        printer.incr().indent().pln("Class __is_a;");
        printer.pln().pln();
        visit(n);
        printer.decr().indent().pln("{");
        printer.indent().pln("}");
        printer.decr().indent().pln("};");
        printer.pln().pln().pln();

    }


    public void visitVFields(GNode n){

        for(int i=1; i<n.size(); i++){
            printer.indent().pln(n.getNode(i).getNode(0).getString(0)+" ("+n.getNode(i).getNode(1).getString(0)+
                    ")("+n.getNode(i).getNode(2).getString(0)+");");
        }
        printer.pln().pln();

    }


    public void visitVMethods(GNode n){

        printer.indent().pln("__"+className+"_VT()");
        printer.indent().pln(": __is_a(__"+className+"::__class()),");
        printer.incr();

        for(int i = 0; i<n.size(); i++){

            if(n.getNode(i).getNode(0).getString(0).equals("__is_a") && n.getNode(i).getNode(1).getString(0).equals("(__"+className+"::__class())")){
                continue;
            }
            printer.indent().pln(n.getNode(i).getNode(0).getString(0)+n.getNode(i).getNode(1).getString(0)+",");
        }
        visit(n);

    }



    public void printParameters(GNode n){
        for (int x = 0; x < n.size() ; x++){
            printer.p(n.getNode(x).getNode(0).getString(0));
            if (x != (n.size()-1)){
                printer.p(",");
            }
        }
    }



    public void visit(Node n) {
        for (Object o : n) if (o instanceof Node) dispatch((Node) o);
    }

}
