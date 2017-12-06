package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.util.SymbolTable;
import edu.nyu.oop.util.SymbolTableUtil;
import xtc.util.Runtime;

import xtc.lang.JavaEntities;

import xtc.type.*;

import edu.nyu.oop.util.NodeUtil;
import edu.nyu.oop.util.SymbolTableBuilder;
//import edu.nyu.oop.BigArray;
//import edu.nyu.oop.PrimitiveArray;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashMap;

/* Building C++ style AST given a root node of a java AST */
public class Phase4 {

    private Runtime runtime;
    private HashMap<String, String> ctp = new HashMap<String, String>();
    private HashMap<String, ArrayList<Phase1.Initializer>> inis = new HashMap<String, ArrayList<Phase1.Initializer>>();

    public ArrayList<BigArray> bigArrays = new ArrayList<BigArray>();
    public ArrayList<PrimitiveArray> primitiveArrays = new ArrayList<PrimitiveArray>();


    public Phase4 (Runtime runtime) {
        this.runtime = runtime;
    }

    public Phase4 (Runtime runtime, HashMap<String, String> ctp, HashMap<String, ArrayList<Phase1.Initializer>> inis) {
        this.runtime = runtime;
        this.ctp = ctp;
        this.inis = inis;
    }

    /* process a list of nodes */
    public List<GNode> process(List<GNode> l) {

        ArrayList<GNode> cppAst = new ArrayList<GNode>();

        for (Object o : l) {
            if (o instanceof Node) {
                cppAst.add((GNode) o);
            }
        }

        for (Object o : cppAst) {
            if (o instanceof Node) {

                SymbolTable table = new SymbolTableBuilder(runtime).getTable((GNode) o);
                Phase4Visitor visitor = new Phase4Visitor(table, runtime, ctp, inis);
                visitor.traverse((Node) o);
            }
        }

        return cppAst;
    }

    /* process a single node */
    public Node runNode(Node n, SymbolTable table) {
        Phase4Visitor visitor = new Phase4Visitor(table, runtime, ctp, inis);
        visitor.traverse(n);
        //bigArrays.addAll(visitor.bigArrays);
        //primitiveArrays.addAll(visitor.primitiveArrays);
        return n;
    }

    /* Visitor class used to modify java AST to C++ AST */
    public static class Phase4Visitor extends Visitor {

        private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

        private Runtime runtime;

        private String currentClass = null;
        private Object extension = null;
        private boolean isMain = false;
        private String methodName = "";
        private String packageInfo = "";
        private boolean constructorFlag = false;
        private HashMap<String, String> ctp;
        private HashMap<String, ArrayList<Phase1.Initializer>> inis;

        private SymbolTable table;

        public ArrayList<BigArray> bigArrays = new ArrayList<BigArray>();
        public ArrayList<PrimitiveArray> primitiveArrays = new ArrayList<PrimitiveArray>();

        public Phase4Visitor(SymbolTable table, Runtime runtime, 
            HashMap<String, String> ctp, HashMap<String, ArrayList<Phase1.Initializer>> inis) {
            this.table = table;
            this.runtime = runtime;
            this.ctp = ctp;
            this.inis = inis;
        }

        public void visitCompilationUnit(GNode n) {

            String packageInfo = "";

            GNode p = (GNode) n.getGeneric(0);
            GNode packageName = (GNode) p.getGeneric(1);
            for (int i = 0; i < packageName.size(); i ++) {
                packageInfo += packageName.get(i).toString() + ".";
            }

            this.packageInfo = packageInfo;

            packageInfo = packageInfo.substring(0, packageInfo.length() - 1);

            table.enter("package(" + packageInfo + ")");
            table.enter(n);
            table.mark(n);

            for (int i = 1; i < n.size(); i++) {
                GNode child = n.getGeneric(i);
                dispatch(child);
            }

            table.exit();
            table.exit();
            table.setScope(table.root());
        }




        /* Visitor for Modifiers
         * Modifiers are not considered in our translator
         */
        public void visitModifiers(GNode n) {
            for (int i = 0; i < n.size(); i ++) {
                n.set(i, null);
            }
            visit(n);
        }

        public String toCppType(String type) {

            String cppType;
            switch (type) {
                case "long":
                    cppType = "int64_t";
                    break;
                case "int":
                    cppType = "int32_t";
                    break;
                case "short":
                    cppType = "int16_t";
                    break;
                case "byte":
                    cppType = "int8_t";
                    break;
                case "boolean":
                    cppType = "bool";
                    break;
                default:
                    cppType = type;
                    break;
            }
            return cppType;

        }

