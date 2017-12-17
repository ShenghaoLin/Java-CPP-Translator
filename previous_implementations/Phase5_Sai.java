/**
 * Phase 5 printer that uses the Xtc's pretty printer to print the updated C++ ast from Phase 4
 * to output.cpp and main.cpp implementation files in C++ format.
 * Note: use sbt's format command to format the final code for indentation
 *
 * @author Shenghao Lin
 * @author Sai Akhil
 */

package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Printer;
import xtc.tree.Visitor;

import edu.nyu.oop.util.XtcProps;
import edu.nyu.oop.util.NodeUtil;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;

/* Print out the information in the AST in a concrete C++ syntax generated from Phase 4
 * Use format command in sbt to indent the code
 * Note: main in included in output.cpp itself.
 */


public class Phase5 extends Visitor {

    private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

    private Printer printer;

    private String outputLocation = XtcProps.get("output.location");

    String packageInfo = "";

    boolean inCout = false;

    String headoffile;

    /* Class constructor. Intializing the writer to the file. */
    public Phase5(String name) {

        Writer w = null;
        try {
            FileOutputStream fos = new FileOutputStream(outputLocation + name);
            OutputStreamWriter ows = new OutputStreamWriter(fos, "utf-8");
            w = new BufferedWriter(ows);
            this.printer = new Printer(w);
        } catch (Exception e) {
            throw new RuntimeException("Output location not found. Create the /output directory.");
        }

        printer.register(this);
    }

    public Printer printer() {
        return this.printer;
    }

    public void setPrinter(Printer p) {
        this.printer = p;
    }

    public String location() {
        return this.outputLocation;
    }


    /* The actual print method */
    public void print(GNode ast) {
        dispatch(ast);
        printer.flush();
    }

    /* Header info */
    public void headOfFile() {
        headoffile = "#include \"output.h\"\n#include <iostream>\n\nusing namespace java::lang;";
        printer.pln("#include \"output.h\"");
        printer.pln("#include <iostream>");
        printer.pln();
        printer.pln("using namespace java::lang;").pln();
    }

    /* Visitor for ClassDeclaration
     * Generate code for default constructor, Class method(class identity and its parent)
     * and vtable initialization.
     */
    public void visitClassDeclaration(GNode n) {

        //Obtaining information of class name of parent class name
        String className = n.get(1).toString();
        String parentName = "";
        GNode extensionClass = (GNode) NodeUtil.dfs(n, "Extension");
        if (extensionClass != null) {
            GNode parentType = (GNode) extensionClass.get(0);
            GNode parentTypeNode = (GNode) parentType.get(0);
            parentName = parentTypeNode.get(0).toString();
        } else  {
            parentName = "Object";
        }


        //default constructor
        printer.pln((String) n.getProperty("defaultConstructor"));
        printer.pln().flush();

        //Default constructor init method
        if (n.getProperty("realDefaultConstructor") != null) {
            printer.pln((String) n.getProperty("realDefaultConstructor"));
        }

        GNode node = (GNode) NodeUtil.dfs(n, "MethodDeclaration");
        if(node.getString(3).contains("main")){
            printer.flush();
            printmain(node);
        }

        //visit class body
        GNode classBody = (GNode) NodeUtil.dfs(n, "ClassBody");
        dispatch(classBody);

        //class method
        printer.pln((String) n.getProperty("classInfo"));


        if (n.getProperty("staticInit") != null) {
            printer.pln((String) n.getProperty("staticInit"));
        }


        //vtable initialization
        printer.pln((String) n.getProperty("vtableInit")).flush();



    }

    public void visitConditionalStatement(GNode n) {

        printer.p("if (").flush();
        GNode condition = (GNode) n.get(0);
        dispatch(condition);
        printer.p(")").flush();

        for (int i = 1; i < n.size(); i++) {
            if (n.get(0) instanceof Node) dispatch((Node) n.get(i));
            if (n.get(0) instanceof String) {
                printer.p((String) n.get(i) + " ").flush();
            }
        }
    }

    public void visitInstanceOfExpression(GNode n) {
        dispatch((Node) n.get(0));
        printer.p("-> __vptr -> getClass(").flush();
        dispatch((Node) n.get(0));
        printer.p(')');
        printer.p("-> __vptr -> isInstance( ").flush();
        dispatch((Node) n.get(0));
        printer.p("-> __vptr -> getClass(").flush();
        dispatch((Node) n.get(0));
        printer.p(")").flush();
        printer.p(", (Object) new __").flush();
        dispatch((Node) n.get(1));
        printer.p("())").flush();

    }

