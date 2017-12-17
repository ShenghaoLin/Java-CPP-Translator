/**
 * Phase 2, first traverses list of asts obtained from Phase 1 while processing
 * relevant data layout information into relevant data structures, after this
 * step vtable structure is resolved using inheritance heuristic (e.g. if B ~ A
 * and A ~ Object, all inheritance data can be resolved using Object ~ A ~ B),
 * vtable populates vfields and vmethods, finally data is dumped into a new AST
 * structure to be used for Phase 3 printer that visits said AST
 * final version using mangled names and delete method have also been implemented
 *
 * @author Goktug Saatcioglu
 * @author Sam Holloway
 *
 * @version 2.0
 */

package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import edu.nyu.oop.util.NodeUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;

public class Phase2 {

    // HashMap of children to their parents
    public static HashMap<String, String> childrenToParents = new HashMap<String, String>();

    /**
     * main method, calls visitor, populates ObjectRepList, build CPP AST
     *
     * @param     n root node of given AST parsed by Phase 1
     *
     * @return root node of AST with built layout and structure for each child and itself
     */
    public static Node runPhase2(Node n) {

        // this was for printing contents of data structures before node processing, now that portion of the code has been commented out
        boolean dump = false;

        //Traverse Java AST
        Phase2Visitor visitor = new Phase2Visitor();
        visitor.traverse(n);

        //Build list of class representations (java.lang, inheritance)
        ObjectRepList unfilled = visitor.getObjectRepresentations();
        ObjectRepList filled = getFilledObjectRepList(unfilled);

        //Build C++ AST from class representations
        return buildCppAst(visitor.getPackageName(), filled);
    }

    /**
     * Visitor class
     */
    public static class Phase2Visitor extends Visitor {

        private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

        private String packageName = "";
        private ObjectRepList objectRepresentations = new ObjectRepList();
        private boolean mainFlag = false;
        private boolean constructorFlag = true;
        private boolean methodFlag = false;

        /**
         * Visits Package Declaration and assigns packagename
         *
         * @param node node being visited
         */
        public void visitPackageDeclaration(GNode node) {
            for (int i = 0; i < node.size(); i++) {
                packageName += "namespace " + node.getNode(1).getString(i) + " { \n";
            }
            //packageName = node.getNode(1).getString(0);
            visit(node);
        }

        /**
         * For each class declaration in file add it to objectRepresentations
         *
         * @param node node being visited
         */
        public void visitClassDeclaration(GNode node) {
            objectRepresentations.add(new ObjectRep(node.getString(1)));
            mainFlag = false;
            constructorFlag = true;
            visit(node);
        }

        /**
         * Adds inheritance of each node to structure
         *
         * @param node node being visited
         */
        public void visitExtension(GNode node) {
            objectRepresentations.getCurrent().parent = new ObjectRep(node.getNode(0).getNode(0).getString(0));
            visit(node);
        }

        /**
         * Gets class constructor and adds it to node's data layout of current ObjectRepresentation
         *
         * @param node node being visited
         */
        public void visitConstructorDeclaration(GNode node) {
            // modifiers
            String accessModifier = "";
            boolean isStatic = false;

            Iterator modifierIter = node.getNode(0).iterator();
            while (modifierIter.hasNext()) {
                Node modifierNode = (Node) modifierIter.next();
                if (modifierNode.getString(0).equals("static")) isStatic = true;
                else accessModifier = modifierNode.getString(0);
            }

            // name
            String constructorName = node.getString(2);

            // parameters
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();

            parameters.add(new Parameter(objectRepresentations.getCurrent().name, "__this"));

            Iterator parameterIter = node.getNode(3).iterator();
            while (parameterIter.hasNext()) {
                Node parameterNode = (Node) parameterIter.next();
                String parameterType = convertType(parameterNode.getNode(1).getNode(0).getString(0));
                String parameterName = parameterNode.getString(3);
                parameters.add(new Parameter(parameterType, parameterName));
            }

            // add
            // constructorName should be called init, updating here
            Constructor constructor = new Constructor(accessModifier, "init", parameters);
            boolean toAdd = true;
            for (Constructor other : objectRepresentations.getCurrent().classRep.constructors) {
                if (other.equals(constructor)) toAdd = false;
            }
            if (toAdd) objectRepresentations.getCurrent().classRep.constructors.add(constructor);

            visit(node);
        }

        /**
         * Visits method declaration and adds it to current node's data layout of current ObjectRepresentation
         *
         * @param node node being visited
         */
        public void visitMethodDeclaration(GNode node) {

            // set method flag
            methodFlag = true;

            // modifiers
            String accessModifier = "";
            boolean isStatic = false;

            Iterator modifierIter = node.getNode(0).iterator();
            while (modifierIter.hasNext()) {
                Node modifierNode = (Node) modifierIter.next();
                if (modifierNode.getString(0).equals("static")) isStatic = true;
                else accessModifier = modifierNode.getString(0);
            }

            // return type
            String returnType;
            Node returnNode = node.getNode(2);
            if(returnNode == null) returnType = "Placeholder";
            else if (returnNode.getName().equals("VoidType")) returnType = "void";
            else returnType = convertType(returnNode.getNode(0).getString(0));

            // name
            String methodName = node.getString(3);
            if (node.getProperty("mangledName") != null) methodName = (String) node.getProperty("mangledName"); //node.getString(3);
            if (methodName.equals("main")) mainFlag = true;
            methodName = methodName.replaceAll("\\s", "");

            if (mainFlag) returnType = "int32_t";

            // parameters
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();
            if (returnType.equals("Placeholder")) parameters.add(new Parameter(objectRepresentations.getCurrent().name, "__this"));
            else if (!isStatic) parameters.add(new Parameter(objectRepresentations.getCurrent().name, "__this"));

            Iterator parameterIter = node.getNode(4).iterator();
            while (parameterIter.hasNext()) {
                Node parameterNode = (Node) parameterIter.next();
                String parameterType = convertType(parameterNode.getNode(1).getNode(0).getString(0));
                if (parameterType.equals("String") && mainFlag) parameterType = "__rt::Array<String>";
                String parameterName = parameterNode.getString(3);
                parameters.add(new Parameter(parameterType, parameterName));
            }

            // add
            Method method = new Method(accessModifier, isStatic, returnType, methodName, parameters);
            if (!returnType.equals("Placeholder")) objectRepresentations.getCurrent().classRep.methods.add(method);
            else {
                if (constructorFlag) {
                    objectRepresentations.getCurrent().classRep.constructors.remove(1);
                    constructorFlag = false;
                }
                Constructor constructor = new Constructor(accessModifier, "init", parameters);
                boolean toAdd = true;
                for (Constructor other : objectRepresentations.getCurrent().classRep.constructors) {
                    if (other.equals(constructor)) toAdd = false;
                }
                if (toAdd) objectRepresentations.getCurrent().classRep.constructors.add(constructor);


            }

            visit(node);

            // set method flag off now
            methodFlag = false;
        }