        public boolean isPrimitiveType(String type) {
            if (type.equals("boolean")||type.equals("byte")||type.equals("char")||
                type.equals("short")||type.equals("int")||type.equals("long")||
                type.equals("float")||type.equals("double")) {
                return true;
            }
            return false;

        }

        public void visitType(GNode n) {
            Object dim = NodeUtil.dfs(n, "Dimensions");

            if (dim != null) {
                String className = n.getNode(0).get(0).toString();

                className = toCppType(className);

                if (isPrimitiveType(className)) {
                    className = toCppType(className);
                    primitiveArrays.add(new PrimitiveArray(className));
                }
                else {
                    bigArrays.add(new BigArray(className, packageInfo));
                }

                n.set(0, GNode.create("QualifiedIdentifier", "__rt::Array<" + className + ">"));
                n.set(1, null);
            }
            visit(n);
        }

        public void visitNewArrayExpression(GNode n) {
            String className = n.getNode(0).get(0).toString();

            if (isPrimitiveType(className)) {
                    className = toCppType(className);
            }

            n.set(0, GNode.create("QualifiedIdentifier", "__rt::Array<" + className + ">"));
            visit(n);
        }

        public void visitBlockDeclaration(GNode n) {
            SymbolTableUtil.enterScope(table, n);
            table.mark(n);
            visit(n);
            SymbolTableUtil.exitScope(table, n);
        }

        /* Visitor for NewClassExpression
         * Class name should start with "__"
         */
        public void visitNewClassExpression(GNode n) {
            GNode id = (GNode) NodeUtil.dfs(n, "QualifiedIdentifier");
            if (!id.get(0).toString().startsWith("__")) {
                Node oldArgs = n.getNode(3);


                GNode args = GNode.create("Arguments");
                n.set(3, args);
                args.add(GNode.create("Argument", "new __" + id.get(0).toString() + "()"));

                for (int j = 0; j < oldArgs.size(); j++) {
                    args.add(oldArgs.get(j));
                }

                id.set(0, "__" + id.get(0) + "::__init");
            }
            visit(n);
        }

        public HashMap<String, String> fieldsInit(String c) {
            ArrayList<String> parents = new ArrayList<String>();
            parents.add(c);
            
            while (this.ctp.get(c) != null) {
                c = this.ctp.get(c);
                parents.add(c);
            }

            HashMap<String, String> f = new HashMap<String, String>();

            //for (int i = parents.size() - 1; i >= 0; i --) {
            //    HashMap<String, String> cf = this.inis.get(parents.get(i));

            //    if (cf != null) {
                    // System.out.println(parents.get(i));
                    // for (Object o : cf.keySet()) {
                    //     if (o instanceof String) {
                    //         System.out.println((String) o);
                    //     }   
                    // }
            //       f.putAll(cf);
            //    }

                //f.putAll(cf);
            //}

            return f;

        }

        /* Visitor for ClassDeclaration
         * Modified method name to "__class::method"
         * Adding __this argument to each method
         * Transform this() and super() called in constructors to the
         * form by calling __init();
         */
        public void visitClassDeclaration(GNode n) {

            SymbolTableUtil.enterScope(table, n);
            table.mark(n);

            //collect class info
            String name = n.get(1).toString();
            currentClass = name;
            extension = NodeUtil.dfs(n, "Extension");

            String parentName = "";

            if (extension != null) {
                GNode parentType = (GNode) ((GNode) extension).getNode(0);
                GNode parentTypeNode = (GNode) parentType.getNode(0);
                parentName = parentTypeNode.get(0).toString();
            } 
            else {
                parentName = "Object";
            }

            String dCon = "__" + name + "::__" + name + "() : ";

            HashMap<String, String> f = fieldsInit(currentClass);

            for (Object o : f.keySet()) {
                if (o instanceof String) {

                    dCon += (String) o;

                    dCon += "(" + f.get((String) o) + "), ";

                    // System.out.println(((VariableT) o).getType().getName());
                    // Set<String> types = ((VariableT) o).getType().getScope();
                    // for (Object oo : types){
                    //     System.out.println((String) oo);
                    // }
                }
            }

            dCon += "__vptr(&__vtable) {}";

            System.out.println(dCon);

            n.setProperty("defaultConstructor", dCon);

            String classInfo = "";

            classInfo += "Class __" + n.get(1).toString() + "::__class() {\n";
            classInfo += "static Class k = new __Class(__rt::literal(\""
                         + this.packageInfo + currentClass + "\"), __"
                         + parentName + "::__class());\n";
            classInfo += "return k;\n";
            classInfo += "}\n";

            n.setProperty("classInfo", classInfo);

            String vtableInit = "__" + currentClass + "_VT __" + currentClass
                                + "::__vtable;\n";

            n.setProperty("vtableInit", vtableInit);

            visit(n);

            extension = null;
            currentClass = null;

            SymbolTableUtil.exitScope(table, n);
        }