    /* Visitor for CompilationUnit
     * Generate information of the package and print namespace
     */
    public void visitCompilationUnit(GNode n) {

        //print namespace and generate package info
        GNode p = (GNode) n.getGeneric(0);
        GNode packageName = (GNode) p.getGeneric(1);
        for (int i = 0; i < packageName.size(); i ++) {
            packageInfo += packageName.get(i).toString() + ".";
            printer.pln("namespace " + packageName.get(i).toString()).flush();
            printer.pln("{").flush();
        }

        //visit children(except for the 0-index node -- which is package name)
        for (int i = 1; i < n.size(); i++) {
            Object o = n.get(i);
            if (o instanceof Node) dispatch((Node) o);
        }

        //right brackets
        for (int i = 0; i < packageName.size(); i ++) {
            printer.pln("}").flush();
        }
        printer.pln().flush();
    }

    /* Visitor for FieldDeclaration
     * Add ";" at the end of each statement
     */
    public void visitFieldDeclaration(GNode n) {
        visit(n);
        printer.pln(";").flush();
    }

    /* Visitor for Block
     * Add brackets "{}" at beginning and ending
     */
    public void visitBlock(GNode n) {
        printer.pln("{").flush();
        visit(n);
        printer.pln("}").flush();
        printer.pln().flush();

    }

    /* Visitor for Arguments
     * Add brackets "()" at beginning and ending
     * add comma between elements
     */
    public void visitArguments(GNode n) {

        printer.p("(").flush();

        //add comma
        for (int i = 0; i < n.size() - 1; i++) {
            try {
                GNode child = (GNode) n.getGeneric(i);
                dispatch(child);
                printer.p(", ").flush();
            } catch (Exception e) {}
        }

        try {
            Object child = n.getGeneric(n.size() - 1);
            if (child instanceof GNode) {
                dispatch((GNode) child);
            } else if (child instanceof String) {
                printer.p((String) child).flush();
            }
        } catch (Exception e) {}
        printer.p(")").flush();
    }

    /* Visitor for FormalParameters
     * Same as Arguments
     */
    public void visitFormalParameters(GNode n) {
        visitArguments(n);
    }

    public void visitClassBody(GNode n) {
        for (Object o : n) {
            if (o instanceof GNode) {
                GNode node = (GNode) o;
                if (!node.hasName("FieldDeclaration")) {
                    dispatch(node);
                }
            }
        }
    }

    /* Skip extension since we already have its info */
    public void visitExtension(GNode n) {}

    /* Visitor for CastExpression
     * Add "()" to the cast type
     */
    public void visitCastExpression(GNode n) {

        GNode type = (GNode) n.getGeneric(0);

        dispatch(type);


        printer.p("(");

        dispatch((GNode) n.getGeneric(1));

        printer.p(") ").flush();
    }

    /* Visitor for CallExpression
     * For cout, arguments should be placed after "<<"" instead of inside "()"
     * toString() should be tranformed to to toString() -> data
     */
    public void visitCallExpression(GNode n) {

        //cout handling
        if (n.getProperty("cout") != null) {

            inCout = true;

            printer.p("std::cout ").flush();
            GNode arguments = (GNode) n.getGeneric(3);

            //print arguments, starts with "<<"
            for (Object o : arguments) {
                printer.p("<< ").flush();
                if (o instanceof Node) {
                    GNode gnode = (GNode) o;
                    dispatch((Node) o);

                    //toString handling
                    // if (gnode.hasName("CallExpression")) {
                    //     if (gnode.get(2).toString().endsWith("toString")) {
                    //         printer.p("-> data ").flush();
                    //     }
                    // }
                }

                if (o instanceof String) printer.p(((String) o) + " ").flush();
            }
        }

        //other calling will not be specialized.
        else {
            visit(n);

        }

        inCout = false;



    }

    public void visitCallExpressionBlock(GNode n) {

        printer.p("({");
        visit(n);
        printer.p("})").flush();
    }

    public void visitCheck(GNode n) {
        printer.p(n.getString(0));
        printer.p("(");
        dispatch(n.getNode(1));
        for (int i = 2; i < n.size(); i++) {
            printer.p(", ");
            dispatch(n.getNode(i));
        }
        printer.pln(");").flush();

    }

    /* Visitor for ExpressionStatement
     * Add ";" at the end of statement
     */
    public void visitExpressionStatement(GNode n) {
        visit(n);
        printer.pln(";").flush();

        Object call = NodeUtil.dfs(n, "CallExpression");

        if (call != null) {

            GNode nn = (GNode) call;

            Object o = nn.getProperty("initStatements");

            if (o != null) {
                printer.p(o.toString()).flush();
            }
        }
    }


