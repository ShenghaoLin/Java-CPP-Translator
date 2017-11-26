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
    private static List<GNode> ast;

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Executing Phase3Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test050/Test050.java");
        ast = Phase1.parse(node);
    }

    @Test
    public void testAll() { //throws xtc.tree.VisitingException{
        List<GNode> javaAsts = Phase1.parse(node);

        ArrayList<Node> cppAsts = new ArrayList<Node>();
        for (Node javaAst : javaAsts) {
            Node cppAst = Phase2.runPhase2(javaAst);
            cppAsts.add(cppAst);
            //runtime.console().format(cppAst).pln().flush();
        }

        Phase3 phase3 = new Phase3();
        // below is again for single ast
        // phase3.print((GNode) cppAst);
        // phase 3
        for (Node cppAst : cppAsts) {
            phase3.print((GNode) cppAst);
        }

        assertTrue("Header", phase3.header.equals("#pragma once\n\n#include <stdint.h>\n#include <string>"));
        assertTrue("End of File", phase3.endof.equals("}\n}"));
        assertTrue("Package", phase3.packageDeclaration.equals("namespace inputs{\nnamespace javalang {"));
        assertTrue("A", phase3.classes.get(0).toString().equals("A"));
        assertTrue("B", phase3.classes.get(1).toString().equals("B"));
        assertTrue("C", phase3.classes.get(2).toString().equals("C"));
        assertTrue("C", phase3.datalayout.equals("struct __C {"));
        assertTrue("A's Virtual Table", phase3.fieldDeclarations.get(0).equals("__A_VT* __vptr;"));
        assertTrue("B's Virtual Table", phase3.fieldDeclarations.get(1).equals("__B_VT* __vptr;"));
        assertTrue("C's Virtual Table", phase3.fieldDeclarations.get(2).equals("__C_VT* __vptr;"));
        assertTrue("A's constructor", phase3.constructors.get(0).equals("static __A();"));
        assertTrue("B's constructor", phase3.constructors.get(1).equals("static __B();"));
        assertTrue("C's constructor", phase3.constructors.get(2).equals("static __C();"));
        assertTrue("Forward Declarations", phase3.forwardDeclarations.equals("struct __A;\nstruct __A_VT;\nstruct __B;\nstruct __B_VT;\nstruct __C;\nstruct __C_VT;\ntypedef __A* A;\ntypedef __B* B;\ntypedef __C* C;\n"));

    }

}