package edu.nyu.oop;

import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.tree.Visitor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashSet;

public class Phase2 {

    public static Node runPhase2(Node n) {

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

    public static class Phase2Visitor extends Visitor {

        private Logger logger = org.slf4j.LoggerFactory.getLogger(this.getClass());

        private String packageName = "";
        private ObjectRepList objectRepresentations = new ObjectRepList();

        public void visitPackageDeclaration(GNode n){
            packageName = n.getNode(1).getString(0);
            visit(n);
        }

        public void visitClassDeclaration(GNode n) {
            objectRepresentations.add(new ObjectRep(n.getString(1)));
            visit(n);
        }

        public void visitExtension(GNode n) {
            objectRepresentations.getCurrent().parent = new ObjectRep(n.getNode(0).getNode(0).getString(0));
            visit(n);
        }

        public void visitConstructorDeclaration(GNode n) {
            // modifiers
            String accessModifier = "";
            boolean isStatic = false;

            Iterator modifierIter = n.getNode(0).iterator();
            while (modifierIter.hasNext()) {
                Node modifierNode = (Node) modifierIter.next();
                if (modifierNode.getString(0).equals("static")) isStatic = true;
                else accessModifier = modifierNode.getString(0);
            }

            // name
            String constructorName = n.getString(2);

            // parameters
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();

            parameters.add(new Parameter(objectRepresentations.getCurrent().name, "__this"));

            Iterator parameterIter = n.getNode(4).iterator();
            while (parameterIter.hasNext()) {
                Node parameterNode = (Node) parameterIter.next();
                String parameterType = convertType(parameterNode.getNode(1).getNode(0).getString(0));
                String parameterName = parameterNode.getString(3);
                parameters.add(new Parameter(parameterType, parameterName));
            }

            // add
            Constructor constructor = new Constructor(accessModifier, constructorName, parameters);
            objectRepresentations.getCurrent().classRep.constructors.add(constructor);

            visit(n);
        }

        public void visitMethodDeclaration(GNode n) {
            // modifiers
            String accessModifier = "";
            boolean isStatic = false;

            Iterator modifierIter = n.getNode(0).iterator();
            while (modifierIter.hasNext()) {
                Node modifierNode = (Node) modifierIter.next();
                if (modifierNode.getString(0).equals("static")) isStatic = true;
                else accessModifier = modifierNode.getString(0);
            }

            // return type
            String returnType;
            Node returnNode = n.getNode(2);
            if (returnNode.getName().equals("VoidType")) returnType = "void";
            else returnType = convertType(returnNode.getNode(0).getString(0));

            // name
            String methodName = n.getString(3);

            // parameters
            ArrayList<Parameter> parameters = new ArrayList<Parameter>();

            Iterator parameterIter = n.getNode(4).iterator();
            while (parameterIter.hasNext()) {
                Node parameterNode = (Node) parameterIter.next();
                String parameterType = convertType(parameterNode.getNode(1).getNode(0).getString(0));
                String parameterName = parameterNode.getString(3);
                parameters.add(new Parameter(parameterType, parameterName));
            }

            // add
            Method method = new Method(accessModifier, isStatic, returnType, methodName, parameters);
            objectRepresentations.getCurrent().classRep.methods.add(method);

            visit(n);
        }

        public void visitFieldDeclaration(GNode n) {
            // modifiers
            String accessModifier = "";
            boolean isStatic = false;

            Iterator modifierIter = n.getNode(0).iterator();
            while (modifierIter.hasNext()) {
                Node modifierNode = (Node) modifierIter.next();
                if (modifierNode.getString(0).equals("static")) isStatic = true;
                else accessModifier = modifierNode.getString(0);
            }

            // type
            String fieldType = convertType(n.getNode(1).getNode(0).getString(0));

            // name
            String fieldName = n.getNode(2).getNode(0).getString(0);

            // initial
            String initial = "";
            Node initial_node = n.getNode(2).getNode(0).getNode(2);

            // add
            Field field = new Field(accessModifier, isStatic, fieldType, fieldName, initial);
            objectRepresentations.getCurrent().classRep.fields.add(field);

            visit(n);
        }

        public void visit(Node n) {
            for (Object o : n) if (o instanceof Node) dispatch((Node) o);
        }

        public void traverse(Node n) {
            super.dispatch(n);
        }

        public String convertType(String type) {
            if (type.equals("long")) return "int64_t";
            else if (type.equals("int")) return "int32_t";
            else if (type.equals("short")) return "int16_t";
            else if (type.equals("byte")) return "int8_t";
            else if (type.equals("boolean")) return "bool";
            else return type;
        }

        public String getPackageName() {
            return packageName;
        }

        public ObjectRepList getObjectRepresentations() {
            return objectRepresentations;
        }
    }

    public static class ObjectRepList extends ArrayList<ObjectRep> {

        public ObjectRep getCurrent() {
            return this.get(this.size() - 1);
        }

        public ObjectRep getFromName(String name) {
            for (ObjectRep rep : this) if (rep.name.equals(name)) return rep;
            return null;
        }

        public int getIndexFromName(String name) {
            return indexOf(getFromName(name));
        }
    }

    public static ObjectRepList getFilledObjectRepList(ObjectRepList unfilled) {

        // manually add object, string, class
        ObjectRepList filled = initializeRepList();

        // fill with reps, in inheritance order
        filled = fill(filled, unfilled);

        // process reps
        for (ObjectRep rep : filled) {
            if (!rep.name.equals("Object") && !rep.name.equals("String") && !rep.name.equals("Class")) {
                // check if main is in the rep
                boolean main = true;
                for (Method method : rep.classRep.methods) if (method.name.equals("main")) main = false;

                // if main is not there, then process output and replace old with the new
                if (!main) {
                    ObjectRep newRep = processVTable(rep, rep.parent);
                    int index = filled.getIndexFromName(rep.name);
                    filled.set(index, newRep);
                }
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

        // remove rep with main method
        int mainIndex = -1;
        for (ObjectRep rep : filled) {
            for (Method method : rep.classRep.methods) {
                if (method.name.equals("main"))
                    mainIndex = filled.indexOf(rep);
            }
        }
        if(mainIndex != -1) filled.remove(mainIndex);

        return filled;
    }

    public static ObjectRepList fill(ObjectRepList filled, ObjectRepList unfilled) {

        //Add classes from unfilled, keep doing this until filled has same size as unfilled
        while (filled.size() < unfilled.size() + 3) {

            for (ObjectRep rep : unfilled) {

                // if no parent set parent to object's rep, which will be at filled.get(0)
                if (rep.parent == null) {
                    rep.parent = filled.get(0);
                }

                // don't add until parent class is already in filled, this makes sure classes are in inheritance order, i.e. if a class iherits a class it should be further down the list
                if (filled.getFromName(rep.parent.name) != null) {
                    int parent_idx = filled.getIndexFromName(rep.parent.name);
                    rep.parent = filled.get(parent_idx);
                    filled.add(rep);
                }
            }
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

    public static ObjectRep processFields(ObjectRep current, ObjectRep parent) {
        // process fields according to the following rules:
        // private instance fields are inherited (there is a workaround way of accessing them)
        // static instance fields are not inherited (static leads to initialization issues)
        // preserve order too
        return determineFields(current, parent);
    }

    public static ObjectRep determineFields(ObjectRep current, ObjectRep parent) {
        // iterate over parent fields from class, if possible to inherit, add to child, then dump child methods, this way we preserve order
        ArrayList<Field> parentFields = parent.classRep.fields;
        ArrayList<Field> currentFields = current.classRep.fields;

        // new array list to dump fields into as they are processed
        ArrayList<Field> updatedFields = new ArrayList<Field>();
        HashSet<String> updatedFieldNames = new HashSet<String>();

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

        current.classRep.fields = updatedFields;

        return current;
    }

    public static ObjectRep processVTable(ObjectRep current, ObjectRep parent) {

        // first process the methods of the class declaration
        // note: constructor processing is not required at the moment
        // fields are simply expanded out in visitor with corresponding method definitions
        // similarly update methods using relationship between an object and its parent
        // constructors are basically simple, multiple constructors currently not allowed

        return determineVTable(current, parent);
    }

    public static ObjectRep determineVTable(ObjectRep current, ObjectRep parent) {

        ArrayList<Field> parentFields = parent.vtable.fields;
        ArrayList<Method> currentMethods = current.classRep.methods;

        VMethod __is_a = current.vtable.methods.get(0);

        ArrayList<Field> updatedFields = new ArrayList<Field>();
        ArrayList<VMethod> updatedVmethods = new ArrayList<VMethod>();

        // determine method declarations dependent on parent declarations (overwritten or not)
        for (Field parentField : parentFields) {
            // boolean to check if something was updated
            boolean notUpdated = true;
            // loop over current methods to determine what has been overwritten and what hasn't, this will ensure preservation of order too
            for (Method currentMethod : currentMethods) {
                // if method is overwritten by child, need extra processing, ignore class definition
                if (parentField.fieldName.replaceFirst("\\*","").equals(currentMethod.name) && parentField.isStatic == false && !parentField.accessModifier.equals("private")) {
                    // process parameters correctly into field declaration
                    String parameters = "";
                    parameters += current.name;
                    ArrayList<Parameter> params = currentMethod.parameters;
                    for (Parameter param : params) parameters += "," + param.type;
                    Field temp = new Field(currentMethod.accessModifier, false, currentMethod.returnType, "*"+currentMethod.name, parameters);
                    temp.inheritedFrom = current.name;
                    updatedFields.add(temp);
                    notUpdated = false;
                    // process parameters correctly into a vMethod declaration
                    updatedVmethods.add(new VMethod(currentMethod.accessModifier, false, currentMethod.name, "(("+currentMethod.returnType+"(*)("+parameters+")) &__"+current.name+"::"+currentMethod.name+")"));
                }
            }
            // if method wasn't overwritten and is not class (which is initialized in ObjectRep creation), modify its args and simply add to updated_fields list, also add inheritnce to updated vMethods list (these will refer to Object)
            if (notUpdated && parentField.isStatic == false && !parentField.accessModifier.equals("private")) {
                String inheritedFrom = "";
                if (parentField.inheritedFrom.equals("")) inheritedFrom = "Object";
                else inheritedFrom = parentField.inheritedFrom;
                Field temp = new Field(parentField.accessModifier, parentField.isStatic, parentField.fieldType, parentField.fieldName, parentField.initial.replaceFirst(parent.name, current.name));
                temp.inheritedFrom = inheritedFrom;
                updatedFields.add(temp);
                if (parentField.fieldName.equals("__is_a")) updatedVmethods.add(__is_a);
                else updatedVmethods.add(new VMethod(parentField.accessModifier, parentField.isStatic, parentField.fieldName.replaceFirst("\\*",""), "(("+parentField.fieldType+"(*)("+parentField.initial.replaceFirst(parent.name, current.name)+")) &__"+inheritedFrom+"::"+parentField.fieldName.replaceFirst("\\*","")+")"));
            }
        }

        ArrayList<Field> updatedFieldsPrime = new ArrayList<Field>();
        ArrayList<VMethod> updatedVmethodsPrime = new ArrayList<VMethod>();

        // use hashset of names for uniqueness property
        HashSet<String> updatedFieldSet = new HashSet<String>();
        for (Field updatedField : updatedFields) updatedFieldSet.add(updatedField.fieldName.replaceFirst("\\*",""));

        // dump rest of methods in current_methods into updated_fields and set precedent for order + preserve order
        for (Method currentMethod : currentMethods) {
            // if not already declared, declare it now, also ignore private methods since they do not get vtable entries, also ignore static methods since they do not get vtable entries too
            if (!updatedFieldSet.contains(currentMethod.name) && !currentMethod.accessModifier.equals("private") && currentMethod.isStatic == false) {
                // process parameters correctly into field declaration
                String parameters = "";
                parameters += current.name;
                ArrayList<Parameter> params = currentMethod.parameters;
                for (Parameter param : params) parameters +=  "," + param.type;
                Field temp = new Field(currentMethod.accessModifier, false, currentMethod.returnType, "*"+currentMethod.name, parameters);
                temp.inheritedFrom = current.name;
                updatedFieldsPrime.add(temp);
                updatedVmethodsPrime.add(new VMethod(currentMethod.accessModifier, false, currentMethod.name, "(("+currentMethod.returnType+"(*)("+parameters+")) &__"+current.name+"::"+currentMethod.name+")"));
            }
        }

        for (Field field : updatedFieldsPrime) updatedFields.add(field);
        for (VMethod vmethod : updatedVmethodsPrime) updatedVmethods.add(vmethod);

        current.vtable.fields = updatedFields;
        current.vtable.methods = updatedVmethods;

        return current;
    }

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
        Field v_hashCode = new Field("public", false, "int32_t", "*hashCode", "Object");
        objectRep.vtable.fields.add(v_hashCode);
        Field v_equals = new Field("public", false, "bool", "*equals", "Object, Object");
        objectRep.vtable.fields.add(v_equals);
        Field v_getClass = new Field("public", false, "Class", "*getClass", "Object");
        objectRep.vtable.fields.add(v_getClass);
        Field v_toString = new Field("public", false, "String", "*toString", "Object");
        objectRep.vtable.fields.add(v_toString);
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

    public static Node buildCppAst(String packageName, ObjectRepList ObjectRepList) {
        // root
        Node root = GNode.create("CompilationUnit");

        // package
        Node packageDeclaration = GNode.create("PackageDeclaration", packageName);
        root.add(packageDeclaration);

        // forward declarations
        Node forwardDeclarations = GNode.create("ForwardDeclarations");
        root.add(forwardDeclarations);

        // process each object representation
        for (ObjectRep rep : ObjectRepList) {
            // add to forward declarations
            forwardDeclarations.add(rep.name);
            // add class node
            root.add(buildClassNode(rep));
        }

        return root;
    }

    public static Node buildClassNode(ObjectRep rep) {
        // name
        Node name = GNode.create("ClassName", rep.name);

        // fields
        Node fields = GNode.create("FieldDeclarations");
        for(Field field : rep.classRep.fields) fields.add(buildFieldNode(field));

        // constructors
        Node constructors = GNode.create("ConstructorDeclarations");
        for(Constructor constructor : rep.classRep.constructors) constructors.add(buildConstructorNode(constructor));

        // methods
        Node methods = GNode.create("MethodDeclarations");
        for(Method method : rep.classRep.methods) methods.add(buildMethodNode(method));

        // vtable
        Node vtable = buildVTableNode(rep.name, rep.vtable.fields, rep.vtable.methods);

        // return class declaration
        return GNode.create("ClassDeclaration", name, fields, constructors, methods, vtable);
    }

    public static Node buildFieldNode(Field field) {
        Node isStatic = GNode.create("IsStatic", String.valueOf(field.isStatic));
        Node fieldType = GNode.create("FieldType", field.fieldType);
        Node fieldName = GNode.create("FieldName", field.fieldName);
        Node initial = GNode.create("Initial", field.initial);
        return GNode.create("Field", isStatic, fieldType, fieldName, initial);
    }

    public static Node buildConstructorNode(Constructor constructor) {
        Node constructorName = GNode.create("ConstructorName", constructor.name);
        Node constructorParameters = GNode.create("ConstructorParameters");
        for(Parameter parameter : constructor.parameters) {
            Node parameterType = GNode.create("ParameterType", parameter.type);
            Node parameterName = GNode.create("ParameterName", parameter.name);
            Node parameterNode = GNode.create("Parameter", parameterType, parameterName);
            constructorParameters.add(parameterNode);
        }
        return GNode.create("ConstructorDeclaration", constructorName, constructorParameters);
    }

    public static Node buildMethodNode(Method method) {
        Node isStatic = GNode.create("IsStatic", String.valueOf(method.isStatic));
        Node returnType = GNode.create("ReturnType", method.returnType);
        Node methodName = GNode.create("MethodName", method.name);
        Node methodParameters = GNode.create("MethodParameters");
        for(Parameter parameter : method.parameters) {
            Node parameterType = GNode.create("ParameterType", parameter.type);
            Node parameterName = GNode.create("ParameterName", parameter.name);
            Node parameterNode = GNode.create("Parameter", parameterType, parameterName);
            methodParameters.add(parameterNode);
        }
        return GNode.create("MethodDeclaration", isStatic, returnType, methodName, methodParameters);
    }

    public static Node buildVTableNode(String className, ArrayList<Field> vfields, ArrayList<VMethod> vmethods) {
        // root
        Node root = GNode.create("VTableLayout");

        // name
        root.add(className);

        // vfields
        for (Field vfield : vfields) {
            Node fieldType = GNode.create("FieldType", vfield.fieldType);
            Node fieldName = GNode.create("FieldName", vfield.fieldName);
            Node initial = GNode.create("Initial", vfield.initial);
            root.add(GNode.create("VField", fieldType, fieldName, initial));
        }

        // vmethods
        for(VMethod vmethod : vmethods) {
            Node methodName = GNode.create("MethodName", vmethod.name);
            Node initial = GNode.create("Initial", vmethod.initial);
            root.add(GNode.create("VMethod", methodName, initial));
        }

        return root;
    }
}