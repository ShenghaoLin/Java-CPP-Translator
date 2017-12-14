/**
 * Phase 1 visitor that traverses all dependencies recrusively and adds nodes of all
 * dependencies, furthermore checks for duplciate files to avoid duplicate nodes
 * using hash list of absolute paths of files containing Java code
 *
 * @author Shenghao Lin
 * @author Sai Akhil
 * @author Goktug Saatcioglu
 * @author Sam Holloway
 *
 * @verion 1.0
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

    public static HashMap<String, ArrayList<Initializer>> mangle(Runtime runtime, SymbolTable table, Node n) {

        Mangler mangler = new Mangler(runtime, table);

        mangler.dispatch(n);
        mangler.dispatch(n);

        return mangler.getInitializers();
    }

    public static class Initializer {
        String name = "";
        boolean isStatic = false;
        String initial = "";
        String value = "";
        String typeName = "";

        public Initializer(String name, boolean isStatic, String typeName, String value) {
            this.name = name;
            this.isStatic = isStatic;
            this.initial = getInitial(typeName);
            this.value = value;
            this.typeName = getTypeName(typeName);
        }

        public static String getInitial(String typeName){
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

    public static class Mangler extends Visitor {

        protected SymbolTable table;
        protected Runtime runtime;
        protected HashMap<String, String> methodScopeToMangledName;
        protected String className = "";
        protected String parentName = "";
        protected HashMap<String, ArrayList<Initializer>> initializers;


        public final List<File> classpath() {
            return JavaEntities.classpath(runtime);
        }

        public Mangler(Runtime runtime, SymbolTable table) {
            this.runtime = runtime;
            this.table = table;
            this.methodScopeToMangledName = new HashMap<String, String>();
            this.initializers = new HashMap<String, ArrayList<Initializer>>();
        }

        public HashMap<String, ArrayList<Initializer>> getInitializers () {
            return initializers;
        }

        //MISC. HELPFUL METHODS
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
                boolean isStatic;
                if(TypeUtil.isStaticType(field)) isStatic = true;
                else isStatic = false;
                String typeName = n.getNode(1).getNode(0).getString(0);
                String value;
                if(n.getNode(2).getNode(0).getNode(2) != null) value = n.getNode(2).getNode(0).getNode(2).get(0).toString();
                else value = "";
                Initializer initializer = new Initializer(fieldName, isStatic, typeName, value);
                ArrayList<Initializer> currentInitializers = initializers.get(JavaEntities.currentType(table).getName());
                for(Initializer currentInitializer : currentInitializers)
                    if((currentInitializer.name.equals(initializer.name)) && (currentInitializer.typeName.equals(initializer.typeName))) return;
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

            initializers.put(JavaEntities.currentType(table).getName(), new ArrayList<Initializer>());

            className = n.get(1).toString();
            Object extension = NodeUtil.dfs(n, "Extension");

            if (extension == null) {
                parentName = "Object";
            } else {
                parentName = ((GNode) NodeUtil.dfs(n, "QualifiedIdentifier")).get(0).toString();
            }


            visit(n);
            className = "";
            parentName = "";
            SymbolTableUtil.exitScope(table, n);
        }


        public void visitMethodDeclaration(GNode n) {
            SymbolTableUtil.enterScope(table, n);
            table.mark(n);

            //Mangle name
            String methodName = n.getString(3);

            if (n.getProperty("mangledName") == null) {
                if ((!methodName.equals("main"))&&(!methodName.equals(className))) {
                    String paramString = "";
                    GNode params = (GNode) n.get(4);
                    for (int i = 0; i < params.size(); i++) {
                        if (i == 0) paramString += params.getNode(i).getNode(1).getNode(0).getString(0);
                        else paramString += "_" + params.getNode(i).getNode(1).getNode(0).getString(0);
                    }

                    String mangledName = methodName + "_ " + paramString;

                    if (mangledName.equals("toString_ ") || mangledName.equals("hashCode_ ") || mangledName.equals("getClass_ ") || (mangledName.split("_ ").length == 2 && mangledName.split("_ ")[0].equals("equals"))) { 
                        System.out.println("this happened");
                        mangledName = mangledName.split("_ ")[0];
                    }

                    n.setProperty("mangledName", mangledName);
                    methodScopeToMangledName.put(table.current().getQualifiedName(), mangledName);
                }
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

        /*
         * Visit a QualifiedIdentifier = Identifier+.
         */
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


        // Helper method to construct the Java AST representation of a 'this' expression.
        // The added type annotation is dependent on the class surrounding the current scope
        // during the traversal of the AST.
        public GNode makeThisExpression() {
            GNode _this = GNode.create("ThisExpression", null);
            TypeUtil.setType(_this, JavaEntities.currentType(table));
            return _this;
        }


        public void visitCallExpression(GNode n) {
            visit(n);
            Node receiver = n.getNode(0);
            String methodName = n.getString(2);
            if (methodName.equals("this")) return;
            if (n.getProperty("mangledName") == null) {
                if ((receiver == null) &&
                        (!"super".equals(methodName)) &&
                        (!"this".equals(methodName))) {
                    Type typeToSearch = JavaEntities.currentType(table);

                    List<Type> actuals = JavaEntities.typeList((List) dispatch(n.getNode(3)));
                    MethodT method =
                            JavaEntities.typeDotMethod(table, classpath(), typeToSearch, true, methodName, actuals);

                    if (method == null) return;

                    // EXPLICIT THIS ACCESS (if method name isn't defined locally and method is not static, add "this.")
                    if (!TypeUtil.isStaticType(method)) {
                        n.set(0, makeThisExpression());
                    }
                } else if (receiver != null) {
                    //GET METHOD
                    MethodT method = null;
                    if(receiver.getName().equals("PrimaryIdentifier")) {
                        Type typeToSearch = null;
                        //STATIC
                        if (JavaEntities.simpleNameToType(table, classpath(), table.current().getQualifiedName(), receiver.get(0).toString()) != null)
                            typeToSearch = JavaEntities.simpleNameToType(table, classpath(), table.current().getQualifiedName(), receiver.get(0).toString());
                            //OBJECTS
                        else {
                            VariableT objectLookup = (VariableT) table.lookup(receiver.get(0).toString());
                            typeToSearch = objectLookup.getType();
                        }
                        List<Type> actuals = JavaEntities.typeList((List) dispatch(n.getNode(3)));
                        method =
                            JavaEntities.typeDotMethod(table, classpath(), typeToSearch, true, methodName, actuals);
                    }
                    else if (receiver.getName().equals("CallExpression")) {
                        Type objectType = returnTypeFromCallExpression(receiver);
                        System.out.println(objectType.toString());
                        List<Type> actuals = JavaEntities.typeList((List) dispatch(n.getNode(3)));
                        method =
                            JavaEntities.typeDotMethod(table, classpath(), objectType, true, methodName, actuals);
                        if (method.isMethod()) System.out.println(method.toString());
                    }

                    else if (receiver.getName().equals("ThisExpression")) {
                        Type currentType = JavaEntities.currentType(table);
                        List<Type> actuals = JavaEntities.typeList((List) dispatch(n.getNode(3)));
                        method =
                            JavaEntities.typeDotMethod(table, classpath(), currentType, true, methodName, actuals);
                    }

                    else if (receiver.getName().equals("SuperExpression")) {
                        Type currentType = JavaEntities.currentType(table);
                        Type superType = JavaEntities.directSuperTypes(table, classpath(), currentType).get(0);
                        List<Type> actuals = JavaEntities.typeList((List) dispatch(n.getNode(3)));
                        method =
                            JavaEntities.typeDotMethod(table, classpath(), superType, true, methodName, actuals);
                    }
                    if (method != null) {
                        System.out.println(method.toString());
                        //Set mangled name
                        n.setProperty("mangledName", methodScopeToMangledName.get(method.getScope()));
                        //Set method type
                        String methodDispatchType = "virtual";
                        if(isPrivateType(method)) methodDispatchType = "private";
                        if(TypeUtil.isStaticType(method)) methodDispatchType = "static";
                        n.setProperty("methodDispatchType", methodDispatchType);
                        //Set method return type
                        String methodReturnType = method.getResult().toString();
                        n.setProperty("methodReturnType", methodReturnType);
                    }
                }
            }
        }



        public Node visitPrimaryIdentifier(GNode n) {
            String fieldName = n.getString(0);

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

}