        /* Visitor for MethodDeclaration
         * Checking whether variables inside this scope are defined in
         * the class field or inside the method
         * (actually the main work is done in visitBlock(), where block will
         * examine whether variable used inside their scopes are defined inside
         * here we check if those which are set to not be defined in the inner scope
         * have been defined as parameters)
         */
        public void visitMethodDeclaration(GNode n) {

            SymbolTableUtil.enterScope(table, n);
            table.mark(n);

            methodName = table.current().getName();

            if (n.getProperty("mangledName") != null) {
                n.set(3, n.getProperty("mangledName").toString().replace(" ", ""));
            }

            //main function has no field method
            if (n.get(3).toString().equals("main")) {

                isMain = true;

                n.set(2, "int");

                //cannot handling array now
                //n.set(4, GNode.create("Arguments", GNode.create("VoidType")));

                visit(n);

                GNode blockinside = (GNode) NodeUtil.dfs(n, "Block");

                GNode newBlock = GNode.create("Block");
                for (Object o : blockinside) {
                    newBlock.add(o);
                }
                newBlock.add(GNode.create("ReturnStatement", "0"));
                n.set(7, newBlock);

                isMain = false;
            }




            else {

                constructorFlag = false;

                visit(n);

                //constructor
                if (n.get(3).toString().equals(currentClass)) {

                    n.set(0, GNode.create("Modifiers"));
                    n.set(2, currentClass);
                    n.set(3, "__" + currentClass + "::" + "__init");

                    GNode blockContent = (GNode) NodeUtil.dfs(n, "Block");
                    GNode newBlock = GNode.create("Block");

                    if (!constructorFlag) {
                        newBlock.add(GNode.create("Statement", "__Object::__init(__this);\n"));
                    }

                    for (Object o : blockContent) {
                        newBlock.add(o);
                    }

                    newBlock.add(GNode.create("ReturnStatement", "__this"));
                    n.set(7, newBlock);
                }

                //normal methods
                else {
                    n.set(0, GNode.create("Modifiers"));
                    n.set(2, currentClass);
                    n.set(3, "__" + currentClass + "::" + n.get(3).toString());
                }

                //Arguments modify, add __this
                GNode thisType = GNode.create("Type", GNode.create("QualifiedIdentifier", currentClass), null);
                GNode thisParameter = GNode.create("FormalParameter",  GNode.create("Modifier"), thisType, null, "__this", null);
                GNode param = GNode.create("FormalParameters");
                GNode oldParam = (GNode) n.get(4);
                param.add(thisParameter);

                //fill old arguments
                for (int j = 0; j < oldParam.size(); j++) {
                    param.add(oldParam.get(j));
                }
                n.set(4, param);


            }

            methodName = "";

            SymbolTableUtil.exitScope(table, n);

        }


        /* Visitor for Block
         * Check whether the variables inside the scope are defined
         * Can also be done using symble table (similar idea here)
         */
        public void visitBlock(GNode n) {

            SymbolTableUtil.enterScope(table, n);
            table.mark(n);

            visit(n);

            SymbolTableUtil.exitScope(table, n);
        }

        public void visitPrimaryIdentifier(GNode n) {
            if (n.get(0).toString().contains("__this")) {
                return;
            }

            //add this to field data
            if (!isMain) {


                //TODO: this is not working for nested scopes
                if (!table.current().getParent().isDefinedLocally(n.get(0).toString())) {
                    n.set(0, "__this -> " + n.get(0).toString());
                }
            }

            visit(n);
        }

        /* Visitor for Selection Expression
         * C++ style "->" for selection
         */
        public void visitSelectionExpression(GNode n) {
            for (int i = 1; i < n.size(); i++) {
                if (n.get(i) instanceof String) {
                    if (!n.get(i).toString().startsWith("-> ")) {
                        n.set(i, "-> " + n.get(i).toString());
                    }
                }
            }
            visit(n);
        }

        public void visitForStatement(GNode n) {
            SymbolTableUtil.enterScope(table, n);
            table.mark(n);

            visit(n);

            SymbolTableUtil.exitScope(table, n);
        }


