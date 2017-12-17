/**
 * Phase 4, traverses Java AST obtained from Phase 1 and muates the AST
 * to obtain a C++ AST, there are multiple cases of conditional logic
 * in a single visit, this is necessary since we check for certain conditions
 * before mutation happens, please check each method to get more details
 *
 * @author Shenghao Lin
 * @author Goktug Saatcioglu
 *
 * @version 2.0
 */

package edu.nyu.oop;

import org.slf4j.Logger;

import xtc.lang.JavaEntities;
import xtc.tree.Node;
import xtc.tree.GNode;
import xtc.tree.Visitor;
import xtc.type.*;
import xtc.util.SymbolTable;
import xtc.util.Runtime;

import edu.nyu.oop.util.NodeUtil;
import edu.nyu.oop.util.SymbolTableBuilder;
import edu.nyu.oop.util.SymbolTableUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Stack;

/* Building C++ style AST given a root node of a java AST */
public class Phase4 {

    private Runtime runtime;
    private HashMap<String, String> childrenToParents = new HashMap<String, String>();
    private HashMap<String, ArrayList<Phase1.Initializer>> inits = new HashMap<String, ArrayList<Phase1.Initializer>>();
    private HashMap<String, ArrayList<Phase1.Initializer>> formerInits = new HashMap<String, ArrayList<Phase1.Initializer>>();

    // public ArrayList<BigArray> bigArrays = new ArrayList<BigArray>();  // no longer needed
    public ArrayList<PrimitiveArray> primitiveArrays = new ArrayList<PrimitiveArray>();

    public Phase4(Runtime runtime) {
        this.runtime = runtime;
    }

    public Phase4(Runtime runtime, HashMap<String, String> childrenToParents, HashMap<String, ArrayList<Phase1.Initializer>> inits) {
        this.runtime = runtime;
        this.childrenToParents = childrenToParents;
        this.inits = inits;

        // copy current inits into former inits
        for (String s : this.inits.keySet()) {
            ArrayList<Phase1.Initializer> tmpInit = new ArrayList<Phase1.Initializer>();
            ArrayList<Phase1.Initializer> init = this.inits.get(s);
            for (int i = 0; i < init.size(); i ++) tmpInit.add(init.get(i));
            this.formerInits.put(s, tmpInit);
        }

        // we need to resolve all initializers
        resolveInitializers();
    }

