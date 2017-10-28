package edu.nyu.oop;

import java.io.*;
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
    public static void beforeClass(){
        logger.debug("Executing Phase2Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test050/Test050.java");
        ast = Phase1.parse(node);
    }

    @Test
    public void testInitializeRepList(){ //throws xtc.tree.VisitingException{
        Phase2.ObjectRepList objList = Phase2.initializeRepList();
        assertTrue("OBJECT", objList.get(0).name.equals("Object"));
        assertTrue("OBJECT", objList.get(1).name.equals("String"));
        assertTrue("OBJECT", objList.get(2).name.equals("Class"));

    }

    @Test
    public void testGetObjectRepresentation(){ //throws xtc.tree.VisitingException{
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
    public void testFill(){ //throws xtc.tree.VisitingException{
        Phase2.Phase2Visitor visitor = new Phase2.Phase2Visitor();
        visitor.traverse(node);
        Phase2.ObjectRepList unfilled = visitor.getObjectRepresentations();
        Phase2.ObjectRepList filled = Phase2.initializeRepList();

        filled = Phase2.fill(filled, unfilled);

        for(ObjectRep o: filled){
            System.out.println("\n\n\nNew ObjectRepList");
            System.out.println("Constructors: ");
            for(Constructor c: o.classRep.constructors){
                System.out.println(c.constructor_name +" "+ c.access_modifier);
            }

            System.out.println("\nFields: ");
            for(Field f: o.classRep.fields){
                System.out.println(f.field_type +" "+ f.field_name);
            }

            System.out.println("\nMethods: ");
            for(Method m: o.classRep.methods){
                System.out.println(m.return_type +" "+ m.method_name);
            }
        }

        System.out.println(unfilled.size());
        assertEquals("Size", 4, unfilled.size());


        assertTrue("A", unfilled.get(0).name.equals("A"));
        assertTrue("B", unfilled.get(1).name.equals("B"));
        assertTrue("C", unfilled.get(2).name.equals("C"));
        assertTrue("Test050", unfilled.get(3).name.equals("Test050"));

//        assertTrue("OBJECT", objList1.get(1).name.equals("String"));
//        assertTrue("OBJECT", objList1.get(2).name.equals("Class"));

    }

}