package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import edu.nyu.oop.util.NodeUtil;

import java.util.List;
import java.util.ArrayList;

/* Building C++ style AST given a root node of a java AST */
public class Phase4 {

    /* process a list of nodes */
    public static List<GNode> process(List<GNode> l) {

        Phase4Visitor visitor = new Phase4Visitor();

        ArrayList<GNode> cppAst = new ArrayList<GNode>();
        for (Object o : l) {
            if (o instanceof Node) {
                cppAst.add((GNode) o);
            }
        }

        for (Object o : cppAst) {
            if (o instanceof Node) {
                visitor.traverse((Node) o);
            }
        }
        return cppAst;
    }

    /* process a single nodes */
    public static Node runNode(Node n) {
        Phase4Visitor visitor = new Phase4Visitor();
        visitor.traverse(n);
        return n;
    }

    /* Visitor class used to modify java AST to C++ AST */ 
    public static class Phase4Visitor extends Visitor {

        private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

        /* Visitor for Modifiers
         * Modifiers are not considered in our translator
         */
        public void visitModifiers(GNode n) {
            for (int i = 0; i < n.size(); i ++) {
                n.set(i, null);
            }
        }

        /* Visitor for Declarator
         * Using __init() function as constructor to initialize a class instance
         */
        public void visitDeclarator(GNode n) {

            for (int i = 0; i < n.size(); i++) {
                try {
                    GNode child = (GNode) n.getGeneric(i);

                    //NewClassExpression handling
                    if (child.hasName("NewClassExpression")) {

                        //Creating a node representing the "self" object
                        GNode newClass = GNode.create("NewClassExpression",
                                                      child.get(0), child.get(1), child.get(2),
                                                      GNode.create("Arguments"), child.get(4));

                        //New Arguments node
                        GNode arguments = GNode.create("Arguments");


                        //Define the new expression as calling __init();
                        GNode expression = GNode.create("Expression");
                        GNode className = (GNode) NodeUtil.dfs(child, "QualifiedIdentifier");
                        GNode oldArg = (GNode) NodeUtil.dfs(child, "Arguments");
                        arguments.add(newClass);
                        expression.add("__" + className.get(0).toString() + "::__init");
                        expression.add(arguments);

                        n.set(i, expression);

                        //Add arguments
                        for (Object o : oldArg) {
                            arguments.add(o);
                        }

                    }

                } catch (Exception e) {}
            }

            visit(n);
        }

        /* Visitor for NewClassExpression
         * Class name should start with "__"
         */
        public void visitNewClassExpression(GNode n) {
            GNode id = (GNode) NodeUtil.dfs(n, "QualifiedIdentifier");
            if (!id.get(0).toString().startsWith("__")) {
               id.set(0, "__" + id.get(0));
            }
            visit(n);
        }

        public void visitInstanceOfExpression(GNode n) {

        }

