package edu.nyu.oop;

import java.io.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.Runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class Phase1Test {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(Phase1Test.class);

    protected final Runtime runtime = new Runtime();

    private static Node node;// = null;

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Executing Phase1Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test050/Test050.java");
    }

//    @Before
//    public void before(){
//        System.out.println("RIGHT HERE!"+node.toString());
//
//    }

    @Test
    public void testParse() { //throws xtc.tree.VisitingException{
        List<GNode> ast = Phase1.parse(node);
//        for(GNode nd: Phase1.parse(node.getNode(1))){
//            ast.add(nd);
//        }

        for (GNode k : ast) {
            //System.out.println("NODE ADDED:\n"+k.getName());
            runtime.console().format(k).pln().flush();
        }

        System.out.println(ast.get(0).get(3).toString().substring(0, 16));

        assertTrue("Compilation Unit is", ast.get(0).hasName("CompilationUnit"));
        assertTrue("PACKAGE DECLARATION IS", ast.get(0).get(0).toString().equals("PackageDeclaration(null, QualifiedIdentifier(\"inputs\", \"test050\"))"));
        assertTrue("CLASS DECLARATION A", ast.get(0).get(1).toString().substring(0, 16).equals("ClassDeclaration"));
        assertTrue("CLASS DECLARATION B", ast.get(0).get(2).toString().substring(0, 16).equals("ClassDeclaration"));
        assertTrue("CLASS DECLARATION C", ast.get(0).get(3).toString().substring(0, 16).equals("ClassDeclaration"));

    }
}