    /* Visitor for ReturnStatement
     * print return, ending with ";"
     */
    public void visitReturnStatement(GNode n) {
        printer.p("return ").flush();
        visit(n);
        printer.pln(";").flush();
    }

    /* Visitor for VoidType
     * print "void"
     */
    public void visitVoidType(GNode n) {
        printer.p("void ").flush();
    }

    /* Visitor for Declarator
     * adding "=" to the statement
     */
    public void visitDeclarator(GNode n) {
        printer.p(n.get(0).toString()).flush();

        boolean eq = false;
        for (int i = 1; i < n.size(); i++) {
            try {
                GNode child = (GNode) n.getGeneric(i);

                if (child != null) {
                    if (!eq) {
                        printer.p(" = ").flush();
                        eq = true;
                    }
                    dispatch(child);
                }
            } catch (Exception e) {}

            if (n.get(i) instanceof String) {

                if (!eq) {
                    printer.p("= ").flush();
                    eq = true;
                }
                printer.p((String) n.get(i)).flush();
            }
        }

    }


    /* Visitor for ThisExpression
     * print "__this"
     */
    // public void visitThisExpression(GNode n) {
    //     printer.p("__this ").flush();
    //     visit(n);
    // }

    public void visitWhileStatement(GNode n) {
        printer.p("while ").flush();
        visit(n);
    }

    public void visitRelationalExpression(GNode n) {
        printer.p("(").flush();
        visit(n);
        printer.p(")").flush();
    }

    public void visitStringLiteral(GNode n) {

        printer.p("__rt::literal(").flush();
        visit(n);
        printer.p(")").flush();

    }

    public void visitForStatement(GNode n) {
        printer.p("for (").flush();
        dispatch(n.getNode(0).getNode(1));
        dispatch(n.getNode(0).getNode(2));
        printer.p("; ").flush();
        visit(n.getNode(0).getNode(3));
        printer.p("; ").flush();
        dispatch(n.getNode(0).getNode(4));
        printer.pln(")").flush();
        dispatch(n.getNode(1));
    }

    public void visitSubscriptExpression(GNode n) {

        if (null != n.getProperty("AccessCheck")) {
            printer.p("({");
            printer.p(n.getProperty("AccessCheck").toString());
        }

        dispatch(n.getNode(0));
        printer.p("-> __data[").flush();
        dispatch(n.getNode(1));
        printer.p("]").flush();

        if (null != n.getProperty("AccessCheck")) {
            printer.p(";})");

        }

    }

    public void visitConcreteDimensions(GNode n) {
        printer.p("(").flush();
        visit(n);
        printer.p(")").flush();
    }

    public void visitNewArrayExpression(GNode n) {
        printer.p("new ").flush();
        visit(n);
    }
    
    public void visitRuntimeNode(GNode n){
        printer.pln(n.getProperty("RuntimeInfo").toString());
    }

    //Prints main implementation seperately to main.cpp
    public void printmain(GNode n){
        Phase5 mainPrint = new Phase5("main.cpp");
        Printer mainPrinter = mainPrint.printer();
        mainPrinter.register(mainPrint);
        mainPrinter.pln("#include \"java_lang.h\"").flush();
        mainPrint.headOfFile();
        mainPrinter.pln("using namespace std;");
        mainPrinter.pln("using namespace " +
        packageInfo.substring(0, packageInfo.length() - 1).replace(".", "::") + ";").pln().flush();
        String info = packageInfo.substring(0, packageInfo.length() - 1).replace(".", "::");
        mainPrinter.pln("int main(int argc, char* argv[]) {\n" +
                "  // Implement generic interface between C++'s main function and Java's main function\n" +
                "  __rt::Array<String> args = new __rt::__Array<String>(argc - 1);\n" +
                "\n" +
                "  for (int32_t i = 1; i < argc; i++) {\n" +
                "    (*args)[i] = __rt::literal(argv[i]);\n" +
                "  }\n" +
                "  \n" +
                "  " + info + "::__Test" + info.substring(12,15) + "::main(args);\n" +
                "  \n" +
                "  return 0;\n" +
                "}");
        mainPrinter.flush();

    }

    /* General visitor
     * Besides dispatch, also print all String instances it meets
     */
    public void visit(Node node) {
        for (Object o : node) {

            //dispatch
            if (o instanceof Node) dispatch((Node) o);

            //print string
            if (o instanceof String) {
                String s = (String) o;

                printer.p(s + " ").flush();
            }
        }
    }
}