        /**
         * Visits field declaration and adds it to current node's data layout of ObjectRepresentation
         *
         * @param node node being visited
         */
        public void visitFieldDeclaration(GNode node) {
            // modifiers
            String accessModifier = "";
            boolean isStatic = false;

            Iterator modifierIter = node.getNode(0).iterator();
            while (modifierIter.hasNext()) {
                Node modifierNode = (Node) modifierIter.next();
                if (modifierNode.getString(0).equals("static")) isStatic = true;
                else accessModifier = modifierNode.getString(0);
            }

            // type
            String fieldType = convertType(node.getNode(1).getNode(0).getString(0));

            // name
            String fieldName = node.getNode(2).getNode(0).getString(0);

            // initial
            String initial = "";
            Node initial_node = node.getNode(2).getNode(0).getNode(2);

            // add if not declared in body of main or body of method (which can be a constructor or another method)
            Field field = new Field(accessModifier, isStatic, fieldType, fieldName, initial);
            if (!mainFlag && !methodFlag) objectRepresentations.getCurrent().classRep.fields.add(field);

            visit(node);
        }

        /**
         * Iteratively visit all children of given node
         *
         * @param node root of current node Ast
         */
        public void visit(Node node) {
            for (Object o : node) if (o instanceof Node) dispatch((Node) o);
        }

        /**
         * Dispatch method, stat the visitor
         *
         * @param node node being visited
         */
        public void traverse(Node node) {
            super.dispatch(node);
        }

        /**
         * Converts from Java data types to C++ data types
         *
         * @param  type java type
         *
         * @return  cpp type
         */
        public String convertType(String type) {
            if (type.equals("long")) return "int64_t";
            else if (type.equals("int")) return "int32_t";
            else if (type.equals("short")) return "int16_t";
            else if (type.equals("byte")) return "int8_t";
            else if (type.equals("boolean")) return "bool";
            else return type;
        }

        /**
         * Get package name, this is associated with the visitor object as a field
         *
         * @return packageName name of the package that is being processed
         */
        public String getPackageName() {
            return packageName;
        }

        /**
         * Gets ObjectRepList objectRepresentations and returns it
         *
         * @return objectRepresentations list of ObjectReps with each ObjectRep representing data layout of an object
         */
        public ObjectRepList getObjectRepresentations() {
            return objectRepresentations;
        }
    }

    /**
     * ArrayList of Structure ObjectRep
     */
    public static class ObjectRepList extends ArrayList<ObjectRep> {

        /**
         * Method to get ObjectRep in last position of Array
         *
         * @return ObjectRep ObjectRep at ObjectRepList[-1]
         */
        public ObjectRep getCurrent() {
            return this.get(this.size() - 1);
        }

        /**
         * Iterativery searches for an ObjectRep from a given name
         *
         * @param       name name to be searched
         *
         * @return ObjectRep return if in list
         * @return      null null if an ObjectRep from a given name isn't found
         */
        public ObjectRep getFromName(String name) {
            for (ObjectRep rep : this) if (rep.name.equals(name)) return rep;
            return null;
        }

        /**
         * Gets index of ObjectRep by its name by searching for it
         *
         * @param   name name to be searched
         *
         * @return index index of ObjectRep if in List, -1 if not
         */
        public int getIndexFromName(String name) {
            int index = 0;
            for (ObjectRep rep : this)
                if (rep.name.equals(name))
                    return index;
                else index++;
            return -1;
        }
    }

