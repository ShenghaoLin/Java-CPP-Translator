package edu.nyu.oop;

import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.nio.file.Path;
import java.util.*;

import static org.junit.Assert.*;

public class Phase1Test {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(Phase1Test.class);
    private static Node node;// = null;
    // Specify file to be tested in format NNN
    private static String testFile = "050";

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Executing Phase1Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test"+testFile+"/Test"+testFile+".java");
    }

    // Testing if same node with different instances will result in the same AST
    @Test
    public void testParse1(){
        GNode n = (GNode) node;
        Set<Path> paths = new HashSet<Path>();
        // After making parse public, uncomment code below
        List<GNode> ast = Phase1.parse(n);
        List<GNode> astComp = Phase1.parse(node);
        assertTrue("ASTrees are not the same", ast.equals(astComp));
    }

    // Testing if nodes hold the right info
    @Test
    public void testParse2() { //throws xtc.tree.VisitingException{
        logger.debug("Executing Parse");
        List<GNode> ast = Phase1.parse(node);

        assertTrue("Compilation Unit is", ast.get(0).hasName("CompilationUnit"));
        assertTrue("PACKAGE DECLARATION IS", ast.get(0).get(0).toString().substring(0, 61).equals("PackageDeclaration(null, QualifiedIdentifier(\"inputs\", \"test0"));
        assertTrue("FIRST CLASS DECLARATION", ast.get(0).get(1).toString().substring(0, 16).equals("ClassDeclaration"));
        //Uncomment in case a second class is expected
        if(ast.get(0).size() > 2) {
            assertTrue("SECOND CLASS DECLARATION", ast.get(0).get(2).toString().substring(0, 16).equals("ClassDeclaration"));
            if(ast.get(0).size() > 3) {
                assertTrue("THIRD CLASS DECLARATION", ast.get(0).get(3).toString().substring(0, 16).equals("ClassDeclaration"));
            }
        }
    }
}
