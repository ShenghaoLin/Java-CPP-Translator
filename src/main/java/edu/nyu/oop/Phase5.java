/**
 * Phase 5 printer that uses the Xtc's pretty printer to print the updated C++ ast from Phase 4
 * to output.cpp and main.cpp implementation files in C++ format.
 * Note: use sbt's format command to format the final code for indentation
 *
 * @author Shenghao Lin
 * @author Sai Akhil
 *
 * @version 2.0
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

/**
 * Print out the information in the AST in a concrete C++ syntax generated from Phase 4
 * Under construction, working for most classes and methods
 * currently working on the seperation of main function
 * no indent
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

        //Add run-time array definition if needed 
        if (null != ast.getProperty("RuntimeInfo")) {
            printer.pln("namespace __rt {");
            printer.pln(ast.getProperty("RuntimeInfo").toString());
            printer.pln("}").flush();
        }
    }

    /* The claim placed in the beginning of cpp files */
    public void headOfFile() {
        headoffile = "#include \"output.h\"\n#include <iostream>\n\nusing namespace java::lang;";
        printer.pln("#include \"output.h\"");
        printer.pln("#include <iostream>");
        printer.pln();
        printer.pln("using namespace java::lang;").pln();
    }

    /**
     * Visitor for ClassDeclaration
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

        //real default constructor
        if (n.getProperty("realDefaultConstructor") != null) {
            printer.pln((String) n.getProperty("realDefaultConstructor"));
        }

        //print main.cpp
        GNode node = (GNode) NodeUtil.dfs(n, "MethodDeclaration");
        if (null != node) {
            if(node.getString(3).contains("main")){
                printer.flush();
                printmain(node);
            }
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

    /**
     * Visitor for the instanceof statment
     * has the stype "a->__vptr->getClass(a)->__vptr->isInstance(a->__vptr->getClass(a), __rt::java_cast<Object>(__B::__init(new __B()));"
     */
    public void visitInstanceOfExpression(GNode n) {
        dispatch((Node) n.get(0));
        printer.p("-> __vptr -> getClass(");
        dispatch((Node) n.get(0));
        printer.p(')');
        printer.p("-> __vptr -> isInstance( ");
        dispatch((Node) n.get(0));
        printer.p("-> __vptr -> getClass(");
        dispatch((Node) n.get(0));
        printer.p(")").flush();
        printer.p(", __rt::java_cast<Object>(__");
        dispatch((Node) n.get(1));
        printer.p("::__init(new __");
        dispatch((Node) n.get(1));
        printer.p("()))");
        printer.p("())").flush();

    }

    /**
     * Visitor for CompilationUnit
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

    /**
     * Visitor for FieldDeclaration
     * Add ";" at the end of each statement
     */
    public void visitFieldDeclaration(GNode n) {
        visit(n);
        printer.pln(";").flush();
    }

    /**
     * Visitor for Block
     * Add brackets "{}" at beginning and ending
     */
    public void visitBlock(GNode n) {
        printer.pln("{").flush();
        visit(n);
        printer.pln("}").flush();
        printer.pln().flush();

    }

    /**
     * Visitor for Arguments
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

    /**
     * Visitor for FormalParameters
     * Same as Arguments
     */
    public void visitFormalParameters(GNode n) {
        visitArguments(n);
    }

    /**
     * Visitor for ClassBody 
     * print everything other than FieldDeclaration
     * as class fields are defined in output.h
     */
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

    /**
     * Visitor for CastExpression
     * Add "()" to the argument of __rt::java_cast<>
     */
    public void visitCastExpression(GNode n) {

        GNode type = (GNode) n.getGeneric(0);

        dispatch(type);


        printer.p("(");

        dispatch((GNode) n.getGeneric(1));

        printer.p(") ").flush();
    }

    /**
     * Visitor for CallExpression
     * For cout, arguments should be placed after "<<"" instead of inside "()"
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

    /**
     * Visitor for CallExpressionBlock defined in phase4
     * add ({}) to the content
     */
    public void visitCallExpressionBlock(GNode n) {

        printer.p("({");
        visit(n);
        printer.p("})").flush();
    }

    /**
     * Visitor for Check defined in phase4
     * print the check, and add () to its arguments 
     */
    public void visitCheck(GNode n) {

        printer.p(n.getString(0));
        
        printer.p("(");

        dispatch(n.getNode(1));
        
        //add comma
        for (int i = 2; i < n.size(); i++) {
            printer.p(", ");
            dispatch(n.getNode(i));
        }

        printer.pln(");").flush();

    }

    /**
     * Visitor for ExpressionStatement
     * Add ";" at the end of statement
     * When it has a CallExpression with property of "initStatement"
     * add initializations after the current expression statement
     */
    public void visitExpressionStatement(GNode n) {
        
        //print current statement
        visit(n);
        printer.pln(";").flush();

        Object call = NodeUtil.dfs(n, "CallExpression");

        if (call != null) {

            GNode nn = (GNode) call;

            //print out initializations 
            Object o = nn.getProperty("initStatements");

            if (o != null) {
                printer.p(o.toString()).flush();
            }
        }
    }


    /**
     * Visitor for ReturnStatement
     * print return, ending with ";"
     */
    public void visitReturnStatement(GNode n) {
        printer.p("return ").flush();
        visit(n);
        printer.pln(";").flush();
    }

    /**
     * Visitor for VoidType
     * print "void"
     */
    public void visitVoidType(GNode n) {
        printer.p("void ").flush();
    }

    /**
     * Visitor for Declarator
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

    /**
     * Visitor for WhileStatement
     * print while
     */
    public void visitWhileStatement(GNode n) {
        printer.p("while ").flush();
        visit(n);
    }

    /**
     * Visitor for RelationalExpression
     * print () to contain its content
     */
    public void visitRelationalExpression(GNode n) {
        printer.p("(").flush();
        visit(n);
        printer.p(")").flush();
    }

    /**
     * Visitor for StringLiteral
     * print it as __rt::literal("")
     */
    public void visitStringLiteral(GNode n) {

        printer.p("__rt::literal(").flush();
        visit(n);
        printer.p(")").flush();

    }

    /**
     * Visitor for ForStatement
     * print for and its following conditions in c++ style
     */
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

    /**
     * Visitor for SubscriptExpression
     * print array access in the format of a->__data[i]
     * if an AccessCheck is needed, add it before the access
     * and use ({}) to contain and return this access
     */
    public void visitSubscriptExpression(GNode n) {

        //Access check if needed
        if (null != n.getProperty("AccessCheck")) {
            printer.p("({");
            printer.p(n.getProperty("AccessCheck").toString());
            printer.p("(");

            if (n.get(0) instanceof Node) {
                dispatch(n.getNode(0));
            }
            else if (n.get(0) instanceof String) {
                printer.p(((String) n.get(0)) + " ");
            }
            
            printer.p(",");
            
            if (n.get(1) instanceof Node) {
                dispatch(n.getNode(1));
            }
            else if (n.get(1) instanceof String) {
                printer.p(((String) n.get(1)) + " ");
            }

            printer.p(");\n");
        }

        //print array name
        if (n.get(0) instanceof Node) {
            dispatch(n.getNode(0));
        }
        else if (n.get(0) instanceof String) {
            printer.p(((String) n.get(0)) + " ");
        }

        //print index
        printer.p("-> __data[").flush();

        if (n.get(1) instanceof Node) {
            dispatch(n.getNode(1));
        }
        else if (n.get(1) instanceof String) {
            printer.p(((String) n.get(1)) + " ");
        }

        printer.p("]").flush();

        //access check if needed
        if (null != n.getProperty("AccessCheck")) {
            printer.p(";})");

        }

    }

    /**
     * Visitor for SelectionExpression
     * If there is a check for the selection, add a ({}) block to contain
     */
    public void visitSelectionExpression(GNode n) {
        if (null != n.getProperty("Block")) {
            printer.p("({");
            if (null != n.getProperty("Check")) {
                printer.p(n.getProperty("Check") + "(");
                dispatch(n.getNode(0));
                printer.p(");\n");
            }
        }

        visit(n);

        if (null != n.getProperty("Block")) {
            printer.p("})");
        }
    }

    /**
     * Visitor for NewArrayExpression
     * Print a statement to create a new array.
     * A initialization will be added to nested arrays
     */
    public void visitNewArrayExpression(GNode n) {

        //nested array
        if (!n.getProperty("InitSubArray").toString().equals("")) {

            printer.p("({" + n.getProperty("ArrayType") + " tmp = " );
            visit(n);
            printer.p(";\n" + n.getProperty("InitSubArray"));
            printer.pln("tmp;})").flush();
        }

        //1D array 
        else {
            visit(n);
        }
    }

    /**
     * Vistor for NullLiteral
     * print __rt::null()
     */
    public void visitNullLiteral(GNode n) {
        printer.p("__rt::null() ").flush();
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
        String info2 = info.split("::")[1];
        mainPrinter.pln("int main(int argc, char* argv[]) {");
        mainPrinter.pln("  // Implement generic interface between C++'s main function and Java's main function");
        mainPrinter.pln("  __rt::Array<String> args = new __rt::__Array<String>(argc - 1);");
        mainPrinter.pln();
        mainPrinter.pln("  for (int32_t i = 1; i < argc; i++) {");
        mainPrinter.pln("    (*args)[i] = __rt::literal(argv[i]);");
        mainPrinter.pln("   }");
        mainPrinter.pln();
        mainPrinter.pln("  " + info + "::__" + info2.substring(0,1).toUpperCase() + info2.substring(1,info2.length()) + "::main(args);");
        mainPrinter.pln();
        mainPrinter.pln("  return 0;");
        mainPrinter.pln("}");
        mainPrinter.flush();
    }


    /**
     * General visitor
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