    /** 
     * Helper method: resolveInitializers
     * resolves all inheritance issue with all fields
     * determines if field is initialized to a different value
     * uses childrenToParents to accomplish this
     *
     */
    public void resolveInitializers() {

        for (String key : childrenToParents.keySet()) {

            // use a stack to resolve all initializations
            String toUpdate = key;
            Stack<String> stack = new Stack<String>();
            stack.push(key);
            String temp = key;

            // find all parents, push to stack
            while (!childrenToParents.get(temp).equals("")) {
                stack.push(childrenToParents.get(temp));
                temp = childrenToParents.get(temp);
            }

            ArrayList<Phase1.Initializer> tmpInit = inits.get(stack.pop());
            ArrayList<Phase1.Initializer> start = new ArrayList<Phase1.Initializer>();

            if (tmpInit != null) {
                for (int i = 0; i < tmpInit.size(); i ++) start.add(tmpInit.get(i));
            }

            // empty the stack
            while (!stack.empty()) {

                // resolve initializations
                ArrayList<Phase1.Initializer> tempList = inits.get(stack.pop());
                for (Phase1.Initializer elem : tempList) {
                    if (!elem.isStatic) {
                        boolean addFlag = true;
                        for (int i = 0; i < start.size(); i++) {
                            if (elem.name.equals(start.get(i).name) && elem.typeName.equals(start.get(i).typeName)) {
                                start.set(i, elem);
                                addFlag = false;
                            }
                        }
                        if (addFlag) start.add(elem);
                    }
                }
            }

            inits.put(toUpdate, start);
        }
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
                Phase4Visitor visitor = new Phase4Visitor(table, runtime, formerInits, childrenToParents, inits);
                visitor.traverse((Node) o);

                String info = "";
                for (PrimitiveArray p : visitor.primitiveArrays) {
                    info += p.dump();
                }
                if (!info.equals("")) {
                    ((Node) o).setProperty("RuntimeInfo", info);
                }
            }
        }

        return cppAst;
    }

    /* process a single node */
    public Node runNode(Node n, SymbolTable table) {

        //run the visitor
        Phase4Visitor visitor = new Phase4Visitor(table, runtime, formerInits, childrenToParents, inits);
        visitor.traverse(n);

        //add primitive array info
        String info = "";
        for (PrimitiveArray p : visitor.primitiveArrays) {
            info += p.dump();
        }
        if (!info.equals("")) {
            System.out.println(info);
            n.setProperty("RuntimeInfo", info);
        }

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
        private HashMap<String, ArrayList<Phase1.Initializer>> completedInits;
        private HashMap<String, ArrayList<Phase1.Initializer>> formerInits;        
        private boolean defaultConstructorNeeded = false;

        private SymbolTable table;

        public ArrayList<BigArray> bigArrays = new ArrayList<BigArray>();
        public ArrayList<PrimitiveArray> primitiveArrays = new ArrayList<PrimitiveArray>();

        public Phase4Visitor(SymbolTable table, Runtime runtime, HashMap<String, ArrayList<Phase1.Initializer>> formerInits,
            HashMap<String, String> ctp, HashMap<String, ArrayList<Phase1.Initializer>> completedInits) {
            this.table = table;
            this.runtime = runtime;
            this.ctp = ctp;
            this.completedInits = completedInits;
            this.formerInits = formerInits;
        }

       /** 
         * Helper method: toCppType
         * 
         * @param  type a String that describes a Java type
         * 
         * @return the conversion from Java type to C++ type
         */
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

        /** 
         * Helper method: isPrimitiveType
         * 
         * @param  type a String that describes a Java type
         * 
         * @return true if its a Java primitive, false otherwise
         */
        public boolean isPrimitiveType(String type) {
            if (type.equals("boolean") || type.equals("byte") || type.equals("char") ||
                    type.equals("short") || type.equals("int") || type.equals("long") ||
                    type.equals("float") || type.equals("double")) {
                return true;
            }
            return false;
        }

        /** 
         * Visitor for CompilationUnit
         * process package information and use symboltable
         * visit nodes for mutation
         *
         */
        public void visitCompilationUnit(GNode n) {

            String packageInfo = "";
            GNode p = (GNode) n.getGeneric(0);
            GNode packageName = (GNode) p.getGeneric(1);
            for (int i = 0; i < packageName.size(); i ++) packageInfo += packageName.get(i).toString() + ".";
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

        /** 
         * Visitor for Modifiers
         * simply remove them as they are not part of the translation schema
         *
         */
        public void visitModifiers(GNode n) {
            for (int i = 0; i < n.size(); i ++) {
                if (n.getNode(i).getString(0).equals("static")) {
                    n.set(i, GNode.create("StaticModifer", null));
                } else {
                    n.set(i, null);
                }

            }
            visit(n);
        }

        /** 
         * Visitor for Cast Expression
         * performs correct run-time casting check, therefore correct casts
         *
         */
        public void visitCastExpression(GNode n) {

            if (!n.getNode(0).getName().equals("JavaCast")) {

                String castStatement = "__rt::java_cast<";
                String toCast = n.getNode(0).getNode(0).get(0).toString();
                n.setProperty("CastType", toCast);
                castStatement += toCast + ">";
                GNode castNode = GNode.create("JavaCast", castStatement);
                n.set(0, castNode);
            }

            visit(n);
        }

        /**
         * Visitor for ClassDeclaration
         * processes constructor declarations including defaults constructor,
         * field initializations, getClass, and static initializations
         *
         */
        public void visitClassDeclaration(GNode n) {

            SymbolTableUtil.enterScope(table, n);
            table.mark(n);

            // at this point a default constructor is needed
            defaultConstructorNeeded = true;

            //collect class info
            currentClass = (String) n.get(1).toString();
            extension = NodeUtil.dfs(n, "Extension");

            // get parent information, if no parent then parent i Object
            String parentName = "";
            if (extension != null) {
                GNode parentType = (GNode) ((GNode) extension).getNode(0);
                GNode parentTypeNode = (GNode) parentType.getNode(0);
                parentName = parentTypeNode.get(0).toString();
            }
            else parentName = "Object";

            // create a default constructor, add default initializations into it
            String defaultConstructor = "__" + currentClass + "::__" + currentClass + "() : ";
            ArrayList<Phase1.Initializer> initializers = completedInits.get(currentClass);
            for (Phase1.Initializer init: initializers) {
                if (!init.isStatic) defaultConstructor += init.name + "(" + init.initial + "), ";
            }
            defaultConstructor += "__vptr(&__vtable) {}";
            n.setProperty("defaultConstructor", defaultConstructor);

            // also determine information for getClass
            String classInfo = "";
            classInfo += "Class __" + n.get(1).toString() + "::__class() {\n";
            classInfo += "static Class k = new __Class(__rt::literal(\""
                         + this.packageInfo + currentClass + "\"), __"
                         + parentName + "::__class());\n";
            classInfo += "return k;\n";
            classInfo += "}\n";
            n.setProperty("classInfo", classInfo);

            // determine v-table initilazation, this is just default implementation
            String vtableInit = "__" + currentClass + "_VT __" + currentClass
                                + "::__vtable;\n";
            n.setProperty("vtableInit", vtableInit);

            visit(n);

            // if we need a default constructor
            if (defaultConstructorNeeded) {
                String initCall = "";
                initCall += currentClass + " __"
                            + currentClass + "::__init("
                            + currentClass + " __this) {\n";

                // String parentName = "";
                // if (extension == null) parentName = "Object";
                // else parentName = ((GNode) NodeUtil.dfs((Node) extension, "QualifiedIdentifier")).get(0).toString();

                initCall += "__" + parentName + "::__init(__this);\n";

                initializers = formerInits.get(currentClass);
                for (Phase1.Initializer init: initializers) {
                    if (!init.value.equals("") && !init.isStatic)
                        initCall += "__this -> " + init.name + " = " + init.value + ";\n";
                }
                initCall += "return __this;\n";
                initCall += "}\n";
                n.setProperty("realDefaultConstructor", initCall);
            }

            // process static initialazations
            String staticInit = "";
            initializers = formerInits.get(currentClass);
            for (Phase1.Initializer init : initializers) {
                if (init.isStatic) {
                    if (init.value.equals("")) 
                        staticInit += init.typeName + " __" + currentClass + "::" + init.name + " = " + init.initial + ";\n";
                    else
                        staticInit += init.typeName + " __" + currentClass + "::" + init.name + " = " + init.value + ";\n";
                }
            }

            // if there are static initialzations add them in
            if (!staticInit.equals("")) n.setProperty("staticInit", staticInit);

            // reset extension and currentClass
            extension = null;
            currentClass = null;

            SymbolTableUtil.exitScope(table, n);
        }

        /** 
         * Visitor for Method Declaration
         * Checking whether variables inside this scope are defined in
         * the class field or inside the method and then translates method 
         * declarations to correct C++ declarations along with its mangled
         * name
         *
         */
        public void visitMethodDeclaration(GNode n) {

            SymbolTableUtil.enterScope(table, n);
            table.mark(n);

            methodName = table.current().getName();
            // process the mangled name correctly
            if (n.getProperty("mangledName") != null) n.set(3, n.getProperty("mangledName").toString().replace(" ", ""));

            // process main function
            if (n.get(3).toString().equals("main")) {

                isMain = true;

                //cpp style main
                n.set(2, "int32_t");
                n.set(3, "__" + currentClass + "::" + n.get(3).toString());
                visit(n);

                //add return s
                GNode blockContent = (GNode) NodeUtil.dfs(n, "Block");
                GNode newBlock = GNode.create("Block");
                for (Object o: blockContent) newBlock.add(o);
                newBlock.add(GNode.create("ReturnStatement", "0"));
                n.set(7, newBlock);

                // not in main anymore
                isMain = false;
            }

            // process anything else
            else {

                // set constructorFlag to false
                constructorFlag = false;
                visit(n);

                // process constructor
                if (n.get(3).toString().equals(currentClass)) {

                    // default constructor is not needed anymore
                    defaultConstructorNeeded = false;
                    //n.set(0, GNode.create("Modifiers"));
                    n.set(2, currentClass);
                    n.set(3, "__" + currentClass + "::" + "__init");
                    GNode blockContent = (GNode) NodeUtil.dfs(n, "Block");
                    GNode newBlock = GNode.create("Block");

                    // if not constructor flag, process initialization statements (if any)
                    if (!constructorFlag) {

                        String parentName = "";
                        if (extension == null) parentName = "Object";
                        else parentName = ((GNode) NodeUtil.dfs((Node) extension, "QualifiedIdentifier")).get(0).toString();

                        newBlock.add(GNode.create("Statement", "__" + parentName + "::__init(__this);\n"));
                        
                        String initStatements = "";
                        ArrayList<Phase1.Initializer> initializers = formerInits.get(currentClass);
                        for (Phase1.Initializer init: initializers) {
                            if (!init.value.equals("") && !init.isStatic)
                                initStatements += "__this -> " + init.name + " = " + init.value + ";\n";
                        }
                        newBlock.add(GNode.create("Statement", initStatements));
                    }

                    // if block isn't null, dump block into newBlock
                    if (blockContent != null) {
                        for (Object o : blockContent) newBlock.add(o);
                    }

                    newBlock.add(GNode.create("ReturnStatement", "__this"));
                    n.set(7, newBlock);
                }

                //normal methods
                else {
                    //n.set(0, GNode.create("Modifiers"));
                    // method declaration
                    n.set(3, "__" + currentClass + "::" + n.get(3).toString());
                }

                // check if method is static
                Object staticCheck = NodeUtil.dfs(n, "StaticModifer");
                if (staticCheck == null) {

                    //Arguments modify, add __this
                    GNode thisType = GNode.create("Type", GNode.create("QualifiedIdentifier", currentClass), null);
                    GNode thisParameter = GNode.create("FormalParameter",  GNode.create("Modifier"), thisType, null, "__this", null);
                    GNode newParams = GNode.create("FormalParameters");
                    GNode oldParams = (GNode) n.get(4);
                    newParams.add(thisParameter);
                    //fill old arguments
                    for (int j = 0; j < oldParams.size(); j++) newParams.add(oldParams.get(j));
                    n.set(4, newParams);
                }
            }

            // reset the method name and exit scope
            methodName = "";
            SymbolTableUtil.exitScope(table, n);
        }

        /**
         * Visitor for ThisExpression
         * Explicates "this" when "this" is used
         *
         */
        public void visitThisExpression(GNode n) {
            n.set(0, "__this");
            visit(n);
        }

        /**
         * Visitor for Type
         * Visits Java types and converts them to c++ types
         * if type if primitive it gets converted
         * if type is type array 
         *
         */
        public void visitType(GNode n) {

            GNode dimensions = (GNode) NodeUtil.dfs(n, "Dimensions");
            String declaration = "";

            // if type array
            if (dimensions != null) {
                String typeName = n.getNode(0).get(0).toString();
                if (isPrimitiveType(typeName)) {
                    typeName = toCppType(typeName);
                    // if its not a primitive array we should create a run time node for it
                    if (!typeName.equals("int32_t")) primitiveArrays.add(new PrimitiveArray(typeName));
                    // else bigArrays.add(new BigArray(typeName, packageInfo)); // big array no longer needed
                }

                // process declaration using expansion
                for (int i = dimensions.size() - 1; i > -1; i--) {
                    if (i == dimensions.size() - 1) declaration = "__rt::Array<" + typeName + ">";
                    else declaration = "__rt::Array<" + declaration + ">";
                }
                GNode arrNode = GNode.create("QualifiedIdentifier", declaration);
                n.set(0, arrNode);
                n.set(1, null);
            }

            visit(n);
        }

        /**
         * Visitor for PrimitiveType
         * Visits Java primitives and converts them to c++ primitives
         *
         */
        public void visitPrimitiveType(GNode n) {
            String typeName = toCppType(n.get(0).toString());
            n.set(0, typeName);
            visit(n);
        }

        /**
         * Visitor for NewArrayExpression
         * When a new array is created, it visits that node and processes the creation
         * Supports nested array declarations
         *
         */
        public void visitNewArrayExpression(GNode n) {

            GNode dimensions = (GNode) n.getNode(2);
            String declaration = "";
            String typeDef = "";
            String length = "";

            // if dimensions are not null, then the array has been defined for its length
            if (dimensions == null) {

                // conversion of java primitive to cpp primitive
                String typeName = n.getNode(0).get(0).toString();
                if (isPrimitiveType(typeName)) {
                    typeName = toCppType(typeName);
                }

                // get the concreteDimensions
                GNode concreteDimensions = (GNode) n.getNode(1);

                length = n.getNode(1).getNode(0).get(0).toString();

                String innerDef = "";

                // supports nested definition via expansion of typeDef
                for (int i = concreteDimensions.size() - 1; i > -1; i--) {
                    if (i == concreteDimensions.size() - 1) typeDef = typeName;

                    else
                    { 
                        //Type name recursive update
                        typeDef = "__rt::Array<" + typeDef + ">";

                        //Initialization of nested arrays(using for loop to initialize the inner arrays)
                        String forDef = "for (int32_t i" + i + " = 0; i" + i + " < " + 
                            concreteDimensions.getNode(i).getString(0) + "; i" + i + "++) {\n";
                        String initStatement = "tmp";

                        for (int j = 0; j < i + 1; j ++) {
                            initStatement += " -> __data[i" + j + "]";
                        } 

                        initStatement += " = __rt::__Array<" + typeDef + ">::__init(new __rt::__Array<" 
                            + typeDef + ">(__rt::checkNegativeIndex(" + concreteDimensions.getNode(i + 1).getString(0) + ")));\n";
                        innerDef = forDef + initStatement + innerDef + "}\n";
                    }
                }

                n.setProperty("InitSubArray", innerDef);
                n.setProperty("ArrayType", "__rt::Array<" + typeDef + ">");

                //c++ array initialization
                declaration = "__rt::__Array<" + typeDef + ">::__init(new __rt::__Array<" + typeDef + ">(__rt::checkNegativeIndex(" + length + ")))";
            
            }

            // create an ArrayExpression node using declaration
            n.set(3, GNode.create("ArrayExpression", declaration));

            n.set(0, null);
            n.set(1, null);
            visit(n);
        }

        /**
         * Visitor for BlockDeclaration
         * Marks a scope in the symbol table and then continues visiting
         *
         */
        public void visitBlockDeclaration(GNode n) {

            SymbolTableUtil.enterScope(table, n);
            table.mark(n);
            visit(n);
            SymbolTableUtil.exitScope(table, n);
        }

        /**
         * Visitor for NewClassExpression
         * Class name should start with "__"
         * Processes a new class creation accordingly and initializes the class
         *
         */
        public void visitNewClassExpression(GNode n) {

            GNode id = (GNode) NodeUtil.dfs(n, "QualifiedIdentifier");
            if (!id.get(0).toString().startsWith("__")) {
                Node oldArgs = n.getNode(3);
                GNode newArgs = GNode.create("Arguments");
                n.set(3, newArgs);
                newArgs.add(GNode.create("Argument", "new __" + id.get(0).toString() + "()"));
                for (int j = 0; j < oldArgs.size(); j++) newArgs.add(oldArgs.get(j));
                id.set(0, "__" + id.get(0) + "::__init");
            }
            visit(n);
        }

        /**
         * Visitor for Block
         * Marks a scope in the symbol table and then continues visiting
         *
         */
        public void visitBlock(GNode n) {

            SymbolTableUtil.enterScope(table, n);
            table.mark(n);
            visit(n);
            SymbolTableUtil.exitScope(table, n);
        }

        /**
         * Check whether the variables inside the scope are defined
         * Marks a scope in the symbol table and then continues visiting
         *
         */
        public void visitPrimaryIdentifier(GNode n) {

            if (n.get(0).toString().contains("__this") || n.getString(0).equals("System")) return;

            //add this to field data
            if (!isMain) {
                if (!table.current().getParent().isDefinedLocally(n.get(0).toString())) {
                    n.set(0, "__this -> " + n.get(0).toString());
                }
            }

            visit(n);
        }

        /** 
         * Visitor for Subscript Expression
         *
         * Implies there is some array processing happening
         * If it is not check by checkStore,
         * Do arrayAccessCheck before accessing happens
         *
         */
        public void visitSubscriptExpression(GNode n) {

            visit(n);

            // if there is an array store
            if (null == n.getProperty("Store")) {
                n.setProperty("Store", "Access");
                String check = "__rt::arrayAccessCheck";
                n.setProperty("AccessCheck", check);
            }
        }

        /** 
         * Visitor for Selection Expression
         *
         * This method visits an Selection Expression and translates dynamic accesses
         * correctly, for array it will check not null, if it finds a static variable
         * call it will translate it correctly, and finally it will add -> operators 
         * for anything else
         *
         */
        public void visitSelectionExpression(GNode n) {

            // retrieve primary identifier
            String primaryIdentifier = "";
            Object primaryIdentifierObj = NodeUtil.dfs(n, "PrimaryIdentifier");

            // if no primary identifier was found using dfs
            if (primaryIdentifierObj != null) primaryIdentifier = ((GNode) primaryIdentifierObj).get(0).toString();

            // check if its a statement in the completedInits hashmap, process static variable
            if (completedInits.keySet().contains(primaryIdentifier)) {
                GNode primaryIdentifierNode = (GNode) n.get(0);
                primaryIdentifierNode.set(0, "__" + primaryIdentifierNode.get(0).toString());
                n.set(1, "::" + n.get(1).toString());
            } 

            // check if length of array is being called, process with null check
            else if (n.get(1).toString().equals("length") && primaryIdentifierObj != null) {
                n.setProperty("Block", "Array length");
                GNode primaryIdentifierNode = (GNode) n.get(0);
                n.setProperty("Check", "__rt::checkNotNull");
                String length = "-> length;" ;
                n.set(1, length);
            } 

            // convert all string accesses 
            else {
                for (int i = 1; i < n.size(); i++) {
                    if (n.get(i) instanceof String) {
                        if (!n.get(i).toString().startsWith("-> ")) {
                            n.set(i, "-> " + n.get(i).toString());
                        }
                    }
                }
            }

            visit(n);
        }

        /**
         * Visitor for For Statement
         *
         * Mark it in the symbol table
         *
         */
        public void visitForStatement(GNode n) {
            SymbolTableUtil.enterScope(table, n);
            table.mark(n);
            visit(n);
            SymbolTableUtil.exitScope(table, n);
        }

        /**
         * Visitor for Expression Statement
         *
         * This method visits an Expression Statement and checks if a store on an array is being made
         * If this is the case we need to check whether an array assignment is taking place and do
         * the necessary checks.
         *
         * For primitive arrays, we only do checkIndex and checkNotNull, as checkArrayStore is not
         * applicable to primitive types.
         *
         */
        public void visitExpressionStatement(GNode n) {

            // retreieve the expressionNode from the expression statement
            Node expressionNode = n.getNode(0);

            // if the node is an expression
            if (expressionNode.hasName("Expression")) {
                // if the node has a subscript expression and has an assignment operator (aka =), translate array stores properly
                if (expressionNode.getNode(0).hasName("SubscriptExpression") && expressionNode.getString(1).equals("=")) {

                    //get the setTo value
                    Node setTo = expressionNode.getNode(2);
                    boolean isPrimitive = true;

                    //find if the setTo is of class types
                    if (setTo.hasName("PrimaryIdentifier")) {
                        Type toType = ((VariableT) table.current().lookup(setTo.get(0).toString())).getType();
                        if (toType.tag().equals("CLASS")) {
                            isPrimitive = false;
                        }
                    }

                    if (setTo.hasName("CallExpression")) {
                        if (((Type) setTo.getProperty("methodReturnType")).tag().equals("CLASS")) {
                            isPrimitive = false;
                        }
                    }

                    if (setTo.hasName("NewClassExpression")) {
                        isPrimitive = false;
                    }

                    if (setTo.hasName("NewArrayExpression")) {
                        isPrimitive = false;
                    }

                    //__rt::checkNotNull does not apply for primitive types
                    //so arrayAccessCheck is used here
                    if (isPrimitive) {
                        expressionNode.getNode(0).setProperty("Store", "Store");
                        GNode newBlock = GNode.create("ExpressionBlock");

                        // add run-time checks for arrays
                        GNode check = GNode.create("Check");
                        check.add("__rt::arrayAccessCheck");
                        check.add(expressionNode.getNode(0).getNode(0));
                        check.add(expressionNode.getNode(0).getNode(1));

                        // create real expression
                        GNode realExpression = GNode.create("realExpression", expressionNode.getNode(0), "=", expressionNode.getNode(2));

                        // add everything to new block
                        newBlock.add(check);
                        newBlock.add(realExpression);

                        n.set(0, newBlock);
                    }

                    //use wrapped checkStore for class types
                    else
                    {
                        expressionNode.getNode(0).setProperty("Store", "Store");

                        // new block along with tmp node for processing
                        GNode newBlock = GNode.create("ExpressionBlock");
                        GNode tmpDef = GNode.create("ExpressionStatement",
                                                    GNode.create("DefExpression", "Object tmp", "=", expressionNode.getNode(2)));

                        // add run-time checks for arrays
                        GNode check = GNode.create("Check");
                        check.add("__rt::arrayStoreCheck");
                        check.add(expressionNode.getNode(0).getNode(0));
                        check.add(expressionNode.getNode(0).getNode(1));
                        check.add(GNode.create("PrimaryIdentifier", "tmp"));

                        // create real expression
                        GNode realExpression = GNode.create("realExpression", expressionNode.getNode(0), "=", GNode.create("PrimaryIdentifier", "tmp"));

                        // add everything to new block
                        newBlock.add(tmpDef);
                        newBlock.add(check);
                        newBlock.add(realExpression);

                        n.set(0, newBlock);
                    }
                }
            }

            visit(n);
        }


        /**
         * Visitor for Call Expression
         *
         * Performs the bulk of the translation
         * Translates print statements
         * Translates method calls
         * Translates calls to super and init
         * Adds run-time checks if necessary
         * 
         */
        public void visitCallExpression(GNode n) {

            visit(n);

            //collect information to see if SelectionStatement and PrimaryIdentifier nodes exist
            Object obj = n.getNode(0);
            GNode selectionStatementNode = null;
            GNode primaryIdentifierNode = null;

            // flag that checks if translation for a specific callExpression is done or not
            boolean undone = true;

            // make sure obj is not null, if it isn't then SS is obj and PI is found using dfs
            if (obj != null) {
                selectionStatementNode = (GNode) obj;
                obj = NodeUtil.dfs(selectionStatementNode, "PrimaryIdentifier");
                if (obj != null) primaryIdentifierNode = (GNode) obj;
            }

            // fix mangled name removing placeholder
            if (n.getProperty("mangledName") != null) n.set(2, n.getProperty("mangledName").toString().replace(" ", ""));

            // if method is not in main, no run time checks plus different conditionals
            if (!isMain) {

                if (selectionStatementNode == null) {

                    n.setProperty("noblock", "init");

                    undone = false;

                    // this call, nothing to change other than specificying which init is being called, set constructor flag
                    if (n.get(2).toString().equals("this")) {

                        constructorFlag = true;
                        n.set(2, "__" + currentClass + "::__init");

                        // when this() is called, the initialization for data fields have been done
                        // String initStatements= "";
                        // ArrayList<Phase1.Initializer> initializers = completedInits.get(currentClass);
                        // for (Phase1.Initializer init: initializers) {
                        //     if (!init.value.equals("") && !init.isStatic)
                        //         initStatements += "__this -> " + init.name + " + " + init.value + ";\n";
                        // }
                        // n.setProperty("initStatements", initStatements);
                    }

                    // super call, call expression is done, also set constructor flag
                    else if (n.get(2).toString().equals("super")) {

                        n.setProperty("noblock", "init");
                        constructorFlag = true;

                        // find parent, if it does have one
                        String parentName = "";
                        if (extension == null) parentName = "Object";
                        else parentName = ((GNode) NodeUtil.dfs((Node) extension, "QualifiedIdentifier")).get(0).toString();

                        // rename the node appropriatly
                        n.set(2, "__" + parentName + "::__init");

                        // determine any other initialization that are necessary
                        String initStatements = "";
                        ArrayList<Phase1.Initializer> initializers = formerInits.get(currentClass);
                        for (Phase1.Initializer init: initializers) {
                            if (!init.value.equals("") && !init.isStatic) initStatements += "__this -> " + init.name + " = " + init.value + ";\n";
                        }

                        // add initStatements in as a property
                        n.setProperty("initStatements", initStatements);
                    } 

                    else n.set(2, "__this -> __vptr -> " + n.get(2));

                    // update arguments accordingly
                    GNode newArgs = GNode.create("Arguments");
                    newArgs.add(GNode.create("ThisExpression", "__this"));
                    GNode oldArgs = (GNode) n.get(3);;
                    n.set(3, newArgs);
                    if (oldArgs != null) {
                        for (Object oldArg: oldArgs) {
                            if (!oldArg.equals(newArgs.get(0)) && oldArg != null ) newArgs.add(oldArg);
                        }
                    }
                }
            }

            // make sure both nodes are not null since they are needed for translation below
            if (selectionStatementNode != null && primaryIdentifierNode != null) {

                // translate System.out.(print/println) if it happens
                if (primaryIdentifierNode.get(0).toString().equals("System")) {

                    // print statement with println, aka endl
                    if (selectionStatementNode.get(1).toString().equals("-> out") && n.get(2).toString().equals("println")) {

                        undone = false;
                        n.setProperty("noblock", "cout");
                        n.setProperty("cout", "cout");
                        n.set(0, null);
                        n.set(2, "std::cout");
                        GNode newArgs = GNode.create("Arguments");
                        GNode oldArgs = (GNode) n.get(3);
                        for (Object oldArg : oldArgs) newArgs.add(oldArg);
                        newArgs.add("std::endl");
                        n.set(3, newArgs);
                    }

                    // print statement with print, aka just cout without endl
                    if (selectionStatementNode.get(1).toString().equals("-> out") && n.get(2).toString().equals("print")) {

                        undone = false;
                        n.setProperty("noblock", "cout");
                        n.setProperty("cout", "cout");
                        n.set(0, null);
                        n.set(2, "std::cout");
                    }
                }
            }

            //calling a method defined in a class
            obj = n.get(0);

            // making sure it is still a GNode
            if (obj instanceof GNode) {

                // cast to child
                GNode child = (GNode) obj;

                // if call to super
                if (child.hasName("SuperExpression")) {

                    String parentName = "";
                    if (extension == null) parentName = "Object";
                    else parentName = ((GNode) NodeUtil.dfs((Node) extension, "QualifiedIdentifier")).get(0).toString();

                    n.set(0, "(new __" + parentName + "())");
                    n.set(2, "-> __vptr -> " + n.get(2).toString());

                    // add this to arguments, update arguments
                    GNode newArgs = GNode.create("Arguments");
                    newArgs.add(GNode.create("Argument", "__this"));
                    GNode oldArgs = (GNode) NodeUtil.dfs(n, "Arguments");
                    n.set(3, newArgs);

                    if (oldArgs != null) {
                        for (Object oldArg : oldArgs) {
                            if (oldArg.equals(newArgs.get(0)) && oldArg != null) newArgs.add(oldArg);
                        }
                    }
                    
                    return;
                }

                // neccessary null check
                if (n.get(0) != null) {

                    //start the method from vtable
                    String methodName = (String) n.get(2);

                    if (null != n.getProperty("methodDispatchType")) {

                        // dispatch type is virtual, therefore use dynamic dispath
                        if (n.getProperty("methodDispatchType").toString().equals("virtual")) {

                            n.set(2, "-> __vptr -> " + methodName);

                            // create new argument but also add this (which is tmp)
                            GNode newArgs = GNode.create("Arguments");
                            newArgs.add(GNode.create("PrimaryIdentifier", "tmp"));
                            GNode oldArgs = (GNode) n.getNode(3);
                            n.set(3, newArgs);
                            if (oldArgs != null) {
                                for (Object oldArg : oldArgs) {
                                    if (!oldArg.equals(newArgs.get(0)) && oldArg != null) newArgs.add(oldArg);
                                }
                            }
                        }

                        // dispatch type is private, therefore use . method to access method
                        else if (n.getProperty("methodDispatchType").toString().equals("private")) {

                            n.set(2, "." + methodName);

                            // create new argument but also add this (which is tmp)
                            GNode newArgs = GNode.create("Arguments");
                            newArgs.add(GNode.create("PrimaryIdentifier", "tmp"));
                            GNode oldArgs = (GNode) n.getNode(3);
                            n.set(3, newArgs);
                            if (oldArgs != null) {
                                for (Object oldArg : oldArgs) {
                                    if (!oldArg.equals(newArgs.get(0)) && oldArg != null) newArgs.add(oldArg);
                                }
                            }
                        }

                        // dispatch type is static, no need to pass this
                        else if (n.getProperty("methodDispatchType").toString().equals("static")) {
                            n.set(2, "::" + methodName);
                        }
                    }

                    else {

                    n.set(2, "-> __vptr -> " + methodName);

                    // create new argument but also add this (which is tmp)
                    GNode newArgs = GNode.create("Arguments");
                    newArgs.add(GNode.create("PrimaryIdentifier", "tmp"));
                    GNode oldArgs = (GNode) n.getNode(3);
                    n.set(3, newArgs);
                    if (oldArgs != null) {
                        for (Object oldArg : oldArgs) {
                            if (!oldArg.equals(newArgs.get(0)) && oldArg != null) newArgs.add(oldArg);
                        }
                    }
                }
                }
            }

            // there may be nested call expressions which require translation too
            GNode callExpressionBlock = GNode.create("CallExpressionBlock");

            // temp variable
            String tmpDef = "";

            // if there is still work to do, aka nested call expressions
            if (undone) {

                // there is another call expression, receive methods return type
                if (n.getNode(0).hasName("CallExpression")) { 
                    Type tmpType = (Type) n.getNode(0).getProperty("methodReturnType");
                    //Get the corret class name from the Type object
                    if (tmpType.isAnnotated()){
                        tmpType = tmpType.deannotate();
                    }
                    if (tmpType.isAlias()) {
                        tmpDef = tmpType.toAlias().getName().toString();
                    }
                    else {
                        tmpDef = tmpType.toString();
                    }
                }

                // there is a primary identifier (aka symbol), update tmp accordingly
                else if (n.getNode(0).hasName("PrimaryIdentifier")) {
                    String primaryKey = n.getNode(0).getString(0);
                    if (completedInits.keySet().contains(primaryKey)) tmpDef = primaryKey;
                    else {
                        Type tmpType = ((VariableT) table.current().lookup(n.getNode(0).get(0).toString())).getType();
                        int counter = 0;

                        //unpack the type information from array Type
                        while (tmpType.tag().toString().equals("ARRAY")) {
                            counter ++;
                            tmpType = tmpType.toArray().getType();
                            if (tmpType.isAnnotated()) {
                                tmpType = tmpType.deannotate();
                            }
                        }

                        //Get the corret class name from the Type object
                        if (tmpType.isAnnotated()){
                            tmpType = tmpType.deannotate();
                        }

                        if (tmpType.isAlias()) {
                            tmpDef = tmpType.toAlias().getName().toString();
                        }
                        else {
                            tmpDef = tmpType.toString();
                        }
                        
                        //Set up proper wrapper
                        for (int i = 0; i < counter; i++) {
                            tmpDef = "__rt::Array<" + tmpDef + ">";
                        }
                    }
                }

                // there is a cast expression, update tmp accordignly
                else if (n.getNode(0).hasName("CastExpression")) tmpDef = n.getNode(0).getProperty("CastType").toString();

                // there is a selection expression, update tmp accordingly
                else if (n.getNode(0).hasName("SelectionExpression")) {
                    String primaryKey = n.getNode(0).getNode(0).getString(0);
                    String secondaryKey = n.getNode(0).getString(1).replaceAll("->", "").replaceAll(" ", "");
                    String classIn = "";

                    if (primaryKey.equals("__this")) classIn = currentClass;
                    else {
                        classIn = (((VariableT)table.current().lookup(primaryKey)).getType().toString()).replaceAll("\\(|\\)", "");
                        String[] classInDetail = classIn.split("\\.");
                        classIn = classInDetail[classInDetail.length - 1];
                    }

                    ArrayList<Phase1.Initializer> classFields = completedInits.get(classIn);

                    for (int i = 0; i < classFields.size(); i++) {
                        if (classFields.get(i).name.equals(secondaryKey)) {
                            tmpDef = classFields.get(i).typeName;
                            break;
                        }
                    }
                }

                String[] tmpDefs = tmpDef.split("\\.");
                tmpDef = tmpDefs[tmpDefs.length - 1];

                // if dispatch is static, don't do anything
                if (null != n.getProperty("methodDispatchType")) {
                    if (n.getProperty("methodDispatchType").toString().equals("static")) {
                        n.set(0, GNode.create("PrimaryIdentifier", "__" + tmpDef));
                        return;
                    }
                }

                // create the tmp node, start adding null checks and process the expression correctly
                GNode def = GNode.create("ExpressionStatement", tmpDef + " tmp", " = ", n.getNode(0));
                GNode nullCheck = GNode.create("Check", "__rt::checkNotNull", GNode.create("PrimaryIdentifier", "tmp"));
                callExpressionBlock.add(def);
                callExpressionBlock.add(nullCheck);

                // process contents of the current call expression and store in a reacl call expression node
                GNode realExpression = GNode.create("ExpressionStatement");
                GNode realCallExpression = GNode.create("RealCallExpression");
                realCallExpression.add(GNode.create("PrimaryIdentifier", "tmp"));

                for (int i = 1; i < n.size(); i++) realCallExpression.add(n.get(i));
                realExpression.add(realCallExpression);
                callExpressionBlock.add(realExpression);
                n.set(0, callExpressionBlock);

                // clear out information we don't need anymore
                for (int i = 1; i < n.size(); i++) n.set(i, null);

            }
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