    /**
     * initializes a ObjectRepList with hardcoded Object, String and Class data
     * fills ObjectRepList using unfilled list with fill method that also sets
     * parents of Objects
     * resolves VTable structure for each object using inheritance relationship
     * proces inherited fields for data layout for each object using inheritance
     * relationship
     * removes object with main method as this does not need to be processed
     * removes Object, String and Class at the last step as these are not needed
     * anymore
     *
     * @param unfilled unprocessed ObjectRepList from visitor
     *
     * @return  filled ObjectRepList of resolved ctable and data layouts for each object
     */
    public static ObjectRepList getFilledObjectRepList(ObjectRepList unfilled) {

        // manually add object, string, class
        ObjectRepList filled = initializeRepList();

        // fill with reps, in inheritance order
        filled = fill(filled, unfilled);

        // process reps
        for (ObjectRep rep : filled) {

            /* main goes here so ignore this, see below for new implementation
            if (!rep.name.equals("Object") && !rep.name.equals("String") && !rep.name.equals("Class")) {
                // check if main is in the rep
                boolean main = false;
                for (Method method : rep.classRep.methods) if (method.name.equals("main")) main = true;
                // if main is not there, then process output and replace old with the new
                if (!main) {
                    ObjectRep newRep = processVTable(rep, rep.parent);
                    int index = filled.getIndexFromName(newRep.name);
                    filled.set(index, newRep);
                }
            }
            */

            // this is the new implementation
            if (!rep.name.equals("Object") && !rep.name.equals("String") && !rep.name.equals("Class")) {
                ObjectRep newRep = processVTable(rep, rep.parent);
                int index = filled.getIndexFromName(newRep.name);
                filled.set(index, newRep);
            }

            // don't forget to update the parents after replacing so that logic works, "bubbling down"
            for (ObjectRep repSub : filled) {
                if (repSub.parent != null) {
                    int parentIndex = filled.getIndexFromName(repSub.parent.name);
                    repSub.parent = filled.get(parentIndex);
                }
            }
        }

        // after processing v-table process inherited fields (this is the last step, everything else should be consistent)
        int counter = 0;
        for (ObjectRep rep : filled) {
            if (counter < 2) counter++;
            else {
                // as long as the parent isn't object there are fields that may be inherited (this depends on if static and so on)
                if (!rep.parent.name.equals("Object")) {
                    ObjectRep newRep = processFields(rep, rep.parent);
                    int index = filled.getIndexFromName(rep.name);
                    filled.set(index, newRep);
                }
            }
            // again, don't forget to update the parents after replacing so that logic works, "bubbling down"
            for (ObjectRep repSub : filled) {
                if (repSub.parent != null) {
                    int parentIndex = filled.getIndexFromName(repSub.parent.name);
                    repSub.parent = filled.get(parentIndex);
                }
            }
        }

        /* don't remove main anymore, it's legal
        // remove rep with main method
        int mainIndex = -1;
        for (ObjectRep rep : filled) {
            for (Method method : rep.classRep.methods) {
                if (method.name.equals("main"))
                    mainIndex = filled.indexOf(rep);
            }
        }
        if(mainIndex != -1) filled.remove(mainIndex);
        */

        // remove Object, String, and Class
        filled.remove(0);
        filled.remove(0);
        filled.remove(0);

        // after processing everything, re-arrange methods so they match their v-table representations
        for (ObjectRep rep : filled) {
            // fix methods forall non Object, String, Class objects
            ObjectRep newRep = processMethods(rep);
            int index = filled.getIndexFromName(rep.name);
            filled.set(index, newRep);
            // again, don't forget to update the parents after replacing so that logic works, "bubbling down"
            for (ObjectRep repSub : filled) {
                if (repSub.parent != null) {
                    int parentIndex = filled.getIndexFromName(repSub.parent.name);
                    if (parentIndex != -1) repSub.parent = filled.get(parentIndex);
                }
            }
        }

        return filled;
    }

    /**
     * Helper method for getFilledObjectRepList that fills up the ObjectRep structure
     * based on its inheritance heirarchy, ensuring that if B inherits A, then B occurs
     * further down on the ObjectRepList, it also adds pointers to relevant parents
     * i.e. B now points to A and A points to Object
     *
     * @param   filled ObjectRepList with just Object, String and Class
     * @param unfilled ObjectRepList of visitor processed Objects
     *
     * @return  filled ObjectRepList with proper inheritance hierarchy
     */
    public static ObjectRepList fill(ObjectRepList filled, ObjectRepList unfilled) {

        //Add classes from unfilled, keep doing this until filled has same size as unfilled
        while (filled.size() < unfilled.size() + 3) {

            for (ObjectRep rep : unfilled) {

                // if no parent set parent to object's rep, which will be at filled.get(0)
                if (rep.parent == null) {
                    rep.parent = filled.get(0);
                }

                // don't add until parent class is already in filled, this makes sure classes are in inheritance order, i.e. if a class inherits a class it should be further down the list
                if (filled.getFromName(rep.parent.name) != null) {
                    int parent_idx = filled.getIndexFromName(rep.parent.name);
                    rep.parent = filled.get(parent_idx);
                    filled.add(rep);
                }
            }
        }

        // processparents here too
        for (ObjectRep rep : filled) {
            if (rep.parent != null && rep.parent.equals(filled.get(0))) childrenToParents.put(rep.name, "");
            else if (rep.parent != null) childrenToParents.put(rep.name, rep.parent.name);
        }

        return filled;
    }