        /* Visitor for Selection Expression
         * C++ style "->" for calling class methods
         */
        public void visitCallExpression(GNode n) {

            //System.out.println("good");
            //collect info
            Object o = n.getNode(0);
            GNode select, primaryID;
            select = null;
            primaryID = null;
            if (o != null) {
                select = (GNode) o;
                Object oo = NodeUtil.dfs(select, "PrimaryIdentifier");
                if (oo != null) {
                    primaryID = (GNode) oo;
                }
            }

            if (n.getProperty("mangledName") != null) {
                n.set(2, n.getProperty("mangledName").toString().replace(" ", ""));
                System.out.println("mangledName");
            }

            //method in current class
            if (!isMain) {
                if (select == null) {

                    //call __init
                    if (n.get(2).toString().equals("this")) {

                        n.set(2, "__" + currentClass + "::__init");
                        constructorFlag = true;

                    }

                    //call parent's __init
                    else if (n.get(2).toString().equals("super")) {
                        constructorFlag = true;
                        String parentName = "";
                        if (extension == null) {
                            parentName = "Object";
                        } else {
                            parentName = ((GNode) NodeUtil.dfs((Node) extension, "QualifiedIdentifier")).get(0).toString();
                        }

                        GNode arguments = GNode.create("Arguments");
                        n.set(2, "__" + parentName + "::__init");
                    }

                    else {
                        n.set(2, "__this -> __vptr -> " + n.get(2));
                    }

                    //change arguments
                    GNode arguments = GNode.create("Arguments");
                    arguments.add(GNode.create("PrimaryIdentifier", "__this"));
                    GNode oldArg = (GNode) n.get(3);;
                    n.set(3, arguments);
                    if (oldArg != null) {
                        for (Object oo : oldArg) {
                            if (!oo.equals(arguments.get(0))) {
                                arguments.add(oo);
                            }
                        }
                    }
                }
            }



            if ((select != null)&&(primaryID != null)) {

                //transform "System.out.print" to "cout"
                if (primaryID.get(0).toString().equals("System")) {

                    //with endl
                    if ((select.get(1).toString().equals("out"))&&(n.get(2).toString().equals("println"))) {
                        n.set(0, null);
                        n.set(2, "cout");
                        GNode arguments = GNode.create("Arguments");
                        GNode oldArg = (GNode) n.get(3);
                        for (Object a : oldArg) {
                            arguments.add(a);
                        }
                        arguments.add("endl");
                        n.set(3, arguments);
                    }

                    //without endl
                    if (n.get(2).toString().equals("print")) {
                        n.set(0, null);
                        n.set(2, "cout");
                    }
                }
            }

            //calling a method defined in a class
            o = n.get(0);

            if (o instanceof GNode) {

                GNode child = (GNode) o;

                if (child.hasName("SuperExpression")) {
                    String parentName = "";
                    if (extension == null) {
                        parentName = "Object";
                    } else {
                        parentName = ((GNode) NodeUtil.dfs((Node) extension, "QualifiedIdentifier")).get(0).toString();
                    }

                    n.set(0, "(new __" + parentName + "())");

                    n.set(2, "-> __vptr -> " + n.get(2).toString());

                    GNode arguments = GNode.create("Arguments");

                        arguments.add(GNode.create("Argument", "(" + parentName + ") __this"));
                        GNode oldArg = (GNode) NodeUtil.dfs(n, "Arguments");
                        n.set(3, arguments);

                        if (oldArg != null) {
                            for (Object oo : oldArg) {
                                if (!oo.equals(arguments.get(0))) {
                                    arguments.add(oo);
                                }
                            }
                        }
                    return;
                }

                else if (child.hasName("ThisExpression")) {
                    n.set(0, "__this");
                }

                if (n.get(0) != null) {

                    //start the method from vtable
                    String methodName = (String) n.get(2);
                    if (!n.get(2).toString().startsWith("-> ")) {
                        n.set(2, "-> __vptr -> " + methodName);

                        //add the object to arguments
                        GNode arguments = GNode.create("Arguments");

                        arguments.add(GNode.create("Argument", n.get(0)));
                        GNode oldArg = (GNode) NodeUtil.dfs(n, "Arguments");
                        n.set(3, arguments);

                        if (oldArg != null) {
                            for (Object oo : oldArg) {
                                if (!oo.equals(arguments.get(0))) {
                                    arguments.add(oo);
                                }
                            }
                        }
                    }
                }
            }
            visit(n);
        }

        public void traverse(Node n) {
            super.dispatch(n);
        }


        public void visit(Node n) {
            for (int i = 0; i < n.size(); ++i) {
                Object o = n.get(i);
                if (o instanceof Node) {
                    Object o1 = dispatch((Node) o);
                    if (o1 != null && o1 instanceof Node) {
                        n.set(i, o1);
                    }
                }
            }
        }

    }
}