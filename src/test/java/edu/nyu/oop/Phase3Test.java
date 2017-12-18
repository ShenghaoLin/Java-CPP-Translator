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

public class Phase3Test {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(Phase1Test.class);

    protected final Runtime runtime = new Runtime();

    private static Node node;// = null;
    public static List<GNode> ast;
    public static ArrayList<Node> cppAsts;

    private static String testFile = "049";

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Executing Phase3Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test"+testFile+"/Test"+testFile+".java");
        ast = Phase1.parse(node);

        cppAsts = new ArrayList<Node>();
        for (Node javaAst : ast) {
            Node cppAst = Phase2.runPhase2(javaAst);
            cppAsts.add(cppAst);
        }
    }

    @Test
    public void testAll() { //throws xtc.tree.VisitingException{

        Phase3 phase3 = new Phase3();

        for (Node cppAst : cppAsts) {
            phase3.print((GNode) cppAst);
        }

        assertTrue("Header", phase3.header.equals("#pragma once\n\n#include <stdint.h>\n#include <string>"));
        assertTrue("End of File", phase3.endof.equals("}\n}"));
        assertTrue("Package", phase3.packageDeclaration.equals("namespace namespace inputs { \nnamespace test"+testFile+" { \n{\nnamespace javalang {"));


        //System.out.println(phase3.constructors.get(2));


        if(phase3.classes.size() == 1){
            assertTrue("__Test"+testFile, phase3.classes.get(0).toString().equals("__Test"+testFile));
            assertTrue("CONSTRUCTOR __Test"+testFile, phase3.constructors.get(0).equals("static __Test"+testFile+"();"));
            assertTrue("VIRTUAL TABLE __Test"+testFile, phase3.fieldDeclarations.get(0).equals("__Test"+testFile+"_VT* __vptr;"));
        }else {
            assertTrue("__A", phase3.classes.get(0).toString().equals("__A"));
            assertTrue("__A's constructor", phase3.constructors.get(0).equals("static __A();"));
            assertTrue("A's Virtual Table", phase3.fieldDeclarations.get(0).equals("__A_VT* __vptr;"));
            if(phase3.classes.size() < 3){
                assertTrue("__TEST1"+testFile, phase3.classes.get(1).toString().equals("__Test"+testFile));
            } else {
                assertTrue("__TEST2"+testFile, phase3.classes.get(1).toString().equals("__Test"+testFile) || phase3.classes.get(1).toString().equals("__B1") || phase3.classes.get(1).toString().equals("__B"));
                assertTrue("__B's constructor", phase3.constructors.get(2).equals("static __B();")
                        || phase3.constructors.get(2).equals("static __B1();")
                        || phase3.constructors.get(2).equals("static __init(A __this, int32_t i);"));
                if(phase3.classes.size() < 4){
                    assertTrue("__TEST2"+testFile, phase3.classes.get(2).toString().equals("__Test"+testFile));
                } else{
                    assertTrue("__C", phase3.classes.get(2).toString().equals("__C") || phase3.classes.get(2).toString().equals("__B2"));
                    if(phase3.classes.size() < 5){
                        assertTrue("__C's init constructor", phase3.constructors.get(5).equals("static __init(C __this);"));
                    }
                }
            }

        }

    }

}