    /*
    public static void printBeforeNodes(ObjectRepList filled) {
        int i = 0;
        for (ObjectRep test : filled) {
            if (i > 2) {  // ignore Object, String, Class
                System.out.println(test.name);
                System.out.println("printing class layout . . .");
                System.out.println("=======================");
                ArrayList<Field> fields = test.classRep.fields;
                System.out.println("printing class-fields . . . ");
                for (Field field : fields) {
                    System.out.println("********************");
                    System.out.println(field.access_modifier);
                    System.out.println(field.is_static);
                    System.out.println(field.field_type);
                    System.out.println(field.field_name);
                    System.out.println(field.initial);
                    System.out.println("********************");
                }
                ArrayList<Constructor> constructors = test.classRep.constructors;
                System.out.println("printing class-contructor declarations . . . ");
                for (Constructor constructor : constructors) {
                    System.out.println("********************");
                    System.out.println(constructor.access_modifier);
                    System.out.println(constructor.constructor_name);
                    ArrayList<Parameter> params = constructor.parameters;
                    System.out.println("printing associated parameters . . . ");
                    for (Parameter param : params) {
                        System.out.println("--------------------");
                        System.out.println(param.type);
                        System.out.println(param.name);
                        System.out.println("--------------------");
                    }
                    System.out.println("********************");
                }
                ArrayList<Method> methods = test.classRep.methods;
                System.out.println("printing class-method declarations . . . ");
                for (Method method : methods) {
                    System.out.println("********************");
                    System.out.println(method.access_modifier);
                    System.out.println(method.is_static);
                    System.out.println(method.return_type);
                    System.out.println(method.method_name);
                    ArrayList<Parameter> params = method.parameters;
                    System.out.println("printing associated parameters . . . ");
                    for (Parameter param : params) {
                        System.out.println("--------------------");
                        System.out.println(param.type);
                        System.out.println(param.name);
                        System.out.println("--------------------");
                    }
                    System.out.println("********************");
                }
                System.out.println("=======================");
                System.out.println("printing v table layout . . .");
                System.out.println("=======================");
                ArrayList<Field> vfields = test.vtable.fields;
                System.out.println("printing v-fields . . . ");
                for (Field vfield : vfields) {
                    System.out.println("********************");
                    System.out.println(vfield.access_modifier);
                    System.out.println(vfield.is_static);
                    System.out.println(vfield.field_type);
                    System.out.println(vfield.field_name);
                    System.out.println(vfield.initial);
                    System.out.println("********************");
                }
                ArrayList<VMethod> vmethods = test.vtable.methods;
                System.out.println("printing v-method declarations . . . ");
                for (VMethod vmethod : vmethods) {
                    System.out.println("********************");
                    System.out.println(vmethod.access_modifier);
                    System.out.println(vmethod.is_static);
                    System.out.println(vmethod.method_name);
                    System.out.println(vmethod.initial);
                    System.out.println("********************");
                }
                System.out.println("=======================");
            }
            i++;
        }
    }
    */

    /**
     * Helper method for getFilledObjectRepList that calls determineMethods
     * method that processes vtable method declarations to determine order
     * of declaration for data layout class declarations
     *
     * @param       current curent object that needs to inherit fields
     *
     * @return currentPrime version of current with method declaration solved
     */
    public static ObjectRep processMethods(ObjectRep current) {
        // process methods according to declarations in vtable
        // this ensures all declarations are in order, thus
        // preserving correctness of output.h
        ObjectRep currentPrime = determineMethods(current);

        return currentPrime;
    }

    /**
     * Determines the method declarations for current object by using its
     * vtable vmethod declarations
     *
     * @param  current ObjectRep with fields that need to be determined
     *
     * @return current ObjectRep with updated fields
     */
    public static ObjectRep determineMethods(ObjectRep current) {
        // iterate over methods and vmethods to get correct ordering
        ArrayList<Method> methods = current.classRep.methods;
        ArrayList<VMethod> vMethods = current.vtable.methods;

        // new array list to dump fields into as they are processed
        ArrayList<Method> updatedMethods = new ArrayList<Method>();
        HashSet<String> updatedMethodNames = new HashSet<String>();
        // remove index 0 and add to updatedMethods at the very end
        Method last = methods.get(0);
        methods.remove(0);

        // iterate over vMethods, if match add to updated methods
        for (VMethod vMethod : vMethods) {
            for (Method method : methods) {
                if (vMethod.name.equals(method.name)) {
                    updatedMethods.add(method);
                    updatedMethodNames.add(method.name);
                }
            }
        }

        // add static methods last, they don't have vtable declarations so order of declarations isn't important
        for (Method method : methods) {
            if (!updatedMethodNames.contains(method.name)) {
                updatedMethods.add(method);
                updatedMethodNames.add(method.name);
            }
        }

        updatedMethods.add(last);

        // update methods
        current.classRep.methods = updatedMethods;

        return current;
    }

    /**
     * Helper method for getFilledObjectRepList that calls determineFields
     * method that process inheritance hierarchy of fields that need to
     * be declared in data layout for an object
     *
     * @param       current curent object that needs to inherit fields
     * @param        parent parent bbject that has fields to give
     *
     * @return currentPrime version of current with field inheritance resolved
     */
    public static ObjectRep processFields(ObjectRep current, ObjectRep parent) {
        // process fields according to the following rules:
        // private instance fields are inherited (there is a workaround way of accessing them)
        // static instance fields are not inherited (static leads to initialization issues)
        // preserve order too
        ObjectRep currentPrime = determineFields(current, parent);

        return currentPrime;
    }

    /**
     * Determines the field variables for current object by using the parent
     * object as the point of reference to inherit from
     *
     * @param  current ObjectRep with fields that need to be determined
     * @param   parent ObjectRep with fields that will give to current
     *
     * @return current ObjectRep with updated fields
     */
    public static ObjectRep determineFields(ObjectRep current, ObjectRep parent) {
        // iterate over parent fields from class, if possible to inherit, add to child, then dump child methods, this way we preserve order
        ArrayList<Field> parentFields = parent.classRep.fields;
        ArrayList<Field> currentFields = current.classRep.fields;

        // new array list to dump fields into as they are processed
        ArrayList<Field> updatedFields = new ArrayList<Field>();
        HashSet<String> updatedFieldNames = new HashSet<String>();
        updatedFields.add(currentFields.get(0));
        currentFields.remove(0);
        Field last = currentFields.get(0);
        currentFields.remove(0);

        // process parent fields and inherit valid fields
        for (Field parentField : parentFields) {
            // if field is static it can't be inherited, furthermore don't inherit the vptr and the vtable
            if (parentField.isStatic == false && !parentField.fieldName.equals("__vptr") && !parentField.fieldName.equals("__vtable")) {
                updatedFields.add(parentField);
                updatedFieldNames.add(parentField.fieldName);
            }
        }

        // process current fields and dump fields to updated fields, if same name is used we assume no shadowed fields therefore no reason to re-declare
        for (Field currentField : currentFields) {
            if (!updatedFieldNames.contains(currentField.fieldName)) {
                updatedFields.add(currentField);
                updatedFieldNames.add(currentField.fieldName);
            }
        }

        updatedFields.add(last);

        current.classRep.fields = updatedFields;

        return current;
    }