        /* Visitor for ClassDeclaration
         * Modified method name to "__class::method"
         * Adding __this argument to each method
         * Transform this() and super() called in constructors to the 
         * form by calling __init();
         */
        public void visitClassDeclaration(GNode n) {

            //collect class info
            String name = n.get(1).toString();
            Object extension = NodeUtil.dfs(n, "Extension");
            GNode body = (GNode) NodeUtil.dfs(n, "ClassBody");

            for (int i = 0; i < body.size(); i++) {
                try {
                    GNode child = body.getGeneric(i);

                    //Modify method declaration
                    if (child.hasName("MethodDeclaration")) {

                        //delete all modifiers
                        child.set(0, GNode.create("Modifiers"));
                        if (!child.get(3).toString().equals("main")) {

                            //change method names
                            child.set(3, "__" + name + "::" + child.get(3));

                            //change arguments, adding __this to arguments
                            GNode thisType = GNode.create("Type",
                                                          GNode.create("QualifiedIdentifier", name), null);
                            GNode thisParameter = GNode.create("FormalParameter",
                                                               GNode.create("Modifier"), thisType, null, "__this", null);
                            GNode param = GNode.create("FormalParameters");
                            GNode oldParam = (GNode) child.get(4);
                            param.add(thisParameter);

                            //fill in the old arguments
                            for (int j = 0; j < oldParam.size(); j++) {
                                param.add(oldParam.get(j));
                            }
                            child.set(4, param);
                        }
                    }

                    //Construtor builder
                    if (child.hasName("ConstructorDeclaration")) {

                        //C++ style __init()
                        child.set(0, GNode.create("Modifiers"));
                        child.set(1, name);
                        child.set(2, "__" + name + "::" + "__init");

                        //Arguments modify, add __this
                        GNode thisType = GNode.create("Type",
                                                      GNode.create("QualifiedIdentifier", name), null);
                        GNode thisParameter = GNode.create("FormalParameter",
                                                           GNode.create("Modifier"), thisType, null, "__this", null);
                        GNode param = GNode.create("FormalParameters");
                        GNode oldParam = (GNode) child.get(3);
                        param.add(thisParameter);

                        //fill old arguments
                        for (int j = 0; j < oldParam.size(); j++) {
                            param.add(oldParam.get(j));
                        }

                        GNode blockcontent = (GNode) NodeUtil.dfs(child, "Block");
                        blockcontent.add(GNode.create("ReturnStatement", "__this"));

                        List<Node> expressions = NodeUtil.dfsAll(child, "CallExpression");

                        for (Object o : expressions) {
                            if (o instanceof GNode) {
                                GNode expression = (GNode) o;

                                // this() calling handling
                                if (expression.get(2).toString().equals("this")) {

                                    //arguments
                                    GNode initArguments = GNode.create("Arguments");
                                    initArguments.add(GNode.create("PrimaryIdentifier", "__this"));
                                    GNode oldArguments = (GNode) expression.get(3);
                                    for (Object oo : oldArguments) {
                                        initArguments.add(oo);
                                    }

                                    //call method
                                    expression.set(2, "__" + name + "::__init");
                                    expression.set(3, initArguments);
                                } 

                                // super() calling handling
                                else if (expression.get(2).toString().equals("super")) {

                                    //arguments, add __this casted to parent class
                                    GNode initArguments = GNode.create("Arguments");
                                    String parentName;
                                    if (extension != null) {
                                        GNode parent = (GNode) extension;
                                        GNode parentType = (GNode) parent.get(0);
                                        GNode parentTypeDetail = (GNode) parentType.get(0);
                                        parentName = parentTypeDetail.get(0).toString();
                                    } else {
                                        parentName = "Object";
                                    }
                                    GNode parentTypeCast = GNode.create("Type",
                                                                        GNode.create("QualifiedIdentifier", parentName),
                                                                        null);
                                    GNode castExpression = GNode.create("CastExpression",
                                                                        parentTypeCast, GNode.create("PrimaryIdentifier", "__this"));
                                    initArguments.add(castExpression);
                                    
                                    GNode oldArguments = (GNode) expression.get(3);

                                    //modify expression
                                    expression.set(2, "__" + parentName + "::__init");
                                    expression.set(3, initArguments);

                                    //fill old arguments
                                    for (Object oo : oldArguments) {
                                        initArguments.add(oo);
                                    }
                                }
                            }
                        }

                        child.set(3, param);
                    }

                } catch(Exception e) {
                }
            }

            visit(n);
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

            boolean isMain = false;
            //main function has no field method
            if (n.get(3).toString().equals("main")) {
                n.set(2, "int");
                //cannot handling array now
                n.set(4, GNode.create("Arguments", GNode.create("VoidType")));
                GNode blockinside = (GNode) NodeUtil.dfs(n, "Block");
                visit(n);
                isMain = true;
                GNode newBlock = GNode.create("Block");
                for (Object o : blockinside) {
                    newBlock.add(o);
                }
                newBlock.add(GNode.create("ReturnStatement", "0"));
                
            }

            //"System" is recognizable
            ArrayList<String> paramsList = new ArrayList<String>();
            paramsList.add("System");

            //Parameters are defined in this method
            Object o = NodeUtil.dfs(n, "FormalParameters");
            if (o != null) {
                GNode params = (GNode) o;

                for (int i = 0; i < params.size(); i++) {
                    try {
                        GNode param = params.getGeneric(i);
                        paramsList.add(param.get(3).toString());
                    } catch (Exception e) {

                    }
                }
            }

            visit(n);//actual work is done here

            GNode block = (GNode) NodeUtil.dfs(n, "Block");
            List<Node> toCheck = NodeUtil.dfsAll(block, "PrimaryIdentifier");
            for (Node id : toCheck) {

                //if a variable is defined in the parameters, it is not a class field
                if (id.get(0).toString().startsWith("__this -> ")) {
                    String variableName = id.get(0).toString().substring(10);
                    if (paramsList.contains(variableName)) {
                        id.set(0, variableName);
                    }
                    else if (isMain) {
                        id.set(0, variableName);
                    }
                }

                //those defined inside the method are safe
                else if (id.get(0).toString().startsWith("in this method ")) {
                    id.set(0, id.get(0).toString().substring(15));
                }
            }
        }

