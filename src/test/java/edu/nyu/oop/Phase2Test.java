package edu.nyu.oop;

import java.io.*;

import edu.nyu.oop.util.RecursiveVisitor;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import scala.collection.immutable.Stream;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.Runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import static org.junit.Assert.*;


public class Phase2Test {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(Phase1Test.class);

    protected final Runtime runtime = new Runtime();

    private static Node node;// = null;
    private static List<GNode> ast;

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Executing Phase2Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test050/Test050.java");
        ast = Phase1.parse(node);
    }

    @Test
    public void testInitializeRepList() { //throws xtc.tree.VisitingException{
        Phase2.ObjectRepList objList = Phase2.initializeRepList();
        assertTrue("OBJECT", objList.get(0).name.equals("Object"));
        assertTrue("OBJECT", objList.get(1).name.equals("String"));
        assertTrue("OBJECT", objList.get(2).name.equals("Class"));

    }

    @Test
    public void testGetObjectRepresentation() { //throws xtc.tree.VisitingException{
        Phase2.Phase2Visitor visitor = new Phase2.Phase2Visitor();
        visitor.traverse(node);

        Phase2.ObjectRepList unfilled = visitor.getObjectRepresentations();
//        for(ObjectRep o: unfilled){
//            System.out.println("Constructors: ");
//            for(Constructor c: o.classRep.constructors){
//                System.out.println(c.constructor_name +" "+ c.access_modifier);
//            }
//
//            System.out.println("\nFields: ");
//            for(Field f: o.classRep.fields){
//                System.out.println(f.field_type +" "+ f.field_name);
//            }
//
//            System.out.println("\nMethods: ");
//            for(Method m: o.classRep.methods){
//                System.out.println(m.return_type +" "+ m.method_name);
//            }
//            System.out.println("\n\n\nNew ObjectRepList");
//        }

        System.out.println(unfilled.size());
        assertEquals("Size", 4, unfilled.size());


        assertTrue("A", unfilled.get(0).name.equals("A"));
        assertTrue("B", unfilled.get(1).name.equals("B"));
        assertTrue("C", unfilled.get(2).name.equals("C"));
        assertTrue("Test050", unfilled.get(3).name.equals("Test050"));

//        assertTrue("OBJECT", objList1.get(1).name.equals("String"));
//        assertTrue("OBJECT", objList1.get(2).name.equals("Class"));

    }


    @Test
    public void testFill() { //throws xtc.tree.VisitingException{
        Phase2.Phase2Visitor visitor = new Phase2.Phase2Visitor();
        visitor.traverse(node);
        Phase2.ObjectRepList unfilled = visitor.getObjectRepresentations();
        Phase2.ObjectRepList filled = Phase2.initializeRepList();

        filled = Phase2.fill(filled, unfilled);

        for(ObjectRep o: filled) {
            System.out.println("\n\n\nNew ObjectRepList");
            System.out.println("Constructors: ");
            for(Constructor c: o.classRep.constructors) {
                System.out.println(c.name +" "+ c.accessModifier);
            }

            System.out.println("\nFields: ");
            for(Field f: o.classRep.fields) {
                System.out.println(f.fieldType +" "+ f.fieldName);
            }

            System.out.println("\nMethods: ");
            for(Method m: o.classRep.methods) {
                System.out.println(m.returnType +" "+ m.name);
            }
        }


        assertTrue("A", unfilled.get(0).name.equals("A"));
        assertTrue("B", unfilled.get(1).name.equals("B"));
        assertTrue("C", unfilled.get(2).name.equals("C"));
        assertTrue("Test050", unfilled.get(3).name.equals("Test050"));

    }

    @Test
    public void testBuidCppAst() {

        Phase2.Phase2Visitor visitor = new Phase2.Phase2Visitor();
        visitor.traverse(node);

        //Build list of class representations (java.lang, inheritance)
        Phase2.ObjectRepList unfilled = visitor.getObjectRepresentations();
        Phase2.ObjectRepList filled = Phase2.getFilledObjectRepList(unfilled);

        GNode g = (GNode) Phase2.buildCppAst(visitor.getPackageName(), filled);

        assertTrue("Root Node", g.get(0).toString().equals("PackageDeclaration(\"inputs\")"));
        assertTrue("Forward Declarations", g.get(1).toString().equals("ForwardDeclarations(\"A\", \"B\", \"C\")"));
        assertTrue("Class A", g.get(2).toString().equals("ClassDeclaration(\"A\", DataLayout(FieldDeclarations(Field(IsStatic(\"false\"), FieldType(\"__A_VT*\"), FieldName(\"__vptr\"), Initial(\"\")), Field(IsStatic(\"true\"), FieldType(\"__A_VT\"), FieldName(\"__vtable\"), Initial(\"\"))), ConstructorDeclarations(ConstructorDeclaration(ConstructorName(\"A\"), ConstructorParameters())), MethodDeclarations(MethodDeclaration(IsStatic(\"true\"), ReturnType(\"Class\"), MethodName(\"__class\"), MethodParameters()), MethodDeclaration(IsStatic(\"false\"), ReturnType(\"void\"), MethodName(\"m\"), MethodParameters(Parameter(ParameterType(\"A\"), ParameterName(\"\")))), MethodDeclaration(IsStatic(\"false\"), ReturnType(\"A\"), MethodName(\"m\"), MethodParameters(Parameter(ParameterType(\"A\"), ParameterName(\"\")), Parameter(ParameterType(\"A\"), ParameterName(\"a\"))))), VFieldDec()), VTableLayout(\"A\", VFields(VField(FieldType(\"Class\"), FieldName(\"__is_a\"), Initial(\"\")), VField(FieldType(\"int32_t\"), FieldName(\"*hashCode\"), Initial(\"A\")), VField(FieldType(\"bool\"), FieldName(\"*equals\"), Initial(\"A, Object\")), VField(FieldType(\"Class\"), FieldName(\"*getClass\"), Initial(\"A\")), VField(FieldType(\"String\"), FieldName(\"*toString\"), Initial(\"A\")), VField(FieldType(\"void\"), FieldName(\"*m\"), Initial(\"A,A\")), VField(FieldType(\"A\"), FieldName(\"*m\"), Initial(\"A,A,A\"))), VMethods(VMethod(MethodName(\"__is_a\"), Initial(\"(__A::__class())\")), VMethod(MethodName(\"hashCode\"), Initial(\"((int32_t(*)(A)) &__Object::hashCode)\")), VMethod(MethodName(\"equals\"), Initial(\"((bool(*)(A, Object)) &__Object::equals)\")), VMethod(MethodName(\"getClass\"), Initial(\"((Class(*)(A)) &__Object::getClass)\")), VMethod(MethodName(\"toString\"), Initial(\"((String(*)(A)) &__Object::toString)\")), VMethod(MethodName(\"m\"), Initial(\"(&__A::m)\")), VMethod(MethodName(\"m\"), Initial(\"(&__A::m)\")))))"));
        assertTrue("Class B", g.get(3).toString().equals("ClassDeclaration(\"B\", DataLayout(FieldDeclarations(Field(IsStatic(\"false\"), FieldType(\"__B_VT*\"), FieldName(\"__vptr\"), Initial(\"\")), Field(IsStatic(\"true\"), FieldType(\"__B_VT\"), FieldName(\"__vtable\"), Initial(\"\"))), ConstructorDeclarations(ConstructorDeclaration(ConstructorName(\"B\"), ConstructorParameters())), MethodDeclarations(MethodDeclaration(IsStatic(\"true\"), ReturnType(\"Class\"), MethodName(\"__class\"), MethodParameters()), MethodDeclaration(IsStatic(\"false\"), ReturnType(\"void\"), MethodName(\"m\"), MethodParameters(Parameter(ParameterType(\"B\"), ParameterName(\"\")))), MethodDeclaration(IsStatic(\"false\"), ReturnType(\"C\"), MethodName(\"m\"), MethodParameters(Parameter(ParameterType(\"B\"), ParameterName(\"\")), Parameter(ParameterType(\"B\"), ParameterName(\"b\")))), MethodDeclaration(IsStatic(\"false\"), ReturnType(\"A\"), MethodName(\"m\"), MethodParameters(Parameter(ParameterType(\"B\"), ParameterName(\"\")), Parameter(ParameterType(\"A\"), ParameterName(\"a\"))))), VFieldDec()), VTableLayout(\"B\", VFields(VField(FieldType(\"Class\"), FieldName(\"__is_a\"), Initial(\"\")), VField(FieldType(\"int32_t\"), FieldName(\"*hashCode\"), Initial(\"B\")), VField(FieldType(\"bool\"), FieldName(\"*equals\"), Initial(\"B, Object\")), VField(FieldType(\"Class\"), FieldName(\"*getClass\"), Initial(\"B\")), VField(FieldType(\"String\"), FieldName(\"*toString\"), Initial(\"B\")), VField(FieldType(\"void\"), FieldName(\"*m\"), Initial(\"B,B\")), VField(FieldType(\"C\"), FieldName(\"*m\"), Initial(\"B,B,B\")), VField(FieldType(\"A\"), FieldName(\"*m\"), Initial(\"B,B,A\")), VField(FieldType(\"void\"), FieldName(\"*m\"), Initial(\"B,B\")), VField(FieldType(\"C\"), FieldName(\"*m\"), Initial(\"B,B,B\")), VField(FieldType(\"A\"), FieldName(\"*m\"), Initial(\"B,B,A\"))), VMethods(VMethod(MethodName(\"__is_a\"), Initial(\"(__B::__class())\")), VMethod(MethodName(\"hashCode\"), Initial(\"((int32_t(*)(B)) &__Object::hashCode)\")), VMethod(MethodName(\"equals\"), Initial(\"((bool(*)(B, Object)) &__Object::equals)\")), VMethod(MethodName(\"getClass\"), Initial(\"((Class(*)(B)) &__Object::getClass)\")), VMethod(MethodName(\"toString\"), Initial(\"((String(*)(B)) &__Object::toString)\")), VMethod(MethodName(\"m\"), Initial(\"(&__B::m)\")), VMethod(MethodName(\"m\"), Initial(\"(&__B::m)\")), VMethod(MethodName(\"m\"), Initial(\"(&__B::m)\")), VMethod(MethodName(\"m\"), Initial(\"(&__B::m)\")), VMethod(MethodName(\"m\"), Initial(\"(&__B::m)\")), VMethod(MethodName(\"m\"), Initial(\"(&__B::m)\")))))"));
        assertTrue("Class C", g.get(4).toString().equals("ClassDeclaration(\"C\", DataLayout(FieldDeclarations(Field(IsStatic(\"false\"), FieldType(\"__C_VT*\"), FieldName(\"__vptr\"), Initial(\"\")), Field(IsStatic(\"true\"), FieldType(\"__C_VT\"), FieldName(\"__vtable\"), Initial(\"\"))), ConstructorDeclarations(ConstructorDeclaration(ConstructorName(\"C\"), ConstructorParameters())), MethodDeclarations(MethodDeclaration(IsStatic(\"true\"), ReturnType(\"Class\"), MethodName(\"__class\"), MethodParameters()), MethodDeclaration(IsStatic(\"false\"), ReturnType(\"void\"), MethodName(\"m\"), MethodParameters(Parameter(ParameterType(\"C\"), ParameterName(\"\"))))), VFieldDec()), VTableLayout(\"C\", VFields(VField(FieldType(\"Class\"), FieldName(\"__is_a\"), Initial(\"\")), VField(FieldType(\"int32_t\"), FieldName(\"*hashCode\"), Initial(\"C\")), VField(FieldType(\"bool\"), FieldName(\"*equals\"), Initial(\"C, Object\")), VField(FieldType(\"Class\"), FieldName(\"*getClass\"), Initial(\"C\")), VField(FieldType(\"String\"), FieldName(\"*toString\"), Initial(\"C\")), VField(FieldType(\"void\"), FieldName(\"*m\"), Initial(\"C,C\")), VField(FieldType(\"void\"), FieldName(\"*m\"), Initial(\"C,C\"))), VMethods(VMethod(MethodName(\"__is_a\"), Initial(\"(__C::__class())\")), VMethod(MethodName(\"hashCode\"), Initial(\"((int32_t(*)(C)) &__Object::hashCode)\")), VMethod(MethodName(\"equals\"), Initial(\"((bool(*)(C, Object)) &__Object::equals)\")), VMethod(MethodName(\"getClass\"), Initial(\"((Class(*)(C)) &__Object::getClass)\")), VMethod(MethodName(\"toString\"), Initial(\"((String(*)(C)) &__Object::toString)\")), VMethod(MethodName(\"m\"), Initial(\"(&__C::m)\")), VMethod(MethodName(\"m\"), Initial(\"(&__C::m)\")))))"));
    }

    @Test
    public void testBuildFieldNode() {
        Field field = new Field("mod", false, "String", "dus", "d");

        Node fieldNode = Phase2.buildFieldNode(field);

        assertTrue("IsStatic", fieldNode.get(0).toString().equals("IsStatic(\"false\")"));
        assertTrue("FieldType", fieldNode.get(1).toString().equals("FieldType(\"String\")"));
        assertTrue("FieldName", fieldNode.get(2).toString().equals("FieldName(\"dus\")"));
        assertTrue("Initial", fieldNode.get(3).toString().equals("Initial(\"d\")"));

    }

}