    /**
     * Helper method for getFilledObjectRepList that calls determineVTable
     * method that process inheritance hierarchy and preserves order for
     * the declaration of methods, furthermore it implements method
     * overriding and creates correct pointer declarations
     *
     * @param       current current object that needs a vtable
     * @param        parent parent object that has vtable info for current
     *
     * @return currentPrime version of current with vtable resolved
     */
    public static ObjectRep processVTable(ObjectRep current, ObjectRep parent) {

        // first process the methods of the class declaration
        // fields are simply expanded out in visitor with corresponding method definitions
        // similarly update methods using relationship between an object and its parent
        // constructors are basically simple, multiple constructors are now allowed
        ObjectRep currentPrime = determineVTable(current, parent);

        return currentPrime;
    }

    /**
     * Determines VTable for given ObjectRep
     *
     * @param  current ObjectRep with VTable that needs to be determined
     * @param   parent ObjectRep that will be used to determine current VTable
     *
     * @return current ObjectRep with updated VTable
     */
    public static ObjectRep determineVTable(ObjectRep current, ObjectRep parent) {
        ArrayList<Field> parentFields = parent.vtable.fields;
        ArrayList<Method> currentMethods = current.classRep.methods;

        VMethod __is_a = current.vtable.methods.get(0);

        ArrayList<Field> updatedFields = new ArrayList<Field>();
        ArrayList<VMethod> updatedVMethods = new ArrayList<VMethod>();

        // determine method declarations dependent on parent declarations (overwritten or not)
        for (Field parentField : parentFields) {
            // boolean to check if something was updated
            boolean notUpdated = true;
            // loop over current methods to determine what has been overwritten and what hasn't, this will ensure preservation of order too
            for (Method currentMethod : currentMethods) {
                // if method is overwritten by child, need extra processing, ignore class definition
                if (checkTwoNames(parentField.fieldName, currentMethod.name) && parentField.isStatic == false && !parentField.accessModifier.equals("private")) {
                    // process parameters correctly into field declaration
                    String parameters = "";
                    ArrayList<Parameter> params = currentMethod.parameters;
                    int idx = 0;
                    for (Parameter param : params) {
                        if (idx == 0) {
                            parameters += param.type;
                            idx++;
                        } else parameters += "," + param.type;
                    }
                    Field temp = new Field(currentMethod.accessModifier, false, currentMethod.returnType, "*"+currentMethod.name, parameters);
                    temp.inheritedFrom = current.name;
                    updatedFields.add(temp);
                    notUpdated = false;
                    // process parameters correctly into a vMethod declaration
                    updatedVMethods.add(new VMethod(currentMethod.accessModifier, false, currentMethod.name, "(&__"+current.name+"::"+currentMethod.name+")"));
                }
            }
            // if method wasn't overwritten and is not class (which is initialized in ObjectRep creation), modify its args and simply add to updated_fields list, also add inheritnce to updated vMethods list (these will refer to Object)
            if (notUpdated && parentField.isStatic == false && !parentField.accessModifier.equals("private")) {
                if (parentField.fieldName.equals("*__delete")) {
                    updatedFields.add(new Field("public", false, "void", "*__delete", "__" + current.name + "*"));
                    updatedVMethods.add(new VMethod("public", false, "__delete", "(&__rt::__delete<__" + current.name + ">)"));
                } else {
                    String inheritedFrom = "";
                    if (parentField.inheritedFrom.equals("")) inheritedFrom = "Object";
                    else inheritedFrom = parentField.inheritedFrom;
                    Field temp = new Field(parentField.accessModifier, parentField.isStatic, parentField.fieldType, parentField.fieldName, parentField.initial.replaceFirst(parent.name, current.name));
                    temp.inheritedFrom = inheritedFrom;
                    updatedFields.add(temp);
                    if (parentField.fieldName.equals("__is_a")) updatedVMethods.add(__is_a);
                    else updatedVMethods.add(new VMethod(parentField.accessModifier, parentField.isStatic, parentField.fieldName.replaceFirst("\\*",""), "(("+parentField.fieldType+"(*)("+parentField.initial.replaceFirst(parent.name, current.name)+")) &__"+inheritedFrom+"::"+parentField.fieldName.replaceFirst("\\*","")+")"));
                }
            }
        }

        ArrayList<Field> updatedFieldsPrime = new ArrayList<Field>();
        ArrayList<VMethod> updatedVMethodsPrime = new ArrayList<VMethod>();

        // use hashset of names for uniqueness property
        HashSet<String> updatedFieldSet = new HashSet<String>();
        for (Field updatedField : updatedFields) updatedFieldSet.add(updatedField.fieldName.replaceFirst("\\*",""));

        // dump rest of methods in current_methods into updated_fields and set precedent for order + preserve order
        for (Method currentMethod : currentMethods) {
            // if not already declared, declare it now, also ignore private methods since they do not get vtable entries, also ignore static methods since they do not get vtable entries too
            if (!updatedFieldSet.contains(currentMethod.name) && !currentMethod.accessModifier.equals("private") && currentMethod.isStatic == false) {
                // process parameters correctly into field declaration
                String parameters = "";
                ArrayList<Parameter> params = currentMethod.parameters;
                int idx = 0;
                for (Parameter param : params) {
                    if (idx == 0) {
                        parameters += param.type;
                        idx++;
                    } else parameters +=  "," + param.type;
                }
                Field temp = new Field(currentMethod.accessModifier, false, currentMethod.returnType, "*"+currentMethod.name, parameters);
                temp.inheritedFrom = current.name;
                updatedFieldsPrime.add(temp);
                // ("+currentMethod.returnType+"(*)("+parameters+")) this was removed from init, keeping it here just in case
                updatedVMethodsPrime.add(new VMethod(currentMethod.accessModifier, false, currentMethod.name, "(&__"+current.name+"::"+currentMethod.name+")"));
            }
        }

        for (Field field : updatedFieldsPrime) updatedFields.add(field);
        for (VMethod vmethod : updatedVMethodsPrime) updatedVMethods.add(vmethod);

        current.vtable.fields = updatedFields;
        current.vtable.methods = updatedVMethods;

        return current;
    }