        /* Visitor for ConstructorDeclaration
         * variable check is same as MethodDeclaration
         */
        public void visitConstructorDeclaration(GNode n) {
            visitMethodDeclaration(n);
        }

        /* Visitor for Block
         * Check whether the variables inside the scope are defined
         * Can also be done using symble table (similar idea here)
         */
        public void visitBlock(GNode n) {

            ArrayList<String> paramsList = new ArrayList<String>();
            paramsList.add("System");

            visit(n);

            for (int i = 0; i < n.size(); i++) {

                //Add declared variables to the list
                try {
                    GNode child = n.getGeneric(i);
                    if (child.hasName("FieldDeclaration")) {

                        GNode typeOfField = (GNode) NodeUtil.dfs(child, "QualifiedIdentifier");
                        String typeName = typeOfField.get(0).toString();

                        GNode declarators = (GNode) NodeUtil.dfs(child, "Declarators");
                        for (int j = 0; j < declarators.size(); j++) {
                            try {
                                GNode d = declarators.getGeneric(j);
                                if (d.hasName("Declarator")) {
                                    d.set(1, "(" + typeName + ") ");
                                    paramsList.add(d.get(0).toString());
                                }
                            } catch (Exception e) {}
                        }
                    }

                    List<Node> toCheck = NodeUtil.dfsAll(child, "PrimaryIdentifier");

                    for (Node id : toCheck) {

                        //A inner scope unrecognizable variable can be defined here
                        if (id.get(0).toString().startsWith("__this -> ")) {
                            String variableName = id.get(0).toString().substring(10);
                            if (paramsList.contains(variableName)) {
                                id.set(0, variableName);
                            }

                        }
                        //Inner scope defined variables are safe 
                        else if (id.get(0).toString().startsWith("in this method ")) {}
                        //If a variable is used but not defined, it must be defined
                        //outside the scope
                        else if (!paramsList.contains(id.get(0).toString())) {
                            id.set(0, "__this -> " + id.get(0).toString());
                        } 
                        //Defined in this scope, safe
                        else if (paramsList.contains(id.get(0).toString())) {
                            id.set(0, "in this method " + id.get(0).toString());
                        }
                    }

                } 
                catch (Exception e) {}
            }
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

        /* Visitor for Selection Expression
         * C++ style "->" for calling class methods
         */
        public void visitCallExpression(GNode n) {

            //collect info
            Object o = NodeUtil.dfs(n, "SelectionExpression");
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

            if ((select != null)&&(primaryID != null)) {

                //transform "System.out.print" to "cout"
                if (select.get(1).toString().equals("out")&&primaryID.get(0).toString().equals("System")) {
                    
                    //with endl
                    if (n.get(2).toString().equals("println")) {
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
                if (child != null) {

                    //start the method from vtable
                    if (!n.get(2).toString().startsWith("-> ")){
                        n.set(2, "-> __vptr -> " + n.get(2).toString());
                    
                        //add the object to arguments
                        GNode arguments = GNode.create("Arguments");

                        arguments.add(child);
                        GNode oldArg = (GNode) NodeUtil.dfs(n, "Arguments");
                        n.set(3, arguments);
                        if (oldArg != null) {
                            for (Object oo : oldArg) {
                                if (!oo.equals(arguments.get(0))){
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
            for (Object o : n) if (o instanceof Node) dispatch((Node) o);
        }

    }
}