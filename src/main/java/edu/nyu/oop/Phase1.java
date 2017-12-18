/**
 * Phase 1 visitor that traverses all dependencies recrusively and adds nodes of all
 * dependencies, furthermore checks for duplciate files to avoid duplicate nodes
 * using hash list of absolute paths of files containing Java code.
 * A second visitor, Mangler, traverses all ASTs from the first part and makes this
 * access explicit, mangles all method names, and uses a SymbolTable to records
 * other useful properties in certain nodes, required later in the translator.
 *
 * @author Sam Holloway
 * @author Goktug Saatcioglu
 * @author Shenghao Lin
 * @author Sai Akhil
 *
 * @verion 2.0
 */

package edu.nyu.oop;

import edu.nyu.oop.util.JavaFiveImportParser;
import edu.nyu.oop.util.SymbolTableUtil;
import edu.nyu.oop.util.NodeUtil;
import edu.nyu.oop.util.TypeUtil;

import xtc.Constants;
import xtc.lang.Java;
import xtc.lang.JavaEntities;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;
import xtc.type.*;
import xtc.tree.Attribute;
import xtc.util.Runtime;
import xtc.util.SymbolTable;


import java.io.File;
import java.util.*;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Phase1 {

    /* default constructor */
    public Phase1() {}

    /** parse the Java files and their dependencies
     *
     * @param   n  Node of type Node
     * @return     List of Java ASTs
     */
    public static List<GNode> parse(Node n) {

        GNode node = (GNode) n;
        Set<Path> paths = new HashSet<Path>();
        List<GNode> ast = new ArrayList<GNode>();

        parse(node, paths, ast);

        return ast;
    }

    /** parse the Java files and their dependencies recursively
     *
     * @param   node  Node of type Node
     * @param  paths  Set of paths
     * @param    ast  List of ASTs
     */
    private static void parse(GNode node, Set<Path> paths, List<GNode> ast) {


        // use a queue of nodes to find dependencies and process them
        Queue<GNode> nodes = new ArrayDeque<GNode>();
        nodes.add(node);

        while(!nodes.isEmpty()) {

            GNode next = nodes.poll();

            //test if seen to avoid cyclical dependencies
            String loc = next.getLocation().file;

            // obtain path and convert  to absolute path to ensure uniqueness
            Path path = Paths.get(loc);
            path = path.toAbsolutePath();

            // if file hasn't been visited, process it
            if(!paths.contains(path)) {
                paths.add(path);
                ast.add(next);
                nodes.addAll(JavaFiveImportParser.parse(next));
            }
        }

    }

    /** Make this access explicit, mangle method names, and record SymbolTable info as Node properties for a given AST
     *
     * @param runtime   xtc runtime
     * @param   table   SymbolTable
     * @param       n   root of AST
     */
    public static HashMap<String, ArrayList<Initializer>> mangle(Runtime runtime, SymbolTable table, Node n) {
        Mangler mangler = new Mangler(runtime, table);

        mangler.dispatch(n);
        mangler.dispatch(n);

        return mangler.getInitializers();
    }

    /*
    Mangler and this access completer -- adapted from Prof. Wies's MemberAccessCompleter

    Its main functions are adding explicit this access and method name mangling, as well as
    using the SymbolTable to record some useful properties on nodes that are required in later phases.
     */
    public static class Mangler extends Visitor {

        protected SymbolTable table;
        protected Runtime runtime;
        protected String className = "";
        protected String parentName = "";
        protected HashMap<String, String> methodScopeToMangledName;
        protected HashMap<String, ArrayList<Initializer>> initializers;

        public Mangler(Runtime runtime, SymbolTable table) {
            this.runtime = runtime;
            this.table = table;
            this.methodScopeToMangledName = new HashMap<String, String>();
            this.initializers = new HashMap<String, ArrayList<Initializer>>();
        }
        
        public final List<File> classpath() {
            return JavaEntities.classpath(runtime);
        }

        public HashMap<String, ArrayList<Initializer>> getInitializers () {
            return initializers;
        }

        //MISC. HELPFUL METHODS
        public GNode makeThisExpression() {
            GNode _this = GNode.create("ThisExpression", null);
            TypeUtil.setType(_this, JavaEntities.currentType(table));
            return _this;
        }

        public Type returnTypeFromCallExpression(Node n) {
            VariableT callExpObjectLookup = (VariableT) table.lookup(n.getNode(0).get(0).toString());
            String callExpMethodName = (String) n.getString(2);
            Type callExpObjectType = callExpObjectLookup.getType();
            List<Type> callExpActuals = JavaEntities.typeList((List) dispatch(n.getNode(3)));
            MethodT callExpMethod =
                    JavaEntities.typeDotMethod(table, classpath(), callExpObjectType, true, callExpMethodName, callExpActuals);
            return callExpMethod.getResult();
        }

        public boolean isPrivateType(Type type) {
            if (type == null) return false;
            Attribute attr  = type.getAttribute(Constants.NAME_VISIBILITY);
            return "private".equals(attr == null ? null : attr.getValue());
        }

        //VISIT METHODS
        public void visitCompilationUnit(GNode n) {
            String packageScope = null == n.get(0) ? visitPackageDeclaration(null) : (String) dispatch(n.getNode(0));
            table.enter(packageScope);
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

        public void visitFieldDeclaration(GNode n) {
            String fieldName = n.getNode(2).getNode(0).getString(0);
            if(JavaEntities.typeDotField(table, classpath(), JavaEntities.currentType(table), true, fieldName) != null) {
                VariableT field = JavaEntities.typeDotField(table, classpath(), JavaEntities.currentType(table), true, fieldName);
                boolean isStatic = false;
                if(TypeUtil.isStaticType(field)) isStatic = true;
                String typeName = n.getNode(1).getNode(0).getString(0);
                String value = "";
                if(n.getNode(2).getNode(0).getNode(2) != null) {
                    if(!typeName.equals("String")) value = n.getNode(2).getNode(0).getNode(2).get(0).toString();
                        //Special case: string literals
                    else value = "__rt::literal(" + n.getNode(2).getNode(0).getNode(2).get(0).toString() + ")";
                }
                //Add field to initializer list if it's not already there
                ArrayList<Initializer> currentInitializers = initializers.get(JavaEntities.currentType(table).getName());
                for(Initializer currentInitializer : currentInitializers)
                    if((currentInitializer.name.equals(fieldName)) && (currentInitializer.typeName.equals(typeName))) return;
                Initializer initializer = new Initializer(fieldName, isStatic, typeName, value);
                initializers.get(JavaEntities.currentType(table).getName()).add(initializer);
            }
        }

        public String visitPackageDeclaration(GNode n) {
            String canonicalName = null == n ? "" : (String) dispatch(n.getNode(1));
            final PackageT result = JavaEntities.canonicalNameToPackage(table, canonicalName);
            return JavaEntities.packageNameToScopeName(result.getName());
        }

        public void visitClassDeclaration(GNode n) {
            SymbolTableUtil.enterScope(table, n);
            table.mark(n);


            //Create new initializer list for the class
            initializers.put(JavaEntities.currentType(table).getName(), new ArrayList<Initializer>());

            className = n.get(1).toString();
            Object extension = NodeUtil.dfs(n, "Extension");

            if (extension == null) parentName = "Object";
            else parentName = ((GNode) NodeUtil.dfs(n, "QualifiedIdentifier")).get(0).toString();

            visit(n);
            className = "";
            parentName = "";
            SymbolTableUtil.exitScope(table, n);
        }

        public void visitMethodDeclaration(GNode n) {
            SymbolTableUtil.enterScope(table, n);
            table.mark(n);


            String methodName = n.getString(3);
            //If the method isn't main, overriding one of Object's methods, or a constructor, mangle its name
            if(!methodName.equals("main") && !methodName.equals("toString") &&
                    !methodName.equals("hashCode") && !methodName.equals("getClass") &&
                    !methodName.equals("equals") && !methodName.equals(className) &&
                    n.getProperty("mangledName") == null) {
                //Method name is mangled: methodName + "_ " + all_parameters
                String mangledName = methodName + "_ ";
                GNode params = (GNode) n.get(4);
                for (int i = 0; i < params.size(); i++) mangledName += params.getNode(i).getNode(1).getNode(0).getString(0) + "_";
                //Set node's mangledName property and record it in methodScopeToMangledName for resolving CallExpressions
                n.setProperty("mangledName", mangledName);
                methodScopeToMangledName.put(table.current().getQualifiedName(), mangledName);
            }

            visit(n);
            SymbolTableUtil.exitScope(table, n);
        }

        public void visitBlockDeclaration(GNode n) {
            SymbolTableUtil.enterScope(table, n);
            table.mark(n);
            visit(n);
            SymbolTableUtil.exitScope(table, n);
        }


        public void visitBlock(GNode n) {
            SymbolTableUtil.enterScope(table, n);
            table.mark(n);
            visit(n);
            SymbolTableUtil.exitScope(table, n);
        }

        public void visitForStatement(GNode n) {
            SymbolTableUtil.enterScope(table, n);
            table.mark(n);
            visit(n);
            SymbolTableUtil.exitScope(table, n);
        }

        public String visitQualifiedIdentifier(final GNode n) {
            // using StringBuffer instead of Utilities.qualify() to avoid O(n^2)
            // behavior
            final StringBuffer b = new StringBuffer();
            for (int i = 0; i < n.size(); i++) {
                if (b.length() > 0)
                    b.append(Constants.QUALIFIER);
                b.append(n.getString(i));
            }
            return b.toString();
        }

        public void visitCallExpression(GNode n) {
            visit(n);
            Node receiver = n.getNode(0);
            String methodName = n.getString(2);
            if (methodName.equals("this")) return;
            if (n.getProperty("mangledName") == null) {
                Type typeDot = null;
                List<Type> parameters;
                MethodT method = null;
                //Explicit this access
                if ((receiver == null) &&
                        (!"super".equals(methodName)) &&
                        (!"this".equals(methodName))) {
                    typeDot = JavaEntities.currentType(table);
                    parameters = JavaEntities.typeList((List) dispatch(n.getNode(3)));
                    method = JavaEntities.typeDotMethod(table, classpath(), typeDot, true, methodName, parameters);
                    if (method == null) return;
                    if (!TypeUtil.isStaticType(method)) n.set(0, makeThisExpression());
                }
                //Gettting appropriate mangled names for called methods
                //First, determine method actually being called using JavaEntities.typeDotMethod()
                //Then, get the method's scope from the MethodT object returned
                //Finally, find the correct mangled name using methodScopeToMangledName hashmap
                else if (receiver != null) {
                    //How we determine the type we use to search for the method with typeDotMethod depends on the kind of node the call receiver is:
                    //PrimaryIdentifier (type or variable, i.e. "a.m()")
                    if(receiver.getName().equals("PrimaryIdentifier")) {
                        String identifierName = receiver.get(0).toString();
                        String currentScope = table.current().getQualifiedName();
                        //Check if identifier is a type (Static methods)
                        Type potentialStaticType = JavaEntities.simpleNameToType(table, classpath(), currentScope, identifierName);
                        if (potentialStaticType != null) typeDot = potentialStaticType;
                        //Otherwise, identifier is a variable (must find type)
                        else typeDot = ((VariableT) table.lookup(identifierName)).getType();
                    }
                    //CallExpression (chained calls, i.e. "a.m().m()")
                    else if (receiver.getName().equals("CallExpression")) {
                        if (receiver.getNode(0).getName().equals("CallExpression")) receiver = receiver.getNode(0);
                        else if (receiver.getNode(0).getName().equals("CastExpression")) receiver = receiver.getNode(0).getNode(1);
                        typeDot = returnTypeFromCallExpression(receiver);
                    }
                    //CastExpression (casted calls, i.e. "((B)a).m()")
                    else if (receiver.getName().equals("CastExpression"))
                        typeDot = JavaEntities.simpleNameToType(table, classpath(), table.current().getQualifiedName(), receiver.getNode(0).getNode(0).get(0).toString());
                        //ThisExpression (this calls, i.e. "this.m()")
                    else if (receiver.getName().equals("ThisExpression"))
                        typeDot = JavaEntities.currentType(table);
                        //SuperExpression (super calls, i.e. "super.m()")
                    else if (receiver.getName().equals("SuperExpression"))
                        typeDot = JavaEntities.directSuperTypes(table, classpath(), JavaEntities.currentType(table)).get(0);
                    if(typeDot == null) return;
                    //Once we have the type to search for the method, we get the parameters
                    // (always in the same place in the CallExpression) and put the collected info into typeDotMethod to search
                    parameters = JavaEntities.typeList((List) dispatch(n.getNode(3)));
                    ArrayList<Type> newParameters = new ArrayList<Type>();
                    for(Type parameter : parameters) {
                        newParameters.add(JavaEntities.resolveIfAlias(table, classpath(), table.current().getQualifiedName(), parameter));
                    }

                    //MANUAL METHOD LOOKUP
                    List<MethodT> classMethods = JavaEntities.methodsOwnAndInherited(table, classpath(), typeDot);
                    for(MethodT classMethod : classMethods) {
                        if (classMethod.getName().equals(methodName)) {
                            if(classMethod.getParameters().equals(parameters)){
                                method = classMethod;
                            }
                        }
                    }

                    //IF NO PERFECT MATCH FOUND, TRY TYPEDOTMETHOD
                    if(method == null) method = JavaEntities.typeDotMethod(table, classpath(), typeDot, true, methodName, parameters);

                    if(method != null) {

                        //If a method is found, we find the correct mangled name using methodScopeToMangledName and add it,
                        // as well as some other information to be used later, to the CallExpression node as properties
                        n.setProperty("mangledName", methodScopeToMangledName.get(method.getScope()));
                        String methodDispatchType = "virtual";
                        if(isPrivateType(method)) methodDispatchType = "private";
                        if(TypeUtil.isStaticType(method)) methodDispatchType = "static";
                        n.setProperty("methodDispatchType", methodDispatchType);
                        n.setProperty("methodReturnType", method.getResult());
                    }
                }
            }
        }

        public Node visitPrimaryIdentifier(GNode n) {
            String fieldName = n.getString(0);

            //staticType property
            Object check = table.lookup(fieldName);
            if(check != null) {
                String typeName = ((VariableT) check).getType().getName();
                n.setProperty("staticType", typeName);
            }

            ClassOrInterfaceT typeToSearch = JavaEntities.currentType(table);
            if (typeToSearch == null) return n;

            VariableT field = null;
            SymbolTable.Scope oldScope = table.current();
            JavaEntities.enterScopeByQualifiedName(table, typeToSearch.getScope());
            for (final VariableT f : JavaEntities.fieldsOwnAndInherited(table, classpath(), typeToSearch))
                if (f.getName().equals(fieldName)) {
                    field = f;
                    break;
                }
            table.setScope(oldScope);

            if (field == null) return n;

            //explicit this access
            Type t = (Type) table.lookup(fieldName);
            if (t == null || !t.isVariable()) {
                t = field;
            }

            if (JavaEntities.isFieldT(t) && !TypeUtil.isStaticType(t)) {
                GNode n1 = GNode.create("SelectionExpression", makeThisExpression(), fieldName);
                TypeUtil.setType(n1, TypeUtil.getType(n));
                return n1;
            }

            return n;
        }

        public List<Type> visitArguments(final GNode n) {
            List<Type> result = new ArrayList<Type>(n.size());
            for (int i = 0; i < n.size(); i++) {
                GNode argi = n.getGeneric(i);
                Type ti = (Type) argi.getProperty(Constants.TYPE);
                if (ti.isVariable()) {
                    VariableT vi = ti.toVariable();
                    ti = vi.getType();
                }
                result.add(ti);
                Object argi1 = dispatch(argi);
                if (argi1 != null && argi1 instanceof Node) {
                    n.set(i, argi1);
                }
            }
            return result;
        }

        public void visit(GNode n) {
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

    public static class Initializer {
        String name = "";
        boolean isStatic = false;
        String typeName = "";
        String value = "";
        String initial = "";

        public Initializer(String name, boolean isStatic, String typeName, String value) {
            this.name = name;
            this.isStatic = isStatic;
            this.value = value;
            this.initial = getInitial(typeName);
            this.typeName = getTypeName(typeName);
        }

        //Gets default initial value from Java type name
        public static String getInitial(String typeName) {
            String toReturn = "";
            switch (typeName) {
                case "boolean":
                    toReturn = "false";
                    break;
                case "double":
                    toReturn = "0";
                    break;
                case "float":
                    toReturn = "0";
                    break;
                case "long":
                    toReturn = "0";
                    break;
                case "int":
                    toReturn = "0";
                    break;
                case "short":
                    toReturn = "0";
                    break;
                case "byte":
                    toReturn = "0";
                    break;
                case "char":
                    toReturn = "0";
                    break;
                default:
                    toReturn = "__rt::null()";
                    break;
            }
            return toReturn;
        }

        //Gets C++ type name from Java type name
        public static String getTypeName(String typeName) {
            String toReturn = "";
            switch (typeName) {
                case "boolean":
                    toReturn = "bool";
                    break;
                case "long":
                    toReturn = "int64_t";
                    break;
                case "int":
                    toReturn = "int32_t";
                    break;
                case "short":
                    toReturn = "int16_t";
                    break;
                case "byte":
                    toReturn = "int8_t";
                    break;
                default:
                    toReturn = typeName;
                    break;
            }
            return toReturn;
        }
    }

}