    /**
     * Helper method for determineVTable, checks if two string are equals
     * Preprocessing step of removes pointer (*) operator from name of name1
     *
     * @param name1 first name to be compared
     * @param name2 second name to be compared
     *
     * @return true if name1 == name2, false o/w
     *
     */
    public static boolean checkTwoNames(String name1, String name2) {
        if (name1.replaceFirst("\\*","").equals(name2)) return true;
        return false;
    }

    /**
     * Initializes ObjectReps for Object, String and Class layout and structures
     * manually with hard coding, Object is necessary and String and Class are
     * hardcoded just in case they may be needed in the future
     *
     * @return filled ObjectRepList with Object, String and Class hard coded
     */
    public static ObjectRepList initializeRepList() {

        ObjectRepList filled = new ObjectRepList();

        // Adding java.lang structure manually for Object
        ObjectRep objectRep = new ObjectRep("Object");
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        params.add(new Parameter("Object", "o"));
        Method hashCode = new Method("public", true, "int32_t", "hashCode", params);
        objectRep.classRep.methods.add(hashCode);
        params.add(new Parameter("Object", "o"));
        Method equals = new Method("public", true, "bool", "equals", params);
        objectRep.classRep.methods.add(equals);
        params.remove(1);
        Method getClass = new Method("public", true, "Class", "getClass", params);
        objectRep.classRep.methods.add(getClass);
        Method toString = new Method("public", true, "String", "toString", params);
        objectRep.classRep.methods.add(toString);
        // class representation for Object filled, now do V-Table filling
        Field v_delete = new Field("public", false, "void", "*__delete", "__Object*");
        objectRep.vtable.fields.add(v_delete);
        Field v_hashCode = new Field("public", false, "int32_t", "*hashCode", "Object");
        objectRep.vtable.fields.add(v_hashCode);
        Field v_equals = new Field("public", false, "bool", "*equals", "Object, Object");
        objectRep.vtable.fields.add(v_equals);
        Field v_getClass = new Field("public", false, "Class", "*getClass", "Object");
        objectRep.vtable.fields.add(v_getClass);
        Field v_toString = new Field("public", false, "String", "*toString", "Object");
        objectRep.vtable.fields.add(v_toString);
        VMethod v_method_delete = new VMethod("public", false, "__delete", "(&__rt::__delete<__Object>)");
        objectRep.vtable.methods.add(v_method_delete);
        VMethod v_method_hashCode = new VMethod("public", false, "hashCode", "(&__Object::__hashCode)");
        objectRep.vtable.methods.add(v_method_hashCode);
        VMethod v_method_equals = new VMethod("public", false, "equals", "(&__Object::__equals)");
        objectRep.vtable.methods.add(v_method_equals);
        VMethod v_method_getClass = new VMethod("public", false, "getClass", "(&__Object::__getClass)");
        objectRep.vtable.methods.add(v_method_getClass);
        VMethod v_method_toString = new VMethod("public", false, "toString", "(&__Object::__toString)");
        objectRep.vtable.methods.add(v_method_toString);
        // set parent to null
        objectRep.parent = null;
        // object is created!
        filled.add(objectRep);

        // Adding java.lang structure manually for String
        ObjectRep stringRep = new ObjectRep("String");
        Field data = new Field("public", false, "std::string", "data", "");
        stringRep.classRep.fields.add(data);
        params = new ArrayList<Parameter>();
        params.add(new Parameter("std::string", "data"));
        Constructor stringConstructor = new Constructor("public", "__String", params);
        stringRep.classRep.constructors.remove(0);
        stringRep.classRep.constructors.add(stringConstructor);
        params = new ArrayList<Parameter>();
        params.add(new Parameter("String", "str"));
        hashCode = new Method("public", true, "int32_t", "hashCode", params);
        stringRep.classRep.methods.add(hashCode);
        params.add(new Parameter("Object", "o"));
        equals = new Method("public", true, "bool", "equals", params);
        stringRep.classRep.methods.add(equals);
        params.remove(1);
        toString = new Method("public", true, "int32_t", "length", params);
        stringRep.classRep.methods.add(toString);
        params.add(new Parameter("int32_t", "int_length"));
        Method charAt = new Method("public", true, "char", "charAt", params);
        stringRep.classRep.methods.add(charAt);
        // class representation for String filled, now do V-Table filling
        v_delete = new Field("public", false, "void", "*__delete", "__String*");
        stringRep.vtable.fields.add(v_delete);
        v_hashCode = new Field("public", false, "int32_t", "*hashCode", "String");
        stringRep.vtable.fields.add(v_hashCode);
        v_equals = new Field("public", false, "bool", "*equals", "String, Object");
        stringRep.vtable.fields.add(v_equals);
        v_getClass = new Field("public", false, "Class", "*getClass", "String");
        stringRep.vtable.fields.add(v_getClass);
        v_toString = new Field("public", false, "String", "*toString", "String");
        stringRep.vtable.fields.add(v_toString);
        Field v_length = new Field("public", false, "int32_t", "*length", "String");
        stringRep.vtable.fields.add(v_length);
        Field v_charAt = new Field("public", false, "char", "*charAt", "String");
        stringRep.vtable.fields.add(v_charAt);
        v_method_delete = new VMethod("public", false, "__delete", "(&__rt::__delete<__String>)");
        stringRep.vtable.methods.add(v_method_delete);
        v_method_hashCode = new VMethod("public", false, "hashCode", "(&__String::__hashCode())");
        stringRep.vtable.methods.add(v_method_hashCode);
        v_method_equals = new VMethod("public", false, "equals", "(&__String::equals)");
        stringRep.vtable.methods.add(v_method_equals);
        v_method_getClass = new VMethod("public", false, "getClass", "((Class(*)(String)) &__Object::getClass");
        stringRep.vtable.methods.add(v_method_getClass);
        v_method_toString = new VMethod("public", false, "toString", "(&__toString::toString)");
        stringRep.vtable.methods.add(v_method_toString);
        VMethod v_method_charAt = new VMethod("public", false, "charAt", "(&__String::charAt)");
        stringRep.vtable.methods.add(v_method_charAt);
        // set parent to Object
        stringRep.parent = objectRep;
        // String is created!
        filled.add(stringRep);

        // Adding java.lang structure manually for Class
        ObjectRep classRep = new ObjectRep("Class");
        Field name = new Field("public", false, "String", "name", "");
        classRep.classRep.fields.add(name);
        Field parent = new Field("public", false, "Class", "parent", "");
        classRep.classRep.fields.add(parent);
        params = new ArrayList<Parameter>();
        params.add(new Parameter("String", "name"));
        params.add(new Parameter("Class", "parent"));
        Constructor classConstructor = new Constructor("public", "__Class", params);
        classRep.classRep.constructors.remove(0);
        classRep.classRep.constructors.add(classConstructor);
        params = new ArrayList<Parameter>();
        params.add(new Parameter("Class", "c"));
        toString = new Method("public", true, "String", "toString", params);
        classRep.classRep.methods.add(toString);
        Method getName = new Method("public", true, "String", "getName", params);
        classRep.classRep.methods.add(getName);
        Method getSuperClass = new Method("public", true, "Class", "getSuperclass", params);
        classRep.classRep.methods.add(getSuperClass);
        params.add(new Parameter("Object", "o"));
        Method isInstance = new Method("public", true, "bool", "isInstance", params);
        classRep.classRep.methods.add(isInstance);
        // class representation for Class filled, now do V-Table filling
        v_delete = new Field("public", false, "void", "*__delete", "__Class*");
        classRep.vtable.fields.add(v_delete);
        v_hashCode = new Field("public", false, "int32_t", "*hashCode", "Class");
        classRep.vtable.fields.add(v_hashCode);
        v_equals = new Field("public", false, "bool", "*equals", "Class, Object");
        classRep.vtable.fields.add(v_equals);
        v_getClass = new Field("public", false, "Class", "*getClass", "Class");
        classRep.vtable.fields.add(v_getClass);
        v_toString = new Field("public", false, "String", "*toString", "Class");
        classRep.vtable.fields.add(v_toString);
        Field v_getName = new Field("public", false, "String", "*getName", "Class");
        classRep.vtable.fields.add(v_getName);
        Field v_getSuperClass = new Field("public", false, "String", "*getSuperclass", "Class");
        classRep.vtable.fields.add(v_getSuperClass);
        Field v_isInstance = new Field("public", false, "String", "*isInstance", "Class");
        classRep.vtable.fields.add(v_isInstance);
        v_method_delete = new VMethod("public", false, "__delete", "(&__rt::__delete<__Class>)");
        classRep.vtable.methods.add(v_method_delete);
        v_method_hashCode = new VMethod("public", false, "hashCode", "((int32_t(*)(Class)) &__Object::hashCode)");
        classRep.vtable.methods.add(v_method_hashCode);
        v_method_equals = new VMethod("public", false, "equals", "((bool(*)(Class,Object)) &__Object::equals)");
        classRep.vtable.methods.add(v_method_equals);
        v_method_getClass = new VMethod("public", false, "getClass", "((Class(*)(Class)) &__Object::getClass)");
        classRep.vtable.methods.add(v_method_getClass);
        v_method_toString = new VMethod("public", false, "toString", "(&__Class::toString)");
        classRep.vtable.methods.add(v_method_toString);
        VMethod v_method_getName = new VMethod("public", false, "getName", "(&__Class::getName)");
        classRep.vtable.methods.add(v_method_getName);
        VMethod v_method_getSuperClass = new VMethod("public", false, "getSuperclass", "(&__Class::getSuperclass)");
        classRep.vtable.methods.add(v_method_getSuperClass);
        VMethod v_method_isInstance = new VMethod("public", false, "isInstance", "(&__Class::isInstance)");
        classRep.vtable.methods.add(v_method_isInstance);
        // set parent to object
        classRep.parent = objectRep;
        // class is created!
        filled.add(classRep);

        return filled;
    }

