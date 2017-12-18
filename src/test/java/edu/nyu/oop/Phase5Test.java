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


public class Phase5Test {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(Phase1Test.class);

    protected final Runtime runtime = new Runtime();

    private static Node node;// = null;

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Executing Phase5Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test050/Test050.java");
    }

    
    @Test
    public void testProcess() { //throws xtc.tree.VisitingException{
        List<GNode> javaAsts = Phase1.parse(node);

//        List<GNode> l = Phase4.process(javaAsts);
//
//        Phase5 printer = new Phase5("output.cpp");
//
//        assertTrue("Head of File", printer.headoffile.equals("#include \"output.h\"\n#include <iostream>\n\nusing namespace java::lang;"));

    }
}