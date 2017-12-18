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

    private static String testFile = "050";

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Executing Phase2Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test"+testFile+"/Test"+testFile+".java");
        ast = Phase1.parse(node);
    }

    @Test
    public void testInitializeRepList() { //throws xtc.tree.VisitingException{
        Phase2.ObjectRepList objList = Phase2.initializeRepList();
        assertTrue("OBJECT", objList.get(0).name.equals("Object"));
        assertTrue("STRING", objList.get(1).name.equals("String"));
        assertTrue("CLASS", objList.get(2).name.equals("Class"));
    }

    @Test
    public void testGetObjectRepresentation() { //throws xtc.tree.VisitingException{
        Phase2.Phase2Visitor visitor = new Phase2.Phase2Visitor();
        visitor.traverse(node);
        Phase2.ObjectRepList unfilled = visitor.getObjectRepresentations();
        if(unfilled.size() == 1){
            assertTrue("Test" + testFile, unfilled.get(0).name.equals("Test" + testFile));
        }else{
            assertTrue("A", unfilled.get(0).name.equals("A"));
            if (unfilled.size() < 3) {
                assertTrue("Test" + testFile, unfilled.get(1).name.equals("Test" + testFile));
            } else {
                assertTrue("B", unfilled.get(1).name.equals("B") || unfilled.get(1).name.equals("B1"));
                if (unfilled.size() < 4) {
                    assertTrue("Test" + testFile, unfilled.get(2).name.equals("Test" + testFile));
                } else {
                    assertTrue("C", unfilled.get(2).name.equals("C") || unfilled.get(2).name.equals("B2"));
                    if (unfilled.size() < 5) {
                        assertTrue("Test" + testFile, unfilled.get(3).name.equals("Test" + testFile));
                    } else {
                        assertTrue("D", unfilled.get(3).name.equals("D") || unfilled.get(3).name.equals("C"));
                        assertTrue("Test" + testFile, unfilled.get(4).name.equals("Test" + testFile));
                    }
                }
            }
        }
    }


    @Test
    public void testFillVTable() { //throws xtc.tree.VisitingException{
        Phase2.Phase2Visitor visitor = new Phase2.Phase2Visitor();
        visitor.traverse(node);
        Phase2.ObjectRepList unfilled = visitor.getObjectRepresentations();
        Phase2.ObjectRepList filled = Phase2.initializeRepList();

        filled = Phase2.fill(filled, unfilled);

        List<Field> fields = filled.get(0).vtable.fields;

        assertTrue("IS A Modifier",fields.get(0).accessModifier.equals("public"));
        assertTrue("IS A fieldName",fields.get(0).fieldName.equals("__is_a"));
        assertTrue("IS A fieldType",fields.get(0).fieldType.equals("Class"));
        assertTrue("IS A inheritedFrom",fields.get(0).inheritedFrom.equals(""));
        assertTrue("IS A Initial",fields.get(0).initial.equals(""));

        assertTrue("DELETE Modifier",fields.get(1).accessModifier.equals("public"));
        assertTrue("DELETE fieldName",fields.get(1).fieldName.equals("*__delete"));
        assertTrue("DELETE fieldType",fields.get(1).fieldType.equals("void"));
        assertTrue("DELETE inheritedFrom",fields.get(1).inheritedFrom.equals(""));
        assertTrue("DELETE Initial",fields.get(1).initial.equals("__Object*"));

        assertTrue("HASHCODE Modifier",fields.get(2).accessModifier.equals("public"));
        assertTrue("HASHCODE fieldName",fields.get(2).fieldName.equals("*hashCode"));
        assertTrue("HASHCODE fieldType",fields.get(2).fieldType.equals("int32_t"));
        assertTrue("HASHCODE inheritedFrom",fields.get(2).inheritedFrom.equals(""));
        assertTrue("HASHCODE Initial",fields.get(2).initial.equals("Object"));

        assertTrue("EQUALS Modifier",fields.get(3).accessModifier.equals("public"));
        assertTrue("EQUALS fieldName",fields.get(3).fieldName.equals("*equals"));
        assertTrue("EQUALS fieldType",fields.get(3).fieldType.equals("bool"));
        assertTrue("EQUALS inheritedFrom",fields.get(3).inheritedFrom.equals(""));
        assertTrue("EQUALS Initial",fields.get(3).initial.equals("Object, Object"));

        assertTrue("GETCLASS Modifier",fields.get(4).accessModifier.equals("public"));
        assertTrue("GETCLASS fieldName",fields.get(4).fieldName.equals("*getClass"));
        assertTrue("GETCLASS fieldType",fields.get(4).fieldType.equals("Class"));
        assertTrue("GETCLASS inheritedFrom",fields.get(4).inheritedFrom.equals(""));
        assertTrue("GETCLASS Initial",fields.get(4).initial.equals("Object"));

        assertTrue("TOSTRING Modifier",fields.get(5).accessModifier.equals("public"));
        assertTrue("TOSTRING fieldName",fields.get(5).fieldName.equals("*toString"));
        assertTrue("TOSTRING fieldType",fields.get(5).fieldType.equals("String"));
        assertTrue("TOSTRING inheritedFrom",fields.get(5).inheritedFrom.equals(""));
        assertTrue("TOSTRING Initial",fields.get(5).initial.equals("Object"));


        List<VMethod> methods = filled.get(0).vtable.methods;

        assertTrue("IS A VMethod Modifier", methods.get(0).accessModifier.equals("public"));
        assertTrue("IS A VMethod Name", methods.get(0).name.equals("__is_a"));
        assertTrue("IS A VMethod Initial",methods.get(0).initial.equals("(__Object::__class())"));

        assertTrue("DELETE VMethod Modifier", methods.get(1).accessModifier.equals("public"));
        assertTrue("DELETE VMethod Name", methods.get(1).name.equals("__delete"));
        assertTrue("DELETE VMethod Initial",methods.get(1).initial.equals("(&__rt::__delete<__Object>)"));

        assertTrue("HASHCODE VMethod Modifier", methods.get(2).accessModifier.equals("public"));
        assertTrue("HASHCODE VMethod Name", methods.get(2).name.equals("hashCode"));
        assertTrue("HASHCODE VMethod Initial",methods.get(2).initial.equals("(&__Object::__hashCode)"));

        assertTrue("EQUALS VMethod Modifier", methods.get(3).accessModifier.equals("public"));
        assertTrue("EQUALS VMethod Name", methods.get(3).name.equals("equals"));
        assertTrue("EQUALS VMethod Initial",methods.get(3).initial.equals("(&__Object::__equals)"));

        assertTrue("GETCLASS VMethod Modifier", methods.get(4).accessModifier.equals("public"));
        assertTrue("GETCLASS VMethod Name", methods.get(4).name.equals("getClass"));
        assertTrue("GETCLASS VMethod Initial",methods.get(4).initial.equals("(&__Object::__getClass)"));

        assertTrue("TOSTRING VMethod Modifier", methods.get(5).accessModifier.equals("public"));
        assertTrue("TOSTRING VMethod Name", methods.get(5).name.equals("toString"));
        assertTrue("TOSTRING VMethod Initial",methods.get(5).initial.equals("(&__Object::__toString)"));

        List<Constructor> constructorObj = filled.get(0).classRep.constructors;
        assertTrue("OBJECT Modifier", constructorObj.get(0).accessModifier.equals("public"));
        assertTrue("OBJECT Name", constructorObj.get(0).name.equals("Object"));

        List<Constructor> constructorInit = filled.get(1).classRep.constructors;
        assertTrue("OBJECT Modifier", constructorInit.get(0).accessModifier.equals("public"));
        assertTrue("OBJECT Name", constructorInit.get(0).name.equals("init"));
        assertTrue("OBJECT INIT NAME", constructorInit.get(0).parameters.get(0).type.equals("String"));
        assertTrue("OBJECT INIT NAME", constructorInit.get(0).parameters.get(0).name.equals("__this"));

    }

    @Test
    public void testBuildCppAst() {
        Phase2.Phase2Visitor visitor = new Phase2.Phase2Visitor();
        visitor.traverse(node);

        Phase2.ObjectRepList unfilled = visitor.getObjectRepresentations();
        Phase2.ObjectRepList filled = Phase2.getFilledObjectRepList(unfilled);

        GNode g = (GNode) Phase2.buildCppAst(visitor.getPackageName(), filled);

        assertTrue("Root Node", g.get(0).toString().equals("PackageDeclaration(\"namespace inputs { \\nnamespace test"+testFile+" { \\n\")"));
        assertTrue("Forward Declarations", g.get(1).toString().substring(0, 20).equals("ForwardDeclarations("));//\"__A\", \"__B\", \"__C\", \"__Test050\")"));

        //System.out.println(g.get(3).toString().substring(0, 23));

        if(g.size() < 4){
            assertTrue("CLASSDECLARATION TEST"+testFile, g.get(2).toString().substring(0,28).equals("ClassDeclaration(\"__Test"+testFile+"\""));
        } else {
            assertTrue("Class A", g.get(2).toString().substring(0, 22).equals("ClassDeclaration(\"__A\""));
            if(g.size() < 5){
                assertTrue("CLASS DECLARATION TEST"+testFile, g.get(3).toString().substring(0,28).equals("ClassDeclaration(\"__Test"+testFile+"\""));
            } else {
                assertTrue("Class B", g.get(3).toString().substring(0, 22).equals("ClassDeclaration(\"__B\"") || g.get(3).toString().substring(0, 23).equals("ClassDeclaration(\"__B1\""));
                if(g.size() < 6){
                    assertTrue("CLASS DECLARATION TEST"+testFile, g.get(4).toString().substring(0,28).equals("ClassDeclaration(\"__Test"+testFile+"\""));
                }else {
                    assertTrue("Class C", g.get(4).toString().substring(0, 22).equals("ClassDeclaration(\"__C\"") || g.get(4).toString().substring(0, 23).equals("ClassDeclaration(\"__B2\""));
                    if(g.size() < 7){
                        assertTrue("CLASSDECLARATION TEST"+testFile, g.get(5).toString().substring(0,28).equals("ClassDeclaration(\"__Test"+testFile+"\""));
                    } else {
                        assertTrue("Class D", g.get(5).toString().substring(0, 22).equals("ClassDeclaration(\"__D\"") || g.get(5).toString().substring(0, 22).equals("ClassDeclaration(\"__C\""));
                        assertTrue("CLASSDECLARATION TEST"+testFile, g.get(6).toString().substring(0,28).equals("ClassDeclaration(\"__Test"+testFile+"\""));
                    }
                }
            }
        }
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