    /**
     * Method to create root node and parse information in ObjectReps to it
     * giving the final AST that the printer in Phase 3 uses to print
     *
     * @param   packageName package name of package being processed
     * @param objectRepList list of ObjectReps that need to be turned into AST
     *
     * @return       cppAst final AST structure for Phase 3
     */
    public static Node buildCppAst(String packageName, ObjectRepList objectRepList) {
        // root
        Node root = GNode.create("CompilationUnit");

        // package
        Node packageDeclaration = GNode.create("PackageDeclaration", packageName);
        root.add(packageDeclaration);

        // forward declarations
        Node forwardDeclarations = GNode.create("ForwardDeclarations");
        root.add(forwardDeclarations);

        // process each object representation
        for (ObjectRep rep : objectRepList) {
            // add to forward declarations
            forwardDeclarations.add("__" + rep.name);
            // add class node
            root.add(buildClassNode(rep));
        }

        return root;
    }

    /**
     * Helper method for buildCppAst that builds a node representing a class
     * it does this by processing the data layout of the current rep by
     * processing fields, constructors and methods, then it proceeds to process
     * the vtable
     *
     * @param   rep ObjectRep that needs to be become a class node
     *
     * @return Node node with class information name, data layout and vtable
     */
    public static Node buildClassNode(ObjectRep rep) {
        // name, commented out not in sai's code but here just in case
        // Node name = GNode.create("ClassName", rep.name);

        // fields
        Node fields = GNode.create("FieldDeclarations");
        for (Field field : rep.classRep.fields) fields.add(buildFieldNode(field));

        // constructors
        Node constructors = GNode.create("ConstructorDeclarations");
        for (Constructor constructor : rep.classRep.constructors) constructors.add(buildConstructorNode(constructor));

        // methods
        Node methods = GNode.create("MethodDeclarations");
        for (Method method : rep.classRep.methods) methods.add(buildMethodNode(method));

        // vtable and declaration
        Node vFieldDeclaration = GNode.create("VFieldDeclaration");
        Node vTable = buildVTableNode(rep.name, rep.vtable.fields, rep.vtable.methods);

        // data layout node
        Node dataLayout = GNode.create("DataLayout",fields, constructors, methods, vFieldDeclaration);

        // return class declaration
        return GNode.create("ClassDeclaration", "__" + rep.name, dataLayout, vTable);
    }

    /**
     * Helper method for buildClassNode, process fields of an ObjectRep and
     * parse the information into a field node
     *
     * @param field Field object that holds field information
     *
     * @return Node returns a node with field information
     */
    public static Node buildFieldNode(Field field) {
        Node isStatic = GNode.create("IsStatic", String.valueOf(field.isStatic));
        Node fieldType = GNode.create("FieldType", field.fieldType);
        Node fieldName = GNode.create("FieldName", field.fieldName);
        Node initial = GNode.create("Initial", field.initial);
        return GNode.create("Field", isStatic, fieldType, fieldName, initial);
    }

    /**
     * Helper methods for buidClassNode, process constructos of an ObjectRep
     * and parse the information into a constructor node
     *
     * @param constructor Constructor object that holds constructor information
     *
     * @return       Node returns a node with constructor information
     */
    public static Node buildConstructorNode(Constructor constructor) {
        Node constructorName = GNode.create("ConstructorName", constructor.name);
        Node constructorParameters = GNode.create("ConstructorParameters");
        for (Parameter parameter : constructor.parameters) {
            Node parameterType = GNode.create("ParameterType", parameter.type);
            Node parameterName = GNode.create("ParameterName", parameter.name);
            Node parameterNode = GNode.create("Parameter", parameterType, parameterName);
            constructorParameters.add(parameterNode);
        }

        return GNode.create("ConstructorDeclaration", constructorName, constructorParameters);
    }

    /**
     * Helper method for buildClassNode, process methods of an ObjectRep
     * and parse the information into a method node
     *
     * @param  method Method object that holds method information
     *
     * @return   Node returns a node with method information
     */
    public static Node buildMethodNode(Method method) {
        Node isStatic = GNode.create("IsStatic", String.valueOf(method.isStatic));
        Node returnType = GNode.create("ReturnType", method.returnType);
        Node methodName = GNode.create("MethodName", method.name);
        Node methodParameters = GNode.create("MethodParameters");
        for (Parameter parameter : method.parameters) {
            Node parameterType = GNode.create("ParameterType", parameter.type);
            Node parameterName = GNode.create("ParameterName", parameter.name);
            Node parameterNode = GNode.create("Parameter", parameterType, parameterName);
            methodParameters.add(parameterNode);
        }

        return GNode.create("MethodDeclaration", isStatic, returnType, methodName, methodParameters);
    }

    /**
     * Helper method for buildClassAst, process vtble information of an
     * ObjectRep and parse the information into a root node that holds
     * vfields and vmethods as child nodes of root
     *
     * @param     name name of current ObjectRep being parsed
     * @param  vfields ArrayList of vfields that need to be processed
     * @param vmethods ArrayList of vmethods that need to be processed
     *
     * @return    root Node that holds vtable information
     */
    public static Node buildVTableNode(String name, ArrayList<Field> vFields, ArrayList<VMethod> vMethods) {
        // root
        Node root = GNode.create("VTableLayout");
        // add name to root
        root.add("__" + name);
        // fields
        Node fields = GNode.create("VFields");
        // process vfields
        for (Field vField : vFields) {
            Node fieldType = GNode.create("FieldType", vField.fieldType);
            Node fieldName = GNode.create("FieldName", vField.fieldName);
            Node initial = GNode.create("Initial", vField.initial);
            fields.add(GNode.create("VField", fieldType, fieldName, initial));
        }
        root.add(fields);
        // methods
        Node methods = GNode.create("VMethods");
        // process vmethods
        for (VMethod vMethod : vMethods) {
            Node methodName = GNode.create("MethodName", vMethod.name);
            Node initial = GNode.create("Initial", vMethod.initial);
            methods.add(GNode.create("VMethod", methodName, initial));
        }
        root.add(methods);

        return root;
    }
}